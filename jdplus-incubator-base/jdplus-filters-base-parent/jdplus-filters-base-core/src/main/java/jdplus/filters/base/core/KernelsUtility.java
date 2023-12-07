/*
 * Copyright 2023 National Bank of Belgium
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
package jdplus.filters.base.core;

import java.util.function.DoubleUnaryOperator;
import jdplus.toolkit.base.core.math.linearfilters.SymmetricFilter;

/**
 *
 * @author Jean Palate
 */
@lombok.experimental.UtilityClass
public class KernelsUtility {

    /**
     * Computes the discrete weights corresponding to a continuous kernel
     *
     * @param kernel The continuous kernel
     * @param bandwidth The bandwidth parameter.
     * @param m The length of the symmetric filter: from -m to m (included)
     * @return
     */
    public SymmetricFilter symmetricFilter(DoubleUnaryOperator kernel, double bandwidth, int m) {
        return SymmetricFilter.ofInternal(symmetricWeights(kernel, bandwidth, m));
    }

    private double[] symmetricWeights(DoubleUnaryOperator kernel, double bandwidth, int m) {
        double[] c = new double[m + 1];
        c[0] = kernel.applyAsDouble(0);
        double s = 0;
        for (int i = 1; i <= m; ++i) {
            c[i] = kernel.applyAsDouble(i / bandwidth);
            s += c[i];
        }
        s = c[0] + 2 * s;
        for (int i = 0; i <= m; ++i) {
            c[i] /= s;
        }
        return c;
    }


    public double discreteMoment(DoubleUnaryOperator kernel, double bandwidth, int l, int u, int order) {
        double s = 0;
        for (int i = l; i <= u; ++i) {
            int k = 1;
            for (int j = 1; j <= order; ++j) {
                k *= i;
            }
            s += k * kernel.applyAsDouble(i / bandwidth);
        }
        return s;
    }

    public double discreteSymmetricMoment(DoubleUnaryOperator kernel, double bandwidth, int m, int order) {
        double s = 0;
        for (int i = 1; i <= m; ++i) {
            int k = 1;
            for (int j = 1; j <= order; ++j) {
                k *= i;
            }
            s += 2 * k * kernel.applyAsDouble(i / bandwidth);
        }
        return s;
    }

    /**
     * Computes the bandwidth b such that sum(j^order*k(j*b)) == 0. Binary
     * search in the range[m, m+2]
     *
     * @param kernel Symmetric kernel
     * @param m length of the corresponding discrete filter
     * @param order order of the constraint. Usually 2
     * @return
     */
    public double optimalBandWidth(DoubleUnaryOperator kernel, int m, int order) {
        final int n = 20;
        final double step = 2.0 / n;
        double[] q = new double[n];
        for (int i = 0; i < n; ++i) {
            double b = m + i * step - .5;
            double z = discreteSymmetricMoment(kernel, b, m, order);
            q[i] = z;
        }

        double r = 0;
        double smin = Double.MAX_VALUE;
        for (int i = 1; i < n; ++i) {
            if (q[i] * q[i - 1] < 0) {
                double b = m + i * step - .5, a = b - step;
                if (q[i] < 0) {
                    double tmp = a;
                    a = b;
                    b = tmp;
                }
                while (Math.abs(b - a) > 1e-6) {
                    double z = (a + b) / 2;
                    double e = discreteSymmetricMoment(kernel, z, m, order);
                    if (e < 0) {
                        a = z;
                    } else {
                        b = z;
                    }
                }
                double rcur = (a + b) / 2;
                double[] w = symmetricWeights(kernel, rcur, m);
                double c = hendersonCriterion(w);
                if (c < smin) {
                    smin = c;
                    r = rcur;
                }
            }
        }
        return r;
    }

    private double hendersonCriterion(double[] w) {
        double s = 0;
        for (int i = 0; i < w.length; ++i) {
            double z = w[i] - 3 * w[i - 1 >= 0 ? i - 1 : 1 - i] + 3 * w[i - 2 >= 0 ? i - 2 : 2 - i] - w[i - 3 >= 0 ? i - 3 : 3 - i];
            s += z * z;

        }
        return s;
    }


}
