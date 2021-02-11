package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataSliceUtil {

    public static String buildSliceStructureSQL(Map<String, DataSlicePropertyType> fieldsDefinitionMap, List<String> primaryKeysList){
        if(fieldsDefinitionMap != null && fieldsDefinitionMap.size()>0) {

            StringBuffer sb = new StringBuffer();
            Set<String> fieldNameSet = fieldsDefinitionMap.keySet();

            for(String currentFieldName:fieldNameSet){
                DataSlicePropertyType currentPropertyType = fieldsDefinitionMap.get(currentFieldName);
                switch(currentPropertyType){
                    case INT: sb.append(currentFieldName +" "+"INT, ");
                    break;
                    case BYTE: sb.append(currentFieldName +" "+"TINYINT, ");
                    break;
                    case DATE: sb.append(currentFieldName +" "+"TIMESTAMP, ");
                    break;
                    case LONG: sb.append(currentFieldName +" "+"BIGINT, ");
                    break;
                    case FLOAT: sb.append(currentFieldName +" "+"REAL, ");
                    break;
                    case SHORT: sb.append(currentFieldName +" "+"SMALLINT, ");
                    break;
                    case BINARY: sb.append(currentFieldName +" "+"BINARY, ");
                    break;
                    case DOUBLE: sb.append(currentFieldName +" "+"DOUBLE, ");
                    break;
                    case STRING: sb.append(currentFieldName +" "+"VARCHAR, ");
                    break;
                    case BOOLEAN: sb.append(currentFieldName +" "+"BOOLEAN, ");
                    break;
                    case DECIMAL: sb.append(currentFieldName +" "+"DECIMAL, ");
                    break;
                    case GEOMETRY: sb.append(currentFieldName +" "+"GEOMETRY, ");
                    break;
                    case UUID: sb.append(currentFieldName +" "+"UUID, ");
                    break;
                }
            }
            sb.append("PRIMARY KEY (");
            if(primaryKeysList != null && primaryKeysList.size() >0){
                for(int i=0; i<primaryKeysList.size(); i++){
                    String currentPK = primaryKeysList.get(i);
                    sb.append(currentPK);
                    if(i < primaryKeysList.size()-1){
                        sb.append(",");
                    }
                }
            }else{
                sb.append(fieldNameSet.iterator().next());
            }
            sb.append(")");
        return sb.toString();
        }
        return null;
    }
}
