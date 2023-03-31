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
package jdplus.advancedsa.base.api;

import jdplus.toolkit.base.api.processing.AlgorithmDescriptor;
import jdplus.sa.base.api.SaSpecification;
import jdplus.sa.base.api.benchmarking.SaBenchmarkingSpec;
import jdplus.sts.base.api.BsmSpec;

/**
 *
 * @author palatej
 */
@lombok.Value
@lombok.Builder(toBuilder = true,  builderClassName="Builder")
public class SeasonalSpecificStsSpec implements SaSpecification{
    public static final String METHOD = "sssts";
     public static final String VERSION = "3.0.0";
    
    public static final AlgorithmDescriptor DESCRIPTOR = new AlgorithmDescriptor(FAMILY, METHOD, VERSION);

    public static SeasonalSpecificStsSpec.Builder builder() {
        return new SeasonalSpecificStsSpec.Builder()
                .preprocessing(PreprocessingSpec.TRAMO)
                .bsm(BsmSpec.DEFAULT)
                .benchmarking(SaBenchmarkingSpec.DEFAULT_DISABLED);
    }
    
   @Override
    public AlgorithmDescriptor getAlgorithmDescriptor() {
        return DESCRIPTOR;
    }
    
    @lombok.NonNull
    private PreprocessingSpec preprocessing;
    @lombok.NonNull
    private BsmSpec bsm;
    @lombok.NonNull
    private SeasonalSpecificSpec seasonal;
    @lombok.NonNull
    private SaBenchmarkingSpec benchmarking;

    
}
