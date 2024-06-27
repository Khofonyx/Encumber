package net.khofo.encumber.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.khofo.encumber.Encumber;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
        );
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

    private static int setItemWeight(CommandContext<CommandSourceStack> context, ResourceLocation item, double weight) {
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
}
