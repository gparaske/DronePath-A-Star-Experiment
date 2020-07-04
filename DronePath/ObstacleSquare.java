package com.example.dronepath;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class ObstacleSquare {
    public String key;
    public int x;
    public int y;
    public Double weight;     // Το βάρος αν διανύσεις όλο το τετράγωνο. 1 για full speed,
                              // >1 για obstacle που καθυστερεί (weight=2 τρέχει με την μισή ταχύτητα,
                              // weight=4 τρέχει με το 1/4 της ταχύτητας,
                              // weight=100 απροσπέλαστο)

    public ObstacleSquare(String key                        ,
                          int x                             ,
                          int y                             ,
                          Double weight
    ){
        this.key                         = key                        ;
        this.x                           = x                          ;
        this.y                           = y                          ;
        this.weight                      = weight                     ;
    }

    public ObstacleSquare(DataSnapshot dataSnapshot){
        this.key                         = dataSnapshot.getKey()                                              ;
        this.x                           = Integer.valueOf(dataSnapshot.child("x"     ).getValue().toString());
        this.y                           = Integer.valueOf(dataSnapshot.child("y"     ).getValue().toString());
        this.weight                      = Double.valueOf( dataSnapshot.child("weight").getValue().toString());
    }

    public ObstacleSquare(){
        this.key                         = "";
        this.x                           = 0;
        this.y                           = 0;
        this.weight                      = 0d;
    }

    public String Print(){
        return  key                                         + ", " +
                String.valueOf(x                          ) + ", " +
                String.valueOf(y                          ) + ", " +
                String.valueOf(weight                     );
    }
    public String Export(){
        return  key                                         + ";" +
                String.valueOf(x                          ) + ";" +
                String.valueOf(y                          ) + ";" +
                String.valueOf(weight                     ) + ";" ;
    }
    public void Import(String value){
        String[] avalue = value.split(";");
        int i = 0;
        key                         =                    avalue[i] ; i++;
        x                           = Integer.parseInt(  avalue[i]); i++;
        y                           = Integer.parseInt(  avalue[i]); i++;
        weight                      = Double.parseDouble(avalue[i]); i++;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("x"                           ,                x                            );
        result.put("y"                           ,                y                            );
        result.put("weight"                      ,                weight                       );
        return result;
    }

    public void WriteRecord(DatabaseReference mDataBase) {
        HashMap<String, Object> record = this.toHashMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, record);
        mDataBase.updateChildren(childUpdates);
    }

}
