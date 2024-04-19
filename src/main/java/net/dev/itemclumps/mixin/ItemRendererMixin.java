package net.dev.itemclumps.mixin;

import net.dev.itemclumps.item.ClumpItem;
import net.dev.itemclumps.model.ItemClumpModels.ClumpModel;
import net.dev.itemclumps.util.ClumpItemUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow @Final public static ModelIdentifier TRIDENT_IN_HAND;
    @Shadow @Final private ItemModels models;
    @Shadow @Final public static ModelIdentifier SPYGLASS_IN_HAND;
    @Shadow public abstract void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model);


    /**
     * @author Modzyyy
     * @reason ItemClumps
     */
    @Overwrite
    public BakedModel getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed) {
        BakedModel bakedModel = stack.isOf(Items.TRIDENT) ? this.models.getModelManager().getModel(TRIDENT_IN_HAND) : (stack.isOf(Items.SPYGLASS) ? this.models.getModelManager().getModel(SPYGLASS_IN_HAND) : this.models.getModel(stack));
        ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld)world : null;
        BakedModel bakedModel2;
        if (!ClumpItemUtil.isClump(stack.getItem())) {
            bakedModel2 = bakedModel.getOverrides().apply(bakedModel, stack, clientWorld, entity, seed);
        } else {
            ItemStack stack1 = ClumpItem.getTopClump(stack);
            ItemStack stack2 = new ItemStack(Items.POPPY, 1);
            BakedModel bakedModel3 = ((ItemRenderer) (Object) this).getModel(stack1, world, entity, seed);
            BakedModel bakedModel4 = ((ItemRenderer) (Object) this).getModel(stack2, world, entity, seed);
            bakedModel2 = new ClumpModel(bakedModel3, bakedModel4);
        }
        return bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2;
    }

//    /**
//     * @author Modzyyy
//     * @reason ItemClumps
//     */
//    @Overwrite
//    public void renderItem(@Nullable LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed) {
//        if (item.isEmpty()) {
//            return;
//        }
//        if (!ClumpItemUtil.isClump(item.getItem())) {
//            BakedModel bakedModel = ((ItemRenderer) (Object) this).getModel(item, world, entity, seed);
//            this.renderItem(item, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel);
//        } else {
//            ItemStack stack1 = ClumpItem.getTopClump(item);
//            ItemStack stack2 = new ItemStack(Items.HANGING_ROOTS, 1);
//            BakedModel bakedModel2 = ((ItemRenderer) (Object) this).getModel(stack1, world, entity, seed);
//            BakedModel bakedModel3 = ((ItemRenderer) (Object) this).getModel(stack2, world, entity, seed);
//            this.renderItem(item, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel2);
//            this.renderItem(item, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel3);
//        }
//    }
}
