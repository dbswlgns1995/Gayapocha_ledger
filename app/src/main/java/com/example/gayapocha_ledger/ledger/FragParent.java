package com.example.gayapocha_ledger.ledger;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gayapocha_ledger.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;


public class FragParent extends Fragment { // 17개 메뉴 하드코딩

    private Realm realm;
    private String TAG = "***fragparent";

    private int ischecked; // 0 non packing, 1 packing
    private static CheckBox takeout_checkbox;
    IncomeList incomeList;

    // -1 버튼
    private List<Button> m_btn;
    private static final int[] m_btn_id = {
            (R.id.a1_m_btn), (R.id.a2_m_btn), (R.id.a3_m_btn), (R.id.a4_m_btn), (R.id.a5_m_btn),
            (R.id.b1_m_btn), (R.id.b2_m_btn), (R.id.b3_m_btn), (R.id.b4_m_btn), (R.id.b5_m_btn), (R.id.b6_m_btn), (R.id.b7_m_btn), (R.id.b8_m_btn),
            (R.id.c1_m_btn), (R.id.c2_m_btn), (R.id.c3_m_btn), (R.id.c4_m_btn)
    };

    // +1 버튼
    private List<Button> p_btn;
    private static final int[] p_btn_id = {
            (R.id.a1_p_btn), (R.id.a2_p_btn), (R.id.a3_p_btn), (R.id.a4_p_btn), (R.id.a5_p_btn),
            (R.id.b1_p_btn), (R.id.b2_p_btn), (R.id.b3_p_btn), (R.id.b4_p_btn), (R.id.b5_p_btn), (R.id.b6_p_btn), (R.id.b7_p_btn), (R.id.b8_p_btn),
            (R.id.c1_p_btn), (R.id.c2_p_btn), (R.id.c3_p_btn), (R.id.c4_p_btn)
    };

    // 갯수 edittext
    private List<EditText> l_edit;
    private static final int[] l_edit_id = {
            (R.id.a1_edit), (R.id.a2_edit), (R.id.a3_edit), (R.id.a4_edit), (R.id.a5_edit),
            (R.id.b1_edit), (R.id.b2_edit), (R.id.b3_edit), (R.id.b4_edit), (R.id.b5_edit), (R.id.b6_edit), (R.id.b7_edit), (R.id.b8_edit),
            (R.id.c1_edit), (R.id.c2_edit), (R.id.c3_edit), (R.id.c4_edit)
    };

    // title
    private static final String[] income_title = {"수구레국밥", "선지국밥", "소고기국밥", "공기밥", "계란추가", "수구레무침", "수구레볶음", "오삼불고기",
            "오돌뼈", "닭갈비", "돼지석쇠", "술국", "부추전", "소주", "맥주", "생탁", "음료수"
    };


    // cost
    private static final int[] income_cost = {8000, 7000, 7000, 1000, 2000, 25000, 25000, 15000, 15000, 15000, 15000, 6000, 6000, 4000, 4000, 3500, 1500};

    Button save_btn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bill, container, false);

        init(view);

        return view;


    }

    // income -,+ btn, edittext 정보 저장
    public void init(View view) {


        realm = Realm.getDefaultInstance();

        m_btn = new ArrayList<>();
        p_btn = new ArrayList<>();
        l_edit = new ArrayList<>();

        takeout_checkbox = view.findViewById(R.id.takeout_checkbox);
        save_btn = view.findViewById(R.id.save_btn);

        // arraylist에 할닫
        for (int i = 0; i < m_btn_id.length; i++) {
            Button m_button = (Button) view.findViewById(m_btn_id[i]);
            m_btn.add(m_button);

            Button p_button = (Button) view.findViewById(p_btn_id[i]);
            p_btn.add(p_button);

            EditText editText = (EditText) view.findViewById(l_edit_id[i]);
            l_edit.add(editText);
        }

        for (int i = 0; i < m_btn.size(); i++) {
            final int j = i;

            // 마이너스 버튼 누르면 edittext -1
            m_btn.get(j).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(l_edit.get(j).getText().toString()) > 0) {
                        l_edit.get(j).setText((Integer.parseInt(l_edit.get(j).getText().toString()) - 1) + "");
                    }
                }
            });

            // 플러스 버튼 누르면 edittext -1
            p_btn.get(j).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    l_edit.get(j).setText((Integer.parseInt(l_edit.get(j).getText().toString()) + 1) + "");

                }
            });
        }


    }


    // edittext 에 0 출력
    public void setting() {
        for (int i = 0; i < l_edit.size(); i++) {
            l_edit.get(i).setText("0");
        }
    }

    // realm db에 income data 저장
    public void save(int type, int date, int check) { // 0 cash, 1 card, 2 receivable
        Log.d(TAG, date + " " + type + " 에 저장할 꼬얌");

        RealmList<IncomeList> incomeListRealmList = new RealmList<>();

        int id = getId(date);

        for (int i = 0; i < l_edit.size(); i++) {
            if (!l_edit.get(i).getText().toString().equals("0")) {
                Log.d(TAG, "incomelist에 저장");
                incomeListRealmList.add(new IncomeList(income_title[i], Integer.parseInt(l_edit.get(i).getText().toString()), id));
            }
        }

        if (incomeListRealmList.size() == 0) {
            Log.d(TAG, "save: 없졍");
            Toast.makeText(getContext(), "정보를 입력하세요!", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "save: "+ id + "번 " + type + "타입 " + date + " 일 " + check + " 체크여부");
            IncomeItem incomeItem = new IncomeItem(id, type, date, check, incomeListRealmList);
            realm.beginTransaction();
            IncomeItem realm_sub = realm.copyToRealm(incomeItem);
            realm.commitTransaction();
            Toast.makeText(getContext(), "저장 완료!", Toast.LENGTH_SHORT).show();
            setting();
        }


    }

    private int getId(int date) {
        //자동으로 Id를 증가시켜야 되기 때문에
        //이 메소드가 불린 시점에서 Realm에 저장되어있는 Diary의 ID의 최대값을 구해옵니다.
        Number currentId = realm.where(IncomeItem.class).equalTo("date", date).max("id");

        //새로 저장할 Diary의 ID값
        int nextId;

        //Realm에 Diary가 저장되어있지 않아 저장된 ID도 없는 경우에는
        if (currentId == null) {
            //처음 생성되는 ID이기 때문에 1을 지정합니다
            nextId = 1;
            //Realm에 Diary가 저장되어있는 경우에는 저장되어 있는 Diary의 최대 ID를 찾아와서
        } else {
            //찾아온 ID에 +1을 해서 돌려줍니다.
            nextId = currentId.intValue() + 1;
        }
        return nextId;
    }


}
