package com.viewfunction.docg.knowledgeManage.applicationService.common;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;

import java.util.HashMap;
import java.util.Map;

public class BaseRealmEntity {

    private String entityUID;
    private CoreRealm coreRealm;
    private Map<String, Object> attributeMap = new HashMap<>();

    public BaseRealmEntity() {}

    public BaseRealmEntity(String entityUIDID, CoreRealm coreRealm) {
        this.setEntityUID(entityUIDID);
        this.setCoreRealm(coreRealm);
    }

    public void set(String propertyName,Object objectValue) {
        if(attributeMap == null) {
            attributeMap = new HashMap<>();
        }
        attributeMap.put(propertyName,objectValue);
    }

    public Object get(String property) {
        if(attributeMap!=null) {
            if (attributeMap.containsKey(property)) {
                return attributeMap.get(property);
            }
        }
        return null;

        /*
        try {
            Object object = getFact().getProperty(property).getPropertyValue();
            map.put(property, object);
            return object;
        } catch (Exception e) {
        //            e.printStackTrace();
        }

        map.put(property, null);
        return null;

         */
    }

    public void link(BaseRealmEntity destBaseRealmEntity, String relationTypeName,Map<String,Object> relationData) {
        /*
        try {
            this.getFact().addToRelation(dastfact.getFact(),relationTypeName,false);
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }
         */
    }

    public String getEntityUID() {
        return entityUID;
    }

    public void setEntityUID(String entityUID) {
        this.entityUID = entityUID;
    }

    public CoreRealm getCoreRealm() {
        return coreRealm;
    }

    public void setCoreRealm(CoreRealm coreRealm) {
        this.coreRealm = coreRealm;
    }
}
