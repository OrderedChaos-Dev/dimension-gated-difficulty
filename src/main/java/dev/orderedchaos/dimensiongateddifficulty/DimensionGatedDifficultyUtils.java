package dev.orderedchaos.dimensiongateddifficulty;

import com.google.common.cache.Cache;
import dev.orderedchaos.dimensiongateddifficulty.config.Config;
import dev.orderedchaos.dimensiongateddifficulty.config.DifficultySettingsLoader;
import dev.orderedchaos.dimensiongateddifficulty.config.DimensionConfig;
import net.minecraft.server.MinecraftServer;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Set;

public class DimensionGatedDifficultyUtils {

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
    Set<String> visitedDimensions = DifficultySavedData.getOrCreate(server).getVisitedDimensions();
    double modifier = 1;
    for (String dimension : visitedDimensions) {
      DimensionConfig config = DifficultySettingsLoader.dimensionConfig.get(dimension);
      if (config != null) {
        modifier += config.getHealthModifier();
      } else {
        modifier += Config.DEFAULT_HEALTH_MODIFIER.get();
      }
    }
    return modifier;
  }

  private static double calculateDamageModifier(MinecraftServer server) {
    Set<String> visitedDimensions = DifficultySavedData.getOrCreate(server).getVisitedDimensions();
    double modifier = 1;
    for (String dimension : visitedDimensions) {
      DimensionConfig config = DifficultySettingsLoader.dimensionConfig.get(dimension);
      if (config != null) {
        modifier += config.getDamageModifier();
      } else {
        modifier += Config.DEFAULT_DAMAGE_MODIFIER.get();
      }
    }
    return modifier;
  }
}
