package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import java.util.*;

public interface MetaConfigItemFeatureSupportable {
    boolean addOrUpdateMetaConfigItem(String itemName,Object itemValue);
    Map<String,Object> getMetaConfigItems();
    Object getMetaConfigItem(String itemName);
    boolean deleteMetaConfigItem(String itemName);
}
