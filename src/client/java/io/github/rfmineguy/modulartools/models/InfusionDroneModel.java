package io.github.rfmineguy.modulartools.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;

public class InfusionDroneModel extends Model {
    private final ModelPart root;
    private final ModelPartData modelPartData;

    public InfusionDroneModel() {
        super(RenderLayer::getEntitySolid);
        Pair<ModelData, ModelPartData> pair = getModelData();
        modelPartData = pair.getRight();
        root = modelPartData.createPart(64, 64);
    }
    private static Pair<ModelData, ModelPartData> getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -1.0F, -3.0F, 6.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return new Pair<>(modelData, modelPartData);
    }
    public static TexturedModelData getTexturedModelData() {
        return TexturedModelData.of(getModelData().getLeft(), 16, 16);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        root.render(matrices, vertices, light, overlay);
    }
}
