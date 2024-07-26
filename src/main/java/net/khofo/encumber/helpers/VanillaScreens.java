package net.khofo.encumber.helpers;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;

import java.util.HashSet;
import java.util.Set;

public class VanillaScreens {
    private static final Set<Class<?>> vanillaScreenClasses = new HashSet<>();

    static {
        vanillaScreenClasses.add(InventoryScreen.class);
        vanillaScreenClasses.add(ContainerScreen.class);
        vanillaScreenClasses.add(FurnaceScreen.class);
        vanillaScreenClasses.add(CraftingScreen.class);
        vanillaScreenClasses.add(EnchantmentScreen.class);
        vanillaScreenClasses.add(HopperScreen.class);
        vanillaScreenClasses.add(LoomScreen.class);
        vanillaScreenClasses.add(DispenserScreen.class);
        vanillaScreenClasses.add(ShulkerBoxScreen.class);
        vanillaScreenClasses.add(SmithingScreen.class);
        vanillaScreenClasses.add(CartographyTableScreen.class);
        vanillaScreenClasses.add(StonecutterScreen.class);
        vanillaScreenClasses.add(AnvilScreen.class);
    }

    public static boolean isVanillaScreen(Screen screen) {
        return vanillaScreenClasses.contains(screen.getClass());
    }

}