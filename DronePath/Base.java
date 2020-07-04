package com.example.dronepath;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Base {
    public String key;
    public String name;
    public Double latitude;
    public Double longitude;
    public Double altitude;
    public float safe_min_battery_percentage; //percentage. Κάποιες μπαταρίες έχουν ελάχιστο ασφαλές ποσοστό στάθμης της μπαταρίας, που αν ξεπεραστεί μπορεί να χαλάσει. Σε κάποιες είναι 30%.
    public float acceleration_vertical; //2 --3,6 g or 35 m/s^2, (1 g = 9.80665 m/s^2) για 100km/h
    public float acceleration_horizontal; //= 1,8
    public float max_speed_vertical;// = 18 km/h (5 m/s)
    public float max_speed_horizontal;// = 79 km/h (22 m/s)
    public float deceleration_vertical;// = 2 x acceleration_vertical
    public float deceleration_horizontal;// = 2 x acceleration_horizontal
    public Double target_latitude;  //= 0
    public Double target_longitude; // = 0
    public Double target_radius; // = 1000 --m

/*
    drone_count = 10 – Το ένα από αυτά τα drones θα είναι master και τα υπόλοιπα θα είναι slaves που απλά αναμεταδίδουν το σήμα.
    status_delay_in_ready_mode = 10 –sec
    status_delay_in_flight_mode = 1 –sec
    lift_off_delay = 2 –sec. Ο χρόνος του lift off μεταξύ δύο drone
    synchronization_time
*/

    public Base(String key                        ,
                String name                       ,
                Double latitude                   ,
                Double longitude                  ,
                Double altitude                   ,
                float  safe_min_battery_percentage,
                float  acceleration_vertical      ,
                float  acceleration_horizontal    ,
                float  max_speed_vertical         ,
                float  max_speed_horizontal       ,
                float  deceleration_vertical      ,
                float  deceleration_horizontal    ,
                Double target_latitude            ,
                Double target_longitude           ,
                Double target_radius
                ){
        this.key                         = key                        ;
        this.name                        = name                       ;
        this.latitude                    = latitude                   ;
        this.longitude                   = longitude                  ;
        this.altitude                    = altitude                   ;
        this.safe_min_battery_percentage = safe_min_battery_percentage;
        this.acceleration_vertical       = acceleration_vertical      ;
        this.acceleration_horizontal     = acceleration_horizontal    ;
        this.max_speed_vertical          = max_speed_vertical         ;
        this.max_speed_horizontal        = max_speed_horizontal       ;
        this.deceleration_vertical       = deceleration_vertical      ;
        this.deceleration_horizontal     = deceleration_horizontal    ;
        this.target_latitude             = target_latitude            ;
        this.target_longitude            = target_longitude           ;
        this.target_radius               = target_radius              ;
    }

    public Base(DataSnapshot dataSnapshot){
        this.key                         = dataSnapshot.getKey()                        ;
        this.name                        =        (String)dataSnapshot.child("name").getValue();
        this.latitude                    = Double.valueOf(dataSnapshot.child("latitude").getValue().toString());
        this.longitude                   = Double.valueOf(dataSnapshot.child("longitude").getValue().toString());
        this.altitude                    = Double.valueOf(dataSnapshot.child("altitude").getValue().toString());
        this.safe_min_battery_percentage =  Float.valueOf(dataSnapshot.child("safe_min_battery_percentage").getValue().toString());
        this.acceleration_vertical       =  Float.valueOf(dataSnapshot.child("acceleration_vertical").getValue().toString());
        this.acceleration_horizontal     =  Float.valueOf(dataSnapshot.child("acceleration_horizontal").getValue().toString());
        this.max_speed_vertical          =  Float.valueOf(dataSnapshot.child("max_speed_vertical").getValue().toString());
        this.max_speed_horizontal        =  Float.valueOf(dataSnapshot.child("max_speed_horizontal").getValue().toString());
        this.deceleration_vertical       =  Float.valueOf(dataSnapshot.child("deceleration_vertical").getValue().toString());
        this.deceleration_horizontal     =  Float.valueOf(dataSnapshot.child("deceleration_horizontal").getValue().toString());
        this.target_latitude             = Double.valueOf(dataSnapshot.child("target_latitude").getValue().toString());
        this.target_longitude            = Double.valueOf(dataSnapshot.child("target_longitude").getValue().toString());
        this.target_radius               = Double.valueOf(dataSnapshot.child("target_radius").getValue().toString());
    }

    public Base(){
        this.key                         = "";
        this.name                        = "";
        this.latitude                    = 0d;
        this.longitude                   = 0d;
        this.altitude                    = 0d;
        this.safe_min_battery_percentage = 0f;
        this.acceleration_vertical       = 0f;
        this.acceleration_horizontal     = 0f;
        this.max_speed_vertical          = 0f;
        this.max_speed_horizontal        = 0f;
        this.deceleration_vertical       = 0f;
        this.deceleration_horizontal     = 0f;
        this.target_latitude             = 0d;
        this.target_longitude            = 0d;
        this.target_radius               = 0d;
    }

    public String Print(){
        return  key                                         + ", " +
                name                                        + ", " +
                String.valueOf(latitude                   ) + ", " +
                String.valueOf(longitude                  ) + ", " +
                String.valueOf(altitude                   ) + ", " +
                String.valueOf(safe_min_battery_percentage) + ", " +
                String.valueOf(acceleration_vertical      ) + ", " +
                String.valueOf(acceleration_horizontal    ) + ", " +
                String.valueOf(max_speed_vertical         ) + ", " +
                String.valueOf(max_speed_horizontal       ) + ", " +
                String.valueOf(deceleration_vertical      ) + ", " +
                String.valueOf(deceleration_horizontal    ) + ", " +
                String.valueOf(target_latitude            ) + ", " +
                String.valueOf(target_longitude           ) + ", " +
                String.valueOf(target_radius              );
    }
    public String Export(){
        return  key                                         + ";" +
                name                                        + ";" +
                String.valueOf(latitude                   ) + ";" +
                String.valueOf(longitude                  ) + ";" +
                String.valueOf(altitude                   ) + ";" +
                String.valueOf(safe_min_battery_percentage) + ";" +
                String.valueOf(acceleration_vertical      ) + ";" +
                String.valueOf(acceleration_horizontal    ) + ";" +
                String.valueOf(max_speed_vertical         ) + ";" +
                String.valueOf(max_speed_horizontal       ) + ";" +
                String.valueOf(deceleration_vertical      ) + ";" +
                String.valueOf(deceleration_horizontal    ) + ";" +
                String.valueOf(target_latitude            ) + ";" +
                String.valueOf(target_longitude           ) + ";" +
                String.valueOf(target_radius              ) + ";" ;
    }
    public void Import(String value){
        String[] avalue = value.split(";");
        int i = 0;
        key                         =                    avalue[i] ; i++;
        name                        =                    avalue[i] ; i++;
        latitude                    = Double.parseDouble(avalue[i]); i++;
        longitude                   = Double.parseDouble(avalue[i]); i++;
        altitude                    = Double.parseDouble(avalue[i]); i++;
        safe_min_battery_percentage =  Float.parseFloat( avalue[i]); i++;
        acceleration_vertical       =  Float.parseFloat( avalue[i]); i++;
        acceleration_horizontal     =  Float.parseFloat( avalue[i]); i++;
        max_speed_vertical          =  Float.parseFloat( avalue[i]); i++;
        max_speed_horizontal        =  Float.parseFloat( avalue[i]); i++;
        deceleration_vertical       =  Float.parseFloat( avalue[i]); i++;
        deceleration_horizontal     =  Float.parseFloat( avalue[i]); i++;
        target_latitude             = Double.parseDouble(avalue[i]); i++;
        target_longitude            = Double.parseDouble(avalue[i]); i++;
        target_radius               = Double.parseDouble(avalue[i]); i++;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name"                       ,                name                        );
        result.put("latitude"                   ,                latitude                    );
        result.put("longitude"                  ,                longitude                   );
        result.put("altitude"                   ,                altitude                    );
        result.put("safe_min_battery_percentage", String.valueOf(safe_min_battery_percentage));
        result.put("acceleration_vertical"      , String.valueOf(acceleration_vertical      ));
        result.put("acceleration_horizontal"    , String.valueOf(acceleration_horizontal    ));
        result.put("max_speed_vertical"         , String.valueOf(max_speed_vertical         ));
        result.put("max_speed_horizontal"       , String.valueOf(max_speed_horizontal       ));
        result.put("deceleration_vertical"      , String.valueOf(deceleration_vertical      ));
        result.put("deceleration_horizontal"    , String.valueOf(deceleration_horizontal    ));
        result.put("target_latitude"            ,                target_latitude             );
        result.put("target_longitude"           ,                target_longitude            );
        result.put("target_radius"              ,                target_radius               );
        return result;
    }

    public void WriteRecord(DatabaseReference mDatabase) {
        HashMap<String, Object> record = this.toHashMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, record);
        mDatabase.updateChildren(childUpdates);
    }

}
