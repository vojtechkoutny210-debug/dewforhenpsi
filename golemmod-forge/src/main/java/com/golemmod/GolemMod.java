package com.golemmod;

import com.golemmod.event.GolemLavaEvent;
import com.golemmod.event.GolemSpawnEvent;
import com.golemmod.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GolemMod.MODID)
public class GolemMod {

    public static final String MODID = "golemmod";

    public GolemMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registrace itemů
        ModItems.ITEMS.register(modBus);

        // Registrace event handlerů
        MinecraftForge.EVENT_BUS.register(new GolemSpawnEvent());
        MinecraftForge.EVENT_BUS.register(new GolemLavaEvent());
    }
}
