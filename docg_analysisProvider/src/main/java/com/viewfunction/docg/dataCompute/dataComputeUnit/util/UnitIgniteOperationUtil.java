package com.viewfunction.docg.dataCompute.dataComputeUnit.util;


import com.viewfunction.docg.dataCompute.exception.ComputeGridNotActiveException;
import org.apache.ignite.Ignite;

public class UnitIgniteOperationUtil {

    public static UnitOperationResult isGridActive(Ignite nodeIgnite){
        UnitOperationResult unitOperationResult =new UnitOperationResult();
        unitOperationResult.setResult(nodeIgnite.active());
        StringBuffer lsDataStoreMessageStringBuffer=new StringBuffer();
        lsDataStoreMessageStringBuffer.append("\n\r");
        lsDataStoreMessageStringBuffer.append("================================================================");
        lsDataStoreMessageStringBuffer.append("\n\r");
        if(!unitOperationResult.getResult()){
            lsDataStoreMessageStringBuffer.append("Grid is not active, please active grid with actvgrid command.");
        }else{
            lsDataStoreMessageStringBuffer.append("Grid is active");
        }
        lsDataStoreMessageStringBuffer.append("\n\r");
        lsDataStoreMessageStringBuffer.append("================================================================");
        unitOperationResult.setResultMessage(lsDataStoreMessageStringBuffer.toString());
        return unitOperationResult;
    }

    public static void checkGridActiveStatus(Ignite nodeIgnite) throws ComputeGridNotActiveException {
        if(!nodeIgnite.active()){
            throw new ComputeGridNotActiveException();
        }
    }
}
