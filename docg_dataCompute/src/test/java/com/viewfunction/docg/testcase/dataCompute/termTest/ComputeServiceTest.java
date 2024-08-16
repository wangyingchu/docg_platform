package com.viewfunction.docg.testcase.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeService;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ValueReturnComputeLogic;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.VoidReturnComputeLogic;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory.ComputeGridTermFactory;
import com.viewfunction.docg.testcase.dataCompute.termTest.computeServiceLogic.*;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
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
        //PerUnitTest
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            VoidReturnComputeLogicA voidReturnComputeLogicA = new VoidReturnComputeLogicA();
            computeService.executePerUnitVoidReturnComputeLogic(voidReturnComputeLogicA);

            ValueReturnComputeLogicA valueReturnComputeLogicA = new ValueReturnComputeLogicA();
            Collection<Double> result = computeService.executePerUnitValueReturnComputeLogic(valueReturnComputeLogicA);
            Assert.assertNotNull(result);
            Assert.assertTrue(result.size()>0);
            Assert.assertTrue(result.iterator().next()>0);

            FixInputTypeComputeLogicA fixInputTypeComputeLogicA = new FixInputTypeComputeLogicA();
            Collection<String> collection = computeService.executePerUnitFixInputTypeComputeLogic(fixInputTypeComputeLogicA,(double)1000);
            Assert.assertNotNull(collection);
            Assert.assertTrue(collection.size()>0);
            Assert.assertTrue(collection.iterator().next().contains("__ResultStr__"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //GridTest
        targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            VoidReturnComputeLogicA voidReturnComputeLogicA = new VoidReturnComputeLogicA();
            computeService.executeGridSingletonVoidReturnComputeLogic(voidReturnComputeLogicA);

            ValueReturnComputeLogicA valueReturnComputeLogicA = new ValueReturnComputeLogicA();
            Double result = computeService.executeGridSingletonValueReturnComputeLogic(valueReturnComputeLogicA);
            Assert.assertNotNull(result);
            Assert.assertTrue(result>0);

            FixInputTypeComputeLogicA fixInputTypeComputeLogicA = new FixInputTypeComputeLogicA();
            String collection = computeService.executeGridSingletonFixInputTypeComputeLogic(fixInputTypeComputeLogicA,(double)1000);
            Assert.assertNotNull(collection);
            Assert.assertTrue(collection.contains("__ResultStr__"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //MultipleTest
        targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
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
            Assert.assertNotNull(result);
            Assert.assertTrue(result.size()==2);
            for(Double currentResult:result){
                Assert.assertTrue(currentResult>0);
            }

            FixInputTypeComputeLogicA fixInputTypeComputeLogic = new FixInputTypeComputeLogicA();
            Collection<Double> fixInputTypeComputeLogicParamList = new ArrayList<>();
            fixInputTypeComputeLogicParamList.add(Double.valueOf(123.44));
            fixInputTypeComputeLogicParamList.add(Double.valueOf(342.345));
            fixInputTypeComputeLogicParamList.add(Double.valueOf(902.345));
            fixInputTypeComputeLogicParamList.add(Double.valueOf(92237.333));
            Collection<String> collection = computeService.executeGridMultipleFixInputTypeComputeLogic(fixInputTypeComputeLogic,fixInputTypeComputeLogicParamList);

            Assert.assertNotNull(collection);
            Assert.assertTrue(collection.size()==4);
            for(String currentResult:collection){
                Assert.assertTrue(currentResult.contains("__ResultStr__"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //doDeploy
        targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            ComputeFunctionImplementationA computeFunctionImplementationA = new ComputeFunctionImplementationA();
            computeService.deployGridComputeFunction("testFunction1",computeFunctionImplementationA);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //doInvoke
        targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            ComputeFunctionA computeFunctionA = computeService.getComputeFunction("testFunction1", ComputeFunctionA.class);
            String result = computeFunctionA.doSomeThing("hello world");
            System.out.println(result);
            Assert.assertNotNull(result);
            Assert.assertEquals(result,"compute some thing -> hello world");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
