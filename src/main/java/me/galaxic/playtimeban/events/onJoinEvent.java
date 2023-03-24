package me.galaxic.playtimeban.events;

import me.galaxic.playtimeban.Playtimeban;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class onJoinEvent implements Listener {

    private Playtimeban playtimeBan;
    public onJoinEvent(Playtimeban playtimeBan) {
        this.playtimeBan = playtimeBan;
    }

    private Map<Player, BukkitTask> playerTasks = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();



        if (!player.hasPlayedBefore()) {
            playtimeBan.getPlaytimeManager().setPlaytime(player, 3 * 3600);
        }

        if (playtimeBan.getPlaytimeManager().getPlaytime(player) == 0) {
            playtimeBan.getConfigManager().addBanned(player.getUniqueId());
        }

        // add more playtime if the user hasn't been online for a few day's
        int serverDay = playtimeBan.getConfigManager().getDay();
        int playerDay = playtimeBan.getPlaytimeManager().getPlayerDay(player);
        if (serverDay != playerDay) {
            int difference = serverDay - playerDay;
            int hoursToAdd = difference * 3;
            int currentPlaytime = playtimeBan.getPlaytimeManager().getPlaytime(player);
            int newPlaytime = currentPlaytime + (hoursToAdd * 3600);
            if (newPlaytime / 3600 < 18) {
                playtimeBan.getPlaytimeManager().setPlaytime(player, newPlaytime);
            } else {
                playtimeBan.getPlaytimeManager().setPlaytime(player, 18 * 3600);
            }

        }
        // start the playtime timer
        playtimeBan.getTimerManager().startPlayerTimer(player);

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        playtimeBan.getTimerManager().stopPlayerTimer(player);
    }
}
