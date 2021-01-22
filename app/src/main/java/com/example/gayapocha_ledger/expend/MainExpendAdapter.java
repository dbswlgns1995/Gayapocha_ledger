package com.example.gayapocha_ledger.expend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gayapocha_ledger.R;

import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class MainExpendAdapter extends RealmRecyclerViewAdapter<MainExpendItem, MainExpendAdapter.ItemViewHolder> {

    private Realm realm;
    Context context;
    MainExpendItem mainClass;
    int date;
    private List<PassSubExpendItem> subExpendItemArrayList;
    ExpendItem expendItem;

    public MainExpendAdapter(RealmResults<MainExpendItem> realmResults, boolean autoUpdate, Context context, int date) {
        super(realmResults, autoUpdate);
        this.context = context;
        this.date = date;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Realm.init(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mainclass_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        mainClass = getItem(position);
        final String title = mainClass.getTitle();

        realm = Realm.getDefaultInstance();

        // 소분류 recyclerview setting
        RealmResults<SubExpendItem> realmResults = realm.where(SubExpendItem.class).equalTo("main_title", title).findAll();
        Log.d("**MainClassAdater log", realmResults.toString());
        final SubExpendAdapter subClassExpendAdapter = new SubExpendAdapter(realmResults, true, title, context, date);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(subClassExpendAdapter);
        //holder.recyclerView.addOnItemTouchListener(mScrollTouchListener);
        //

        holder.item_title_text.setText(mainClass.getTitle());

        // add activity 로 intent 하여 소분류 추가
        holder.subitem_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("mainclass", title);
                v.getContext().startActivity(intent);
            }
        });

        // 대분류 제거 하위 소분류도 함께 제거
        holder.item_remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("삭제하시겠습니까?").setMessage("모든 하위 기록들도 함께 삭제됩니다.");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                realm = Realm.getDefaultInstance();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmResults<MainExpendItem> results = realm.where(MainExpendItem.class).equalTo("title", title).findAll();
                                        results.deleteAllFromRealm();
                                        RealmResults<SubExpendItem> results1 = realm.where(SubExpendItem.class).equalTo("main_title", title).findAll();
                                        for(SubExpendItem i : results1){
                                            RealmResults<ExpendItem> results2 = realm.where(ExpendItem.class).equalTo("sub_title", i.getSub_title()).findAll();
                                            results2.deleteAllFromRealm();
                                        }
                                        results1.deleteAllFromRealm();
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

        // subclassadapter에서 textwatcher 받아서 전체 저장
        holder.item_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                subExpendItemArrayList = subClassExpendAdapter.pass_list();
                for (final PassSubExpendItem i : subExpendItemArrayList) {
                    expendItem = realm.where(ExpendItem.class).equalTo("sub_title", i.sub_expend_title).equalTo("date", date).findFirst();
                    if(i.sub_expend_cost != 0){
                        if (expendItem==null){
                            realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    ExpendItem expendItem1 = realm.createObject(ExpendItem.class);
                                    expendItem1.setMain_title(title);
                                    expendItem1.setSub_title(i.sub_expend_title);
                                    expendItem1.setCost(i.sub_expend_cost);
                                    expendItem1.setDate(date);

                                }
                            });

                        }else{ // 수정
                            realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    ExpendItem item = realm.where(ExpendItem.class).equalTo("sub_title", i.sub_expend_title).equalTo("date", date).findFirst();
                                    item.setCost(i.sub_expend_cost);

                                }
                            });


                        }
                        Toast.makeText(v.getContext(), "저장완료!", Toast.LENGTH_SHORT).show();

                    }else{
                        if (expendItem!=null){
                            realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    ExpendItem item = realm.where(ExpendItem.class).equalTo("sub_title", i.sub_expend_title).equalTo("date", date).findFirst();
                                    item.deleteFromRealm();

                                }
                            });
                        }
                    }
                }


            }
        });

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView item_title_text;
        Button item_remove_btn, subitem_add_btn,item_save_btn;
        RecyclerView recyclerView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            subitem_add_btn = itemView.findViewById(R.id.mainclass_subitem_add_btn);
            item_title_text = itemView.findViewById(R.id.mainclass_item_title_text);
            item_remove_btn = itemView.findViewById(R.id.mainclass_item_remove_btn);
            recyclerView = itemView.findViewById(R.id.mainclass_item_recyclerview);
            item_save_btn = itemView.findViewById(R.id.mainclass_item_save_btn);
        }
    }
}
