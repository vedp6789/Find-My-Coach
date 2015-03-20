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

import com.findmycoach.app.activity.AddNewSlotActivity;
import com.findmycoach.app.R;
import com.findmycoach.app.activity.ScheduleNewClass;

import java.util.Calendar;

/**
 * Created by praka_000 on 2/17/2015.
 */
public class TillDateDialogFragment extends DialogFragment implements View.OnClickListener {
    DatePicker datePicker;
    Button b_ok,b_can;
    public AddNewSlotActivity addNewSlotActivity;
    public ScheduleNewClass scheduleNewClass;
    CheckBox cb_till_date;
    Calendar calendar;
    long time;
    static boolean allow_forever =false;// for making user select forever option for till date
    private static final String selected_date=null;
    private String called_from;
    private static final String TAG="FMC";

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.b_ok:
                String day=String.valueOf(datePicker.getDayOfMonth());
                String month=String.valueOf(datePicker.getMonth()+1);
                String year=String.valueOf(datePicker.getYear());
                dismiss();
                if(addNewSlotActivity != null ){
                    addNewSlotActivity.setSelectedTillDate(day,month,year,allow_forever);
                }else if(scheduleNewClass != null){
                    scheduleNewClass.setSelectedTillDate(day,month,year,allow_forever);   // allow_forever i.e. fourth parameter is not being used here
                }

                break;
            case R.id.b_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int [] date;
//        called_from=getArguments().getString("ComingFrom");
//
//        if(called_from.equals("AddNewSlotActivity")){
//            addNewSlotActivity=new AddNewSlotActivity();
//            date=addNewSlotActivity.getTillInitialLimit();
//        }else{
//            scheduleNewClass=new ScheduleNewClass();
//            date=scheduleNewClass.getTillInitialLimit();
//        }
        allow_forever=false;

        if(addNewSlotActivity == null)
            date = scheduleNewClass.getTillInitialLimit();
        else
            date = addNewSlotActivity.getTillInitialLimit();
        calendar = Calendar.getInstance();
        calendar.set(date[2], date[1], date[0]+1, 0, 0, 0);
        time = calendar.getTimeInMillis();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_till_date_fragment,container,false);
        datePicker= (DatePicker) view.findViewById(R.id.slotdatePicker);
        b_ok= (Button) view.findViewById(R.id.b_ok);
        b_can= (Button) view.findViewById(R.id.b_cancel);
        cb_till_date= (CheckBox) view.findViewById(R.id.cb_till_date);
        datePicker.setMinDate(time);

        cb_till_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (cb_till_date.isChecked()) {
                    allow_forever=true;
                    Log.d(TAG,"Forever allowed:"+allow_forever);
                } else {
                    allow_forever=false;
                    Log.d(TAG,"Forever allowed:"+allow_forever);
                }
            }
        });

        if(scheduleNewClass != null){

            cb_till_date.setVisibility(View.GONE);
        }

        b_ok.setOnClickListener(this);
        b_can.setOnClickListener(this);
        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.date_picker_to));
        dialog.setCanceledOnTouchOutside(false);

        return view;
    }
}
