package com.jlj.exam.http;

import com.alibaba.fastjson.JSON;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

class ResultParser {
    public <T> T parse(Class<T> cls, InputStream in){
        String jsonStr = getString(in);
        return parse(cls, jsonStr);
    }

    public <T> T parse(Class<T> cls, String data){
        ResultObject result = JSON.parseObject(data, ResultObject.class);

        if(result != null && result.getResult() != null && result.getResult().getRecords() != null && !result.getResult().getRecords().isEmpty()){
            return collect(cls, result.getResult().getRecords());
        }

        return null;

    }

    private <T> T collect(Class<T> cls, List<Record> records){

        List<Record> list = new ArrayList<>();
        Map<String, Double> map = new TreeMap<>();
        Set<String> dropYearSet = new HashSet<>();
        double maxValue = 0;
        double minValue = Double.MAX_VALUE;

        double lastValue = 0;

        for (Record record : records){
            String[] quarter = record.getQuarter().split("-");
            if(!map.containsKey(quarter[0])){
                map.put(quarter[0], 0.0);
                lastValue = record.getVolume_of_mobile_data();
            }

            map.put(quarter[0], map.get(quarter[0]) + record.getVolume_of_mobile_data());
            if(lastValue > record.getVolume_of_mobile_data()){
                dropYearSet.add(quarter[0]);
            }

            maxValue = maxValue < record.getVolume_of_mobile_data() ? record.getVolume_of_mobile_data() : maxValue;
            minValue = minValue > record.getVolume_of_mobile_data() ? record.getVolume_of_mobile_data() : minValue;

            lastValue = record.getVolume_of_mobile_data();
        }

        Iterator<String> it = map.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();

            Record record = new Record();
            record.setYear(key);
            record.setVolume_of_mobile_data(map.get(key));
            record.setDrop(dropYearSet.contains(key));

            list.add(record);
        }

        return injectObject(cls, maxValue, minValue, list);
    }

    private <T> T injectObject(Class<T> cls, double maxValue, double minValue, List<Record> records){
        try {
            T obj = cls.newInstance();
            Field[] fields = obj.getClass().getDeclaredFields();
            if(fields != null && fields.length > 0){
                for(int i = 0 ; i < fields.length ; i++){
                    Field field = fields[i];
                    if(field.getName().equals("maxValue")){
                        field.setAccessible(true);
                        field.setDouble(obj, maxValue);
                        continue;
                    }
                    if(field.getName().equals("minValue")){
                        field.setAccessible(true);
                        field.setDouble(obj, minValue);
                        continue;
                    }
                    if(field.getType().getSimpleName().equalsIgnoreCase("List")){
                        field.setAccessible(true);
                        field.set(obj, new ArrayList<>());

                        Type[] type = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();

                        for(Record record : records){
                            ((List)field.get(obj)).add(copyAttr(record, (Class<T>) type[0]));
                        }
                        continue;
                    }
                }
            }

            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private <T> T copyAttr(Record source, Class<T> target){
        try {
            T obj = target.newInstance();

            Field[] fields = obj.getClass().getDeclaredFields();
            if(fields != null && fields.length > 0){
                for(int i = 0 ; i < fields.length ; i++){
                    Field field = fields[i];
                    if(field.getName().equals("volume_of_mobile_data")){
                        field.setAccessible(true);
                        field.setDouble(obj, source.getVolume_of_mobile_data());
                        continue;
                    }
                    if(field.getName().equals("quarter")){
                        field.setAccessible(true);
                        field.set(obj, source.getQuarter());
                        continue;
                    }
                    if(field.getName().equals("year")){
                        field.setAccessible(true);
                        field.set(obj, source.getYear());
                        continue;
                    }
                    if(field.getName().equals("isDrop")){
                        field.setAccessible(true);
                        field.setBoolean(obj, source.isDrop());
                        continue;
                    }
                }
            }

            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
}
