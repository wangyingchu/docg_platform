package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleFeatureSupportable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Neo4JGeospatialScaleFeatureSupportable extends GeospatialScaleFeatureSupportable,Neo4JKeyResourcesRetrievable{

    static Logger logger = LoggerFactory.getLogger(Neo4JGeospatialScaleFeatureSupportable.class);

    public default WKTGeometryType getGeometryType() {
        return null;
    }

    public default boolean addOrUpdateGeometryType(WKTGeometryType wKTGeometryType){return false;}

    public default String getGlobalCRSAID(){return null;}

    public default boolean addOrUpdateGlobalCRSAID(String crsAID){return false;}

    public default String getCountryCRSAID(){return null;}

    public default boolean addOrUpdateCountryCRSAID(String crsAID){return false;}

    public default String getLocalCRSAID(){return null;}

    public default boolean addOrUpdateLocalCRSAID(String crsAID){return false;}

    public default String getGLGeometryContent(){return null;}

    public default boolean addOrUpdateGLGeometryContent(String wKTContent){return false;}

    public default String getCLGeometryContent(){return null;}

    public default boolean addOrUpdateCLGeometryContent(String wKTContent){return false;}

    public default String getLLGeometryContent(){return null;}

    public default boolean addOrUpdateLLGeometryContent(String wKTContent){return false;}

}
