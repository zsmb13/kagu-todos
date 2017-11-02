package co.zsmb.kagutodos.frontend.model

data class Todo(val text: String,
                val description: String?,
                val completed: Boolean,
                val lastModified: Long? = null,
                val _id: String? = null)

fun Todo.fixMethods() = Todo(text, description, completed, lastModified, _id)
fun Array<Todo>.fixMethods() = map { it.fixMethods() }.toTypedArray()
