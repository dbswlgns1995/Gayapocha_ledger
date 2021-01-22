package com.example.gayapocha_ledger.calendar;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gayapocha_ledger.R;
import com.example.gayapocha_ledger.expend.ExpendItem;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class CalculateExpendAdapter extends RealmRecyclerViewAdapter<ExpendItem, CalculateExpendAdapter.ItemViewHolder> {

    public CalculateExpendAdapter(RealmResults<ExpendItem> realmResults, boolean autoUpdate) {
        super(realmResults, autoUpdate);
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.calculate_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        ExpendItem expendItem = getItem(position);
        holder.cal_item_title.setText(expendItem.getSub_title());
        holder.cal_item_cost.setText("-"+CalculateFragment.toNumFormat(expendItem.getCost()));
        holder.cal_item_cost.setTextColor(Color.RED);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView cal_item_title, cal_item_cost;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            cal_item_title  = itemView.findViewById(R.id.cal_item_title);
            cal_item_cost = itemView.findViewById(R.id.cal_item_cost);
        }
    }
}
