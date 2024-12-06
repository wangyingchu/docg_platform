package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ResponseDataset implements Serializable {

    private ArrayList<HashMap<String,Object>> dataList;
    private ArrayList<HashMap<String,Object>> propertiesInfoList;

    public ResponseDataset(HashMap<String,String> propertiesInfo,ArrayList<HashMap<String,Object>> dataList){
        this.setDataList(dataList);
    }

    public ResponseDataset(ArrayList<HashMap<String,Object>> propertiesInfoList,ArrayList<HashMap<String,Object>> dataList){
        this.setDataList(dataList);
        this.setPropertiesInfoList(propertiesInfoList);
    }

    public ArrayList<HashMap<String,Object>> getDataList() {
        return dataList;
    }

    private void setDataList(ArrayList<HashMap<String,Object>> dataList) {
        this.dataList = dataList;
    }

    public HashMap<String, String> getPropertiesInfo() {
        Object propertiesInfoObject = propertiesInfoList.get(0);
        return (HashMap<String, String>)propertiesInfoObject;
    }

    public void clearDataList(){
        this.dataList.clear();
    }

    public void setPropertiesInfoList(ArrayList<HashMap<String, Object>> propertiesInfoList) {
        this.propertiesInfoList = propertiesInfoList;
    }
}
