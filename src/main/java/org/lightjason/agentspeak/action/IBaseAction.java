/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason AgentSpeak(L++)                                #
 * # Copyright (c) 2015-16, LightJason (info@lightjason.org)                            #
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

package org.lightjason.agentspeak.action;

import com.google.common.collect.ImmutableMultiset;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.language.variable.IVariable;

import java.util.stream.Stream;


/**
 * default implementation of an action
 */
public abstract class IBaseAction implements IAction
{

    @Override
    public final int hashCode()
    {
        return this.name().hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof IAction ) && ( this.hashCode() == p_object.hashCode() );
    }

    @Override
    public final String toString()
    {
        return this.name().toString();
    }

    @Override
    public final double score( final IAgent<?> p_agent )
    {
        return p_agent.aggregation().evaluate( p_agent, ImmutableMultiset.of( this ) );
    }

    @Override
    public Stream<IVariable<?>> variables()
    {
        return Stream.<IVariable<?>>empty();
    }

}
