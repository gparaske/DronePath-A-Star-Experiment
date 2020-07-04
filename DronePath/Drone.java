package com.example.dronepath;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Drone {
/*
    Role = 0 – 1 για master και >1 για τα υπόλοιπα που θα επιλεχτούν
    ReadyForLiftOff = 1 --1 true, 0 false
    Flying = 0 --1 true, 0 false
    BatteryPercentage = 99 –in %
    CurrentLatitude = 38.157320
    CurrentLongitude = 23.957970
    Angle = 240
    TargetLatitude = 0
    TargetLongitude = 0
    TargetRadius = 50 --m
    TimeStamp = 1562323953 -- 2019-07-05T10:52:33+00:00 in ISO 8601
*/
    public String key;
    public int role;
    public int ready;
    public int flying;
    public float battery;       // battery percentage
    public Double latitude;     // current latitude
    public Double longitude;    // current longitude
    public Float angle;         // direction
    public Double target_latitude;  // where the UAV want to go
    public Double target_longitude; // where the UAV want to go
    public Double target_radius;    // target radius in m
    public Timestamp timestamp;      // timestamp

    public Marker marker;

    public Drone(String key             ,
                 int role               ,
                 int ready              ,
                 int flying             ,
                 float battery          ,
                 Double latitude        ,
                 Double longitude       ,
                 float angle            ,
                 Double target_latitude ,
                 Double target_longitude,
                 Double target_radius   ,
                 Timestamp timestamp
    ){
        this.key             = key             ;
        this.role            = role            ;
        this.ready           = ready           ;
        this.flying          = flying          ;
        this.battery         = battery         ;
        this.latitude        = latitude        ;
        this.longitude       = longitude       ;
        this.angle           = angle           ;
        this.target_latitude = target_latitude ;
        this.target_longitude= target_longitude;
        this.target_radius   = target_radius   ;
        this.timestamp       = timestamp       ;
    }

    public Drone(DataSnapshot dataSnapshot){
        this.key             = dataSnapshot.getKey()                                                        ;
        this.role            = Integer.valueOf(dataSnapshot.child("role"            ).getValue().toString());
        this.ready           = Integer.valueOf(dataSnapshot.child("ready"           ).getValue().toString());
        this.flying          = Integer.valueOf(dataSnapshot.child("flying"          ).getValue().toString());
        this.battery         =   Float.valueOf(dataSnapshot.child("battery"         ).getValue().toString());
        this.latitude        =  Double.valueOf(dataSnapshot.child("latitude"        ).getValue().toString());
        this.longitude       =  Double.valueOf(dataSnapshot.child("longitude"       ).getValue().toString());
        this.angle           =   Float.valueOf(dataSnapshot.child("angle"           ).getValue().toString());
        this.target_latitude =  Double.valueOf(dataSnapshot.child("target_latitude" ).getValue().toString());
        this.target_longitude=  Double.valueOf(dataSnapshot.child("target_longitude").getValue().toString());
        this.target_radius   =  Double.valueOf(dataSnapshot.child("target_radius"   ).getValue().toString());
        this.timestamp       = Timestamp.valueOf(dataSnapshot.child("timestamp"     ).getValue().toString());
    }

    public Drone(){
        this.key                         = "";
        this.role            = 0;
        this.ready           = 0;
        this.flying          = 0;
        this.battery         = 0f;
        this.latitude        = 0d;
        this.longitude       = 0d;
        this.angle           = 0f;
        this.target_latitude = 0d;
        this.target_longitude= 0d;
        this.target_radius   = 0d;
        this.timestamp       = new Timestamp(System.currentTimeMillis());
    }

    public String Print(){
        return  key                              + ", " +
                String.valueOf(role            ) + ", " +
                String.valueOf(ready           ) + ", " +
                String.valueOf(flying          ) + ", " +
                String.valueOf(battery         ) + ", " +
                String.valueOf(latitude        ) + ", " +
                String.valueOf(longitude       ) + ", " +
                String.valueOf(angle           ) + ", " +
                String.valueOf(target_latitude ) + ", " +
                String.valueOf(target_longitude) + ", " +
                String.valueOf(target_radius   ) + ", " +
                String.valueOf(timestamp       );
    }

    public String Export(){
        return  key                                         + ";" +
                String.valueOf(role            ) + ";" +
                String.valueOf(ready           ) + ";" +
                String.valueOf(flying          ) + ";" +
                String.valueOf(battery         ) + ";" +
                String.valueOf(latitude        ) + ";" +
                String.valueOf(longitude       ) + ";" +
                String.valueOf(angle           ) + ";" +
                String.valueOf(target_latitude ) + ";" +
                String.valueOf(target_longitude) + ";" +
                String.valueOf(target_radius   ) + ";" +
                String.valueOf(timestamp       ) + ";" ;
    }
    public void Import(String value){
        String[] avalue = value.split(";");
        int i = 0;
        key                         =                    avalue[i] ; i++;
        role             = Integer.parseInt(avalue[i]); i++;
        ready            = Integer.parseInt(avalue[i]); i++;
        flying           = Integer.parseInt(avalue[i]); i++;
        battery          = Float.parseFloat(avalue[i]); i++;
        latitude         = Double.parseDouble(avalue[i]); i++;
        longitude        = Double.parseDouble(avalue[i]); i++;
        angle            = Float.parseFloat(avalue[i]); i++;
        target_latitude  = Double.parseDouble(avalue[i]); i++;
        target_longitude = Double.parseDouble(avalue[i]); i++;
        target_radius    = Double.parseDouble(avalue[i]); i++;
        timestamp        = new Timestamp(Integer.parseInt(avalue[i])); i++;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("role"            , role            );
        result.put("ready"           , ready           );
        result.put("flying"          , flying          );
        result.put("battery"         , battery         );
        result.put("latitude"        , latitude        );
        result.put("longitude"       , longitude       );
        result.put("angle"           , angle           );
        result.put("target_latitude" , target_latitude );
        result.put("target_longitude", target_longitude);
        result.put("target_radius"   , target_radius   );
        result.put("timestamp"       , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp)       );
        return result;
    }

    public void WriteRecord(DatabaseReference mDataBase) {
        HashMap<String, Object> record = this.toHashMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, record);
        mDataBase.updateChildren(childUpdates);
    }

}
