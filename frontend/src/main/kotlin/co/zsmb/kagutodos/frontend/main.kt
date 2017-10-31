package co.zsmb.kagutodos.frontend

import co.zsmb.kagutodos.frontend.components.addtodo.AddTodoComponent
import co.zsmb.kagutodos.frontend.components.home.HomeComponent
import co.zsmb.kagutodos.frontend.components.listtodos.ListTodosComponent
import co.zsmb.kagutodos.frontend.services.network.NetworkModule
import co.zsmb.weblib.core.init.application


fun main(args: Array<String>) = application {

    components {
        +HomeComponent
        +AddTodoComponent
        +ListTodosComponent
    }

    modules {
        +NetworkModule
    }

    routing {
        defaultState {
            path = "/home"
            handler = HomeComponent
        }
    }

}
