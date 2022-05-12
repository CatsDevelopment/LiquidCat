/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.item;

import lol.liquidcat.features.module.modules.combat.KillAura;
import lol.liquidcat.features.module.modules.render.AntiBlind;
import lol.liquidcat.features.module.modules.render.ItemView;
import lol.liquidcat.features.module.modules.render.SwingAnimation;
import lol.liquidcat.utils.render.animation.easing.easings.Back;
import lol.liquidcat.utils.render.animation.easing.easings.Expo;
import lol.liquidcat.utils.render.animation.easing.easings.Sine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
@SideOnly(Side.CLIENT)
public abstract class MixinItemRenderer {

    @Shadow
    private float prevEquippedProgress;

    @Shadow
    private float equippedProgress;

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    protected abstract void rotateArroundXAndY(float angle, float angleY);

    @Shadow
    protected abstract void setLightMapFromPlayer(AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks);

    @Shadow
    private ItemStack itemToRender;

    @Shadow
    protected abstract void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress);

    /**
     * @author Shurpe
     */
    @Overwrite
    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
        final ItemView itemView = ItemView.INSTANCE;

        if (itemView.getState()) {
            GlStateManager.translate(itemView.getX(), itemView.getY(), itemView.getZ());
        } else {
            GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        }

        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);

        if (itemView.getState()) {
            GlStateManager.scale(itemView.getScale(), itemView.getScale(), itemView.getScale());
        } else {
            GlStateManager.scale(0.4F, 0.4F, 0.4F);
        }
    }

    @Shadow
    protected abstract void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks);

    @Shadow
    protected abstract void doBlockTransformations();

    @Shadow
    protected abstract void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void doItemUsedTransformations(float swingProgress);

    @Shadow
    public abstract void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform);

    @Shadow
    protected abstract void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress);

    /**
     * @author CCBlueX
     */
    @Overwrite
    public void renderItemInFirstPerson(float partialTicks) {
        float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);

        EntityPlayerSP player = this.mc.thePlayer;

        float f1 = player.getSwingProgress(partialTicks);
        float f2 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        float f3 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;

        this.rotateArroundXAndY(f2, f3);
        this.setLightMapFromPlayer(player);
        this.rotateWithPlayerRotations(player, partialTicks);

        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        if (this.itemToRender != null) {
            final KillAura killAura = KillAura.INSTANCE;

            if (this.itemToRender.getItem() instanceof ItemMap) {
                this.renderItemMap(player, f2, f, f1);

            } else if (player.getItemInUseCount() > 0 || (itemToRender.getItem() instanceof ItemSword && killAura.getBlockingStatus())) {
                EnumAction enumaction = killAura.getBlockingStatus() ? EnumAction.BLOCK : this.itemToRender.getItemUseAction();

                switch(enumaction) {
                    case NONE:
                        this.transformFirstPersonItem(f, 0.0F);
                        break;

                    case EAT:
                    case DRINK:
                        this.performDrinking(player, partialTicks);
                        this.transformFirstPersonItem(f, 0.0F);
                        break;

                    case BLOCK:
                        final ItemView itemView = ItemView.INSTANCE;

                        if (itemView.getState()) {
                            switch (itemView.getAnimation()) {
                                case "None":
                                    this.transformFirstPersonItem(f, 0.0F);
                                    this.doBlockTransformations();
                                    break;

                                case "Normal":
                                    this.transformFirstPersonItem(f, f1);
                                    this.doBlockTransformations();
                                    break;

                                case "Slide":
                                    this.transformFirstPersonItem(f, 0.0F);
                                    this.doBlockTransformations();

                                    float f4 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);

                                    GlStateManager.rotate(-f4 * 90.0F * itemView.getSlideFactor(), -1.0F, 0.0F, 1.0F);
                                    GlStateManager.rotate(-f4 * 90.0F, 1.0F, 0.0F, 0.0F);
                                    break;
                            }
                        } else {
                            this.transformFirstPersonItem(f, f1);
                            this.doBlockTransformations();
                        }
                        break;

                    case BOW:
                        this.transformFirstPersonItem(f, 0.0F);
                        this.doBowTransformations(partialTicks, player);
                }
            } else {
                if (!SwingAnimation.INSTANCE.getState())
                    this.doItemUsedTransformations(f1);
                this.transformFirstPersonItem(f, f1);
            }

            this.renderItem(player, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!player.isInvisible()) {
            this.renderPlayerArm(player, f, f1);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    private void renderFireInFirstPerson(final CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = AntiBlind.INSTANCE;

        if (antiBlind.getState() && antiBlind.getFire())
            callbackInfo.cancel();
    }
}