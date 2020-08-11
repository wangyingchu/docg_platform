package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import java.util.Map;

public interface MetaConfigItemFeatureSupportable {
    public boolean addOrUpdateMetaConfigItem(String itemName,Object itemValue);
    public Map<String,Object> getMetaConfigItems();
    public Object getMetaConfigItem(String itemName);
    public boolean deleteMetaConfigItem(String itemName);
}
