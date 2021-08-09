package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.PathTravelable;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface Neo4JPathTravelable extends PathTravelable,Neo4JKeyResourcesRetrievable {

    Logger logger = LoggerFactory.getLogger(Neo4JPathTravelable.class);

    default public List<Path> expandPath(int minJump,int maxJump){


        //https://neo4j.com/labs/apoc/4.1/graph-querying/expand-paths/#path-expander-paths-procedure-overview
        String cypherProcedureString="MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                "CALL apoc.path.expand(n, \"DOCG_GS_SpatialConnect\", null, 1, 2)\n" +
                "YIELD path\n" +
                "RETURN path, length(path) AS hops\n" +
                "ORDER BY hops;";
        return null;
    }


}
