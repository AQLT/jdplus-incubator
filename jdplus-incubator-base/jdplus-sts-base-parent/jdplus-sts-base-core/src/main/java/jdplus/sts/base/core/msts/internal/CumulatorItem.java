/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdplus.sts.base.core.msts.internal;

import jdplus.sts.base.core.msts.StateItem;
import jdplus.toolkit.base.api.data.DoubleSeq;
import java.util.List;
import jdplus.toolkit.base.core.ssf.benchmarking.SsfCumulator;
import jdplus.sts.base.core.msts.MstsMapping;
import jdplus.sts.base.core.msts.ParameterInterpreter;
import jdplus.toolkit.base.core.ssf.ISsfLoading;
import jdplus.toolkit.base.core.ssf.StateComponent;

/**
 *
 * @author Jean Palate
 */
public class CumulatorItem extends StateItem {

    private final StateItem core;
    private final int period, start;

    public CumulatorItem(String name, StateItem core, int period, int start) {
        super(name);
        this.core = core;
        this.period = period;
        this.start = start;
    }
    
    @Override
    public CumulatorItem duplicate(){
        return new CumulatorItem(name, core.duplicate(), period, start);
    }

    @Override
    public void addTo(MstsMapping mapping) {
        List<ParameterInterpreter> parameters = core.parameters();
        for (ParameterInterpreter p : parameters) {
            mapping.add(p);
        }
        mapping.add((p, builder) -> {
            StateComponent cmp = core.build(p);
            ISsfLoading l = core.defaultLoading(0);
            builder.add(name, SsfCumulator.of(cmp, l, period, start), SsfCumulator.defaultLoading(l, period, start));
            return core.parametersCount();
        });

    }

    @Override
    public List<ParameterInterpreter> parameters() {
        return core.parameters();
    }

    @Override
    public StateComponent build(DoubleSeq p) {
        StateComponent cmp = core.build(p);
        ISsfLoading l = core.defaultLoading(0);
        return SsfCumulator.of(cmp, l, period, start);
    }

    @Override
    public int parametersCount() {
        return core.parametersCount();
    }

    @Override
    public ISsfLoading defaultLoading(int m) {
        if (m > 0) {
            return null;
        }
        ISsfLoading l = core.defaultLoading(0);
        return SsfCumulator.defaultLoading(l, period, start);
    }

    @Override
    public int defaultLoadingCount() {
        return 1;
    }

    @Override
    public int stateDim() {
        return 1+core.stateDim();
    }
}
