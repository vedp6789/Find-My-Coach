package com.findmycoach.app.views;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.DobAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by ShekharKG on 11/6/15.
 */
public class DobPicker {

    private Context context;
    private TextView textView;
    private Dialog dialog;
    private GridView gridView;
    private TextView title;
    private ImageView backButton;
    private int currentYear;
    private List<String> months;

    public String selectedDecade, selectedYear, selectedMonth, selectedDay;

    public DobPicker(Context context, TextView textView, int currentYear) {
        this.context = context;
        this.textView = textView;
        this.currentYear = currentYear - 1;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dob_dialog);
        gridView = (GridView) dialog.findViewById(R.id.dobGrid);
        title = (TextView) dialog.findViewById(R.id.title);
        backButton = (ImageView) dialog.findViewById(R.id.backButton);
        months = new ArrayList<>();
        Collections.addAll(months, context.getResources().getStringArray(R.array.months));
        showDecades();
    }

    private void showDecades() {
        backButton.setVisibility(View.INVISIBLE);
        title.setText(context.getString(R.string.prompt_date_of_birth));
        final List<String> list = new ArrayList<>();
        Collections.addAll(list, context.getResources().getStringArray(R.array.decade_array));
        gridView.setNumColumns(3);
        gridView.setAdapter(new DobAdapter(1, context, list, this));
        dialog.show();
    }

    public void showYearsInDecade() {
        Log.e("Decade", selectedDecade);
        String[] slittedDecade = selectedDecade.split("-");
        int startYear = Integer.parseInt(slittedDecade[0]);
        int lastYear = startYear + 9;
        if (lastYear > currentYear) {
            lastYear = currentYear;
        }

        backButton.setVisibility(View.VISIBLE);
        title.setText(startYear + " - " + lastYear);
        gridView.setNumColumns(3);

        final List<String> list = new ArrayList<>();
        for (int i = startYear; i <= lastYear; i++) {
            list.add(String.valueOf(i));
        }
        gridView.setAdapter(new DobAdapter(2, context, list, this));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDecades();
            }
        });
    }

    public void showMonths() {
        Log.d("Year", selectedYear);
        title.setText(selectedYear);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYearsInDecade();
            }
        });

        gridView.setNumColumns(3);
        gridView.setAdapter(new DobAdapter(3, context, months, this));
    }

    public void showDates() {
        Log.d("Year", selectedMonth);
        title.setText(selectedYear + " - " + selectedMonth);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMonths();
            }
        });
        int year = Integer.parseInt(selectedYear);
        int month = months.indexOf(selectedMonth);
        Calendar myCal = new GregorianCalendar(year, month, 1);
        int daysInMonth = myCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < daysInMonth; i++)
            list.add((i + 1) + "");

        gridView.setNumColumns(7);
        gridView.setAdapter(new DobAdapter(4, context, list, this));
        Log.e("Date", year + " : " + month + " : " + daysInMonth);

    }

    public void setDob() {
        textView.setText(selectedDay + "-" +  selectedMonth.charAt(0) + selectedMonth.charAt(1)
                + selectedMonth.charAt(2) + "-" + selectedYear);
        dialog.dismiss();
    }
}
