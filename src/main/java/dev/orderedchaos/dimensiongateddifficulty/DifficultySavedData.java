package dev.orderedchaos.dimensiongateddifficulty;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DifficultySavedData extends SavedData {

  private Set<String> visitedDimensions = new HashSet<>();

  public static DifficultySavedData create() {
    return new DifficultySavedData();
  }

  public static DifficultySavedData load(CompoundTag tag) {
    DifficultySavedData data = DifficultySavedData.create();
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

  public static DifficultySavedData getOrCreate(MinecraftServer server) {
    DimensionDataStorage dataStorage = server.overworld().getDataStorage();
    return dataStorage.computeIfAbsent(DifficultySavedData::load, DifficultySavedData::create, "dimension_dated_difficulty");
  }
}
