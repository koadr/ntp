package com.koadr


object Utils {
  def guid() = java.util.UUID.randomUUID().toString.replaceAll("-","")
}
