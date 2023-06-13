package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public interface TemporalScaleCalculable {

    /**
     * 时间类计算使用的时间属性的颗粒度
     * Date : 精度到天的日期
     * Time : 精度到秒的时间
     * Datetime :  精度到天的日期 + 精度到秒的时间
     * Timestamp : 精度到毫秒的时间戳
     */
    public enum TemporalScaleLevel {Date,Time,Datetime,Timestamp}

    public enum TemporalValueFormat {
        Format1("yyyy-MM-dd HH:mm:ss"),
        Format2("yyyyMMdd"),
        Format3("yyyy-MM-dd"),
        Format4("yyyy-MM-dd HH:mm:ss"),
        Format5("yyyy/mm/dd hh:mm:ss"),
        Format6("yyyy/m/d h:mm:ss"),
        Format7("yyyy/m/dd h:mm:ss"),
        Format8("yyyymmdd"),
        Format9("yyyy/mm/dd"),
        Format10("yyyy/m/d");

        private static final Map<String, TemporalValueFormat> nameToValueMap;

        static {
            nameToValueMap = new HashMap<String, TemporalValueFormat>();
            for (TemporalValueFormat temporalValueFormat : EnumSet.allOf(TemporalValueFormat.class)) {
                nameToValueMap.put(temporalValueFormat.valueFormatString, temporalValueFormat);
            }
        }

        private final String valueFormatString;

        private TemporalValueFormat(String valueFormatString) {
            this.valueFormatString = valueFormatString;
        }

        public String getValueFormatString() {
            return valueFormatString;
        }

        public static TemporalValueFormat fromValueFormatString(String valueFormatString) {
            return nameToValueMap.get(valueFormatString);
        }

        public String toString() {
            return this.valueFormatString;
        }
    }
}