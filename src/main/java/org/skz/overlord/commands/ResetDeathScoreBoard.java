package org.skz.overlord.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ResetDeathScoreBoard implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            Scoreboard scoreboard = player.getScoreboard();
            Objective objective = scoreboard.getObjective("Deaths");

            if (objective != null) {
                Score score = objective.getScore(player.getName());
                score.setScore(0);

                player.sendMessage(ChatColor.DARK_RED + "Your death scoreboard has been reset.");
            } else {
                player.sendMessage(ChatColor.DARK_RED + "No death scoreboard found.");
            }

            return true;
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "This command can only be used by a player.");
            return false;
        }
    }

}
