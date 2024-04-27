package net.dev.itemclumps.mixin;

import net.dev.itemclumps.item.ClumpItem;
import net.dev.itemclumps.model.ItemClumpModels.ClumpModelUtil;
import net.dev.itemclumps.model.ItemClumpModels.ShiftedBakedModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Shadow @Final private MatrixStack matrices;
    @Shadow @Final private MinecraftClient client;
    @Shadow public abstract void draw();
    @Shadow public abstract VertexConsumerProvider.Immediate getVertexConsumers();

    /**
     * @author Modzyyy
     * @reason ItemClumps
     */
    @Overwrite
    private void drawItem(@Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int x, int y, int seed, int z) {
        if (stack.isEmpty()) {
            return;
        }
        boolean isClump = ClumpItem.isClump(stack.getItem());
        boolean isSideLit;
        boolean hasDepth;
        BakedModel model1;
        BakedModel model2 = null;
        if (isClump) {
            model1 = this.client.getItemRenderer().getModel(ClumpItem.getTopStack(stack), world, entity, seed);
            model2 = this.client.getItemRenderer().getModel(ClumpItem.getNextStack(stack), world, entity, seed);
            isSideLit = model1.isSideLit() && model2.isSideLit();
            hasDepth = model1.hasDepth() || model2.hasDepth();
        } else {
            model1 = this.client.getItemRenderer().getModel(stack, world, entity, seed);
            isSideLit = model1.isSideLit();
            hasDepth = model1.hasDepth();
        }
        this.matrices.push();
        this.matrices.translate(x + 8, y + 8, 150 + (hasDepth ? z : 0));
        try {
            this.matrices.multiplyPositionMatrix(new Matrix4f().scaling(1.0f, -1.0f, 1.0f));
            this.matrices.scale(16.0f, 16.0f, 16.0f);
            if (!isSideLit) {
                DiffuseLighting.disableGuiDepthLighting();
            }
            if (isClump) {
                boolean is3DModel1 = ClumpModelUtil.isEntityModel(model1.getQuads(null, null, Random.create(seed)));
                boolean is3DModel2 = ClumpModelUtil.isEntityModel(model2.getQuads(null, null, Random.create(seed)));
                float[] shift1 = is3DModel1 ? ClumpModelUtil.FORWARD_3D  : ClumpModelUtil.FORWARD_2D;
                float[] shift2 = is3DModel2 ? ClumpModelUtil.BACKWARD_3D : ClumpModelUtil.BACKWARD_2D;
                model1 = new ShiftedBakedModel(model1, shift1[0], shift1[1],  shift1[2], ClumpItem.getTopStack(stack));
                model2 = new ShiftedBakedModel(model2, shift2[0], shift2[1],  shift2[2], ClumpItem.getNextStack(stack));
                this.client.getItemRenderer().renderItem(ClumpItem.getTopStack(stack), ModelTransformationMode.GUI, false, this.matrices, this.getVertexConsumers(), 0xF000F0, OverlayTexture.DEFAULT_UV, model1);
                this.client.getItemRenderer().renderItem(ClumpItem.getNextStack(stack), ModelTransformationMode.GUI, false, this.matrices, this.getVertexConsumers(), 0xF000F0, OverlayTexture.DEFAULT_UV, model2);
            } else {
                this.client.getItemRenderer().renderItem(stack, ModelTransformationMode.GUI, false, this.matrices, this.getVertexConsumers(), 0xF000F0, OverlayTexture.DEFAULT_UV, model1);
            }
            this.draw();
            if (!isSideLit) {
                DiffuseLighting.enableGuiDepthLighting();
            }
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Rendering item");
            CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
            crashReportSection.add("Item Type", () -> String.valueOf(stack.getItem()));
            crashReportSection.add("Item Damage", () -> String.valueOf(stack.getDamage()));
            crashReportSection.add("Item NBT", () -> String.valueOf(stack.getNbt()));
            crashReportSection.add("Item Foil", () -> String.valueOf(stack.hasGlint()));
            throw new CrashException(crashReport);
        }
        this.matrices.pop();
    }
}
