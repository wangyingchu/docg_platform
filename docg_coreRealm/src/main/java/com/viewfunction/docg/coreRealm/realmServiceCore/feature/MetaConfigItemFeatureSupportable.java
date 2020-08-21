package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import java.util.Map;

public interface MetaConfigItemFeatureSupportable {
    default boolean addOrUpdateMetaConfigItem(String itemName,Object itemValue) {
    return false;
    }
    default Map<String,Object> getMetaConfigItems(){return null;}
    default Object getMetaConfigItem(String itemName){return null;}
    default boolean deleteMetaConfigItem(String itemName){return false;}
}
