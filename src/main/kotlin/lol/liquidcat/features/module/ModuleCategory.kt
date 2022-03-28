/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module

enum class ModuleCategory(val displayName: String) {

    /**
     * Category for modules related to combat
     */
    COMBAT("Combat"),

    /**
     * Category for different modules related to the player
     */
    PLAYER("Player"),

    /**
     * Category for modules related to change the player's movement
     */
    MOVEMENT("Movement"),

    /**
     * Category for visual modules
     */
    RENDER("Render"),

    /**
     * Category for modules that interact with the world
     */
    WORLD("World"),

    /**
     * Category for modules that does not fit in other categories
     */
    MISC("Misc"),

    /**
     * Category for modules that exploit server vulnerabilities
     */
    EXPLOIT("Exploit"),

    /**
     * Category for modules created for fun
     */
    FUN("Fun")
}