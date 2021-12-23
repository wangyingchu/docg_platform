package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResponseDataset implements Serializable {

    private ArrayList<HashMap<String,Object>> dataList;
    private Map<String,String> propertiesInfo;

    public ResponseDataset(Map<String,String> propertiesInfo,ArrayList<HashMap<String,Object>> dataList){
        this.setDataList(dataList);
        this.setPropertiesInfo(propertiesInfo);
    }

    public ArrayList<HashMap<String,Object>> getDataList() {
        return dataList;
    }

    private void setDataList(ArrayList<HashMap<String,Object>> dataList) {
        this.dataList = dataList;
    }

    public Map<String, String> getPropertiesInfo() {
        return propertiesInfo;
    }

    private void setPropertiesInfo(Map<String, String> propertiesInfo) {
        this.propertiesInfo = propertiesInfo;
    }

    public void clearDataList(){
        this.dataList.clear();
    }
}
