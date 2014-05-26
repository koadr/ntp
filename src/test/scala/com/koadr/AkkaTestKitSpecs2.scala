package com.koadr

import org.specs2.mutable._
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.specs2.specification.Scope

/*
    The Implicit Sender below ensures a test actor is implicitly used.
    In most cases this may be sufficient instead of initializing a separate actor for testing.
 */

abstract class AkkaTestKitSpecs2(_system: ActorSystem) extends TestKit(_system) with After with ImplicitSender {
  override def after = system.shutdown()


  def this() = this(ActorSystem("Ntp",
    ConfigFactory.parseString("""
      akka.loggers = ["akka.testkit.TestEventListener"]
      """)))
}

trait akkaIntegration extends AkkaTestKitSpecs2 with Scope {
  val testProducer = TestActorRef[Producer]
  val consumerName = s"consumer:${Utils.guid}"
}
