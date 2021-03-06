package co.zsmb.kagutodos.backend.verticles

import co.zsmb.kagutodos.backend.models.Todo
import co.zsmb.kagutodos.backend.util.jsonStr
import co.zsmb.kagutodos.backend.util.parseJson
import co.zsmb.kagutodos.backend.util.toJson
import co.zsmb.kagutodos.backend.verticles.DataVerticle.Companion.ADD_NEW_TODO
import co.zsmb.kagutodos.backend.verticles.DataVerticle.Companion.GET_ALL_TODOS
import co.zsmb.kagutodos.backend.verticles.DataVerticle.Companion.GET_TODO_BY_ID
import co.zsmb.kagutodos.backend.verticles.DataVerticle.Companion.REMOVE_TODO_BY_ID
import co.zsmb.kagutodos.backend.verticles.DataVerticle.Companion.SET_TODOS
import co.zsmb.kagutodos.backend.verticles.DataVerticle.Companion.UPDATE_TODO_BY_ID
import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.eventbus.Message
import io.vertx.core.eventbus.ReplyException
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler

class WebVerticle : AbstractVerticle() {

    override fun start(startFuture: Future<Void>) {
        val router = Router.router(vertx).apply {
            route().handler(CorsHandler.create("*")
                    .allowedMethod(HttpMethod.GET)
                    .allowedMethod(HttpMethod.POST)
                    .allowedMethod(HttpMethod.PUT)
                    .allowedMethod(HttpMethod.DELETE)
            )
            route().handler(BodyHandler.create())

            get("/").handler { req ->
                req.response().end("Todo server is up")
            }

            get("/todos").handler { req ->
                vertx.eventBus().send<String>(GET_ALL_TODOS, "") { res ->
                    respondWithJson(res, req)
                }
            }
            post("/todos").handler { req ->
                val task = if (req.bodyAsString.startsWith("[")) SET_TODOS else ADD_NEW_TODO
                vertx.eventBus().send<String>(task, req.bodyAsString) { res ->
                    respondWithJson(res, req)
                }
            }

            get("/todos/:id").handler { req ->
                val message = jsonStr { "_id" to req.pathParam("id") }
                vertx.eventBus().send<String>(GET_TODO_BY_ID, message) { res ->
                    respondWithJson(res, req)
                }
            }
            put("/todos/:id").handler { req ->
                val pathParamId = req.pathParam("id")
                val todo = req.bodyAsString.parseJson<Todo>().copy(_id = pathParamId)

                vertx.eventBus().send<String>(UPDATE_TODO_BY_ID, todo.toJson()) { res ->
                    respondWithJson(res, req)
                }
            }
            delete("/todos/:id").handler { req ->
                val message = jsonStr { "_id" to req.pathParam("id") }
                vertx.eventBus().send<String>(REMOVE_TODO_BY_ID, message) { res ->
                    respondWithJson(res, req)
                }
            }
        }

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(config().getInteger("http.port", 8080)) { res ->
                    if (res.succeeded()) {
                        startFuture.complete()
                        println("Started http server")
                    } else {
                        startFuture.fail(res.cause())
                    }
                }
    }

    private fun respondWithJson(res: AsyncResult<Message<String>>, req: RoutingContext) {
        if (res.succeeded()) {
            req.response().endWithJson(res.result().body())
        } else {
            val cause = res.cause()
            val statusCode = (cause as? ReplyException)?.failureCode() ?: 500

            req.response()
                    .setStatusCode(statusCode)
                    .end(cause.message)
        }
    }

    private fun HttpServerResponse.endWithJson(json: String) {
        this.putHeader("Content-Type", "application/json; charset=utf-8")
                .end(json)
    }

}
