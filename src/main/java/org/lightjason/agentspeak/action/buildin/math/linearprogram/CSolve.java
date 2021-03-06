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

package org.lightjason.agentspeak.action.buildin.math.linearprogram;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.lightjason.agentspeak.action.buildin.IBuildinAction;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


/**
 * solves the linear program
 */
public final class CSolve extends IBuildinAction
{

    /**
     * ctor
     */
    public CSolve()
    {
        super( 3 );
    }

    @Override
    public final int minimalArgumentNumber()
    {
        return 1;
    }

    @Override
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation
    )
    {
        // first argument is the LP pair object, second argument is the goal-type (maximize / minimize),
        // third & four argument can be the number of iterations or string with "non-negative" variables
        final List<OptimizationData> l_settings = new LinkedList<>();

        final Pair<LinearObjectiveFunction, Collection<LinearConstraint>> l_default = p_argument.get( 0 ).raw();
        l_settings.add( l_default.getLeft() );
        l_settings.add( new LinearConstraintSet( l_default.getRight() ) );

        p_argument.subList( 1, p_argument.size() ).stream()
                                 .map( i -> {
                                     if ( CCommon.rawvalueAssignableTo( i, Number.class ) )
                                         return new MaxIter( i.raw() );

                                     if ( CCommon.rawvalueAssignableTo( i, String.class ) )
                                         switch ( i.<String>raw().trim().toLowerCase() )
                                         {
                                             case "non-negative":
                                                 return new NonNegativeConstraint( true );
                                             case "maximize":
                                                 return GoalType.MAXIMIZE;
                                             case "minimize":
                                                 return GoalType.MINIMIZE;

                                             default:
                                                 return null;
                                         }

                                     return null;
                                 } )
                                 .filter( Objects::nonNull )
                                 .forEach( l_settings::add );



        // optimze and return
        final SimplexSolver l_lp = new SimplexSolver();
        final PointValuePair l_result = l_lp.optimize( l_settings.toArray( new OptimizationData[l_settings.size()] ) );

        p_return.add( CRawTerm.from( l_result.getValue() ) );
        p_return.add( CRawTerm.from( l_result.getPoint().length ) );
        Arrays.stream( l_result.getPoint() ).boxed().map( CRawTerm::from ).forEach( p_return::add );

        return CFuzzyValue.from( true );
    }

}
