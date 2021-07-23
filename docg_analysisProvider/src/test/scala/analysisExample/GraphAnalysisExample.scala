package analysisExample

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.util.coreRealm.ResultSetConvertor
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import com.viewfunction.docg.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil
import org.apache.spark.graphx.{Edge, Graph, PartitionStrategy, VertexId}
import org.apache.spark.rdd.RDD

import java.sql.ResultSet

object GraphAnalysisExample {

  def main(args:Array[String]):Unit = {
    val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
    val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
    val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation)

    val connectionEdgeDF = globalDataAccessor.getDataFrameFromDataSlice("GS_SpatialConnect",CoreRealmOperationUtil.defaultSliceGroup)
    val endPointDF = globalDataAccessor.getDataFrameFromDataSlice("MainlineEndPoint",CoreRealmOperationUtil.defaultSliceGroup)
    val mainlineDF = globalDataAccessor.getDataFrameFromDataSlice("PermittedUseMainline",CoreRealmOperationUtil.defaultSliceGroup)
    val middlePointDF = globalDataAccessor.getDataFrameFromDataSlice("MainlineConnectionPoint",CoreRealmOperationUtil.defaultSliceGroup)

    //println(connectionEdgeDF.count())
    //println(endPointDF.count())
    //println(mainlineDF.count())
    //println(middlePointDF.count())

    val jdbcResultSetConvertImpl = new ResultSetConvertor {
      override def convertFunction(resultSet: ResultSet): Any = {
        (resultSet.getString(1),resultSet.getString(2))
      }
    }

    val result = (globalDataAccessor._getJdbcRDD("GS_SpatialConnect",CoreRealmOperationUtil.defaultSliceGroup,jdbcResultSetConvertImpl)).asInstanceOf[RDD[(String,String)]]
   // println(result.count())

    /*
    result.take(100).foreach(data=>{
      println(data._1+" -> "+data._2)
    })
    */

    val edgeRDD:RDD[Edge[(Long,String,String)]] = globalDataAccessor.getEdgeRDD("GS_SpatialConnect",CoreRealmOperationUtil.defaultSliceGroup)

    edgeRDD.take(10).foreach(println(_))



    val vertexRDD1:RDD[(VertexId, (String, String))] = globalDataAccessor.getVertexRDD("PermittedUseMainline",CoreRealmOperationUtil.defaultSliceGroup)

    val vertexRDD2:RDD[(VertexId, (String, String))] = globalDataAccessor.getVertexRDD("MainlineEndPoint",CoreRealmOperationUtil.defaultSliceGroup)
    val vertexRDD3:RDD[(VertexId, (String, String))] = globalDataAccessor.getVertexRDD("MainlineConnectionPoint",CoreRealmOperationUtil.defaultSliceGroup)

    val wholeDataVertexRDD = vertexRDD1.union(vertexRDD2).union(vertexRDD3)

    vertexRDD1.take(10).foreach(println(_))
    //println(wholeDataVertexRDD.count())


    val networkGraph = Graph(wholeDataVertexRDD,edgeRDD)
    //networkGraph.partitionBy(PartitionStrategy.RandomVertexCut,20)


    //println(networkGraph.numEdges)
    //println(networkGraph.numVertices)
    println(networkGraph.connectedComponents(20))





    val jdbcResultSetConvertImpl2 = new ResultSetConvertor {
      override def convertFunction(resultSet: ResultSet): Any = {
        (resultSet.getDouble(1),resultSet.getString(2))
      }
    }

    val vertexRDD4 = globalDataAccessor.getVertexRDD("PermittedUseMainline",CoreRealmOperationUtil.defaultSliceGroup,jdbcResultSetConvertImpl2)

    val xxx = vertexRDD4.asInstanceOf[RDD[(VertexId, (Double, String))]]

    xxx.take(100).foreach(println(_))

    globalDataAccessor.close()
  }

}
