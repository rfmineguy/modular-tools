package io.github.rfmineguy.modulartools.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class ModularInfusionConnectionParticle extends SpriteBillboardParticle {

    protected ModularInfusionConnectionParticle(ClientWorld clientWorld, double x, double y, double z) {
        super(clientWorld, x, y, z);
        this.maxAge = 10;
        this.gravityStrength = 0;
        this.scale = 1;
        this.setBoundingBoxSpacing(0.05F, 0.05F);
        this.velocityMultiplier = 0;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dy));
        super.move(dx, dy, dz);
    }

    @Override
    public float getSize(float tickDelta) {
        return this.scale * 1;
    }

    @Override
    protected int getBrightness(float tint) {
        return super.getBrightness(tint);
    }

    // Intentionally not calling super.tick()
    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            ModularInfusionConnectionParticle particle = new ModularInfusionConnectionParticle(world, x, y, z);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
