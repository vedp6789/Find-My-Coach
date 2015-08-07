package com.findmycoach.app.util;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.AddNewSlotActivity;
import com.findmycoach.app.adapter.GradeAdapter;
import com.findmycoach.app.beans.search.SearchResponse;
import com.findmycoach.app.beans.student.ChildDetails;
import com.findmycoach.app.beans.student.Grade;
import com.findmycoach.app.fragment.MyScheduleFragment;
import com.findmycoach.app.views.ChizzleAutoCompleteTextView;
import com.findmycoach.app.views.ChizzleTextView;
import com.findmycoach.app.views.DobPicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by abhi7 on 24/07/15.
 */
public class ChildDetailsDialog implements Callback {

    private Context context;
    private Dialog dialog;
    private Spinner genderSpinner, gradeSpinner;
    private Button doneButton;
    private Button cancelButton;
    private ChildDetails childDetails;
    private ChizzleAutoCompleteTextView childName;
    private ChildDetailsAddedListener mChildDetailsAddedListener;
    private ArrayList<Grade> gradeArrayList;
    private GradeAdapter gradeAdapter;

    public ChildDetailsDialog(Context context) {
        this.context = context;
        gradeArrayList = new ArrayList<>();


    }

    public void setChildAddedListener(ChildDetailsAddedListener childDetailsAddedListener) {
        mChildDetailsAddedListener = childDetailsAddedListener;
    }

    public void onChildDetailsAdded(ChildDetails childDetails) {
        mChildDetailsAddedListener.onChildDetailsAdded(childDetails);
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                ((TextView) v).setError(null);
        }
    };

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ((TextView) v).setError(null);
            return false;
        }
    };

    public void showPopUp() {
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.child_details_layout);
        genderSpinner = (Spinner) dialog.findViewById(R.id.childGender);
        doneButton = (Button) dialog.findViewById(R.id.done);
        childName = (ChizzleAutoCompleteTextView) dialog.findViewById(R.id.childName);
        cancelButton = (Button) dialog.findViewById(R.id.cancel);
        gradeSpinner = (Spinner) dialog.findViewById(R.id.gradeSpinner);
        getGrades();
        final TextView childDOB = (TextView) dialog.findViewById(R.id.childDOB);
        childDOB.setOnTouchListener(onTouchListener);
        childDOB.setOnFocusChangeListener(onFocusChangeListener);
        childDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DobPicker(context, childDOB,
                        context.getResources().getInteger(R.integer.starting_year_child),
                        Calendar.getInstance().get(Calendar.YEAR) - context.getResources().getInteger(R.integer.mentee_min_age_child));
            }
        });

        genderSpinner.setAdapter(new ArrayAdapter<String>(context, R.layout.textview, context.getResources().getStringArray(R.array.gender)));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    childDetails = new ChildDetails();
                    String sex = genderSpinner.getSelectedItem().toString();
                    // TODO
                    if (sex.equals("Male"))
                        childDetails.setGender("M");
                    else
                        childDetails.setGender("F");

                    childDetails.setName(childName.getText().toString());
                    childDetails.setGradeId(gradeArrayList.get(gradeSpinner.getSelectedItemPosition()).getId());
                    childDetails.setGrade(gradeArrayList.get(gradeSpinner.getSelectedItemPosition()).getGrade());
                    childDetails.setDob(childDOB.getText().toString());
                    onChildDetailsAdded(childDetails);
                    dialog.dismiss();
                }


            }

            private boolean validate() {
                boolean is_valid = true;
                if (childName != null && childName.getText().toString().trim().length() < 1) {
                    is_valid = false;
                    childName.setError(context.getResources().getString(R.string.error_field_required));
                }

                if (childDOB.getText().toString().trim().length() < 1) {
                    is_valid = false;
                    childDOB.setError(context.getResources().getString(R.string.error_field_required));
                }
                return is_valid;
            }
        });
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    private void getGrades() {
        if (NetworkManager.isNetworkConnected(context)) {
            NetworkClient.getGrades(context, StorageHelper.getUserDetails(context, "auth_token"), this, 56);
        } else
            Toast.makeText(context, context.getString(R.string.check_network_connection), Toast.LENGTH_LONG).show();


    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        String response = (String) object;
        try {
            JSONArray jsonArray = new JSONArray(response);
            gradeArrayList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<Grade>>() {
            }.getType());
            Log.d("ArrayList", gradeArrayList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gradeAdapter = new GradeAdapter(context, R.layout.textview, gradeArrayList);
        gradeSpinner.setAdapter(gradeAdapter);

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {

    }

    public interface ChildDetailsAddedListener {
        public void onChildDetailsAdded(ChildDetails childDetails);
    }


}
