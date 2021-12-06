package com.viewfunction.docg.analysisProvider.tools.dataMaintain


import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlicePropertyType

import java.util

case class SpatialDataInfo(spatialDataPropertiesDefinition:util.HashMap[String,DataSlicePropertyType],
                           spatialDataValue:util.ArrayList[util.HashMap[String, Any]])
