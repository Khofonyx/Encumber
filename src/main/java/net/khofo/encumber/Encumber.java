package net.khofo.encumber;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import net.khofo.encumber.commands.WeightCommands;
import net.khofo.encumber.configs.Configs;
import net.khofo.encumber.events.WeightEvent;
import net.khofo.encumber.overlays.WeightOverlay;
import net.minecraft.resources.ResourceLocation;
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
    private static final Logger LOGGER = LogUtils.getLogger();
    public Encumber()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(new WeightEvent());
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.SPEC, "encumber-common.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        loadItemWeights();
        MinecraftForge.EVENT_BUS.register(WeightCommands.class);
    }

    public static void updateConfigWeights(){
        // Get the path to item_weight.json from configs
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
        // Get Path to item_weight.json from Configs
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("item_weights.json");

        // If we find the config file doesn't exist, copy the default one from the resources folder to the configs
        if (!Files.exists(configPath)) {
            copyDefaultConfig(configPath);
        } else {
            System.out.println("item_weights.json found in config directory.");
        }

        // Try to read in the content from the Json config file.
        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            String rawJson = reader.lines().collect(Collectors.joining("\n"));

            Map<String, Double> weights = GSON.fromJson(rawJson, ITEM_WEIGHT_TYPE);
            if (weights == null) {
                System.err.println("item_weights.json is empty or incorrectly formatted.");
            } else {
                weights.forEach((key, value) -> itemWeights.put(new ResourceLocation(key), value));
                itemWeights.forEach((key, value) -> System.out.printf("Item: %s, Weight: %s%n", key, value));
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing item_weights.json: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error loading item weights: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void copyDefaultConfig(Path configPath) {
        try (InputStream inputStream = getClass().getResourceAsStream("/assets/encumber/item_weights.json")) {
            if (inputStream == null) {
                System.err.println("Default item_weights.json not found in resources");
                return;
            }
            Files.createDirectories(configPath.getParent());
            try (OutputStream outputStream = Files.newOutputStream(configPath)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }

            System.out.println("Copied default item_weights.json to config directory");
        } catch (IOException e) {
            System.err.println("Error copying default item_weights.json to config directory");
            e.printStackTrace();
        }
    }

    public static double getItemWeight(ResourceLocation item) {
        return itemWeights.getOrDefault(item, 1.0); // Default weight if not found
    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MinecraftForge.EVENT_BUS.register(WeightOverlay.class);
        }
    }
}
