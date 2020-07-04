package com.example.dronepath;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.dronepath.MapsActivity.createTextIcon;

public class NodeManager {
    public ArrayList<Node> nodeShortestPath = new ArrayList<>();
    public ArrayList<ArrayList<Node>> nodes = new ArrayList<>();
    public int base_x; //x-Axis of base
    public int base_y; //y-Axis of base
    public int target_x; //x-Axis of target
    public int target_y; //y-Axis of target
    public int max_x;
    public int max_y;
    private Double diagonalMove = 1.4142d; // Απόσταση μεταξύ δύο διαγώνιων τετραγώνων (Πυθαγόρειο)
    public ArrayList<Node> nodeNeighbors = new ArrayList<>();
    public ArrayList<Node> nodeAllNeighbors = new ArrayList<>();
//    private Double w = 0.5d; // 0.5 Σταθμισμένος συντελεστής
    private Double degreesPerSec = 0.066d; // sec για 45 μοίρες στροφή 0.066->3sec, 0.088->4sec, 0.111->5sec

    private static int colorBlue = (150 & 0xff) << 24 | (0 & 0xff) << 16 | (150 & 0xff) << 8 | (255 & 0xff); // A & RGB
    private static int colorGreen = (50 & 0xff) << 24 | (0 & 0xff) << 16 | (255 & 0xff) << 8 | (150 & 0xff); // A & RGB
    private static int colorPurple = (50 & 0xff) << 24 | (100 & 0xff) << 16 | (0 & 0xff) << 8 | (100 & 0xff); // A & RGB
    private static int colorBlueOnPurple = (50 & 0xff) << 24 | (1 & 0xff) << 16 | (0 & 0xff) << 8 | (255 & 0xff); // A & RGB

    public Node getNode(int x, int y){
        return nodes.get(x).get(y);
    }

    public void InitializeBase(){
        Node nodeCurrent = getNode(base_x, base_y);
        nodeCurrent.g = 0d; // 1 more step from node
        nodeCurrent.h = distanceFromTwoPoints(nodeCurrent.x, nodeCurrent.y, target_x, target_y);
        nodeCurrent.f = nodeCurrent.g + nodeCurrent.h;
        nodeCurrent.color = colorBlue;
        nodeCurrent.open = false;
        System.out.println("f("+String.valueOf(nodeCurrent.x)+","+String.valueOf(nodeCurrent.y)+") = "+String.valueOf(nodeCurrent.g)+"g + "+String.valueOf(nodeCurrent.h)+"h = "+String.valueOf(nodeCurrent.f));
        nodeAllNeighbors.add(nodeCurrent);
    }

    public void showText(Node node, String text){
        class RunnableWithParameter implements Runnable {
            Node node;
            String text;
            RunnableWithParameter(Node node, String text) { this.node = node; this.text = text; }
            public void run() {
            }
        }
        new Handler(Looper.getMainLooper()).post(new RunnableWithParameter(node, text) {
            @Override
            public void run() {
                node.textMarker.setIcon(createTextIcon(text));
            }
        });

    }

    public ArrayList<Node> getNeighbors(int x, int y){
        Node node = getNode(x,y); // Current node
        nodeNeighbors.clear();
        if (node.y>0) { // Check Up
            UpdateNode(node.x,node.y-1, node, 1d);
        }
        if (node.x>0 & node.y>0) { // Check Left-Up
            UpdateNode(node.x-1,node.y-1, node, diagonalMove);
        }
        if (node.x>0) { // Check Left
            UpdateNode(node.x-1,node.y, node, 1d);
        }
        if (node.x>0 & node.y<max_y-1) { // Check Left-Down
            UpdateNode(node.x-1,node.y+1, node, diagonalMove);
        }
        if (node.y<max_y-1) { // Check Down
            UpdateNode(node.x,node.y+1, node, 1d);
        }
        if (node.x<max_x-1 & node.y<max_y-1) { // Check Right-Down
            UpdateNode(node.x+1,node.y+1, node, diagonalMove);
        }
        if (node.x<max_x-1) { // Check Right
            UpdateNode(node.x+1,node.y, node, 1d);
        }
        if (node.x<max_x-1 & node.y>0) { // Check Right-Up
            UpdateNode(node.x+1,node.y-1, node, diagonalMove);
        }
        return nodeNeighbors;
    }
    public ArrayList<Node> getJustNeighbors(int x, int y){
        Node node = getNode(x,y); // Current node
        nodeNeighbors.clear();
        if (node.y>0) { // Check Up
            nodeNeighbors.add(nodes.get(x).get(y-1));
        }
        if (node.x>0 & node.y>0) { // Check Left-Up
            nodeNeighbors.add(nodes.get(x-1).get(y-1));
        }
        if (node.x>0) { // Check Left
            nodeNeighbors.add(nodes.get(x-1).get(y));
        }
        if (node.x>0 & node.y<max_y-1) { // Check Left-Down
            nodeNeighbors.add(nodes.get(x-1).get(y+1));
        }
        if (node.y<max_y-1) { // Check Down
            nodeNeighbors.add(nodes.get(x).get(y+1));
        }
        if (node.x<max_x-1 & node.y<max_y-1) { // Check Right-Down
            nodeNeighbors.add(nodes.get(x+1).get(y+1));
        }
        if (node.x<max_x-1) { // Check Right
            nodeNeighbors.add(nodes.get(x+1).get(y));
        }
        if (node.x<max_x-1 & node.y>0) { // Check Right-Up
            nodeNeighbors.add(nodes.get(x+1).get(y-1));
        }
        return nodeNeighbors;
    }

    public Double ResultInSeconds(){
        Double dTime = nodes.get(target_x).get(target_y).time;
        // Αν θέλουμε να επιστρέφει πρέπει να διπλασιάσουμε τον χρόνο και να προσθέσουμε και
        // μια αναστροφή.
        if(MapsActivity.DefaultRoute==1){
            dTime *= 2;
            dTime += degreesPerSec*180;
        }
        return dTime;
    }
    private void UpdateNode(int x, int y, Node nodePrev, Double moveCost){
        Parameter parameter = getParameter(nodePrev.x, nodePrev.y, x, y);
        //Double g = nodePrev.g + ((nodes.get(x).get(y).weight+nodePrev.weight)*moveCost)/(2 * parameter.speed); // 1 more step from node
/*
        Double t = nodePrev.time + distanceFromTwoPoints(x, y, nodePrev.x, nodePrev.y) / parameter.speed;
        Double g = nodePrev.g + (distanceFromTwoPoints(x, y, nodePrev.x, nodePrev.y) / parameter.speed); // 1 more step from node
        Double h = distanceFromTwoPoints(x, y, target_x, target_y) / parameter.speed;
*/
        Double a = angleFromTwoPoints(nodePrev.x, nodePrev.y, x, y);
        Double diffAngle = Math.abs(a-nodePrev.angle);
        Double D = distanceFromTwoPoints(nodes.get(x).get(y).LatLngCenter, nodePrev.LatLngCenter);
        Double TurnCost = degreesPerSec*diffAngle; //sec
        if(!MapsActivity.DefaultAlgorithm.contentEquals("3")){
            TurnCost = 0d; //sec
        }
        // Σύμφωνα με τις τελευταίες οδηγίες η ταχύτητα δεν μπορεί να είναι άλλη από την μέγιστη
        // (5 m/s) ή την ελάχιστη (1 m/s).
        if(parameter.speed==5f) {
            parameter.speed = MapsActivity.DefaultSpeed;
        }else{
            parameter.speed = 1f;
        }
        Double t = nodePrev.time + (distanceFromTwoPoints(nodes.get(x).get(y).LatLngCenter, nodePrev.LatLngCenter) / parameter.speed)+degreesPerSec*diffAngle;
        //Double t = nodePrev.time + (distanceFromTwoPoints(nodes.get(x).get(y).LatLngCenter, nodePrev.LatLngCenter)) / parameter.speed;

        //Double g = 0d; // δεν έχει σημασία το παρελθόν φτάνει να πηγαίνουμε όσο πιο κοντά
        //Double g = nodePrev.g + (distanceFromTwoPoints(nodes.get(x).get(y).LatLngCenter, nodePrev.LatLngCenter) / parameter.speed); // δεν έχουν σημασία οι αλλαγές κατεύθυνησης
        //Double g = nodePrev.g + (distanceFromTwoPoints(nodes.get(x).get(y).LatLngCenter, nodePrev.LatLngCenter) / parameter.speed)+ TurnCost; // 1 more step from node
        Double g = nodePrev.g + (distanceFromTwoPoints(nodes.get(x).get(y).LatLngCenter, nodePrev.LatLngCenter) / parameter.speed); // 1 more step from node
        Double h = (distanceFromTwoPoints(nodes.get(x).get(y).LatLngCenter, nodes.get(target_x).get(target_y).LatLngCenter) / parameter.speed) + TurnCost;
        //Double h = distanceFromTwoPoints(nodes.get(x).get(y).LatLngCenter, nodes.get(target_x).get(target_y).LatLngCenter) / MapsActivity.DefaultSpeed; //den mas endoiaferoyn ta empodia
        if(MapsActivity.DefaultAlgorithm.contentEquals("1")){
            g = 0d; // δεν έχει σημασία το παρελθόν φτάνει να πηγαίνουμε όσο πιο κοντά
            h = distanceFromTwoPoints(nodes.get(x).get(y).LatLngCenter, nodes.get(target_x).get(target_y).LatLngCenter) / MapsActivity.DefaultSpeed; //den mas endoiaferoyn ta empodia
        }

        //Double f = (1-w) * g + w * h;
        Double f = g + h;
        //System.out.println("f("+String.valueOf(nodes.get(x).get(y).x)+","+String.valueOf(nodes.get(x).get(y).y)+") = (1-w)*"+String.valueOf(g)+"g + w*"+String.valueOf(h)+"h = "+String.valueOf(f));
        //if (nodes.get(x).get(y).f> (1-w) * g + w * h){ // better way to reach that point
        if (nodes.get(x).get(y).f> g + h){ // better way to reach that point
            nodes.get(x).get(y).time = t;
            nodes.get(x).get(y).angle = a;
            nodes.get(x).get(y).g = g;
            nodes.get(x).get(y).h = h;
            nodes.get(x).get(y).f = f;
            showText(nodes.get(x).get(y), String.valueOf(String.format("%.0f",nodes.get(x).get(y).f)));
            nodes.get(x).get(y).prev_node = nodePrev;
            nodeNeighbors.add(nodes.get(x).get(y));
            nodeAllNeighbors.add(nodes.get(x).get(y));
            System.out.println("f("+String.valueOf(nodes.get(x).get(y).x)+","+String.valueOf(nodes.get(x).get(y).y)+") = (1-w)*"+String.valueOf(nodes.get(x).get(y).g)+"g + w*"+String.valueOf(nodes.get(x).get(y).h)+"h = "+String.valueOf(nodes.get(x).get(y).f));
        }

    }

    private Double angleFromTwoPoints(int x1, int y1, int x2, int y2){
        if (x1==x2 & y1>y2) { // Check Up
            return 90d;
        }
        if (x1>x2 & y1>y2) { // Check Left-Up
            return 135d;
        }
        if (x1>x2 & y1==y2) { // Check Left
            return 180d;
        }
        if (x1>x2 & y1<y2) { // Check Left-Down
            return 225d;
        }
        if (x1==x2 & y1<y2) { // Check Down
            return 270d;
        }
        if (x1<x2 & y1<y2) { // Check Right-Down
            return 315d;
        }
        if (x1<x2 & y1==y2) { // Check Right
            return 0d;
        }
        if (x1>x2 & y1>y2) { // Check Right-Up
            return 45d;
        }
        return MapsActivity.DefaultAngle;
    }
    private Double distanceFromTwoPoints(int x1, int y1, int x2, int y2){
        Double distance = Math.sqrt(Math.pow(x1-x2, 2d)+Math.pow(y1-y2, 2d));
        return distance;
    }
    private Double distanceFromTwoPoints(LatLng from_LatLng, LatLng to_LatLng){
        Double distance = Math.sqrt(Math.pow(from_LatLng.latitude-to_LatLng.latitude, 2d)+Math.pow(from_LatLng.longitude-to_LatLng.longitude, 2d));
        return distance*111193; //meters
    }

    public void FlashNode(Node node, int color){
        class RunnableWithParameter implements Runnable {
            Node node;
            int color;
            RunnableWithParameter(Node node, int color) { this.node = node; this.color = color; }
            public void run() {
            }
        }
        int colorWhite = (225 & 0xff) << 24 | (255 & 0xff) << 16 | (255 & 0xff) << 8 | (255 & 0xff); // A & RGB

        new Handler(Looper.getMainLooper()).postDelayed(new RunnableWithParameter(node, colorWhite) {
            @Override
            public void run() {
                node.polygon.setFillColor(color);
            }
        }, 10L);//200
        new Handler(Looper.getMainLooper()).postDelayed(new RunnableWithParameter(node, color) {
            @Override
            public void run() {
                node.setColor(color);
/*
                node.color = color;
                node.polygon.setFillColor(color);
*/
            }
        }, 10L);//350
    }

    public void SortNodeNeighborsListBy_f() {
        Collections.sort(nodeAllNeighbors, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2)
            {
                return  (int)((o1.f-o2.f)*1000d);
            }
        });
    }

    // Χρωματίζει τα τετράγωνα στον χάρτη βάση της τρέχουσας πιο σύντομης διαδρομής για το κόμβο
    // που του δίνουμε.
    public void BackTraceNode(Node CurrentNode) {
        class RunnableWithParameter implements Runnable {
            Node node;
            int color;
            RunnableWithParameter(Node node, int color) { this.node = node; this.color = color; }
            public void run() {
            }
        }
        // a string with the sort path
        String sSortPath = ";" + String.valueOf(CurrentNode.x) + "," + String.valueOf(CurrentNode.y) + ";";
        while (CurrentNode.x != base_x || CurrentNode.y != base_y) {
            CurrentNode = CurrentNode.prev_node;
            sSortPath += String.valueOf(CurrentNode.x) + "," + String.valueOf(CurrentNode.y) + ";";
        }

        for (Node node : nodeAllNeighbors) {
            if(sSortPath.indexOf( ";"+String.valueOf(node.x) + "," + String.valueOf(node.y) + ";" )>=0){
                // in sortest path
                if(node.color==colorPurple){
                    new Handler(Looper.getMainLooper()).post(new RunnableWithParameter(node, colorBlueOnPurple) {
                        @Override
                        public void run() {
                            node.setColor(colorBlueOnPurple);
                        }
                    });
                    //node.setColor(colorBlueOnPurple);
                } else {
                    new Handler(Looper.getMainLooper()).post(new RunnableWithParameter(node, colorBlue) {
                        @Override
                        public void run() {
                            node.setColor(colorBlue);
                        }
                    });
                    //node.setColor(colorBlue);
                }
            } else {
                if(node.color==colorBlueOnPurple){
                    new Handler(Looper.getMainLooper()).post(new RunnableWithParameter(node, colorPurple) {
                        @Override
                        public void run() {
                            node.setColor(colorPurple);
                        }
                    });
//                    node.setColor(colorPurple);
                } else if(node.color==colorBlue){
                    new Handler(Looper.getMainLooper()).post(new RunnableWithParameter(node, colorGreen) {
                        @Override
                        public void run() {
                            node.setColor(colorGreen);
                        }
                    });
//                    node.setColor(colorGreen);
                }
            }
        }
    }

    // Ενημερώνει όλα τα nodes του χάρτη με default τιμές. Κάθε φορά που εκτελείται η συγκεκριμένη
    // μέθοδος κάνει reset όλη την προηγούμενη πληροφόρηση. Πρέπει να τρέχει μόνο μια φορά. Μόνο
    // για νέα σενάρια.
    public void UploadNodesToCloud(){
        // Δημιουργία/Ενημέρωση των nodes στο cloud βάσει του ονόματος της βάσης (base.name)
        // Αλλάζοντας όνομα στην βάση είναι εφικτό να δημιουργούνται νέα σενάρια.
        // Αμέσως μετά την δημιουργία των nodes τα φορτώνουμε στο cloud και εκεί μπορεί ο χρήστης να πειράξει τα altitude
        // δεν είναι απαραίτητο κάθε φορά να δημιουργούμε νέα nodes εκτός απ' την πρώτη φορά οι υπόλοιπες μπορούν να
        // φορτώνονται απ' το cloud.

        // Δημιουργεί καινούρια με βάσει τις default τιμές (το parameters array θα γεμίσει αυτόματα από το MapsActivity)
        for (ArrayList<Node> a_node : nodes) {
            for (Node node : a_node) {
                // Βρίσκει τους Neighbors ώστε να βάζει τα σωστά c_to
                ArrayList<Node> neighbors = getJustNeighbors(node.x, node.y);
                for (Node to_node : neighbors) {
                    // Create a record for Parameter and add it on database
                    DatabaseReference createNewParamer;// = MapsActivity.cloud.database.getReference("Parameter");
                    switch(MapsActivity.DefaultScenario) {
                        case "1":
                            createNewParamer = MapsActivity.cloud.database.getReference("Parameter1");
                            break;
                        case "2":
                            createNewParamer = MapsActivity.cloud.database.getReference("Parameter2");
                            break;
                        default:
                            createNewParamer = MapsActivity.cloud.database.getReference("Parameter");
                            break;
                    }
                    Parameter parameter = new Parameter(createNewParamer.push().getKey(),
                            node.index(),
                            to_node.index(),
                            Float.valueOf(node.speed));
                    parameter.WriteRecord(createNewParamer);
                }
            }
        }
    }

    // Ενημερώνει το array nodeShortestPath με τους κόμβους της πιο σύντομης διαδρομής
    public void BackTraceShortPathFromTargetToBase(){
        Node CurrentNode = nodes.get(target_x).get(target_y);
        ArrayList<Node> nodeShortestPathTmp = new ArrayList<>();
        nodeShortestPathTmp.add(CurrentNode);
        while (CurrentNode.x != base_x || CurrentNode.y != base_y) {
            CurrentNode = CurrentNode.prev_node;
            nodeShortestPathTmp.add(CurrentNode);
        }
        for (int i = nodeShortestPathTmp.size()-1; i > -1; i--) {
            nodeShortestPath.add(nodeShortestPathTmp.get(i));
        }
        if (MapsActivity.DefaultRoute==1) {
            for (int i = 1; i < nodeShortestPathTmp.size(); i++) {
                nodeShortestPath.add(nodeShortestPathTmp.get(i));
            }
        }
        for (Node node : nodeShortestPath) {
            System.out.println("Shortest Path: "+String.valueOf(node.x) + "," + String.valueOf(node.y));
        }
    }

    // Καθαρίζει προηγούμενες διαδρομές και ενημερώνει το Cloud με την σειρά των συντεταγμένων Path
    // του τρέχοντος σύντομου μονοπατιού.
    public void UploadShortestPathToCloud(){
        // Καθαρίζει όσα Path υπάρχουν στην βάση
        DatabaseReference databaseReference = MapsActivity.cloud.database.getReference("Path");
        if (databaseReference!=null) {
            databaseReference.removeValue();
        }
        for (Node node : nodeShortestPath) {
            // Create a record for path and add it on database
            DatabaseReference createNewPath = MapsActivity.cloud.database.getReference("Path");
            Path path = new Path(createNewPath.push().getKey(),
                            node.LatLngCenter.latitude,
                            node.LatLngCenter.longitude,
                            Double.valueOf(node.z));
            path.WriteRecord(createNewPath);
        }
    }

    public Node getNodeFromIndex(int index){
        int x = -1;
        int y = -1;
        if (MapsActivity.countUnitsHeight>0) {
            x = index/MapsActivity.countUnitsHeight;
            y = index-x*MapsActivity.countUnitsHeight;
        }
        if (x>=0 && x<=nodes.size() && y>=0 && y<= nodes.get(x).size()) {
            return nodes.get(x).get(y);
        }
        //System.out.println(String.valueOf(index)+":"+String.valueOf(x)+","+String.valueOf(y));
        return null;
    }
    public Parameter getParameter(int from_x, int from_y, int to_x, int to_y){
        int c_from = nodes.get(from_x).get(from_y).index();
        int c_to = nodes.get(to_x).get(to_y).index();
        for (Parameter parameter : MapsActivity.parameters) {
            if(parameter.c_from==c_from && parameter.c_to==c_to){
                return parameter;
            }
        }
        return null;
    }

    public void RotateDroneMarker(int i, LatLng latLng) {
        class RunnableWithParameter implements Runnable {
            LatLng latLng;
            int i;
            RunnableWithParameter(int i, LatLng latLng) { this.latLng = latLng; this.i = i; }
            public void run() {
            }
        }
        new Handler(Looper.getMainLooper()).post(new RunnableWithParameter(i, latLng) {
            @Override
            public void run() {
                Location oldLoc = new Location("Location from");
                oldLoc.setLatitude(MapsActivity.markers.get(i).getPosition().latitude);
                oldLoc.setLongitude(MapsActivity.markers.get(i).getPosition().longitude);
                Location newLoc = new Location("Location to");
                newLoc.setLatitude(latLng.latitude);
                newLoc.setLongitude(latLng.longitude);
                MapsActivity.markers.get(i).setRotation(oldLoc.bearingTo(newLoc));
            }
        });
    }
    public void MoveDroneMarker(int i, LatLng latLng) {
        class RunnableWithParameter implements Runnable {
            LatLng latLng;
            int i;
            RunnableWithParameter(int i, LatLng latLng) { this.latLng = latLng; this.i = i; }
            public void run() {
            }
        }
        new Handler(Looper.getMainLooper()).post(new RunnableWithParameter(i, latLng) {
            @Override
            public void run() {
                //Double a = angleFromTwoPoints(nodePrev.x, nodePrev.y, x, y);
                MapsActivity.markers.get(i).setPosition(latLng);
            }
        });
    }

}

