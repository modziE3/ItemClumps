package net.dev.itemclumps.mixin;

import net.dev.itemclumps.item.ClumpItem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {

    protected ItemEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Shadow @Final private Random random;
    @Shadow @Final private ItemRenderer itemRenderer;
    @Shadow protected abstract int getRenderedAmount(ItemStack stack);

    /**
     * @author Modzyyy
     * @reason ItemClumps
     */
    @Overwrite
    public void render(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        float t;
        float s;
        matrixStack.push();
        ItemStack itemStack = itemEntity.getStack();
        if (ClumpItem.isClump(itemStack.getItem())) {
            int count = itemStack.getCount();
            itemStack = ClumpItem.getTopStack(itemStack);
            itemStack.setCount(count);
        }
        int j = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
        this.random.setSeed(j);
        BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.getWorld(), null, itemEntity.getId());
        boolean bl = bakedModel.hasDepth();
        int k = this.getRenderedAmount(itemStack);
        float h = 0.25f;
        float l = MathHelper.sin(((float)itemEntity.getItemAge() + g) / 10.0f + itemEntity.uniqueOffset) * 0.1f + 0.1f;
        float m = bakedModel.getTransformation().getTransformation((ModelTransformationMode)ModelTransformationMode.GROUND).scale.y();
        matrixStack.translate(0.0f, l + 0.25f * m, 0.0f);
        float n = itemEntity.getRotation(g);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(n));
        float o = bakedModel.getTransformation().ground.scale.x();
        float p = bakedModel.getTransformation().ground.scale.y();
        float q = bakedModel.getTransformation().ground.scale.z();
        if (!bl) {
            float r = -0.0f * (float)(k - 1) * 0.5f * o;
            s = -0.0f * (float)(k - 1) * 0.5f * p;
            t = -0.09375f * (float)(k - 1) * 0.5f * q;
            matrixStack.translate(r, s, t);
        }
        for (int u = 0; u < k; ++u) {
            matrixStack.push();
            if (u > 0) {
                if (bl) {
                    s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float v = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    matrixStack.translate(s, t, v);
                } else {
                    s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    matrixStack.translate(s, t, 0.0f);
                }
            }
            this.itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
            matrixStack.pop();
            if (bl) continue;
            matrixStack.translate(0.0f * o, 0.0f * p, 0.09375f * q);
        }
        matrixStack.pop();
        super.render(itemEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
