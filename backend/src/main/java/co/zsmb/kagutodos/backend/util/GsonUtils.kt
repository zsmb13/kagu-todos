package co.zsmb.kagutodos.backend.util

import com.google.gson.Gson
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject

object GsonHolder {
    val gson = Gson()
}

fun Any.toJson() = GsonHolder.gson.toJson(this)

fun Any.toJsonObject() = JsonObject(GsonHolder.gson.toJson(this))

inline fun <reified T> String.parseJson(): T = GsonHolder.gson.fromJson<T>(this, T::class.java)

inline fun <reified T> JsonObject.parse(): T = GsonHolder.gson.fromJson<T>(this.encode(), T::class.java)

inline fun <reified T> Message<String>.parseJson(): T = GsonHolder.gson.fromJson<T>(this.body(), T::class.java)
