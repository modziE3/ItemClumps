package net.dev.itemclumps.model.ItemClumpModels;

import net.minecraft.client.render.model.BakedQuad;

import java.util.List;

public class ClumpModelUtil {

    public static final float[]  FORWARD_3D = new float[]{ 0.2f,     0f, -0.125f};
    public static final float[]  FORWARD_2D = new float[]{-0.2f,   0.0f,    1f};
    public static final float[] BACKWARD_3D = new float[]{-0.2f, -0.05f,  0.125f};
    public static final float[] BACKWARD_2D = new float[]{0.25f,   0.2f,  -5f};

    public static boolean isEntityModel(List<BakedQuad> quads) {
        return quads.size() < 5;
    }
}