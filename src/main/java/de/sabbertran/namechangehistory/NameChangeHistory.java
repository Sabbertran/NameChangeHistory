package de.sabbertran.namechangehistory;

import de.sabbertran.namechangehistory.commands.NameChangeHistoryCommand;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NameChangeHistory extends JavaPlugin {

    private Logger log = Bukkit.getLogger();
    private boolean loginInfoEnabled;
    private String messageFormat;
    private String nameFormat;
    private String dateFormat;

    @Override
    public void onEnable() {
        getConfig().addDefault("NameChangeHistory.LoginInfo.enabled", true);
        getConfig().addDefault("NameChangeHistory.Message.Format", "Former names for player &r&7&l%name%:");
        getConfig().addDefault("NameChangeHistory.Name.Format", "%date%: &7%name%");
        getConfig().addDefault("NameChangeHistory.Message.DateFormat", "dd.MM.yyyy HH:mm");
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        loginInfoEnabled = getConfig().getBoolean("NameChangeHistory.LoginInfo.enabled");
        messageFormat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("NameChangeHistory.Message.Format"));
        nameFormat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("NameChangeHistory.Name.Format"));
        dateFormat = getConfig().getString("NameChangeHistory.Message.DateFormat");
        
        getServer().getPluginManager().registerEvents(new Events(this), this);
        getCommand("namechangehistory").setExecutor(new NameChangeHistoryCommand(this));

        log.info("NameChangeHistory enabled");
    }

    @Override
    public void onDisable() {
        log.info("NameChangeHistory disabled");
    }

    public TreeMap<Date, String> getFormerNames(Player p) {
        TreeMap<Date, String> formerNames = new TreeMap<Date, String>();
        String content = "";
        try {
            URL api = new URL("https://api.mojang.com/user/profiles/" + p.getUniqueId().toString().replace("-", "") + "/names");
            BufferedReader in = new BufferedReader(new InputStreamReader(api.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content = content + inputLine;
            }
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(NameChangeHistory.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            JSONArray json = (JSONArray) new JSONParser().parse(content);
            for (Object o : json.toArray()) {
                JSONObject jo = (JSONObject) o;
                String name = "" + jo.get("name");
                Date d = new Date(0L);
                if (jo.get("changedToAt") != null) {
                    d = new Date(Long.parseLong("" + jo.get("changedToAt")));
                }
                formerNames.put(d, "" + jo.get("name"));
            }
        } catch (ParseException ex) {
            Logger.getLogger(NameChangeHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return formerNames;
    }

    public boolean isLoginInfoEnabled() {
        return loginInfoEnabled;
    }

    public String getMessageFormat() {
        return messageFormat;
    }

    public String getNameFormat() {
        return nameFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }
}
