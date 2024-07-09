package net.khofo.encumber.groups;

import net.khofo.encumber.Encumber;
import net.khofo.encumber.overlays.BaseItem;
import net.khofo.encumber.overlays.Group;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class ItemGroups {

    public static Group rootGroup = new Group("Minecraft", 0.0);
    public static final Map<String, List<Item>> groups = new HashMap<>();

    static {
        generateGroups();
        // Initialize the root group and its subgroups/items
    }

    public static void initGroup(Group rootGroup) {
        // Access the existing item weights map
        Map<ResourceLocation, Double> items = Encumber.itemWeights;

        // Convert the map entries to a list and sort them alphabetically by item name
        List<Map.Entry<ResourceLocation, Double>> sortedItems = items.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey((a, b) -> a.toString().compareTo(b.toString())))
                .collect(Collectors.toList());

        // Iterate through the sorted items and add them to the main subgroup
        for (Map.Entry<ResourceLocation, Double> entry : sortedItems) {
            ResourceLocation itemName = entry.getKey();
            Double itemWeight = entry.getValue();
            BaseItem baseItem = new BaseItem(itemName.toString(), itemWeight);
            rootGroup.addSubGroup(baseItem);
        }
    }

    private static void generateGroups() {
        initGroup(rootGroup);
        groups.put("CARPETS", new ArrayList<>());
        groups.put("ARMORS", new ArrayList<>());
        groups.put("STAIRS", new ArrayList<>());
        groups.put("WALLS", new ArrayList<>());
        groups.put("FOODS", new ArrayList<>());
        groups.put("WEAPONS", new ArrayList<>());
        groups.put("TOOLS", new ArrayList<>());
        groups.put("TRAPDOORS", new ArrayList<>());
        groups.put("FENCES", new ArrayList<>());
        groups.put("WOOLS", new ArrayList<>());
        groups.put("TERRACOTTAS", new ArrayList<>());
        groups.put("LOGS", new ArrayList<>());
        groups.put("PLANKS", new ArrayList<>());
        groups.put("MINECRAFT", new ArrayList<>());
        groups.put("DOORS", new ArrayList<>());
        groups.put("BUTTONS", new ArrayList<>());
        groups.put("PRESSURE_PLATES", new ArrayList<>());
        groups.put("STONES", new ArrayList<>());
        groups.put("SOILS", new ArrayList<>());
        groups.put("PLANTS", new ArrayList<>());
        groups.put("EGGS", new ArrayList<>());
        groups.put("SIGNS", new ArrayList<>());
        groups.put("GLASS_PANES", new ArrayList<>());
        groups.put("GLASS", new ArrayList<>());
        groups.put("BANNERS", new ArrayList<>());
        groups.put("CANDLES", new ArrayList<>());
        groups.put("BEDS", new ArrayList<>());
        groups.put("SHULKER_BOXES", new ArrayList<>());



        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item);
            if (itemName != null) {
                if (itemName.getPath().contains("carpet")) {
                    groups.get("CARPETS").add(item);
                }
                if (item instanceof ArmorItem) {
                    groups.get("ARMORS").add(item);
                }
                if (itemName.getPath().contains("stairs")) {
                    groups.get("STAIRS").add(item);
                }
                if (itemName.getPath().contains("wall")) {
                    groups.get("WALLS").add(item);
                }
                if (item.isEdible()) {
                    groups.get("FOODS").add(item);
                }
                if (isWeapon(item)) {
                    groups.get("WEAPONS").add(item);
                }
                if (isTool(item)) {
                    groups.get("TOOLS").add(item);
                }
                if (itemName.getPath().contains("trapdoor")) {
                    groups.get("TRAPDOORS").add(item);
                }
                if (itemName.getPath().contains("fence")) {
                    groups.get("FENCES").add(item);
                }
                if (itemName.getPath().contains("wool")) {
                    groups.get("WOOLS").add(item);
                }
                if (itemName.getPath().contains("terracotta")) {
                    groups.get("TERRACOTTAS").add(item);
                }
                if (itemName.getPath().contains("log") || itemName.getPath().contains("hyphae") || (itemName.getPath().contains("stem") && !itemName.getPath().contains("mushroom"))) {
                    groups.get("LOGS").add(item);
                }
                if (itemName.getPath().contains("plank")) {
                    groups.get("PLANKS").add(item);
                }
                if (itemName.getPath().contains("door") && !itemName.getPath().contains("trapdoor")) {
                    groups.get("DOORS").add(item);
                }
                if (itemName.getPath().contains("button")) {
                    groups.get("BUTTONS").add(item);
                }
                if (itemName.getPath().contains("pressure")) {
                    groups.get("PRESSURE_PLATES").add(item);
                }
                if (
                        (itemName.getPath().contains("stone") &&
                        !(isTool(item)) &&
                        !itemName.getPath().contains("restone") &&
                        !(isWeapon(item)) &&
                        !itemName.getPath().contains("glow") &&
                        !itemName.getPath().contains("grind") &&
                        !itemName.getPath().contains("cutter") &&
                        !itemName.getPath().contains("lode")) ||
                        itemName.getPath().contains("concrete") ||
                        itemName.getPath().contains("calcite") ||
                        itemName.getPath().contains("tuff") ||
                        itemName.getPath().contains("basalt") ||
                        itemName.getPath().contains("terracotta") ||
                        (itemName.getPath().contains("prismarine") && !itemName.getPath().contains("shard") && !itemName.getPath().contains("crystals")) ||
                        (itemName.getPath().contains("deepslate") && !itemName.getPath().contains("ore")) ||
                        itemName.getPath().contains("andesite") ||
                        itemName.getPath().contains("diorite") ||
                        itemName.getPath().contains("granite") ||
                        itemName.getPath().contains("purpur") ||
                        itemName.getPath().contains("obsidian") ||
                        itemName.getPath().contains("magma_block") ||
                        itemName.getPath().contains("minecraft:bricks") ||
                        itemName.getPath().contains("nether_brick") ||
                        (itemName.getPath().contains("nether_bricks") && !itemName.getPath().contains("nether_brick")) ||
                        itemName.getPath().contains("bedrock"))
                {
                    groups.get("STONES").add(item);
                }
                if (itemName.getPath().contains("minecraft:")) {
                    groups.get("MINECRAFT").add(item);
                }
                if (
                        (itemName.getPath().contains("sand") && !itemName.getPath().contains("sandstone")) ||
                        itemName.getPath().contains("grass_block") ||
                                itemName.getPath().contains("podzol") ||
                                itemName.getPath().contains("mycelium") ||
                                itemName.getPath().contains("dirt") ||
                                itemName.getPath().contains("farmland") ||
                                (itemName.getPath().contains("mud") && !itemName.getPath().contains("brick")) ||
                                (itemName.getPath().contains("clay") && !itemName.getPath().contains("ball")) ||
                                itemName.getPath().contains("gravel") ||
                                itemName.getPath().contains("moss_block") ||
                                itemName.getPath().contains("soul_soil") ||
                                itemName.getPath().contains("nylium")
                ) {
                    groups.get("SOILS").add(item);
                }
                if (
                        itemName.getPath().contains("leaves") ||
                        itemName.getPath().contains("sapling") ||
                        itemName.getPath().contains("propagule") ||
                        itemName.getPath().contains("azalea") ||
                        (itemName.getPath().contains("mushroom") && !itemName.getPath().contains("stew")) ||
                        (itemName.getPath().contains("fungus") && !itemName.getPath().contains("stick"))  ||
                        (itemName.getPath().contains("grass") && !itemName.getPath().contains("block")) ||
                        itemName.getPath().contains("fern") ||
                        itemName.getPath().contains("dead_bush") ||
                        itemName.getPath().contains("dandelion") ||
                        itemName.getPath().contains("poppy") ||
                        itemName.getPath().contains("orchid") ||
                        itemName.getPath().contains("allium") ||
                        itemName.getPath().contains("bluet") ||
                        itemName.getPath().contains("tulip") ||
                        itemName.getPath().contains("cornflower") ||
                        itemName.getPath().contains("lily") ||
                        itemName.getPath().contains("torchflower") ||
                        itemName.getPath().contains("rose") ||
                        itemName.getPath().contains("petals") ||
                        itemName.getPath().contains("blossom") ||
                        itemName.getPath().equals("minecraft:bamboo") ||
                        itemName.getPath().equals("minecraft:sugar_cane") ||
                        itemName.getPath().contains("cactus") ||
                        (itemName.getPath().contains("roots") && !itemName.getPath().contains("muddy")) ||
                        itemName.getPath().contains("sprout") ||
                        itemName.getPath().contains("vine") ||
                        itemName.getPath().contains("sunflower") ||
                        itemName.getPath().contains("lilac") ||
                        itemName.getPath().contains("peony") ||
                        itemName.getPath().contains("pitcher") ||
                        itemName.getPath().contains("dripleaf") ||
                        (itemName.getPath().contains("chorus") && !itemName.getPath().contains("fruit")) ||
                        itemName.getPath().contains("lichen") ||
                        itemName.getPath().contains("seeds") ||
                        itemName.getPath().contains("cocoa") ||
                        itemName.getPath().contains("wart") ||
                        itemName.getPath().contains("pickle") ||
                        (itemName.getPath().contains("kelp") && !itemName.getPath().equals("minecraft:dried_kelp")) ||
                        itemName.getPath().contains("coral") ||
                        itemName.getPath().equals("minecraft:melon") ||
                        (itemName.getPath().contains("pumpkin") && !itemName.getPath().contains("pie")) ||
                        itemName.getPath().contains("hay") ||
                        itemName.getPath().contains("wheat")
                ) {
                    groups.get("PLANTS").add(item);
                }
                if ((itemName.getPath().contains("egg") && !itemName.getPath().contains("legg"))) {
                    groups.get("EGGS").add(item);
                }
                if (itemName.getPath().contains("sign")) {
                    groups.get("SIGNS").add(item);
                }
                if (itemName.getPath().contains("_bed")) {
                    groups.get("BEDS").add(item);
                }
                if (itemName.getPath().contains("shulker_box")) {
                    groups.get("SHULKER_BOXES").add(item);
                }
                if (itemName.getPath().contains("candle")) {
                    groups.get("CANDLES").add(item);
                }
                if (itemName.getPath().contains("banner")) {
                    groups.get("BANNERS").add(item);
                }
                if (itemName.getPath().contains("glass") && !itemName.getPath().contains("spyglass") && !itemName.getPath().contains("glass_bottle")) {
                    groups.get("GLASS").add(item);
                }
                if (itemName.getPath().contains("pane")) {
                    groups.get("GLASS_PANES").add(item);
                }
                /*else if (item instanceof BlockItem) {
                    BlockItem blockItem = (BlockItem) item;
                    SoundType soundType = blockItem.getBlock().defaultBlockState().getSoundType();
                    ResourceLocation breakSoundLocation = ForgeRegistries.SOUND_EVENTS.getKey(soundType.getBreakSound());
                    if (breakSoundLocation != null && breakSoundLocation.getPath().contains("stone")) {
                        groups.get("STONES").add(item);
                    }
                }*/
            }
        }
    }

    private static boolean isWeapon(Item item) {
        return item instanceof SwordItem || item instanceof AxeItem || item instanceof BowItem || item instanceof CrossbowItem;
    }

    private static boolean isTool(Item item) {
        return item instanceof PickaxeItem || item instanceof ShovelItem || item instanceof HoeItem;
    }


}
