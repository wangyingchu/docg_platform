package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeService;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.factory.ComputeGridTermFactory;

public class ComputeService_FunctionTest {

    public static void main(String[] args) throws ComputeGridException{
        doDeploy();
        doInvoke();
    }

    public static void doDeploy() {
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            ComputeFunctionImplementationA computeFunctionImplementationA = new ComputeFunctionImplementationA();
            computeService.deployGridComputeFunction("testFunction1",computeFunctionImplementationA);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void doInvoke() {
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(ComputeService computeService = targetComputeGrid.getComputeService()){
            ComputeFunctionA computeFunctionA = computeService.getComputeFunction("testFunction1", ComputeFunctionA.class);
            String result = computeFunctionA.doSomeThing("hello world");
            System.out.println(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
