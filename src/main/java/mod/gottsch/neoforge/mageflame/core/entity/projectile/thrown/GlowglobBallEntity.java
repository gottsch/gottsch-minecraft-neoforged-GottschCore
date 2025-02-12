/*
 * This file is part of Mage Flame.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
 *
 * Mage Flame is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mage Flame is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURCoordsE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mage Flame.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neoforge.mageflame.core.entity.projectile.thrown;

import mod.gottsch.neoforge.mageflame.core.entity.creature.GlowglobEntity;
import mod.gottsch.neoforge.mageflame.core.item.ModItems;
import mod.gottsch.neoforge.mageflame.core.setup.Registration;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * @author Mark Gottschling on 1/23/2025
 */
public class GlowglobBallEntity extends ThrowableItemProjectile {

    /**
     * forge entity registration constructor
     * @param entityType
     * @param level
     */
    public GlowglobBallEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public GlowglobBallEntity(Level world) {
        super(Registration.GLOWGLOB_BALL_ENTITY.get(), world);
    }
    public GlowglobBallEntity(Level world, LivingEntity owner) {
        super(Registration.GLOWGLOB_BALL_ENTITY.get(), owner, world);
    }

    public GlowglobBallEntity(Level world, double d, double e, double f) {
        super(Registration.GLOWGLOB_BALL_ENTITY.get(), d, e, f, world);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 3) { // 3 = projectile particles status
            double d = 0.08;

            for (int i = 0; i < 8; i++) {
                this.level()
                        .addParticle(
                                new ItemParticleOption(ParticleTypes.ITEM, this.getItem()),
                                this.getX(), this.getY(), this.getZ(),
                                ((double)this.random.nextFloat() - 0.5D) * 0.08D,
                                ((double)this.random.nextFloat() - 0.5D) * 0.08D,
                                ((double)this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        entityHitResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            GlowglobEntity entity = Registration.GLOWGLOB_ENTITY.get().create(this.level());
            if (entity != null) {
                entity.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                this.level().addFreshEntity(entity);
            }
            this.level().broadcastEntityEvent(this, (byte)3); // magic number?
            this.discard();
        }
    }
    @Override
    protected Item getDefaultItem() {
        return ModItems.GLOWGLOB_BALL.get();
    }
}
