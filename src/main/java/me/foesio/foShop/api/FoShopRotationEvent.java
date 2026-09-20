package me.foesio.foShop.api;

import java.util.*;
import org.bukkit.Material;
import org.bukkit.event.*;

/** Server-thread selection hook. No I/O; only currently eligible offers may be chosen.
 * Cancellation preserves the current rotation and retries later. Null selection retains
 * FoShop's normal random selection. Addons own all category/quota policy.
 */
public final class FoShopRotationEvent extends Event implements Cancellable {
    public record RotationCandidate(String sectionId, String itemId, Material material) {}
    private static final HandlerList HANDLERS = new HandlerList();
    private final List<RotationCandidate> candidates;
    private final int slots;
    private List<RotationCandidate> selection;
    private boolean cancelled;
    public FoShopRotationEvent(List<RotationCandidate> candidates, int slots) {
        this.candidates = List.copyOf(candidates);
        if (slots < 1) throw new IllegalArgumentException("slots must be positive");
        this.slots = slots;
    }
    public List<RotationCandidate> candidates() { return candidates; }
    public int slots() { return slots; }
    public List<RotationCandidate> selection() { return selection; }
    public void select(List<RotationCandidate> selected) {
        List<RotationCandidate> copy = List.copyOf(selected);
        if (copy.size() > slots || new HashSet<>(copy).size() != copy.size() || !candidates.containsAll(copy))
            throw new IllegalArgumentException("Selection must contain distinct eligible offers within slot count");
        selection = copy;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean value) { cancelled = value; }
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
