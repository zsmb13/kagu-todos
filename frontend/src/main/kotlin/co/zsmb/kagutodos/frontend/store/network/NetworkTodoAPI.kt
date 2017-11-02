package co.zsmb.kagutodos.frontend.store.network

import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.model.fixMethods
import co.zsmb.kagutodos.frontend.store.repository.TodoAPI
import co.zsmb.weblib.core.di.inject
import co.zsmb.weblib.services.http.HttpService
import co.zsmb.weblib.services.logging.Logger

class NetworkTodoAPI(private val httpService: HttpService) : TodoAPI {

    companion object {
        private const val BASE_URL = "http://localhost:8080"
    }

    init {
        httpService.backoffStrategy = HttpService.BackoffStrategy.Exponential
    }

    val logger by inject<Logger>()

    fun <T> sendError(callback: (T?) -> Unit): (String) -> Unit = {
        callback(null)
    }

    override fun getTodos(callback: (Array<Todo>?) -> Unit) {
        httpService.get("${BASE_URL}/todos",
                onSuccess = { result ->
                    val todos = JSON.parse<Array<Todo>>(result)
                    val fixedTodos = todos.map { it.fixMethods() }.toTypedArray()
                    callback(fixedTodos)
                },
                onError = sendError(callback))
    }

    override fun getTodo(id: String, callback: (Todo?) -> Unit) {
        httpService.get("${BASE_URL}/todos/$id",
                onSuccess = { result ->
                    logger.d(this, result)
                    callback(JSON.parse<Todo>(result).fixMethods())
                },
                onError = sendError(callback))
    }

    override fun addTodo(todo: Todo, callback: (Todo?) -> Unit) {
        httpService.post("${BASE_URL}/todos", todo,
                onSuccess = { result ->
                    logger.d(this, result)
                    callback(JSON.parse<Todo>(result).fixMethods())
                },
                onError = sendError(callback))
    }

    override fun removeTodo(id: String, callback: (Todo?) -> Unit) {
        httpService.delete("${BASE_URL}/todos/$id",
                onSuccess = { result ->
                    logger.d(this, result)
                    callback(JSON.parse<Todo>(result).fixMethods())
                },
                onError = sendError(callback))
    }

    override fun updateTodo(id: String, todo: Todo, callback: (Todo?) -> Unit) {
        httpService.put("${BASE_URL}/todos/$id", todo,
                onSuccess = { result ->
                    logger.d(this, result)
                    callback(JSON.parse<Todo>(result).fixMethods())
                },
                onError = sendError(callback))
    }

}
