package com.example.dronepath;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Parameter {
    public String key;
    public int c_from;
    public int c_to;
    public Float speed;

    public Parameter(String key                        ,
                     int c_from                        ,
                     int c_to                          ,
                     Float speed
    ){
        this.key                         = key                        ;
        this.c_from                      = c_from                     ;
        this.c_to                        = c_to                       ;
        this.speed                       = speed                      ;
    }

    public Parameter(DataSnapshot dataSnapshot){
        this.key                         = dataSnapshot.getKey()                        ;
        this.c_from                      = Integer.valueOf(dataSnapshot.child("c_from").getValue().toString());
        this.c_to                        = Integer.valueOf(dataSnapshot.child("c_to").getValue().toString());
        this.speed                       =   Float.valueOf(dataSnapshot.child("speed").getValue().toString());
    }

    public Parameter(){
        this.key                         = "";
        this.c_from                      = 0;
        this.c_to                        = 0;
        this.speed                       = 0f;
    }

    public String Print(){
        return  key                                         + ", " +
                String.valueOf(c_from                     ) + ", " +
                String.valueOf(c_to                       ) + ", " +
                String.valueOf(speed                      );
    }
    public String Export(){
        return  key                                         + ";" +
                String.valueOf(c_from                     ) + ";" +
                String.valueOf(c_to                       ) + ";" +
                String.valueOf(speed                      ) + ";" ;
    }
    public void Import(String value){
        String[] avalue = value.split(";");
        int i = 0;
        key                         =                    avalue[i] ; i++;
        c_from                      =   Integer.parseInt(avalue[i]); i++;
        c_to                        =   Integer.parseInt(avalue[i]); i++;
        speed                       =   Float.parseFloat(avalue[i]); i++;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("c_from"                     ,                c_from                      );
        result.put("c_to"                       ,                c_to                        );
        result.put("speed"                      ,                speed                       );
        return result;
    }

    public void WriteRecord(DatabaseReference mDataBase) {
        HashMap<String, Object> record = this.toHashMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, record);
        mDataBase.updateChildren(childUpdates);
    }

}
