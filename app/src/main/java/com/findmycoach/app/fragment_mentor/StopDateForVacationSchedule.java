package com.findmycoach.app.fragment_mentor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.AddNewSlotActivity;
import com.findmycoach.app.activity.ScheduleYourVacation;

import java.util.Calendar;

/**
 * Created by praka_000 on 3/3/2015.
 */
public class StopDateForVacationSchedule extends DialogFragment implements View.OnClickListener {
    DatePicker datePicker;
    Button b_ok,b_can;

    ScheduleYourVacation scheduleYourVacation;

    Calendar calendar;
    long time;
    static boolean allow_forever =true;// for making user select forever option for till date
    private static final String selected_date=null;

    private static final String TAG="FMC";

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.b_ok:
                String day=String.valueOf(datePicker.getDayOfMonth());
                String month=String.valueOf(datePicker.getMonth()+1);
                String year=String.valueOf(datePicker.getYear());
                dismiss();
                scheduleYourVacation.setSelectedTillDate(day, month, year, allow_forever);
                break;
            case R.id.b_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scheduleYourVacation =new ScheduleYourVacation();
        int [] date=scheduleYourVacation.getTillInitialLimit();
        allow_forever=false;

        calendar = Calendar.getInstance();
        calendar.set(date[2], date[1], date[0]+1, 0, 0, 0);
        time = calendar.getTimeInMillis();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_date_fragment,container,false);
        datePicker= (DatePicker) view.findViewById(R.id.slotdatePicker);
        b_ok= (Button) view.findViewById(R.id.b_ok);
        b_can= (Button) view.findViewById(R.id.b_cancel);

        datePicker.setMinDate(time);



        b_ok.setOnClickListener(this);
        b_can.setOnClickListener(this);
        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.date_picker));
        dialog.setCanceledOnTouchOutside(false);

        return view;
    }
}
