/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import lol.liquidcat.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat extends Gui {

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    public abstract int getLineCount();

    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;

    @Shadow
    public abstract boolean getChatOpen();

    @Shadow
    public abstract float getChatScale();

    @Shadow
    public abstract int getChatWidth();

    @Shadow
    private int scrollPos;

    @Shadow
    private boolean isScrolled;

    @Inject(method = "drawChat", at = @At("HEAD"), cancellable = true)
    private void drawChat(int updateCounter, CallbackInfo ci) {
        final HUD hud = HUD.INSTANCE;

        if (hud.getState()) {
            ci.cancel();

            if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
                int i = this.getLineCount();
                boolean flag = false;
                int j = 0;
                int k = this.drawnChatLines.size();
                float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
                if (k > 0) {
                    if (this.getChatOpen()) {
                        flag = true;
                    }

                    float f1 = this.getChatScale();
                    int l = MathHelper.ceiling_float_int((float)this.getChatWidth() / f1);
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(2.0F, 20.0F, 0.0F);
                    GlStateManager.scale(f1, f1, 1.0F);

                    int i1;
                    int j1;
                    int l1;
                    for(i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1) {
                        ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);
                        if (chatline != null) {
                            j1 = updateCounter - chatline.getUpdatedCounter();
                            if (j1 < 200 || flag) {
                                double d0 = (double)j1 / 200.0D;
                                d0 = 1.0D - d0;
                                d0 *= 10.0D;
                                d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D);
                                d0 *= d0;
                                l1 = (int)(255.0D * d0);
                                if (flag) {
                                    l1 = 255;
                                }

                                l1 = (int)((float)l1 * f);
                                ++j;
                                if (l1 > 3) {
                                    int i2 = 0;
                                    int j2 = -i1 * 9;

                                    if (!hud.getCleanChat())
                                        drawRect(i2, j2 - 9, i2 + l + 4, j2, l1 / 2 << 24);

                                    String s = chatline.getChatComponent().getFormattedText();
                                    GlStateManager.enableBlend();

                                    if (hud.getFontChat())
                                        Fonts.nunito.drawStringWithShadow(s, (float)i2, (float)(j2 - 8), 16777215 + (l1 << 24));
                                    else
                                        this.mc.fontRendererObj.drawStringWithShadow(s, (float)i2, (float)(j2 - 8), 16777215 + (l1 << 24));

                                    GlStateManager.disableAlpha();
                                    GlStateManager.disableBlend();
                                }
                            }
                        }
                    }

                    if (flag) {
                        i1 = hud.getFontChat() ? Fonts.nunito.FONT_HEIGHT : this.mc.fontRendererObj.FONT_HEIGHT;
                        GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                        int l2 = k * i1 + k;
                        j1 = j * i1 + j;
                        int j3 = this.scrollPos * j1 / k;
                        int k1 = j1 * j1 / l2;
                        if (l2 != j1) {
                            l1 = j3 > 0 ? 170 : 96;
                            int l3 = this.isScrolled ? 13382451 : 3355562;
                            drawRect(0, -j3, 2, -j3 - k1, l3 + (l1 << 24));
                            drawRect(2, -j3, 1, -j3 - k1, 13421772 + (l1 << 24));
                        }
                    }

                    GlStateManager.popMatrix();
                }
            }
        }
    }
}