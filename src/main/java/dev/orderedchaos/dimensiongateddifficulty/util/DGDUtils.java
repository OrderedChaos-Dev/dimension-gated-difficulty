package dev.orderedchaos.dimensiongateddifficulty.util;

import com.mojang.brigadier.context.CommandContext;
import dev.orderedchaos.dimensiongateddifficulty.level.DGDSavedData;
import dev.orderedchaos.dimensiongateddifficulty.config.DGDConfig;
import dev.orderedchaos.dimensiongateddifficulty.config.DGDSettingsLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Set;

public class DGDUtils {

  private static Double cachedHealthModifier = null;
  private static Double cachedDamageModifier = null;
  private static Double cachedExperienceModifier = null;

  public static void clearCaches() {
    cachedHealthModifier = null;
    cachedDamageModifier = null;
    cachedExperienceModifier = null;
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

  public static double getExperienceModifier(MinecraftServer server) {
    if (cachedExperienceModifier == null) {
      cachedExperienceModifier = calculateExperienceModifier(server);
    }
    return cachedExperienceModifier;
  }

  private static double calculateHealthModifier(MinecraftServer server) {
    Set<String> visitedDimensions = DGDSavedData.getOrCreate(server).getVisitedDimensions();
    double modifier = 1 + DGDConfig.BASE_HEALTH_MODIFIER.get();
    for (String dimension : visitedDimensions) {
      if (DGDConfig.DIMENSION_BLACKLIST.get().contains(dimension) || DGDConfig.DIMENSION_MOD_BLACKLIST.get().contains(dimension.split(":")[0])) {
        continue;
      }

      DGDSettingsLoader.DimensionConfig config = DGDSettingsLoader.dimensionConfigs.get(dimension);
      if (config != null) {
        double val = config.healthModifier() != null ? config.healthModifier() : DGDConfig.DEFAULT_HEALTH_MODIFIER.get();
        modifier += val;
      } else {
        modifier += DGDConfig.DEFAULT_HEALTH_MODIFIER.get();
      }
    }
    return modifier;
  }

  private static double calculateDamageModifier(MinecraftServer server) {
    Set<String> visitedDimensions = DGDSavedData.getOrCreate(server).getVisitedDimensions();
    double modifier = 1 + DGDConfig.BASE_DAMAGE_MODIFIER.get();
    for (String dimension : visitedDimensions) {
      if (DGDConfig.DIMENSION_BLACKLIST.get().contains(dimension) || DGDConfig.DIMENSION_MOD_BLACKLIST.get().contains(dimension.split(":")[0])) {
        continue;
      }

      DGDSettingsLoader.DimensionConfig config = DGDSettingsLoader.dimensionConfigs.get(dimension);
      if (config != null) {
        double val = config.damageModifier() != null ? config.damageModifier() : DGDConfig.DEFAULT_DAMAGE_MODIFIER.get();
        modifier += val;
      } else {
        modifier += DGDConfig.DEFAULT_DAMAGE_MODIFIER.get();
      }
    }
    return modifier;
  }

  private static double calculateExperienceModifier(MinecraftServer server) {
    Set<String> visitedDimensions = DGDSavedData.getOrCreate(server).getVisitedDimensions();
    double modifier = 1 + DGDConfig.BASE_EXPERIENCE_MODIFIER.get();
    for (String dimension : visitedDimensions) {
      if (DGDConfig.DIMENSION_BLACKLIST.get().contains(dimension) || DGDConfig.DIMENSION_MOD_BLACKLIST.get().contains(dimension.split(":")[0])) {
        continue;
      }

      DGDSettingsLoader.DimensionConfig config = DGDSettingsLoader.dimensionConfigs.get(dimension);
      if (config != null) {
        double val = config.experienceModifier() != null ? config.experienceModifier() : DGDConfig.DEFAULT_EXPERIENCE_MODIFIER.get();
        modifier += val;
      } else {
        modifier += DGDConfig.DEFAULT_EXPERIENCE_MODIFIER.get();
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
      double experienceModifier = DGDUtils.getExperienceModifier(server);
      double healthMultiplier = healthModifier * 100;
      double damageMultiplier = damageModifier * 100;
      double experienceMultiplier = experienceModifier * 100;
      String message = String.format("HP=%.2f%%, DMG=%.2f%%, EXP=%.2f%%", healthMultiplier, damageMultiplier, experienceMultiplier);
      Component component = Component.literal(message);
      player.sendSystemMessage(component);
    }

    return 1;
  }
}
