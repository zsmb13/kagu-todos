package co.zsmb.kagutodos.frontend.store.repository

import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.store.local.LocalTodoAPI
import co.zsmb.kagutodos.frontend.store.network.NetworkTodoAPI
import co.zsmb.weblib.core.util.Date

class TodoRepositoryImpl(
        private val localApi: LocalTodoAPI,
        private val networkApi: NetworkTodoAPI) : TodoRepository {

    private var networkErrored = true
    private var syncInProgress = false

    fun sync() {
        if (!networkErrored) return
        if (syncInProgress) return

        syncInProgress = true
        networkErrored = false

        networkApi.getTodos { remoteTodos ->
            if (remoteTodos == null) {
                syncInProgress = false
                return@getTodos
            }

            localApi.getTodos { localTodos ->
                localApi.getDeletedTodos { deletedTodos ->
                    mergeTodos(remoteTodos, localTodos!!, deletedTodos)
                }
            }
        }
    }

    private fun mergeTodos(remote: Array<Todo>, local: Array<Todo>, deletedTodos: Array<Todo>) {
        val allTodos = (remote + local).groupBy { it._id }

        val finalTodos = mutableListOf<Todo>()

        allTodos.forEach { (id, todosWithId) ->
            val deleted = deletedTodos.filter { it._id == id }.maxBy { it.lastModified ?: 0 }
            val latest = todosWithId.maxBy { it.lastModified ?: 0 }!!

            if (deleted != null && deleted.lastModified!! > latest.lastModified!!) {
                return@forEach
            }

            finalTodos += latest
        }

        val finalTodoArray = finalTodos.toTypedArray()

        localApi.setTodos(finalTodoArray) {
            networkApi.setTodos(finalTodoArray) {
                syncInProgress = false
            }
        }
    }

    init {
        networkErrored = true
        sync()
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

            networkApi.addTodo(localTodo) { remoteTodo ->
                if (remoteTodo == null) {
                    networkErrored = true
                } else {
                    sync()
                    callback(remoteTodo)
                }
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

    override fun updateTodo(todo: Todo, callback: (Todo) -> Unit) {
        val timeStampedTodo = todo.copy(lastModified = Date.now())
        if (timeStampedTodo._id == null) {
            throw IllegalArgumentException("Todo ID can't be null")
        }
        localApi.updateTodo(timeStampedTodo._id, timeStampedTodo) { localTodo ->
            callback(localTodo!!)

            networkApi.updateTodo(timeStampedTodo._id, localTodo) { remoteTodo ->
                if (remoteTodo == null) {
                    networkErrored = true
                } else {
                    sync()
                    callback(remoteTodo)
                }
            }
        }
    }

}
