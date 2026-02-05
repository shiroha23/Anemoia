package com.shiroha23.anemoia.meowmere.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class PlayerRainbowParticle extends RainbowParticle {
    private final int entityId;
    private int bakeRainbowIndex = 2;

    public PlayerRainbowParticle(ClientLevel world, double x, double y, double z, int entityId) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.entityId = entityId;
        this.target = this.calculatePlayerPos();
        this.totalDistance = this.origin.distanceTo(this.target);
        this.rainbowVecCount = 30;
        this.fillSpeed = 1;
        this.fadeSpeed = 0;
        this.lifetime = 4;
        this.bakedRainbowVecs = new Vec3[this.rainbowVecCount];
        for (int i = 0; i < this.rainbowVecCount; ++i) {
            this.bakedRainbowVecs[i] = Vec3.ZERO;
        }
    }

    public Vec3 calculatePlayerPos() {
        Entity entity = this.level.getEntity(this.entityId);
        if (this.entityId != -1 && entity != null) {
            return entity.position().add(0.0, 0.1, 0.0);
        }
        return this.getPos();
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1.0f;
        float ageClamp = Mth.clamp((float) this.age / ((float) this.lifetime - (float) this.fillSpeed), 0.0f, 1.0f);
        this.alphaProgression = ageClamp * (float) Math.max(0, this.bakeRainbowIndex - 1);
        if (this.bakeRainbowIndex < this.rainbowVecCount) {
            this.target = this.calculatePlayerPos();
            this.totalDistance = this.origin.distanceTo(this.target);
            double y = this.target.y - this.origin.y;
            double xz = this.target.subtract(this.origin).horizontalDistance();
            this.bakedRainbowVecs[this.bakeRainbowIndex] = new Vec3(xz, y, 0.0);
            ++this.bakeRainbowIndex;
        }
    }

    @Override
    protected float processAngle(float angle, float partialTick, PoseStack posestack) {
        Vec3 vec3 = this.calculatePlayerPos();
        Vec3 vecForAngle = vec3.subtract(this.origin);
        this.angle = Math.atan2(vecForAngle.x, vecForAngle.z);
        return (float) this.angle;
    }

    @Override
    protected float getRainbowWidth() {
        return 0.2f;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new PlayerRainbowParticle(worldIn, x, y, z, (int) xSpeed);
        }
    }
}
