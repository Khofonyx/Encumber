package net.khofo.encumber.configs;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfigs {
    public static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> ANVIL_UI_X_OFFSET;
    public static final ForgeConfigSpec.ConfigValue<Integer> ANVIL_UI_Y_OFFSET;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TOGGLE_ANVIL_ICON;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TOGGLE_WEIGHT_TEXT;

    static {
        CLIENT_BUILDER.push("Client Configs for Encumbered:");

        ANVIL_UI_X_OFFSET = CLIENT_BUILDER.comment("\nSet the horizontal offset for the anvil UI. default: 0")
                .define("anvil_ui_x_offset", 0);

        ANVIL_UI_Y_OFFSET = CLIENT_BUILDER.comment("\nSet the vertical offset for the anvil UI. default: 0")
                .define("anvil_ui_y_offset", 0);

        TOGGLE_ANVIL_ICON = CLIENT_BUILDER.comment("\nWhether or not the anvil icon appears. default: true")
                .define("toggle_anvil_icon", true);

        TOGGLE_WEIGHT_TEXT = CLIENT_BUILDER.comment("\nWhether or not the weight text appears. default: true")
                .define("toggle_weight_text", true);

        CLIENT_BUILDER.pop();
        CLIENT_SPEC = CLIENT_BUILDER.build();
    }
}
