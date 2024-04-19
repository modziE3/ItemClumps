package net.dev.itemclumps.model.ItemClumpModels;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClumpModel implements BakedModel {


    private final BakedModel model1;
    private final BakedModel model2;

    public ClumpModel(BakedModel model1, BakedModel model2) {
        this.model1 = model1;
        this.model2 = model2;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        List<BakedQuad> quads = new ArrayList<>(model1.getQuads(state, face, random));
        quads.addAll(repositionQuads(model2.getQuads(state, face, random), 0.2f, 0.2f, -0.2f));
        return quads;
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

    @Override
    public boolean useAmbientOcclusion() {
        return model1.useAmbientOcclusion() || model2.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return model1.hasDepth() || model2.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return model1.isSideLit() || model2.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return model1.getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return model1.getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return model1.getOverrides();
    }
}