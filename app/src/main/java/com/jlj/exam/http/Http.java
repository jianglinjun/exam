package com.jlj.exam.http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.jlj.exam.entity.ResultEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class Http {
    private final String url = "https://data.gov.sg/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f&limit=100";

    private HttpCallback callback = null;
    private Context context = null;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private Http(Context context){
        this.context = context;
    }

    public static Http create(Context context){
        return new Http(context);
    }

    public Http callback(HttpCallback<?> callback){
        this.callback = callback;
        return this;
    }

    public void get(){
        if(callback != null){
            post(PostType.TYPE_ONSTART, null);
            String cacheData = CacheUtils.get(context, url);
            if(cacheData != null && !cacheData.equalsIgnoreCase("")){
                //先读缓存
                post(PostType.TYPE_ONSUCCESS, new ResultParser().parse(getT(), cacheData));
            }

            HttpConnectionPool.exec(new Runnable() {
                @Override
                public void run() {
                    doit();
                }
            });
        }
    }

    private void doit(){
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30 * 1000);
            connection.setReadTimeout(30 * 1000);
            connection.setDoInput(true);

            int code = connection.getResponseCode();

            if(code >= 200 && code < 300){
                String jsonStr = getString(connection.getInputStream());
                if(jsonStr != null && !jsonStr.equalsIgnoreCase("")){
                    //缓存最新的数据
                    CacheUtils.save(context, url, jsonStr);
                    post(PostType.TYPE_ONSUCCESS, new ResultParser().parse(getT(), jsonStr));
                }else{
                    post(PostType.TYPE_ONFAIL, "response data is empty!");
                }
            }else{
                post(PostType.TYPE_ONFAIL, "http fail! code: " + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
            post(PostType.TYPE_ONERROR, e);
        } finally {
            post(PostType.TYPE_ONEND, null);
        }
    }

    private String getString(InputStream in){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        int index = 0;
        byte[] buff = new byte[1 * 1024];

        try {
            while((index = in.read(buff)) != -1) {
                bos.write(buff, 0, index);
            }

            return new String(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bos.close();
            } catch (Exception e) {
            }
        }

        return null;
    }

    private void post(final int type, final Object obj){
        if(callback != null){
            handler.post(new Runnable() {
                @Override
                public void run() {

                    switch (type){
                        case PostType.TYPE_ONSTART:
                            callback.onStart();
                            break;
                        case PostType.TYPE_ONSUCCESS:
                            callback.onSuccess(obj);
                            break;
                        case PostType.TYPE_ONFAIL:
                            callback.onFail(obj + "");
                            break;
                        case PostType.TYPE_ONERROR:
                            callback.onError((Exception) obj);
                            break;
                        case PostType.TYPE_ONEND:
                            callback.onEnd();
                            break;
                    }
                }
            });
        }
    }

    private Class<?> getT(){
        Type[] types = callback.getClass().getGenericInterfaces();
        types = ((ParameterizedType)types[0]).getActualTypeArguments();
        return (Class<?>) types[0];
    }

    public static void main(String[] args) {

    }

}
