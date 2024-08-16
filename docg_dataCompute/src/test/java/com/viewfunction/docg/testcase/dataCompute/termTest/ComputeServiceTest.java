package com.viewfunction.docg.testcase.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeService;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory.ComputeGridTermFactory;
import com.viewfunction.docg.testcase.dataCompute.termTest.computeServiceLogic.FixInputTypeComputeLogicA;
import com.viewfunction.docg.testcase.dataCompute.termTest.computeServiceLogic.ValueReturnComputeLogicA;
import com.viewfunction.docg.testcase.dataCompute.termTest.computeServiceLogic.VoidReturnComputeLogicA;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Collection;

public class ComputeServiceTest {

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for ComputeServiceTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testComputeServiceFunction() throws ComputeGridException {
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
}
