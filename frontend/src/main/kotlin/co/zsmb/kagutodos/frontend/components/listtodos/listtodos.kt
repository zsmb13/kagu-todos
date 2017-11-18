package co.zsmb.kagutodos.frontend.components.listtodos

import co.zsmb.kagu.core.Component
import co.zsmb.kagu.core.Controller
import co.zsmb.kagu.core.di.inject
import co.zsmb.kagu.core.dom.onClick
import co.zsmb.kagu.core.lookup
import co.zsmb.kagu.services.messaging.MessageBroker
import co.zsmb.kagu.services.messaging.MessageCallback
import co.zsmb.kagu.services.navigation.Navigator
import co.zsmb.kagu.services.templates.TemplateLoader
import co.zsmb.kagutodos.frontend.store.repository.TodoRepository
import co.zsmb.kagutodos.frontend.util.removeChildren
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement

object ListTodosComponent : Component(
        selector = "list-todos",
        templateUrl = "components/listtodos/listtodos.html",
        controller = ::ListTodosController
)

class ListTodosController : Controller() {

    private val repo by inject<TodoRepository>()
    private val templateLoader by inject<TemplateLoader>()
    private val navigator by inject<Navigator>()
    private val messageBroker by inject<MessageBroker>()

    private val refreshBtn by lookup<HTMLButtonElement>("refreshBtn")
    private val todoList by lookup<HTMLElement>("todoList")

    private val todosChangedCallback: MessageCallback = {
        refreshTodos()
    }

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
        messageBroker.subscribe("TODOS_CHANGED", todosChangedCallback)
    }

    override fun onRemoved() {
        messageBroker.unsubscribe("TODOS_CHANGED", todosChangedCallback)
        super.onRemoved()
    }

    private fun refreshTodos() {
        repo.getTodos { todos ->
            todoList.removeChildren()
            todos.sortedBy { it.text }.forEach { todo ->
                templateLoader.get("components/listtodos/listitem.html") { listItem ->
                    listItem.textContent = todo.text
                    val colorClass = if (todo.completed) "list-group-item-success" else "list-group-item-danger"
                    listItem.classList.add(colorClass)
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
