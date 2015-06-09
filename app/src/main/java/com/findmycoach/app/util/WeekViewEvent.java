package com.findmycoach.app.util;

import com.findmycoach.app.beans.CalendarSchedule.Mentee;
import com.findmycoach.app.beans.CalendarSchedule.MentorInfo;
import com.findmycoach.app.beans.CalendarSchedule.Slot;
import com.findmycoach.app.beans.CalendarSchedule.SlotDurationDetailBean;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;
import com.findmycoach.app.beans.CalendarSchedule.VacationCoincidingSlot;
import com.findmycoach.app.beans.CalendarSchedule.VacationDurationDetailBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by praka_000 on 3/4/2015.
 */
public class WeekViewEvent {

    private long mId;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String mName;
    private int mColor;
    private int slot_start_day;
    private int slot_start_month;
    private int slot_start_year;
    private int slot_start_hour;
    private int slot_start_minute;
    private int slot_stop_day;
    private int slot_stop_month;
    private int slot_stop_year;
    private int slot_stop_hour;
    private int slot_stop_minute;
    private String slot_type;
    private String mentor_id;
    private String mentor_availablity;
    private String [] slot_on_week_days;
    private String charges;
    private ArrayList<String> arrayList_sub_category;
    private ArrayList<SlotDurationDetailBean> slotDurationDetailBeans;
    private ArrayList<VacationDurationDetailBean> vacationDurationDetailBeans;
    private ArrayList<VacationCoincidingSlot> vacationCoincidingSlots;
    private List<Mentee> menteeFoundOnThisDate;
    private List<Vacation> coincidingVacations;
    private Slot slot;
    private Vacation vacation;
    private int event_type;
    private MentorInfo mentorInfo;

    public MentorInfo getMentorInfo() {
        return mentorInfo;
    }

    public void setMentorInfo(MentorInfo mentorInfo) {
        this.mentorInfo = mentorInfo;
    }

    public Vacation getVacation() {
        return vacation;
    }

    public void setVacation(Vacation vacation) {
        this.vacation = vacation;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public List<Vacation> getCoincidingVacations() {
        return coincidingVacations;
    }

    public void setCoincidingVacations(List<Vacation> coincidingVacations) {
        this.coincidingVacations = coincidingVacations;
    }

    public int getEvent_type() {
        return event_type;
    }

    public void setEvent_type(int event_type) {
        this.event_type = event_type;
    }

    public List<Mentee> getMenteeFoundOnThisDate() {
        return menteeFoundOnThisDate;
    }

    public void setMenteeFoundOnThisDate(List<Mentee> menteeFoundOnThisDate) {
        this.menteeFoundOnThisDate = menteeFoundOnThisDate;
    }

    public ArrayList<VacationCoincidingSlot> getVacationCoincidingSlots() {
        return vacationCoincidingSlots;
    }

    public void setVacationCoincidingSlots(ArrayList<VacationCoincidingSlot> vacationCoincidingSlots) {
        this.vacationCoincidingSlots = vacationCoincidingSlots;
    }

    public ArrayList<SlotDurationDetailBean> getSlotDurationDetailBeans() {
        return slotDurationDetailBeans;
    }

    public void setSlotDurationDetailBeans(ArrayList<SlotDurationDetailBean> slotDurationDetailBeans) {
        this.slotDurationDetailBeans = slotDurationDetailBeans;
    }

    public ArrayList<VacationDurationDetailBean> getVacationDurationDetailBeans() {
        return vacationDurationDetailBeans;
    }

    public void setVacationDurationDetailBeans(ArrayList<VacationDurationDetailBean> vacationDurationDetailBeans) {
        this.vacationDurationDetailBeans = vacationDurationDetailBeans;
    }

    public ArrayList<String> getArrayList_sub_category() {
        return arrayList_sub_category;
    }

    public String getCharges() {
        return charges;
    }

    public String[] getSlot_on_week_days() {
        return slot_on_week_days;
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public Calendar getmStartTime() {
        return mStartTime;
    }

    public void setmStartTime(Calendar mStartTime) {
        this.mStartTime = mStartTime;
    }

    public Calendar getmEndTime() {
        return mEndTime;
    }

    public void setmEndTime(Calendar mEndTime) {
        this.mEndTime = mEndTime;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public int getSlot_start_day() {
        return slot_start_day;
    }

    public void setSlot_start_day(int slot_start_day) {
        this.slot_start_day = slot_start_day;
    }

    public int getSlot_start_month() {
        return slot_start_month;
    }

    public void setSlot_start_month(int slot_start_month) {
        this.slot_start_month = slot_start_month;
    }

    public int getSlot_start_year() {
        return slot_start_year;
    }

    public void setSlot_start_year(int slot_start_year) {
        this.slot_start_year = slot_start_year;
    }

    public int getSlot_start_hour() {
        return slot_start_hour;
    }

    public void setSlot_start_hour(int slot_start_hour) {
        this.slot_start_hour = slot_start_hour;
    }

    public int getSlot_start_minute() {
        return slot_start_minute;
    }

    public void setSlot_start_minute(int slot_start_minute) {
        this.slot_start_minute = slot_start_minute;
    }

    public int getSlot_stop_day() {
        return slot_stop_day;
    }

    public void setSlot_stop_day(int slot_stop_day) {
        this.slot_stop_day = slot_stop_day;
    }

    public int getSlot_stop_month() {
        return slot_stop_month;
    }

    public void setSlot_stop_month(int slot_stop_month) {
        this.slot_stop_month = slot_stop_month;
    }

    public int getSlot_stop_year() {
        return slot_stop_year;
    }

    public void setSlot_stop_year(int slot_stop_year) {
        this.slot_stop_year = slot_stop_year;
    }

    public int getSlot_stop_hour() {
        return slot_stop_hour;
    }

    public void setSlot_stop_hour(int slot_stop_hour) {
        this.slot_stop_hour = slot_stop_hour;
    }

    public int getSlot_stop_minute() {
        return slot_stop_minute;
    }

    public void setSlot_stop_minute(int slot_stop_minute) {
        this.slot_stop_minute = slot_stop_minute;
    }

    public String getSlot_type() {
        return slot_type;
    }

    public void setSlot_type(String slot_type) {
        this.slot_type = slot_type;
    }

    public String getMentor_id() {
        return mentor_id;
    }

    public void setMentor_id(String mentor_id) {
        this.mentor_id = mentor_id;
    }

    public String getMentor_availablity() {
        return mentor_availablity;
    }

    public void setMentor_availablity(String mentor_availablity) {
        this.mentor_availablity = mentor_availablity;
    }



    public WeekViewEvent() {

    }

    /**
     * Initializes the event for week view.
     *
     * @param id          The id of the event.
     * @param name        Name of the event.
     * @param startYear   Year when the event starts.
     * @param startMonth  Month when the event starts.
     * @param startDay    Day when the event starts.
     * @param startHour   Hour (in 24-hour format) when the event starts.
     * @param startMinute Minute when the event starts.
     * @param endYear     Year when the event ends.
     * @param endMonth    Month when the event ends.
     * @param endDay      Day when the event ends.
     * @param endHour     Hour (in 24-hour format) when the event ends.
     * @param endMinute   Minute when the event ends.
     */
    public WeekViewEvent(long id, String name, int startYear, int startMonth, int startDay, int startHour, int startMinute, int endYear, int endMonth, int endDay, int endHour, int endMinute) {
        this.mId = id;

        this.mStartTime = Calendar.getInstance();
        this.mStartTime.set(Calendar.YEAR, startYear);
        this.mStartTime.set(Calendar.MONTH, startMonth - 1);
        this.mStartTime.set(Calendar.DAY_OF_MONTH, startDay);
        this.mStartTime.set(Calendar.HOUR_OF_DAY, startHour);
        this.mStartTime.set(Calendar.MINUTE, startMinute);

        this.mEndTime = Calendar.getInstance();
        this.mEndTime.set(Calendar.YEAR, endYear);
        this.mEndTime.set(Calendar.MONTH, endMonth - 1);
        this.mEndTime.set(Calendar.DAY_OF_MONTH, endDay);
        this.mEndTime.set(Calendar.HOUR_OF_DAY, endHour);
        this.mEndTime.set(Calendar.MINUTE, endMinute);

        this.mName = name;
    }

    /**
     * Initializes the event for week view.
     *
     * @param id        The id of the event.
     * @param name      Name of the event.
     * @param startTime The time when the event starts.
     * @param endTime   The time when the event ends.
     */
    public WeekViewEvent(long id, String name, Calendar startTime, Calendar endTime,String slot_type, int event_type) {
        this.mId = id;
        this.mName = name;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.event_type = event_type;
        this.slot_type=slot_type;

    }

/* This weekViewEvnent constructor is for Event(class detail) on mentor week view */
    public WeekViewEvent(long id, String name, Calendar startTime, Calendar endTime,List<Mentee> menteeFoundOnThisDate,String slot_type,Slot slot,int event_type) {  /* Here event_type differentate this constructor as Class event*/
        this.mId = id;
        this.mName = name;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.menteeFoundOnThisDate = menteeFoundOnThisDate;
        this.slot_type=slot_type;
        this.slot = slot;
        this.event_type = event_type;


    }


    /* This weekViewEvnent constructor is for coincidingVacations on mentor week view */
    public WeekViewEvent(long id, String name, Calendar startTime, Calendar endTime,List<Vacation> coincidingVacations,Slot slot, int event_type) {
        this.mId = id;
        this.mName = name;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.menteeFoundOnThisDate = menteeFoundOnThisDate;
        this.slot_type=slot_type;
        this.slot = slot;
        this.event_type = event_type;


    }

/* This if for  Mentor slot where nither vacation nor any class found in mentor schedule weekview */
    public WeekViewEvent(long id, String name,Calendar startTime,Calendar endTime, Slot slot,int event_type){
        this.mId = id;
        this.mName = name;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.slot = slot;
        this.event_type = event_type;
    }


    /* for non coinciding vacation in mentor schedule weekview */
    public WeekViewEvent(long id, String name,Calendar startTime,Calendar endTime,Vacation vacation,int event_type){
        this.mId = id;
        this.mName = name;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.event_type = event_type;
        this.vacation = vacation;
    }



    /* for class info to mentee  */
    public WeekViewEvent(long id, String name,Calendar startTime,Calendar endTime,Slot slot, MentorInfo mentorInfo, List<Mentee> menteeFoundOnThisDate,int event_type){
        this.mId = id;
        this.mName = name;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.event_type = event_type;
        this.slot = slot;
        this.mentorInfo = mentorInfo;
        this.menteeFoundOnThisDate = menteeFoundOnThisDate;


    }

    /* Vacation in mentee schedule */
    public WeekViewEvent(long id, String name, Calendar startTime, Calendar endTime, Slot slot,MentorInfo mentorInfo,int event_type,List<Vacation> coinciding_vacation_of_this_day_for_this_slot){
        this.mId = id;
        this.mName = name;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.event_type = event_type;
        this.slot = slot;
        this.mentorInfo = mentorInfo;
        this.coincidingVacations = coinciding_vacation_of_this_day_for_this_slot;

    }




    /**
     * Initializes the event for week view, this week-view will display free slots available and mentee can select one free slot for his schedule request with mentor.
     */

    public WeekViewEvent(long id, String name, Calendar startTime, Calendar endTime, int slot_start_day, int slot_start_month, int slot_start_year, int slot_stop_day, int slot_stop_month, int slot_stop_year, int slot_start_hour, int slot_start_minute, int slot_stop_hour, int slot_stop_minute, String slot_type,String [] slot_on_week_days, String mentor_id, String mentor_availability,int free_slot_event_type,String charges,ArrayList<String> arrayList_sub_category,ArrayList<SlotDurationDetailBean> slotDurationDetailBeans, ArrayList<VacationCoincidingSlot> vacationCoincidingSlots) {
        this.mId = id;
        this.mName = name;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.slot_start_day = slot_start_day;
        this.slot_start_month = slot_start_month;
        this.slot_start_year = slot_start_year;
        this.slot_stop_day = slot_stop_day;
        this.slot_stop_month = slot_stop_month;
        this.slot_stop_year = slot_stop_year;
        this.slot_start_hour = slot_start_hour;
        this.slot_start_minute = slot_start_minute;
        this.slot_stop_hour = slot_stop_hour;
        this.slot_stop_minute = slot_stop_minute;
        this.slot_type = slot_type;
        this.slot_on_week_days=slot_on_week_days;
        this.mentor_id = mentor_id;
        this.mentor_availablity = mentor_availability;
        this.event_type=free_slot_event_type;
        this.charges=charges;
        this.arrayList_sub_category=arrayList_sub_category;
        this.slotDurationDetailBeans=slotDurationDetailBeans;
        this.vacationCoincidingSlots=vacationCoincidingSlots;

    }


    public Calendar getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Calendar startTime) {
        this.mStartTime = startTime;
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Calendar endTime) {
        this.mEndTime = endTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

}
