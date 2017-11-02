package co.zsmb.kagutodos.frontend.store.repository

import co.zsmb.kagutodos.frontend.model.Todo

interface TodoRepository {

    fun getTodos(callback: (Array<Todo>) -> Unit)

    fun getTodo(id: String, callback: (Todo) -> Unit)

    fun addTodo(todo: Todo, callback: (Todo) -> Unit)

    fun removeTodo(id: String, callback: (Todo) -> Unit)

    fun updateTodo(id: String, todo: Todo, callback: (Todo) -> Unit)

}