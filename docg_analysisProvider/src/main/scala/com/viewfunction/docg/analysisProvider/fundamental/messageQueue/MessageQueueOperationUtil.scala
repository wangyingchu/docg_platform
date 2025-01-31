package com.viewfunction.docg.analysisProvider.fundamental.messageQueue

import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset

import java.util
import scala.util.{Failure,Try}

import java.nio.charset.StandardCharsets

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import org.apache.rocketmq.client.apis.message.MessageBuilder
import org.apache.rocketmq.client.apis.producer.Producer
import org.apache.rocketmq.client.apis.{ClientConfiguration, ClientException, ClientServiceProvider}

import java.io.IOException

object MessageQueueOperationUtil {

  val messageBrokerAddress:String = AnalysisProviderApplicationUtil.getApplicationProperty("messageBrokerAddress")

  def sendMessageFromResponseDataset(messageTopic:String,messageTag:String,messageProperties:java.util.HashMap[String,String],
                                       responseDataset:ResponseDataset):Unit = {
    Try {
      val endpoint = messageBrokerAddress
      val topic = messageTopic
      val provider = ClientServiceProvider.loadService()
      val builder = ClientConfiguration.newBuilder().setEndpoints(endpoint)
      val configuration = builder.build()
      var producer: Producer = null

      producer = provider.newProducerBuilder()
        .setTopics(topic)
        .setClientConfiguration(configuration)
        .build()

      val dataList: java.util.ArrayList[java.util.HashMap[String,Object]] = responseDataset.getDataList

      dataList.forEach((map: util.HashMap[String, Object]) => {
        val jsonString = JSON.toJSONString(map, SerializerFeature.PrettyFormat)
        val messageBuilder:MessageBuilder = provider.newMessageBuilder()
          .setTopic(topic)
          .setBody(jsonString.getBytes(StandardCharsets.UTF_8))

        if(messageTag != null){
          messageBuilder.setTag(messageTag)
        }
        if(messageProperties != null){
          messageProperties.keySet().forEach((key:String) => {
            messageBuilder.addProperty(key, messageProperties.get(key))
          })
        }
        val message=messageBuilder.build()
        val sendReceipt = producer.send(message)
      })

      producer.close()
    } match {
      case Failure(e: ClientException) =>
        throw new RuntimeException()
      case   Failure(e: IOException) =>
        throw new RuntimeException()
      case _ =>
    }
  }
}
