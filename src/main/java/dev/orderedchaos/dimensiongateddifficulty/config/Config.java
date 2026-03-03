package dev.orderedchaos.dimensiongateddifficulty.config;

import dev.orderedchaos.dimensiongateddifficulty.DimensionGatedDifficulty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = DimensionGatedDifficulty.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  public static final ForgeConfigSpec.DoubleValue DEFAULT_HEALTH_MODIFIER = BUILDER
    .comment("Default health modifier")
    .defineInRange("Default Health Modifier", 0.2, 0, 1000);

  public static final ForgeConfigSpec.DoubleValue DEFAULT_DAMAGE_MODIFIER = BUILDER
    .comment("Default damage modifier")
    .defineInRange("Default Damage Modifier", 0.1, 0, 1000);

  public static final ForgeConfigSpec SPEC = BUILDER.build();

  @SubscribeEvent
  static void onLoad(final ModConfigEvent event) {

  }
}
