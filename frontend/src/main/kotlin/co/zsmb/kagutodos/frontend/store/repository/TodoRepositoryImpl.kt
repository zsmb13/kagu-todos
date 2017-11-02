package co.zsmb.kagutodos.frontend.store.repository

import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.store.local.LocalTodoAPI
import co.zsmb.kagutodos.frontend.store.network.NetworkTodoAPI
import co.zsmb.weblib.core.util.Date

class TodoRepositoryImpl(
        private val localApi: LocalTodoAPI,
        private val networkApi: NetworkTodoAPI) : TodoRepository {

    var networkErrored = false

    fun sync() {
        if (!networkErrored) return


    }

    override fun getTodos(callback: (Array<Todo>) -> Unit) {
        localApi.getTodos { localTodos ->
            callback(localTodos!!)
        }
        networkApi.getTodos { remoteTodos ->
            if (remoteTodos == null) {
                networkErrored = true
            } else {
                sync()
                callback(remoteTodos)
            }
        }
    }

    override fun getTodo(id: String, callback: (Todo) -> Unit) {
        localApi.getTodo(id) { localTodo ->
            callback(localTodo!!)
        }
        networkApi.getTodo(id) { remoteTodo ->
            if (remoteTodo == null) {
                networkErrored = true
            } else {
                sync()
                callback(remoteTodo)
            }
        }
    }

    override fun addTodo(todo: Todo, callback: (Todo) -> Unit) {
        val timestampedTodo = todo.copy(lastModified = Date.now())
        localApi.addTodo(timestampedTodo) { localTodo ->
            callback(localTodo!!)
        }
        networkApi.addTodo(timestampedTodo) { remoteTodo ->
            if (remoteTodo == null) {
                networkErrored = true
            } else {
                sync()
                callback(remoteTodo)
            }
        }
    }

    override fun removeTodo(id: String, callback: (Todo) -> Unit) {
        localApi.removeTodo(id) { localTodo ->
            callback(localTodo!!)
        }
        networkApi.removeTodo(id) { remoteTodo ->
            if (remoteTodo == null) {
                networkErrored = true
            } else {
                sync()
            }
        }
    }

    override fun updateTodo(id: String, todo: Todo, callback: (Todo) -> Unit) {
        val todoWithId = todo.copy(_id = id)
        localApi.updateTodo(id, todoWithId) { localTodo ->
            callback(localTodo!!)
        }
        networkApi.updateTodo(id, todoWithId) { remoteTodo ->
            if (remoteTodo == null) {
                networkErrored = true
            } else {
                sync()
                callback(remoteTodo)
            }
        }
    }

}
