package com.stuntguy3000.minecraft.targetpractice.core.objects;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Data
public class ShotStatistic {
    private final OfflinePlayer offlinePlayer;

    private final UUID uuid;
    private final double distance;
    private final int accuracy;

    public ShotStatistic(UUID uuid, double distance, int accuracy) {
        this.uuid = uuid;
        this.accuracy = accuracy;

        // Round Distance to 2 Significant Digits
        // Java is stupid - https://stackoverflow.com/questions/11701399/round-up-to-2-decimal-places-in-java
        this.distance = Math.round(distance * 100.0) / 100.0;

        // Derive offlinePlayer from UUID
        this.offlinePlayer = Bukkit.getOfflinePlayer(uuid);
    }
}
