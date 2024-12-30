package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class ConceptionKindDataCapabilityInfo {

    private boolean containsGeospatialAttribute = false;
    private boolean attachedToGeospatialScaleEvent = false;
    private boolean attachedToTimeScaleEvent = false;

    public ConceptionKindDataCapabilityInfo(){}

    public ConceptionKindDataCapabilityInfo(boolean containsGeospatialAttribute,
                                            boolean attachedToGeospatialScaleEvent,
                                            boolean attachedToTimeScaleEvent){
        this.setContainsGeospatialAttribute(containsGeospatialAttribute);
        this.setAttachedToGeospatialScaleEvent(attachedToGeospatialScaleEvent);
        this.setAttachedToTimeScaleEvent(attachedToTimeScaleEvent);
    }

    public boolean containsGeospatialAttribute() {
        return containsGeospatialAttribute;
    }

    public void setContainsGeospatialAttribute(boolean containsGeospatialAttribute) {
        this.containsGeospatialAttribute = containsGeospatialAttribute;
    }

    public boolean attachedToGeospatialScaleEvent() {
        return attachedToGeospatialScaleEvent;
    }

    public void setAttachedToGeospatialScaleEvent(boolean attachedToGeospatialScaleEvent) {
        this.attachedToGeospatialScaleEvent = attachedToGeospatialScaleEvent;
    }

    public boolean attachedToTimeScaleEvent() {
        return attachedToTimeScaleEvent;
    }

    public void setAttachedToTimeScaleEvent(boolean attachedToTimeScaleEvent) {
        this.attachedToTimeScaleEvent = attachedToTimeScaleEvent;
    }
}
