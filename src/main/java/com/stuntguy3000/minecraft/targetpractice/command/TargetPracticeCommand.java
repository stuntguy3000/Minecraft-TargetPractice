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

package com.stuntguy3000.minecraft.targetpractice.command;

import com.stuntguy3000.minecraft.targetpractice.PluginMain;
import com.stuntguy3000.minecraft.targetpractice.core.plugin.Lang;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Handles the processing of the /targetpractice command
 *
 * @author stuntguy3000
 */
@Data
@AllArgsConstructor
public class TargetPracticeCommand implements CommandExecutor, TabExecutor {
    private PluginMain pluginMain;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1: {
                if (args[0].equalsIgnoreCase("version")) {
                    Lang.sendMessage(sender, Lang.COMMAND_VERSION, PluginMain.getInstance().getDescription().getVersion());
                    return true;
                } else if (args[0].equalsIgnoreCase("stats")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        pluginMain.getStatsHandler().sendPlayerStats(sender, player.getUniqueId());
                    } else {
                        Lang.sendMessage(sender, Lang.ERROR_NOT_PLAYER);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("topstats")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        pluginMain.getStatsHandler().sendTopStats(sender);
                    } else {
                        Lang.sendMessage(sender, Lang.ERROR_NOT_PLAYER);
                    }
                    return true;
                }
            }
            case 2: {
                if (args[0].equalsIgnoreCase("stats")) {
                    String targetName = args[1];

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);

                    if (offlinePlayer.getName() != null) {
                        pluginMain.getStatsHandler().sendPlayerStats(sender, offlinePlayer.getUniqueId());
                    } else {
                        Lang.sendMessage(sender, Lang.ERROR_PLAYER_NOT_FOUND);
                    }
                    return true;
                }
            }
        }

        // Help Menu
        Lang.sendMessage(sender, Lang.COMMAND_HELP_TITLE);

        Lang.sendMessage(sender, Lang.COMMAND_HELP_ENTRY, label, "stats [name]", "View the stats for a player");
        Lang.sendMessage(sender, Lang.COMMAND_HELP_ENTRY, label, "topstats", "View the stats leaderboard");
        Lang.sendMessage(sender, Lang.COMMAND_HELP_ENTRY, label, "version", "View plugin information");
        Lang.sendMessage(sender, Lang.COMMAND_HELP_ENTRY, label, "help", "View plugin commands");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("stats", "topstats", "version", "help");
        }

        return null;
    }
}
