package me.refracdevelopment.simplegems.listeners;

import me.refracdevelopment.simplegems.SimpleGems;
import me.refracdevelopment.simplegems.api.SimpleGemsAPI;
import me.refracdevelopment.simplegems.utilities.SimpleGemsMultiplierUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

/**
 * Intercepts "gems give" commands from external plugins (AFKZone, hourly rewards, etc.)
 * and applies prestige multiplier automatically
 */
public class GemsCommandInterceptor implements Listener {
    
    private final SimpleGemsAPI gemsAPI = SimpleGems.getInstance().getGemsAPI();
    
    /**
     * Intercept player-executed commands (rare, but possible)
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        String command = e.getMessage().toLowerCase();
        
        // Only intercept "gems give" commands
        if (!command.startsWith("/gems give") && !command.startsWith("gems give")) {
            return;
        }
        
        processGemsGiveCommand(e.getMessage(), e.getPlayer(), true, e);
    }
    
    /**
     * Intercept console/plugin-executed commands (hourly rewards, AFKZone, etc.)
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent e) {
        String command = e.getCommand().toLowerCase();
        
        // Only intercept "gems give" commands
        if (!command.startsWith("gems give")) {
            return;
        }
        
        // Debug log
        SimpleGems.getInstance().getLogger().info("[GemsCommandInterceptor] Caught: " + e.getCommand());
        
        processGemsGiveCommand(e.getCommand(), null, false, e);
    }
    
    /**
     * Process and apply multiplier to gems give commands
     */
    private void processGemsGiveCommand(String command, Player executor, boolean isPlayerCommand, Object event) {
        // Parse: "gems give <player> <amount>"
        String[] args = command.trim().split("\\s+");
        
        // Need at least: gems give player amount
        if (args.length < 4) {
            SimpleGems.getInstance().getLogger().warning("[GemsCommandInterceptor] Invalid format: " + command + " (args: " + args.length + ")");
            return;
        }
        
        try {
            String playerName = args[2];
            double baseAmount = Double.parseDouble(args[3]);
            
            // Find target player
            Player target = Bukkit.getPlayerExact(playerName);
            if (target == null) {
                SimpleGems.getInstance().getLogger().info("[GemsCommandInterceptor] Player not online: " + playerName);
                return; // Player not online, let normal command handle it
            }
            
            // Get prestige multiplier from permission
            double multiplier = SimpleGemsMultiplierUtil.getPrestigeMultiplier(target);
            
            SimpleGems.getInstance().getLogger().info("[GemsCommandInterceptor] " + playerName + " multiplier: " + multiplier + "x");
            
            // If no multiplier (1.0x), let the normal command execute
            if (multiplier == 1.0) {
                SimpleGems.getInstance().getLogger().info("[GemsCommandInterceptor] No multiplier, skipping: " + playerName);
                return;
            }
            
            // Cancel the original command
            if (isPlayerCommand) {
                ((PlayerCommandPreprocessEvent) event).setCancelled(true);
            } else {
                ((ServerCommandEvent) event).setCancelled(true);
            }
            
            // Apply multiplier and give gems
            double finalAmount = baseAmount * multiplier;
            gemsAPI.giveGems(target, finalAmount);
            
            SimpleGems.getInstance().getLogger().info("[GemsCommandInterceptor] Gave " + finalAmount + " gems to " + playerName + " (" + baseAmount + " × " + multiplier + "x)");
            
            // Notify player with breakdown
            target.sendMessage("§e✦ +§a" + String.format("%.1f", finalAmount) + " gems " +
                             "§7(§e" + String.format("%.0f", baseAmount) + " §7× §b" + 
                             SimpleGemsMultiplierUtil.getFormattedMultiplier(target) + "§7)");
            
        } catch (NumberFormatException ex) {
            SimpleGems.getInstance().getLogger().warning("[GemsCommandInterceptor] Failed to parse: " + command + " (" + ex.getMessage() + ")");
        }
    }
}
