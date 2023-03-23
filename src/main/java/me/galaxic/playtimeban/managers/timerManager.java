package me.galaxic.playtimeban.managers;

import me.galaxic.playtimeban.Playtimeban;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class timerManager {

    private Playtimeban playtimeBan;
    public timerManager(Playtimeban playtimeBan) {
        this.playtimeBan = playtimeBan;
    }

    private Map<Player, BukkitTask> playerTasks = new HashMap<>();

    public void startPlayerTimer(Player player) {
        // start the player timer
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                // change the playtime
                int playtimeRaw = playtimeBan.getPlaytimeManager().getPlaytime(player);
                int hours = playtimeRaw / 3600; // get the number of hours
                int minutes = (playtimeRaw % 3600) / 60; // get the number of minutes
                int seconds = playtimeRaw % 60; // get the number of seconds

                if (seconds > 0) {
                    seconds--;
                    int playtimeUpd = hours * 3600 + minutes * 60 + seconds;
                    playtimeBan.getPlaytimeManager().setPlaytime(player,playtimeUpd);
                } else if (minutes > 0) {
                    minutes--;
                    seconds = 59;
                    int playtimeUpd = hours * 3600 + minutes * 60 + seconds;
                    playtimeBan.getPlaytimeManager().setPlaytime(player,playtimeUpd);
                } else if (hours == 0) {
                    // playtime is over, ban player
                    playtimeBan.getConfigManager().addBanned(player.getUniqueId());
                } else {
                    hours--;
                    minutes = 59;
                    seconds = 59;
                    int playtimeUpd = hours * 3600 + minutes * 60 + seconds;
                    playtimeBan.getPlaytimeManager().setPlaytime(player,playtimeUpd);

                }

                // create the formatted string
                String playtime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                // only show the timer if the player wants to see it.
                if (playtimeBan.getPlaytimeManager().getTimerToggled(player) == 1) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§2Playtime remaining: " + playtime));
                }

            }
        }.runTaskTimerAsynchronously(playtimeBan, 0, 20);
        playerTasks.put(player, task);
    }

    public void stopPlayerTimer(Player player) {
        // stops the playtime loop
        BukkitTask task = playerTasks.get(player);
        if (task != null) {
            task.cancel();
            playerTasks.remove(player);
        }
    }

}
