package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataService;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.factory.ComputeGridTermFactory;

import java.util.List;
public class ComputeGridTest {

    public static void main(String[] args)  {

        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        System.out.println(targetComputeGrid.getGridImplTech());

        try(DataService dataService = targetComputeGrid.getDataService()){

            List<String> dataSliceList = dataService.listDataSlices();
            System.out.println(dataSliceList);


        } catch (ComputeGridException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
