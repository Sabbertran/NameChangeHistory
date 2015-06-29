package de.sabbertran.namechangehistory.commands;

import de.sabbertran.namechangehistory.NameChangeHistory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NameChangeHistoryCommand implements CommandExecutor {
    
    private NameChangeHistory main;
    
    public NameChangeHistoryCommand(NameChangeHistory main) {
        this.main = main;
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length > 0) {
            if (sender.hasPermission("namechangehistory.lookup")) {
                final Player p = main.getServer().getPlayerExact(args[0]);
                if (p != null) {
                    main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
                        @Override
                        public void run() {
                            String names = "";
                            DateFormat df = new SimpleDateFormat(main.getDateFormat());
                            for (Map.Entry<Date, String> entry : main.getFormerNames(p).entrySet()) {
                                Date date = entry.getKey();
                                String d = date.getTime() != 0L ? df.format(date) : "Original";
                                String name = entry.getValue();
                                names = names + main.getNameFormat().replace("%name%", name).replace("%date%", d) + ChatColor.RESET + ", \n";
                            }
                            if (names.length() > 1) {
                                names = names.substring(0, names.length() - 5);
                            }
                            String msg = main.getMessageFormat().replace("%name%", p.getName() + ChatColor.RESET) + "\n" + names;
                            sender.sendMessage(msg.split("\n"));
                        }
                    });
                    return true;
                } else {
                    sender.sendMessage("Player '" + args[0] + "' not found");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                return true;
            }
        } else {
            return false;
        }
    }
}
