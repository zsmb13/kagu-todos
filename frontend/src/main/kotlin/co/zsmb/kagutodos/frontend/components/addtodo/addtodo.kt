package co.zsmb.kagutodos.frontend.components.addtodo

import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.store.repository.TodoRepositoryImpl
import co.zsmb.kagutodos.frontend.util.hide
import co.zsmb.kagutodos.frontend.util.show
import co.zsmb.weblib.core.Component
import co.zsmb.weblib.core.Controller
import co.zsmb.weblib.core.di.inject
import co.zsmb.weblib.core.util.lookup
import co.zsmb.weblib.core.util.onClick
import co.zsmb.weblib.services.logging.Logger
import co.zsmb.weblib.services.navigation.Navigator
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

    val logger by inject<Logger>()
    val repo by inject<TodoRepositoryImpl>()
    val navigator by inject<Navigator>()

    val todoText by lookup<HTMLInputElement>("todoText")
    val titleError by lookup<HTMLDivElement>("titleError")
    val todoDescription by lookup<HTMLTextAreaElement>("todoDescription")
    val completedCheckbox by lookup<HTMLInputElement>("completedCheckbox")

    val addButton by lookup<HTMLButtonElement>("addButton")

    override fun onCreate() {
        super.onCreate()

        addButton.onClick {
            logger.d(this, "button clicked!")

            val newTodo = createTodo() ?: return@onClick
            repo.addTodo(newTodo) { todo ->
                logger.d(this, "todo back from repo after insert: ${JSON.stringify(todo)}")
                navigator.goto("/view/${todo._id}")
                clearInputs()
            }
        }
    }

    private fun createTodo(): Todo? {
        val title = todoText.value
        val description = todoDescription.value
        val completed = completedCheckbox.checked

        if (title.isBlank()) {
            logger.d(this, "title invalid")

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

        return Todo(title, description, completed)
    }

    private fun clearInputs() {
        todoText.value = ""
        todoDescription.value = ""
        completedCheckbox.checked = false
    }

}
