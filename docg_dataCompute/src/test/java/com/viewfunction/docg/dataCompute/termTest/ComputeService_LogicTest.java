package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeService;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.factory.ComputeGridTermFactory;

public class ComputeService_LogicTest {

    public static void main(String[] args){
        runPerUnitTest();
    }

    private static void runPerUnitTest(){
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            VoidReturnComputeLogicA voidReturnComputeLogicA = new VoidReturnComputeLogicA();
            computeService.executePerUnitComputeLogic(voidReturnComputeLogicA);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
