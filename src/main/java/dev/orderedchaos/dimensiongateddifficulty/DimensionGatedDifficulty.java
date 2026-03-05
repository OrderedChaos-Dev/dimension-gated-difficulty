package dev.orderedchaos.dimensiongateddifficulty;

import com.mojang.logging.LogUtils;
import dev.orderedchaos.dimensiongateddifficulty.config.DGDConfig;
import dev.orderedchaos.dimensiongateddifficulty.config.DGDSettingsLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.IOException;

@Mod(DimensionGatedDifficulty.MODID)
public class DimensionGatedDifficulty {
  public static final String MODID = "dimensiongateddifficulty";
  public static final Logger LOGGER = LogUtils.getLogger();

  public DimensionGatedDifficulty(FMLJavaModLoadingContext context) {
    IEventBus modEventBus = context.getModEventBus();

    modEventBus.addListener(this::commonSetup);
    MinecraftForge.EVENT_BUS.register(this);
    context.registerConfig(ModConfig.Type.COMMON, DGDConfig.SPEC);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    try {
      DGDSettingsLoader.loadSettings();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
