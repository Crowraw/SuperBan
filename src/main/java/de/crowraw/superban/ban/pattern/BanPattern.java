package de.crowraw.superban.ban.pattern;

import de.crowraw.superban.SuperBan;
import de.crowraw.superban.ban.BanType;

public class BanPattern {

    private int years;
    private int days;
    private int hours;
    private int minutes;
    private int seconds;
    private BanType type;
    private String patterName;

    public BanPattern(int years, int days, int hours, int minutes, int seconds, BanType type, String patterName) {
        this.years = years;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.type = type;
        this.patterName = patterName;
    }

    public static BanPattern getPattern(String patterName) {
        if (SuperBan.getInstance().getConfigUtil().getYamlConfiguration().get("pattern." + patterName + ".years") == null) {
            return null;
        }
        int years = SuperBan.getInstance().getConfigUtil().getYamlConfiguration().getInt("pattern." + patterName + ".years");
        int days = SuperBan.getInstance().getConfigUtil().getYamlConfiguration().getInt("pattern." + patterName + ".days");
        int hours = SuperBan.getInstance().getConfigUtil().getYamlConfiguration().getInt("pattern." + patterName + ".hours");
        int minutes = SuperBan.getInstance().getConfigUtil().getYamlConfiguration().getInt("pattern." + patterName + ".minutes");
        int seconds = SuperBan.getInstance().getConfigUtil().getYamlConfiguration().getInt("pattern." + patterName + ".seconds");

        BanType banType = BanType.valueOf(SuperBan.getInstance().getConfigUtil().getYamlConfiguration().getString("pattern." + patterName + ".type"));
        return new BanPattern(years, days, hours, minutes, seconds, banType, patterName);
    }

    public BanType getType() {
        return type;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getYears() {
        return years;
    }

    public String getPatterName() {
        return patterName;
    }
}
