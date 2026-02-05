package com.shiroha23.anemoia.meowmere.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.shiroha23.anemoia.meowmere.MeowmereContent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;

public class CatModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(MeowmereContent.resource("cat_projectile"), "main");
    private final ModelPart bb_main;

    public CatModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main",
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-2.5f, -5.0f, -2.0f, 5.0f, 5.0f, 5.0f, new CubeDeformation(0.0f))
                .texOffs(0, 10)
                .addBox(-1.5f, -5.0f, -3.0f, 3.0f, 2.0f, 1.0f, new CubeDeformation(0.0f)),
            PartPose.offset(0.0f, 2.0f, 0.0f));

        bb_main.addOrReplaceChild("cube_r1",
            CubeListBuilder.create().texOffs(0, 13).addBox(-4.5f, -15.5f, 1.0f, 2.0f, 2.0f, 1.0f, new CubeDeformation(0.0f)),
            PartPose.offsetAndRotation(-10.7f, 12.9f, -0.6f, 0.0f, -0.3927f, 0.7854f));

        bb_main.addOrReplaceChild("cube_r2",
            CubeListBuilder.create().texOffs(8, 10).addBox(2.5f, -15.5f, -2.0f, 2.0f, 2.0f, 1.0f, new CubeDeformation(0.0f)),
            PartPose.offsetAndRotation(11.5f, 12.0f, 2.2f, 0.0f, 0.3927f, -0.7854f));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
