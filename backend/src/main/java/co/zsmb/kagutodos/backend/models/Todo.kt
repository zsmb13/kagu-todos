package co.zsmb.kagutodos.backend.models

data class Todo(val text: String, val completed: Boolean, val id: Long? = null)
