package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.InheritanceTree;

import java.util.List;

public interface Classification extends MetaAttributeFeatureSupportable, AttributesMeasurable{

    public String getClassificationName();
    public String getClassificationDesc();
    public boolean isRootClassification();
    public Classification getParentClassification();
    public List<Classification> getChildClassifications();
    public InheritanceTree<Classification> getOffspringClassifications();

    public boolean attachChildClassification(String childClassificationName) throws CoreRealmServiceRuntimeException;
    public boolean detachChildClassification(String childClassificationName) throws CoreRealmServiceRuntimeException;
    public Classification createChildClassification(String classificationName,String classificationDesc);
    public boolean removeChildClassification(String classificationName) throws CoreRealmServiceRuntimeException;





}
