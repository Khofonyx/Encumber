package net.khofo.encumber.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.khofo.encumber.Encumber;
import net.khofo.encumber.configs.CommonConfigs;
import net.khofo.encumber.groups.ItemGroups;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

public class WeightCommands {

    public WeightCommands(Map<ResourceLocation, Double> weights) {
    }

    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
        WeightCommands.register(commandDispatcher);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("weight")
                        .then(Commands.literal("get")
                                .then(Commands.argument("item", ResourceLocationArgument.id())
                                        .executes(context -> getItemWeight(context, ResourceLocationArgument.getId(context, "item")))))
                        .then(Commands.literal("set")
                                .then(Commands.argument("item", ResourceLocationArgument.id())
                                        .then(Commands.argument("weight", DoubleArgumentType.doubleArg())
                                                .executes(context -> setItemWeight(context, ResourceLocationArgument.getId(context, "item"), DoubleArgumentType.getDouble(context, "weight"))))))
                        .then(Commands.literal("setgroup")
                                .then(Commands.argument("group", StringArgumentType.string())
                                        .then(Commands.argument("weight", DoubleArgumentType.doubleArg())
                                                .executes(context -> {
                                                    String group = StringArgumentType.getString(context, "group").toUpperCase();
                                                    double weight = DoubleArgumentType.getDouble(context, "weight");
                                                    return setGroupWeight(context, group, weight);
                                                }))))
                        .then(Commands.literal("boost")
                                .then(Commands.literal("add")
                                        .then(Commands.argument("item", ResourceLocationArgument.id())
                                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg())
                                                        .executes(context -> {
                                                            ResourceLocation item = ResourceLocationArgument.getId(context, "item");
                                                            double amount = DoubleArgumentType.getDouble(context, "amount");
                                                            return addBoostItem(context, item, amount);
                                                        }))))
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("item", ResourceLocationArgument.id())
                                                .executes(context -> removeBoostItem(context, ResourceLocationArgument.getId(context, "item"))))))
                        .then(Commands.literal("boost_armor")
                                .then(Commands.literal("add")
                                        .then(Commands.argument("item", ResourceLocationArgument.id())
                                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg())
                                                        .executes(context -> {
                                                            ResourceLocation item = ResourceLocationArgument.getId(context, "item");
                                                            double amount = DoubleArgumentType.getDouble(context, "amount");
                                                            return addBoostArmor(context, item, amount);
                                                        }))))
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("item", ResourceLocationArgument.id())
                                                .executes(context -> removeBoostArmor(context, ResourceLocationArgument.getId(context, "item"))))))
                        .then(Commands.literal("threshold")
                                .then(Commands.literal("encumbered")
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                .executes(context -> setEncumberedThreshold(context, DoubleArgumentType.getDouble(context, "value")))))
                                .then(Commands.literal("over_encumbered")
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                .executes(context -> setOverEncumberedThreshold(context, DoubleArgumentType.getDouble(context, "value")))))
                        )
                        .then(Commands.literal("help")
                                .executes(context -> {
                                    context.getSource().sendSuccess(() -> Component.literal("Available commands:"), true);
                                    context.getSource().sendSuccess(() -> Component.literal("/weight get <item> - Gets the weight of a item. <item> should be the registry name (Ex. minecraft:stone)"), true);
                                    context.getSource().sendSuccess(() -> Component.literal("/weight set <item> <weight> - Sets the weight of a specified item"), true);
                                    context.getSource().sendSuccess(() -> Component.literal("/weight setgroup <group> <weight> - Sets the weight for a predefined group of items"), true);
                                    context.getSource().sendSuccess(() -> Component.literal("/weight boost add <item> <amount> - Creates a boost item"), true);
                                    context.getSource().sendSuccess(() -> Component.literal("/weight boost remove <item> - Removes a boost item"), true);
                                    context.getSource().sendSuccess(() -> Component.literal("/weight boost_armor add <item> <amount> - Creates a boost armor item"), true);
                                    context.getSource().sendSuccess(() -> Component.literal("/weight boost_armor remove <item> - Removes a boost armor item"), true);
                                    context.getSource().sendSuccess(() -> Component.literal("/weight threshold encumbered <value> - Sets the encumbered (yellow) multiplier"), true);
                                    context.getSource().sendSuccess(() -> Component.literal("/weight threshold over_encumbered <value> - Sets the over_encumbered (red) threshold"), true);
                                    return 1;
                                }))
        );
    }

    private static int setEncumberedThreshold(CommandContext<CommandSourceStack> context, double value) {
        CommonConfigs.ENCUMBERED_THRESHOLD_MULTIPLIER.set(value);
        context.getSource().sendSuccess(() -> Component.literal("Set Encumbered Multiplier To: " + value), true);
        return 1;
    }

    private static int setOverEncumberedThreshold(CommandContext<CommandSourceStack> context, double value) {
        CommonConfigs.OVER_ENCUMBERED_THRESHOLD.set(value);
        context.getSource().sendSuccess(() -> Component.literal("Set Over_Encumbered Threshold To: " + value), true);
        return 1;
    }




    private static int addBoostItem(CommandContext<CommandSourceStack> context, ResourceLocation item, double amount) {
        List<String> boostItems = CommonConfigs.BOOST_ITEMS.get();
        List<Double> boostAmounts = CommonConfigs.BOOST_ITEMS_AMOUNT.get();

        String itemName = item.toString();

        if (!boostItems.contains(itemName)) {
            boostItems.add(itemName);
            boostAmounts.add(amount);
        } else {
            int index = boostItems.indexOf(itemName);
            boostAmounts.set(index, amount);
        }

        CommonConfigs.BOOST_ITEMS.set(boostItems);
        CommonConfigs.BOOST_ITEMS_AMOUNT.set(boostAmounts);

        context.getSource().sendSuccess(() -> Component.literal("Successfully made " + itemName + " a boost item with boost amount: " + amount), true);

        return 1;
    }

    private static int removeBoostItem(CommandContext<CommandSourceStack> context, ResourceLocation item) {
        List<String> boostItems = CommonConfigs.BOOST_ITEMS.get();
        List<Double> boostAmounts = CommonConfigs.BOOST_ITEMS_AMOUNT.get();

        String itemName = item.toString();

        if (boostItems.contains(itemName)) {
            int index = boostItems.indexOf(itemName);
            boostItems.remove(index);
            boostAmounts.remove(index);

            CommonConfigs.BOOST_ITEMS.set(boostItems);
            CommonConfigs.BOOST_ITEMS_AMOUNT.set(boostAmounts);

            context.getSource().sendSuccess(() -> Component.literal("Successfully removed " + itemName + " from boost items."), true);
        } else {
            context.getSource().sendFailure(Component.literal("Item " + itemName + " is not in the boost items list."));
        }

        return 1;
    }

    private static int addBoostArmor(CommandContext<CommandSourceStack> context, ResourceLocation item, double amount) {
        List<String> boostArmors = CommonConfigs.BOOST_ARMORS.get();
        List<Double> boostArmorsAmount = CommonConfigs.BOOST_ARMORS_AMOUNT.get();

        String itemName = item.toString();

        if (!boostArmors.contains(itemName)) {
            boostArmors.add(itemName);
            boostArmorsAmount.add(amount);
        } else {
            int index = boostArmors.indexOf(itemName);
            boostArmorsAmount.set(index, amount);
        }

        CommonConfigs.BOOST_ARMORS.set(boostArmors);
        CommonConfigs.BOOST_ARMORS_AMOUNT.set(boostArmorsAmount);

        context.getSource().sendSuccess(() -> Component.literal("Successfully made " + itemName + " a boost armor with boost amount: " + amount), true);

        return 1;
    }

    private static int removeBoostArmor(CommandContext<CommandSourceStack> context, ResourceLocation item) {
        List<String> boostArmors = CommonConfigs.BOOST_ARMORS.get();
        List<Double> boostArmorsAmount = CommonConfigs.BOOST_ARMORS_AMOUNT.get();

        String itemName = item.toString();

        if (boostArmors.contains(itemName)) {
            int index = boostArmors.indexOf(itemName);
            boostArmors.remove(index);
            boostArmorsAmount.remove(index);

            CommonConfigs.BOOST_ARMORS.set(boostArmors);
            CommonConfigs.BOOST_ARMORS_AMOUNT.set(boostArmorsAmount);

            context.getSource().sendSuccess(() -> Component.literal("Successfully removed " + itemName + " from boost armors."), true);
        } else {
            context.getSource().sendFailure(Component.literal("Item " + itemName + " is not in the boost armors list."));
        }

        return 1;
    }

    private static int getItemWeight(CommandContext<CommandSourceStack> context, ResourceLocation item) {
        try {
            Double weight = Encumber.itemWeights.get(item);
            if (weight == null){
                context.getSource().sendFailure(Component.literal("Item " + item + " not found in the config file."));
                return 0;
            }else{
                context.getSource().sendSuccess(() -> Component.literal("Weight of " + item + ": " + weight), false);
                return 1;
            }
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error getting weight: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }

    public static int setItemWeight(CommandContext<CommandSourceStack> context, ResourceLocation item, double weight) {
        try {
            Encumber.itemWeights.put(item, weight);
            Encumber.updateConfigWeights();
            context.getSource().sendSuccess(() -> Component.literal("Set weight of " + item + " to " + weight), false);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error setting weight: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }

    public static int setGroupWeight(CommandContext<CommandSourceStack> context, String group, double weight) {
        if (ItemGroups.groups.containsKey(group)) {
            List<Item> items = ItemGroups.groups.get(group);
            for (Item item : items) {
                ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item);
                Encumber.itemWeights.put(itemName, weight);
            }
            Encumber.updateConfigWeights();
            context.getSource().sendSuccess(() -> Component.literal("Set weight of group " + group + " to " + weight), false);
            return 1;
        } else {
            context.getSource().sendFailure(Component.literal("Group " + group + " not found"));
            return 0;
        }
    }
}
