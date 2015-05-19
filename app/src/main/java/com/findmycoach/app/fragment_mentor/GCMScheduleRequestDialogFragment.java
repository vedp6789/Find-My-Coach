package com.findmycoach.app.fragment_mentor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Request;
import com.findmycoach.app.R;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

/**
 * Created by ved on 1/5/15.
 */
public class GCMScheduleRequestDialogFragment extends DialogFragment{
EditText et_message;
    Button b_accept,b_reject;
    String slot_type;
    String event_id;
    String student_id;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        Bundle bundle=this.getArguments();
        slot_type=bundle.getString("slot_type");
        event_id=bundle.getString("event_id");
        student_id=bundle.getString("student_id");


        Log.d("FMC","slot_type : "+slot_type+" event_id : "+event_id+"student_id : "+student_id);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_gcm_schedule_request, container,false);
        et_message= (EditText) view.findViewById(R.id.et_mentor_message);
        b_accept= (Button) view.findViewById(R.id.b_accept);
        b_reject= (Button) view.findViewById(R.id.b_reject);

        b_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 RequestParams requestParams=new RequestParams();
                requestParams.add("slot_type",slot_type);
                requestParams.add("event_id",event_id);
                requestParams.add("student_id",student_id);

                if(et_message.getText().toString() != null){
                    requestParams.add("message",et_message.getText().toString());
                }else{
                    requestParams.add("message","");
                }
                requestParams.add("response","true");
                progressDialog.show();
                NetworkClient.finalizeEvent(getActivity(),requestParams,new Callback() {
                    @Override
                    public void successOperation(Object object, int statusCode, int calledApiValue) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),(String)object,Toast.LENGTH_SHORT).show();
                        dismiss();
                    }

                    @Override
                    public void failureOperation(Object object, int statusCode, int calledApiValue) {
                        Toast.makeText(getActivity(),(String)object,Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        dismiss();
                    }
                },49);

            }
        });

        b_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_message.getText().toString().trim().length() <= 0){
                    Toast.makeText(getActivity(),getResources().getString(R.string.response_message),Toast.LENGTH_SHORT).show();
                }else{
                    RequestParams requestParams=new RequestParams();
                    requestParams.add("slot_type",slot_type);
                    requestParams.add("event_id",event_id);
                    requestParams.add("student_id",student_id);
                    if(et_message.getText().toString() != null){
                        requestParams.add("message",et_message.getText().toString());
                    }else{
                        requestParams.add("message","");
                    }
                    requestParams.add("response","false");
                    NetworkClient.finalizeEvent(getActivity(),requestParams,new Callback() {
                        @Override
                        public void successOperation(Object object, int statusCode, int calledApiValue) {

                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),(String)object,Toast.LENGTH_SHORT).show();
                            dismiss();
                        }

                        @Override
                        public void failureOperation(Object object, int statusCode, int calledApiValue) {
                            progressDialog.dismiss();
                            dismiss();
                            Toast.makeText(getActivity(),(String)object,Toast.LENGTH_SHORT).show();
                        }
                    },49);

                }
            }
        });


        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.new_schedule_req));
        dialog.setCanceledOnTouchOutside(false);

        return view;
    }



}
