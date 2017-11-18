package co.zsmb.kagutodos.frontend.store.local

import co.zsmb.kagu.services.storage.LocalStorage
import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.model.fixMethods
import co.zsmb.kagutodos.frontend.store.repository.TodoAPI
import kotlin.js.Math

class LocalTodoAPI(private val localStorage: LocalStorage) : TodoAPI {

    companion object {
        private const val KEY_TODOS = "KEY_TODOS"
        private const val KEY_DELETED_TODOS = "KEY_DELETED_TODOS"
    }

    private val todos = mutableListOf<Todo>()
    private val deletedTodos = mutableListOf<Todo>()

    init {
        initTodosList()
        initDeletedTodosList()
    }

    private fun initTodosList() {
        val todosString = localStorage[KEY_TODOS] ?: "[]"
        val parsedTodos = JSON.parse<Array<Todo>>(todosString)
        val todoArray = parsedTodos.fixMethods()
        todos.addAll(todoArray)
    }

    private fun initDeletedTodosList() {
        val todosString = localStorage[KEY_DELETED_TODOS] ?: "[]"
        val parsedTodos = JSON.parse<Array<Todo>>(todosString)
        deletedTodos.addAll(parsedTodos)
    }

    override fun setTodos(todos: Array<Todo>, callback: (success: Boolean) -> Unit) {
        this.todos.clear()
        this.todos += todos
        writeTodos()
        callback(true)
    }

    override fun getTodos(callback: (todos: Array<Todo>?) -> Unit) {
        callback(todos.toTypedArray())
    }

    override fun getTodo(id: String, callback: (todo: Todo?) -> Unit) {
        callback(todos.find { it._id == id }!!)
    }

    override fun addTodo(todo: Todo, callback: (addedTodo: Todo?) -> Unit) {
        val todoWithId = todo.copy(_id = IdGenerator.get())
        todos += todoWithId
        writeTodos()
        callback(todoWithId)
    }

    override fun removeTodo(id: String, callback: (removedTodo: Todo?) -> Unit) {
        val todoToRemove = todos.find { it._id == id }!!
        todos -= todoToRemove
        writeTodos()
        deletedTodos += todoToRemove
        writeDeletedTodos()
        callback(todoToRemove)
    }

    override fun updateTodo(id: String, todo: Todo, callback: (updatedTodo: Todo?) -> Unit) {
        val todoToRemove = todos.find { it._id == id }!!
        todos -= todoToRemove
        todos += todo
        writeTodos()
        callback(todo)
    }

    fun getDeletedTodos(callback: (deletedTodos: Array<Todo>) -> Unit) {
        callback(deletedTodos.toTypedArray())
    }

    private fun writeTodos() {
        localStorage[KEY_TODOS] = JSON.stringify(todos.toTypedArray())
    }

    private fun writeDeletedTodos() {
        localStorage[KEY_DELETED_TODOS] = JSON.stringify(deletedTodos.toTypedArray())
    }

    private object IdGenerator {
        private const val HEX_CHARS = "0123456789abcdef"
        private const val TARGET_LENGTH = 24

        fun get(): String {
            val id = (0..TARGET_LENGTH).map {
                val index = randomInt(0, HEX_CHARS.length - 1)
                HEX_CHARS[index]
            }
            return id.joinToString(separator = "")
        }

        private fun randomInt(min: Int, max: Int)
                = Math.floor(Math.random() * (max - min + 1)) + min
    }

}
