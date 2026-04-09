package dev.orderedchaos.dimensiongateddifficulty.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DGDSavedData extends SavedData {

  private final Set<String> visitedDimensions = new HashSet<>();

  public static DGDSavedData create() {
    return new DGDSavedData();
  }

  public static DGDSavedData load(CompoundTag tag) {
    DGDSavedData data = DGDSavedData.create();
    data.visitedDimensions.addAll(tag.getAllKeys());

    return data;
  }

  @Override
  public CompoundTag save(CompoundTag tag) {
    visitedDimensions.forEach((dimension) -> tag.putBoolean(dimension, true));
    return tag;
  }

  public void addVisitedDimension(String dimension) {
    this.visitedDimensions.add(dimension);
    this.setDirty();
  }

  public void resetVisitedDimensions() {
    this.visitedDimensions.clear();
    this.setDirty();
  }

  public Set<String> getVisitedDimensions() {
    return this.visitedDimensions;
  }

  @Override
  public String toString() {
    return visitedDimensions.toString();
  }

  public List<String> asList() {
    return this.visitedDimensions.stream().toList();
  }

  public static DGDSavedData getOrCreate(MinecraftServer server) {
    DimensionDataStorage dataStorage = server.overworld().getDataStorage();
    // TODO: fix this typo in 1.0 release lol
    return dataStorage.computeIfAbsent(DGDSavedData::load, DGDSavedData::create, "dimension_dated_difficulty");
  }
}
