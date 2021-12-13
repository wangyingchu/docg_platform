package researchDataPrepare

import com.viewfunction.docg.analysisProvider.fundamental.dataMaintenance.SpatialDataMaintainUtil
import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleLevel
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.{DataServiceInvoker, DataSlicePropertyType}

import java.util

object FirmDataCal extends App{

  val dataServiceInvoker = DataServiceInvoker.getInvokerInstance()
  val spatialDataMaintainUtil = new SpatialDataMaintainUtil

  val syncPropertiesMapping = new util.HashMap[String,DataSlicePropertyType]
  syncPropertiesMapping.put("name",DataSlicePropertyType.STRING)

  try{

    val resultDataSlice =
      spatialDataMaintainUtil.syncGeospatialConceptionKindToDataSlice(dataServiceInvoker,
        "Firm","firmData","defaultGroup",syncPropertiesMapping,GeospatialScaleLevel.CountryLevel)
      println(resultDataSlice.getDataSliceMetaInfo.getDataSliceName)
      println(resultDataSlice.getDataSliceMetaInfo.getTotalDataCount)
    spatialDataMaintainUtil.syncGeospatialRegionToDataSlice(dataServiceInvoker,"defaultGroup");
  }catch{
    case e : Exception =>
      println("Exception Occured : "+e)
      dataServiceInvoker.close()
  }

  dataServiceInvoker.close()
}
