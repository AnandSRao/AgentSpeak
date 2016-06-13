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

package org.lightjason.agentspeak.beliefbase;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import org.lightjason.agentspeak.agent.IAgent;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


/**
 * thread-safe storage of the data
 *
 * @tparam N multi-element type
 * @tparam M single-element type
 * @tparam T agent type
 */
public final class CStorage<N, M, T extends IAgent<?>> implements IStorage<N, M, T>
{
    /**
     * map with elements
     **/
    private final SetMultimap<String, N> m_multielements = Multimaps.synchronizedSetMultimap( HashMultimap.create() );
    /**
     * map with single elements
     **/
    private final Map<String, M> m_singleelements = new ConcurrentHashMap<>();
    /**
     * belief perceiver object
     */
    private final IBeliefPerceive<T> m_perceive;

    /**
     * ctor
     */
    @SuppressWarnings( "unchecked" )
    public CStorage()
    {
        this( (IBeliefPerceive<T>) IBeliefPerceive.EMPTY );
    }

    /**
     * ctor
     *
     * @param p_perceive perceive object
     */
    public CStorage( final IBeliefPerceive<T> p_perceive )
    {
        m_perceive = p_perceive;
    }

    @Override
    public final SetMultimap<String, N> getMultiElements()
    {
        return m_multielements;
    }

    @Override
    public final Map<String, M> getSingleElements()
    {
        return m_singleelements;
    }

    @Override
    public final Stream<N> streamMultiElements()
    {
        return m_multielements.values().stream();
    }

    @Override
    public final Stream<M> streamSingleElements()
    {
        return m_singleelements.values().stream();
    }

    @Override
    public final boolean containsMultiElement( final String p_key )
    {
        return m_multielements.containsKey( p_key );
    }

    @Override
    public final boolean containsSingleElement( final String p_key )
    {
        return m_singleelements.containsKey( p_key );
    }

    @Override
    public final boolean putMultiElements( final String p_key, final N p_value )
    {
        return m_multielements.put( p_key, p_value );
    }

    @Override
    public final boolean putSingleElements( final String p_key, final M p_value )
    {
        return m_singleelements.put( p_key, p_value ) == null;
    }

    @Override
    public final boolean removeMultiElements( final String p_key, final N p_value )
    {
        return m_multielements.remove( p_key, p_value );
    }

    @Override
    public final boolean removeSingleElements( final String p_key )
    {
        return m_singleelements.remove( p_key ) != null;
    }

    @Override
    public final M getSingleElement( final String p_key )
    {
        return m_singleelements.get( p_key );
    }

    @Override
    public final M getSingleElementOrDefault( final String p_key, final M p_default )
    {
        return m_singleelements.getOrDefault( p_key, p_default );
    }

    @Override
    public final Collection<N> getMultiElement( final String p_key )
    {
        return m_multielements.get( p_key );
    }

    @Override
    public final void clear()
    {
        m_multielements.clear();
        m_singleelements.clear();
    }

    @Override
    public final boolean contains( final String p_key )
    {
        return m_multielements.containsKey( p_key ) || m_singleelements.containsKey( p_key );
    }

    @Override
    public final boolean isEmpty()
    {
        return m_multielements.isEmpty() && m_singleelements.isEmpty();
    }

    @Override
    public final T update( final T p_agent )
    {
        return m_perceive.perceive( p_agent );
    }

    @Override
    public final int size()
    {
        return m_multielements.asMap().values().stream().mapToInt( Collection::size ).sum();
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format( "[multi elements: {0}, single elements: {1}]", m_multielements, m_singleelements );
    }
}
