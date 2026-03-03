package dev.orderedchaos.dimensiongateddifficulty.config;

public class DimensionConfig {
  private String dimension;
  private double healthModifier;
  private double damageModifier;

  public DimensionConfig(String dimension, double healthModifier, double damageModifier) {
    this.dimension = dimension;
    this.healthModifier = healthModifier;
    this.damageModifier = damageModifier;
  }

  public double getHealthModifier() {
    return this.healthModifier;
  }

  public double getDamageModifier() {
    return this.damageModifier;
  }
}