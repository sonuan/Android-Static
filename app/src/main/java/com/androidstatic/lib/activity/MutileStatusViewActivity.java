package com.androidstatic.lib.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidstatic.lib.R;

public class MutileStatusViewActivity extends AppCompatActivity {


    public static void toActivity(Context context) {
        Intent intent = new Intent(context, MutileStatusViewActivity.class);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutile_status_view);
    }
}
