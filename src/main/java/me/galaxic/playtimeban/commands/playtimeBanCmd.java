package me.galaxic.playtimeban.commands;

import me.galaxic.playtimeban.Playtimeban;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;

public class playtimeBanCmd implements CommandExecutor {

    private Playtimeban playtimeBan;

    public playtimeBanCmd(Playtimeban playtimeBan) {
        this.playtimeBan = playtimeBan;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("ptb")) {
            Player player = (Player) sender;
           if (args.length == 1) {
               String arg0 = args[0];
               if (arg0.equalsIgnoreCase("toggle")) {
                    // toggle the timer above your hotbar
                   int toggle = playtimeBan.getPlaytimeManager().getTimerToggled(player);
                   // 0 = no timer, 1 = timer
                   if (toggle == 1) {
                       playtimeBan.getPlaytimeManager().setTimerToggled(player, 0);
                       player.sendMessage(ChatColor.GREEN + "Disabled the playtime timer.");
                   } else {
                       playtimeBan.getPlaytimeManager().setTimerToggled(player, 1);
                       player.sendMessage(ChatColor.GREEN + "Enabled the playtime timer.");
                   }

               } else if (arg0.equalsIgnoreCase("setup")) {
                   // change the "started" value inside the config to true
                   playtimeBan.getConfigManager().setStarted(true);

                   // set the nextday time
                   Calendar calendar = Calendar.getInstance();
                   calendar.setTimeInMillis(System.currentTimeMillis());
                   calendar.add(Calendar.HOUR_OF_DAY, 24);
                   long nextDayTime = calendar.getTimeInMillis();
                   playtimeBan.getConfigManager().setNextDay(nextDayTime);
                   player.sendMessage(ChatColor.GREEN + "Successfully completed the setup.");
                   playtimeBan.getPlaytimeManager().setPlaytime(player, 3 * 3600);
               }
           }
        }

        return false;
    }
}
