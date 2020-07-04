package com.example.dronepath;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Obstacle {
    public String key;
    public Double latitude;
    public Double longitude;
    public Double radius;    // Femtocells <= 10m, Picocells <= 200m, Microcells <= 2000m
    public Double slow;     // 1..100 ποσοστό μείωσης ταχύτητας. 50 = μείωση της ταχύτητας στο μισό, 100 = απροσπέλαστο σημείο.

    public Obstacle(String key                    ,
                Double latitude                   ,
                Double longitude                  ,
                Double radius                     ,
                Double slow
    ){
        this.key                         = key                        ;
        this.latitude                    = latitude                   ;
        this.longitude                   = longitude                  ;
        this.radius                      = radius                     ;
        this.slow                        = slow                       ;
    }

    public Obstacle(DataSnapshot dataSnapshot){
        this.key                         = dataSnapshot.getKey()                                                ;
        this.latitude                    = Double.valueOf(dataSnapshot.child("latitude" ).getValue().toString());
        this.longitude                   = Double.valueOf(dataSnapshot.child("longitude").getValue().toString());
        this.radius                      = Double.valueOf(dataSnapshot.child("radius"   ).getValue().toString());
        this.slow                        = Double.valueOf(dataSnapshot.child("slow"     ).getValue().toString());
    }

    public Obstacle(){
        this.key                         = "";
        this.latitude                    = 0d;
        this.longitude                   = 0d;
        this.radius                      = 0d;
        this.slow                        = 0d;
    }

    public String Print(){
        return  key                                         + ", " +
                String.valueOf(latitude                   ) + ", " +
                String.valueOf(longitude                  ) + ", " +
                String.valueOf(radius                     ) + ", " +
                String.valueOf(slow                       );
    }
    public String Export(){
        return  key                                         + ";" +
                String.valueOf(latitude                   ) + ";" +
                String.valueOf(longitude                  ) + ";" +
                String.valueOf(radius                     ) + ";" +
                String.valueOf(slow                       ) + ";" ;
    }
    public void Import(String value){
        String[] avalue = value.split(";");
        int i = 0;
        key                         =                    avalue[i] ; i++;
        latitude                    = Double.parseDouble(avalue[i]); i++;
        longitude                   = Double.parseDouble(avalue[i]); i++;
        radius                      = Double.parseDouble(avalue[i]); i++;
        slow                        = Double.parseDouble(avalue[i]); i++;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude"                   ,                latitude                    );
        result.put("longitude"                  ,                longitude                   );
        result.put("radius"                     ,                radius                      );
        result.put("slow"                       ,                slow                        );
        return result;
    }

    public void WriteRecord(DatabaseReference mDataBase) {
        HashMap<String, Object> record = this.toHashMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, record);
        mDataBase.updateChildren(childUpdates);
    }

}
