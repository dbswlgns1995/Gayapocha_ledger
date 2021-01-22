package com.example.gayapocha_ledger.expend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gayapocha_ledger.R;
import com.example.gayapocha_ledger.ledger.IncomeItem;

import io.realm.Realm;

public class AddActivity extends AppCompatActivity {

    TextView title_text;
    EditText title_edit;
    Button save_btn;
    Realm realm;
    String main_title;
    int type;   // getintent type 대분류 0 , 소분류 1

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Realm.init(this); //Realm.init(getContext());
        realm = Realm.getDefaultInstance();

        title_text = findViewById(R.id.add_title_text);
        title_edit = findViewById(R.id.add_title_edit);
        save_btn = findViewById(R.id.add_save_btn);

        Intent intent = getIntent();
        type = intent.getIntExtra("type",0);
        main_title = intent.getStringExtra("mainclass");


        if(type == 0){ // 대분류
            title_text.setText("대분류 추가");

        }else{ // 소분류
            title_text.setText("소분류 추가");
        }

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title_edit.getText().toString().equals("")){
                    Toast.makeText(AddActivity.this, "분류명을 추가해 주세요!", Toast.LENGTH_SHORT).show();
                }else{
                    if(type == 0){ // 대분류
                        if (realm.where(MainExpendItem.class).equalTo("title", title_edit.getText().toString()).count() == 0) {
                            MainExpendItem mainClass = new MainExpendItem(getMainId() ,title_edit.getText().toString());
                            realm.beginTransaction();
                            MainExpendItem realm_main = realm.copyToRealm(mainClass);
                            realm.commitTransaction();
                            Log.d("AddActivity", "지출 대분류 " + title_edit.getText().toString() + "추가");
                            finish();
                        } else {
                            Toast.makeText(AddActivity.this, "이미 사용중인 대분류 이름 입니다!", Toast.LENGTH_LONG).show();
                        }

                    }else{ // 소분류
                        if (realm.where(SubExpendItem.class).equalTo("sub_title", title_edit.getText().toString()).count() == 0) {
                            SubExpendItem subClass = new SubExpendItem(getSubId(main_title), main_title,  title_edit.getText().toString());
                            realm.beginTransaction();
                            SubExpendItem realm_sub = realm.copyToRealm(subClass);
                            realm.commitTransaction();
                            Log.d("AddActivity", "지출 소분류 " + title_edit.getText().toString() + "추가");
                            finish();
                        } else {
                            Toast.makeText(AddActivity.this, "이미 사용중인 소분류 이름 입니다!", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }
        });



    }

    private int getMainId() {
        //자동으로 Id를 증가시켜야 되기 때문에
        //이 메소드가 불린 시점에서 Realm에 저장되어있는 Diary의 ID의 최대값을 구해옵니다.
        Number currentId = realm.where(MainExpendItem.class).max("id");

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

    private int getSubId(String title) {
        //자동으로 Id를 증가시켜야 되기 때문에
        //이 메소드가 불린 시점에서 Realm에 저장되어있는 Diary의 ID의 최대값을 구해옵니다.
        Number currentId = realm.where(SubExpendItem.class).equalTo("main_title", title).max("id");

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
