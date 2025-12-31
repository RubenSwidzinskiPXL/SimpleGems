# SimpleGems - Premium Gem Economy System

[![](https://jitpack.io/v/RefracDevelopment/SimpleGems.svg)](https://jitpack.io/#RefracDevelopment/SimpleGems) [![Java CI with Maven](https://github.com/RefracDevelopment/SimpleGems/actions/workflows/maven.yml/badge.svg)](https://github.com/RefracDevelopment/SimpleGems/actions/workflows/maven.yml)

A complete gem economy system for Spigot servers with prestige integration, playtime rewards, and universal multiplier system.

[🎮 Spigot](https://www.spigotmc.org/resources/96827/) | [📦 Hangar](https://hangar.papermc.io/RefracDevelopment/SimpleGems/) | [💬 Discord](https://discord.gg/EFeSKPg739)

---

## 🚀 Quick Start (5 Minutes)

### Installation

```bash
# Build the plugin
mvn clean package

# Copy to server
cp target/SimpleGems-*.jar /path/to/server/plugins/

# Restart server
```

### First Time Setup

```bash
# 1. Configure basic settings
# Edit: plugins/SimpleGems/config.yml
data-type: "SQLite"  # or MySQL

# 2. Check it loaded
/gems help

# 3. Test your balance
/gems balance
```

### Enable Prestige Multipliers

Ensure your LifeStealZ plugin is updated to grant multiplier permissions:

```bash
# When you prestige, you'll automatically get:
# Permission: lifestealz.prestige.multiplier.105 (for Prestige 1)

# Check it worked:
/lp user YourName permission info
```

---

## 📖 Player Features

### Commands

```bash
# Check your gem balance
/gems balance

# Transfer gems to another player
/gems pay <player> <amount>

# View top 10 gem holders
/gems top

# Access gem shop
/gems shop

# View help
/gems help
```

### Prestige System

When you prestige in LifeStealZ:
- ✅ Health resets
- ✅ Prestige count increases
- ✅ **Multiplier permission granted** (new!)
- ✅ Next rewards are multiplied automatically

```bash
# View your prestige
/lifestealz prestige info

# Prestige (when ready)
/lifestealz prestige confirm
```

---

## 💻 Developer Guide

### Getting Started

#### Maven Dependency

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.RefracDevelopment</groupId>
    <artifactId>SimpleGems</artifactId>
    <version>LATEST</version>
    <scope>provided</scope>
</dependency>
```

Update `LATEST` from [Releases](https://github.com/RefracDevelopment/SimpleGems/releases/latest).

Add to `plugin.yml`:
```yaml
depend:
  - SimpleGems

softdepend:
  - LuckPerms
  - PlaceholderAPI
```

### Basic API Usage

#### Give Gems

```java
import me.refracdevelopment.simplegems.SimpleGems;
import me.refracdevelopment.simplegems.api.SimpleGemsAPI;
import org.bukkit.entity.Player;

SimpleGemsAPI gemsAPI = SimpleGems.getInstance().getGemsAPI();

// Give player 100 gems
gemsAPI.giveGems(player, 100);

// Remove gems
gemsAPI.removeGems(player, 50);

// Get balance
double balance = gemsAPI.getBalance(player);
```

### Prestige Multiplier Integration

#### Get Multiplier

```java
public double getPrestigeMultiplier(Player player) {
    for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
        if (perm.getPermission().startsWith("lifestealz.prestige.multiplier.")) {
            String multiplierStr = perm.getPermission()
                .replace("lifestealz.prestige.multiplier.", "");
            return Double.parseDouble(multiplierStr) / 100.0;
        }
    }
    return 1.0;
}
```

#### Apply Multiplier to Reward

```java
@EventHandler
public void onPlayerJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    int dailyGems = 50;
    
    // Get multiplier and apply
    double multiplier = getPrestigeMultiplier(player);
    double finalGems = dailyGems * multiplier;
    
    // Give gems
    gemsAPI.giveGems(player, finalGems);
    
    // Notify player
    player.sendMessage("§e✦ +§a" + (int)finalGems + " gems " + 
                      "§7(§a" + dailyGems + " §7x §b" + 
                      String.format("%.2f", multiplier) + "§7)");
}
```

### Advanced Examples

#### Boss Drops

```java
@EventHandler
public void onBossDeath(BossDeathEvent e) {
    Player killer = e.getKiller();
    int baseGems = 100;
    
    double multiplier = getPrestigeMultiplier(killer);
    double finalGems = baseGems * multiplier;
    
    gemsAPI.giveGems(killer, (int)finalGems);
    killer.sendMessage("§e✦ Boss defeated! +§a" + (int)finalGems + " gems");
}
```

#### Crate Rewards

```java
@EventHandler
public void onCrateOpen(CrateOpenEvent e) {
    Player player = e.getPlayer();
    int baseGems = 75;
    
    double multiplier = getPrestigeMultiplier(player);
    double finalGems = baseGems * multiplier;
    
    gemsAPI.giveGems(player, (int)finalGems);
    player.sendMessage("§e✦ Crate! +§a" + (int)finalGems + " gems");
}
```

#### Fishing Integration

```java
@EventHandler
public void onFishCatch(PlayerFishEvent e) {
    if (e.getState() == State.CAUGHT_FISH) {
        Player player = e.getPlayer();
        int baseGems = 10;
        
        double multiplier = getPrestigeMultiplier(player);
        double finalGems = baseGems * multiplier;
        
        gemsAPI.giveGems(player, (int)finalGems);
    }
}
```

---

## 🎯 Permission System

### Prestige Multiplier Permissions

All prestige multipliers use this format:

```
lifestealz.prestige.multiplier.XXX
```

Where `XXX` = multiplier × 100

| Prestige | Multiplier | Permission | Base 50 Gems |
|----------|------------|------------|---|
| 0 | 1.00x | (none) | 50 gems |
| 1 | 1.05x | `lifestealz.prestige.multiplier.105` | 52 gems |
| 2 | 1.10x | `lifestealz.prestige.multiplier.110` | 55 gems |
| 5 | 1.25x | `lifestealz.prestige.multiplier.125` | 62 gems |
| 10 | 1.50x | `lifestealz.prestige.multiplier.150` | 75 gems |
| 20 | 2.00x | `lifestealz.prestige.multiplier.200` | 100 gems |

### Command Permissions

```bash
gems.balance        # View own/others balance
gems.pay            # Transfer gems
gems.shop           # Use gem shop
gems.top            # View leaderboard
gems.admin          # Administrative commands
gems.reload         # Reload plugin
```

---

## 🔧 Configuration

### config.yml

```yaml
# Database backend
data-type: "SQLite"  # Options: SQLite, MySQL, MariaDB

mysql:
  host: "localhost"
  port: 3306
  database: "simplegems"
  username: "root"
  password: "password"

# Prestige multiplier formula
prestige:
  multiplier-per-level: 0.05  # +5% per level

# Playtime rewards
playtime:
  per-hour: 50        # Gems per hour
  daily-cap: 200      # Max per day

# Shop
shop:
  enabled: true
```

### menus.yml

```yaml
hourly_reward:
  page: 0
  slot: 11
  material: CLOCK
  name: "&e&lHourly Reward"
  lore:
    - "&7Play for &f1 hour &7to earn"
    - "&e50 Gems &7x &b%simplegems_prestige_multiplier%"
  interval: 3600
  commands:
    - "gems give %player% 50"
  message: ""
```

---

## 📊 PlaceholderAPI Integration

### Available Placeholders

```
%simplegems_balance%              → Player's gem balance (formatted)
%simplegems_balance_decimal%      → Player's balance (with decimals)
%simplegems_prestige_level%       → Player's prestige level
%simplegems_prestige_multiplier%  → Player's multiplier (e.g., 1.05)
%simplegems_player_<rank>%        → Leaderboard position (1-10)
```

### Usage Examples

```
Scoreboard: Gems: %simplegems_balance%
Tab List: P%simplegems_prestige_level% x%simplegems_prestige_multiplier%
Chat: [%simplegems_prestige_level%⭐] %player_name%
```

---

## 🐛 Troubleshooting

### Multiplier Not Applying?

```bash
# 1. Check LuckPerms is running
/lp status

# 2. Check player has permission
/lp user <player> permission info | grep prestige

# 3. Verify LifeStealZ granted it
# (Prestige a player and check again)

# 4. Test manually
/lp user <player> permission set lifestealz.prestige.multiplier.105 true
```

### Plugin Won't Load?

Check these:
- ✅ Spigot 1.19+
- ✅ LuckPerms 5.3+ installed
- ✅ PlaceholderAPI 2.11.3+ (optional but recommended)
- ✅ Check console for errors
- ✅ Verify database connectivity

### Commands Not Working?

```bash
# Reload and try again
/gems reload

# Check permissions
/lp user <player> permission info

# Try admin bypass
/lp user <player> permission set gems.admin true
```

---

## 🎉 Features

### Core Features
✅ **Gem Economy** - Full balance system  
✅ **Playtime Rewards** - Automatic daily rewards  
✅ **Gem Shop** - Buy items with gems  
✅ **Leaderboards** - Top 10 players  
✅ **Player Transfers** - `/gems pay` between players  

### Advanced Features
✅ **Prestige Integration** - Permission-based multipliers  
✅ **LuckPerms Sync** - Automatic permission management  
✅ **PlaceholderAPI** - Universal placeholder support  
✅ **Multi-Database** - SQLite or MySQL  
✅ **Custom Menus** - Configurable rewards  
✅ **Flexible API** - Easy plugin integration  

---

## 📚 Admin Commands

### Gem Management

```bash
/gems give <player> <amount>        # Give gems
/gems take <player> <amount>        # Remove gems
/gems set <player> <amount>         # Set exact amount
/gems balance [player]              # Check balance
```

### Prestige Management (LifeStealZ)

```bash
/lifestealz prestige get <player>   # View prestige
/lifestealz prestige set <player> <level>  # Set prestige
/lifestealz prestige reset <player> # Reset prestige
```

Note: These automatically update the multiplier permission!

### Server

```bash
/gems reload                        # Reload plugin
/gems version                       # Check version
/gems top                           # View leaderboard
```

---

## 🔗 Links & Support

- **GitHub:** [RefracDevelopment/SimpleGems](https://github.com/RefracDevelopment/SimpleGems)
- **Discord:** [RefracDevelopment Server](https://discord.gg/EFeSKPg739)
- **Issues:** [Report bugs](https://github.com/RefracDevelopment/SimpleGems/issues)
- **Spigot:** [Spigot Resource](https://www.spigotmc.org/resources/96827/)
- **Hangar:** [Hangar Repository](https://hangar.papermc.io/RefracDevelopment/SimpleGems/)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

**Icon from:** [Flaticon](https://www.flaticon.com/)

---

## ✨ Quick Reference

### Most Common Setup Tasks

```bash
# 1. Install plugin
cp SimpleGems.jar plugins/
restart server

# 2. Configure database
# Edit: plugins/SimpleGems/config.yml

# 3. Prestige a player and check multiplier
/lifestealz prestige confirm
/lp user YourName permission info

# 4. Test reward
/gems give YourName 50
```

### For Developers

```java
// Add dependency in pom.xml
// Add to plugin.yml
depend:
  - SimpleGems

// Use in code
SimpleGemsAPI api = SimpleGems.getInstance().getGemsAPI();
api.giveGems(player, 100);
```

---

**Ready to go!** Need help? Check the [Discord](https://discord.gg/EFeSKPg739) or [GitHub Issues](https://github.com/RefracDevelopment/SimpleGems/issues).
