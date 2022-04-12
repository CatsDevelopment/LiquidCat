package lol.liquidcat.ui.client.hud.element

/**
 * CustomHUD Side
 *
 * Allows to change default x and y position by side
 */
class Side(var horizontal: Horizontal = Horizontal.LEFT, var vertical: Vertical = Vertical.UP) {

    /**
     * Horizontal side
     */
    enum class Horizontal(val sideName: String) {

        LEFT("Left"),
        MIDDLE("Middle"),
        RIGHT("Right");

        companion object {

            @JvmStatic
            fun byName(name: String) = values().find { it.sideName == name }
        }
    }

    /**
     * Vertical side
     */
    enum class Vertical(val sideName: String) {

        UP("Up"),
        MIDDLE("Middle"),
        DOWN("Down");

        companion object {

            @JvmStatic
            fun byName(name: String) = values().find { it.sideName == name }
        }
    }
}