package co.zsmb.kagutodos.frontend.components.listtodos

import co.zsmb.kagutodos.frontend.services.network.TodoAPI
import co.zsmb.kagutodos.frontend.util.removeChildren
import co.zsmb.weblib.core.Component
import co.zsmb.weblib.core.Controller
import co.zsmb.weblib.core.di.inject
import co.zsmb.weblib.core.util.lookup
import co.zsmb.weblib.core.util.onClick
import co.zsmb.weblib.services.logging.Logger
import co.zsmb.weblib.services.templates.TemplateLoader
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLUListElement

object ListTodosComponent : Component(
        selector = "list-todos",
        templateUrl = "components/listtodos/listtodos.html",
        controller = ::ListTodosController
)

class ListTodosController : Controller() {

    private val logger by inject<Logger>()
    private val todoAPI by inject<TodoAPI>()
    private val templateLoader by inject<TemplateLoader>()

    private val refreshBtn by lookup<HTMLButtonElement>("refreshBtn")
    private val todoList by lookup<HTMLUListElement>("todoList")

    override fun onCreate() {
        super.onCreate()

        refreshBtn.onClick {
            refreshTodos()
        }
    }

    override fun onAdded() {
        super.onAdded()
        logger.d(this, "onAdded, refreshing todos")
        refreshTodos()
    }

    private fun refreshTodos() {
        todoAPI.getTodos { todos ->
            todoList.removeChildren()
            todos.forEach {
                templateLoader.get("components/listtodos/listitem.html") { listItem ->
                    listItem.textContent = it.text
                    todoList.appendChild(listItem)
                }
            }
        }
    }

}
