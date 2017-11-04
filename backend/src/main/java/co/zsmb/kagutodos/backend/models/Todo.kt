package co.zsmb.kagutodos.backend.models

data class Todo(val text: String,
                val description: String?,
                val completed: Boolean,
                val lastModified: Long? = null,
                val _id: String? = null)
