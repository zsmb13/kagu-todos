package co.zsmb.kagutodos.backend.util

import io.vertx.core.json.JsonObject

class JsonBuilder {

    private val jsonObject = JsonObject()

    infix fun String.to(obj: Any?) {
        jsonObject.put(this, obj)
    }

    fun build() = jsonObject

}

fun json(setup: JsonBuilder.() -> Unit) = JsonBuilder().apply(setup).build()

fun jsonStr(setup: JsonBuilder.() -> Unit) = json(setup).encode()

