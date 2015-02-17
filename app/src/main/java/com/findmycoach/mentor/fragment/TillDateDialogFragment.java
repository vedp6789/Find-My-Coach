package com.findmycoach.mentor.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.findmycoach.mentor.activity.AddNewSlotActivity;
import com.fmc.mentor.findmycoach.R;

/**
 * Created by praka_000 on 2/17/2015.
 */
public class TillDateDialogFragment extends DialogFragment implements View.OnClickListener {
    DatePicker datePicker;
    Button b_ok,b_can;
    AddNewSlotActivity addNewSlotActivity;

    private static final String selected_date=null;


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.b_ok:
                String day=String.valueOf(datePicker.getDayOfMonth());
                String month=String.valueOf(datePicker.getMonth()+1);
                String year=String.valueOf(datePicker.getYear());
                dismiss();

                addNewSlotActivity.setSelectedTillDate(day,month,year);
                break;
            case R.id.b_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addNewSlotActivity=new AddNewSlotActivity();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_date_fragment,container,false);
        datePicker= (DatePicker) view.findViewById(R.id.slotdatePicker);
        b_ok= (Button) view.findViewById(R.id.b_ok);
        b_can= (Button) view.findViewById(R.id.b_cancel);

        b_ok.setOnClickListener(this);
        b_can.setOnClickListener(this);
        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.date_picker));
        dialog.setCanceledOnTouchOutside(false);

        return view;
    }
}
