package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeService;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.factory.ComputeGridTermFactory;

import java.util.Collection;

public class ComputeService_LogicTest {

    public static void main(String[] args){
        runPerUnitTest();
    }

    private static void runPerUnitTest(){
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            VoidReturnComputeLogicA voidReturnComputeLogicA = new VoidReturnComputeLogicA();
            computeService.executePerUnitComputeLogic(voidReturnComputeLogicA);

            ValueReturnComputeLogicA valueReturnComputeLogicA = new ValueReturnComputeLogicA();
            Collection<Integer> result = computeService.executePerUnitComputeLogic(valueReturnComputeLogicA);
            System.out.println(result);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
