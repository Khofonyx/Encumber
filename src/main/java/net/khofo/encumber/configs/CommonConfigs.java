package net.khofo.encumber.configs;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class CommonConfigs {
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
    public static final ForgeConfigSpec.ConfigValue<Double> ENCUMBERED_THRESHOLD_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Double> OVER_ENCUMBERED_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_MULTIPLE_BOOST_ITEMS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RIDING_FLYING_JUMPING_TIED_TO_OVER_ENCUMBERED_THRESHOLD;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SINK_IN_WATER_LAVA;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LOCK_GUI_TO_CREATIVE;
    public static final ForgeConfigSpec.ConfigValue<Double> UNENCUMBERMENT_LEVEL1_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Double> UNENCUMBERMENT_LEVEL2_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Double> UNENCUMBERMENT_LEVEL3_MULTIPLIER;

    static {
        BUILDER.push("Common Configs for Encumbered:");

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

        ENCUMBERED_THRESHOLD_MULTIPLIER = BUILDER.comment("\nThe Multiplier for the first encumberment stage. Pick a value between 1 and 100. For example, if your max weight is 1000, and you set the multiplier to 70, then 70% of 1000 is your first encumberment stage. default: 70")
                .define("encumbered_threshold", 70.0D);

        OVER_ENCUMBERED_THRESHOLD = BUILDER.comment("\nIf weight is above this, you get slowness 5, cannot jump, and cannot sprint (Negative values will break the mod) default: 100")
                .define("over_encumbered_threshold", 100.0D);

        ALLOW_MULTIPLE_BOOST_ITEMS = BUILDER.comment("\nWhether or not boost item's additional capacities stack. If there are multiple boost items present in your inventory and this is false, it chooses the better boost item. default: true")
                .define("allow_multiple_boost_items", true);

        SINK_IN_WATER_LAVA = BUILDER.comment("\nWhether or not the player sinks in WATER and LAVA while OVER_ENCUMBERED. default: true")
                .define("sink_in_water_and_lava", true);

        LOCK_GUI_TO_CREATIVE = BUILDER.comment("\nIf set to true, only players in creative mode can open the weight edit GUI. default: false")
                .define("lock_gui_to_creative", true);

        UNENCUMBERMENT_LEVEL1_MULTIPLIER = BUILDER.comment("\nMultiplier for Unencumberment Level 1")
                .defineInRange("unencumberment_level_1_multiplier", 1.1, 1, 100.0);
        UNENCUMBERMENT_LEVEL2_MULTIPLIER = BUILDER.comment("\nMultiplier for Unencumberment Level 2")
                .defineInRange("unencumberment_level_2_multiplier", 1.25, 1, 100.0);
        UNENCUMBERMENT_LEVEL3_MULTIPLIER = BUILDER.comment("\nMultiplier for Unencumberment Level 3")
                .defineInRange("unencumberment_level_3_multiplier", 1.5, 1, 100.0);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}