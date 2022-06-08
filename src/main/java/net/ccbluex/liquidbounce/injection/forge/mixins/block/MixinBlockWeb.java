package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import net.minecraft.block.BlockWeb;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import lol.liquidcat.features.module.modules.movement.NoSlow;

@Mixin(BlockWeb.class)
@SideOnly(Side.CLIENT)
public class MixinBlockWeb {

    @Inject(method = "onEntityCollidedWithBlock", at = @At("HEAD"), cancellable = true)
    private void onEntityCollidedWithBlock(CallbackInfo callbackInfo) {
        final NoSlow noSlow = NoSlow.INSTANCE;

        if (noSlow.getState() && noSlow.getWeb())
            callbackInfo.cancel();
    }
}