package dev.orderedchaos.dimensiongateddifficulty.config;

import com.google.gson.*;
import dev.orderedchaos.dimensiongateddifficulty.DimensionGatedDifficulty;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class DGDSettingsLoader {

  private static final String SETTINGS_FILE = "dimensiongateddifficulty.json";

  public record DimensionConfig(String dimension, @Nullable Double healthModifier, @Nullable Double damageModifier, @Nullable Double experienceModifier) {}

  private static final List<DimensionConfig> defaults = List.of(
    new DimensionConfig("minecraft:the_nether", 0.2, 0.1, 0.2),
    new DimensionConfig("minecraft:the_end", 0.2, 0.1, 0.2)
  );

  public static HashMap<String, DimensionConfig> dimensionConfigs = new HashMap<>();

  public static void loadSettings() throws IOException {
    Path configPath = FMLPaths.CONFIGDIR.get().resolve(SETTINGS_FILE);
    File file = configPath.toFile();

    if (!file.exists()) {
      Files.write(configPath, new GsonBuilder().setPrettyPrinting().create().toJson(defaults).getBytes());
    }

    String input = Files.readString(configPath);
    JsonArray root = JsonParser.parseString(input).getAsJsonArray();
    root.forEach((element) -> {
      JsonObject object = element.getAsJsonObject();
      String dimension = object.getAsJsonPrimitive("dimension").getAsString();

      Double healthModifier = null;
      Double damageModifier = null;
      Double experienceModifier = null;

      JsonElement healthModifierElement = object.get("healthModifier");
      JsonElement damageModifierElement = object.get("damageModifier");
      JsonElement experienceModifierElement = object.get("experienceModifier");

      if (healthModifierElement != null && healthModifierElement.isJsonPrimitive()) {
        healthModifier = healthModifierElement.getAsDouble();
      }
      if (damageModifierElement != null && damageModifierElement.isJsonPrimitive()) {
        damageModifier = damageModifierElement.getAsDouble();
      }
      if (experienceModifierElement != null && experienceModifierElement.isJsonPrimitive()) {
        experienceModifier = experienceModifierElement.getAsDouble();
      }

      if(dimensionConfigs.putIfAbsent(dimension, new DimensionConfig(dimension, healthModifier, damageModifier, experienceModifier)) == null) {
        DimensionGatedDifficulty.LOGGER.info(
          "Registered new dimension difficulty setting [{}] (healthModifier={}, damageModifier={}, experienceModifier={})",
          dimension, healthModifier, damageModifier, experienceModifier
        );
      } else {
        DimensionGatedDifficulty.LOGGER.info(
          "Skipped duplicate configuration for {}",
          dimension
        );
      }
    });
  }
}