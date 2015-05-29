package com.findmycoach.app.beans.CalendarSchedule;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by ved on 6/3/15.
 */
public class Day implements Parcelable {

    public Day(){
        eventBeans =new ArrayList<EventBean>();
        slotBeans =new ArrayList<SlotBean>();
        vacationBeans =new ArrayList<VacationBean>();
    }



    public String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static Creator getCreator() {
        return CREATOR;
    }

    public List<EventBean> eventBeans;
    public List<SlotBean> slotBeans;
    public List<VacationBean> vacationBeans;

    public List<VacationBean> getVacationBeans() {
        return vacationBeans;
    }

    public void setVacationBeans(List<VacationBean> vacationBeans) {
        this.vacationBeans = vacationBeans;
    }

    public List<SlotBean> getSlotBeans() {
        return slotBeans;
    }

    public void setSlotBeans(List<SlotBean> slotBeans) {
        this.slotBeans = slotBeans;
    }

    public List<EventBean> getEventBeans() {
        return eventBeans;
    }

    public void setEventBeans(List<EventBean> eventBeans) {
        this.eventBeans = eventBeans;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);

        dest.writeTypedList(this.eventBeans);
        dest.writeTypedList(this.slotBeans);
        dest.writeTypedList(this.vacationBeans);

    }
    public Day(Parcel parcel){
        //readFromParcel(parcel);
        this();
        this.date=parcel.readString();
        //List<DayEvent> list = null;
        parcel.readTypedList(this.eventBeans, EventBean.CREATOR);
        parcel.readTypedList(this.slotBeans, SlotBean.CREATOR);
        parcel.readTypedList(this.vacationBeans, VacationBean.CREATOR);
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }

        public Day[] newArray(int size) {
            return new Day[size];
        }
    };


}
