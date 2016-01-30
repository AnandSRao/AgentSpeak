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

package lightjason.agent.action.buildin.math.statistic;

import lightjason.agent.action.buildin.IBuildinAction;
import lightjason.error.CIllegalStateException;
import lightjason.language.CCommon;
import lightjason.language.CRawTerm;
import lightjason.language.ITerm;
import lightjason.language.execution.IContext;
import lightjason.language.execution.fuzzy.CBoolean;
import lightjason.language.execution.fuzzy.IFuzzyValue;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.List;


/**
 * action to return a statistic value
 */
public final class CGetStatisticValue extends IBuildinAction
{

    /**
     * ctor
     */
    public CGetStatisticValue()
    {
        super( 3 );
    }

    @Override
    public final int getMinimalArgumentNumber()
    {
        return 2;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final IFuzzyValue<Boolean> execute( final IContext<?> p_context, final Boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation
    )
    {
        final StatisticalSummary l_statistic = CCommon.<StatisticalSummary, ITerm>getRawValue( p_argument.get( 0 ) );
        final EValue l_value = EValue.valueOf( CCommon.<String, ITerm>getRawValue( p_argument.get( 1 ) ).trim().toUpperCase() );

        if ( l_statistic instanceof SummaryStatistics )
        {
            p_return.add( CRawTerm.from( l_value.get( (SummaryStatistics) l_statistic, p_argument.subList( 2, p_argument.size() ) ) ) );
            return CBoolean.from( true );
        }

        if ( l_statistic instanceof DescriptiveStatistics )
        {
            p_return.add( CRawTerm.from( l_value.get( (DescriptiveStatistics) l_statistic, p_argument.subList( 2, p_argument.size() ) ) ) );
            return CBoolean.from( true );
        }

        return CBoolean.from( false );
    }



    /**
     * enum of statistic value types
     */
    private enum EValue
    {
        GEOMETRICMEAN,
        MAX,
        MIN,
        COUNT,
        POPULATIONVARIANCE,
        QUADRATICMEAN,
        SECONDMOMENT,
        STANDARDDEVIATION,
        SUM,
        SUMLOG,
        SUMSQUARE,
        VARIANCE,
        MEAN,
        KURTIOSIS,
        PERCENTILE;

        /**
         * returns a statistic value
         *
         * @param p_statistic statistic object
         * @param p_argument argument
         * @return value
         */
        public final Number get( final SummaryStatistics p_statistic, final List<ITerm> p_argument )
        {
            switch ( this )
            {
                case GEOMETRICMEAN:
                    return p_statistic.getGeometricMean();

                case MAX:
                    return p_statistic.getMax();

                case MIN:
                    return p_statistic.getMin();

                case COUNT:
                    return p_statistic.getN();

                case POPULATIONVARIANCE:
                    return p_statistic.getPopulationVariance();

                case QUADRATICMEAN:
                    return p_statistic.getQuadraticMean();

                case SECONDMOMENT:
                    return p_statistic.getSecondMoment();

                case STANDARDDEVIATION:
                    return p_statistic.getStandardDeviation();

                case SUM:
                    return p_statistic.getSum();

                case SUMLOG:
                    return p_statistic.getSumOfLogs();

                case SUMSQUARE:
                    return p_statistic.getSumsq();

                case VARIANCE:
                    return p_statistic.getVariance();

                case MEAN:
                    return p_statistic.getMean();

                default:
                    throw new CIllegalStateException( lightjason.common.CCommon.getLanguageString( this, "unknown", this ) );
            }
        }

        /**
         * returns a statistic value
         *
         * @param p_statistic statistic object
         * @param p_argument argument
         * @return value
         */
        public final Number get( final DescriptiveStatistics p_statistic, final List<ITerm> p_argument )
        {
            switch ( this )
            {
                case GEOMETRICMEAN:
                    return p_statistic.getGeometricMean();

                case MAX:
                    return p_statistic.getMax();

                case MIN:
                    return p_statistic.getMin();

                case COUNT:
                    return p_statistic.getN();

                case POPULATIONVARIANCE:
                    return p_statistic.getPopulationVariance();

                case QUADRATICMEAN:
                    return p_statistic.getQuadraticMean();

                case STANDARDDEVIATION:
                    return p_statistic.getStandardDeviation();

                case SUM:
                    return p_statistic.getSum();

                case SUMSQUARE:
                    return p_statistic.getSumsq();

                case VARIANCE:
                    return p_statistic.getVariance();

                case MEAN:
                    return p_statistic.getMean();

                case KURTIOSIS:
                    return p_statistic.getKurtosis();

                case PERCENTILE:
                    return p_statistic.getPercentile( CCommon.<Number, ITerm>getRawValue( p_argument.get( 0 ) ).doubleValue() );

                default:
                    throw new CIllegalStateException( lightjason.common.CCommon.getLanguageString( this, "unknown", this ) );
            }
        }
    }

}