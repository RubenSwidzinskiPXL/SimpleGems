package me.refracdevelopment.simplegems.utilities;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

/**
 * Utility for getting prestige multiplier from LuckPerms permissions
 * Works with the new LifeStealZ prestige permission system
 * 
 * Permission format: lifestealz.prestige.multiplier.XXX
 * Where XXX = multiplier * 100
 * Examples: 105 = 1.05x, 150 = 1.50x, 200 = 2.00x
 */
public class SimpleGemsMultiplierUtil {
    
    /**
     * Get prestige multiplier from player's permissions
     * 
     * @param player The player to check
     * @return Multiplier value (e.g., 1.05, 1.10, 2.00)
     *         Returns 1.0 if no multiplier permission found
     */
    public static double getPrestigeMultiplier(Player player) {
        if (player == null) {
            return 1.0;
        }
        
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            if (perm.getPermission().startsWith("lifestealz.prestige.multiplier.")) {
                try {
                    String multiplierStr = perm.getPermission()
                        .replace("lifestealz.prestige.multiplier.", "");
                    // Convert "105" → 1.05, "150" → 1.50, "200" → 2.00
                    return Double.parseDouble(multiplierStr) / 100.0;
                } catch (NumberFormatException ignored) {
                    // Permission format was invalid, continue searching
                }
            }
        }
        return 1.0; // Default: no multiplier found
    }
    
    /**
     * Apply multiplier to a base gem amount
     * 
     * @param player Player with potential multiplier
     * @param baseGems Base amount before multiplier
     * @return Final gem amount after multiplier
     */
    public static double applyMultiplier(Player player, double baseGems) {
        double multiplier = getPrestigeMultiplier(player);
        return baseGems * multiplier;
    }
    
    /**
     * Get formatted multiplier string for display
     * 
     * @param player Player to check
     * @return Formatted string (e.g., "1.05x", "2.00x")
     */
    public static String getFormattedMultiplier(Player player) {
        double multiplier = getPrestigeMultiplier(player);
        return String.format("%.2f", multiplier) + "x";
    }
}
