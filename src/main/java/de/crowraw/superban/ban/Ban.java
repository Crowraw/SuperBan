package de.crowraw.superban.ban;

import java.util.Calendar;
import java.util.Date;

public class Ban {
    private BanType banType;
    private Date time;
    private String uuid;
    private String banner;
    private String bannedAt;
    public Ban(BanType banType, Date date, String uuid,String banner,String bannedAt) {
        this.banType = banType;
        this.time = date;
        this.uuid = uuid;
        this.banner=banner;
        this.bannedAt =bannedAt;
    }

    public boolean isOver() {
        return (getTimeLeft()[0] + getTimeLeft()[1] + getTimeLeft()[2] + getTimeLeft()[3] + getTimeLeft()[4]) < 0;
    }

    public Integer[] getTimeLeft() {
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        Date d2 = time;

        long difference_In_Time
                = d2.getTime() - d1.getTime();

        long difference_In_Seconds
                = (difference_In_Time
                / 1000)
                % 60;

        long difference_In_Minutes
                = (difference_In_Time
                / (1000 * 60))
                % 60;

        long difference_In_Hours
                = (difference_In_Time
                / (1000 * 60 * 60))
                % 24;

        long difference_In_Years
                = (difference_In_Time
                / (1000L * 60 * 60 * 24 * 365));

        long difference_In_Days
                = (difference_In_Time
                / (1000 * 60 * 60 * 24))
                % 365;
        Integer[] array = new Integer[5];
        array[0] = Math.toIntExact(difference_In_Seconds);
        array[1] = Math.toIntExact(difference_In_Minutes);
        array[2] = Math.toIntExact(difference_In_Hours);
        array[3] = Math.toIntExact(difference_In_Days);
        array[4] = Math.toIntExact(difference_In_Years);
        return array;
    }

    public void removeBan() {
        BanRepository.removeBan(this);
    }

    public String getUuid() {
        return uuid;
    }

    public BanType getBanType() {
        return banType;
    }

    public Date getTime() {
        return time;
    }

    public String getBannedAt() {
        return bannedAt;
    }

    public String getBanner() {
        return banner;
    }
}
