package co.zsmb.kagutodos.frontend.components.home

import co.zsmb.kagutodos.frontend.services.network.TodoAPI
import co.zsmb.weblib.core.Component
import co.zsmb.weblib.core.Controller
import co.zsmb.weblib.core.di.inject
import co.zsmb.weblib.core.util.lookup
import co.zsmb.weblib.core.util.onClick
import co.zsmb.weblib.services.logging.Logger
import org.w3c.dom.HTMLButtonElement

object HomeComponent : Component(
        selector = "home",
        templateUrl = "components/home/home.html",
        controller = ::HomeController
)

class HomeController : Controller() {

    val logger by inject<Logger>()
    val todoAPI by inject<TodoAPI>()

    val btn1 by lookup<HTMLButtonElement>("btn1")
    val todos by lookup<HTMLButtonElement>("todos")

    override fun onCreate() {
        super.onCreate()

        logger.d(this, "onCreate!")

        btn1.onClick {
            todoAPI.getTodos { todos ->
                todos.forEach {
                    logger.d(this, "TODO: ${it.text}, completed: ${it.completed}")
                }
            }
        }
    }

}
