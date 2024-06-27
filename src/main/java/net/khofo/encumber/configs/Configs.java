package net.khofo.encumber.configs;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class Configs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<String>> CONTAINERS;
    public static final ForgeConfigSpec.ConfigValue<List<String>> BOOST_ITEMS;
    public static final ForgeConfigSpec.ConfigValue<List<Double>> BOOST_AMOUNT;
    public static final ForgeConfigSpec.ConfigValue<Double> RIDING_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Double> FALL_FLYING_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Double> JUMPING_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Double> SLOWNESS_3_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Double> SLOWNESS_5_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Double> WEIGHT_UI_X_OFFSET;
    public static final ForgeConfigSpec.ConfigValue<Double> WEIGHT_UI_Y_OFFSET;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_MULTIPLE_BOOST_ITEMS;

    static {
        BUILDER.push("Configs for Encumbered:");

        CONTAINERS = BUILDER.comment("Which container items should be checked?")
                .define("container_item", Arrays.asList(
                        "minecraft:shulker_box", "minecraft:white_shulker_box", "minecraft:orange_shulker_box",
                        "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box",
                        "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box",
                        "minecraft:light_gray_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box",
                        "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box",
                        "minecraft:red_shulker_box", "minecraft:black_shulker_box"));

        BOOST_ITEMS = BUILDER.comment("Add any items you want to boost the player's carrying capacity")
                .define("boost_items", Arrays.asList("minecraft:diamond_helmet","minecraft:emerald"));

        BOOST_AMOUNT = BUILDER.comment("Add boost amounts that correspond to the BOOST_ITEMS above")
                .define("boost_amount", Arrays.asList(100.0D,1000.0D));

        RIDING_THRESHOLD = BUILDER.comment("If weight is above this, you cannot ride any mount (set to negative value to disable)")
                .define("riding_threshold", 1280.0D);

        FALL_FLYING_THRESHOLD = BUILDER.comment("If weight is above this, you cannot fly with an elytra (set to negative value to disable)")
                .define("fall_flying_threshold", 128.0D);

        JUMPING_THRESHOLD = BUILDER.comment("If weight is above this, you cannot jump (set to negative value to disable)")
                .define("jumping_threshold", 1280.0D);

        SLOWNESS_3_THRESHOLD = BUILDER.comment("If weight is above this, you get slowness 3 (doesn't stack with slowness 1) (set to negative value to disable)")
                .define("slowness_3_threshold", 640.0D);

        SLOWNESS_5_THRESHOLD = BUILDER.comment("If weight is above this, you get slowness 5 (doesn't stack with slowness 1) (set to negative value to disable)")
                .define("slowness_5_threshold", 1280.0D);

        WEIGHT_UI_Y_OFFSET = BUILDER.comment("Set the vertical offset for the UI that shows your weight (negative values to move down)")
                .define("weight_ui_y_offset", 0.0D);

        WEIGHT_UI_X_OFFSET = BUILDER.comment("Set the horizontal offset for the UI that shows your weight (negative values to move left)")
                .define("weight_ui_x_offset", 0.0D);

        ALLOW_MULTIPLE_BOOST_ITEMS = BUILDER.comment("Whether or not boost item's additional capacities stack. If there are multiple boost items present in your inventory and this is false, it chooses the better boost item")
                .define("allow_multiple_boost_items", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}