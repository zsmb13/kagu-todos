package co.zsmb.kagutodos.frontend.components.addtodo

import co.zsmb.kagu.core.Component
import co.zsmb.kagu.core.Controller
import co.zsmb.kagu.core.di.inject
import co.zsmb.kagu.core.dom.onClick
import co.zsmb.kagu.core.dom.onKeyPress
import co.zsmb.kagu.core.lookup
import co.zsmb.kagu.services.navigation.Navigator
import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.store.repository.TodoRepository
import co.zsmb.kagutodos.frontend.util.hide
import co.zsmb.kagutodos.frontend.util.show
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement

object AddTodoComponent : Component(
        selector = "add-todo",
        templateUrl = "components/addtodo/addtodo.html",
        controller = ::AddTodoController
)

class AddTodoController : Controller() {

    val repo by inject<TodoRepository>()
    val navigator by inject<Navigator>()

    val todoText by lookup<HTMLInputElement>("todoText")
    val titleError by lookup<HTMLDivElement>("titleError")
    val todoDescription by lookup<HTMLTextAreaElement>("todoDescription")
    val completedCheckbox by lookup<HTMLInputElement>("completedCheckbox")

    val addButton by lookup<HTMLButtonElement>("addButton")

    override fun onCreate() {
        super.onCreate()

        addButton.onClick {
            val newTodo = createTodo() ?: return@onClick
            repo.addTodo(newTodo) { todo ->
                navigator.goto("/view/${todo._id}")
            }
        }
    }

    override fun onAdded() {
        super.onAdded()
        clearInputs()
    }

    private fun createTodo(): Todo? {
        val title = todoText.value
        val description = todoDescription.value
        val completed = completedCheckbox.checked

        if (title.isBlank()) {
            titleError.show()
            todoText.classList.add("is-invalid")
            todoText.onKeyPress {
                if (todoText.value.isNotBlank()) {
                    titleError.hide()
                    todoText.classList.remove("is-invalid")
                    todoText.onkeypress = null
                }
            }
            return null
        }

        return Todo(title, description, completed)
    }

    private fun clearInputs() {
        todoText.value = ""
        todoDescription.value = ""
        completedCheckbox.checked = false
    }

}
