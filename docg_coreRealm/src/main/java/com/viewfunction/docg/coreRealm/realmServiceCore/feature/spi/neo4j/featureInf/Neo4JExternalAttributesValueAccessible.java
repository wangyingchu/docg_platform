package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ExternalAttributesValueAccessible;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityExternalAttributesValueRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.util.Set;

public interface Neo4JExternalAttributesValueAccessible extends ExternalAttributesValueAccessible,Neo4JKeyResourcesRetrievable{

    public default Set<AttributesViewKind> getAvailableAttributesViewKinds(){return null;}

    public default ConceptionEntityExternalAttributesValueRetrieveResult getEntityExternalAttributesValues(
            AttributesViewKind attributesViewKind, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException{return null;}

    public default Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters){return null;}
}
