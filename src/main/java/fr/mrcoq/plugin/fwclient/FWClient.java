package fr.mrcoq.plugin.fwclient;

import fr.mrcoq.plugin.fwclient.manager.ShowManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class FWClient extends JavaPlugin {

    private PluginManager pm;
    private ShowManager showManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        pm = getServer().getPluginManager();
        showManager  = new ShowManager(this);

        try {
            showManager.reloadShows();
            showManager.generateSoundsFile();
            showManager.extractAllSounds();
        } catch(IOException e) {
            e.printStackTrace();
        }

        getCommand("show").setExecutor(new ShowCommand(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public PluginManager getPm() {
        return pm;
    }

    public ShowManager getShowManager() {
        return showManager;
    }

}
