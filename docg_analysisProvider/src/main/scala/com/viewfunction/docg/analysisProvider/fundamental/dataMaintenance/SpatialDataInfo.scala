package com.viewfunction.docg.analysisProvider.fundamental.dataMaintenance

import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataSlicePropertyType

import java.util

case class SpatialDataInfo(spatialDataPropertiesDefinition: util.HashMap[String, DataSlicePropertyType],
                           spatialDataValue: util.ArrayList[util.HashMap[String, Any]])
