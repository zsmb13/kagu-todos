package co.zsmb.kagutodos.frontend.store

import co.zsmb.kagutodos.frontend.store.local.LocalTodoAPI
import co.zsmb.kagutodos.frontend.store.network.NetworkTodoAPI
import co.zsmb.kagutodos.frontend.store.repository.TodoRepository
import co.zsmb.kagutodos.frontend.store.repository.TodoRepositoryImpl
import co.zsmb.koinjs.dsl.module.Module
import co.zsmb.weblib.services.http.HttpService
import co.zsmb.weblib.services.storage.LocalStorage

object StorageModule : Module() {

    override fun context() = declareContext {
        provide { createNetworkTodoAPI(get()) }
        provide { createLocalTodoAPI(get()) }
        provide { createTodoRepository(get(), get()) }
    }

    private fun createNetworkTodoAPI(httpService: HttpService) = NetworkTodoAPI(httpService)

    private fun createLocalTodoAPI(localStorage: LocalStorage) = LocalTodoAPI(localStorage)

    private fun createTodoRepository(networkApi: NetworkTodoAPI, localApi: LocalTodoAPI): TodoRepository
            = TodoRepositoryImpl(localApi, networkApi)

}
