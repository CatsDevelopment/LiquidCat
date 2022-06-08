/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import net.minecraft.client.renderer.chunk.VisGraph;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import lol.liquidcat.features.module.modules.render.XRay;

@Mixin(VisGraph.class)
public class MixinVisGraph {

    @Inject(method = "func_178606_a", at = @At("HEAD"), cancellable = true)
    private void func_178606_a(final CallbackInfo callbackInfo) {
        if (XRay.INSTANCE.getState())
            callbackInfo.cancel();
    }
}