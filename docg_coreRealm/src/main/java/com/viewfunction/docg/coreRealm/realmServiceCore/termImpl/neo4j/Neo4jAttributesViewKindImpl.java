package com.viewfunction.docg.coreRealm.realmServiceCore.termImpl.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Neo4jAttributesViewKindImpl implements AttributesViewKind {

    private static Logger logger = LoggerFactory.getLogger(Neo4JConceptionKindImpl.class);
    private String coreRealmName;
    private String attributesViewKindName;
    private String attributesViewKindDesc;
    private String attributesViewKindUID;
    private AttributesViewKindDataForm attributesViewKindDataForm;

    public Neo4jAttributesViewKindImpl(String coreRealmName,String attributesViewKindName,String attributesViewKindDesc,AttributesViewKindDataForm attributesViewKindDataForm,String attributesViewKindUID){
        this.coreRealmName = coreRealmName;
        this.attributesViewKindName = attributesViewKindName;
        this.attributesViewKindDesc = attributesViewKindDesc;
        this.attributesViewKindUID = attributesViewKindUID;
        this.attributesViewKindDataForm = attributesViewKindDataForm != null ? attributesViewKindDataForm : AttributesViewKindDataForm.SINGLE_VALUE;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public String getAttributesViewKindUID() {
        return attributesViewKindUID;
    }

    @Override
    public String getAttributesViewKindName() {
        return attributesViewKindName;
    }

    @Override
    public String getAttributesViewKindDesc() {
        return attributesViewKindDesc;
    }

    @Override
    public boolean isCollectionAttributesViewKind() {
        boolean isCollectionAttributesViewKind = false;
        switch (attributesViewKindDataForm){
            case SINGLE_VALUE: isCollectionAttributesViewKind = false;
                break;
            case LIST_VALUE: isCollectionAttributesViewKind = true;
                break;
            case RELATED_VALUE: isCollectionAttributesViewKind = true;
                break;
            case EXTERNAL_VALUE: isCollectionAttributesViewKind = true;
        }
        return isCollectionAttributesViewKind;
    }

    @Override
    public AttributesViewKindDataForm getAttributesViewKindDataForm() {
        return attributesViewKindDataForm;
    }

    @Override
    public boolean addAttributeKind(String attributeKindUID) {
        return false;
    }

    @Override
    public boolean addAttributeKind(AttributeKind attributeKind) {
        return false;
    }

    @Override
    public boolean removeAttributeKind(String attributeKindUID) {
        return false;
    }

    @Override
    public List<AttributeKind> getContainsAttributeKinds() {
        return null;
    }

    @Override
    public List<ConceptionKind> getContainerConceptionKinds() {
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
