package com.golemmod.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.EnumSet;

public class GolemLavaEvent {

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof IronGolem golem
                && !event.getLevel().isClientSide()) {
            golem.goalSelector.addGoal(3, new GoToLavaGoal(golem));
        }
    }

    static class GoToLavaGoal extends Goal {

        private final IronGolem golem;
        private BlockPos lavaPos = null;

        private static final int  SEARCH_RADIUS = 16;
        private static final int  CHECK_INTERVAL = 60;

        public GoToLavaGoal(IronGolem golem) {
            this.golem = golem;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (golem.getTarget() != null) return false;
            if (golem.level().getGameTime() % CHECK_INTERVAL != 0) return false;
            lavaPos = findNearbyLava();
            return lavaPos != null;
        }

        @Override
        public boolean canContinueToUse() {
            if (golem.getTarget() != null) return false;
            if (lavaPos == null) return false;
            return golem.blockPosition().distSqr(lavaPos) > 9;
        }

        @Override
        public void start() {
            if (lavaPos != null) {
                golem.getNavigation().moveTo(
                        lavaPos.getX() + 0.5,
                        lavaPos.getY(),
                        lavaPos.getZ() + 0.5,
                        1.0
                );
            }
        }

        @Override
        public void stop() {
            golem.getNavigation().stop();
            lavaPos = null;
        }

        private BlockPos findNearbyLava() {
            BlockPos golemPos = golem.blockPosition();
            BlockPos closest  = null;
            double closestDist = Double.MAX_VALUE;

            for (BlockPos pos : BlockPos.betweenClosed(
                    golemPos.offset(-SEARCH_RADIUS, -4, -SEARCH_RADIUS),
                    golemPos.offset( SEARCH_RADIUS,  4,  SEARCH_RADIUS))) {

                if (golem.level().getBlockState(pos).is(Blocks.LAVA)) {
                    double dist = golemPos.distSqr(pos);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = pos.immutable();
                    }
                }
            }
            return closest;
        }
    }
}
