package com.example.dronepath;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;

public class Node {
    public int x;
    public int y;
    public int z; // altitude in meters
    public int color;
    public int purple;
    public Polygon polygon;
    public LatLng LatLngCenter;
    public Marker textMarker;
    public Double weight; // Το βάρος αν διανύσεις όλο το τετράγωνο. 1 για full speed,
                          // >1 για obstacle που καθυστερεί (weight=2 τρέχει με την μισή ταχύτητα,
                          // weight=4 τρέχει με το 1/4 της ταχύτητας,
                          // weight=100 απροσπέλαστο)
    public Node prev_node;// Το προηγούμενο node από το οποίο εισέρχεται το drone

    public Float speed; // m/sec
    public Double time; // sec
    public Double angle; // radius
    public Double g; // Manhattan distance from starting square
    public Double h; // Euclidean distance from finish square
    public Double f; // Function value
    public Boolean open = true; //open to be chosen

    public Node(int x, int y){
        this.x = x;
        this.y = y;
        this.z = 100; // default altitude in meters
        this.speed = MapsActivity.DefaultSpeed;
        this.time = 0d;
        this.angle = MapsActivity.DefaultAngle;
        this.g = 0d;
        this.h = 0d;
        this.f = 99999d;
        this.weight = 1d;
    }

    public void setPolygon(Polygon polygon){
        this.polygon = polygon;
        this.color = polygon.getFillColor();
    }

    public void setColor(int color){
        if (this.polygon != null) {
            this.polygon.setFillColor(color);
        }
        this.color = color;
    }

    // Μετατροπή των συντεταγμένων (0,0) τετραγώνου σε δείκτη (0), κτλ
    public int index(){
        int iIndex = x*MapsActivity.countUnitsHeight;
        return iIndex+y;
    }
}
