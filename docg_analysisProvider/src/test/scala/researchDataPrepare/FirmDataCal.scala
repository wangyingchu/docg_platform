package researchDataPrepare

import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleLevel
import com.viewfunction.docg.analysisProvider.tools.dataMaintain.SpatialDataMaintainUtil
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.{DataServiceInvoker, DataSlicePropertyType}

import java.util

object FirmDataCal extends App{

  val dataServiceInvoker = DataServiceInvoker.getInvokerInstance()

  val syncPropertiesMapping = new util.HashMap[String,DataSlicePropertyType]
  val spatialDataMaintainUtil = new SpatialDataMaintainUtil
  syncPropertiesMapping.put("name",DataSlicePropertyType.STRING)


  try{
  val resultDataSlice =
    spatialDataMaintainUtil.syncGeospatialConceptionKindToDataSlice(dataServiceInvoker,
      "Firm","firmData","defaultGroup",syncPropertiesMapping,GeospatialScaleLevel.CountryLevel)

  println(resultDataSlice.getDataSliceMetaInfo.getDataSliceName)
  println(resultDataSlice.getDataSliceMetaInfo.getTotalDataCount)

  }catch{
    case e : Exception =>
      println("Exception Occured : "+e)
      dataServiceInvoker.close()
  }

  dataServiceInvoker.close()
}
