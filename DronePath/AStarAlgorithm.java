package com.example.dronepath;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class AStarAlgorithm implements Runnable {//extends Thread {
    private Object mPauseLock;
    private boolean mPaused;
    private boolean mFinished;
    private NodeManager nodeManager;
    //private Node CurrentNode;
    private int current_x;
    private int current_y;

    private static int colorBlue = (50 & 0xff) << 24 | (0 & 0xff) << 16 | (150 & 0xff) << 8 | (255 & 0xff); // A & RGB
    private static int colorGreen = (50 & 0xff) << 24 | (0 & 0xff) << 16 | (255 & 0xff) << 8 | (150 & 0xff); // A & RGB
    private static int colorPurple = (50 & 0xff) << 24 | (100 & 0xff) << 16 | (0 & 0xff) << 8 | (100 & 0xff); // A & RGB
    private static int colorBlueOnPurple = (50 & 0xff) << 24 | (1 & 0xff) << 16 | (0 & 0xff) << 8 | (255 & 0xff); // A & RGB


    public AStarAlgorithm(NodeManager nodeManager) {
        mPauseLock = new Object();
        mPaused = false;
        mFinished = false;
        this.nodeManager = nodeManager;

        nodeManager.InitializeBase();
        //CurrentNode = nodeManager.getNode(nodeManager.base_x, nodeManager.base_y);
        current_x = nodeManager.base_x; current_y = nodeManager.base_y;
    }

    public void run() {
        while (current_x!=nodeManager.target_x || current_y!=nodeManager.target_y){//(CurrentNode != nodeManager.getNode(nodeManager.target_x, nodeManager.target_y)) {
            // get all neighbors of the current node
            //ArrayList<Node> nodeNeighbors = ;
            for(Node node:nodeManager.getNeighbors(current_x, current_y)) {
                if(node.color==0){
                    nodeManager.FlashNode(node, colorGreen);
                } else {
                    nodeManager.FlashNode(node, node.color);
                }
/*
                int currentColor = 0;
                currentColor = node.getColor();//node.polygon.getFillColor();
                try {
                    currentColor = node.getColor();//node.polygon.getFillColor();
                    nodeManager.FlashNode(node, currentColor);
                } catch (Exception e) {
                    e.printStackTrace();
                    nodeManager.FlashNode(node, colorGreen);
                }
*/
/*                if(node.polygon.getFillColor()==0){
                    nodeManager.FlashNode(node, colorGreen);
                } else {
                    nodeManager.FlashNode(node, node.polygon.getFillColor());
                }*/
                synchronized (mPauseLock) {
                    try {
                        mPauseLock.wait(50);//350 delay
                    } catch (InterruptedException e) {
                    }
                }
            }

            // make blue the current option
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
/*
                    if(prev_valid != null) {
                        if(node.x==prev_valid.x & node.y==prev_valid.y){
                            break; // αν και εφόσον φτάσεις στο προηγούμενο της τρέχουσας επιλογής, δεν υπάρχει λόγος να συνεχίσεις.
                        } else {
                            if(node.color==colorBlue){
                                node.color = colorGreen;
                                node.polygon.setFillColor(colorGreen);
                            }
                        }
                    }
*/
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

/*
            CurrentNode = nodeManager.nodeNeighbors.get(0);
            nodeManager.FlashNode(CurrentNode, colorBlue);
*/
            synchronized (mPauseLock) {
                try {
                    mPauseLock.wait(50);//350 delay
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

        // Finish
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                nodeManager.BackTraceShortPathFromTargetToBase();
                MapsActivity.NextStage();
            }
        });
/*
            MapsActivity.NextStage();
*/


    }

    /**
     * Call this on pause.
     */
    public void onPause() {
        synchronized (mPauseLock) {
            mPaused = true;
        }
    }

    /**
     * Call this on resume.
     */
    public void onResume() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }
}
