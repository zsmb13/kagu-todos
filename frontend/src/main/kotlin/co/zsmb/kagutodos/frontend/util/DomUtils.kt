package co.zsmb.kagutodos.frontend.util

import org.w3c.dom.HTMLElement
import org.w3c.dom.Node

fun Node.removeChildren() {
    while (hasChildNodes()) {
        removeChild(firstChild!!)
    }
}

fun HTMLElement.hide() {
    classList.add("invisible")
}

fun HTMLElement.show() {
    classList.remove("invisible")
}
