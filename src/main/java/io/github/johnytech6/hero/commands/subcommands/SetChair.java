package io.github.johnytech6.hero.commands.subcommands;

import io.github.johnytech6.DndPlayer;
import io.github.johnytech6.Handler.PluginHandler;
import io.github.johnytech6.dm.commands.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SetChair extends SubCommand {

    PluginHandler ph;

    public SetChair(PluginHandler pluginHandler) {
        ph = pluginHandler;
    }

    @Override
    public String getName() {
        return "set_chair";
    }

    @Override
    public String getDescription() {
        return "Set your chair in the dnd room";
    }

    @Override
    public String getSyntax() {
        return "/hero set_chair ";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            UUID playerID = ((Player) sender).getUniqueId();

            DndPlayer p = ph.getDndPlayer(playerID);

            if (ph.isPlayerDndPlayer(p.getUniqueId())) {
                DndPlayer dndP = ph.getDndPlayer(p.getUniqueId());
                Location targetLocation;
                if (args.length == 4 && isDouble(args[1]) && isDouble(args[2]) && isDouble(args[3])) {
                    targetLocation = new Location(p.getPlayer().getWorld(), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
                } else {
                    targetLocation = p.getLocation();
                }

                dndP.setChairPosition(targetLocation);

                ph.savePlayerChairPosition(dndP, dndP.getLocation());


            } else {
                p.sendMessage("You are not a DndPlayer ask Jean for help.");
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("[<x> <y> <z>]");
            return arguments;
        } else if (args.length == 2 && isDouble(args[1])) {
            List<String> arguments = new ArrayList<>();
            arguments.add("<y> <z>");
            return arguments;
        } else if (args.length == 3 && isDouble(args[1]) && isDouble(args[2])) {
            List<String> arguments = new ArrayList<>();
            arguments.add("<z>");
            return arguments;
        }
        return null;
    }


}

