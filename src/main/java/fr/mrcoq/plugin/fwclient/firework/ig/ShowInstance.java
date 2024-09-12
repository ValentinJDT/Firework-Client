package fr.mrcoq.plugin.fwclient.firework.ig;

import java.util.ArrayList;
import java.util.List;

import fr.mrcoq.plugin.fwclient.FWClient;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ShowInstance {

    private FWClient instance;
    private String id;
    private List<FireworkInstance> fireworks = new ArrayList<>();

    private BukkitTask task;

    public ShowInstance(FWClient instance, String id) {
        this.instance = instance;
        this.id = id;
    }

    public void addFireworkInstance(FireworkInstance firework) {
        fireworks.add(firework);
    }

    /**
     * 
     */
    public void start() {
        BukkitRunnable runnable = new BukkitRunnable() {
            long start = 0;
            private int shooted = 0;

            @Override
            public void run() {
                List<FireworkInstance> goingToBeShooted = fireworks.stream().filter((fw) -> fw.getTiming() == start).toList();

                shooted += goingToBeShooted.size();

                goingToBeShooted.forEach(FireworkInstance::spawn);

                if(shooted >= fireworks.size()) {
                    Bukkit.broadcastMessage("Show ended !");
                    cancel();
                }

                start = start + 25;
            }
        };

        Bukkit.broadcastMessage("Starting show...");
        task = runnable.runTaskTimer(instance, 0, 5);
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.playSound(onlinePlayer.getLocation(), "show." + this.id, SoundCategory.MASTER, 1.0F, 1.0F);
        }
    }

    public void stop() {
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.stopSound("show." + this.id);
        }
        task.cancel();
    }

}
