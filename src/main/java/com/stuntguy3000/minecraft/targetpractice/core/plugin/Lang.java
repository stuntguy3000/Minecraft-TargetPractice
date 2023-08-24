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

package com.stuntguy3000.minecraft.targetpractice.core.plugin;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Functions and Strings of Language elements used by the plugin
 */
public class Lang {
    public static final String PLUGIN_PREFIX = "§3TargetPractice §f» §7";

    public static final String COMMAND_VERSION = "§7TargetPractice version §f%s§7 by §astuntguy3000§7.";
    public static final String COMMAND_HELP_TITLE = "§bCommand Help:";
    public static final String COMMAND_HELP_ENTRY = "§7/%s §e%s §8- §f%s";

    public static final String STATS_RECORD = "§b%s: §e%s§8/§715 §7@ §a%sm";
    public static final String STATS_RECORD_BULLSEYE = "§b%s: §e§lBULLSEYE! §7@ §a%sm";

    public static final String ERROR_PREFIX = "§cError: ";
    public static final String ERROR_PERMISSION_DENIED = ERROR_PREFIX + "You do not have permission to perform this action.";
    public static final String ERROR_NOT_PLAYER = ERROR_PREFIX + "You must be a player to perform this action.";
    public static final String ERROR_PLAYER_NOT_FOUND = ERROR_PREFIX + "Player not found.";

    public static final String EVENT_TARGET_HIT = "§eHit! §8(§e%s§8/§715 §7@ §a%sm§8)";
    public static final String EVENT_TARGET_HIT_BULLSEYE = "§e§lBULLSEYE! §7@ §a%sm";

    public static final String STATS_NEW_RECORD = "§b§lNew Personal Best!";
    public static final String STATS_TOP = "§e§lTop 5 Shots";
    public static final String ERROR_NO_TOP_SHOTS = ERROR_PREFIX + "No shots recorded!";

    /**
     * Send a message to a CommandSender.
     * If sender is a Player, the message will be sent with the plugin's message prefix.
     *
     * @param sender  CommandSender the entity to send the message to.
     * @param message String the message to send.
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(PLUGIN_PREFIX + message);
        } else {
            sender.sendMessage(ChatColor.stripColor(message));
        }
    }

    /**
     * Send a formatted message to a CommandSender.
     * If sender is a Player, the message will be sent with the plugin's message prefix.
     *
     * @param sender  CommandSender the entity to send the message to.
     * @param message String the message to send.
     * @param format  Object[] format objects
     */
    public static void sendMessage(CommandSender sender, String message, Object... format) {
        sendMessage(sender, String.format(message, format));
    }
}
