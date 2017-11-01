package co.zsmb.kagutodos.frontend.services.network

import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.model.fixMethods
import co.zsmb.weblib.core.di.inject
import co.zsmb.weblib.services.http.HttpService
import co.zsmb.weblib.services.logging.Logger

class TodoAPIImpl(private val httpService: HttpService) : TodoAPI {

    companion object {
        private const val BASE_URL = "http://localhost:8080"
    }

    val logger by inject<Logger>()

    override fun getTodos(callback: (Array<Todo>) -> Unit) {
        httpService.get("$BASE_URL/todos") { result ->
            val todos = JSON.parse<Array<Todo>>(result)
            val fixedTodos = todos.map { it.fixMethods() }.toTypedArray()
            callback(fixedTodos)
        }
    }

    override fun getTodo(id: String, callback: (Todo) -> Unit) {
        httpService.get("$BASE_URL/todos/$id") { result ->
            logger.d(this, result)
            callback(JSON.parse<Todo>(result).fixMethods())
        }
    }

    override fun addTodo(todo: Todo, callback: (Todo) -> Unit) {
        httpService.post("$BASE_URL/todos", todo) { result ->
            logger.d(this, result)
            callback(JSON.parse<Todo>(result).fixMethods())
        }
    }

    override fun removeTodo(id: String, callback: (Todo) -> Unit) {
        httpService.delete("$BASE_URL/todos/$id") { result ->
            logger.d(this, result)
            callback(JSON.parse<Todo>(result).fixMethods())
        }
    }

    override fun updateTodo(id: String, todo: Todo, callback: (Todo) -> Unit) {
        httpService.put("$BASE_URL/todos/$id", todo) { result ->
            logger.d(this, result)
            callback(JSON.parse<Todo>(result).fixMethods())
        }
    }

}
