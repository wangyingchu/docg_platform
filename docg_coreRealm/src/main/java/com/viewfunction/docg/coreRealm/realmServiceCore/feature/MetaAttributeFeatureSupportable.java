package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import java.util.Date;

public interface MetaAttributeFeatureSupportable {
    public Date getCreateDateTime();
    public Date getLastModifyDateTime();
    public String getCreatorId();
    public String getDataOrigin();
    public boolean updateModifyDate();
    public boolean updateCreatorId();
    public boolean updateDataOrigin();
}
