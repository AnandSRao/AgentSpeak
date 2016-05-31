/**
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason AgentSpeak(L)                                  #
 * # Copyright (c) 2015-16, Philipp Kraus (philipp@lightjason.org)                      #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package org.lightjason.agentspeak.common;

import com.google.common.reflect.ClassPath;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.action.binding.CMethodAction;
import org.lightjason.agentspeak.action.binding.IAgentActionAllow;
import org.lightjason.agentspeak.action.binding.IAgentActionBlacklist;
import org.lightjason.agentspeak.action.binding.IAgentActionDeny;
import org.lightjason.agentspeak.action.binding.IAgentActionWhitelist;
import org.lightjason.agentspeak.agent.IAgent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;


/**
 * class for any helper calls
 */
public final class CCommon
{
    /**
     * package name
     **/
    public static final String PACKAGEROOT = "org.lightjason.agentspeak";
    /**
     * logger
     */
    protected static final Logger LOGGER = CCommon.getLogger( CCommon.class );
    /**
     * language resource bundle
     **/
    private static final ResourceBundle LANGUAGE = ResourceBundle.getBundle(
                                                        MessageFormat.format( "{0}.{1}", PACKAGEROOT, "language" ),
                                                        Locale.getDefault(),
                                                        new CUTF8Control()
    );
    /**
     * properties of the package
     */
    private static final ResourceBundle PROPERTIES = ResourceBundle.getBundle(
                                                        MessageFormat.format( "{0}.{1}", PACKAGEROOT, "configuration" ),
                                                        Locale.getDefault(),
                                                        new CUTF8Control()
    );


    /**
     * private ctor - avoid instantiation
     */
    private CCommon()
    {
    }

    /**
     * returns a logger instance
     *
     * @param p_class class type
     * @return logger
     */
    public static Logger getLogger( final Class<?> p_class )
    {
        return Logger.getLogger( p_class.getName() );
    }

    /**
     * returns the language bundle
     *
     * @return bundle
     */
    public static ResourceBundle getLanguageBundle()
    {
        return LANGUAGE;
    }

    /**
     * returns the property data of the package
     *
     * @return bundle object
     */
    public static ResourceBundle getConfiguration()
    {
        return PROPERTIES;
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * get all classes within an Java package as action
     *
     * @param p_package full-qualified package name or empty for default package
     * @return action stream
     */
    @SuppressWarnings( "unchecked" )
    public static Stream<IAction> getActionsFromPackage( final String... p_package )
    {
        return ( ( p_package == null ) || ( p_package.length == 0 )
                 ? Stream.of( MessageFormat.format( "{0}.{1}", PACKAGEROOT, "action.buildin" ) )
                 : Arrays.stream( p_package ) )
            .flatMap( j -> {
                try
                {
                    return ClassPath.from( Thread.currentThread().getContextClassLoader() )
                                    .getTopLevelClassesRecursive( j )
                                    .parallelStream()
                                    .map( ClassPath.ClassInfo::load )
                                    .filter( i -> !Modifier.isAbstract( i.getModifiers() ) )
                                    .filter( i -> !Modifier.isInterface( i.getModifiers() ) )
                                    .filter( i -> Modifier.isPublic( i.getModifiers() ) )
                                    .filter( IAction.class::isAssignableFrom )
                                    .map( i -> {
                                        try
                                        {
                                            return (IAction) i.newInstance();
                                        }
                                        catch ( final IllegalAccessException | InstantiationException l_exception )
                                        {
                                            LOGGER.warning( CCommon.getLanguageString( CCommon.class, "actioninstantiate", i, l_exception ) );
                                            return null;
                                        }
                                    } )
                                    .filter( i -> i != null );
                }
                catch ( final IOException l_exception )
                {
                    throw new UncheckedIOException( l_exception );
                }
            } );
    }


    /**
     * returns actions by a class
     * @note class must be an inheritance of the IAgent interface
     *
     * @param p_class class list
     * @return action stream
     */
    @SuppressWarnings( "unchecked" )
    public static Stream<IAction> getActionsFromAgentClass( final Class<?>... p_class )
    {
        return p_class == null || p_class.length == 0
               ? Stream.of()
               : Arrays.stream( p_class )
                       .parallel()
                       .filter( IAgent.class::isAssignableFrom )
                       .flatMap( i -> CCommon.methods( i, i ) )
                       .map( i -> {
                           try
                           {
                               return (IAction) new CMethodAction( i );
                           }
                           catch ( final IllegalAccessException l_exception )
                           {
                               LOGGER.warning( CCommon.getLanguageString( CCommon.class, "actioninstantiate", i, l_exception ) );
                               return null;
                           }
                       } )
                       .filter( i -> i != null );
    }

    /**
     * reads all methods by the action-annotations
     * for building agent-actions
     *
     * @param p_class class
     * @param p_root root class
     * @return stream of all methods with inheritance
     */
    private static Stream<Method> methods( final Class<?> p_class, final Class<?> p_root )
    {
        final boolean l_iswhitelisted = CCommon.isActionWhitelist( p_class, p_root );
        if ( l_iswhitelisted == CCommon.isActionBlacklist( p_class, p_root ) )
            return p_class.getSuperclass() == null
                   ? Stream.of()
                   : methods( p_class.getSuperclass(), p_root );

        final Predicate<Method> l_filter = l_iswhitelisted
                                           ? i -> !CCommon.isActionDeny( i, p_root )
                                           : i -> CCommon.isActionAllow( i, p_root );

        return Stream.concat(
            Arrays.stream( p_class.getDeclaredMethods() )
                  .parallel()
                  .map( i -> {
                      i.setAccessible( true );
                      return i;
                  } )
                  .filter( i -> !Modifier.isAbstract( i.getModifiers() ) )
                  .filter( i -> !Modifier.isInterface( i.getModifiers() ) )
                  .filter( i -> !Modifier.isNative( i.getModifiers() ) )
                  .filter( i -> !Modifier.isStatic( i.getModifiers() ) )
                  .filter( l_filter ),
            methods( p_class.getSuperclass(), p_root )
        );
    }

    /**
     * whitelist filter for a class
     *
     * @param p_class class for checking
     * @param p_root root class
     * @return boolean flag of check result
     */
    private static boolean isActionWhitelist( final Class<?> p_class, final Class<?> p_root )
    {
        return p_class.isAnnotationPresent( IAgentActionWhitelist.class )
               && (
                   ( p_class.getAnnotation( IAgentActionWhitelist.class ).classes().length == 0 )
                   || ( Arrays.stream( p_class.getAnnotation( IAgentActionWhitelist.class ).classes() )
                              .parallel()
                              .filter( p_class::equals )
                              .findFirst()
                              .isPresent()
                   )
               );
    }

    /**
     * blacklist filter of a class
     *
     * @param p_class class for checking
     * @param p_root root class
     * @return boolean flag of check result
     */
    private static boolean isActionBlacklist( final Class<?> p_class, final Class<?> p_root )
    {
        return p_class.isAnnotationPresent( IAgentActionBlacklist.class )
               && (
                   ( p_class.getAnnotation( IAgentActionBlacklist.class ).classes().length == 0 )
                   || ( Arrays.stream( p_class.getAnnotation( IAgentActionBlacklist.class ).classes() )
                              .parallel()
                              .filter( p_class::equals )
                              .findFirst()
                              .isPresent()
                   )
               );
    }

    /**
     * allow filter of a method
     *
     * @param p_method method for checking
     * @param p_root root class
     * @return boolean flag of check result
     */
    private static boolean isActionAllow( final Method p_method, final Class<?> p_root )
    {
        return p_method.isAnnotationPresent( IAgentActionAllow.class )
               && (
                   ( p_method.getAnnotation( IAgentActionAllow.class ).classes().length == 0 )
                   || ( Arrays.stream( p_method.getAnnotation( IAgentActionAllow.class ).classes() )
                              .parallel()
                              .filter( p_root::equals )
                              .findFirst()
                              .isPresent()
                   )
               );
    }

    /**
     * deny filter of a method
     *
     * @param p_method method for checking
     * @param p_root root class
     * @return boolean flag of check result
     */
    private static boolean isActionDeny( final Method p_method, final Class<?> p_root )
    {
        return p_method.isAnnotationPresent( IAgentActionDeny.class )
               && (
                   ( p_method.getAnnotation( IAgentActionDeny.class ).classes().length == 0 )
                   || ( Arrays.stream( p_method.getAnnotation( IAgentActionDeny.class ).classes() )
                              .parallel()
                              .filter( p_root::equals )
                              .findFirst()
                              .isPresent()
                   )
               );
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * concats an URL with a path
     *
     * @param p_base base URL
     * @param p_string additional path
     * @return new URL
     *
     * @throws URISyntaxException thrown on syntax error
     * @throws MalformedURLException thrown on malformat
     */
    public static URL concatURL( final URL p_base, final String p_string ) throws MalformedURLException, URISyntaxException
    {
        return new URL( p_base.toString() + p_string ).toURI().normalize().toURL();
    }

    /**
     * returns root path of the resource
     *
     * @return URL of file or null
     */
    public static URL getResourceURL()
    {
        return CCommon.class.getClassLoader().getResource( "" );
    }

    /**
     * returns a file from a resource e.g. Jar file
     *
     * @param p_file file
     * @return URL of file or null
     *
     * @throws URISyntaxException thrown on syntax error
     * @throws MalformedURLException thrown on malformat
     */
    public static URL getResourceURL( final String p_file ) throws URISyntaxException, MalformedURLException
    {
        return getResourceURL( new File( p_file ) );
    }

    /**
     * returns a file from a resource e.g. Jar file
     *
     * @param p_file file relative to the CMain
     * @return URL of file or null
     *
     * @throws URISyntaxException is thrown on URI errors
     * @throws MalformedURLException is thrown on malformat
     */
    private static URL getResourceURL( final File p_file ) throws URISyntaxException, MalformedURLException
    {
        if ( p_file.exists() )
            return p_file.toURI().normalize().toURL();
        return CCommon.class.getClassLoader().getResource( p_file.toString().replace( File.separator, "/" ) ).toURI().normalize().toURL();
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * returns the language depend string on any object
     *
     * @param p_source any object
     * @param p_label label name
     * @param p_parameter parameter
     * @return translated string
     *
     * @tparam T object type
     */
    public static <T> String getLanguageString( final T p_source, final String p_label, final Object... p_parameter )
    {
        return getLanguageString( p_source.getClass(), p_label, p_parameter );
    }

    /**
     * returns a string of the resource file
     *
     * @param p_class class for static calls
     * @param p_label label name of the object
     * @param p_parameter object array with substitutions
     * @return resource string
     */
    public static String getLanguageString( final Class<?> p_class, final String p_label, final Object... p_parameter )
    {
        try
        {
            return MessageFormat.format( LANGUAGE.getString( getLanguageLabel( p_class, p_label ) ), p_parameter );
        }
        catch ( final MissingResourceException l_exception )
        {
            return "";
        }
    }

    /**
     * returns the label of a class and string to get access to the resource
     *
     * @param p_class class for static calls
     * @param p_label label name of the object
     * @return label name
     */
    private static String getLanguageLabel( final Class<?> p_class, final String p_label )
    {
        return ( p_class.getCanonicalName().toLowerCase() + "." + p_label.toLowerCase() ).replaceAll( "[^a-zA-Z0-9_\\.]+", "" ).replace(
            PACKAGEROOT + ".", "" );
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * class to read UTF-8 encoded property file
     *
     * @note Java default encoding for property files is ISO-Latin-1
     */
    private static final class CUTF8Control extends ResourceBundle.Control
    {

        public final ResourceBundle newBundle( final String p_basename, final Locale p_locale, final String p_format, final ClassLoader p_loader,
                                               final boolean p_reload
        ) throws IllegalAccessException, InstantiationException, IOException
        {
            InputStream l_stream = null;
            final String l_resource = this.toResourceName( this.toBundleName( p_basename, p_locale ), "properties" );

            if ( !p_reload )
                l_stream = p_loader.getResourceAsStream( l_resource );
            else
            {

                final URL l_url = p_loader.getResource( l_resource );
                if ( l_url == null )
                    return null;

                final URLConnection l_connection = l_url.openConnection();
                if ( l_connection == null )
                    return null;

                l_connection.setUseCaches( false );
                l_stream = l_connection.getInputStream();
            }

            try
            {
                return new PropertyResourceBundle( new InputStreamReader( l_stream, "UTF-8" ) );

            }
            catch ( final Exception l_exception )
            {
            }
            finally
            {
                l_stream.close();
            }

            return null;
        }
    }

}
