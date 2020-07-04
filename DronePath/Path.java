package com.example.dronepath;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Path {
    public String key;
    public Double latitude;
    public Double longitude;
    public Double altitude;

    public Path(String key                        ,
                Double latitude                   ,
                Double longitude                  ,
                Double altitude
    ){
        this.key                         = key                        ;
        this.latitude                    = latitude                   ;
        this.longitude                   = longitude                  ;
        this.altitude                    = altitude                   ;
    }

    public Path(DataSnapshot dataSnapshot){
        this.key                         = dataSnapshot.getKey()                        ;
        this.latitude                    = Double.valueOf(dataSnapshot.child("latitude").getValue().toString());
        this.longitude                   = Double.valueOf(dataSnapshot.child("longitude").getValue().toString());
        this.altitude                    = Double.valueOf(dataSnapshot.child("altitude").getValue().toString());
    }

    public Path(){
        this.key                         = "";
        this.latitude                    = 0d;
        this.longitude                   = 0d;
        this.altitude                    = 0d;
    }

    public String Print(){
        return  key                                         + ", " +
                String.valueOf(latitude                   ) + ", " +
                String.valueOf(longitude                  ) + ", " +
                String.valueOf(altitude                   );
    }
    public String Export(){
        return  key                                         + ";" +
                String.valueOf(latitude                   ) + ";" +
                String.valueOf(longitude                  ) + ";" +
                String.valueOf(altitude                   ) + ";" ;
    }
    public void Import(String value){
        String[] avalue = value.split(";");
        int i = 0;
        key                         =                    avalue[i] ; i++;
        latitude                    = Double.parseDouble(avalue[i]); i++;
        longitude                   = Double.parseDouble(avalue[i]); i++;
        altitude                    = Double.parseDouble(avalue[i]); i++;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude"                     ,                latitude                      );
        result.put("longitude"                    ,                longitude                     );
        result.put("altitude"                     ,                altitude                      );
        return result;
    }

    public void WriteRecord(DatabaseReference mDataBase) {
        HashMap<String, Object> record = this.toHashMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, record);
        mDataBase.updateChildren(childUpdates);
    }

}
