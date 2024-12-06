package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ResponseDataset implements Serializable {

    private ArrayList<HashMap<String,Object>> dataList;
    private HashMap<String,String> propertiesInfo;

    private ArrayList<HashMap<String,Object>> propertiesInfoList;

    public ResponseDataset(HashMap<String,String> propertiesInfo,ArrayList<HashMap<String,Object>> dataList){
        this.setDataList(dataList);
        //this.setPropertiesInfo(propertiesInfo);
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

    //private void setPropertiesInfo(HashMap<String, String> propertiesInfo) {
    //    this.propertiesInfo = propertiesInfo;
    //}

    public void clearDataList(){
        this.dataList.clear();
    }

    public ArrayList<HashMap<String, Object>> getPropertiesInfoList() {
        return propertiesInfoList;
    }

    public void setPropertiesInfoList(ArrayList<HashMap<String, Object>> propertiesInfoList) {
        this.propertiesInfoList = propertiesInfoList;
    }
}
