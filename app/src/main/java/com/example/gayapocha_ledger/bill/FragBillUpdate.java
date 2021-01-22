package com.example.gayapocha_ledger.bill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.gayapocha_ledger.R;
import com.example.gayapocha_ledger.ledger.FragParent;
import com.example.gayapocha_ledger.ledger.IncomeItem;
import com.example.gayapocha_ledger.ledger.IncomeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.RealmClass;

public class FragBillUpdate extends Fragment {

    int date, id;
    private String TAG = "***FragBillUpdate";
    CheckBox checkBox;
    Realm realm;
    Button save_btn;

    RealmList<IncomeList> incomeListRealmList;
    IncomeItem incomeItem;

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

    public FragBillUpdate() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bill, container, false);

        bu_init(view);

        id = getArguments().getInt("id");
        date = getArguments().getInt("date");

        Log.d(TAG, "onCreateView: " + id + " " + date);

        edit_init(id, date, view);

        checkBox = view.findViewById(R.id.takeout_checkbox);
        checkBox.setVisibility(View.INVISIBLE);

        save_btn = view.findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bu_save(id, date);
            }
        });




        return view;
    }

    public void bu_init(View view){

        m_btn = new ArrayList<>();
        p_btn = new ArrayList<>();
        l_edit = new ArrayList<>();

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

    private void edit_init(int id, int date, View view){

        realm = Realm.getDefaultInstance();
        incomeItem = realm.where(IncomeItem.class).equalTo("date", date).equalTo("id", id).findFirst();
        RealmResults<IncomeList> listRealmResults = incomeItem.getIncomeLists().where().equalTo("id", incomeItem.getId()).sort("count", Sort.DESCENDING).findAll();

        ArrayList mNewList = new ArrayList(Arrays.asList(income_title));

        if (l_edit!=null){
            Log.d(TAG, l_edit.size()+"사이즈");
        }else{
            Log.d(TAG, "널띠");
        }


        for(IncomeList i : listRealmResults){
            Log.d(TAG, i.getTitle() + " " + i.getCount() + " 저장" );
            int index = mNewList.indexOf(i.getTitle());
            l_edit.get(index).setText(i.getCount()+"");
        }



    }


    // realm db에 income data 수정
    public void bu_save(int id, int date) {

        incomeListRealmList = new RealmList<>();

        int save_date = date;
        int save_id = id;

        for (int i = 0; i < l_edit.size(); i++) {
            if (!l_edit.get(i).getText().toString().equals("0")) {
                Log.d(TAG, income_title[i] + " " + Integer.parseInt(l_edit.get(i).getText().toString()) + " id 저장 ");
                incomeListRealmList.add(new IncomeList(income_title[i], Integer.parseInt(l_edit.get(i).getText().toString()), id));
            }
        }

        Log.d(TAG, incomeListRealmList.size() + " " + incomeListRealmList.toString());


        int type = incomeItem.getType();
        int ischecked = incomeItem.getPacking();


        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                IncomeItem incomeItem = realm.where(IncomeItem.class).equalTo("date", save_date).equalTo("id", save_id).findFirst();
                incomeItem.deleteFromRealm();

                IncomeItem incomeItem1 = new IncomeItem(save_id, type, save_date, ischecked, incomeListRealmList);
                IncomeItem realm_sub = realm.copyToRealm(incomeItem1);

            }
        });
        Toast.makeText(getContext(), "저장완료!", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

}
