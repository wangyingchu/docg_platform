package com.viewfunction.docg.realmExample.generator;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.NullValueFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChemicalProducts_Realm_Generator {

    private static final String CompoundConceptionType = "Compound";
    private static final String CompoundId = "id";
    private static final String CompoundName = "name";
    private static final String CompoundCASNumber = "CASNumber";

    private static final String IngredientConceptionType = "Ingredient";
    private static final String IngredientId = "id";
    private static final String IngredientName = "name";
    private static final String IngredientCategory = "category";

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind _CompoundConceptionKind = coreRealm.getConceptionKind(CompoundConceptionType);
        if(_CompoundConceptionKind != null){
            coreRealm.removeConceptionKind(CompoundConceptionType,true);
        }
        _CompoundConceptionKind = coreRealm.getConceptionKind(CompoundConceptionType);
        if(_CompoundConceptionKind == null){
            _CompoundConceptionKind = coreRealm.createConceptionKind(CompoundConceptionType,"化合物");
        }

        ConceptionKind _IngredientConceptionKind = coreRealm.getConceptionKind(IngredientConceptionType);
        if(_IngredientConceptionKind != null){
            coreRealm.removeConceptionKind(IngredientConceptionType,true);
        }
        _IngredientConceptionKind = coreRealm.getConceptionKind(IngredientConceptionType);
        if(_IngredientConceptionKind == null){
            _IngredientConceptionKind = coreRealm.createConceptionKind(IngredientConceptionType,"原料");
        }

        List<ConceptionEntityValue> compoundEntityValueList = new ArrayList<>();
        File file = new File("realmExampleData/ingr_comp/comp_info.tsv");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {

                String currentLine = !tempStr.startsWith("#")? tempStr : null;
                if(currentLine != null){
                    String[] dataItems =  currentLine.split("\t");
                    String compoundConceptionId = dataItems[0];
                    String compoundConceptionName = dataItems[1];
                    String compoundConceptionCASNumber = dataItems[2];

                    Map<String,Object> newEntityValueMap = new HashMap<>();
                    newEntityValueMap.put(CompoundId,compoundConceptionId);
                    newEntityValueMap.put(CompoundName,compoundConceptionName);
                    newEntityValueMap.put(CompoundCASNumber,compoundConceptionCASNumber);

                    ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                    compoundEntityValueList.add(conceptionEntityValue);
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
        _CompoundConceptionKind.newEntities(compoundEntityValueList,false);

        List<ConceptionEntityValue> ingredientEntityValueList = new ArrayList<>();
        File file2 = new File("realmExampleData/ingr_comp/ingr_info.tsv");
        BufferedReader reader2 = null;
        try {
            reader2 = new BufferedReader(new FileReader(file2));
            String tempStr;
            while ((tempStr = reader2.readLine()) != null) {

                String currentLine = !tempStr.startsWith("#")? tempStr : null;
                if(currentLine != null){
                    String[] dataItems =  currentLine.split("\t");
                    String ingredientConceptionId = dataItems[0];
                    String ingredientConceptionName = dataItems[1];
                    String ingredientConceptionCategory = dataItems[2];

                    Map<String,Object> newEntityValueMap = new HashMap<>();
                    newEntityValueMap.put(IngredientId,ingredientConceptionId);
                    newEntityValueMap.put(IngredientName,ingredientConceptionName);
                    newEntityValueMap.put(IngredientCategory,ingredientConceptionCategory);

                    ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                    ingredientEntityValueList.add(conceptionEntityValue);
                }
            }
            reader2.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        _IngredientConceptionKind.newEntities(ingredientEntityValueList,false);

        coreRealm.openGlobalSession();

        ConceptionKind _CompoundConceptionKind1 = coreRealm.getConceptionKind(CompoundConceptionType);
        QueryParameters queryParameters2 = new QueryParameters();
        NullValueFilteringItem defaultFilterItem = new NullValueFilteringItem(CompoundId);
        defaultFilterItem.reverseCondition();
        queryParameters2.setDefaultFilteringItem(defaultFilterItem);
        ConceptionEntitiesRetrieveResult _CompoundResult= _CompoundConceptionKind1.getEntities(queryParameters2);
        Map<String,String> idUIDMapping_Compound = new HashMap();
        for(ConceptionEntity currentCompoundConceptionEntity : _CompoundResult.getConceptionEntities()){
            String uid = currentCompoundConceptionEntity.getConceptionEntityUID();
            String idValue = currentCompoundConceptionEntity.getAttribute(CompoundId).getAttributeValue().toString();
            idUIDMapping_Compound.put(idValue,uid);
        }

        RelationAttachInfo relationAttachInfo = new RelationAttachInfo();
        relationAttachInfo.setRelationKind("belongsToCategory");
        relationAttachInfo.setRelationDirection(RelationDirection.FROM);
        List<String> existClassificationList = new ArrayList<>();

        ConceptionKind _IngredientConceptionKind1 = coreRealm.getConceptionKind(IngredientConceptionType);
        QueryParameters queryParameters3 = new QueryParameters();
        NullValueFilteringItem defaultFilterItem2 = new NullValueFilteringItem(IngredientId);
        defaultFilterItem2.reverseCondition();
        queryParameters3.setDefaultFilteringItem(defaultFilterItem2);
        ConceptionEntitiesRetrieveResult _IngredientResult= _IngredientConceptionKind1.getEntities(queryParameters3);
        Map<String,String> idUIDMapping_Ingredient = new HashMap();
        for(ConceptionEntity currentIngredientConceptionEntity : _IngredientResult.getConceptionEntities()){
            String uid = currentIngredientConceptionEntity.getConceptionEntityUID();
            String idValue = currentIngredientConceptionEntity.getAttribute(IngredientId).getAttributeValue().toString();
            idUIDMapping_Ingredient.put(idValue,uid);

            String categoryName = currentIngredientConceptionEntity.getAttribute(IngredientCategory).getAttributeValue().toString().trim();
            if (!existClassificationList.contains(categoryName)) {
                if (coreRealm.getClassification(categoryName) == null) {
                    coreRealm.createClassification(categoryName, "");
                }
                existClassificationList.add(categoryName);
            }
            currentIngredientConceptionEntity.attachClassification(relationAttachInfo,categoryName);
        }

        File file3 = new File("realmExampleData/ingr_comp/ingr_comp.tsv");
        BufferedReader reader3 = null;
        try {
            reader3 = new BufferedReader(new FileReader(file3));
            String tempStr;
            while ((tempStr = reader3.readLine()) != null) {

                String currentLine = !tempStr.startsWith("#")? tempStr : null;
                if(currentLine != null){
                    String[] dataItems =  currentLine.split("\t");
                    String ingredientId = dataItems[0].trim();
                    String compoundId = dataItems[1].trim();
                    linkItem(_IngredientConceptionKind1,idUIDMapping_Ingredient,idUIDMapping_Compound,ingredientId,compoundId);
                }
            }
            reader3.close();
        } catch (IOException | CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        } finally {
            if (reader3 != null) {
                try {
                    reader3.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        coreRealm.closeGlobalSession();
    }

    private static void linkItem(ConceptionKind _IngredientConceptionKind,Map<String,String> idUIDMapping_Ingredient,Map<String,String> idUIDMapping_Compound,
                                 String ingredientId,String compoundId) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        String ingredientEntityUID = idUIDMapping_Ingredient.get(ingredientId);
        ConceptionEntity _IngredientEntity = _IngredientConceptionKind.getEntityByUID(ingredientEntityUID);
        String _CompoundEntityUID = idUIDMapping_Compound.get(compoundId);
        if(_CompoundEntityUID != null){
            _IngredientEntity.attachFromRelation(_CompoundEntityUID,"isUsedIn",null,false);
        }
    }
}
