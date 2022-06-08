/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.network;

import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerLoginClient.class)
@SideOnly(Side.CLIENT)
public class MixinNetHandlerLoginClient {

    @Shadow
    @Final
    private NetworkManager networkManager;

    @Inject(method = "handleEncryptionRequest", at = @At("HEAD"), cancellable = true)
    private void handleEncryptionRequest(S01PacketEncryptionRequest packetIn, CallbackInfo callbackInfo) {

    }
}