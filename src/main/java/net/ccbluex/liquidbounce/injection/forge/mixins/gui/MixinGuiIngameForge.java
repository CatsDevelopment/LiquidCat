/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer;
import net.minecraftforge.client.GuiIngameForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import lol.liquidcat.event.EventManager;
import lol.liquidcat.event.Render2DEvent;

@Mixin(GuiIngameForge.class)
abstract class MixinGuiIngameForge {

    @Inject(method = "renderGameOverlay", at = @At("RETURN"))
    public void handleGameOverlay(float partialTicks, CallbackInfo ci) {
        EventManager.callEvent(new Render2DEvent(partialTicks));
        AWTFontRenderer.Companion.garbageCollectionTick();
    }
}