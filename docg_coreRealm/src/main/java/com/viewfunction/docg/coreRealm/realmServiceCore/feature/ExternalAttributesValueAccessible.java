package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityExternalAttributesValueRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.util.Set;

public interface ExternalAttributesValueAccessible {

    public Set<AttributesViewKind> getAvailableExternalValueAttributesViewKinds();

    public ConceptionEntityExternalAttributesValueRetrieveResult getEntityExternalAttributesValues(
            AttributesViewKind attributesViewKind, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException;;

    public Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind,
                                                    AttributesParameters attributesParameters) throws CoreRealmServiceEntityExploreException;

}
