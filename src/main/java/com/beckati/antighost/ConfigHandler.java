package com.beckati.antighost;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ConfigHandler {
    public static Configuration config;

    private static final int defaultTicksPerAutoRun = 20;
    public static int ticksPerAutoRun = defaultTicksPerAutoRun;

    public static void init(File file) {
        config = new Configuration(file);
        String category;

        category = "Client Config";
        config.addCustomCategoryComment(category, "Client-side configuration");
        ticksPerAutoRun = config.getInt("ticksPerAutoRun", category, defaultTicksPerAutoRun, 20, 500, "Ticks per automatic block request");
    }

    public static void registerConfig(FMLPreInitializationEvent event) {
        AntiGhost.config = new File(event.getModConfigurationDirectory() + "/" + AntiGhost.MODID);
        AntiGhost.config.mkdirs();
        init(new File(AntiGhost.config.getPath(), AntiGhost.MODID + ".cfg"));
    }
}
