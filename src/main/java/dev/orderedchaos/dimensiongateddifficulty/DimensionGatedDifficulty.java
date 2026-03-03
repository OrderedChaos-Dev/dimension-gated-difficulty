package dev.orderedchaos.dimensiongateddifficulty;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import dev.orderedchaos.dimensiongateddifficulty.config.Config;
import dev.orderedchaos.dimensiongateddifficulty.config.DifficultySettingsLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
    context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    try {
      DifficultySettingsLoader.loadSettings();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SubscribeEvent
  public void changeDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
    ResourceKey<Level> key = event.getTo();
    MinecraftServer server = event.getEntity().getServer();
    if (server != null && key != Level.OVERWORLD) {
      DifficultySavedData.getOrCreate(server).addVisitedDimension(key.location().toString());
      DimensionGatedDifficultyUtils.clearCaches();
    }
  }

  @SubscribeEvent
  public void registerCommands(RegisterCommandsEvent event) {
    event.getDispatcher().register(
      Commands
        .literal("dgd")
        .then(Commands.literal("list")
          .executes(this::listVisitedDimensions))
        .then(Commands.literal("modifiers")
          .executes(this::showCurrentModifiers))
    );
  }

  @SubscribeEvent
  public void updateMobStats(EntityJoinLevelEvent event) {
    if (event.getEntity() instanceof Player) return;
    if (event.getLevel().isClientSide()) return;

    if (event.getEntity() instanceof LivingEntity livingEntity) {
      double healthModifier = DimensionGatedDifficultyUtils.getHealthModifier(event.getEntity().getServer());
      double damageModifier = DimensionGatedDifficultyUtils.getDamageModifier(event.getEntity().getServer());

      AttributeInstance maxHealth = livingEntity.getAttribute(Attributes.MAX_HEALTH);
      AttributeInstance damage = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);

      if (maxHealth != null) {
        double newMaxHealth = maxHealth.getBaseValue() * healthModifier;
        maxHealth.setBaseValue(maxHealth.getBaseValue() * healthModifier);
        livingEntity.setHealth((float) newMaxHealth);
      }

      if (damage != null) {
        damage.setBaseValue(damage.getBaseValue() * damageModifier);
      }
    }
  }

  public int listVisitedDimensions(CommandContext<CommandSourceStack> context) {
    Player player = context.getSource().getPlayer();
    if (player != null) {
      String visitedDimensions = DifficultySavedData.getOrCreate(context.getSource().getServer()).toString();
      Component component = Component.literal(visitedDimensions);
      player.sendSystemMessage(component);
    }

    return 1;
  }

  public int showCurrentModifiers(CommandContext<CommandSourceStack> context) {
    Player player = context.getSource().getPlayer();
    if (player != null) {
      MinecraftServer server = context.getSource().getServer();
      double healthModifier = DimensionGatedDifficultyUtils.getHealthModifier(server);
      double damageModifier = DimensionGatedDifficultyUtils.getDamageModifier(server);
      double healthMultiplier = healthModifier * 100;
      double damageMultiplier = damageModifier * 100;
      String message = String.format("HP=%.2f%%, DMG=%.2f%%", healthMultiplier, damageMultiplier);
      Component component = Component.literal(message);
      player.sendSystemMessage(component);
    }

    return 1;
  }
}
