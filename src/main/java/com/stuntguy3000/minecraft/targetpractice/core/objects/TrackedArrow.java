package com.stuntguy3000.minecraft.targetpractice.core.objects;

import lombok.Data;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

/**
 * Represents an arrow (typically still in the air) being tracked with metadata
 */
@Data
public class TrackedArrow {
    private Player shooter;
    private Arrow arrow;
    private WorldVector shotFrom;

    public TrackedArrow(Arrow arrow) throws AssertionError {
        assert arrow != null;
        assert arrow.getShooter() != null;
        assert arrow.getShooter() instanceof Player;

        this.arrow = arrow;
        this.shooter = (Player) arrow.getShooter();
        this.shotFrom = new WorldVector(shooter.getLocation().clone());
    }
}
