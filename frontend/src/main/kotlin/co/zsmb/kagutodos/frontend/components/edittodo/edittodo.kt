package co.zsmb.kagutodos.frontend.components.edittodo

import co.zsmb.kagu.core.Component
import co.zsmb.kagu.core.Controller
import co.zsmb.kagu.core.di.inject
import co.zsmb.kagu.core.dom.onClick
import co.zsmb.kagu.core.lookup
import co.zsmb.kagu.services.navigation.Navigator
import co.zsmb.kagu.services.pathparams.PathParams
import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.store.repository.TodoRepository
import co.zsmb.kagutodos.frontend.util.hide
import co.zsmb.kagutodos.frontend.util.show
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement

object EditTodoComponent : Component(
        selector = "edit-todo",
        templateUrl = "components/edittodo/edittodo.html",
        controller = ::EditTodoController
)

class EditTodoController : Controller() {

    val repo by inject<TodoRepository>()
    val navigator by inject<Navigator>()
    val params by inject<PathParams>()

    val todoText by lookup<HTMLInputElement>("todoText")
    val titleError by lookup<HTMLDivElement>("titleError")
    val todoDescription by lookup<HTMLTextAreaElement>("todoDescription")
    val completedCheckbox by lookup<HTMLInputElement>("completedCheckbox")

    val todoId by lazy {
        params.getString("todoId")
                ?: throw RuntimeException("Invalid todo id, should have thought about this")
    }

    val saveButton by lookup<HTMLButtonElement>("saveButton")

    var todo: Todo? = null

    override fun onCreate() {
        super.onCreate()

        saveButton.onClick {
            val newTodo = getTodo() ?: return@onClick
            repo.updateTodo(newTodo) { savedTodo ->
                navigator.goto("/view/${savedTodo._id}")
            }
        }
    }

    override fun onAdded() {
        super.onAdded()

        repo.getTodo(todoId) { todo ->
            this.todo = todo
            todoText.value = todo.text
            todoDescription.value = todo.description ?: ""
            completedCheckbox.checked = todo.completed
        }
    }

    private fun getTodo(): Todo? {
        val current = todo ?: return null

        val title = todoText.value
        val description = todoDescription.value
        val completed = completedCheckbox.checked

        if (title.isBlank()) {
            titleError.show()
            todoText.classList.add("is-invalid")

            todoText.onkeypress = {
                if (todoText.value.isNotBlank()) {
                    titleError.hide()
                    todoText.classList.remove("is-invalid")
                    todoText.onkeypress = null
                }
            }
            return null
        }

        return current.copy(text = title, description = description, completed = completed)
    }

}
