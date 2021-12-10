package com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial

import scala.collection.mutable

case class SpatialQueryParam(spatialDataFrameName: String, spatialAttributeName: String, resultAttributes: mutable.Buffer[String]) {}
