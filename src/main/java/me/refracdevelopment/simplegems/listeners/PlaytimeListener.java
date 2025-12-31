package me.refracdevelopment.simplegems.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import me.refracdevelopment.simplegems.SimpleGems;
import me.refracdevelopment.simplegems.api.SimpleGemsAPI;
import me.refracdevelopment.simplegems.player.data.ProfileData;
import me.refracdevelopment.simplegems.utilities.SimpleGemsMultiplierUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlaytimeListener implements Listener {
    
    private final SimpleGemsAPI gemsAPI = SimpleGems.getInstance().getGemsAPI();
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        ProfileData data = gemsAPI.getProfileData(player);
        
        if (data == null) return;
        
        // Read playtime from PlaceholderAPI
        String playtimeStr = PlaceholderAPI.setPlaceholders(player, "%yourplaytime_daily%");
        
        if (playtimeStr == null || playtimeStr.isEmpty() || playtimeStr.equals("InvalidPlaceholder")) {
            return; // Playtime plugin not installed
        }
        
        // ✅ Get multiplier from LuckPerms permission (not hardcoded calculation)
        double gemMultiplier = SimpleGemsMultiplierUtil.getPrestigeMultiplier(player);
        
        try {
            long dailyMinutes = parsePlaytime(playtimeStr);
            
            if (dailyMinutes <= 0) return;
            
            int playtimeRewardPerHour = SimpleGems.getInstance().getSettings().PLAYTIME_PER_HOUR;
            int dailyCap = SimpleGems.getInstance().getSettings().PLAYTIME_DAILY_CAP;
            
            double playGems = Math.min(((dailyMinutes / 60.0) * playtimeRewardPerHour * gemMultiplier), dailyCap);
            
            if (playGems > 0) {
                gemsAPI.giveGems(player, playGems);
                String message = "§e✦ +" + (int)playGems + " gems for playtime (x" + 
                                String.format("%.2f", gemMultiplier) + ")";
                player.sendMessage(message);
            }
        } catch (Exception ex) {
            // Silently fail - playtime placeholder may not be available
        }
    }
    
    private long parsePlaytime(String playtimeStr) {
        try {
            // Handle "HH:MM" format
            if (playtimeStr.contains(":")) {
                String[] parts = playtimeStr.split(":");
                long hours = Long.parseLong(parts[0]);
                long minutes = Long.parseLong(parts[1]);
                return hours * 60 + minutes;
            }
            // Handle plain number (minutes)
            return Long.parseLong(playtimeStr);
        } catch (Exception e) {
            return 0;
        }
    }
}
