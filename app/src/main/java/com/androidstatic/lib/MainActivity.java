package com.androidstatic.lib;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.androidstatic.lib.utils.DeviceUtils;
import com.androidstatic.lib.utils.DisplayUtil;
import com.androidstatic.lib.utils.L;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public class Image{
        private int width;

        public Image(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        @Override
        public String toString() {
            return "Image{" +
                    "width=" + width +
                    '}';
        }
    }

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mTextView = (TextView) findViewById(R.id.tv_text);
        mTextView.setOnClickListener(this);
        text();
        List<Image> list = new ArrayList<>();
        list.add(new Image(1));
        list.add(new Image(2));
        list.add(new Image(3));
        list.add(new Image(4));
        list.add(new Image(5));
//        L.i("mmmmmm");
        L.i("mmmmmmmmm", list);
    }

    private void text() {
        StringBuilder sb = new StringBuilder();
        sb.append(DisplayUtil.getScreenRealHeight(this) + "\n")
                .append(DisplayUtil.getScreenHeight(this) + "\n")
                .append(DisplayUtil.getStatusBarHeight(this) + "\n")
                .append(DisplayUtil.getNavigationBarHeight(this) + "\n")
                .append(DisplayUtil.getAppHeight(this) + "\n")
                .append(DisplayUtil.getKeyboardHeight(this) + "\n")
                .append(DeviceUtils.getAndroidID(this) + "\n")
                .append(DeviceUtils.getUUID(this) + "\n")
        ;
        mTextView.setText(sb.toString());
        sb.reverse();
    }

    @Override
    public void onClick(View v) {
        text();
    }
}
