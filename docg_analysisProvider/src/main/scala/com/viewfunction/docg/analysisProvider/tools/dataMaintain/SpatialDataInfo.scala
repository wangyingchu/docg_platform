package com.viewfunction.docg.analysisProvider.tools.dataMaintain

import com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.MemoryTablePropertyType

import java.util

case class SpatialDataInfo(spatialDataPropertiesDefinition:util.HashMap[String,MemoryTablePropertyType],
                           spatialDataValue:util.ArrayList[util.HashMap[String, Any]])
