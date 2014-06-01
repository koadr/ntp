package com.koadr

import org.specs2.mutable._
import org.specs2.time.NoTimeConversions
import akka.actor.Props
import akka.testkit.{EventFilter, TestActorRef}


class ConsumerSpec extends Specification with NoTimeConversions {

  args(sequential = true)

  "A Consumer" should {
    "send KeepAlive Messages to producer" in new akkaIntegration {
      val keepAlive = EventFilter.info(s"Heartbeat: $consumerName", occurrences = 1) intercept{
        TestActorRef[Consumer](Props(classOf[Consumer],testProducer,1), consumerName )
      }

      keepAlive should throwA[AssertionError].not
    }

    "does not send KeepAlive messages to producer if max keepalive messages are 0" in new akkaIntegration {
      val keepAlive = EventFilter.info(s"Heartbeat: $consumerName", occurrences = 0) intercept{
        TestActorRef[Consumer](Props(classOf[Consumer],testProducer,0), consumerName )
      }

      keepAlive should throwA[AssertionError].not
    }
  }

}
