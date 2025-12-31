package me.refracdevelopment.simplegems.managers.configuration.cache;

import me.refracdevelopment.simplegems.SimpleGems;

import java.util.List;

public class Config {

    // Settings
    public double STARTING_GEMS;
    public long LEADERBOARD_UPDATE_INTERVAL;
    public String DATA_TYPE;
    public boolean CHECK_FOR_UPDATES;

    // Prestige
    public int PRESTIGE_GEM_REWARD_BASE;
    public double PRESTIGE_MULTIPLIER_PER_LEVEL;

    // Playtime
    public int PLAYTIME_PER_HOUR;
    public int PLAYTIME_DAILY_CAP;

    // Top
    public String GEMS_TOP_TITLE;
    public String GEMS_TOP_FORMAT;
    public int GEMS_TOP_ENTRIES;
    public boolean BROADCAST_MESSAGE;

    // Messages
    public List<String> GEMS_BALANCE;

    // Gems Item
    public String GEMS_ITEM_MATERIAL;
    public int GEMS_ITEM_DURABILITY, GEMS_ITEM_CUSTOM_MODEL_DATA;
    public boolean GEMS_ITEM_CUSTOM_DATA, GEMS_ITEM_ITEMS_ADDER, GEMS_ITEM_GLOW;
    public String GEMS_ITEM_NAME;
    public List<String> GEMS_ITEM_LORE;

    public Config() {
        loadConfig();
    }

    public void loadConfig() {
        // Settings
        STARTING_GEMS = SimpleGems.getInstance().getConfigFile().getDouble("starting-gems");
        LEADERBOARD_UPDATE_INTERVAL = SimpleGems.getInstance().getConfigFile().getLong("leaderboard-update-interval");
        DATA_TYPE = SimpleGems.getInstance().getConfigFile().getString("data-type");
        CHECK_FOR_UPDATES = SimpleGems.getInstance().getConfigFile().getBoolean("check-for-updates");

        // Prestige
        PRESTIGE_GEM_REWARD_BASE = SimpleGems.getInstance().getConfigFile().getInt("prestige.gem-reward-base");
        PRESTIGE_MULTIPLIER_PER_LEVEL = SimpleGems.getInstance().getConfigFile().getDouble("prestige.multiplier-per-level");

        // Playtime
        PLAYTIME_PER_HOUR = SimpleGems.getInstance().getConfigFile().getInt("rewards.playtime-per-hour");
        PLAYTIME_DAILY_CAP = SimpleGems.getInstance().getConfigFile().getInt("rewards.daily-cap");

        // Top
        GEMS_TOP_TITLE = SimpleGems.getInstance().getConfigFile().getString("gems-top.title");
        GEMS_TOP_FORMAT = SimpleGems.getInstance().getConfigFile().getString("gems-top.format");
        GEMS_TOP_ENTRIES = SimpleGems.getInstance().getConfigFile().getInt("gems-top.top-entries");
        BROADCAST_MESSAGE = SimpleGems.getInstance().getConfigFile().getBoolean("gems-top.broadcast-message");

        // Messages
        GEMS_BALANCE = SimpleGems.getInstance().getConfigFile().getStringList("gems-balance");

        // Gems Item
        GEMS_ITEM_MATERIAL = SimpleGems.getInstance().getConfigFile().getString("gems-item.material");
        GEMS_ITEM_DURABILITY = SimpleGems.getInstance().getConfigFile().getInt("gems-item.durability");
        GEMS_ITEM_CUSTOM_DATA = SimpleGems.getInstance().getConfigFile().getBoolean("gems-item.customData");
        GEMS_ITEM_CUSTOM_MODEL_DATA = SimpleGems.getInstance().getConfigFile().getInt("gems-item.customModelData");
        GEMS_ITEM_ITEMS_ADDER = SimpleGems.getInstance().getConfigFile().getBoolean("gems-item.itemsAdder");
        GEMS_ITEM_NAME = SimpleGems.getInstance().getConfigFile().getString("gems-item.name");
        GEMS_ITEM_GLOW = SimpleGems.getInstance().getConfigFile().getBoolean("gems-item.glow");
        GEMS_ITEM_LORE = SimpleGems.getInstance().getConfigFile().getStringList("gems-item.lore");
    }
}