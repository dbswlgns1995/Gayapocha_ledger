package com.example.gayapocha_ledger.ledger;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.gayapocha_ledger.R;

import io.realm.Realm;

// 카드
public class FragCard extends FragParent {

    String date;
    private String TAG = "***cardFragment";
    private int TYPE = 1;

    Button[] m_btn, p_btn;
    EditText[] l_edit;
    int i;
    CheckBox checkBox;
    int check;

    Realm realm;

    public static FragCard newInstance() {
        FragCard fragcard = new FragCard();
        return fragcard;
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
