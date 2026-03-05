package dev.orderedchaos.dimensiongateddifficulty.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.orderedchaos.dimensiongateddifficulty.DimensionGatedDifficulty;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class DGDSettingsLoader {

  public record DimensionConfig(String dimension, double healthModifier, double damageModifier) {}

  private static final List<DimensionConfig> defaults = List.of(
    new DimensionConfig("minecraft:the_nether", 0.2, 0.1),
    new DimensionConfig("minecraft:the_end", 0.2, 0.1)
  );

  public static HashMap<String, DimensionConfig> dimensionConfigs = new HashMap<>();

  public static void loadSettings() throws IOException {
    String settingsFile = "dimensiongateddifficulty.json";
    Path configPath = FMLPaths.CONFIGDIR.get().resolve(settingsFile);
    File file = configPath.toFile();

    if (!file.exists()) {
      Files.write(configPath, new GsonBuilder().setPrettyPrinting().create().toJson(defaults).getBytes());
    }

    String input = Files.readString(configPath);
    JsonArray root = JsonParser.parseString(input).getAsJsonArray();
    root.forEach((element) -> {
      JsonObject object = element.getAsJsonObject();
      String dimension = object.getAsJsonPrimitive("dimension").getAsString();
      double healthModifier = object.getAsJsonPrimitive(("healthModifier")).getAsDouble();
      double damageModifier = object.getAsJsonPrimitive(("damageModifier")).getAsDouble();
      if(dimensionConfigs.putIfAbsent(dimension, new DimensionConfig(dimension, healthModifier, damageModifier)) == null) {
        DimensionGatedDifficulty.LOGGER.info(
          "Registered new dimension difficulty setting [{}] (healthModifier={}, damageModifier={})",
          dimension, healthModifier, damageModifier
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