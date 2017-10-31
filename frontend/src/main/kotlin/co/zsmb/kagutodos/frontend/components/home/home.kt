package co.zsmb.kagutodos.frontend.components.home

import co.zsmb.weblib.core.Component
import co.zsmb.weblib.core.Controller

object HomeComponent : Component(
        selector = "home",
        templateUrl = "components/home/home.html",
        controller = ::HomeController
)

class HomeController : Controller() {


}
