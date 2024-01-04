package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeFunction;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeService;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl.ComputeFunctionImpl;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.factory.ComputeGridTermFactory;

public class ComputeServiceTest {

    public static void main(String[] args) throws ComputeGridException {
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            ComputeFunction targetComputeFunction = new ComputeFunctionImpl() {
                @Override
                public void onPrepare() {
                    System.out.println("onPrepare_B");
                }

                @Override
                public void onRun() {
                    System.out.println("onFinish_B");
                }

                @Override
                public void onFinish() {
                    System.out.println("onRun_B");
                }
            };
            computeService.deployGridComputeFunction("testFunction3",targetComputeFunction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
