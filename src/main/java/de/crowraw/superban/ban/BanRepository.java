package de.crowraw.superban.ban;

import de.crowraw.superban.SuperBan;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class BanRepository {
    private static ArrayList<Ban> bans = new ArrayList<>();
    private static ArrayList<Ban> newBans = new ArrayList<>();
    private static final String DATE_FORMAT = "MMM d, yyyy HH:mm a";

    public static void loadBanInCache(String uuid) {
        if (!isBanned(uuid)) {
            return;
        }
        for (Ban ban : bans) {
            if (ban.getUuid().equals(uuid)) {
                return;
            }
        }
        String dateString = null;
        String type = null;
        String banner = null;
        String bannedAt =null;
        try {
            PreparedStatement statement = SuperBan.getInstance().getMySQLUtil().getCon().
                    prepareStatement("SELECT `DATE` FROM `BAN_STATS` " +
                            "WHERE `UUID` = ?");
            statement.setString(1, uuid);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                dateString = rs.getString("DATE");
            }

            PreparedStatement statmentBanner = SuperBan.getInstance().getMySQLUtil().getCon().
                    prepareStatement("SELECT `BANNER` FROM `BAN_STATS` " +
                            "WHERE `uuid` = ?");
            statmentBanner.setString(1, uuid);
            ResultSet statementBanner = statmentBanner.executeQuery();
            while (statementBanner.next()) {
                banner = statementBanner.getString("BANNER");
            }


            PreparedStatement statementType = SuperBan.getInstance().getMySQLUtil().getCon().
                    prepareStatement("SELECT `TYPE` FROM `BAN_STATS` " +
                            "WHERE `uuid` = ?");
            statementType.setString(1, uuid);
            ResultSet resultSetType = statementType.executeQuery();
            while (resultSetType.next()) {
                type = resultSetType.getString("TYPE");
            }



            PreparedStatement statmentBannedAt = SuperBan.getInstance().getMySQLUtil().getCon().
                    prepareStatement("SELECT `WASBANNEDAT` FROM `BAN_STATS` " +
                            "WHERE `uuid` = ?");
            statmentBannedAt.setString(1, uuid);
            ResultSet bannedAtResult = statmentBannedAt.executeQuery();
            while (bannedAtResult.next()) {
                bannedAt = bannedAtResult.getString("WASBANNEDAT");
            }


            try {
                DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
                Date date = formatter.parse(dateString);
                Ban ban = new Ban(BanType.valueOf(type), date, uuid, banner,bannedAt);
                bans.add(ban);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isBanned(String uuid) {
        if (getBanForCache(uuid) != null) {
            return true;
        }
        try {
            PreparedStatement statement = SuperBan.getInstance().getMySQLUtil().getCon().prepareStatement("SELECT `UUID` FROM `BAN_STATS` WHERE `UUID` = ?");
            statement.setString(1, uuid);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Ban getBanForCache(String uuid) {
        AtomicReference<Ban> banAtom = new AtomicReference<>();
        bans.forEach(ban -> {
            if (ban.getUuid().equals(uuid)) {
                banAtom.set(ban);
            }
        });
        return banAtom.get();
    }

    public static void pushBan() {
        newBans.forEach(ban -> {
            SuperBan.getInstance().getMySQLUtil().updateAsync(() -> {
                try {
                    DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
                    String formattedString = formatter.format(ban.getTime());

                    SuperBan.getInstance().getMySQLUtil().getCon().createStatement().execute(
                            "INSERT INTO `BAN_STATS` (UUID, DATE ,TYPE, BANNER, WASBANNEDAT) VALUES ('" + ban.getUuid() + "', '" + formattedString + "', '" + ban.getBanType().toString() + "', '" + ban.getBanner() + "', '" + ban.getBannedAt() + "')");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }


    public static void removeBan(Ban ban) {
        newBans.remove(ban);
        bans.remove(ban);
        SuperBan.getInstance().getMySQLUtil().updateAsync(() -> {
            try {
                SuperBan.getInstance().getMySQLUtil().getCon().createStatement().execute("DELETE FROM `BAN_STATS` WHERE `UUID` ='" + ban.getUuid() + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
    }

    public static void addBan(Ban ban, String playerName) {



        if (isBanned(ban.getUuid())) {
            removeBan(ban);
        }
        newBans.add(ban);
        bans.add(ban);
        int seconds = ban.getTimeLeft()[0];
        int minutes = ban.getTimeLeft()[1];
        int hours = ban.getTimeLeft()[2];
        int days = ban.getTimeLeft()[3];
        int years = ban.getTimeLeft()[4];
        Player player = Bukkit.getPlayer(playerName);
        if (player == null || !player.isOnline()) {
            return;
        }
        switch (ban.getBanType()) {
            case NETWORK:
                player.kickPlayer(SuperBan.getInstance().getPrefix() +
                        SuperBan.getInstance().getConfigUtil().
                                getStringMessage("§4§lYou are banned! \n" +
                                        "Time until unban:§7 %year% §aYears,§7 %days% §aDays, §7%hours%§a Hours,§7 %minutes% §aMinutes,§7 %seconds%§a Seconds!" +
                                        "\n§6You can create a §4§lunban appeal§6 at our website", "message.banned").
                                replace("%year%", String.valueOf(years)).
                                replace("%days%", String.valueOf(days)).
                                replace("%hours%", String.valueOf(hours)).
                                replace("%minutes%", String.valueOf(minutes)).
                                replace("%seconds%", String.valueOf(seconds)));
                break;
            case CHAT:
                player.sendMessage(SuperBan.getInstance().getPrefix() +
                        SuperBan.getInstance().getConfigUtil().
                                getStringMessage("§4§lYou are muted! Time until umute:§7 %year% §aYears,§7 %days% §aDays, §7%hours%§a Hours,§7 %minutes% §aMinutes,§7 %seconds%§a Seconds!", "message.muted").
                                replace("%year%", String.valueOf(years)).
                                replace("%days%", String.valueOf(days)).
                                replace("%hours%", String.valueOf(hours)).
                                replace("%minutes%", String.valueOf(minutes)).
                                replace("%seconds%", String.valueOf(seconds)));
                break;
            case PVP:
                String message = SuperBan.getInstance().getConfigUtil().
                        getStringMessage("§4§lYou were banned from pvp! §7You cannot participate in pvp until the ban is over! §4§lTime till unban:§7 %year% §aYears,§7 %days% §aDays, §7%hours%§a Hours,§7 %minutes% §aMinutes,§7 %seconds%§a Seconds!", "message.pvpban").
                        replace("%year%", String.valueOf(years)).
                        replace("%days%", String.valueOf(days)).
                        replace("%hours%", String.valueOf(hours)).
                        replace("%minutes%", String.valueOf(minutes)).
                        replace("%seconds%", String.valueOf(seconds));
                player.sendMessage(SuperBan.getInstance().getPrefix() +
                        message);
                break;

        }
    }

    public static ArrayList<Ban> getNewBans() {
        return newBans;
    }

    public static String getDateFormat() {
        return DATE_FORMAT;
    }
}
