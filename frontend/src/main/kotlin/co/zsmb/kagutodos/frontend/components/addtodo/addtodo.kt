package co.zsmb.kagutodos.frontend.components.addtodo

import co.zsmb.kagutodos.frontend.model.Todo
import co.zsmb.kagutodos.frontend.services.network.TodoAPI
import co.zsmb.weblib.core.Component
import co.zsmb.weblib.core.Controller
import co.zsmb.weblib.core.di.inject
import co.zsmb.weblib.core.util.Date
import co.zsmb.weblib.core.util.lookup
import co.zsmb.weblib.core.util.onClick
import co.zsmb.weblib.services.logging.Logger
import org.w3c.dom.HTMLButtonElement

object AddTodoComponent : Component(
        selector = "add-todo",
        templateUrl = "components/addtodo/addtodo.html",
        controller = ::AddTodoController
)

class AddTodoController : Controller() {

    val logger by inject<Logger>()
    val todoAPI by inject<TodoAPI>()

    val btn by lookup<HTMLButtonElement>("addbtn")

    override fun onCreate() {
        super.onCreate()

        btn.onClick {
            val newTodo = Todo("new todo ${Date().getTime()}", false)
            todoAPI.addTodo(newTodo) { todo ->
                logger.d(this, "todo back from server ${todo.text}")
            }
        }
    }

}
