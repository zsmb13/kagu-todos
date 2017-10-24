package co.zsmb.kagutodos.backend.verticles

import co.zsmb.kagutodos.backend.models.Todo
import co.zsmb.kagutodos.backend.util.parseJson
import co.zsmb.kagutodos.backend.util.toJson
import io.vertx.core.AbstractVerticle

class DataVerticle : AbstractVerticle() {

    companion object {
        const val GET_ALL_TODOS = "dataverticle.todos.get"
        const val ADD_NEW_TODO = "dataverticle.todos.add"
        const val GET_TODO_BY_ID = "dataverticle.todo.get"
        const val UPDATE_TODO_BY_ID = "dataverticle.todo.update"
        const val REMOVE_TODO_BY_ID = "dataverticle.todo.delete"
    }

    private val todos = mutableListOf<Todo>(
            Todo("My first todo", false, 1L),
            Todo("Another todo", true, 2L)
    )

    override fun start() {
        vertx.eventBus().consumer<String>(GET_ALL_TODOS) { msg ->
            msg.reply(todos.toJson())
        }

        vertx.eventBus().consumer<String>(ADD_NEW_TODO) { msg ->
            val todo = msg.body().parseJson<Todo>()

            val maxId = todos
                    .map { it.id }
                    .filterNotNull()
                    .max()
                    ?: 1
            val todoWithId = todo.copy(id = maxId + 1)

            todos += todoWithId

            msg.reply(todoWithId.toJson())
        }

        vertx.eventBus().consumer<String>(GET_TODO_BY_ID) { msg ->
            val id = msg.body().toLongOrNull()
                    ?: return@consumer msg.fail(400, "Invalid ID")

            val todo = todos.find { it.id == id }
                    ?: return@consumer msg.fail(404, "No todo found by given ID")

            msg.reply(todo.toJson())
        }

        vertx.eventBus().consumer<String>(UPDATE_TODO_BY_ID) { msg ->
            val newTodo = msg.body().parseJson<Todo>()

            val oldTodo = todos.find { it.id == newTodo.id }
                    ?: return@consumer msg.fail(404, "No todo found by given ID")

            todos -= oldTodo
            todos += newTodo

            msg.reply(newTodo.toJson())
        }

        vertx.eventBus().consumer<String>(REMOVE_TODO_BY_ID) { msg ->
            val id = msg.body().toLongOrNull()
                    ?: return@consumer msg.fail(400, "Invalid ID")

            val todo = todos.find { it.id == id }
                    ?: return@consumer msg.fail(404, "No todo found by given ID")

            todos -= todo

            msg.reply(todo.toJson())
        }

    }

}
