package com.viewfunction.docg.coreRealm.realmServiceCore.term;

public interface GeospatialRegion {

    //中国行政区划
    //省级行政区(Province) 省、直辖市、自治区、特别行政区
    //地级行政区(Prefecture) 地级市、地区、自治州、盟
    //县级行政区(County) 市辖区、县级市、县、自治县等
    //乡级行政区(Township) 街道、镇、乡、民族乡
    //村级行政区(Village) 村

    public enum GeospatialScaleGrade {PLANET,CONTINENT,COUNTRY_REGION,PROVINCE,PREFECTURE,COUNTY,TOWNSHIP,VILLAGE}
}
