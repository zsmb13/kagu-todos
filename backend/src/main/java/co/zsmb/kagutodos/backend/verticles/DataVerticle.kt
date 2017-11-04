package co.zsmb.kagutodos.backend.verticles

import co.zsmb.kagutodos.backend.models.Todo
import co.zsmb.kagutodos.backend.util.*
import com.google.gson.JsonSyntaxException
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.BulkOperation
import io.vertx.ext.mongo.MongoClient

class DataVerticle : AbstractVerticle() {

    companion object {
        const val GET_ALL_TODOS = "dataverticle.todos.get"
        const val ADD_NEW_TODO = "dataverticle.todos.add"
        const val GET_TODO_BY_ID = "dataverticle.todo.get"
        const val SET_TODOS = "dataverticle.todo.set"
        const val UPDATE_TODO_BY_ID = "dataverticle.todo.update"
        const val REMOVE_TODO_BY_ID = "dataverticle.todo.delete"

        private const val DB_NAME = "todos"
    }

    lateinit var mongoClient: MongoClient

    override fun start() {

        mongoClient = MongoClient.createShared(vertx,
                json {
                    "connection_string" to "mongodb://localhost:27017"
                    "db_name" to "data_verticle_db"
                }
        )

        subscribe(GET_ALL_TODOS) { msg ->
            mongoClient.find(DB_NAME, json {}) { res ->
                if (res.succeeded()) {
                    val todos = res.result().map { it.encode().parseJson<Todo>() }
                    msg.reply(todos.toJson())
                } else {
                    msg.fail("MongoDB error")
                    res.cause().printStackTrace()
                }
            }
        }

        subscribe(SET_TODOS) { msg ->
            mongoClient.dropCollection(DB_NAME) { dropResult ->
                if (!dropResult.succeeded()) {
                    msg.fail(500, "Couldn't drop collection")
                } else {
                    val todos = msg.parseJson<Array<Todo>>()
                    val insertOps = todos.map { todo ->
                        BulkOperation.createInsert(JsonObject(todo.toJson())).apply {
                            isUpsert = true
                        }
                    }
                    mongoClient.bulkWrite(DB_NAME, insertOps) { insertRes ->
                        if (!insertRes.succeeded()) {
                            msg.fail("Couldn't set todos: ${insertRes.cause().message}", 500)
                        } else {
                            msg.reply(jsonStr { "success" to true })
                        }
                    }
                }
            }
        }

        subscribe(ADD_NEW_TODO) { msg ->
            val todo = try {
                msg.parseJson<Todo>()
            } catch (e: JsonSyntaxException) {
                msg.fail("Invalid Todo", 400)
                return@subscribe
            }

            mongoClient.insert(DB_NAME, JsonObject(todo.toJson())) { res ->
                if (res.succeeded()) {
                    msg.reply(todo)
                } else {
                    msg.fail("MongoDB error")
                    res.cause().printStackTrace()
                }
            }
        }

        subscribe(GET_TODO_BY_ID) { msg ->
            val _id = JsonObject(msg.body()).getString("_id")

            mongoClient.findOne(DB_NAME, json { "_id" to _id }, json {}) { res ->
                if (res.succeeded()) {
                    val todo = res.result().encode().parseJson<Todo>()
                    msg.reply(todo.toJson())
                } else {
                    msg.fail("MongoDB error")
                    res.cause().printStackTrace()
                }
            }
        }

        subscribe(UPDATE_TODO_BY_ID) { msg ->
            val todo = msg.body().parseJson<Todo>()

            mongoClient.findOneAndReplace(DB_NAME, json { "_id" to todo._id }, todo.toJsonObject()) { res ->
                if (res.succeeded()) {
                    // val oldTodo = res.result()
                    msg.reply(todo.toJson())
                } else {
                    msg.fail("MongoDB error")
                    res.cause().printStackTrace()
                }
            }
        }

        subscribe(REMOVE_TODO_BY_ID) { msg ->
            val _id = JsonObject(msg.body()).getString("_id")

            mongoClient.findOneAndDelete(DB_NAME, json { "_id" to _id }) { res ->
                if (res.succeeded()) {
                    val deletedTodo = res.result()
                    msg.reply(deletedTodo.encode())
                } else {
                    msg.fail("MongoDB error")
                    res.cause().printStackTrace()
                }
            }
        }
    }

    fun subscribe(address: String, handler: (Message<String>) -> Unit)
            = vertx.eventBus().consumer<String>(address, handler)

    private fun Message<String>.fail(errorMessage: String = "Unknown error", code: Int = 500)
            = fail(code, jsonStr { "error" to errorMessage })

    override fun stop() {
        mongoClient.close()
        super.stop()
    }

}
