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
        Format1("yyyyMMdd"),
        Format2("yyyy-MM-dd"),
        Format3("yyyy-MM-dd HH:mm:ss"),
        Format4("yyyy/mm/dd hh:mm:ss"),
        Format5("yyyy/m/d h:mm:ss"),
        Format6("yyyy/m/dd h:mm:ss"),
        Format7("yyyymmdd"),
        Format8("yyyy/mm/dd"),
        Format9("yyyy/m/d"),
        Format10("MM/dd/yyyy HH:mm:ss a"),
        Format11("MM/dd/yyyy hh:mm:ss a");

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