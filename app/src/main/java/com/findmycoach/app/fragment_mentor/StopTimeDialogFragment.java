package com.findmycoach.app.fragment_mentor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.AddNewSlotActivity;

import java.util.Calendar;

/**
 * Created by praka_000 on 3/2/2015.
 */
public class StopTimeDialogFragment extends DialogFragment implements View.OnClickListener {
    public static TimePicker timePicker;
    Button b_ok, b_can;
    AddNewSlotActivity addNewSlotActivity;
    private int hour;
    private int minute;
    private static final String selected_date = null;

    private static final String TAG = "FMC";


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_ok:
                String hour = String.valueOf(timePicker.getCurrentHour());
                String minute = String.valueOf(timePicker.getCurrentMinute());
                dismiss();
                addNewSlotActivity.setSelectedTillTime(hour, minute);
                break;
            case R.id.b_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addNewSlotActivity = new AddNewSlotActivity();
        String from = getArguments().getString("from");
        if (from.equals("AddNewSlotActivity")) {
            final Calendar c = Calendar.getInstance();
            hour = Integer.parseInt(getArguments().getString("hour"));
            minute = Integer.parseInt(getArguments().getString("minute"));
        } else {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

/*

        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

*/


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    timePicker.setCurrentHour(hour);
                    timePicker.setCurrentMinute(minute);
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_time_fragment, container, false);
        timePicker = (TimePicker) view.findViewById(R.id.slottimePicker);
        b_ok = (Button) view.findViewById(R.id.b_ok);
        b_can = (Button) view.findViewById(R.id.b_cancel);
        b_ok.setOnClickListener(this);
        b_can.setOnClickListener(this);
        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.set_time));
        dialog.setCanceledOnTouchOutside(false);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minuteL) {
                if (minuteL != minute) {
                    timePicker.setCurrentMinute(minute);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
