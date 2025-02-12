/*
 * This file is part of  Mage Flame.
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
package mod.gottsch.neoforge.mageflame.core.entity.ai.goal;

import mod.gottsch.neoforge.mageflame.core.entity.creature.SummonedPathAwareEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

/**
 * Created by Mark Gottschling on 1/10/2025
 */
public class SummonedLightSourceTrackOwnerAttackerGoal extends TargetGoal {
    private final SummonedPathAwareEntity lightSourceEntity;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public SummonedLightSourceTrackOwnerAttackerGoal(SummonedPathAwareEntity lightSourceEntity) {
        super(lightSourceEntity, false);
        this.lightSourceEntity = lightSourceEntity;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
       LivingEntity livingEntity = this.lightSourceEntity.getOwner();
        if (livingEntity == null) {
            return false;
        } else {
            this.attacker = livingEntity.getLastAttacker();
            int i = livingEntity.getLastHurtByMobTimestamp();
            return i != this.lastAttackedTime && this.canAttack(this.attacker, TargetingConditions.DEFAULT) && this.lightSourceEntity.canAttackWithOwner(this.attacker, livingEntity);
        }
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity livingEntity = this.lightSourceEntity.getOwner();
        if (livingEntity != null) {
            this.lastAttackedTime = livingEntity.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
