package analysisExample

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.util.coreRealm.ResultSetConvertor
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import com.viewfunction.docg.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil
import org.apache.spark.graphx.{Edge, Graph, PartitionStrategy, VertexId}
import org.apache.spark.rdd.RDD

import java.sql.ResultSet
import java.util.Date

object GraphAnalysisExample {

  def main(args:Array[String]):Unit = {
    val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
    val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
    val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation)

    val jdbcResultSetConvertImpl = new ResultSetConvertor {
      override def convertFunction(resultSet: ResultSet): Any = {
        (resultSet.getDouble(1),resultSet.getString(2))
      }
    }
    val vertexRDD4 = globalDataAccessor.getVertexRDD("PermittedUseMainline",CoreRealmOperationUtil.defaultSliceGroup,jdbcResultSetConvertImpl)
    val vRdd1 = vertexRDD4.asInstanceOf[RDD[(VertexId, (Double, String))]]
    vRdd1.take(10).foreach(println(_))

    val jdbcResultSetConvertImpl1 = new ResultSetConvertor {
      override def convertFunction(resultSet: ResultSet): Any = {
        (10000,"kokosss",new Date())
      }
    }
    val edgeRDD2 = globalDataAccessor.getEdgeRDD("GS_SpatialConnect",CoreRealmOperationUtil.defaultSliceGroup,jdbcResultSetConvertImpl1)
    val eRdd1 = edgeRDD2.asInstanceOf[RDD[Edge[(Long,Int,String,Date)]]]
    eRdd1.take(10).foreach(println(_))

    val edgeRDD:RDD[Edge[(Long,String,String)]] = globalDataAccessor.getEdgeRDD("GS_SpatialConnect",CoreRealmOperationUtil.defaultSliceGroup)
    edgeRDD.take(10).foreach(println(_))

    val vertexRDD1:RDD[(VertexId, (String, String))] = globalDataAccessor.getVertexRDD("PermittedUseMainline",CoreRealmOperationUtil.defaultSliceGroup)
    val vertexRDD2:RDD[(VertexId, (String, String))] = globalDataAccessor.getVertexRDD("MainlineEndPoint",CoreRealmOperationUtil.defaultSliceGroup)
    val vertexRDD3:RDD[(VertexId, (String, String))] = globalDataAccessor.getVertexRDD("MainlineConnectionPoint",CoreRealmOperationUtil.defaultSliceGroup)
    val wholeDataVertexRDD = vertexRDD1.union(vertexRDD2).union(vertexRDD3)
    vertexRDD1.take(10).foreach(println(_))

    val networkGraph = Graph(wholeDataVertexRDD,edgeRDD)
    //networkGraph.partitionBy(PartitionStrategy.RandomVertexCut,20)
    //println(networkGraph.numEdges)
    //println(networkGraph.numVertices)
    println(networkGraph.connectedComponents(20))

    println(networkGraph.edges.getClass)
    println(networkGraph.vertices.getClass)
    println(networkGraph.triangleCount().numVertices)

    globalDataAccessor.close()
  }
}
