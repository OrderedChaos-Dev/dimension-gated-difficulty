package dev.orderedchaos.dimensiongateddifficulty.config;

import dev.orderedchaos.dimensiongateddifficulty.DimensionGatedDifficulty;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = DimensionGatedDifficulty.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DGDConfig {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  public static final ForgeConfigSpec.DoubleValue DEFAULT_HEALTH_MODIFIER = BUILDER
    .comment("Default health modifier")
    .defineInRange("Default Health Modifier", 0.2, 0, 1000);

  public static final ForgeConfigSpec.DoubleValue DEFAULT_DAMAGE_MODIFIER = BUILDER
    .comment("Default damage modifier")
    .defineInRange("Default Damage Modifier", 0.1, 0, 1000);

  public static final ForgeConfigSpec.DoubleValue DEFAULT_EXPERIENCE_MODIFIER = BUILDER
    .comment("Default experience modifier")
    .defineInRange("Default Experience Modifier", 0.2, 0, 1000);

  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST = BUILDER
    .comment("Dimension blacklist - these dimensions are ignored when calculating difficulty")
    .defineList("Dimension blacklist", List.of(), (str) -> str instanceof String);

  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_MOD_BLACKLIST = BUILDER
    .comment("Dimension mod blacklist - dimensions from mods in this list are ignored when calculating difficulty")
    .defineList("Dimension mod blacklist", List.of(), (str) -> str instanceof String);

  public static final ForgeConfigSpec SPEC = BUILDER.build();

  @SubscribeEvent
  static void onLoad(final ModConfigEvent event) {

  }
}
