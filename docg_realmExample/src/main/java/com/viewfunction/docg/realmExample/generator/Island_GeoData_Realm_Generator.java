package com.viewfunction.docg.realmExample.generator;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class Island_GeoData_Realm_Generator {

    private static final String IndividualTreeConceptionType = "IndividualTree";
    private static final String FrutexConceptionType = "Frutex";
    private static final String FunctionalZoneConceptionType = "FunctionalZone";
    private static final String ZoneSectionConceptionType = "ZoneSection";
    private static final String SectionBlockConceptionType = "SectionBlock";

    private static final String _LocalCRSAID = "EPSG:4545"; // CGCS2000 / 3-degree Gauss-Kruger CM 108E - Projected

    public static void main(String[] args) throws CoreRealmServiceRuntimeException {
        //Please unzip islandGISData.zip before execute
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();
        generateIndividualTreeData(coreRealm);
        generateFrutexData(coreRealm);
        generateFunctionalZoneData(coreRealm);
        generateZoneSectionData(coreRealm);
        generateSectionBlockData(coreRealm);
        coreRealm.closeGlobalSession();
    }

    private static int generateIndividualTreeData(CoreRealm coreRealm) throws CoreRealmServiceRuntimeException {
        ConceptionKind _IndividualTreeConceptionType = coreRealm.getConceptionKind(IndividualTreeConceptionType);
        if(_IndividualTreeConceptionType != null){
            coreRealm.removeConceptionKind(IndividualTreeConceptionType,true);
        }
        _IndividualTreeConceptionType = coreRealm.getConceptionKind(IndividualTreeConceptionType);
        if(_IndividualTreeConceptionType == null){
            _IndividualTreeConceptionType = coreRealm.createConceptionKind(IndividualTreeConceptionType,"单木");
        }

        int addedResultCount = 0;
        String filePath ="realmExampleData/island_geoData/islandGISData/danmu1.shp";
        SimpleFeatureCollection colls = readShp(filePath,null);
        SimpleFeatureIterator iters = colls.features();
        while(iters.hasNext()){
            SimpleFeature sf = iters.next();

            Map<String,Object> entityDataMap = new HashMap<>();
            entityDataMap.put("X",sf.getAttribute("X"));
            entityDataMap.put("Y",sf.getAttribute("Y"));
            entityDataMap.put("SG",sf.getAttribute("SG"));
            entityDataMap.put("XJ",sf.getAttribute("XJ"));
            entityDataMap.put("GF",sf.getAttribute("GF"));
            entityDataMap.put("SGMJ",sf.getAttribute("SGMJ"));
            entityDataMap.put("SGTJ",sf.getAttribute("SGTJ"));
            entityDataMap.put("SZ",sf.getAttribute("SZ"));
            entityDataMap.put("ZXG",sf.getAttribute("ZXG"));
            entityDataMap.put("CSZS",sf.getAttribute("CSZS"));
            entityDataMap.put("Shape_Leng",sf.getAttribute("Shape_Leng"));
            entityDataMap.put("GNQHID",sf.getAttribute("GNQHID"));
            entityDataMap.put("DMID",sf.getAttribute("DMID"));

            String _LLGeometryContent = sf.getDefaultGeometry().toString();
            ConceptionEntityValue currentEntityValue = new ConceptionEntityValue();
            currentEntityValue.setEntityAttributesValue(entityDataMap);
            ConceptionEntity resultEntity = _IndividualTreeConceptionType.newEntity(currentEntityValue,false);
            if(resultEntity != null){
                resultEntity.addOrUpdateGeometryType(GeospatialScaleFeatureSupportable.WKTGeometryType.POINT);
                resultEntity.addOrUpdateLocalCRSAID(_LocalCRSAID);
                resultEntity.addOrUpdateLLGeometryContent(_LLGeometryContent);
                addedResultCount++;
            }
        }
        return addedResultCount;
    }

    private static int generateFrutexData(CoreRealm coreRealm) throws CoreRealmServiceRuntimeException {
        ConceptionKind _FrutexConceptionType = coreRealm.getConceptionKind(FrutexConceptionType);
        if(_FrutexConceptionType != null){
            coreRealm.removeConceptionKind(FrutexConceptionType,true);
        }
        _FrutexConceptionType = coreRealm.getConceptionKind(FrutexConceptionType);
        if(_FrutexConceptionType == null){
            _FrutexConceptionType = coreRealm.createConceptionKind(FrutexConceptionType,"灌木");
        }

        int addedResultCount = 0;
        String filePath ="realmExampleData/island_geoData/islandGISData/guanmu.shp";
        SimpleFeatureCollection colls = readShp(filePath,null);
        SimpleFeatureIterator iters = colls.features();
        while(iters.hasNext()){
            SimpleFeature sf = iters.next();

            Map<String,Object> entityDataMap = new HashMap<>();
            entityDataMap.put("TreeLocati",sf.getAttribute("TreeLocati"));
            entityDataMap.put("TreeLoca_1",sf.getAttribute("TreeLoca_1"));
            entityDataMap.put("TreeHeight",sf.getAttribute("TreeHeight"));
            entityDataMap.put("DBH",sf.getAttribute("DBH"));
            entityDataMap.put("CrownDiame",sf.getAttribute("CrownDiame"));
            entityDataMap.put("CrownArea",sf.getAttribute("CrownArea"));
            entityDataMap.put("CrownVolum",sf.getAttribute("CrownVolum"));
            entityDataMap.put("GMLX",sf.getAttribute("GMLX"));

            String _LLGeometryContent = sf.getDefaultGeometry().toString();
            ConceptionEntityValue currentEntityValue = new ConceptionEntityValue();
            currentEntityValue.setEntityAttributesValue(entityDataMap);
            ConceptionEntity resultEntity = _FrutexConceptionType.newEntity(currentEntityValue,false);
            if(resultEntity != null){
                resultEntity.addOrUpdateGeometryType(GeospatialScaleFeatureSupportable.WKTGeometryType.POINT);
                resultEntity.addOrUpdateLocalCRSAID(_LocalCRSAID);
                resultEntity.addOrUpdateLLGeometryContent(_LLGeometryContent);
                addedResultCount++;
            }
        }
        return addedResultCount;
    }

    private static int generateFunctionalZoneData(CoreRealm coreRealm) throws CoreRealmServiceRuntimeException {
        ConceptionKind _FunctionalZoneConceptionType = coreRealm.getConceptionKind(FunctionalZoneConceptionType);
        if(_FunctionalZoneConceptionType != null){
            coreRealm.removeConceptionKind(FunctionalZoneConceptionType,true);
        }
        _FunctionalZoneConceptionType = coreRealm.getConceptionKind(FunctionalZoneConceptionType);
        if(_FunctionalZoneConceptionType == null){
            _FunctionalZoneConceptionType = coreRealm.createConceptionKind(FunctionalZoneConceptionType,"功能区");
        }

        int addedResultCount = 0;
        String filePath ="realmExampleData/island_geoData/islandGISData/gongnengqu.shp";
        SimpleFeatureCollection colls = readShp(filePath,null);
        SimpleFeatureIterator iters = colls.features();
        while(iters.hasNext()){
            SimpleFeature sf = iters.next();

            Map<String,Object> entityDataMap = new HashMap<>();
            entityDataMap.put("GNQHID",sf.getAttribute("GNQHID"));
            entityDataMap.put("GNQLX",sf.getAttribute("GNQLX"));
            entityDataMap.put("GNQMS",sf.getAttribute("GNQMS"));
            entityDataMap.put("GNQMJ",sf.getAttribute("GNQMJ"));
            entityDataMap.put("GNQMC",sf.getAttribute("GNQMC"));
            entityDataMap.put("Shape_Leng",sf.getAttribute("Shape_Leng"));
            entityDataMap.put("Shape_Area",sf.getAttribute("Shape_Area"));

            String _LLGeometryContent = sf.getDefaultGeometry().toString();
            ConceptionEntityValue currentEntityValue = new ConceptionEntityValue();
            currentEntityValue.setEntityAttributesValue(entityDataMap);
            ConceptionEntity resultEntity = _FunctionalZoneConceptionType.newEntity(currentEntityValue,false);
            if(resultEntity != null){
                resultEntity.addOrUpdateGeometryType(GeospatialScaleFeatureSupportable.WKTGeometryType.MULTIPOLYGON);
                resultEntity.addOrUpdateLocalCRSAID(_LocalCRSAID);
                resultEntity.addOrUpdateLLGeometryContent(_LLGeometryContent);
                addedResultCount++;
            }
        }
        return addedResultCount;
    }

    private static int generateZoneSectionData(CoreRealm coreRealm) throws CoreRealmServiceRuntimeException {
        ConceptionKind _ZoneSectionConceptionType = coreRealm.getConceptionKind(ZoneSectionConceptionType);
        if(_ZoneSectionConceptionType != null){
            coreRealm.removeConceptionKind(ZoneSectionConceptionType,true);
        }
        _ZoneSectionConceptionType = coreRealm.getConceptionKind(ZoneSectionConceptionType);
        if(_ZoneSectionConceptionType == null){
            _ZoneSectionConceptionType = coreRealm.createConceptionKind(ZoneSectionConceptionType,"功能区");
        }

        int addedResultCount = 0;
        String filePath ="realmExampleData/island_geoData/islandGISData/bankuai.shp";
        SimpleFeatureCollection colls = readShp(filePath,null);
        SimpleFeatureIterator iters = colls.features();
        while(iters.hasNext()){
            SimpleFeature sf = iters.next();

            Map<String,Object> entityDataMap = new HashMap<>();
            entityDataMap.put("GNQBH",sf.getAttribute("GNQBH"));
            entityDataMap.put("GNQHID",sf.getAttribute("GNQHID"));
            entityDataMap.put("BKLX",sf.getAttribute("BKLX"));
            entityDataMap.put("BKMS",sf.getAttribute("BKMS"));
            entityDataMap.put("BKMJ",sf.getAttribute("BKMJ"));
            entityDataMap.put("BKMC",sf.getAttribute("BKMC"));
            entityDataMap.put("Shape_Leng",sf.getAttribute("Shape_Leng"));
            entityDataMap.put("Shape_Area",sf.getAttribute("Shape_Area"));

            String _LLGeometryContent = sf.getDefaultGeometry().toString();
            ConceptionEntityValue currentEntityValue = new ConceptionEntityValue();
            currentEntityValue.setEntityAttributesValue(entityDataMap);
            ConceptionEntity resultEntity = _ZoneSectionConceptionType.newEntity(currentEntityValue,false);
            if(resultEntity != null){
                resultEntity.addOrUpdateGeometryType(GeospatialScaleFeatureSupportable.WKTGeometryType.MULTIPOLYGON);
                resultEntity.addOrUpdateLocalCRSAID(_LocalCRSAID);
                resultEntity.addOrUpdateLLGeometryContent(_LLGeometryContent);
                addedResultCount++;
            }
        }
        return addedResultCount;
    }

    private static int generateSectionBlockData(CoreRealm coreRealm) throws CoreRealmServiceRuntimeException {
        ConceptionKind _SectionBlockConceptionType = coreRealm.getConceptionKind(SectionBlockConceptionType);
        if(_SectionBlockConceptionType != null){
            coreRealm.removeConceptionKind(SectionBlockConceptionType,true);
        }
        _SectionBlockConceptionType = coreRealm.getConceptionKind(SectionBlockConceptionType);
        if(_SectionBlockConceptionType == null){
            _SectionBlockConceptionType = coreRealm.createConceptionKind(SectionBlockConceptionType,"功能区");
        }

        int addedResultCount = 0;
        String filePath ="realmExampleData/island_geoData/islandGISData/xiaoban.shp";
        SimpleFeatureCollection colls = readShp(filePath,null);
        SimpleFeatureIterator iters = colls.features();
        while(iters.hasNext()){
            SimpleFeature sf = iters.next();

            Map<String,Object> entityDataMap = new HashMap<>();
            entityDataMap.put("XBLX",sf.getAttribute("XBLX"));
            entityDataMap.put("XBMS",sf.getAttribute("XBMS"));
            entityDataMap.put("XFLX",sf.getAttribute("XFLX"));
            entityDataMap.put("XBMJ",sf.getAttribute("XBMJ"));
            entityDataMap.put("YSSZ",sf.getAttribute("YSSZ"));
            entityDataMap.put("SZZC",sf.getAttribute("SZZC"));
            entityDataMap.put("YBD",sf.getAttribute("YBD"));
            entityDataMap.put("STLX",sf.getAttribute("STLX"));
            entityDataMap.put("BKMC",sf.getAttribute("BKMC"));
            entityDataMap.put("Xmin",sf.getAttribute("Xmin"));
            entityDataMap.put("Ymax",sf.getAttribute("Ymax"));
            entityDataMap.put("BH",sf.getAttribute("BH"));
            entityDataMap.put("BKBH",sf.getAttribute("BKBH"));
            entityDataMap.put("XBMC",sf.getAttribute("XBMC"));
            entityDataMap.put("GNQHID",sf.getAttribute("GNQHID"));
            entityDataMap.put("LXBH",sf.getAttribute("LXBH"));
            entityDataMap.put("Shape_Leng",sf.getAttribute("Shape_Leng"));
            entityDataMap.put("Shape_Area",sf.getAttribute("Shape_Area"));

            String _LLGeometryContent = sf.getDefaultGeometry().toString();
            ConceptionEntityValue currentEntityValue = new ConceptionEntityValue();
            currentEntityValue.setEntityAttributesValue(entityDataMap);
            ConceptionEntity resultEntity = _SectionBlockConceptionType.newEntity(currentEntityValue,false);
            if(resultEntity != null){
                resultEntity.addOrUpdateGeometryType(GeospatialScaleFeatureSupportable.WKTGeometryType.MULTIPOLYGON);
                resultEntity.addOrUpdateLocalCRSAID(_LocalCRSAID);
                resultEntity.addOrUpdateLLGeometryContent(_LLGeometryContent);
                addedResultCount++;
            }
        }
        return addedResultCount;
    }

    private static SimpleFeatureCollection  readShp(String path , Filter filter){
        SimpleFeatureSource featureSource = readStoreByShp(path);
        if(featureSource == null){
            return null;
        }
        try {
            return filter != null ? featureSource.getFeatures(filter) : featureSource.getFeatures() ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null ;
    }

    private static  SimpleFeatureSource readStoreByShp(String path ){
        File file = new File(path);
        FileDataStore store;
        SimpleFeatureSource featureSource = null;
        try {
            store = FileDataStoreFinder.getDataStore(file);
            ((ShapefileDataStore) store).setCharset(Charset.forName("GBK"));
            featureSource = store.getFeatureSource();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return featureSource;
    }
}
