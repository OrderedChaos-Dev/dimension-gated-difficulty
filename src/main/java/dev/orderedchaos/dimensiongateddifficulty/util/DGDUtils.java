package dev.orderedchaos.dimensiongateddifficulty.util;

import com.mojang.brigadier.context.CommandContext;
import dev.orderedchaos.dimensiongateddifficulty.level.DGDSavedData;
import dev.orderedchaos.dimensiongateddifficulty.config.DGDConfig;
import dev.orderedchaos.dimensiongateddifficulty.config.DGDSettingsLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DGDUtils {

  private static Double cachedHealthModifier = null;
  private static Double cachedDamageModifier = null;

  public static void clearCaches() {
    cachedHealthModifier = null;
    cachedDamageModifier = null;
  }

  public static double getHealthModifier(MinecraftServer server) {
    if (cachedHealthModifier == null) {
      cachedHealthModifier = calculateHealthModifier(server);
    }
    return cachedHealthModifier;
  }

  public static double getDamageModifier(MinecraftServer server) {
    if (cachedDamageModifier == null) {
      cachedDamageModifier = calculateDamageModifier(server);
    }
    return cachedDamageModifier;
  }

  private static double calculateHealthModifier(MinecraftServer server) {
    Set<String> visitedDimensions = DGDSavedData.getOrCreate(server).getVisitedDimensions();
    double modifier = 1;
    for (String dimension : visitedDimensions) {
      if (DGDConfig.DIMENSION_BLACKLIST.get().contains(dimension) || DGDConfig.DIMENSION_MOD_BLACKLIST.get().contains(dimension.split(":")[0])) {
        continue;
      }

      DGDSettingsLoader.DimensionConfig config = DGDSettingsLoader.dimensionConfigs.get(dimension);
      if (config != null) {
        modifier += config.healthModifier();
      } else {
        modifier += DGDConfig.DEFAULT_HEALTH_MODIFIER.get();
      }
    }
    return modifier;
  }

  private static double calculateDamageModifier(MinecraftServer server) {
    Set<String> visitedDimensions = DGDSavedData.getOrCreate(server).getVisitedDimensions();
    double modifier = 1;
    for (String dimension : visitedDimensions) {
      if (DGDConfig.DIMENSION_BLACKLIST.get().contains(dimension) || DGDConfig.DIMENSION_MOD_BLACKLIST.get().contains(dimension.split(":")[0])) {
        continue;
      }

      DGDSettingsLoader.DimensionConfig config = DGDSettingsLoader.dimensionConfigs.get(dimension);
      if (config != null) {
        modifier += config.damageModifier();
      } else {
        modifier += DGDConfig.DEFAULT_DAMAGE_MODIFIER.get();
      }
    }
    return modifier;
  }

  public static int listVisitedDimensions(CommandContext<CommandSourceStack> context) {
    Player player = context.getSource().getPlayer();
    if (player != null) {
      List<String> visitedDimensions = DGDSavedData.getOrCreate(context.getSource().getServer()).asList();
      List<String> filteredDimensions = visitedDimensions.stream().filter(dimension ->
        !DGDConfig.DIMENSION_BLACKLIST.get().contains(dimension) && !DGDConfig.DIMENSION_MOD_BLACKLIST.get().contains(dimension.split(":")[0])
      ).toList();
      Component component = Component.literal(filteredDimensions.toString());
      player.sendSystemMessage(component);
    }

    return 1;
  }

  public static int showCurrentModifiers(CommandContext<CommandSourceStack> context) {
    Player player = context.getSource().getPlayer();
    if (player != null) {
      MinecraftServer server = context.getSource().getServer();
      double healthModifier = DGDUtils.getHealthModifier(server);
      double damageModifier = DGDUtils.getDamageModifier(server);
      double healthMultiplier = healthModifier * 100;
      double damageMultiplier = damageModifier * 100;
      String message = String.format("HP=%.2f%%, DMG=%.2f%%", healthMultiplier, damageMultiplier);
      Component component = Component.literal(message);
      player.sendSystemMessage(component);
    }

    return 1;
  }
}
