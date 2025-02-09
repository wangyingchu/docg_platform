package externalDataExchangeTestCase;

import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.relationDB.RelationDBOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.relationDB.RelationDBPropertyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RationalDataBaseOperationTest {

    public static void main(String[] args) {

        String dbName = "demo";
        String tableName = "mytable";

        Map<String, RelationDBPropertyType> propertiesDataTypeMap = new HashMap<>();
        propertiesDataTypeMap.put("k1", RelationDBPropertyType.SHORT);
        propertiesDataTypeMap.put("k2", RelationDBPropertyType.DOUBLE);
        propertiesDataTypeMap.put("k3", RelationDBPropertyType.STRING);
        propertiesDataTypeMap.put("k4", RelationDBPropertyType.INT);

        List<Map<String,Object>> batchData = new ArrayList<>();

        for(int i=0;i<25432;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("k1",Short.valueOf("12"));
            map.put("k2",1.01+i);
            map.put("k3","k3");
            map.put("k4",1000+i);
            batchData.add(map);
        }
        System.out.println(new java.util.Date());

        RelationDBOperationUtil.insertBatchData(dbName,tableName, propertiesDataTypeMap, batchData);
        System.out.println(new java.util.Date());

    }
}
