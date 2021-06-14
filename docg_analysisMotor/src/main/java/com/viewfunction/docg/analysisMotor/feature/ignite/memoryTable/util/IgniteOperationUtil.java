package com.viewfunction.docg.analysisMotor.feature.ignite.memoryTable.util;

import com.viewfunction.docg.analysisMotor.feature.ignite.memoryTable.exception.MemoryTablesGridNotActiveException;
import org.apache.ignite.Ignite;

public class IgniteOperationUtil {

    public static IgniteOperationResult isGridActive(Ignite nodeIgnite){
        IgniteOperationResult igniteOperationResult =new IgniteOperationResult();
        igniteOperationResult.setResult(nodeIgnite.active());
        StringBuffer lsDataStoreMessageStringBuffer=new StringBuffer();
        lsDataStoreMessageStringBuffer.append("\n\r");
        lsDataStoreMessageStringBuffer.append("================================================================");
        lsDataStoreMessageStringBuffer.append("\n\r");
        if(!igniteOperationResult.getResult()){
            lsDataStoreMessageStringBuffer.append("Grid is not active, please active grid with actvgrid command.");
        }else{
            lsDataStoreMessageStringBuffer.append("Grid is active");
        }
        lsDataStoreMessageStringBuffer.append("\n\r");
        lsDataStoreMessageStringBuffer.append("================================================================");
        igniteOperationResult.setResultMessage(lsDataStoreMessageStringBuffer.toString());
        return igniteOperationResult;
    }

    public static void checkGridActiveStatus(Ignite nodeIgnite) throws MemoryTablesGridNotActiveException {
        if(!nodeIgnite.active()){
            throw new MemoryTablesGridNotActiveException();
        }
    }
}
