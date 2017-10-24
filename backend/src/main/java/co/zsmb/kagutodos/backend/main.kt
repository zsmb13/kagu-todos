package co.zsmb.kagutodos.backend

import io.vertx.core.Vertx


fun main(args: Array<String>) {

    val vertx = Vertx.vertx()

    vertx.deployVerticle("co.zsmb.kagutodos.backend.verticles.WebVerticle")
    vertx.deployVerticle("co.zsmb.kagutodos.backend.verticles.DataVerticle")

    println("Deployed verticles")

}
