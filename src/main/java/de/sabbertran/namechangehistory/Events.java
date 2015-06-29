package de.sabbertran.namechangehistory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {

    private NameChangeHistory main;

    public Events(NameChangeHistory main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
        final Player p = ev.getPlayer();
        if (main.isLoginInfoEnabled()) {
            main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
                @Override
                public void run() {
                    TreeMap<Date, String> map = main.getFormerNames(p);
                    if (map.size() > 1) {
                        String names = "";
                        DateFormat df = new SimpleDateFormat(main.getDateFormat());
                        int i = 0;
                        for (Map.Entry<Date, String> entry : map.entrySet()) {
                            Date date = entry.getKey();
                            String d = date.getTime() != 0L ? df.format(date) : "Original";
                            String name = entry.getValue();
                            names = names + main.getNameFormat().replace("%name%", name).replace("%date%", d) + ChatColor.RESET + ", " + (i == 1 ? "\n" : "");
                            if (i >= 1) {
                                i = 0;
                            } else {
                                i++;
                            }
                        }
                        if (names.length() > 1) {
                            if (names.endsWith("\n")) {
                                names = names.substring(0, names.length() - 5);
                            } else {
                                names = names.substring(0, names.length() - 2);
                            }
                        }
                        String msg = main.getMessageFormat().replace("%name%", p.getName() + ChatColor.RESET) + "\n" + names;
                        for (Player pl : main.getServer().getOnlinePlayers()) {
                            if (pl.hasPermission("namechangehistory.logininfo")) {
                                pl.sendMessage(msg.split("\n"));
                            }
                        }
                    }
                }
            });
        }
    }
}
