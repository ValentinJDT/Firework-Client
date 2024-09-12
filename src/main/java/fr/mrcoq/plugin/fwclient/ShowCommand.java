package fr.mrcoq.plugin.fwclient;

import fr.mrcoq.plugin.fwclient.firework.ShowFile;
import fr.mrcoq.plugin.fwclient.firework.ig.FireworkInstance;
import fr.mrcoq.plugin.fwclient.firework.ig.ShowInstance;
import fr.mrcoq.plugin.fwclient.manager.ShowManager;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

public class ShowCommand implements CommandExecutor {

    private FWClient instance;
    private ShowManager showManager;

    public ShowCommand(FWClient instance) {
        this.instance = instance;
        this.showManager = instance.getShowManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1 && args[0].equalsIgnoreCase("list")) {
            for(ShowFile showFile : showManager.getShowFiles()) {
                sender.sendMessage(showFile.getDisplayName() + " - v" + showFile.getVersion() + "\n" + "By : " + String.join(", ", showFile.getAuthors()));
            }
        } else if(args.length == 2 && args[0].equalsIgnoreCase("start")) {
            String showId = args[1];

            ShowFile showFile = showManager.getShowFile(showId);

            if(showFile == null) {
                sender.sendMessage("Show doesn't exist");
                return true;
            }

            ShowInstance sinstance = showManager.createShowInstance(showFile);

            sinstance.start();
        }

        return false;
    }


}
