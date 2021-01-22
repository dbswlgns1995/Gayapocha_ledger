package com.example.gayapocha_ledger.ledger;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.gayapocha_ledger.R;

import io.realm.Realm;
import io.realm.RealmResults;

// 현금
public class FragCash extends FragParent {

    String date;
    private String TAG = "***cashFragment";
    private int TYPE = 0;

    Realm realm;
    CheckBox checkBox;
    int check;



    public static FragCash newInstance() {
        FragCash fragcash = new FragCash();
        return fragcash;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bill, container, false);


        MainActivity main_activity = (MainActivity)getActivity();
        date = main_activity.sendData();

        checkBox = view.findViewById(R.id.takeout_checkbox);

        init(view);
        setting();


        realm = Realm.getDefaultInstance();
        RealmResults<IncomeItem> realmResults = realm.where(IncomeItem.class).equalTo("date", Integer.parseInt(date)).findAll();
        if(realmResults!=null){
            Log.d(TAG, realmResults.toString());
        }


        // realm db에 수입(카드) data 저장
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