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

package lightjason.language.plan.action;

import lightjason.beliefbase.IBeliefBaseMask;
import lightjason.language.ILiteral;
import lightjason.language.IVariable;
import lightjason.language.plan.IPlan;

import java.util.Map;


/**
 * encapsulate class for any non-executable data type e.g. boolean
 */
public final class CBlankAction<T> extends IAction<T>
{
    /**
     * ctor
     *
     * @param p_data any object data
     */
    public CBlankAction( final T p_data )
    {
        super( p_data );
    }

    @Override
    public int hashCode()
    {
        return m_data != null ? m_data.hashCode() : super.hashCode();
    }

    @Override
    public String toString()
    {
        return m_data != null ? m_data.toString() : super.toString();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public boolean execute( final IBeliefBaseMask p_beliefbase, final Map<ILiteral, IPlan> p_runningplan )
    {
        if ( m_data instanceof IVariable )
            return ( (IVariable) m_data ).isAllocated();

        if ( m_data instanceof Boolean )
            return (Boolean) m_data;

        return true;
    }
}