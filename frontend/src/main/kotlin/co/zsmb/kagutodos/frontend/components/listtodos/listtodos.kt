package co.zsmb.kagutodos.frontend.components.listtodos

import co.zsmb.kagutodos.frontend.services.network.TodoAPI
import co.zsmb.kagutodos.frontend.util.removeChildren
import co.zsmb.weblib.core.Component
import co.zsmb.weblib.core.Controller
import co.zsmb.weblib.core.di.inject
import co.zsmb.weblib.core.jquery.JQ
import co.zsmb.weblib.core.util.lookup
import co.zsmb.weblib.core.util.onClick
import co.zsmb.weblib.services.logging.Logger
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLUListElement
import kotlin.dom.addClass

object ListTodosComponent : Component(
        selector = "list-todos",
        templateUrl = "components/listtodos/listtodos.html",
        controller = ::ListTodosController
)

class ListTodosController : Controller() {

    val logger by inject<Logger>()
    val todoAPI by inject<TodoAPI>()

    val btn1 by lookup<HTMLButtonElement>("btn1")
    val todoList by lookup<HTMLUListElement>("todos")

    override fun onCreate() {
        super.onCreate()
        logger.d(this, "onCreate")

        btn1.onClick {
            todoAPI.getTodos { todos ->
                todoList.removeChildren()
                todos.forEach {
                    val listItem = JQ.parseHTML("<li></li>")[0] as Element
                    listItem.addClass("list-group-item info")
                    listItem.textContent = it.text
                    todoList.appendChild(listItem)
                }
            }
        }
    }

}
