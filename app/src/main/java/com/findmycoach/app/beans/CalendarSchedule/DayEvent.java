package com.findmycoach.app.beans.CalendarSchedule;

/**
 * Created by ved on 6/3/15.
 */
public class DayEvent {
    private int event_start_hour;
    private int event_start_min;
    private int event_stop_hour;
    private int event_stop_min;
    private String event_name;

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public int getEvent_start_hour() {
        return event_start_hour;
    }

    public void setEvent_start_hour(int event_start_hour) {
        this.event_start_hour = event_start_hour;
    }

    public int getEvent_start_min() {
        return event_start_min;
    }

    public void setEvent_start_min(int event_start_min) {
        this.event_start_min = event_start_min;
    }

    public int getEvent_stop_hour() {
        return event_stop_hour;
    }

    public void setEvent_stop_hour(int event_stop_hour) {
        this.event_stop_hour = event_stop_hour;
    }

    public int getEvent_stop_min() {
        return event_stop_min;
    }

    public void setEvent_stop_min(int event_stop_min) {
        this.event_stop_min = event_stop_min;
    }
}
