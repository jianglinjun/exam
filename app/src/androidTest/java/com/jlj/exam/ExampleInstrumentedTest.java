package com.jlj.exam;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.alibaba.fastjson.JSON;
import com.jlj.exam.entity.ResultEntity;
import com.jlj.exam.http.Http;
import com.jlj.exam.http.HttpCallback;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.jlj.exam", appContext.getPackageName());
    }

    @Test
    public void http(){
        Context context = InstrumentationRegistry.getTargetContext();

        Http.create(context).callback(new HttpCallback<ResultEntity>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd() {

            }

            @Override
            public void onSuccess(ResultEntity entity) {
                System.out.println("============");
                System.out.println(JSON.toJSON(entity));
            }

            @Override
            public void onFail(String failMsg) {
                int i = 0 ;
            }

            @Override
            public void onError(Exception ex) {
                int i = 0 ;
            }
        }).get();
    }
}
