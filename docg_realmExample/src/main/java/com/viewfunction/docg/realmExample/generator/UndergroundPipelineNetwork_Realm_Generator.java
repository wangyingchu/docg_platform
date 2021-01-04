package com.viewfunction.docg.realmExample.generator;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UndergroundPipelineNetwork_Realm_Generator {

    private static final String PipelineConceptionType = "Pipeline";
    private static final String PipePointConceptionType = "PipePoint";

    private static final String PipePointStandardCode = "standardCode";
    private static final String PipePointNetworkType = "networkType";
    private static final String PipePointGroundElevation = "groundElevation";
    private static final String PipePointName = "name";
    private static final String PipePointLocationRoad = "locationRoad";
    private static final String PipePointPointID = "pointID";
    private static final String PipePointElementType = "elementType";

    private static final String PipeTubulationConceptionType = "PipeTubulation";




    //standardCode,startPortionTopElevation,networkType,endPortionBuriedDepth,endPortionTopElevation,tubulationID,crossSection,NAME,startPortionBuriedDepth,locationRoad,startPointID,material,elementType,tubulationLength,endPointID








    public static void main(String[] args) throws CoreRealmServiceRuntimeException {

        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind _PipelineConceptionKind = coreRealm.getConceptionKind(PipelineConceptionType);
        if(_PipelineConceptionKind != null){
            coreRealm.removeConceptionKind(PipelineConceptionType,true);
        }
        _PipelineConceptionKind = coreRealm.getConceptionKind(PipelineConceptionType);
        if(_PipelineConceptionKind == null){
            _PipelineConceptionKind = coreRealm.createConceptionKind(PipelineConceptionType,"地下管线");
        }

        ConceptionKind _PipePointConceptionKind = coreRealm.getConceptionKind(PipePointConceptionType);
        if(_PipePointConceptionKind != null){
            coreRealm.removeConceptionKind(PipePointConceptionType,true);
        }
        _PipePointConceptionKind = coreRealm.getConceptionKind(PipePointConceptionType);
        if(_PipePointConceptionKind == null){
            _PipePointConceptionKind = coreRealm.createConceptionKind(PipePointConceptionType,"管点");
        }

        ConceptionKind _PipeTubulationConceptionKind = coreRealm.getConceptionKind(PipeTubulationConceptionType);
        if(_PipeTubulationConceptionKind != null){
            coreRealm.removeConceptionKind(PipeTubulationConceptionType,true);
        }
        _PipeTubulationConceptionKind = coreRealm.getConceptionKind(PipeTubulationConceptionType);
        if(_PipePointConceptionKind == null){
            _PipeTubulationConceptionKind = coreRealm.createConceptionKind(PipeTubulationConceptionType,"管段");
        }


        List<ConceptionEntityValue> pipePointEntityValueList = new ArrayList<>();
        File file = new File("realmExampleData/underground_pipelinenetwork/UGPN_Point.csv");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                String currentLine = !tempStr.startsWith("standardCode")? tempStr : null;
                if(currentLine != null){
                    String[] dataItems =  currentLine.split(",");
                    String standardCode = dataItems[0];
                    String networkType = dataItems[1];
                    String groundElevation = dataItems[2];
                    String name = dataItems[3];
                    String locationRoad = dataItems[4];
                    String pointID = dataItems[5];
                    String elementType = dataItems[6];

                    System.out.println(currentLine);
                    Map<String,Object> newEntityValueMap = new HashMap<>();
                    newEntityValueMap.put(PipePointStandardCode,standardCode);
                    newEntityValueMap.put(PipePointNetworkType,networkType);
                    if(groundElevation.equals("")){
                        newEntityValueMap.put(PipePointGroundElevation,Float.valueOf(0.0f));
                    }else{
                        newEntityValueMap.put(PipePointGroundElevation,Float.parseFloat(groundElevation));
                    }
                    newEntityValueMap.put(PipePointName,name);
                    newEntityValueMap.put(PipePointLocationRoad,locationRoad);
                    newEntityValueMap.put(PipePointPointID,pointID);
                    newEntityValueMap.put(PipePointElementType,elementType);

                    ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                    pipePointEntityValueList.add(conceptionEntityValue);
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        _PipePointConceptionKind.newEntities(pipePointEntityValueList,false);

    }
}
