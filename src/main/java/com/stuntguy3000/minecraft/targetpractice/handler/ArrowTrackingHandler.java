package com.stuntguy3000.minecraft.targetpractice.handler;

import com.stuntguy3000.minecraft.targetpractice.PluginMain;
import com.stuntguy3000.minecraft.targetpractice.core.objects.ShotStatistic;
import com.stuntguy3000.minecraft.targetpractice.core.objects.TrackedArrow;
import com.stuntguy3000.minecraft.targetpractice.core.plugin.Lang;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrowTrackingHandler {
    private final PluginMain plugin;

    @Getter
    private List<TrackedArrow> trackedArrows = new ArrayList<>();

    public ArrowTrackingHandler() {
        this.plugin = PluginMain.getInstance();
    }

    public void trackArrow(Arrow arrow) throws AssertionError {
        assert arrow != null;
        assert arrow.getShooter() instanceof Player;

        trackedArrows.add(new TrackedArrow(arrow));
    }

    public TrackedArrow getTrackedArrow(Arrow arrow) throws AssertionError {
        assert arrow != null;

        for (TrackedArrow trackedArrow : trackedArrows) {
            if (trackedArrow.getArrow().equals(arrow)) {
                return trackedArrow;
            }
        }

        return null;
    }

    public List<TrackedArrow> getTrackedArrows(Player player) throws AssertionError {
        assert player != null;

        List<TrackedArrow> playerTrackedArrows = new ArrayList<>();

        for (TrackedArrow trackedArrow : trackedArrows) {
            if (trackedArrow.getShooter().equals(player)) {
                playerTrackedArrows.add(trackedArrow);
            }
        }

        return playerTrackedArrows.isEmpty() ? null : playerTrackedArrows;
    }

    public boolean untrackArrow(Arrow arrow) {
        try {
            return trackedArrows.remove(getTrackedArrow(arrow));
        } catch (NullPointerException ignored) {
        }
        return false;
    }

    public boolean untrackArrows(Player player) {
        try {
            return trackedArrows.removeAll(getTrackedArrows(player));
        } catch (NullPointerException ignored) {

        }
        return false;
    }

    public void removeInvalidArrows() {
        Iterator<TrackedArrow> iterator = trackedArrows.iterator();

        while (iterator.hasNext()) {
            Arrow arrow = iterator.next().getArrow();

            if (arrow == null || arrow.isDead() || !arrow.isValid()) {
                iterator.remove();
                return;
            }
        }
    }

    public void processPlayerHitTargetBlock(Player player, TrackedArrow trackedArrow, Block targetBlock) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player != null && trackedArrow != null && targetBlock != null) {
                Arrow arrow = trackedArrow.getArrow();

                if (arrow != null && arrow.isValid()) {
                    // Calculate the shot
                    double distance = trackedArrow.getArrow().getLocation().distance(trackedArrow.getShotFrom().getLocation());
                    int accuracy = ((AnaloguePowerable) targetBlock.getBlockData()).getPower();

                    ShotStatistic shotStatistic = new ShotStatistic(player.getUniqueId(), distance, accuracy);

                    // Send the player a notification of their hit
                    if (accuracy == 15) {
                        Lang.sendMessage(player, Lang.EVENT_TARGET_HIT_BULLSEYE, shotStatistic.getDistance());
                    } else {
                        Lang.sendMessage(player, Lang.EVENT_TARGET_HIT, accuracy, shotStatistic.getDistance());
                    }

                    // Process stats
                    plugin.getStatsHandler().trySaveShotStatistic(player, shotStatistic);
                }
            }
        }, 1L);
    }
}