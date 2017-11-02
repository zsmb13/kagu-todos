package co.zsmb.kagutodos.frontend.model

data class Todo(val text: String,
                val description: String?,
                val completed: Boolean,
                val _id: String? = null)

fun Todo.fixMethods(): Todo = Todo(text, description, completed, _id)
