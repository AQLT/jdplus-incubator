/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdplus.msts;

import jdplus.data.DataBlock;
import demetra.data.DoubleSeqCursor;
import jdplus.math.functions.IParametersDomain;
import jdplus.math.functions.ParamValidation;
import jdplus.math.polynomials.Polynomial;
import jdplus.sarima.estimation.SarimaMapping;
import org.checkerframework.checker.nullness.qual.NonNull;
import demetra.data.DoubleSeq;
import jdplus.math.linearfilters.FilterUtility;

/**
 *
 * @author palatej
 */
public class StablePolynomialInterpreter implements ParameterInterpreter {

    private final String name;
    private final double[] values;
    private final Domain domain;
    private boolean fixed;

    public StablePolynomialInterpreter(final String name, @NonNull double[] values, boolean fixed) {
        this.name = name;
        this.values = values;
        this.fixed = fixed;
        this.domain = new Domain(values.length);
    }

    @Override
    public StablePolynomialInterpreter duplicate() {
        return new StablePolynomialInterpreter(name, values.clone(), fixed);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isFixed() {
        return fixed;
    }

    @Override
    public boolean isScaleSensitive(boolean variance) {
        return false;
    }

    @Override
    public int rescaleVariances(double factor, double[] buffer, int pos) {
        return pos + values.length;
    }

    @Override
    public int decode(DoubleSeqCursor reader, double[] buffer, int pos) {
        if (!fixed) {
            for (int i = 0; i < values.length; ++i) {
                buffer[pos++] = reader.getAndNext();
            }
        } else {
            for (int i = 0; i < values.length; ++i) {
                buffer[pos++] = values[i];
            }
        }
        return pos;
    }

    @Override
    public int encode(DoubleSeqCursor reader, double[] buffer, int pos) {
        if (!fixed) {
            for (int i = 0; i < values.length; ++i) {
                buffer[pos++] = reader.getAndNext();
            }
        } else {
            reader.skip(values.length);
        }
        return pos;
    }

    @Override
    public void fixModelParameter(DoubleSeqCursor reader) {
        for (int i = 0; i < values.length; ++i) {
            values[i] = reader.getAndNext();
        }
        fixed = true;
    }

    @Override
    public void free() {
        fixed = false;
    }

    @Override
    public IParametersDomain getDomain() {
        return domain;
    }

    @Override
    public int fillDefault(double[] buffer, int pos) {
        if (!fixed) {
            for (int i = 0; i < values.length; ++i) {
                buffer[pos + i] = values[i];
            }
            return pos + values.length;
        } else {
            return pos;
        }
    }

    static class Domain implements IParametersDomain {

        private final int degree;

        Domain(int degree) {
            this.degree = degree;
        }

        @Override
        public boolean checkBoundaries(DoubleSeq inparams) {
            return FilterUtility.checkStability(inparams);
        }

        @Override
        public double epsilon(DoubleSeq inparams, int idx) {
            return 1e-6;
        }

        @Override
        public int getDim() {
            return degree;
        }

        @Override
        public double lbound(int idx) {
            return -Double.MAX_VALUE;
        }

        @Override
        public double ubound(int idx) {
            return Double.MAX_VALUE;
        }

        @Override
        public ParamValidation validate(DataBlock ioparams) {
            boolean rslt = FilterUtility.stabilize(ioparams, 1);
            if (rslt) {
                return ParamValidation.Changed;
            } else {
                return ParamValidation.Valid;
            }
        }

    }

}
