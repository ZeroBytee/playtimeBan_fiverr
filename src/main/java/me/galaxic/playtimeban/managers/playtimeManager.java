package me.galaxic.playtimeban.managers;

import me.galaxic.playtimeban.Playtimeban;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class playtimeManager {


    private Playtimeban playtimeBan;
    public NamespacedKey playtimeKey;
    public NamespacedKey playerDayKey;
    public NamespacedKey timerToggleKey;

    public playtimeManager(Playtimeban playtimeBan) {
        this.playtimeBan = playtimeBan;
        this.playtimeKey = new NamespacedKey(playtimeBan, "ptb_playtimeLeft");
        this.playerDayKey = new NamespacedKey(playtimeBan, "ptb_day");
        this.timerToggleKey = new NamespacedKey(playtimeBan, "ptb_toggled");
    }

    // this will set the playtime variable inside the player's NBT to the playtime that is given
    public void setPlaytime(Player player, int playtime) {
        UUID uuid = player.getUniqueId();
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(playtimeKey, PersistentDataType.INTEGER, playtime);
    }

    // this will set the day variable inside the player's NBT to the day that is given
    public void setPlayerDay(Player player, int day) {
        UUID uuid = player.getUniqueId();
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(playerDayKey, PersistentDataType.INTEGER, day);
    }

    public void setTimerToggled(Player player, int toggle) {
        UUID uuid = player.getUniqueId();
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(timerToggleKey, PersistentDataType.INTEGER, toggle);
    }

    // returns the playtime of the player in seconds
    public int getPlaytime(Player player) {
        return Objects.requireNonNull(player.getPersistentDataContainer().get(playtimeKey, PersistentDataType.INTEGER));
    }

    // returns the last day the player has played
    public int getPlayerDay(Player player) {
        int day = playtimeBan.getConfigManager().getDay();
        if (player.getPersistentDataContainer().get(playerDayKey, PersistentDataType.INTEGER) != null) {
            day = Objects.requireNonNull(player.getPersistentDataContainer().get(playerDayKey, PersistentDataType.INTEGER));
        }
        return day;
    }

    // return if the player wants to see the playtime timer or not
    public int getTimerToggled(Player player) {
        int toggled = 1;
        if (player.getPersistentDataContainer().get(timerToggleKey, PersistentDataType.INTEGER) != null) {
            toggled = Objects.requireNonNull(player.getPersistentDataContainer().get(timerToggleKey, PersistentDataType.INTEGER));
        }
        return toggled;
    }

}
