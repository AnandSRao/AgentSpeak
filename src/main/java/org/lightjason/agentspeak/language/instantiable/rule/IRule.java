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

package org.lightjason.agentspeak.language.instantiable.rule;

import com.google.common.collect.Multimap;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.instantiable.IInstantiable;


/**
 * interface of logical rule
 */
public interface IRule extends IInstantiable
{

    /**
     * returns the identifier of the rule
     *
     * @return literal
     */
    ILiteral getIdentifier();

    /**
     * replaces all placeholder objects and reinstantiate object
     *
     * @param p_rules full instantiated rules
     * @return new object instance without placeholders
     */
    IRule replaceplaceholder( final Multimap<IPath, IRule> p_rules );

}
