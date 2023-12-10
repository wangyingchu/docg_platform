package com.viewfunction.docg.dataCompute.computeServiceCore.util.factory;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl.IgniteComputeGridImpl;

public class ComputeGridTermFactory {

    public static ComputeGrid getComputeGrid(){
        return new IgniteComputeGridImpl();
    }
}
