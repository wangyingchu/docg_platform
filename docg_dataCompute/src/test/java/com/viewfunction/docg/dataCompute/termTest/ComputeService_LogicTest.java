package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.*;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.factory.ComputeGridTermFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComputeService_LogicTest {

    public static void main(String[] args){
        //runPerUnitTest();
        //runGridTest();
        runMultipleTest();
    }

    private static void runPerUnitTest(){
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            VoidReturnComputeLogicA voidReturnComputeLogicA = new VoidReturnComputeLogicA();
            computeService.executePerUnitVoidReturnComputeLogic(voidReturnComputeLogicA);

            ValueReturnComputeLogicA valueReturnComputeLogicA = new ValueReturnComputeLogicA();
            Collection<Double> result = computeService.executePerUnitValueReturnComputeLogic(valueReturnComputeLogicA);
            System.out.println(result);

            FixInputTypeComputeLogicA fixInputTypeComputeLogicA = new FixInputTypeComputeLogicA();
            Collection<String> collection = computeService.executePerUnitFixInputTypeComputeLogic(fixInputTypeComputeLogicA,(double)1000);
            System.out.println(collection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void runGridTest(){
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            VoidReturnComputeLogicA voidReturnComputeLogicA = new VoidReturnComputeLogicA();
            computeService.executeGridSingletonVoidReturnComputeLogic(voidReturnComputeLogicA);

            ValueReturnComputeLogicA valueReturnComputeLogicA = new ValueReturnComputeLogicA();
            Double result = computeService.executeGridSingletonValueReturnComputeLogic(valueReturnComputeLogicA);
            System.out.println(result);

            FixInputTypeComputeLogicA fixInputTypeComputeLogicA = new FixInputTypeComputeLogicA();
            String collection = computeService.executeGridSingletonFixInputTypeComputeLogic(fixInputTypeComputeLogicA,(double)1000);
            System.out.println(collection);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void runMultipleTest(){
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            VoidReturnComputeLogicA voidReturnComputeLogic1 = new VoidReturnComputeLogicA();
            VoidReturnComputeLogicA voidReturnComputeLogic2 = new VoidReturnComputeLogicA();
            Collection<VoidReturnComputeLogic> voidReturnComputeLogicList = new ArrayList<>();
            voidReturnComputeLogicList.add(voidReturnComputeLogic1);
            voidReturnComputeLogicList.add(voidReturnComputeLogic2);
            computeService.executeGridMultipleVoidReturnComputeLogic(voidReturnComputeLogicList);

            ValueReturnComputeLogicA valueReturnComputeLogic1 = new ValueReturnComputeLogicA();
            ValueReturnComputeLogicA valueReturnComputeLogic2 = new ValueReturnComputeLogicA();
            Collection<ValueReturnComputeLogic<Double>> valueReturnComputeLogicList = new ArrayList<>();
            valueReturnComputeLogicList.add(valueReturnComputeLogic1);
            valueReturnComputeLogicList.add(valueReturnComputeLogic2);
            Collection<Double> result = computeService.executeGridMultipleValueReturnComputeLogic(valueReturnComputeLogicList);
            System.out.println(result);

            FixInputTypeComputeLogicA fixInputTypeComputeLogic = new FixInputTypeComputeLogicA();
            Collection<Double> fixInputTypeComputeLogicParamList = new ArrayList<>();
            fixInputTypeComputeLogicParamList.add(Double.valueOf(123.44));
            fixInputTypeComputeLogicParamList.add(Double.valueOf(342.345));
            //fixInputTypeComputeLogicParamList.add(Double.valueOf(902.345));
            //fixInputTypeComputeLogicParamList.add(Double.valueOf(92237.333));
            Collection<String> collection = computeService.executeGridMultipleFixInputTypeComputeLogic(fixInputTypeComputeLogic,fixInputTypeComputeLogicParamList);
            System.out.println(collection);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
