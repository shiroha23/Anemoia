package com.shiroha23.anemoia.meowmere.entity;

import com.shiroha23.anemoia.Anemoia;
import com.shiroha23.anemoia.AnemoiaConfig;
import com.shiroha23.anemoia.meowmere.MeowmereContent;
import java.awt.Color;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import org.joml.Vector3f;

public class CatProjectile extends Projectile {
    protected static final EntityDataAccessor<Integer> BOUNCESLEFT = SynchedEntityData.defineId(CatProjectile.class, EntityDataSerializers.INT);
    private float damageMultiplier = 1.0f;

    public CatProjectile(EntityType<? extends CatProjectile> entityEntityType, Level level) {
        super(entityEntityType, level);
    }

    public CatProjectile(EntityType<? extends CatProjectile> entityType, Level levelIn, LivingEntity shooter) {
        this(entityType, levelIn);
        this.setOwner(shooter);
    }

    public CatProjectile(Level levelIn, LivingEntity shooter) {
        this(Anemoia.CAT_PROJECTILE.get(), levelIn, shooter);
        this.setNoGravity(false);
    }

    public int getBouncesLeft() {
        return this.entityData.get(BOUNCESLEFT);
    }

    public void setBouncesLeft(int i) {
        this.entityData.set(BOUNCESLEFT, i);
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    public void shoot(Vec3 rotation, float speed) {
        this.setDeltaMovement(rotation.scale(speed));
    }

    protected double getDefaultGravity() {
        return 0.025f;
    }

    @Override
    public void tick() {
        super.tick();
        this.setPos(this.position().add(this.getDeltaMovement()));
        if (!this.isNoGravity()) {
            Vec3 vec = this.getDeltaMovement();
            this.setDeltaMovement(vec.x, vec.y - this.getDefaultGravity(), vec.z);
        }
        if (this.level().isClientSide) {
            if (this.getBouncesLeft() >= 1) {
                this.level().addParticle(Anemoia.PLAYER_RAINBOW.get(), this.xo, this.yo, this.zo, this.getId(), 0.0, 0.0);
            }
            if (this.tickCount % 5 == 0) {
                int particleCount = 1;
                double spread = 1.0;
                double speed = 1.0;
                this.rainbowDustParticle(this.level(), this.xo, this.yo, this.zo, speed, spread, particleCount);
            }
        }
        this.handleHitDetection();
    }

    public void handleHitDetection() {
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent(this, hitresult))) {
            this.onHit(hitresult);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.getBouncesLeft() >= -1 && AnemoiaConfig.ENABLE_MEOWMERE_SOUND.get()) {
            this.playSound(SoundEvents.CAT_AMBIENT, 3.0f, 1.0f);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity) {
            this.hurtTarget(entity);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        Vec3 hitPos = hitResult.getLocation();
        int bounceCount = 5 - this.getBouncesLeft();
        if (this.level().isClientSide) {
            int particleCount = 10 + 15 * bounceCount;
            double spread = 3 + bounceCount;
            double speed = 1.0;
            this.rainbowDustParticle(this.level(), hitPos.x, hitPos.y, hitPos.z, speed, spread, particleCount);
        } else {
            if (this.getBouncesLeft() >= 0) {
                float range = 1.5f + 0.6f * bounceCount;
                AABB boundingBox = new AABB(hitPos.x - range, hitPos.y - range, hitPos.z - range,
                    hitPos.x + range, hitPos.y + range, hitPos.z + range);
                List<Entity> entities = this.level().getEntities(this, boundingBox);
                for (Entity target : entities) {
                    this.hurtTarget(target);
                }
                Direction face = hitResult.getDirection();
                Vec3 normal = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ());
                Vec3 bounceVector = this.getDeltaMovement().subtract(normal.scale(2.0 * this.getDeltaMovement().dot(normal)));
                this.setDeltaMovement(bounceVector);
                double offset = 0.1;
                this.setPos(hitPos.x + normal.x * offset, hitPos.y + normal.y * offset, hitPos.z + normal.z * offset);
            }
            this.setBouncesLeft(this.getBouncesLeft() - 1);
        }
        if (this.level().isClientSide && this.getBouncesLeft() <= 0) {
            this.discard();
        }
    }

    private void rainbowDustParticle(Level level, double x, double y, double z, double speed, double spread, int particleCount) {
        for (int i = 0; i < particleCount; ++i) {
            float hue = (float) i / (float) particleCount;
            float saturation = 0.2f;
            float brightness = 0.95f;
            int rgb = Color.HSBtoRGB(hue, saturation, brightness);
            float r = (float) (rgb >> 16 & 0xFF) / 255.0f;
            float g = (float) (rgb >> 8 & 0xFF) / 255.0f;
            float b = (float) (rgb & 0xFF) / 255.0f;
            float whiteFactor = 0.4f;
            r = r * (1.0f - whiteFactor) + whiteFactor;
            g = g * (1.0f - whiteFactor) + whiteFactor;
            b = b * (1.0f - whiteFactor) + whiteFactor;
            double offsetX = (MeowmereContent.RANDOM.nextDouble() - 0.5) * spread;
            double offsetY = (MeowmereContent.RANDOM.nextDouble() - 0.5) * spread;
            double offsetZ = (MeowmereContent.RANDOM.nextDouble() - 0.5) * spread;
            DustParticleOptions particle = new DustParticleOptions(new Vector3f(r, g, b), 1.5f);
            level.addParticle(particle, x + offsetX, y + offsetY, z + offsetZ, speed, speed, speed);
        }
    }

    private void hurtTarget(Entity entity) {
        if (this.level().isClientSide) {
            return;
        }
        if (!(this.getOwner() instanceof Player player)) {
            return;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        if (player == livingEntity) {
            return;
        }
        if (livingEntity instanceof Player targetPlayer && (targetPlayer.isCreative() || player.isAlliedTo(targetPlayer))) {
            return;
        }
        if (livingEntity instanceof TamableAnimal tamable && tamable.isTame() && tamable.getOwner() == player) {
            return;
        }
        if (livingEntity instanceof OwnableEntity ownable && ownable.getOwner() == player) {
            return;
        }

        DamageSource damageSource = this.damageSources().playerAttack(player);
        float damage = player.getAttribute(Attributes.ATTACK_DAMAGE) != null
            ? (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE)
            : 1.0f;
        livingEntity.hurt(damageSource, this.damageMultiplier * damage);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BOUNCESLEFT, 4);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setBouncesLeft(compound.getInt("BouncesLeft"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("BouncesLeft", this.getBouncesLeft());
    }
}
