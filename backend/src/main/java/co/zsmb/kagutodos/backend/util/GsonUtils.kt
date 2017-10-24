package co.zsmb.kagutodos.backend.util

import com.google.gson.Gson

object GsonHolder {
    val gson = Gson()
}

fun Any.toJson() = GsonHolder.gson.toJson(this)

inline fun <reified T> String.parseJson(): T = GsonHolder.gson.fromJson<T>(this, T::class.java)
