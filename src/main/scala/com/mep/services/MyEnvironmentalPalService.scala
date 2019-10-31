package com.mep.services

import com.mep.config.ComponentProvider
import com.twitter.util.{Future, Try}
import com.mep.models.{Footprint, Message, UserId}

class MyEnvironmentalPalService {
  val context: ComponentProvider = new ComponentProvider

  val message = Message("Hello, world!")

  def getMessage: Future[Message] = {
    Future.value(message)
  }

  def acceptMessage(incomingMessage: Message): Future[Message] = {
    Future.value(Message("previous message: " + incomingMessage.message))
  }

  def getFootprint(userId: UserId): Future[Footprint] = {
    println("getting daily footprint for userId = " + userId)
    Future value Footprint(context.footprintDao.getFootprintById("1".toInt))
  }
}
