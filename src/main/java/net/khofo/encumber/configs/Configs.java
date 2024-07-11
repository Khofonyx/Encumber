package net.khofo.encumber.configs;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Configs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<String>> CONTAINERS;
    public static final ForgeConfigSpec.ConfigValue<List<String>> BOOST_ITEMS;
    public static final ForgeConfigSpec.ConfigValue<List<Double>> BOOST_ITEMS_AMOUNT;
    public static final ForgeConfigSpec.ConfigValue<List<String>> BOOST_ARMORS;
    public static final ForgeConfigSpec.ConfigValue<List<Double>> BOOST_ARMORS_AMOUNT;
    public static final ForgeConfigSpec.ConfigValue<Double> RIDING_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Double> FALL_FLYING_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Double> JUMPING_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Double> ENCUMBERED_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Double> OVER_ENCUMBERED_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Double> WEIGHT_UI_X_OFFSET;
    public static final ForgeConfigSpec.ConfigValue<Double> WEIGHT_UI_Y_OFFSET;

    public static final ForgeConfigSpec.ConfigValue<Integer> ANVIL_UI_X_OFFSET;
    public static final ForgeConfigSpec.ConfigValue<Integer> ANVIL_UI_Y_OFFSET;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_MULTIPLE_BOOST_ITEMS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RIDING_FLYING_JUMPING_TIED_TO_OVER_ENCUMBERED_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SINK_IN_WATER_LAVA;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TOGGLE_ANVIL_ICON;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TOGGLE_WEIGHT_TEXT;
    public static final ForgeConfigSpec.ConfigValue<Double> UNENCUMBERMENT_LEVEL1_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Double> UNENCUMBERMENT_LEVEL2_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Double> UNENCUMBERMENT_LEVEL3_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DISABLE_ENCHANTS;

    static {
        BUILDER.push("Configs for Encumbered:");

        // Define predicates for the list elements
        Predicate<Object> stringValidator = obj -> obj instanceof String;
        Predicate<Object> doubleValidator = obj -> obj instanceof Double;

        CONTAINERS = (ForgeConfigSpec.ConfigValue<List<String>>) (Object) BUILDER.comment("\nWhich container items should be checked?")
                .defineListAllowEmpty("container_item", Arrays.asList(
                        "minecraft:shulker_box", "minecraft:white_shulker_box", "minecraft:orange_shulker_box",
                        "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box",
                        "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box",
                        "minecraft:light_gray_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box",
                        "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box",
                        "minecraft:red_shulker_box", "minecraft:black_shulker_box"), stringValidator);

        BOOST_ITEMS = (ForgeConfigSpec.ConfigValue<List<String>>) (Object) BUILDER.comment("\nAdd any items you want to boost the player's carrying capacity. Item's here count in your inventory and when equipped.")
                .defineListAllowEmpty("boost_items", Arrays.asList("minecraft:torch", "minecraft:emerald"), stringValidator);

        BOOST_ITEMS_AMOUNT = (ForgeConfigSpec.ConfigValue<List<Double>>) (Object) BUILDER.comment("\nAdd boost amounts that correspond to the BOOST_ITEMS above")
                .defineListAllowEmpty("boost_items_amount", Arrays.asList(100.0D, 1000.0D), doubleValidator);

        BOOST_ARMORS = (ForgeConfigSpec.ConfigValue<List<String>>) (Object) BUILDER.comment("\nAdd any equipables you want to boost the player's carrying capacity. Items in this list require you to equip them before they take effect.")
                .defineListAllowEmpty("boost_armors", Arrays.asList("minecraft:diamond_helmet"), stringValidator);

        BOOST_ARMORS_AMOUNT = (ForgeConfigSpec.ConfigValue<List<Double>>) (Object) BUILDER.comment("\nAdd boost amounts that correspond to the BOOST_ARMORS above")
                .defineListAllowEmpty("boost_armors_amount", Arrays.asList(100.0D), doubleValidator);

        RIDING_FLYING_JUMPING_TIED_TO_OVER_ENCUMBERED_THRESHOLD = BUILDER.comment("\nIf this is true, when the player is at the OVER_ENCUMBERED_THRESHOLD, they cannot jump, fly, or ride mounts. If false, you can configure these thresholds below. default: true")
                .define("tie_to_over_encumbered_threshold", true);

        RIDING_THRESHOLD = BUILDER.comment("\nIf weight is above this, you cannot ride any mount (Negative values will break the mod) default: 100")
                .define("riding_threshold", 100.0D);

        FALL_FLYING_THRESHOLD = BUILDER.comment("\nIf weight is above this, you cannot fly with an elytra (Negative values will break the mod) default: 100")
                .define("fall_flying_threshold", 100.0D);

        JUMPING_THRESHOLD = BUILDER.comment("\nIf weight is above this, you cannot jump (Negative values will break the mod) default: 100")
                .define("jumping_threshold", 100.0D);

        ENCUMBERED_THRESHOLD = BUILDER.comment("\nIf weight is above this, you cannot sprint (Negative values will break the mod) default: 70")
                .define("encumbered_threshold", 70.0D);

        OVER_ENCUMBERED_THRESHOLD = BUILDER.comment("\nIf weight is above this, you get slowness 5, cannot jump, and cannot sprint (Negative values will break the mod) default: 100")
                .define("over_encumbered_threshold", 100.0D);

        WEIGHT_UI_Y_OFFSET = BUILDER.comment("\nSet the vertical offset for the UI that shows your weight, default is 10% from the top (near the bottom of the screen). default: 10")
                .define("weight_ui_y_offset", 10D);

        WEIGHT_UI_X_OFFSET = BUILDER.comment("\nSet the horizontal offset for the UI that shows your weight, default is 50% from the left (centered horizontally). default: 50")
                .define("weight_ui_x_offset", 50D);

        ANVIL_UI_Y_OFFSET = BUILDER.comment("\nSet the vertical offset for the anvil UI. default: 0")
                .define("anvil_ui_y_offset", 0);

        ANVIL_UI_X_OFFSET = BUILDER.comment("\nSet the horizontal offset for the anvil UI. default: 0")
                .define("anvil_ui_x_offset", 0);

        ALLOW_MULTIPLE_BOOST_ITEMS = BUILDER.comment("\nWhether or not boost item's additional capacities stack. If there are multiple boost items present in your inventory and this is false, it chooses the better boost item. default: true")
                .define("allow_multiple_boost_items", true);

        SINK_IN_WATER_LAVA = BUILDER.comment("\nWhether or not the player sinks in WATER and LAVA while OVER_ENCUMBERED. default: true")
                .define("sink_in_water_and_lava", true);

        TOGGLE_ANVIL_ICON = BUILDER.comment("\nWhether or not the anvil icon appears. default: true")
                .define("toggle_anvil_icon", true);

        TOGGLE_WEIGHT_TEXT = BUILDER.comment("\nWhether or not the weight text appears. default: true")
                .define("toggle_weight_text", true);

        UNENCUMBERMENT_LEVEL1_MULTIPLIER = BUILDER.comment("\nMultiplier for Unencumberment Level 1 (Pick a value between 1 and 100)")
                .defineInRange("unencumberment_level_1_multiplier", 1.1, 1, 100.0);
        UNENCUMBERMENT_LEVEL2_MULTIPLIER = BUILDER.comment("\nMultiplier for Unencumberment Level 2 (Pick a value between 1 and 100)")
                .defineInRange("unencumberment_level_2_multiplier", 1.25, 1, 100.0);
        UNENCUMBERMENT_LEVEL3_MULTIPLIER = BUILDER.comment("\nMultiplier for Unencumberment Level 3 (Pick a value between 1 and 100)")
                .defineInRange("unencumberment_level_3_multiplier", 1.5, 1, 100.0);

        DISABLE_ENCHANTS = BUILDER.comment("\nDisables Unencumberment enchant. default: false")
                .define("disable_enchants", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}