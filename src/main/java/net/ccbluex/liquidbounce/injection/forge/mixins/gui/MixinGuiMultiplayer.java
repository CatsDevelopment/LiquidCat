/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import lol.liquidcat.features.misc.BungeeCordSpoof;
import lol.liquidcat.file.FileManager;
import lol.liquidcat.file.configs.ValuesConfig;
import lol.liquidcat.ui.client.guis.GuiAntiForge;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends MixinGuiScreen {

    private GuiButton bungeeCordSpoofButton;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        buttonList.add(new GuiButton(997, 5, 8, 98, 20, "AntiForge"));
        buttonList.add(bungeeCordSpoofButton = new GuiButton(998, 108, 8, 98, 20, "BungeeCord Spoof: " + (BungeeCordSpoof.enabled ? "On" : "Off")));
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        switch(button.id) {
            case 997:
                mc.displayGuiScreen(new GuiAntiForge((GuiScreen) (Object) this));
                break;
            case 998:
                BungeeCordSpoof.enabled = !BungeeCordSpoof.enabled;
                bungeeCordSpoofButton.displayString = "BungeeCord Spoof: " + (BungeeCordSpoof.enabled ? "On" : "Off");
                FileManager.INSTANCE.saveConfig(ValuesConfig.INSTANCE, false);
                break;
        }
    }
}