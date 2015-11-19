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

package lightjason.agent;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * test agent structure
 */
public class Test_CAgent
{
    /**
     * map with actions
     */
    private static final Map<String, IAction> c_actions = new HashMap<>();

    /*
    @Test
    public void test_ComplexAgent() throws IOException
    {
        assertTrue( testAgent( "src/test/resources/agentsuccess.asl", "complex successful agent" ) );
    }

    @Test
    public void test_SimpleAgent() throws IOException
    {
        assertTrue( testAgent( "src/test/resources/agentsimple.asl", "simple agent" ) );
    }
    */

    private static boolean testAgent( final String p_script, final String p_name )
    {
        IAgent l_agent = null;
        try (
                final InputStream l_stream = new FileInputStream( p_script );
        )
        {
            l_agent = new CAgent( l_stream, c_actions );
        }
        catch ( final Exception l_exception )
        {
            System.err.println( MessageFormat.format( "{0} passed with failure: {1}", p_name, l_exception ) );
            return false;
        }

        System.out.println( MessageFormat.format( "{0} passed successfully in: {1}", p_name, l_agent ) );
        return true;
    }

}