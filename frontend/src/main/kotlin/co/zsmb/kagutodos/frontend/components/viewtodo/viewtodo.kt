package co.zsmb.kagutodos.frontend.components.viewtodo

import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.store.repository.TodoRepository
import co.zsmb.weblib.core.Component
import co.zsmb.weblib.core.Controller
import co.zsmb.weblib.core.di.inject
import co.zsmb.weblib.core.dom.onClick
import co.zsmb.weblib.core.lookup
import co.zsmb.weblib.services.navigation.Navigator
import co.zsmb.weblib.services.pathparams.PathParams
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.HTMLSpanElement

object ViewTodoComponent : Component(
        selector = "view-todo",
        templateUrl = "components/viewtodo/viewtodo.html",
        controller = ::ViewTodoController
)

class ViewTodoController : Controller() {

    val nameSpan by lookup<HTMLSpanElement>("nameSpan")
    val statusButton by lookup<HTMLButtonElement>("statusButton")
    val description by lookup<HTMLParagraphElement>("description")
    val editButton by lookup<HTMLButtonElement>("editButton")
    val removeButton by lookup<HTMLButtonElement>("removeButton")

    val repo by inject<TodoRepository>()
    val params by inject<PathParams>()
    val navigator by inject<Navigator>()

    val todoId by lazy {
        params.getString("todoId")
                ?: throw RuntimeException("Invalid todo id, should have thought about this")
    }

    var todo: Todo? = null

    override fun onCreate() {
        super.onCreate()

        statusButton.onClick { toggleCompletion() }
        editButton.onClick { editTodo() }
        removeButton.onClick { removeTodo() }
    }

    override fun onAdded() {
        super.onAdded()

        repo.getTodo(todoId) { todo ->
            this.todo = todo
            displayTodo()
        }
    }

    private fun displayTodo() {
        val curretTodo = todo ?: return

        nameSpan.innerText = curretTodo.text
        description.innerText = curretTodo.description ?: "This item has no description."

        statusButton.classList.remove(
                "btn-outline-secondary",
                "btn-outline-success",
                "btn-outline-danger")
        if (curretTodo.completed) {
            statusButton.classList.add("btn-outline-success")
            statusButton.textContent = "Completed"
        } else {
            statusButton.classList.add("btn-outline-danger")
            statusButton.textContent = "Not completed"
        }
    }

    private fun toggleCompletion() {
        todo?.let { currentTodo ->
            val modifiedTodo = currentTodo.copy(completed = !currentTodo.completed)

            repo.updateTodo(modifiedTodo) {
                todo = modifiedTodo
                displayTodo()
            }
        }
    }

    private fun editTodo() {
        todo?.let {
            navigator.goto("/edit/${it._id}")
        }
    }

    private fun removeTodo() {
        todo?._id?.let {
            repo.removeTodo(it) {
                navigator.goto("/")
            }
        }
    }

}
