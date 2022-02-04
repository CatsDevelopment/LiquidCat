/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import lol.liquidcat.event.*;
import lol.liquidcat.LiquidCat;
import lol.liquidcat.features.module.Module;
import lol.liquidcat.features.module.ModuleCategory;
import lol.liquidcat.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner;
import lol.liquidcat.value.BoolValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.RENDER, array = false)
@SideOnly(Side.CLIENT)
public class HUD extends Module {
    public final BoolValue blackHotbarValue = new BoolValue("BlackHotbar", true);
    public final BoolValue inventoryParticle = new BoolValue("InventoryParticle", false);
    private final BoolValue blurValue = new BoolValue("Blur", false);
    public final BoolValue fontChatValue = new BoolValue("FontChat", false);

    public HUD() {
        setState(true);
    }

    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        if (mc.currentScreen instanceof GuiHudDesigner)
            return;

        LiquidCat.hud.render(false);
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        LiquidCat.hud.update();
    }

    @EventTarget
    public void onKey(final KeyEvent event) {
        LiquidCat.hud.handleKey('a', event.getKey());
    }

    @EventTarget(ignoreCondition = true)
    public void onScreen(final ScreenEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null)
            return;

        if (getState() && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.getGuiScreen() != null &&
                !(event.getGuiScreen() instanceof GuiChat || event.getGuiScreen() instanceof GuiHudDesigner))
            mc.entityRenderer.loadShader(new ResourceLocation(LiquidCat.CLIENT_NAME.toLowerCase() + "/blur.json"));
        else if (mc.entityRenderer.getShaderGroup() != null &&
                mc.entityRenderer.getShaderGroup().getShaderGroupName().contains("liquidbounce/blur.json"))
            mc.entityRenderer.stopUseShader();
    }
}
