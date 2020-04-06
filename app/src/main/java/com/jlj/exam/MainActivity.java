package com.jlj.exam;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jlj.exam.entity.ResultEntity;
import com.jlj.exam.http.Http;
import com.jlj.exam.http.HttpCallback;
import com.jlj.exam.view.ChartView;

public class MainActivity extends Activity {

    private ChartView chartView = null;
    private ProgressBar loading = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chartView = findViewById(R.id.chartView);
        loading = findViewById(R.id.loading);

        findViewById(R.id.load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
    }

    private void fillData(ResultEntity resultEntity){
        chartView.fill(resultEntity);
    }

    private void loading(boolean isShow){
        loading.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void loadData(){
        Http.create(this).callback(new HttpCallback<ResultEntity>() {
            @Override
            public void onStart() {
                int i = 0;
                loading(true);
            }

            @Override
            public void onEnd() {
                int i = 0;
                loading(false);
            }

            @Override
            public void onSuccess(ResultEntity resultEntity) {
                int i = 0;
                toast("success");
            }

            @Override
            public void onFail(String failMsg) {
                int i = 0;
                toast(failMsg);
            }

            @Override
            public void onError(Exception ex) {
                int i = 0;
                toast("error");
            }
        }).get();

    }
}
