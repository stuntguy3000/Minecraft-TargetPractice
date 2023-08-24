package com.stuntguy3000.minecraft.targetpractice.handler;

import com.stuntguy3000.minecraft.targetpractice.PluginMain;

public class SchedulerHandler {
    private final PluginMain plugin;

    public SchedulerHandler() {
        this.plugin = PluginMain.getInstance();
    }

    public void scheduleArrowScanner() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> plugin.getArrowTrackingHandler().removeInvalidArrows(), 1L, 1L);
    }
}
