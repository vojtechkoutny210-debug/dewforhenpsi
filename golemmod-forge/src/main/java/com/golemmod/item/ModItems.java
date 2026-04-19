package com.golemmod.item;

import com.golemmod.GolemMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GolemMod.MODID);

    public static final RegistryObject<Item> ACTIVATOR_ROD =
            ITEMS.register("activator_rod",
                    () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ACTIVATOR_FRAME =
            ITEMS.register("activator_frame",
                    () -> new Item(new Item.Properties().stacksTo(16)));
}
