package net.ccbluex.liquidbounce.injection.forge.mixins.resources;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import lol.liquidcat.features.module.modules.misc.NameProtect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mixin(SkinManager.class)
public class MixinSkinManager {

    @Inject(method = "loadSkinFromCache", cancellable = true, at = @At("HEAD"))
    private void injectSkinProtect(GameProfile gameProfile, CallbackInfoReturnable<Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> cir) {
        if (gameProfile == null)
            return;
        
        NameProtect nameProtect = NameProtect.INSTANCE;

        if (nameProtect.getState() && nameProtect.getSkinProtect()) {
            if (nameProtect.getAllPlayers() || Objects.equals(gameProfile.getId(), Minecraft.getMinecraft().getSession().getProfile().getId())) {
                cir.setReturnValue(new HashMap<>());
                cir.cancel();
            }
        }
    }
}