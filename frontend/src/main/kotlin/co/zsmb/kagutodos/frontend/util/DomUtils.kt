package co.zsmb.kagutodos.frontend.util

import org.w3c.dom.Node

fun Node.removeChildren() {
    while (hasChildNodes()) {
        removeChild(firstChild!!)
    }
}
