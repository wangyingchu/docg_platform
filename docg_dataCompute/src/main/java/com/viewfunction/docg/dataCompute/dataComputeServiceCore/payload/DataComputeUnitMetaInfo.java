package com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload;

import java.util.Collection;

public class DataComputeUnitMetaInfo {

    private String unitID;
    private String unitType;
    private Collection<String> unitHostNames;
    private Collection<String> unitIPAddresses;
    private boolean isClientUnit;

    public DataComputeUnitMetaInfo(String unitID, String unitType, Collection<String> unitHostNames, Collection<String> unitIPAddresses, boolean isClientUnit){
        this.unitID = unitID;
        this.unitType = unitType;
        this.unitHostNames = unitHostNames;
        this.unitIPAddresses = unitIPAddresses;
        this.isClientUnit = isClientUnit;
    }

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public Collection<String> getUnitHostNames() {
        return unitHostNames;
    }

    public void setUnitHostNames(Collection<String> unitHostNames) {
        this.unitHostNames = unitHostNames;
    }

    public Collection<String> getUnitIPAddresses() {
        return unitIPAddresses;
    }

    public void setUnitIPAddresses(Collection<String> unitIPAddresses) {
        this.unitIPAddresses = unitIPAddresses;
    }

    public boolean getIsClientUnit() {
        return isClientUnit;
    }

    public void setClientUnit(boolean clientUnit) {
        isClientUnit = clientUnit;
    }
}
