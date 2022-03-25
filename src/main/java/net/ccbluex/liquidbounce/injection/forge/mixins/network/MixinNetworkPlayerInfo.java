package net.ccbluex.liquidbounce.injection.forge.mixins.network;

import com.mojang.authlib.GameProfile;
import lol.liquidcat.LiquidCat;
import lol.liquidcat.features.module.modules.misc.NameProtect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(NetworkPlayerInfo.class)
public class MixinNetworkPlayerInfo {
    @Shadow
    @Final
    private GameProfile gameProfile;

    @Inject(method = "getLocationSkin", cancellable = true, at = @At("HEAD"))
    private void injectSkinProtect(CallbackInfoReturnable<ResourceLocation> cir) {
        NameProtect nameProtect = NameProtect.INSTANCE;

        if (nameProtect.getState() && nameProtect.getSkinProtect()) {
            if (nameProtect.getAllPlayers() || Objects.equals(gameProfile.getId(), Minecraft.getMinecraft().getSession().getProfile().getId())) {
                cir.setReturnValue(DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
                cir.cancel();
            }
        }
    }
}