package com.example.gayapocha_ledger.bill;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.gayapocha_ledger.R;
import com.example.gayapocha_ledger.ledger.MainActivity;

import java.util.List;

public class BillUpdateActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbar_textview;
    private FrameLayout frameLayout;
    int id, date;



    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_update);


        toolbar_textview = findViewById(R.id.bill_update_toolbar_title);
        FragBillUpdate fragBillUpdate = new FragBillUpdate();

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        date = intent.getIntExtra("date", 0);

        // fragbillupdate에 id date data passing
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putInt("date", date);
        fragBillUpdate.setArguments(bundle);

        // 액션바 설정
        toolbar = findViewById(R.id.bill_update_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기

        // 뒤로가기 버튼 크기 조절
        Drawable drawable= getResources().getDrawable(R.drawable.back);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 120, 120, true));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(newdrawable);

        toolbar_textview.setText(date + " "+ id + "번 영수증");

        getSupportFragmentManager().beginTransaction().add(R.id.bill_update_framelayout, fragBillUpdate).commit();



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: // 왼쪽 상단 버튼 눌렀을 때
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}