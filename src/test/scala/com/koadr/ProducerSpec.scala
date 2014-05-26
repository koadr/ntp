package com.koadr

import org.specs2.mutable._
import akka.actor.{Props}

import akka.testkit.{EventFilter, TestActorRef}
import com.koadr.Producer.Time
import com.koadr.Consumer.Register
import scala.concurrent.duration._


class ProducerSpec extends Specification {

  args(sequential = true)

  implicit val timeout = Duration(10, SECONDS)

  "A Producer Actor" should {

    "Register Client" in new akkaIntegration {
      val registerNotification = EventFilter.info(message = s"Registered new client $consumerName", occurrences = 1) intercept {
        TestActorRef[Consumer](Props(classOf[Consumer],testProducer,1), consumerName )
      }
      registerNotification should throwA[AssertionError].not
    }

    "send Time messages after a consumer registers" in new akkaIntegration {
      system.actorOf(Props[Producer], "producer") ! Register
      expectMsgClass(classOf[Time]) should not be null
    }

    // Test has to run for at least the timeout period of 10 seconds plus 5 second frequency. Disabling
   "will stop sending time messages after KeepAlive Message timeout" in new akkaIntegration {
     skipped("Test Too Long. Disabled")
     val stopMsg = EventFilter.info(message = s"Deactivating Messaging to $consumerName", occurrences = 1) intercept {
        TestActorRef[Consumer](Props(classOf[Consumer],testProducer,0), consumerName)
        Thread.sleep(15000)
      }
     stopMsg should throwA[AssertionError].not
    }
  }




}
