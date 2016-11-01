package com.androidstatic.lib.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidstatic.lib.R;
import com.androidstatic.lib.http.CustomRequest;
import com.androidstatic.lib.http.HttpClientRequest;
import com.androidstatic.lib.http.params.HttpParams;
import com.androidstatic.lib.utils.DeviceUtils;
import com.androidstatic.lib.utils.DisplayUtil;
import com.androidstatic.lib.utils.L;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    public class Image {
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
        setContentView(R.layout.activity_main2);
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
        //        text();
        for (int i = 0; i < 50; i++) {
            getDemoData("Android", "10", new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    L.i(response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }, "");
        }

    }

    public static final String POST_URL = "http://v.juhe.cn/postcode/query";

    public static final String GET_URL = "http://gank.io/api/search/query/listview/category/Android/count/1/page/1";
    /**
     * 使用和参数配置范例
     *
     * @param param1
     * @param param2
     * @param listener
     * @param errorListener
     */
    public void getDemoData(String param1,
                            String param2,
                            Response.Listener listener,
                            Response.ErrorListener errorListener, String tag) {
        HttpParams params = new HttpParams();
        params.put("postcode", "215001");
        params.put("key", "215001");

        CustomRequest request = new CustomRequest.RequestBuilder()
                .method(HttpParams.HttpMethod.POST)
//                                .post()//不设置的话默认GET 但是设置了参数就不需要了。。。
                .url(POST_URL)
                //url会统一配置到requestUrl类中
//                .addMethodParams("") //请求的方法名
                // 添加参数方法1 适用参数比较多的情况下
                                .params(params)
                // 添加参数方法2
//                .addParams("category", param1)//添加参数1
                //                .addParams("count", param2)//添加参数2
                //                .clazz(Test.calss) //如果设置了返回类型，会自动解析返回model 如果不设置会直接返回json数据;
                .successListener(listener)//获取数据成功的listener
                .errorListener(errorListener)//获取数据异常的listener
                .build();
        HttpClientRequest.getInstance(this).addRequest(request, tag);
        //将请求add到队列中。并设置tag  并需要相应activity onStop方法中调用cancel方法
    }
}
