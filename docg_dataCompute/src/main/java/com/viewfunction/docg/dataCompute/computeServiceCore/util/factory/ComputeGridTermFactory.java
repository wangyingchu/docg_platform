package com.viewfunction.docg.dataCompute.computeServiceCore.util.factory;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeFunction;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl.IgniteComputeFunctionImpl;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl.IgniteComputeGridImpl;

public class ComputeGridTermFactory {

    public static ComputeGrid getComputeGrid(){
        return new IgniteComputeGridImpl();
    }

    public static ComputeFunction getComputeFunction(){
        return new IgniteComputeFunctionImpl();
    }
}
