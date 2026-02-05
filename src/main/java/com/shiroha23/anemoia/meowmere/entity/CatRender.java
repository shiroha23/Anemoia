package com.shiroha23.anemoia.meowmere.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.shiroha23.anemoia.meowmere.MeowmereContent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class CatRender extends EntityRenderer<CatProjectile> {
    private static final ResourceLocation TEXTURE = MeowmereContent.resource("textures/entity/cat_projectile.png");
    private final CatModel<CatProjectile> model;

    public CatRender(EntityRendererProvider.Context context) {
        super(context);
        this.model = new CatModel<>(context.bakeLayer(CatModel.LAYER_LOCATION));
    }

    @Override
    public void render(CatProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1.0f, 1.0f, 1.0f);
        poseStack.translate(0.0f, 0.2f, 0.0f);
        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * 57.2957763671875) - 90.0f);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * 57.2957763671875) + 90.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        VertexConsumer vertexBuilder = buffer.getBuffer(this.model.renderType(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CatProjectile entity) {
        return TEXTURE;
    }
}
