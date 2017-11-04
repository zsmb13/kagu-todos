package co.zsmb.kagutodos.frontend.components.listtodos

import co.zsmb.kagutodos.frontend.store.repository.TodoRepositoryImpl
import co.zsmb.kagutodos.frontend.util.removeChildren
import co.zsmb.weblib.core.Component
import co.zsmb.weblib.core.Controller
import co.zsmb.weblib.core.di.inject
import co.zsmb.weblib.core.util.lookup
import co.zsmb.weblib.core.util.onClick
import co.zsmb.weblib.services.navigation.Navigator
import co.zsmb.weblib.services.templates.TemplateLoader
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement

object ListTodosComponent : Component(
        selector = "list-todos",
        templateUrl = "components/listtodos/listtodos.html",
        controller = ::ListTodosController
)

class ListTodosController : Controller() {

    private val repo by inject<TodoRepositoryImpl>()
    private val templateLoader by inject<TemplateLoader>()
    private val navigator by inject<Navigator>()

    private val refreshBtn by lookup<HTMLButtonElement>("refreshBtn")
    private val todoList by lookup<HTMLElement>("todoList")

    override fun onCreate() {
        super.onCreate()

        refreshBtn.onClick {
            refreshTodos()
            refreshBtn.blur()
        }
    }

    override fun onAdded() {
        super.onAdded()
        refreshTodos()
    }

    private fun refreshTodos() {
        repo.getTodos { todos ->
            todoList.removeChildren()
            todos.forEach { todo ->
                templateLoader.get("components/listtodos/listitem.html") { listItem ->
                    listItem.textContent = todo.text
                    listItem.onClick {
                        it.preventDefault()
                        navigator.goto("/view/${todo._id}")
                    }
                    todoList.appendChild(listItem)
                }
            }
        }
    }

}
