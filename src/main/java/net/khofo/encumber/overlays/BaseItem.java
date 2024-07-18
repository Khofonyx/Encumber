package net.khofo.encumber.overlays;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BaseItem implements GroupItem {
    private final String name;
    private double weight;
    private final ItemStack itemStack;

    public BaseItem(String name, double weight) {
        this.name = name;
        this.weight = weight;
        this.itemStack = createItemStackFromRegistryName(name);
        if (itemStack.isEmpty()) {
            System.out.println("ItemStack creation failed for name: " + name);
        } else {
            System.out.println("ItemStack created for: " + name);
        }
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    private ItemStack createItemStackFromRegistryName(String registryName) {
        ResourceLocation resourceLocation = new ResourceLocation(registryName);
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
        if (item != null) {
            return new ItemStack(item);
        } else {
            System.out.println("Item not found for registry name: " + registryName);
            return ItemStack.EMPTY;
        }
    }
}