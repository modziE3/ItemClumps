package net.dev.itemclumps.mixin;

import net.dev.itemclumps.item.ClumpItem;
import net.dev.itemclumps.util.ClumpItemUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow private float prevEquipProgressMainHand;
    @Shadow private float equipProgressMainHand;
    @Shadow private float equipProgressOffHand;
    @Shadow @Final private MinecraftClient client;
    @Shadow private ItemStack mainHand;
    @Shadow private ItemStack offHand;
    @Shadow private float prevEquipProgressOffHand;

    /**
     * @author Modzyyy
     * @reason ItemClumps
     */
    @Overwrite
    public void updateHeldItems() {
        this.prevEquipProgressMainHand = this.equipProgressMainHand;
        this.prevEquipProgressOffHand = this.equipProgressOffHand;
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        ItemStack itemStack = clientPlayerEntity.getMainHandStack();
        if (ClumpItemUtil.isClump(itemStack.getItem())) {
            itemStack = ClumpItem.getTopClump(itemStack);
        }
        ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
        if (ClumpItemUtil.isClump(itemStack2.getItem())) {
            itemStack2 = ClumpItem.getTopClump(itemStack2);
        }
        if (ItemStack.areEqual(this.mainHand, itemStack)) {
            this.mainHand = itemStack;
        }
        if (ItemStack.areEqual(this.offHand, itemStack2)) {
            this.offHand = itemStack2;
        }
        if (clientPlayerEntity.isRiding()) {
            this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4f, 0.0f, 1.0f);
            this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4f, 0.0f, 1.0f);
        } else {
            float f = clientPlayerEntity.getAttackCooldownProgress(1.0f);
            this.equipProgressMainHand += MathHelper.clamp((this.mainHand == itemStack ? f * f * f : 0.0f) - this.equipProgressMainHand, -0.4f, 0.4f);
            this.equipProgressOffHand += MathHelper.clamp((float)(this.offHand == itemStack2 ? 1 : 0) - this.equipProgressOffHand, -0.4f, 0.4f);
        }
        if (this.equipProgressMainHand < 0.1f) {
            this.mainHand = itemStack;
        }
        if (this.equipProgressOffHand < 0.1f) {
            this.offHand = itemStack2;
        }
    }
}
