package co.zsmb.kagutodos.frontend

import co.zsmb.kagu.core.init.application
import co.zsmb.kagutodos.frontend.components.addtodo.AddTodoComponent
import co.zsmb.kagutodos.frontend.components.edittodo.EditTodoComponent
import co.zsmb.kagutodos.frontend.components.listtodos.ListTodosComponent
import co.zsmb.kagutodos.frontend.components.viewtodo.ViewTodoComponent
import co.zsmb.kagutodos.frontend.store.StorageModule


fun main(args: Array<String>) = application {

    components {
    }

    modules {
        +StorageModule
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
        state {
            path = "/edit/:todoId"
            handler = EditTodoComponent
        }
    }

}
