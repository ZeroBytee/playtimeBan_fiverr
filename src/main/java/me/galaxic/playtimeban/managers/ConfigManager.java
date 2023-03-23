package me.galaxic.playtimeban.managers;


import com.sun.org.apache.xerces.internal.xs.StringList;
import me.galaxic.playtimeban.Playtimeban;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ConfigManager {

    private static FileConfiguration config;
    private Playtimeban playtimeban;

    public static void setupConfig(Playtimeban playtimeBan) {
        ConfigManager.config = playtimeBan.getConfig();
        playtimeBan.saveDefaultConfig();
    }
    public ConfigManager(Playtimeban playtimeban) {
        this.playtimeban = playtimeban;
    }

    // gets the server day from config
    public int getDay() {
        return config.getInt("day");
    }

    // gets a list of all the banned players (banned because their playtime ran out)
    public List<String> getBanned() {
        List<String> banned_today =  config.getStringList("banned_today");
        return banned_today;
    }

    // gets the next day from config
    public long getNextDay() {
        return config.getLong("next_day");
    }

    // gets the value of the started variable in the config
    public boolean getStarted() {
        return (boolean) Objects.requireNonNull(config.get("started"));
    }

    // set the server date in the config
    public void setDay(int Day) {
        config.set("day", Day);
        playtimeban.saveConfig();
    }

    // add a user to the banlist (banned because their playtime ran out)
    public void addBanned(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        List<String> banned_today = config.getStringList("banned_today");
        // makes sure the player isn't already on the banlist (in config)
        assert player != null;
        if (!banned_today.contains(String.valueOf(player.getUniqueId()))) {
            banned_today.add(String.valueOf(uuid));
            config.set("banned_today", banned_today);
            playtimeban.saveConfig();
            // You can't run a kick/ban command in async, so created a non async task to ban and kick the player
            Bukkit.getScheduler().runTask(playtimeban, new Runnable() {
                @Override
                public void run() {
                    Bukkit.getBanList(BanList.Type.NAME).addBan(player.getDisplayName(), ChatColor.RED + "You ran out of playtime for today.", null, null);
                    player.kickPlayer(ChatColor.RED + "You ran out of playtime for today.");
                }
            });
        }
    }

    // remove the user from the banlist
    public void removeBanned(UUID uuid) {
        // removes the player from the banned variable in the config
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        List<String> banned_today = config.getStringList("banned_today");
        banned_today.remove(String.valueOf(uuid));
        config.set("banned_today", banned_today);
        playtimeban.saveConfig();
        Bukkit.getBanList(BanList.Type.NAME).pardon(Objects.requireNonNull(player.getName()));
    }

    // set the next_day variable in the config
    public void setNextDay(long day) {
        config.set("next_day", day);
        playtimeban.saveConfig();
    }

    // set the started variable inside the config
    public void setStarted(boolean value) {
        config.set("started", value);
        playtimeban.saveConfig();
    }
}
