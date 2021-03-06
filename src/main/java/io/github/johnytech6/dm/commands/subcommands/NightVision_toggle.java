package io.github.johnytech6.dm.commands.subcommands;

import io.github.johnytech6.DndPlayer;
import io.github.johnytech6.Handler.DMHandler;
import io.github.johnytech6.Handler.PluginHandler;
import io.github.johnytech6.dm.Dm;
import io.github.johnytech6.dm.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NightVision_toggle extends SubCommand {

    private PluginHandler pluginHandler;
    private DMHandler dmh;

    public NightVision_toggle(PluginHandler pluginHandler){
        this.pluginHandler = pluginHandler;
        dmh = pluginHandler.getDmHandler();
    }

    @Override
    public String getName() {
        return "night_vision_toggle";
    }

    @Override
    public String getDescription() {
        return "Toggle your night vision as a dm or the night vision of another dm.";
    }

    @Override
    public String getSyntax() {
        return "/dm night_vision_toggle [player]";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            UUID playerID = ((Player) sender).getUniqueId();

            DndPlayer p = pluginHandler.getDndPlayer(playerID);

            if (dmh.isPlayerDm(playerID) /*&& p.hasPermission("dm.mode.vision")*/) {
                Dm targetDm;
                if (args.length == 2) {
                    targetDm = dmh.getDm(UUID.fromString(args[1]));
                    p.sendMessage("Night vision state of " + args[1] + " : " + targetDm.hasNightVision());
                } else {
                    targetDm = dmh.getDm(playerID);
                }
                targetDm.setNightVision(!targetDm.hasNightVision());
                pluginHandler.saveNightVision(targetDm, targetDm.hasNightVision());
            } else {
                p.sendMessage("You need to be DM to toggle night vision.");
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