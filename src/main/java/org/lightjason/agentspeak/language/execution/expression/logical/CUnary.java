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

package org.lightjason.agentspeak.language.execution.expression.logical;

import org.lightjason.agentspeak.error.CIllegalArgumentException;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.expression.EOperator;
import org.lightjason.agentspeak.language.execution.expression.IBaseUnary;
import org.lightjason.agentspeak.language.execution.expression.IExpression;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * logical unary expression
 */
public final class CUnary extends IBaseUnary
{

    /**
     * ctor
     *
     * @param p_operator operator
     * @param p_expression expression
     */
    public CUnary( final EOperator p_operator, final IExpression p_expression )
    {
        super( p_operator, p_expression );
        if ( !m_operator.isLogical() )
            throw new CIllegalArgumentException( org.lightjason.agentspeak.common.CCommon.languagestring( this, "operator", m_operator ) );
    }

    @Override
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation
    )
    {
        final List<ITerm> l_argument = new LinkedList<>();
        if ( ( !m_expression.execute( p_context, p_parallel, Collections.<ITerm>emptyList(), l_argument, Collections.<ITerm>emptyList() ).value() )
             || ( l_argument.size() != 1 ) )
            return CFuzzyValue.from( false );


        switch ( m_operator )
        {
            case NEGATION:
                p_return.add( CRawTerm.from(
                    !l_argument.get( 0 ).<Boolean>raw()
                ) );
                return CFuzzyValue.from( true );

            default:
                return CFuzzyValue.from( false );
        }
    }

}
