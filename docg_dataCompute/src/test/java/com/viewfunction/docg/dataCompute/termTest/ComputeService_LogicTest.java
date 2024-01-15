package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeService;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.factory.ComputeGridTermFactory;

import java.util.Collection;

public class ComputeService_LogicTest {

    public static void main(String[] args){
        //runPerUnitTest();
        runGridTest();
    }

    private static void runPerUnitTest(){
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            VoidReturnComputeLogicA voidReturnComputeLogicA = new VoidReturnComputeLogicA();
            computeService.executePerUnitComputeLogic(voidReturnComputeLogicA);

            ValueReturnComputeLogicA valueReturnComputeLogicA = new ValueReturnComputeLogicA();
            Collection<Double> result = computeService.executePerUnitComputeLogic(valueReturnComputeLogicA);
            System.out.println(result);

            FixInputTypeComputeLogicA fixInputTypeComputeLogicA = new FixInputTypeComputeLogicA();
            Collection<String> collection = computeService.executePerUnitComputeLogic(fixInputTypeComputeLogicA,(double)1000);
            System.out.println(collection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void runGridTest(){
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            VoidReturnComputeLogicA voidReturnComputeLogicA = new VoidReturnComputeLogicA();
            computeService.executeGridSingletonComputeLogic(voidReturnComputeLogicA);

            ValueReturnComputeLogicA valueReturnComputeLogicA = new ValueReturnComputeLogicA();
            Double result = computeService.executeGridSingletonComputeLogic(valueReturnComputeLogicA);
            System.out.println(result);

            FixInputTypeComputeLogicA fixInputTypeComputeLogicA = new FixInputTypeComputeLogicA();
            String collection = computeService.executeGridSingletonComputeLogic(fixInputTypeComputeLogicA,(double)1000);
            System.out.println(collection);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
