package io.github.rfmineguy.modulartools.models;

import io.github.rfmineguy.modulartools.ModularToolsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ActivatorModel extends Model {
    public static final Identifier TEXTURE = Identifier.of(ModularToolsMod.MODID, "textures/entity/activator.png");
    private static final ModelPart root;
    private static final ModelData modelData = DEFAULT();

    static {
        root = modelData.getRoot().createPart(16, 16);
    }

    public ActivatorModel() {
        super(RenderLayer::getEntitySolid);
    }

    public static TexturedModelData getTexturedModelData() {
        return TexturedModelData.of(modelData, 16, 64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.getTextureManager().bindTexture(TEXTURE);
        root.render(matrices, vertices, light, overlay);
    }

    private static ModelData DEFAULT() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData group = modelPartData.addChild("group", ModelPartBuilder.create().uv(-2, -2).cuboid(0.0F, -2.0F, -2.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(1, 0).cuboid(2.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(1, 0).cuboid(-1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 23.0F, -6.0F));

        ModelPartData cube_r1 = group.addChild("cube_r1", ModelPartBuilder.create().uv(1, 0).cuboid(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));
        ModelPartData cube_r2 = group.addChild("cube_r2", ModelPartBuilder.create().uv(1, 0).cuboid(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, 1.5708F));
        return modelData;
    }
}
