package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.ResourceNetworkNotActiveException;
import org.apache.ignite.Ignite;

public class ResourceNodeIgniteOperationUtil {

    public static OperationResultVO isGridActive(Ignite nodeIgnite){
        OperationResultVO operationResultVO=new OperationResultVO();
        operationResultVO.setResult(nodeIgnite.active());
        StringBuffer lsDataStoreMessageStringBuffer=new StringBuffer();
        lsDataStoreMessageStringBuffer.append("\n\r");
        lsDataStoreMessageStringBuffer.append("================================================================");
        lsDataStoreMessageStringBuffer.append("\n\r");
        if(!operationResultVO.getResult()){
            lsDataStoreMessageStringBuffer.append("Grid is not active, please active grid with actvgrid command.");
        }else{
            lsDataStoreMessageStringBuffer.append("Grid is active");
        }
        lsDataStoreMessageStringBuffer.append("\n\r");
        lsDataStoreMessageStringBuffer.append("================================================================");
        operationResultVO.setResultMessage(lsDataStoreMessageStringBuffer.toString());
        return operationResultVO;
    }

    public static void checkGridActiveStatus(Ignite nodeIgnite) throws ResourceNetworkNotActiveException {
        if(!nodeIgnite.active()){
            throw new ResourceNetworkNotActiveException();
        }
    }
}
