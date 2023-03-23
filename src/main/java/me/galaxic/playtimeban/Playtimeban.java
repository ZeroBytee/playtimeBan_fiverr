package me.galaxic.playtimeban;

import me.galaxic.playtimeban.commands.playtimeBanCmd;
import me.galaxic.playtimeban.events.onJoinEvent;
import me.galaxic.playtimeban.managers.*;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;
import java.util.UUID;

public final class Playtimeban extends JavaPlugin {

    private playtimeManager playtimeManager;
    private ConfigManager configManager;
    private timerManager timerManager;


    @Override
    public void onEnable() {
        // Plugin startup logic
        ConfigManager.setupConfig(this);
        loadCommands();
        playtimeManager = new playtimeManager(this);
        configManager = new ConfigManager(this);
        timerManager = new timerManager(this);

        // start all the playtime timers again
        for (Player player : Bukkit.getOnlinePlayers()) {
            timerManager.startPlayerTimer(player);
        }

        // start day cyles
        if (configManager.getStarted()) {
            // the cycles have started, running task every 10 seconds
            getServer().getScheduler().runTaskTimer(this, new Runnable() {
                @Override
                public void run() {
                    if (configManager.getStarted()) {
                        // the cycles have started
                        if (configManager.getNextDay() == 0) {
                            // next day value is not set
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.add(Calendar.HOUR_OF_DAY, 24);
                            long nextDayTime = calendar.getTimeInMillis();
                            configManager.setNextDay(nextDayTime);
                        } else {
                            // next day value is set, check if current time is past the value
                            long currentTime = System.currentTimeMillis();
                            long nextDayTime = configManager.getNextDay();
                            if (currentTime >= nextDayTime) {
                                // current time is past next day value, perform necessary actions
                                // change the day of the server inside the config by 1
                                int serverDay = configManager.getDay() + 1;
                                configManager.setDay(serverDay);

                                // get all players and add 3 hours of playtime (unless they reach their cap of 18 hours)
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    int playtimeRaw = playtimeManager.getPlaytime(player);
                                    int hours = playtimeRaw / 3600; // get the number of hours
                                    int minutes = (playtimeRaw % 3600) / 60; // get the number of minutes
                                    int seconds = playtimeRaw % 60; // get the number of seconds
                                    // check if the player has reached its playtime cap
                                    if (hours >= 15) {
                                        // player will reach his cap, set playtime at 18 (cap)
                                        int hoursNew = 18;
                                        int playtimeNew = hoursNew * 3600;
                                        playtimeManager.setPlaytime(player, playtimeNew);
                                    } else {
                                        // player won't reach his cap, add 3 hours (extra_hours)
                                        int extra_hours = 3;
                                        int hoursNew = hours + extra_hours;
                                        int playtimeNew = hoursNew * 3600 + minutes * 60 + seconds;
                                        playtimeManager.setPlaytime(player, playtimeNew);
                                    }
                                    // change everyone's day to the current server day
                                    playtimeManager.setPlayerDay(player, configManager.getDay());
                                }
                                // set next day value for the next cycle
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                calendar.add(Calendar.HOUR_OF_DAY, 24);
                                nextDayTime = calendar.getTimeInMillis();
                                configManager.setNextDay(nextDayTime);

                                // unban every player
                                for (String uuid : configManager.getBanned()) {
                                    Player player = Bukkit.getPlayer(uuid);
                                    configManager.removeBanned(UUID.fromString(uuid));
                                }

                                // let everyone know a day has passed
                                Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "A day has passed, every player has gained a extra 3 hours of playtime.");
                            }
                        }
                    }
                }
            }, 0, 200);
        }

    }

    public playtimeManager getPlaytimeManager() {
        return playtimeManager;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }
    public timerManager getTimerManager() {
        return timerManager;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void loadCommands(){
        Bukkit.getPluginManager().registerEvents(new onJoinEvent(this), this);
        getCommand("ptb").setExecutor(new playtimeBanCmd(this));
    }

}
