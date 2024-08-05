package net.khofo.encumber.UIElements;


import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BaseItem implements GroupItem {
    /**
     * name: the name of the BaseItem
     * weight: the weight of the BaseItem
     * itemStack: the ItemStack of the BaseItem, used to access the registry image of the item.
     */
    private final String name;
    private double weight;
    private final ItemStack itemStack;

    /**
     * Default constructor to create a BaseItem
     */
    public BaseItem(String name, double weight) {
        this.name = name;
        this.weight = weight;
        this.itemStack = createItemStackFromRegistryName(name);
    }

    /**
     * Method to get the weight of the BaseItem
     */
    @Override
    public double getWeight() {
        return weight;
    }

    /**
     * Method to set the weight of the BaseItem
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Method to get the name of the BaseItem
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * method to get the ItemStack of the BaseItem
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Method to create the item stack of the BaseItem
     */
    private ItemStack createItemStackFromRegistryName(String registryName) {
        // Grab the resourceLocation from the registry name
        ResourceLocation resourceLocation = new ResourceLocation(registryName);
        // Get the item from the registry
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
        if (item != null) {
            // If the item was successfully grabbed from the registry, create a new ItemStack for it.
            return new ItemStack(item);
        } else {
            // If the item was not grabbed successfully, return an empty item stack and print an error.
            System.out.println("Item not found for registry name: " + registryName);
            return ItemStack.EMPTY;
        }
    }

    public List<Component> getTooltipInfo(@Nullable Player pPlayer, TooltipFlag pIsAdvanced){
        return this.itemStack.getTooltipLines(pPlayer,pIsAdvanced);
    }

}