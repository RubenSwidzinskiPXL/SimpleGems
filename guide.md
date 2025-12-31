Updated Integration Approach

Skip building prestige system—instead listen to your custom plugin's events or use PlaceholderAPI to detect prestige-ups and auto-grant gems + multipliers.
Revised Step-by-Step (30 mins total)
Step 1: Project Setup + Dependencies (5 mins)

Same as before, but add these to pom.xml:

xml
<!-- PlaceholderAPI for prestige detection -->
<dependency>
    <groupId>me.clip</groupId>
    <artifactId>placeholderapi</artifactId>
    <version>2.11.5</version>
    <scope>provided</scope>
</dependency>
<!-- CrazyCrates API -->
<dependency>
    <groupId>com.github.stevenc32</groupId>
    <artifactId>CrazyCrates</artifactId>
    <version>1.2</version>
    <scope>provided</scope>
</dependency>

Step 2: Prestige Detection & Rewards (10 mins)

src/main/java/dev/refrac/simplegems/listeners/PrestigeListener.java - New file:

java
public class PrestigeListener implements Listener {
    private SimpleGemsAPI gemsAPI;
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCustomPrestige(PlayerCommandPreprocessEvent e) {
        // Detect your prestige command (adjust name)
        if (e.getMessage().contains("/lif steal prestige") || e.getMessage().contains("/prestige")) {
            Player player = e.getPlayer();
            PlayerData data = gemsAPI.getPlayerData(player);
            
            // Read current prestige from PlaceholderAPI
            String prestigeStr = PlaceholderAPI.setPlaceholders(player, "%lif steal_prestige_count%");
            int newPrestige = Integer.parseInt(prestigeStr.equals("None") ? "0" : prestigeStr);
            
            if (newPrestige > data.getPrestigeLevel()) {
                int gemsReward = 50 * newPrestige; // 50, 100, 150...
                gemsAPI.addGems(player, gemsReward);
                data.setPrestigeLevel(newPrestige);
                data.setGemMultiplier(1.0 + (newPrestige * 0.05)); // +5% per prestige
                data.save();
                
                player.sendMessage("§6✦ §aPrestige " + newPrestige + "! +§e" + gemsReward + " gems §a+ x" + 
                                 String.format("%.2f", data.getGemMultiplier()) + " multiplier!");
            }
        }
    }
}

Main.java - Register listener + PAPI:

java
@Override
public void onEnable() {
    // Existing...
    if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
        new SimpleGemsExpansion().register(); // Create PAPI expansion below
    }
    getServer().getPluginManager().registerEvents(new PrestigeListener(), this);
}

Step 3: CrazyCrates Integration (5 mins)

PrestigeListener.java - Add crate hooks:

java
@EventHandler
public void onCrateOpen(CrateOpenEvent e) { // CrazyCrates event
    if (Math.random() < 0.25) { // 25% chance
        PlayerData data = gemsAPI.getPlayerData(e.getPlayer());
        int gems = 3 + (int)(data.getPrestigeLevel() * 0.5); // Scales with prestige
        gemsAPI.addGems(e.getPlayer(), gems);
        e.getPlayer().sendMessage("§e✦ Found §6" + gems + " gems §ein crate!");
    }
}

// Replace hourly keys with gems in CrazyCrates config
// In your crate prize: give gems instead of key

Step 4: Playtime Rewards (5 mins)

src/main/java/dev/refrac/simplegems/listeners/PlaytimeListener.java:

java
@EventHandler
public void onPlayerJoin(PlayerJoinEvent e) {
    PlayerData data = gemsAPI.getPlayerData(e.getPlayer());
    String playtimeStr = PlaceholderAPI.setPlaceholders(e.getPlayer(), "%yourplaytime_daily%"); // Adjust placeholder
    long dailyMinutes = Long.parseLong(playtimeStr.split(":")[0]) * 60 + // Parse your format
                       Long.parseLong(playtimeStr.split(":")[1]);
    
    int playGems = Math.min((int)(dailyMinutes / 60.0 * 5 * data.getGemMultiplier()), 50);
    gemsAPI.addGems(e.getPlayer(), playGems);
    e.getPlayer().sendMessage("§e✦ +" + playGems + " gems for playtime (x" + 
                            String.format("%.2f", data.getGemMultiplier()) + ")");
}

Step 5: PlaceholderAPI Expansion (5 mins)

src/main/java/dev/refrac/simplegems/SimpleGemsExpansion.java:

java
public class SimpleGemsExpansion extends PlaceholderExpansion {
    @Override
    public String getIdentifier() { return "simplegems"; }
    @Override
    public String getAuthor() { return "yourname"; }
    @Override
    public String getVersion() { return "1.0"; }
    
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";
        PlayerData data = GemsAPI.getPlayerData(player);
        
        return switch (params) {
            case "balance" -> String.valueOf(data.getGems());
            case "prestige_multiplier" -> String.format("%.2f", data.getGemMultiplier());
            case "prestige_level" -> String.valueOf(data.getPrestigeLevel());
            default -> null;
        };
    }
}

Now use %simplegems_balance%, %simplegems_prestige_multiplier% in other plugins!
Step 6: Gems Shop (5 mins) - Simplified

GemsCommand.java - Add shop subcommand:

java
if (args[0].equalsIgnoreCase("shop")) {
    // Quick buy commands instead of full GUI
    if (args[1].equalsIgnoreCase("key")) {
        if (gemsAPI.removeGems(player, 25)) {
            // CrazyCrates API: CratesAPI.getInstance().giveCrate(player, "yourcrate", 1);
            player.sendMessage("§aPurchased crate key for 25 gems!");
        }
    }
}

Step 7: Config Integration

text
# config.yml additions
prestige:
  gem-reward-base: 50
  multiplier-per-level: 0.05
rewards:
  crate-drop-chance: 0.25
  playtime-per-hour: 5
  daily-cap: 50

Testing Commands

text
/gems balance  # Shows gems + multiplier
/pla reload    # Test prestige detection
/crate open    # Test crate gem drops

File Changes Summary

text
├── listeners/PrestigeListener.java     # Prestige + Crates
├── listeners/PlaytimeListener.java     # Playtime rewards
├── SimpleGemsExpansion.java           # PAPI outputs
├── Main.java                          # Register listeners + PAPI
└── config.yml                         # Tunable rates

Deploy: mvn clean package → /plugins/SimpleGems.jar. Works with your existing prestige (detects via command/PAPI), CrazyCrates events, Vault economy, and playtime plugin placeholders.​