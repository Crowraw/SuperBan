package de.crowraw.superban;

import de.crowraw.api.data.ConfigUtil;
import de.crowraw.api.data.MySQLUtil;
import de.crowraw.superban.ban.BanRepository;
import de.crowraw.superban.ban.BanType;
import de.crowraw.superban.commands.*;
import de.crowraw.superban.listener.ChatHandler;
import de.crowraw.superban.listener.HitHandler;
import de.crowraw.superban.listener.JoinHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class SuperBan extends JavaPlugin {
    private MySQLUtil mySQLUtil;
    private ConfigUtil configUtil;
    private static SuperBan instance;
    private String prefix;
    ArrayList<String> noBannable = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        getLogger().info("Contact the Developer if you have any questions: Crowraw#9875");
        configUtil = new ConfigUtil("plugins//SuperBan//config.yml");


        if (getConfigUtil().getYamlConfiguration().get("plugin.notbannable") == null) {
            noBannable.add("AddNameHereIfYouDontWantHimToGetBanned");
            getConfigUtil().getYamlConfiguration().set("plugin.notbannable", noBannable);
            getConfigUtil().saveConfig();
        }
        List<String> arrayList = getConfigUtil().getYamlConfiguration().getStringList("plugin.notbannable");
        noBannable.addAll(arrayList);
        if (getConfigUtil().getYamlConfiguration().get("pattern.example.years") == null) {
            getConfigUtil().getYamlConfiguration().set("pattern.example.years", 1);
            getConfigUtil().getYamlConfiguration().set("pattern.example.days", 1);
            getConfigUtil().getYamlConfiguration().set("pattern.example.hours", 1);
            getConfigUtil().getYamlConfiguration().set("pattern.example.minutes", 1);
            getConfigUtil().getYamlConfiguration().set("pattern.example.seconds", 1);
            getConfigUtil().getYamlConfiguration().set("pattern.example.years", 1);
            getConfigUtil().getYamlConfiguration().set("pattern.example.type", BanType.NETWORK.toString());
            getConfigUtil().saveConfig();
        }
        mySQLUtil = new MySQLUtil(configUtil.getHost(), configUtil.getPort(), configUtil.getDatabase(), configUtil.getUser(), configUtil.getPassword());
        if (mySQLUtil.hasConnection()) {
            mySQLUtil.createTable("BAN_STATS", "UUID VARCHAR(100), DATE VARCHAR(100), TYPE VARCHAR(100), BANNER VARCHAR(100), WASBANNEDAT VARCHAR(100)");
        } else {
            getLogger().warning("COULD NOT CREATE MYSQL CONNECTION!!!! PLUGIN WILL BE DISABLED");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        prefix = configUtil.getStringMessage("§6[§4§lSuperBan§r§6] ", "message.prefix");
        registerCommand();

        registerListener(Bukkit.getPluginManager());
        //    test();



    }

    private void registerCommand() {
        getCommand("pvpban").setExecutor(new PvPBanCommand());
        getCommand("patternban").setExecutor(new PatternBanCommand());
        getCommand("sorry").setExecutor(new SorryCommand());
        getCommand("mute").setExecutor(new MuteCommand());
        getCommand("info").setExecutor(new InfoCommand());
        getCommand("ban").setExecutor(new BanCommand());
        getCommand("gui").setExecutor(new GUICommand());
    }

    private void registerListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new JoinHandler(), this);
        pluginManager.registerEvents(new ChatHandler(), this);
        pluginManager.registerEvents(new HitHandler(), this);
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("" + BanRepository.getNewBans().size() + " people were banned / muted today");

        BanRepository.pushBan();
    }

    public ConfigUtil getConfigUtil() {
        return configUtil;
    }

    public static SuperBan getInstance() {
        return instance;
    }

    public MySQLUtil getMySQLUtil() {
        return mySQLUtil;
    }

    public ArrayList<String> getNoBannable() {
        return noBannable;
    }

    public String getPrefix() {
        return prefix;
    }
}
//ideen: ban gui