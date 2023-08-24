/*
 * MIT License
 *
 * Copyright (c) 2020 Luke Anderson (stuntguy3000)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.stuntguy3000.minecraft.targetpractice;

import com.stuntguy3000.minecraft.targetpractice.command.TargetPracticeCommand;
import com.stuntguy3000.minecraft.targetpractice.core.plugin.MinecraftPlugin;
import com.stuntguy3000.minecraft.targetpractice.core.plugin.storage.config.MainConfig;
import com.stuntguy3000.minecraft.targetpractice.event.EntityEvents;
import com.stuntguy3000.minecraft.targetpractice.event.PlayerEvents;
import com.stuntguy3000.minecraft.targetpractice.handler.ArrowTrackingHandler;
import com.stuntguy3000.minecraft.targetpractice.handler.ConfigHandler;
import com.stuntguy3000.minecraft.targetpractice.handler.SchedulerHandler;
import com.stuntguy3000.minecraft.targetpractice.handler.StatsHandler;
import lombok.Getter;

import java.sql.SQLException;

/**
 * Represents the entry point/main class for this plugin
 */
@Getter
public final class PluginMain extends MinecraftPlugin {

    @Getter
    private static PluginMain instance;

    private ConfigHandler configHandler;
    private ArrowTrackingHandler arrowTrackingHandler;
    private SchedulerHandler schedulerHandler;
    private StatsHandler statsHandler;

    @Override
    public void registerHandlers() {
        configHandler = new ConfigHandler();
        configHandler.registerConfiguration(new MainConfig());
        configHandler.loadConfigurations();

        arrowTrackingHandler = new ArrowTrackingHandler();
        schedulerHandler = new SchedulerHandler();
        statsHandler = new StatsHandler();
    }

    @Override
    public void registerCommands() {
        TargetPracticeCommand command = new TargetPracticeCommand(this);

        this.getCommand("TargetPractice").setExecutor(command);
        this.getCommand("TargetPractice").setTabCompleter(command);
    }

    @Override
    public void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityEvents(this), this);
    }

    @Override
    public void registerTasks() {
        if (MainConfig.getConfig().isEnableAggressiveArrowCleanup()) {
            getLogger().info("Aggressive arrow cleanup task running.");
            schedulerHandler.scheduleArrowScanner();
        }
    }

    @Override
    public void setInstance() {
        instance = this;
    }

    @Override
    public void onDisable() {
        try {
            getStatsHandler().closeDatabase();
        } catch (SQLException ignore) {

        }
    }
}
