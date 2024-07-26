package net.khofo.encumber;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import net.khofo.encumber.commands.WeightCommands;
import net.khofo.encumber.configs.ClientConfigs;
import net.khofo.encumber.configs.CommonConfigs;
import net.khofo.encumber.enchantment.UnencumbermentEnchant;
import net.khofo.encumber.events.ClientEventHandler;
import net.khofo.encumber.events.TooltipEvent;
import net.khofo.encumber.events.WeightEvent;
import net.khofo.encumber.UIElements.WeightOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Encumber.MOD_ID)
public class Encumber {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type ITEM_WEIGHT_TYPE = new TypeToken<Map<String, Double>>() {}.getType();
    public static Map<ResourceLocation, Double> itemWeights = new HashMap<>();
    public static final String MOD_ID = "encumber";
    private static final Map<String, Double> defaultCategoryWeights = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    static {
        defaultCategoryWeights.put("weapons", 2.0);
        defaultCategoryWeights.put("armor", 12.0);
        defaultCategoryWeights.put("tools", 3.0);
        defaultCategoryWeights.put("resources", 8.0);
        defaultCategoryWeights.put("consumables", 0.25);
        defaultCategoryWeights.put("miscellaneous", 1.0);
    }
    public Encumber()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(new WeightEvent());
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfigs.SPEC, "encumber-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigs.CLIENT_SPEC, "encumber-client.toml");
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.KeyInputHandler.class);
        ENCHANTMENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        loadItemWeights();
        MinecraftForge.EVENT_BUS.register(WeightCommands.class);
    }

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "encumber");

    public static final RegistryObject<Enchantment> UNENCUMBERMENT = ENCHANTMENTS.register("unencumberment",
            () -> new UnencumbermentEnchant(Enchantment.Rarity.RARE, EquipmentSlot.LEGS));


    public static void updateConfigWeights() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("item_weights.json");

        Map<String, Double> weightsToSave = new HashMap<>();
        itemWeights.forEach((key, value) -> weightsToSave.put(key.toString(), value));

        String json = GSON.toJson(weightsToSave, ITEM_WEIGHT_TYPE);

        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            writer.write(json);
        } catch (IOException e) {
            System.err.println("Error saving item weights: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadItemWeights() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("item_weights.json");

        // Load existing weights from the config file
        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            String rawJson = reader.lines().collect(Collectors.joining("\n"));
            Map<String, Double> weights = GSON.fromJson(rawJson, ITEM_WEIGHT_TYPE);
            if (weights != null) {
                weights.forEach((key, value) -> itemWeights.put(new ResourceLocation(key), value));
            } else {
                System.err.println("item_weights.json is empty or incorrectly formatted.");
            }
        } catch (IOException e) {
            System.err.println("Error loading item weights: " + e.getMessage());
            e.printStackTrace();
        }

        // Assign default weights to items not present in the config file
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item);
            if (!itemWeights.containsKey(itemName)) {
                double weight = assignWeightByCategory(item);
                itemWeights.put(itemName, weight);
                System.out.printf("Item: %s, Category Weight: %s%n", itemName, weight); // Debugging statement
            }
        }

        // Save updated weights back to the config file
        updateConfigWeights();
    }

    public static double getItemWeight(ResourceLocation itemName) {
        if (itemWeights.containsKey(itemName)) {
            return itemWeights.get(itemName);
        }

        Item item = ForgeRegistries.ITEMS.getValue(itemName);
        if (item == null) {
            return 1.0; // Default weight if the item is not found in the registry
        }

        double weight = assignWeightByCategory(item);
        itemWeights.put(itemName, weight);
        updateConfigWeights(); // Save the new weight to the config file
        return weight;
    }

    private static double assignWeightByCategory(Item item) {
        if (item instanceof SwordItem || item instanceof AxeItem) {
            return defaultCategoryWeights.get("weapons");
        } else if (item instanceof ArmorItem) {
            return defaultCategoryWeights.get("armor");
        } else if (item instanceof PickaxeItem || item instanceof ShovelItem || item instanceof HoeItem) {
            return defaultCategoryWeights.get("tools");
        } else if (ForgeRegistries.ITEMS.getKey(item).toString().contains("ingot")) {
            return defaultCategoryWeights.get("resources");
        } else if (item.isEdible()) {
            return defaultCategoryWeights.get("consumables");
        } else {
            return defaultCategoryWeights.get("miscellaneous");
        }
    }

    private static boolean isTaggedAs(Item item, String tagName) {
        TagKey<Item> tagKey = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(tagName));
        boolean isTagged = item.builtInRegistryHolder().is(tagKey);
        System.out.printf("Item: %s, Tag: %s, IsTagged: %s%n", ForgeRegistries.ITEMS.getKey(item), tagName, isTagged); // Debugging statement
        return isTagged;
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MinecraftForge.EVENT_BUS.register(WeightOverlay.class);
            MinecraftForge.EVENT_BUS.register(new TooltipEvent());
        }
    }
}
