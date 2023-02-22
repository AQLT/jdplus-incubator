/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdplus.msts;

import demetra.data.DoubleSeqCursor;
import jdplus.sarima.estimation.SarimaMapping;
import demetra.arima.SarimaOrders;

/**
 *
 * @author palatej
 */
public final class SarimaInterpreter implements ParameterInterpreter {

    private final String name;
    private double[] values;
    private final SarimaMapping mapping;
    private final int np;
    private boolean fixed;

    public SarimaInterpreter(final String name, final SarimaOrders spec, final double[] p, final boolean fixed) {
        this.name = name;
        this.mapping = SarimaMapping.of(spec);
        this.values = p == null ? mapping.getDefaultParameters().toArray() : p;
        this.np = spec.getParametersCount();
        this.fixed = fixed;
    }

    @Override
    public SarimaInterpreter duplicate(){
        return new SarimaInterpreter(name, mapping.getSpec(), values.clone(), fixed);
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
    public int decode(DoubleSeqCursor reader, double[] buffer, int pos) {
        if (!fixed) {
            for (int i = 0; i < np; ++i) {
                buffer[pos++] = reader.getAndNext();
            }
        } else {
            for (int i = 0; i < np; ++i) {
                buffer[pos++] = values[i];
            }
        }
        return pos;
    }

    @Override
    public int encode(DoubleSeqCursor reader, double[] buffer, int pos) {
        if (!fixed) {
            for (int i = 0; i < np; ++i) {
                buffer[pos++] = reader.getAndNext();
            }
        } else {
            reader.skip(np);
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
    public void free(){
        fixed=false;
    }

    @Override
    public SarimaMapping getDomain() {
        return mapping;
    }

    @Override
    public int fillDefault(double[] buffer, int pos) {
        if (! fixed) {
           for (int i = 0; i < values.length; ++i) {
                buffer[pos + i] = values[i];
            }
            return pos + values.length;
        } else {
            return pos;
        }
    }

    @Override
    public boolean isScaleSensitive(boolean variance) {
        return false;
    }

    @Override
    public int rescaleVariances(double factor, double[] buffer, int pos) {
        return pos+values.length;
    }

}
