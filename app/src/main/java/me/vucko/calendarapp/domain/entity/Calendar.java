package me.vucko.calendarapp.domain.entity;

public class Calendar {

    private int id;
    private int time;
    private String name;
    private int snooze;
    private int vibrate;

    public Calendar() {
    }

    public Calendar(int time, String name) {
        this.time = time;
        this.name = name;
    }

    public Calendar(int id, String name, int time) {
        this.id = id;
        this.name = name;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
