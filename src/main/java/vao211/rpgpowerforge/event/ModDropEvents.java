package vao211.rpgpowerforge.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import vao211.rpgpowerforge.registry.ModRegistry;

public class ModDropEvents {
    public static void register() {

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!(entity.getWorld() instanceof ServerWorld world)) return;

            EntityType<?> type = entity.getType();

            if (type == EntityType.WITHER_SKELETON || type == EntityType.SLIME ||
                    type == EntityType.PILLAGER || type == EntityType.VINDICATOR) {

                if (world.random.nextFloat() < 0.10f) {
                    dropItem(world, entity, new ItemStack(ModRegistry.BASIC_POWER_ORB, 1));
                }
            }

            if (type == EntityType.ENDER_DRAGON || type == EntityType.WITHER || type == EntityType.WARDEN) {
                dropItem(world, entity, new ItemStack(ModRegistry.BASIC_POWER_ORB, 3));
            }

            if (world.random.nextFloat() < 0.05f) {
                dropItem(world, entity, new ItemStack(ModRegistry.BASIC_ATTRIBUTE_ORB, 1));
            }
        });
    }

    private static void dropItem(ServerWorld world, LivingEntity entity, ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), stack);
        world.spawnEntity(itemEntity);
    }
}