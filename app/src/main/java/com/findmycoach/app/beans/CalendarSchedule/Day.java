package com.findmycoach.app.beans.CalendarSchedule;

import java.util.List;


/**
 * Created by ved on 6/3/15.
 */
public class Day {
    private int day;
    private int month;
    private int year;
    private List<DayEvent> dayEvents;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<DayEvent> getDayEvents() {
        return dayEvents;
    }

    public void setDayEvents(List<DayEvent> dayEvents) {
        this.dayEvents = dayEvents;
    }
}
