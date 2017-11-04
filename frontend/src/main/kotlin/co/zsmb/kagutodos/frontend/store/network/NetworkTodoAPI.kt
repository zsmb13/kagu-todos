package co.zsmb.kagutodos.frontend.store.network

import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.model.fixMethods
import co.zsmb.kagutodos.frontend.store.repository.TodoAPI
import co.zsmb.weblib.services.http.HttpService

class NetworkTodoAPI(private val httpService: HttpService) : TodoAPI {

    companion object {
        private const val BASE_URL = "http://localhost:8080"
    }

    init {
        httpService.backoffStrategy = HttpService.BackoffStrategy.Exponential
    }

    fun <T> sendError(callback: (T?) -> Unit): (String) -> Unit = {
        callback(null)
    }

    override fun setTodos(todos: Array<Todo>, callback: (success: Boolean) -> Unit) {
        httpService.post("${BASE_URL}/todos", todos,
                onSuccess = { callback(true) },
                onError = { callback(false) })
    }

    override fun getTodos(callback: (todos: Array<Todo>?) -> Unit) {
        httpService.get("${BASE_URL}/todos",
                onSuccess = { result ->
                    val todos = JSON.parse<Array<Todo>>(result)
                    val fixedTodos = todos.map { it.fixMethods() }.toTypedArray()
                    callback(fixedTodos)
                },
                onError = sendError(callback))
    }

    override fun getTodo(id: String, callback: (todo: Todo?) -> Unit) {
        httpService.get("${BASE_URL}/todos/$id",
                onSuccess = { result ->
                    callback(JSON.parse<Todo>(result).fixMethods())
                },
                onError = sendError(callback))
    }

    override fun addTodo(todo: Todo, callback: (addedTodo: Todo?) -> Unit) {
        httpService.post("${BASE_URL}/todos", todo,
                onSuccess = { result ->
                    callback(JSON.parse<Todo>(result).fixMethods())
                },
                onError = sendError(callback))
    }

    override fun removeTodo(id: String, callback: (removedTodo: Todo?) -> Unit) {
        httpService.delete("${BASE_URL}/todos/$id",
                onSuccess = { result ->
                    callback(JSON.parse<Todo>(result).fixMethods())
                },
                onError = sendError(callback))
    }

    override fun updateTodo(id: String, todo: Todo, callback: (updatedTodo: Todo?) -> Unit) {
        httpService.put("${BASE_URL}/todos/$id", todo,
                onSuccess = { result ->
                    callback(JSON.parse<Todo>(result).fixMethods())
                },
                onError = sendError(callback))
    }

}
