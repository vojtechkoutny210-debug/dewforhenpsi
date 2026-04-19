package com.golemmod.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.level.LevelTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GolemSpawnEvent {

    private final Map<UUID, Long> spawnCooldowns = new HashMap<>();

    private static final int  MAX_GOLEMS    = 15;
    private static final int  MIN_VILLAGERS = 3;
    private static final int  SEARCH_RADIUS = 48;
    private static final long COOLDOWN_TICKS = 600L; // 30 sekund

    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.getGameTime() % 80 != 0) return; // kontrola kazdych 4 sekund

        List<Villager> allVillagers = level.getEntitiesOfClass(
                Villager.class,
                new AABB(-30000, -64, -30000, 30000, 320, 30000)
        );

        for (Villager villager : allVillagers) {
            if (!isScared(villager)) continue;

            List<Villager> nearbyScared = level
                    .getEntitiesOfClass(Villager.class,
                            villager.getBoundingBox().inflate(SEARCH_RADIUS))
                    .stream()
                    .filter(this::isScared)
                    .toList();

            if (nearbyScared.size() < MIN_VILLAGERS) continue;

            UUID key = villager.getUUID();
            long now  = level.getGameTime();
            if (spawnCooldowns.getOrDefault(key, 0L) > now) continue;

            long existingGolems = level.getEntitiesOfClass(IronGolem.class,
                    villager.getBoundingBox().inflate(SEARCH_RADIUS)).size();

            long toSpawn = MAX_GOLEMS - existingGolems;
            if (toSpawn <= 0) continue;

            for (int i = 0; i < toSpawn; i++) {
                Villager spawnNear = nearbyScared.get(i % nearbyScared.size());
                spawnGolemNear(level, spawnNear);
            }

            spawnCooldowns.put(key, now + COOLDOWN_TICKS);
            break;
        }
    }

    private boolean isScared(Villager villager) {
        return !villager.isTrading()
                && (villager.getTarget() != null
                    || villager.getDeltaMovement().horizontalDistanceSqr() > 0.05);
    }

    private void spawnGolemNear(ServerLevel level, Villager near) {
        double x = near.getX() + (level.random.nextDouble() - 0.5) * 10;
        double z = near.getZ() + (level.random.nextDouble() - 0.5) * 10;

        IronGolem golem = new IronGolem(EntityType.IRON_GOLEM, level);
        golem.setPos(x, near.getY(), z);
        golem.setPlayerCreated(false);
        level.addFreshEntity(golem);
    }
}
