package dev.orderedchaos.dimensiongateddifficulty.events;

import dev.orderedchaos.dimensiongateddifficulty.DimensionGatedDifficulty;
import dev.orderedchaos.dimensiongateddifficulty.level.DGDSavedData;
import dev.orderedchaos.dimensiongateddifficulty.util.DGDUtils;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DimensionGatedDifficulty.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DGDEvents {

  @SubscribeEvent
  public static void changeDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
    ResourceKey<Level> key = event.getTo();
    MinecraftServer server = event.getEntity().getServer();
    if (server != null && key != Level.OVERWORLD) {
      DGDSavedData.getOrCreate(server).addVisitedDimension(key.location().toString());
      DGDUtils.clearCaches();
    }
  }

  @SubscribeEvent
  public static void registerCommands(RegisterCommandsEvent event) {
    event.getDispatcher().register(
      Commands
        .literal("dgd")
        .then(Commands.literal("list")
          .executes(DGDUtils::listVisitedDimensions))
        .then(Commands.literal("modifiers")
          .executes(DGDUtils::showCurrentModifiers))
    );
  }

  @SubscribeEvent
  public static void updateMobStats(MobSpawnEvent.FinalizeSpawn event) {
    if (event.getLevel().isClientSide()) return;
    Mob entity = event.getEntity();
    MinecraftServer server = entity.getServer();

    if (server != null) {
      // max health
      double healthModifier = DGDUtils.getHealthModifier(server);
      AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
      if (maxHealth != null) {
        double newMaxHealth = maxHealth.getBaseValue() * healthModifier;
        maxHealth.setBaseValue(maxHealth.getBaseValue() * healthModifier);
        entity.setHealth((float) newMaxHealth);
      }

      // attack damage
      double damageModifier = DGDUtils.getDamageModifier(server);
      AttributeInstance damage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
      if (damage != null) {
        damage.setBaseValue(damage.getBaseValue() * damageModifier);
      }
    }
  }

  @SubscribeEvent
  public static void dropExperience(LivingExperienceDropEvent event) {
    MinecraftServer server = event.getEntity().getServer();
    if (server != null) {
      /*
        Calculates experience based on random chance.
        If the original dropped experience is 2 and the total modifier is 120%,
        then 2 * 1.2 = 2.4
        We take the value rounded down (2) and the leftover fraction (0.4) as a random chance threshold, i.e. 40% chance,
        which means there is a 40% chance to increment the new experience drop by 1.
        This averages out to 2.4 experience.
       */
      int originalExperience = event.getOriginalExperience();
      double experienceModifier = DGDUtils.getExperienceModifier(server);
      double modifiedExperience = originalExperience * experienceModifier;

      double randomChance = event.getEntity().getRandom().nextDouble();

      int newDroppedExperience = (int)Math.floor(modifiedExperience);

      if (modifiedExperience - newDroppedExperience < randomChance) {
        newDroppedExperience += 1;
      }

      event.setDroppedExperience(newDroppedExperience);
    }
  }

  @SubscribeEvent
  public static void serverStopping(ServerStoppingEvent event) {
    DGDUtils.clearCaches();
  }

  @SubscribeEvent
  public static void serverAboutToStart(ServerAboutToStartEvent event) {
    DGDUtils.clearCaches();
  }
}
