package co.zsmb.kagutodos.frontend.services.network

import co.zsmb.koinjs.dsl.module.Module
import co.zsmb.weblib.services.http.HttpService

object NetworkModule : Module() {

    override fun context() = declareContext {
        provide { createTodoAPI(get()) }
    }

    private fun createTodoAPI(httpService: HttpService): TodoAPI = TodoAPIImpl(httpService)

}

