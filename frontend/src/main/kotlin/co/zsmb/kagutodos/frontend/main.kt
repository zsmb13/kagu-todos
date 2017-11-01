package co.zsmb.kagutodos.frontend

import co.zsmb.kagutodos.frontend.components.addtodo.AddTodoComponent
import co.zsmb.kagutodos.frontend.components.listtodos.ListTodosComponent
import co.zsmb.kagutodos.frontend.components.viewtodo.ViewTodoComponent
import co.zsmb.kagutodos.frontend.services.network.NetworkModule
import co.zsmb.weblib.core.init.application


fun main(args: Array<String>) = application {

    components {
    }

    modules {
        +NetworkModule
    }

    routing {
        defaultState {
            path = "/"
            handler = ListTodosComponent
        }
        state {
            path = "/add"
            handler = AddTodoComponent
        }
        state {
            path = "/view/:todoId"
            handler = ViewTodoComponent
        }
    }

}
