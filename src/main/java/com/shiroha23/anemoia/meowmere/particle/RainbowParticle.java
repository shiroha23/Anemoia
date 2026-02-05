package com.shiroha23.anemoia.meowmere.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.shiroha23.anemoia.meowmere.MeowmereContent;
import java.util.Optional;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RainbowParticle extends Particle {
    public static final ParticleGroup PARTICLE_GROUP = new ParticleGroup(100);
    private static final RenderType RAINBOW_RENDER_TYPE = getTeslaBulb(MeowmereContent.resource("textures/particle/rainbow.png"));

    public int rainbowVecCount = 64;
    public int fadeSpeed = 15;
    public int fillSpeed = 40;
    public Vec3 origin;
    public Vec3 target;
    public double totalDistance;
    public double angle;
    protected Vec3[] bakedRainbowVecs;
    public float alphaProgression;
    private float prevAlphaProgression;
    private float prevAlpha;

    public static RenderType getTeslaBulb(ResourceLocation resourceLocation) {
        return RenderType.energySwirl(resourceLocation, 0.0f, 0.0f);
    }

    public RainbowParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.origin = new Vec3(x, y, z);
        this.target = new Vec3(xd, yd, zd);
        this.totalDistance = this.origin.distanceTo(this.target);
        this.rainbowVecCount = 64;
        this.bakedRainbowVecs = new Vec3[this.rainbowVecCount];
        this.rebakeRainbowVecs(this.totalDistance);
        this.lifetime = (int) ((double) this.fillSpeed + this.totalDistance * 4.0);
        this.gravity = 0.0f;
        this.setSize(3.0f, 3.0f);
    }

    protected void rebakeRainbowVecs(double totalDistance) {
        Vec3 rotateZero = new Vec3(totalDistance, 0.0, 0.0);
        for (int i = 0; i < this.rainbowVecCount; ++i) {
            float lifeAt = (float) i / (float) this.rainbowVecCount;
            float ageJump = (float) Math.sin(lifeAt * Math.PI);
            this.bakedRainbowVecs[i] = rotateZero.scale(lifeAt).add(0.0, ageJump * Math.max(totalDistance, 1.0), 0.0);
        }
        Vec3 vecForAngle = this.target.subtract(this.origin);
        this.angle = Math.atan2(vecForAngle.x, vecForAngle.z);
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RAINBOW_RENDER_TYPE);
        Vec3 cameraPos = camera.getPosition();
        PoseStack posestack = new PoseStack();
        posestack.pushPose();
        posestack.translate(this.origin.x - cameraPos.x, this.origin.y - cameraPos.y, this.origin.z - cameraPos.z);
        float f = this.processAngle((float) this.angle, partialTick, posestack);
        posestack.mulPose(Axis.YP.rotation(f - 1.5707964f));
        int packedLight = this.getLightColor(partialTick);
        float width = this.getRainbowWidth();
        float alphaLerped = this.prevAlpha + (this.alpha - this.prevAlpha) * partialTick;
        float alphaProgressionLerped = this.prevAlphaProgression + (this.alphaProgression - this.prevAlphaProgression) * partialTick;

        for (int vertIndex = 0; vertIndex < this.bakedRainbowVecs.length - 1; ++vertIndex) {
            posestack.pushPose();
            float u1 = (float) vertIndex / (float) this.bakedRainbowVecs.length;
            float u2 = u1 + 1.0f / (float) this.bakedRainbowVecs.length;
            Vec3 draw1 = this.bakedRainbowVecs[vertIndex];
            Vec3 draw2 = this.bakedRainbowVecs[vertIndex + 1];
            PoseStack.Pose pose = posestack.last();
            float alpha0 = this.calcAlphaForVertex(vertIndex, alphaProgressionLerped) * alphaLerped;
            float alpha1 = this.calcAlphaForVertex(vertIndex + 1, alphaProgressionLerped) * alphaLerped;

            vertexconsumer.vertex(pose.pose(), (float) draw1.x, (float) draw1.y, (float) draw1.z + width)
                .color(1.0f, 1.0f, 1.0f, alpha0)
                .uv(u1, 1.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0.0f, 1.0f, 0.0f)
                .endVertex();
            vertexconsumer.vertex(pose.pose(), (float) draw2.x, (float) draw2.y, (float) draw1.z + width)
                .color(1.0f, 1.0f, 1.0f, alpha1)
                .uv(u2, 1.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0.0f, 1.0f, 0.0f)
                .endVertex();
            vertexconsumer.vertex(pose.pose(), (float) draw2.x, (float) draw2.y, (float) draw2.z - width)
                .color(1.0f, 1.0f, 1.0f, alpha1)
                .uv(u2, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0.0f, 1.0f, 0.0f)
                .endVertex();
            vertexconsumer.vertex(pose.pose(), (float) draw1.x, (float) draw1.y, (float) draw2.z - width)
                .color(1.0f, 1.0f, 1.0f, alpha0)
                .uv(u1, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0.0f, 1.0f, 0.0f)
                .endVertex();
            posestack.popPose();
        }

        bufferSource.endBatch();
        posestack.popPose();
    }

    protected float getRainbowWidth() {
        return 0.5f;
    }

    protected float processAngle(float angle, float partialTick, PoseStack posestack) {
        return angle;
    }

    private float calcAlphaForVertex(int vertIndex, float alphaIn) {
        return Mth.clamp(alphaIn - (float) vertIndex, 0.0f, 1.0f);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void tick() {
        super.tick();
        this.prevAlpha = this.alpha;
        this.prevAlphaProgression = this.alphaProgression;
        int left = this.lifetime - this.age;
        if (left <= this.fadeSpeed) {
            this.alpha = (float) left / (float) this.fadeSpeed;
        } else {
            float ageClamp = Mth.clamp((float) this.age / ((float) this.lifetime - (float) this.fillSpeed), 0.0f, 1.0f);
            this.alphaProgression = ageClamp * (float) this.rainbowVecCount;
        }
    }

    @Override
    public Optional<ParticleGroup> getParticleGroup() {
        return Optional.of(PARTICLE_GROUP);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new RainbowParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
