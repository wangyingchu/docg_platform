package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import java.util.Date;

public interface MetaAttributeFeatureSupportable {
    Date getCreateDateTime();
    Date getLastModifyDateTime();
    String getCreatorId();
    String getDataOrigin();
    boolean updateLastModifyDateTime();
    boolean updateCreatorId(String creatorId);
    boolean updateDataOrigin(String dataOrigin);
}
