package net.dev.itemclumps.model.ItemClumpModels;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShiftedBakedModel implements BakedModel {

    private final BakedModel model;
    private final List<Float> shift = new ArrayList<>();

    public ShiftedBakedModel(BakedModel model, float shiftX, float shiftY, float shiftZ, ItemStack stack1) {
        this.model = model;
        this.shift.add(shiftX);
        this.shift.add(shiftY);
        this.shift.add(shiftZ);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return repositionQuads(this.model.getQuads(state, face, random), this.shift.get(0), this.shift.get(1), this.shift.get(2));
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.model.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return this.model.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return this.model.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return this.model.isBuiltin();
    }

    @Override
    public Sprite getParticleSprite() {
        return this.model.getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return this.model.getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return this.model.getOverrides();
    }

    public static List<BakedQuad> repositionQuads(List<BakedQuad> quads, float offsetX, float offsetY, float offsetZ) {
        List<BakedQuad> newQuads = new ArrayList<>();
        for (BakedQuad quad : quads) {
            newQuads.add(getNewShiftedQuad(quad, offsetX, offsetY, offsetZ));
        }
        return newQuads;
    }

    public static BakedQuad getNewShiftedQuad(BakedQuad quad, float offsetX, float offsetY, float offsetZ) {
        int[] vertexData = quad.getVertexData().clone();
        for (int i = 0; i < vertexData.length; i += 8) {
            float x = Float.intBitsToFloat(vertexData[i]) + offsetX;
            float y = Float.intBitsToFloat(vertexData[i + 1]) + offsetY;
            float z = Float.intBitsToFloat(vertexData[i + 2]) + offsetZ;
            vertexData[i] = Float.floatToRawIntBits(x);
            vertexData[i + 1] = Float.floatToRawIntBits(y);
            vertexData[i + 2] = Float.floatToRawIntBits(z);
        }
        return getNewQuadWithVertexData(quad, vertexData);
    }

    public static BakedQuad getNewQuadWithVertexData(BakedQuad quad, int[] vertexData) {
        return new BakedQuad(vertexData, quad.getColorIndex(), quad.getFace(), quad.getSprite(), quad.hasShade());
    }
}
