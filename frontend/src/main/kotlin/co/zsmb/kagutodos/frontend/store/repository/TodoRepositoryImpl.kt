package co.zsmb.kagutodos.frontend.store.repository

import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.store.local.LocalTodoAPI
import co.zsmb.kagutodos.frontend.store.network.NetworkTodoAPI
import co.zsmb.weblib.core.util.Date
import co.zsmb.weblib.services.messaging.MessageBroker

class TodoRepositoryImpl(
        private val localApi: LocalTodoAPI,
        private val networkApi: NetworkTodoAPI,
        private val messageBroker: MessageBroker) : TodoRepository {

    private var syncInProgress = false
    private var desynchronized = false

    init {
        sync()
    }

    override fun getTodos(callback: (Array<Todo>) -> Unit) {
        localApi.getTodos { localTodos ->
            callback(localTodos!!)
        }
    }

    override fun getTodo(id: String, callback: (Todo) -> Unit) {
        localApi.getTodo(id) { localTodo ->
            callback(localTodo!!)
        }
    }

    override fun addTodo(todo: Todo, callback: (Todo) -> Unit) {
        val timestampedTodo = todo.copy(lastModified = Date.now())
        localApi.addTodo(timestampedTodo) { localTodo ->
            callback(localTodo!!)

            networkApi.addTodo(localTodo) network@ { remoteTodo ->
                updateNetworkStatus(remoteTodo)
            }
        }
    }

    override fun removeTodo(id: String, callback: (Todo) -> Unit) {
        localApi.removeTodo(id) { localTodo ->
            callback(localTodo!!)
            networkApi.removeTodo(id) network@ { remoteTodo ->
                updateNetworkStatus(remoteTodo)
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

            networkApi.updateTodo(timeStampedTodo._id, localTodo) network@ { remoteTodo ->
                updateNetworkStatus(remoteTodo)
            }
        }
    }

    private fun updateNetworkStatus(response: Any?) {
        when {
            response == null ->
                desynchronized = true
            desynchronized && !syncInProgress ->
                sync()
        }
    }

    private fun sync() {
        syncInProgress = true

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
        val remoteSet = remote.toMutableSet()

        val finalTodos = mutableListOf<Todo>()

        local.forEach { localTodo ->
            val localId = localTodo._id
            val remoteTodo = remoteSet.find { it._id == localId }

            val latestTodo = when {
                remoteTodo == null || localTodo.lastModified!! > remoteTodo.lastModified!! ->
                    localTodo
                else -> {
                    remoteSet -= remoteTodo
                    remoteTodo
                }
            }

            val deletedTodo = deletedTodos.filter { it._id == localId }.maxBy { it.lastModified ?: 0 }

            if (deletedTodo != null && deletedTodo.lastModified!! > latestTodo.lastModified!!) {
                return@forEach
            }

            if (latestTodo != localTodo) {
                announceUpdatedTodo(latestTodo)
            }

            finalTodos += latestTodo
        }

        finalTodos += remoteSet

        val finalTodoArray = finalTodos.toTypedArray()

        localApi.setTodos(finalTodoArray) {
            networkApi.setTodos(finalTodoArray) {
                announceTodosChanged(finalTodoArray)
                syncInProgress = false
                desynchronized = false
            }
        }
    }

    private fun announceUpdatedTodo(newTodo: Todo) {
        messageBroker.publish("TODO_CHANGED_${newTodo._id}", newTodo)
    }

    private fun announceTodosChanged(allTodos: Array<Todo>) {
        messageBroker.publish("TODOS_CHANGED", allTodos)
    }

}
