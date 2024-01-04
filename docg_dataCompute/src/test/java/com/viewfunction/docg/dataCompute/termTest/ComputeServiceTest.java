package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeFunction;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeFunctionCallback;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeService;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl.IgniteComputeFunctionAbstractImpl;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.factory.ComputeGridTermFactory;

public class ComputeServiceTest {

    public static void main(String[] args) throws ComputeGridException {
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        //ComputeService computeService = targetComputeGrid.getComputeService();

        try(ComputeService computeService = targetComputeGrid.getComputeService()){


            ComputeFunction targetComputeFunction = new IgniteComputeFunctionAbstractImpl() {
                @Override
                public void onPrepare() {
                    System.out.println("onPrepare_A");
                }

                @Override
                public void onRun() {
                    System.out.println("onFinish_A");
                }

                @Override
                public void onFinish() {
                    System.out.println("onRun_A");
                }


            };

                    ;

            /*
            targetComputeFunction.setComputeFunctionCallback(new ComputeFunctionCallback() {
                private static final long serialVersionUID = 100L;
                @Override
                public void onPrepare() {
                    System.out.println("onPrepare_A");
                }

                @Override
                public void onFinish() {
                    System.out.println("onFinish_A");
                }

                @Override
                public void onRun() {
                    System.out.println("onRun_A");
                }
            });
            */

            computeService.deployGridComputeFunction("testFunction3",targetComputeFunction);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
