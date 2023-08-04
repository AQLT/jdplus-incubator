/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdplus.x11plus.base.core.x13;

import jdplus.toolkit.base.api.timeseries.TsData;

/**
 *
 * @author palatej
 */
@lombok.Value
@lombok.Builder
public class X13plusPreadjustment {
    
    TsData a1, a1a, a1b, a6, a7, a8, a8t, a8s, a8i, a9, a9u, a9sa, a9ser;
}
