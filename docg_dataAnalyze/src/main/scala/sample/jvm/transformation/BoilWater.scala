package sample.jvm.transformation

case class BoilWater(time: Int) extends Action { // 烧水消息
  val message = "Burn a pot of water"
}
