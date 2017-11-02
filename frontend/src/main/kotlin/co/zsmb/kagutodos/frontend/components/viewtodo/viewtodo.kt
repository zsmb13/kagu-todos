package co.zsmb.kagutodos.frontend.components.viewtodo

import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.store.repository.TodoRepositoryImpl
import co.zsmb.weblib.core.Component
import co.zsmb.weblib.core.Controller
import co.zsmb.weblib.core.di.inject
import co.zsmb.weblib.core.util.lookup
import co.zsmb.weblib.core.util.onClick
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
    val idButton by lookup<HTMLButtonElement>("idButton")
    val description by lookup<HTMLParagraphElement>("description")

    val repo by inject<TodoRepositoryImpl>()
    val params by inject<PathParams>()

    val todoId by lazy {
        params.getString("todoId")
                ?: throw RuntimeException("Invalid todo id, should have thought about this")
    }

    lateinit var todo: Todo

    override fun onCreate() {
        super.onCreate()

        idButton.innerText = todoId

        statusButton.onClick {
            toggleCompletion()
        }
    }

    override fun onAdded() {
        super.onAdded()

        repo.getTodo(todoId) { todo ->
            this.todo = todo
            displayTodo()
        }
    }

    private fun displayTodo() {
        nameSpan.innerText = todo.text
        description.innerText = todo.description ?: "This item has no description."

        statusButton.classList.remove(
                "btn-outline-secondary",
                "btn-outline-success",
                "btn-outline-danger")
        if (todo.completed) {
            statusButton.classList.add("btn-outline-success")
            statusButton.textContent = "Completed"
        } else {
            statusButton.classList.add("btn-outline-danger")
            statusButton.textContent = "Not completed"
        }
    }

    private fun toggleCompletion() {
        todo = todo.copy(completed = !todo.completed)

        repo.updateTodo(todoId, todo) {
            displayTodo()
        }
    }

}
