package me.refracdevelopment.simplegems.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import me.refracdevelopment.simplegems.SimpleGems;
import me.refracdevelopment.simplegems.api.SimpleGemsAPI;
import me.refracdevelopment.simplegems.player.data.ProfileData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PrestigeListener implements Listener {
    
    private final SimpleGemsAPI gemsAPI = SimpleGems.getInstance().getGemsAPI();
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCustomPrestige(PlayerCommandPreprocessEvent e) {
        // Detect LifeStealZ prestige command
        if (!e.getMessage().toLowerCase().contains("prestige")) {
            return;
        }
        
        Player player = e.getPlayer();
        ProfileData data = gemsAPI.getProfileData(player);
        
        if (data == null) return;
        
        // Delay to allow command to execute first
        SimpleGems.getInstance().getFoliaLib().getScheduler().runNextTick(task -> {
            // ✅ CORRECT: Use the actual LifeStealZ placeholder
            String prestigeStr = PlaceholderAPI.setPlaceholders(player, "%lifestealz_prestige_count%");
            
            if (prestigeStr == null || prestigeStr.isEmpty() || prestigeStr.equals("InvalidPlaceholder")) {
                return; // PlaceholderAPI not working or LifeStealZ not installed
            }
            
            int newPrestige = 0;
            try {
                newPrestige = Integer.parseInt(prestigeStr);
            } catch (NumberFormatException ex) {
                return;
            }
            
            if (newPrestige > data.getPrestigeLevel()) {
                int baseReward = SimpleGems.getInstance().getSettings().PRESTIGE_GEM_REWARD_BASE;
                double gemsReward = (double)(baseReward * newPrestige);
                double multiplierPerLevel = SimpleGems.getInstance().getSettings().PRESTIGE_MULTIPLIER_PER_LEVEL;
                double newMultiplier = 1.0 + (newPrestige * multiplierPerLevel);
                
                gemsAPI.giveGems(player, gemsReward);
                data.setPrestigeLevel(newPrestige);
                data.setGemMultiplier(newMultiplier);
                data.save();
                
                String message = "§6✦ §aPrestige " + newPrestige + "! +§e" + (int)gemsReward + " gems §a+ x" + 
                                String.format("%.2f", newMultiplier) + " multiplier!";
                player.sendMessage(message);
            }
        });
    }
}
