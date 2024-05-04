package com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.spi.ignite.termImpl.IgniteComputeGridImpl;

public class ComputeGridTermFactory {

    public static ComputeGrid getComputeGrid(){
        return new IgniteComputeGridImpl();
    }
}
