package com.findmycoach.app.beans.CalendarSchedule;

/**
 * Created by ved on 11/3/15.
 */
public class DaySlot {
    private String slot_start_time;
    private String slot_stop_time;

    public String getSlot_start_time() {
        return slot_start_time;
    }

    public void setSlot_start_time(String slot_start_time) {
        this.slot_start_time = slot_start_time;
    }

    public String getSlot_stop_time() {
        return slot_stop_time;
    }

    public void setSlot_stop_time(String slot_stop_time) {
        this.slot_stop_time = slot_stop_time;
    }
}
