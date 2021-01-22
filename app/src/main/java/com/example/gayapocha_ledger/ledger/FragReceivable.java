package com.example.gayapocha_ledger.ledger;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.gayapocha_ledger.R;

import java.util.List;

import io.realm.Realm;

// 미수금
public class FragReceivable extends FragParent {

    String date;
    private String TAG = "***receivableFragment";
    private int TYPE = 2;
    Realm realm;

    CheckBox checkBox;
    int check;



    public static FragReceivable newInstance() {
        FragReceivable fragReceivable = new FragReceivable();
        return fragReceivable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bill, container, false);


        MainActivity main_activity = (MainActivity) getActivity();
        date = main_activity.sendData();

        checkBox = view.findViewById(R.id.takeout_checkbox);

        init(view);
        setting();


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = main_activity.sendData();
                Log.d(TAG, date);
                if (checkBox.isChecked()){
                    check = 1;
                }else{
                    check = 0;
                }
                save(TYPE, Integer.parseInt(date), check);
            }
        });

        return view;
    }





}
