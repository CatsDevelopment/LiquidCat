/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.ui.client.clickgui.elements;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import lol.liquidcat.LiquidCat;

@SideOnly(Side.CLIENT)
public class ButtonElement extends Element {

    protected String displayName;
    protected int color = 0xffffff;

    public int hoverTime;

    public ButtonElement(String displayName) {
        createButton(displayName);
    }

    public void createButton(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float button) {
        LiquidCat.clickGui.style.drawButtonElement(mouseX, mouseY, this);
        super.drawScreen(mouseX, mouseY, button);
    }

    @Override
    public int getHeight() {
        return 16;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + 16;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
