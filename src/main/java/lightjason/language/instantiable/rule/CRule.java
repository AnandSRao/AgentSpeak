/**
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the Light-Jason                                               #
 * # Copyright (c) 2015-16, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
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

package lightjason.language.instantiable.rule;

import com.google.common.collect.Multimap;
import lightjason.agent.IAgent;
import lightjason.common.IPath;
import lightjason.language.CCommon;
import lightjason.language.ILiteral;
import lightjason.language.ITerm;
import lightjason.language.execution.IContext;
import lightjason.language.execution.IExecution;
import lightjason.language.execution.action.CProxyRule;
import lightjason.language.execution.fuzzy.CFuzzyValue;
import lightjason.language.execution.fuzzy.IFuzzyValue;
import lightjason.language.variable.IVariable;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * rule structure
 *
 * @bug incomplete e.g. annotations are not existing
 */
public final class CRule implements IRule
{
    /**
     * identifier of the rule
     */
    protected final ILiteral m_id;
    /**
     * action list
     */
    protected final List<IExecution> m_action;
    /**
     * hash code
     */
    private final int m_hash;

    /**
     * ctor
     *
     * @param p_id literal with signature
     * @param p_action action list
     */
    public CRule( final ILiteral p_id, final List<IExecution> p_action )
    {
        m_id = p_id;
        m_action = Collections.unmodifiableList( p_action );
        m_hash = m_id.hashCode() + m_action.stream().mapToInt( i -> i.hashCode() ).sum();
    }

    @Override
    public final ILiteral getIdentifier()
    {
        return m_id;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final IRule replaceplaceholder( final Multimap<IPath, IRule> p_rules )
    {
        return new CRule(
                m_id,
                m_action.stream().map( i ->
                                               i instanceof CRulePlaceholder
                                               ? new CProxyRule( p_rules, ( (CRulePlaceholder) i ).getIdentifier() )
                                               : i
                ).collect( Collectors.toList() )
        );
    }

    @Override
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation
    )
    {
        return CFuzzyValue.from( false );
    }

    @Override
    public final double score( final IAgent p_agent )
    {
        return p_agent.getAggregation().evaluate( m_action.parallelStream().mapToDouble( i -> i.score( p_agent ) ).boxed() );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final Stream<IVariable<?>> getVariables()
    {
        return (Stream<IVariable<?>>) Stream.of(
                CCommon.recursiveterm( m_id.orderedvalues() ).filter( i -> i instanceof IVariable<?> ).map( i -> (IVariable<?>) i ),
                CCommon.recursiveliteral( m_id.annotations() ).filter( i -> i instanceof IVariable<?> ).map( i -> (IVariable<?>) i )

        )
                                            .reduce( Stream::concat )
                                            .orElseGet( Stream::<IVariable<?>>empty );
    }

    @Override
    public final int hashCode()
    {
        return m_hash;
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return m_id.hashCode() == p_object.hashCode();
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format(
                "{0} ({1} ==>> {2})",
                super.toString(),
                m_id,
                m_action
        );
    }

    @Override
    public final IContext instantiate( final IAgent p_agent, final Stream<IVariable<?>> p_variable
    )
    {
        return CCommon.instantiate( this, p_agent, p_variable );
    }

}
