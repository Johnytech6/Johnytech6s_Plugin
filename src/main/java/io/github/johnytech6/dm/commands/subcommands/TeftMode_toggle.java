package io.github.johnytech6.dm.commands.subcommands;

import io.github.johnytech6.DndPlayer;
import io.github.johnytech6.Handler.DMHandler;
import io.github.johnytech6.Handler.PluginHandler;
import io.github.johnytech6.dm.Dm;
import io.github.johnytech6.dm.commands.SubCommand;
import io.github.johnytech6.Handler.TeftHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeftMode_toggle extends SubCommand {

    private static DMHandler dmh = DMHandler.getInstance();
    private static TeftHandler th = TeftHandler.getInstance();

    @Override
    public String getName() {
        return "teft_mode_toggle";
    }

    @Override
    public String getDescription() {
        return "Toggle your teft mode as a dm or the teft mode of another dm.";
    }

    @Override
    public String getSyntax() {
        return "/dm teft_mode_toggle [player]";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            UUID playerID = ((Player) sender).getUniqueId();

            DndPlayer p = PluginHandler.getInstance().getDndPlayer(playerID);

            if (dmh.isPlayerDm(playerID) /* && p.hasPermission("dm.mode.teft")*/) {
                Dm targetDm;
                if (args.length == 2) {
                    targetDm = dmh.getDm(UUID.fromString(args[1]));
                    th.ToggleTeftMode(targetDm.getPlayer(), true);
                    p.sendMessage("Teft power state of " + args[1] + " : " + targetDm.hasTeftPower());
                } else {
                    targetDm = dmh.getDm(playerID);
                    th.ToggleTeftMode(p.getPlayer(), true);
                }
                targetDm.setTeftPower(!targetDm.hasTeftPower());
            } else {
                p.sendMessage("You need to be DM to toggle teft power.");
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 2) {
            List<String> playerNames = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
            Bukkit.getServer().getOnlinePlayers().toArray(players);
            for (int i = 0; i < players.length; i++) {
                playerNames.add(players[i].getName());
            }

            return playerNames;
        }
        return null;
    }


}

