package com.example.waterbucketrelease;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = WaterBucketRelease.MODID, name = "WATER BUCKET. RELEASE! (Backport)", version = "1.0.0",
        acceptedMinecraftVersions = "[1.12.2]")
public class WaterBucketRelease {
    public static final String MODID = "waterbucketrelease";

    @Mod.Instance(MODID)
    public static WaterBucketRelease instance;

    @SidedProxy(clientSide = "com.example.waterbucketrelease.ClientProxy",
                serverSide = "com.example.waterbucketrelease.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        // frequência de update 1 + velocidades: projétil muito rápido precisa de sync a cada tick
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "water_shot"),
                EntityWaterShot.class, "water_shot", 0, instance, 64, 1, true);
        MinecraftForge.EVENT_BUS.register(new ReleaseHandler());
        proxy.preInit();
    }
}
