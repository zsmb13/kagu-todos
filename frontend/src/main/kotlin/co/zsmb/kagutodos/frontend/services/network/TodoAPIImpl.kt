package co.zsmb.kagutodos.frontend.services.network

import co.zsmb.kagutodos.frontend.model.Todo
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
            callback(todos)
        }
    }

    override fun getTodo(id: Long, callback: (Todo) -> Unit) {
        httpService.get("$BASE_URL/todo/$id") { result ->
            logger.d(this, result)
            callback(JSON.parse(result))
        }
    }

    override fun addTodo(todo: Todo, callback: (Todo) -> Unit) {
        httpService.post("$BASE_URL/todos", todo) { result ->
            logger.d(this, result)
            callback(JSON.parse(result))
        }
    }

    override fun removeTodo(id: Long, callback: (Todo) -> Unit) {
        httpService.delete("$BASE_URL/todos/$id") { result ->
            logger.d(this, result)
            callback(JSON.parse(result))
        }
    }

    override fun updateTodo(id: Long, todo: Todo, callback: (Todo) -> Unit) {
        httpService.put("$BASE_URL/todos/$id", todo) { result ->
            logger.d(this, result)
            callback(JSON.parse(result))
        }
    }

}
