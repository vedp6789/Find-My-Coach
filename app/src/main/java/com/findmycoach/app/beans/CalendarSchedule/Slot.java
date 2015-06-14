package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ved on 31/5/15.
 */
public class Slot implements Parcelable {
    private static final String TAG = "FMC";

    public Slot() {
        events = new ArrayList<Event>();
        vacations = new ArrayList<Vacation>();
    }

    public String slot_id;
    public String mentor_id;
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
    public String slot_subject;

    public String getSlot_subject() {
        return slot_subject;
    }

    public void setSlot_subject(String slot_subject) {
        this.slot_subject = slot_subject;
    }

    public String getMentor_id() {
        return mentor_id;
    }

    public void setMentor_id(String mentor_id) {
        this.mentor_id = mentor_id;
    }

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
        dest.writeString(this.mentor_id);
        dest.writeTypedList(this.events);
        dest.writeTypedList(this.vacations);
        dest.writeString(this.slot_created_on_network_success);
        dest.writeString(this.slot_subject);
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
        this.mentor_id = source.readString();
        source.readTypedList(this.events, DayEvent.CREATOR);
        source.readTypedList(this.vacations, DayVacation.CREATOR);
        this.slot_created_on_network_success = source.readString();
        this.slot_subject =source.readString();
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

                List<Mentee> mentees = event.getMentees();
                level_mentee: for (int mentee_no = 0; mentee_no < mentees.size(); mentee_no++) {
                    Mentee mentee = mentees.get(mentee_no);
                    List<EventDuration> eventDurations =mentee.getEventDurations();  /* For each mentee, there event duration can be in broken chunk of durations. So we have to check whether the specific day of slot coming in between of one of the chunk with valid week-day or not. We have to find number of active classes between that chunk where this specific day lie and we have to match whether this day is similar to one of the active days or not  */
                    for(int event_duration_no=0; event_duration_no < eventDurations.size() ; event_duration_no++ ){
                        EventDuration eventDuration=eventDurations.get(event_duration_no);
                        String start_date =eventDuration.getStart_date();
                        Calendar calendar_start_date =Calendar.getInstance();
                        calendar_start_date.set(Integer.parseInt(start_date.split("-")[0]),Integer.parseInt(start_date.split("-")[1])-1,Integer.parseInt(start_date.split("-")[2]));
                        String stop_date =eventDuration.getStop_date();
                        Calendar calendar_stop_date =Calendar.getInstance();
                        calendar_stop_date.set(Integer.parseInt(stop_date.split("-")[0]),Integer.parseInt(stop_date.split("-")[1])-1,Integer.parseInt(stop_date.split("-")[2]));

                        ArrayList<SlotDurationDetailBean> activeClassDaysAndTheirWeekDay = calculateNoOfTotalClassDays(calendar_start_date,calendar_stop_date,slot_week_days);
                        for(int activeClassDay = 0; activeClassDay < activeClassDaysAndTheirWeekDay.size() ; activeClassDay++ ){
                            SlotDurationDetailBean slotDurationDetailBean = activeClassDaysAndTheirWeekDay.get(activeClassDay);
                            String active_class_date = slotDurationDetailBean.getDate();
                            Calendar calendar_active_class_date=Calendar.getInstance();
                            calendar_active_class_date.set(Integer.parseInt(active_class_date.split("-")[0]),Integer.parseInt(active_class_date.split("-")[1])-1,Integer.parseInt(active_class_date.split("-")[2]));
                            long active_class_in_millis =calendar_active_class_date.getTimeInMillis();

                            if(active_class_in_millis == calendar_time_in_millis){
                                /* The day of grid matches with one of mentee active class days */
                                event_found = true;
                                break level_mentee;
                            }
                        }


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


                Calendar calendar_vacation_start_date = Calendar.getInstance();
                calendar_vacation_start_date.set(Integer.parseInt(start_date.split("-")[0]), Integer.parseInt(start_date.split("-")[1])-1, Integer.parseInt(start_date.split("-")[2]));
                long vacation_start_date_in_millis = calendar_vacation_start_date.getTimeInMillis();


                Calendar calendar_vacation_stop_date = Calendar.getInstance();
                calendar_vacation_stop_date.set(Integer.parseInt(stop_date.split("-")[0]), Integer.parseInt(stop_date.split("-")[1])-1, Integer.parseInt(stop_date.split("-")[2]));
                long vacation_stop_date_in_millis = calendar_vacation_stop_date.getTimeInMillis();


                if ((calendar_time_in_millis == vacation_start_date_in_millis) ||
                        (calendar_time_in_millis == vacation_stop_date_in_millis) ||
                        (calendar_time_in_millis < vacation_stop_date_in_millis && calendar_time_in_millis > vacation_start_date_in_millis)) {

                        vacation_found = true;
                        /* this proves there is a coinciding vacation for the current grid day*/
                        break;



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

    public ArrayList<SlotDurationDetailBean> calculateNoOfTotalClassDays(Calendar calendar_schedule_start_date, Calendar calendar_stop_date_of_schedule, String[] slot_on_week_days) {

        int workDays = 0;
        ArrayList<SlotDurationDetailBean> slotDurationDetailBeans = new ArrayList<SlotDurationDetailBean>();
        //Return 0 if start and end are the same

        /*if (calendar_schedule_start_date.getTimeInMillis() == calendar_stop_date_of_schedule.getTimeInMillis()) {
            return slotDurationDetailBeans;
        }*/

        List<Integer> selectedDays = new ArrayList<Integer>();
        for (String d : slot_on_week_days) {
            if (d.equalsIgnoreCase("su"))
                selectedDays.add(1);
            if (d.equalsIgnoreCase("m"))
                selectedDays.add(2);
            if (d.equalsIgnoreCase("t"))
                selectedDays.add(3);
            if (d.equalsIgnoreCase("w"))
                selectedDays.add(4);
            if (d.equalsIgnoreCase("th"))
                selectedDays.add(5);
            if (d.equalsIgnoreCase("f"))
                selectedDays.add(6);
            if (d.equalsIgnoreCase("s"))
                selectedDays.add(7);
        }

        do {
            //excluding start date

            if (selectedDays.contains(calendar_schedule_start_date.get(Calendar.DAY_OF_WEEK))) {
                ++workDays;
                SlotDurationDetailBean slotDurationDetailBean = new SlotDurationDetailBean();
                Log.d(TAG, "year: " + calendar_schedule_start_date.get(Calendar.YEAR) + "month: " + calendar_schedule_start_date.get(Calendar.MONTH) + "day_of_month: " + calendar_schedule_start_date.get(Calendar.DAY_OF_MONTH) + "date: " + calendar_schedule_start_date.get(Calendar.DATE));
                slotDurationDetailBean.setDate(calendar_schedule_start_date.get(Calendar.YEAR) + "-" + calendar_schedule_start_date.get(Calendar.MONTH) + "-" + calendar_schedule_start_date.get(Calendar.DAY_OF_MONTH));
                slotDurationDetailBean.setWeek_day(String.valueOf(calendar_schedule_start_date.get(Calendar.DAY_OF_WEEK)));
                slotDurationDetailBeans.add(slotDurationDetailBean);
            }
            calendar_schedule_start_date.add(Calendar.DAY_OF_MONTH, 1);



        }
        while (calendar_schedule_start_date.getTimeInMillis() <= calendar_stop_date_of_schedule.getTimeInMillis()); //excluding end date

        return slotDurationDetailBeans;
    }


}
