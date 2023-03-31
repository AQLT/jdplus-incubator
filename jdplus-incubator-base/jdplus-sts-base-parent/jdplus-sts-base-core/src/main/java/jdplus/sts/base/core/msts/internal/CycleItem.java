/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdplus.sts.base.core.msts.internal;

import jdplus.sts.base.core.msts.StateItem;
import jdplus.toolkit.base.api.data.DoubleSeq;
import jdplus.sts.base.core.msts.BoundedParameterInterpreter;
import jdplus.sts.base.core.msts.MstsMapping;
import jdplus.sts.base.core.msts.VarianceInterpreter;
import jdplus.toolkit.base.core.ssf.sts.CyclicalComponent;
import java.util.Arrays;
import java.util.List;
import jdplus.sts.base.core.msts.ParameterInterpreter;
import jdplus.toolkit.base.core.ssf.ISsfLoading;
import jdplus.toolkit.base.core.ssf.StateComponent;

/**
 *
 * @author palatej
 */
public class CycleItem extends StateItem {

    private final BoundedParameterInterpreter factor, period;
    private final VarianceInterpreter v;

    public CycleItem(String name, double dumpingFactor, double cyclicalPeriod, boolean fixedcycle, double cvar, boolean fixedvar) {
        super(name);
        factor = BoundedParameterInterpreter.builder()
                .name(name + ".factor")
                .value(dumpingFactor, fixedcycle)
                .bounds(0, 1, true)
                .build();
        period = BoundedParameterInterpreter.builder()
                .name(name + ".period")
                .value(cyclicalPeriod, fixedcycle)
                .bounds(2, Double.MAX_VALUE, false)
                .build();
        v = new VarianceInterpreter(name + ".var", cvar, fixedvar, true);
    }
    
    private CycleItem(CycleItem item){
        super(item.name);
        factor=item.factor.duplicate();
        period=item.period.duplicate();
        v=item.v.duplicate();
    }
    
    @Override
    public CycleItem duplicate(){
        return new CycleItem(this);
    }

    @Override
    public void addTo(MstsMapping mapping) {
        mapping.add(factor);
        mapping.add(period);
        mapping.add(v);
        mapping.add((p, builder) -> {
            double f = p.get(0), l = p.get(1), var = p.get(2);
            builder.add(name, CyclicalComponent.stateComponent(f, l, var), CyclicalComponent.defaultLoading());
            return 3;
        });
    }

    @Override
    public List<ParameterInterpreter> parameters() {
        return Arrays.asList(factor, period, v);
    }

    @Override
    public StateComponent build(DoubleSeq p) {
        double f = p.get(0), l = p.get(1), var = p.get(2);
        return CyclicalComponent.stateComponent(f, l, var);
    }

    @Override
    public int parametersCount() {
        return 3;
    }

    @Override
    public ISsfLoading defaultLoading(int m) {
        if (m > 0) {
            return null;
        }
        return CyclicalComponent.defaultLoading();
    }

    @Override
    public int defaultLoadingCount() {
        return 1;
    }

    @Override
    public int stateDim() {
        return 2;
    }
    
    @Override
    public boolean isScalable() {
        return !v.isFixed();
    }
}
