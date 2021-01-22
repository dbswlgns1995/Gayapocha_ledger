package com.example.gayapocha_ledger.expend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gayapocha_ledger.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

class PassSubExpendItem {
    public String main_expend_title, sub_expend_title;
    public int sub_expend_cost;

    public PassSubExpendItem(String main_expend_title, String sub_expend_title, int sub_expend_cost) {
        this.main_expend_title = main_expend_title;
        this.sub_expend_title = sub_expend_title;
        this.sub_expend_cost = sub_expend_cost;
    }
}

public class SubExpendAdapter extends RealmRecyclerViewAdapter<SubExpendItem, SubExpendAdapter.ItemViewHolder> {

    private Realm realm;
    String main_title;
    Context context;
    ExpendItem expendItem;
    String TAG = "****SubClassExpendAdapter****";
    int cost, date;

    private List<PassSubExpendItem> subExpendItemArrayList;
    RealmResults<SubExpendItem> realmResults;

    public SubExpendAdapter(RealmResults<SubExpendItem> realmResults, boolean autoUpdate, String main_title , Context context, int date) {
        super(realmResults, autoUpdate);
        this.main_title = main_title;
        this.context = context;
        this.realmResults = realmResults;
        this.date = date;
    }

    @NonNull
    @Override
    public SubExpendAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        subExpendItemArrayList = new ArrayList<PassSubExpendItem>(realmResults.size());
        for(SubExpendItem i : realmResults){
            subExpendItemArrayList.add(new PassSubExpendItem("","", 0));
        }
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.subclass_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubExpendAdapter.ItemViewHolder holder, int position) {
        final SubExpendItem subClass = getItem(position);
        final String sub_title = subClass.getSub_title();

        realm = Realm.getDefaultInstance();
        final ExpendItem realm_expend_item = realm.where(ExpendItem.class).equalTo("sub_title", sub_title).equalTo("date", date).findFirst();

        subExpendItemArrayList.get(position).sub_expend_title = sub_title;

        holder.subclass_item_title_text.setText(sub_title);

        // edittext에 해당날짜의 cost를 settext
        if(realm_expend_item != null) {
            cost = realm_expend_item.getCost();
            holder.subclass_item_edit.setText(String.valueOf(cost));
            //Log.d(TAG, realm_expend_item.toString());
            subExpendItemArrayList.get(position).sub_expend_cost = cost;

        }

        // edittext 에서 cost를 입력받아 mainadapter로 data passing
        holder.subclass_item_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    subExpendItemArrayList.get(position).sub_expend_cost = Integer.parseInt(s.toString());
                }
                subExpendItemArrayList.get(position).sub_expend_title = sub_title;
                Log.d(TAG, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // subclass 와 그 하위 기록들을 모두제거
        holder.subclass_remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("삭제하시겠습니까?").setMessage("모든 기록들도 함께 삭제됩니다.");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                realm = Realm.getDefaultInstance();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmResults<SubExpendItem> results = realm.where(SubExpendItem.class).equalTo("sub_title", sub_title).findAll();
                                        results.deleteAllFromRealm();
                                        RealmResults<ExpendItem> expend_results = realm.where(ExpendItem.class).equalTo("sub_title", sub_title).findAll();
                                        expend_results.deleteAllFromRealm();
                                    }
                                });

                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();

            }
        });

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        Button subclass_remove_btn;
        TextView subclass_item_title_text;
        EditText subclass_item_edit;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            subclass_remove_btn = itemView.findViewById(R.id.subclass_item_remove_btn);
            subclass_item_title_text = itemView.findViewById(R.id.subclass_item_title_text);
            subclass_item_edit = itemView.findViewById(R.id.subclass_item_edit);
        }
    }

    private class MyCustomEditTextListener implements TextWatcher{
        private int position;

        public void updatePosition(int position){
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public List<PassSubExpendItem> pass_list(){
        return subExpendItemArrayList;

    }
}
