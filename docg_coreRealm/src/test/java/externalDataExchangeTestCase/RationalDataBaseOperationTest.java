package externalDataExchangeTestCase;

import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.rationalDB.RationalDBOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.rationalDB.RationalDBPropertyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RationalDataBaseOperationTest {

    public static void main(String[] args) {

        String dbName = "demo";
        String tableName = "mytable";

        Map<String, RationalDBPropertyType> propertiesDataTypeMap = new HashMap<>();
        propertiesDataTypeMap.put("k1",RationalDBPropertyType.SHORT);
        propertiesDataTypeMap.put("k2",RationalDBPropertyType.DOUBLE);
        propertiesDataTypeMap.put("k3",RationalDBPropertyType.STRING);
        propertiesDataTypeMap.put("k4",RationalDBPropertyType.INT);

        List<Map<String,Object>> batchData = new ArrayList<>();

        for(int i=0;i<10000;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("k1",Short.valueOf("12"));
            map.put("k2",1.01+i);
            map.put("k3","k3");
            map.put("k4",1000+i);
            batchData.add(map);
        }
        System.out.println(new java.util.Date());

        RationalDBOperationUtil.insertBatchData(dbName,tableName, propertiesDataTypeMap, batchData);
        System.out.println(new java.util.Date());

    }
}
