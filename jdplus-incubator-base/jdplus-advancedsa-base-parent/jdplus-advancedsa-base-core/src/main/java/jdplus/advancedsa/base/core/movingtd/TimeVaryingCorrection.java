/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package jdplus.advancedsa.base.core.movingtd;

import jdplus.toolkit.base.api.timeseries.TsData;
import jdplus.toolkit.base.core.math.matrices.FastMatrix;
import jdplus.toolkit.base.core.sarima.SarimaModel;

/**
 *
 * @author palatej
 */
@lombok.Value
@lombok.Builder
public class TimeVaryingCorrection implements MovingTradingDaysCorrection{
    
    private FastMatrix tdCoefficients, stdeTdCoefficients;
    private TsData tdEffect, fullTdEffect, partialLinearizedSeries;
    private SarimaModel arima0, arima;
    private double aic0, aic;

}
