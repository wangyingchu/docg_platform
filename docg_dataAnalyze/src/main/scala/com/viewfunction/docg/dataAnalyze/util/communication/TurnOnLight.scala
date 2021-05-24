package com.viewfunction.docg.dataAnalyze.util.communication

case class TurnOnLight(time: Int) extends Action {   // 开灯消息
  val message = "Turn on the living room light"
}