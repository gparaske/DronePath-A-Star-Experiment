package com.example.dronepath;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class DroneAlgorithm implements Runnable {//extends Thread {
    private Object mPauseLock;
    private boolean mPaused;
    private boolean mFinished;
    private NodeManager nodeManager;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private static ArrayList<LatLng> latLngs = new ArrayList<>();

    public DroneAlgorithm(NodeManager nodeManager) {
        mPauseLock = new Object();
        mPaused = false;
        mFinished = false;
        this.nodeManager = nodeManager;

        //nodeManager.InitializeBase();
        this.latitude = nodeManager.getNode(nodeManager.base_x, nodeManager.base_y).LatLngCenter.latitude;
        this.longitude = nodeManager.getNode(nodeManager.base_x, nodeManager.base_y).LatLngCenter.longitude;
        this.altitude = Double.valueOf(nodeManager.getNode(nodeManager.base_x, nodeManager.base_y).z);
    }

    private void updateLatLngs(Double fromLat, Double fromLng, Double toLat, Double toLng){
        int steps = 100;
        Double deltaLat = (toLat - fromLat) / steps;
        Double deltaLng = (toLng - fromLng) / steps;
        latLngs.clear();
        for (int i = 0; i < steps; i++) {
            latLngs.add(new LatLng(fromLat+(i*deltaLat), fromLng+(i*deltaLng)));
        }
    }

    // Στο πρόγραμμα αγνοείται η τιμή του altitude εφόσον η απεικόνιση γίνεται στις δυο διαστάσεις.
    public void run() {
        int nextPath = 1;
        int maxPath = MapsActivity.Paths.size()-1;
        for(Path path:MapsActivity.Paths){
            System.out.println(path.Print());

            LatLng toLatLng = new LatLng(MapsActivity.Paths.get(nextPath).latitude, MapsActivity.Paths.get(nextPath).longitude);
            // Rotate android markers from path.latitude/path.longitude to nextPath.latitude/nextPath.longitude
            int i=0;
            for(Marker marker:MapsActivity.markers){
                nodeManager.RotateDroneMarker(i, toLatLng);
                i++;
            }



            // Move android markers from path.latitude/path.longitude to nextPath.latitude/nextPath.longitude
            updateLatLngs(path.latitude, path.longitude, toLatLng.latitude, toLatLng.longitude);
            for(LatLng latLng:latLngs){
                i=0;
                for(Marker marker:MapsActivity.markers){
                    nodeManager.MoveDroneMarker(i, latLng);
                    i++;
                }

                synchronized (mPauseLock) {
                    try {
                        mPauseLock.wait(5);//350 delay
                    } catch (InterruptedException e) {
                    }
                }

                synchronized (mPauseLock) {
                    while (mPaused) {
                        try {
                            mPauseLock.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
            if (nextPath<maxPath) nextPath++;
            //MapsActivity.drone.marker.setPosition(latLng);



        //while (current_x!=nodeManager.target_x || current_y!=nodeManager.target_y){//(CurrentNode != nodeManager.getNode(nodeManager.target_x, nodeManager.target_y)) {

/*            for(Node node:nodeManager.getNeighbors(current_x, current_y)) {
                if(node.color==0){
                    nodeManager.FlashNode(node, colorGreen);
                } else {
                    nodeManager.FlashNode(node, node.color);
                }
                synchronized (mPauseLock) {
                    try {
                        mPauseLock.wait(50);//350 delay
                    } catch (InterruptedException e) {
                    }
                }
            }

            nodeManager.SortNodeNeighborsListBy_f(); // ταξινόμηση από το μικρότερο f στο μεγαλύτερο.
            // επιλέγουμε το μικρότερο f όπου προηγουμένως δεν έχει ξανα-επιλεχθεί.
            //Node prev_valid = null;
            int i = 0;
            for(Node node:nodeManager.nodeAllNeighbors) {
                if(node.open==true){ // μόνο όσα nodes βρίσκονται στα άκρα είναι διαθέσιμα για επιλογή
                    System.out.println("check: f("+String.valueOf(node.x)+","+String.valueOf(node.y)+")="+String.valueOf(node.g)+"g + "+String.valueOf(node.h)+"h = "+String.valueOf(node.f)+" (open="+String.valueOf(node.open)+")");
                    if(node.open==true) {
//                        if(node.open==true & prev_valid == null) {
//                        if(node.color!=colorBlue & prev_valid == null) {
                        // στο πρώτο μικρότερο f που δεν βρίσκεται στο βασικό path (δεν είναι μπλέ)
                        // κάντο επιλογή για το επόμενο κοντινότερο node
                        //node.setColor(colorBlue);
                        //node.color = colorBlue;
                        //this.CurrentNode = node;
                        current_x = node.x; current_y = node.y;
                        nodeManager.getNode(node.x, node.y).open = false;
                        System.out.println("CurrentNode: f("+String.valueOf(nodeManager.getNode(node.x, node.y).x)+","+String.valueOf(nodeManager.getNode(node.x, node.y).y)+")="+String.valueOf(nodeManager.getNode(node.x, node.y).f));
                        // Αλλάζουμε χρώμα στα nodes ώστε να φαίνεται ποιο έχει επιλεχθεί
                        if(node.color!=colorBlue){
                            //nodeManager.FlashNode(nodeManager.getNode(node.x, node.y), colorBlueOnPurple);
                            nodeManager.FlashNode(nodeManager.getNode(node.x, node.y), node.color);
                        } else {
                            nodeManager.FlashNode(nodeManager.getNode(node.x, node.y), colorBlue);
                        }
                        //prev_valid = CurrentNode.prev_node; // το προηγούμενό του πρέπει να είναι επίσης μπλέ, ενώ όλα τα άλλα πράσινα
                        break;
                    }
                }
                i++;
            }

            nodeManager.BackTraceNode(nodeManager.getNode(current_x, current_y));
            for (int y=0; y<nodeManager.max_y; y++){
                for (int x=0; x<nodeManager.max_x; x++){
                    Node node = nodeManager.getNode(x, y);
                    if(node.f<100d){
                        System.out.print("|f("+String.valueOf(node.x)+","+String.valueOf(node.y)+")="+String.valueOf(Math.round(node.f*1000d)/1000));
                    } else {
                        System.out.print("|");
                    }
                }
                System.out.println("|");
            }
*/
        }

        // Finish
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                MapsActivity.NextStage();
            }
        });
    }

    // Call this on pause.
    public void onPause() {
        synchronized (mPauseLock) {
            mPaused = true;
        }
    }

    // Call this on resume.
    public void onResume() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }
}
