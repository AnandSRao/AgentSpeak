/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the Light-Jason                                               #
 * # Copyright (c) 2015, Philipp Kraus (philipp.kraus@tu-clausthal.de)                  #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package lightjason.error;


/**
 * exception for parsing error
 *
 * @note must be an illegal state exception, because the AntLR error
 * lister cannot throw any other exception
 */
@SuppressWarnings( "serial" )
public final class CSyntaxErrorException extends IllegalStateException
{

    /**
     * ctor
     */
    public CSyntaxErrorException()
    {
        super();
    }

    /**
     * ctor
     *
     * @param p_message message
     */
    public CSyntaxErrorException( final String p_message )
    {
        super( p_message );
    }

    /**
     * ctor
     *
     * @param p_message message
     * @param p_cause cause
     */
    public CSyntaxErrorException( final String p_message, final Throwable p_cause )
    {
        super( p_message, p_cause );
    }

    /**
     * ctor
     *
     * @param p_cause cause
     */
    public CSyntaxErrorException( final Throwable p_cause )
    {
        super( p_cause );
    }
}