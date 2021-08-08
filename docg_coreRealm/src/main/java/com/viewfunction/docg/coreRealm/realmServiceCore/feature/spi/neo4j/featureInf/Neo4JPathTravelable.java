package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.PathTravelable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Neo4JPathTravelable extends PathTravelable,Neo4JKeyResourcesRetrievable {

    Logger logger = LoggerFactory.getLogger(Neo4JPathTravelable.class);
}
