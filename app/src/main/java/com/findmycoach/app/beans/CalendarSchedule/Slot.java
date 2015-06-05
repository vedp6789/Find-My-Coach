package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ved on 31/5/15.
 */
public class Slot implements Parcelable {
    public Slot() {
        events = new ArrayList<Event>();
        vacations = new ArrayList<Vacation>();
    }

    public String slot_id;
    public String slot_start_time;
    public String slot_stop_time;
    public String slot_start_date;
    public String slot_stop_date;
    public String slot_type;
    public String slot_max_users;
    public String[] slot_week_days;
    public List<Event> events;
    public List<Vacation> vacations;
    public String slot_created_on_network_success;


    public String isSlot_created_on_network_success() {
        return slot_created_on_network_success;
    }

    public void setSlot_created_on_network_success(String slot_created_on_network_success) {
        this.slot_created_on_network_success = slot_created_on_network_success;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }


    public String getSlot_id() {
        return slot_id;
    }

    public void setSlot_id(String slot_id) {
        this.slot_id = slot_id;
    }

    public String[] getSlot_week_days() {
        return slot_week_days;
    }

    public void setSlot_week_days(String[] slot_week_days) {
        this.slot_week_days = slot_week_days;
    }

    public String getSlot_max_users() {
        return slot_max_users;
    }

    public void setSlot_max_users(String slot_max_users) {
        this.slot_max_users = slot_max_users;
    }

    public String getSlot_type() {
        return slot_type;
    }

    public void setSlot_type(String slot_type) {
        this.slot_type = slot_type;
    }

    public String getSlot_start_date() {
        return slot_start_date;
    }

    public void setSlot_start_date(String slot_start_date) {
        this.slot_start_date = slot_start_date;
    }

    public String getSlot_stop_date() {
        return slot_stop_date;
    }

    public void setSlot_stop_date(String slot_stop_date) {
        this.slot_stop_date = slot_stop_date;
    }

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


    public List<Vacation> getVacations() {
        return vacations;
    }

    public void setVacations(List<Vacation> vacations) {
        this.vacations = vacations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.slot_start_date);
        dest.writeString(this.slot_stop_date);
        dest.writeString(this.slot_start_time);
        dest.writeString(this.slot_stop_time);
        dest.writeString(this.slot_type);
        dest.writeString(this.slot_max_users);
        dest.writeStringArray(this.slot_week_days);
        dest.writeString(this.slot_id);
        dest.writeTypedList(this.events);
        dest.writeTypedList(this.vacations);
        dest.writeString(this.slot_created_on_network_success);
    }


    public Slot(Parcel source) {
    /*
     * Reconstruct from the Parcel
     */
        this();
        this.slot_start_date = source.readString();
        this.slot_stop_date = source.readString();
        this.slot_start_time = source.readString();
        this.slot_stop_time = source.readString();
        this.slot_type = source.readString();
        this.slot_max_users = source.readString();
        this.slot_week_days = source.createStringArray();
        this.slot_id = source.readString();
        source.readTypedList(this.events, DayEvent.CREATOR);
        source.readTypedList(this.vacations, DayVacation.CREATOR);
        this.slot_created_on_network_success = source.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DaySlot createFromParcel(Parcel in) {
            return new DaySlot(in);
        }

        public DaySlot[] newArray(int size) {
            return new DaySlot[size];
        }
    };


    /**
     * Utility functions
     */

    public List<ClassBean> getAvailablClasses() {
        return getAvailablClasses(new Date(this.slot_start_date), new Date(this.slot_stop_date));
    }

    public List<ClassBean> getAvailablClasses(Date startDate, Date stopDate) {
        List<ClassBean> classes = new ArrayList<ClassBean>();

        return classes;
    }


    public boolean anyEventFound(long calendar_time_in_millis) {     /* checking whether any event found for this specific day or not */
        if (events.size() > 0) {
            boolean event_found = false;
            for (int event_index = 0; event_index < events.size(); event_index++) {
                Event event = events.get(event_index);

                String stop_date = event.getEvent_stop_date();

                List<Mentee> mentees = event.getMentees();
                for (int mentee_no = 0; mentee_no < mentees.size(); mentee_no++) {
                    Mentee mentee = mentees.get(mentee_no);
                    String start_date = mentee.getEvent_start_date();

                    Calendar calendar_event_start_date = Calendar.getInstance();
                    calendar_event_start_date.set(Integer.parseInt(start_date.split("-")[0]), Integer.parseInt(start_date.split("-")[1]), Integer.parseInt(start_date.split("-")[2]));
                    long event_start_date_in_millis = calendar_event_start_date.getTimeInMillis();


                    Calendar calendar_event_stop_date = Calendar.getInstance();
                    calendar_event_stop_date.set(Integer.parseInt(stop_date.split("-")[0]), Integer.parseInt(stop_date.split("-")[1]), Integer.parseInt(stop_date.split("-")[2]));
                    long event_stop_date_in_millis = calendar_event_stop_date.getTimeInMillis();


                    if ((calendar_time_in_millis == event_start_date_in_millis) ||
                            (calendar_time_in_millis == event_stop_date_in_millis) ||
                            (calendar_time_in_millis < event_stop_date_in_millis && calendar_time_in_millis > event_start_date_in_millis)) {
                        event_found = true;
                        break;
                    } else {
                        event_found = false;
                    }
                }

            }
            return event_found;
        } else {
            return false;
        }
    }


    public boolean anyVacationFound(Calendar grid_day) {     /* checking whether any vacation found for a specific day or not */

        long calendar_time_in_millis=grid_day.getTimeInMillis();
        int week_day_for_grid_day = grid_day.get(Calendar.DAY_OF_WEEK);
        if (vacations.size() > 0) {
            boolean vacation_found = false;
            for (int vacation_index = 0; vacation_index < vacations.size(); vacation_index++) {
                Vacation vacation = vacations.get(vacation_index);
                String start_date = vacation.getStart_date();
                String stop_date = vacation.getStop_date();
                String [] vacation_week_days= vacation.getWeek_days();

                Calendar calendar_vacation_start_date = Calendar.getInstance();
                calendar_vacation_start_date.set(Integer.parseInt(start_date.split("-")[0]), Integer.parseInt(start_date.split("-")[1]), Integer.parseInt(start_date.split("-")[2]));
                long vacation_start_date_in_millis = calendar_vacation_start_date.getTimeInMillis();


                Calendar calendar_vacation_stop_date = Calendar.getInstance();
                calendar_vacation_stop_date.set(Integer.parseInt(stop_date.split("-")[0]), Integer.parseInt(stop_date.split("-")[1]), Integer.parseInt(stop_date.split("-")[2]));
                long vacation_stop_date_in_millis = calendar_vacation_stop_date.getTimeInMillis();


                if ((calendar_time_in_millis == vacation_start_date_in_millis) ||
                        (calendar_time_in_millis == vacation_stop_date_in_millis) ||
                        (calendar_time_in_millis < vacation_stop_date_in_millis && calendar_time_in_millis > vacation_start_date_in_millis)) {

                    if(thisDayMatchesWithVacationWeekDays(vacation_week_days, week_day_for_grid_day)){
                        vacation_found = true;
                        /* this proves there is a coinciding vacation for the current grid day*/
                        break;
                    }



                } else {
                    vacation_found = false;
                }


            }
            return vacation_found;
        } else {
            return false;
        }
    }

    private boolean thisDayMatchesWithVacationWeekDays(String[] vacation_week_days, int week_day_for_grid_day) {
        boolean day_matches = false;
        String this_day_week_day = null;   /* this will have the day which is calendar current day according to grid view position*/
        switch (week_day_for_grid_day) {
            case 1:
                this_day_week_day = "S";
                break;
            case 2:
                this_day_week_day = "M";
                break;
            case 3:
                this_day_week_day = "T";
                break;
            case 4:
                this_day_week_day = "W";
                break;
            case 5:
                this_day_week_day = "Th";
                break;
            case 6:
                this_day_week_day = "F";
                break;
            case 7:
                this_day_week_day = "Sa";
                break;
        }

        for (int vacation_week_day_index = 0; vacation_week_day_index < vacation_week_days.length; vacation_week_day_index++) {
            if (this_day_week_day != null && this_day_week_day.equalsIgnoreCase(slot_week_days[vacation_week_day_index])) {
                day_matches = true;
            }
        }

        return day_matches;
    }


}
