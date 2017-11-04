package co.zsmb.kagutodos.frontend.store.repository

import co.zsmb.kagutodos.frontend.model.Todo

interface TodoAPI {

    fun setTodos(todos: Array<Todo>, callback: (success: Boolean) -> Unit)

    fun getTodos(callback: (todos: Array<Todo>?) -> Unit)

    fun getTodo(id: String, callback: (todo: Todo?) -> Unit)

    fun addTodo(todo: Todo, callback: (addedTodo: Todo?) -> Unit)

    fun removeTodo(id: String, callback: (removedTodo: Todo?) -> Unit)

    fun updateTodo(id: String, todo: Todo, callback: (updatedTodo: Todo?) -> Unit)

}
