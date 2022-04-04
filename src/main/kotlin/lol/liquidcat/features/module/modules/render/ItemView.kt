package lol.liquidcat.features.module.modules.render

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.FloatValue

object ItemView : Module("ItemView", "Changes the look of the item in your hand.", ModuleCategory.RENDER) {

    val x by FloatValue("X", 0.56f, -1f..1f)
    val y by FloatValue("Y", -0.52f, -1f..1f)
    val z by FloatValue("Z", -0.71999997f, -1f..1f)

    val scale by FloatValue("Scale", 0.4f, 0.1f..3f)
}