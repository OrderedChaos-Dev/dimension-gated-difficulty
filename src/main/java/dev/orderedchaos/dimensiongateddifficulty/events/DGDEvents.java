package dev.orderedchaos.dimensiongateddifficulty.events;

import dev.orderedchaos.dimensiongateddifficulty.DimensionGatedDifficulty;
import dev.orderedchaos.dimensiongateddifficulty.level.DGDSavedData;
import dev.orderedchaos.dimensiongateddifficulty.util.DGDUtils;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
  public static void updateMobStats(EntityJoinLevelEvent event) {
    if (event.getEntity() instanceof Player) return;
    if (event.getLevel().isClientSide()) return;

    if (event.getEntity() instanceof LivingEntity livingEntity) {
      // max health
      double healthModifier = DGDUtils.getHealthModifier(event.getEntity().getServer());
      AttributeInstance maxHealth = livingEntity.getAttribute(Attributes.MAX_HEALTH);
      if (maxHealth != null) {
        double newMaxHealth = maxHealth.getBaseValue() * healthModifier;
        maxHealth.setBaseValue(maxHealth.getBaseValue() * healthModifier);
        livingEntity.setHealth((float) newMaxHealth);
      }

      // attack damage
      double damageModifier = DGDUtils.getDamageModifier(event.getEntity().getServer());
      AttributeInstance damage = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);
      if (damage != null) {
        damage.setBaseValue(damage.getBaseValue() * damageModifier);
      }
    }
  }
}
