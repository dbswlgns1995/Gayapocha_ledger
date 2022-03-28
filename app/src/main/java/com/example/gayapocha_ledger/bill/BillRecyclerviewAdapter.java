package com.example.gayapocha_ledger.bill;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gayapocha_ledger.R;
import com.example.gayapocha_ledger.ledger.IncomeItem;
import com.example.gayapocha_ledger.ledger.IncomeList;
import com.example.gayapocha_ledger.util.NewBill;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

public class BillRecyclerviewAdapter extends RealmRecyclerViewAdapter<IncomeItem, BillRecyclerviewAdapter.ItemViewHolder> {

    // 미니빌지 recyclerview adapter

    // title
    private static final String[] income_title = {"수구레국밥", "선지국밥", "소고기국밥", "공기밥", "계란추가", "수구레무침", "수구레볶음", "오삼불고기",
            "오돌뼈", "닭갈비", "돼지석쇠", "술국", "부추전", "소주", "맥주", "생탁", "음료수"
    };


    // cost
    private static final int[] income_cost = {7000, 6000, 6000, 1000, 2000, 20000, 20000, 15000, 15000, 15000, 15000, 6000, 6000, 4000, 4000, 3500, 1500};


    private Realm realm;
    Context context;
    private String TAG = "***BillRecyclerviewAdapter";
    IncomeItem incomeItem;

    public BillRecyclerviewAdapter(@Nullable OrderedRealmCollection<IncomeItem> data, boolean autoUpdate) {
        super(data, autoUpdate);
    }


    @NonNull
    @Override
    public BillRecyclerviewAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        realm = Realm.getDefaultInstance();
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.small_bill, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BillRecyclerviewAdapter.ItemViewHolder holder, int position) {

        int result = 0;


        incomeItem = getItem(position);

        realm = Realm.getDefaultInstance();
        holder.id_text.setText(incomeItem.getId()+"");
        RealmList<IncomeList> realmList = incomeItem.getIncomeLists();
        RealmResults<IncomeList> listRealmResults = realmList.where().equalTo("id", incomeItem.getId()).sort("count", Sort.DESCENDING).findAll();

        int date = incomeItem.getDate();
        int id = incomeItem.getId();

        ArrayList mNewList = new ArrayList(Arrays.asList(income_title));
        // 3순위 까지 저장용 arraylist
        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<Integer> countList = new ArrayList<>();

        for (IncomeList i : listRealmResults){
            titleList.add(i.getTitle());
            countList.add(i.getCount());
            int index = mNewList.indexOf(i.getTitle());

            if (date >= 20220328){
                result += (NewBill.new_income_list[index] * i.getCount());
            } else {
                result += (income_cost[index] * i.getCount()); // 1회분 빌지 합계 result text 에 저장
            }


        }

        // 3순위 까지 저장, 없으면 공백
        holder.title1_text.setText(titleList.get(0));
        holder.count1_text.setText(countList.get(0)+"");

        if(listRealmResults.size()>1){
            if(titleList.get(1) != null){
                holder.title2_text.setText(titleList.get(1));
                holder.count2_text.setText(countList.get(1)+"");
            }
        }

        if(listRealmResults.size()>2){
            if(titleList.get(2) != null){
                holder.title3_text.setText(titleList.get(2));
                holder.count3_text.setText(countList.get(2)+"");
            }
        }



        holder.result_text.setText("합계  :  " + result);

        String type="";
        String packing="";

        switch (incomeItem.getType()){
            case 0:
                type = "현금";
                break;
            case 1:
                type = "카드";
                break;
            case 2:
                type = "미수금";
                break;
        }

        switch (incomeItem.getPacking()){
            case 0:
                packing = "식사";
                break;
            case 1:
                packing = "포장";
                break;
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //빌지 수정으로 넘어가기
                Intent intent = new Intent(view.getContext(), BillUpdateActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("id", id);
                view.getContext().startActivity(intent);


            }
        });

        holder.type_text.setText(type + " " + packing);



        // 미니 빌지 삭제, db data 삭제 id, date로 접근
        holder.remove_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("삭제하시겠습니까?").setMessage("기록들도 함께 삭제됩니다.");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                realm = Realm.getDefaultInstance();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmResults<IncomeItem> results = realm.where(IncomeItem.class).equalTo("id", id).equalTo("date", date).findAll();
                                        results.deleteAllFromRealm();
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

        TextView id_text;
        TextView title1_text, title2_text, title3_text;
        TextView count1_text, count2_text, count3_text;
        TextView result_text, type_text;
        LinearLayout linearLayout;

        ImageView remove_imageview;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.sb_linearlayout);
            remove_imageview = itemView.findViewById(R.id.remove_imageview);
            id_text = itemView.findViewById(R.id.sb_id_text);
            title1_text = itemView.findViewById(R.id.sb1_title_text);
            title2_text = itemView.findViewById(R.id.sb2_title_text);
            title3_text = itemView.findViewById(R.id.sb3_title_text);
            count1_text = itemView.findViewById(R.id.sb1_count_text);
            count2_text = itemView.findViewById(R.id.sb2_count_text);
            count3_text = itemView.findViewById(R.id.sb3_count_text);
            result_text = itemView.findViewById(R.id.sb_result_text);
            type_text = itemView.findViewById(R.id.sb_type_text);
        }
    }
}
