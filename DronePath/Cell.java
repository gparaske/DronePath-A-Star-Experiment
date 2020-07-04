package com.example.dronepath;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Cell {
    public String key;
    public Double latitude;
    public Double longitude;
    public Double radius;    // Femtocells <= 10m, Picocells <= 200m, Microcells <= 2000m
    public Double power;     // 1..100 antenna signal on drone

    public Cell(String key                        ,
                Double latitude                   ,
                Double longitude                  ,
                Double radius                     ,
                Double power
                ){
        this.key                         = key                        ;
        this.latitude                    = latitude                   ;
        this.longitude                   = longitude                  ;
        this.radius                      = radius                     ;
        this.power                       = power                      ;
    }

    public Cell(DataSnapshot dataSnapshot){
        this.key                         = dataSnapshot.getKey()                        ;
        this.latitude                    = Double.valueOf(dataSnapshot.child("latitude").getValue().toString());
        this.longitude                   = Double.valueOf(dataSnapshot.child("longitude").getValue().toString());
        this.radius                      = Double.valueOf(dataSnapshot.child("radius").getValue().toString());
        this.power                       = Double.valueOf(dataSnapshot.child("power").getValue().toString());
    }

    public Cell(){
        this.key                         = "";
        this.latitude                    = 0d;
        this.longitude                   = 0d;
        this.radius                      = 0d;
        this.power                       = 0d;
    }

    public String Print(){
        return  key                                         + ", " +
                String.valueOf(latitude                   ) + ", " +
                String.valueOf(longitude                  ) + ", " +
                String.valueOf(radius                     ) + ", " +
                String.valueOf(power                      );
    }
    public String Export(){
        return  key                                         + ";" +
                String.valueOf(latitude                   ) + ";" +
                String.valueOf(longitude                  ) + ";" +
                String.valueOf(radius                     ) + ";" +
                String.valueOf(power                      ) + ";" ;
    }
    public void Import(String value){
        String[] avalue = value.split(";");
        int i = 0;
        key                         =                    avalue[i] ; i++;
        latitude                    = Double.parseDouble(avalue[i]); i++;
        longitude                   = Double.parseDouble(avalue[i]); i++;
        radius                      = Double.parseDouble(avalue[i]); i++;
        power                       = Double.parseDouble(avalue[i]); i++;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude"                   ,                latitude                    );
        result.put("longitude"                  ,                longitude                   );
        result.put("radius"                     ,                radius                      );
        result.put("power"                      ,                power                       );
        return result;
    }

    public void WriteRecord(DatabaseReference mDataBase) {
        HashMap<String, Object> record = this.toHashMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, record);
        mDataBase.updateChildren(childUpdates);
    }

}
