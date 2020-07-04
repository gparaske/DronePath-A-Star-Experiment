package com.example.dronepath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.solver.widgets.Rectangle;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.getExternalStorageDirectory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    //public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private LatLngBounds viewBounds; // The visible bounds of view Area

    public static Cloud cloud;

    private static Base base;
    private static Cell cell;
    private static Drone drone;
    private static int colorBlue = (50 & 0xff) << 24 | (0 & 0xff) << 16 | (150 & 0xff) << 8 | (255 & 0xff); // A & RGB
    private static int colorGreen = (50 & 0xff) << 24 | (0 & 0xff) << 16 | (255 & 0xff) << 8 | (150 & 0xff); // A & RGB
    private static int colorTarget = (125 & 0xff) << 24 | (0 & 0xff) << 16 | (150 & 0xff) << 8 | (255 & 0xff); // A & RGB
    //private static int colorTarget = (150 & 0xff) << 24 | (0 & 0xff) << 16 | (150 & 0xff) << 8 | (255 & 0xff); // A & RGB
    private static int colorWhite = (225 & 0xff) << 24 | (255 & 0xff) << 16 | (255 & 0xff) << 8 | (255 & 0xff); // A & RGB
    private static int colorPurple = (50 & 0xff) << 24 | (100 & 0xff) << 16 | (0 & 0xff) << 8 | (100 & 0xff); // A & RGB

    public static String DefaultScenario = "1";
    public static String DefaultAlgorithm = "1";
    public static String DefaultCell = "Microcell";
    public static int DefaultSignalStrength = 90;
    public static int DefaultRadius = 2000;
    public static int DefaultSqareDimension = 1000;// 1000: 1000m, 800: 800m
    public static Float DefaultSpeed = 20f; //5f;
    public static Double DefaultAngle = 270d;
    public static int DefaultRoute = 1;// 0: μόνο να πάει, 1: να πάει και να γυρίσει.

    // Array of Cells
    public ArrayList<Cell> cells = new ArrayList<>();
    public ArrayList<Circle> circles = new ArrayList<>();
    public int currentCell = 1;
    public int maxCell = 0;

    // Array of Drones
    public ArrayList<Drone> drones = new ArrayList<>();
    public static ArrayList<Marker> markers = new ArrayList<>();
    public int currentDrone = 1;
    public int maxDrone = 0;
    public int totalDrone = 5;

    // Array of Obstacle Squares
    public ArrayList<ObstacleSquare> obstacleSquares = new ArrayList<>();
    public int currentObstacleSquare = 1;
    public int maxObstacleSquare = 0;

    // Array of Parameter
    public static ArrayList<Parameter> parameters = new ArrayList<>();
    public int currentParameter = 1;
    public int maxParameter = 0;
    private boolean ResetScenario = false;

    // Array of Path
    public static ArrayList<Path> Paths = new ArrayList<>();
    public int currentPath = 1;
    public int maxPath = 0;

    public static FloatingActionButton fabplay;
    public static Boolean bPlaying = false;
    public Boolean bPaused = false;
    private AStarAlgorithm aStarAlgorithm;
    private DroneAlgorithm droneAlgorithm;
    private static Boolean bPurpleMarkerOn = false;
    private static Boolean bEraserMarkerOn = false;


    private static int iLoad = 0;
    // iLoad==3; base, cells and drones are Loaded

    private static int iStage = 0;
    // iStage==0: Before initialization disable the "Play" button
    // iStage==1: Initialization: Read all data from database and update Arraylists with cells, drones, etc
    // iStage==2: Scan the visible Area:

    LatLng TopLeft;
    LatLng BottomRight;
    Double distanceUnitWidth; // DefaultSqareDimension in meridian unit
    Double distanceUnitHeight; // DefaultSqareDimension in meridian unit
    public static int countUnitsWidth; // Number of squares in the area
    public static int countUnitsHeight; // Number of squares in the area
    private NodeManager nodeManager = new NodeManager();

    private static int fontSize;
    private static Typeface typeface;

    private static DatabaseReference databaseReferencePath;
    private static ValueEventListener valueEventListener;
    //ValueEventListener databaseReferenceParameterEL;
    /*@Override
    public void onStop() {
        databaseReferenceParameter.removeEventListener(databaseReferenceParameterEL);
        super.onStop();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MapsActivity.this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //init variables
        MapsActivity.iLoad = 0;
        MapsActivity.iStage = 0;
        MapsActivity.countUnitsWidth = 0;
        MapsActivity.countUnitsHeight = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fontSize = getFontSize(0.0100f); // get a font size as a rate of screen width
        typeface = FileStreamTypeface(R.raw.itcclearface);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //this.getActionBar().setSubtitle("aaaa");
        //((AppCompatActivity)getActivity()).setSupportActionBar(mractionbar);
        //(AppCompatActivity)getActionBar().setSubtitle("aaaa");
//        setActionBar(toolbar);
//        (MapsActivity) setActionBar(toolbar);
//        ((MapsActivity)setActionBar(toolbar)).setSupportActionBar(toolbar);
//        ((MapsActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle();
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Disable the other buttons
                bEraserMarkerOn = false;

                if (iStage<2) {
                    Snackbar.make(view, "Please wait until area definition completed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } else {
                    //if (marker==null) {
                        Snackbar.make(view, "Paint tool to add Delay (-1 m/s) is Selected", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    //} else {
                        //bPurpleMarkerOn = true; //todo: remove remarks in production
                    // Το delay είναι μόνο επειδή γίνεται πολύ γρήγορα.
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // Actions to do after 2 seconds
                            Toast.makeText(MapsActivity.this, "Disabled for demo", Toast.LENGTH_LONG).show();
                            NextStage();
                        }
                    }, 1000);
/*
                    // Add New Cell
                    mMap.addCircle(new CircleOptions()
                            .center(marker.getPosition())
                            .radius(DefaultRadius) // meters
                            .strokeWidth(0f)
                            .fillColor(colorGreen));
*/
/*
                    // Create a record for Cell and add it on database
                    DatabaseReference createNewCell = cloud.database.getReference("cell");;
                    cell = new Cell(createNewCell.push().getKey(),
                            marker.getPosition().latitude,
                            marker.getPosition().longitude,
                            Double.valueOf(DefaultRadius),
                            Double.valueOf(DefaultSignalStrength));
                    cell.WriteRecord(createNewCell);
*/
                    //}
                }
            }
        });

        FloatingActionButton fab_obstacle = findViewById(R.id.fabobstacle);
        fab_obstacle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Disable the other buttons
                bPurpleMarkerOn = false;

                if (iStage<2) {
                    Snackbar.make(view, "Please wait until area definition completed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Eraser tool to remove Delay (+1 m/s) is Selected", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //bEraserMarkerOn = true; //todo: remove remarks in production
                    // Το delay είναι μόνο επειδή γίνεται πολύ γρήγορα.
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // Actions to do after 2 seconds
                            Toast.makeText(MapsActivity.this, "Disabled for demo", Toast.LENGTH_LONG).show();
                            NextStage();
                        }
                    }, 1000);

/*
                    // Create a record for Obstacle and add it on database
                    DatabaseReference createNewObstacleSquare = cloud.database.getReference("obstacleSquare");;
                    ObstacleSquare obstacleSquare = new ObstacleSquare(createNewObstacleSquare.push().getKey(),
                            4,
                            3,
                            Double.valueOf(2));
                    obstacleSquare.WriteRecord(createNewObstacleSquare);
*/
                }
            }
        });


        fabplay = findViewById(R.id.fabplay);
        fabplay.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   // Disable the other buttons
                   bPurpleMarkerOn = false;
                   bEraserMarkerOn = false;

                   if (bPlaying) {
                       fabplay.setImageResource(android.R.drawable.ic_media_play); bPlaying = false;
                       switch (iStage){
                           case 3: // it was executing A* Algorithm and you press pause
                               Snackbar.make(view, "Paused", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();
                               bPaused = true;
                               aStarAlgorithm.onPause();
                               break;
                           case 7: // it was executing Drone Algorithm and you press pause
                               Snackbar.make(view, "Paused", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();
                               bPaused = true;
                               droneAlgorithm.onPause();
                               break;
                       }
                   } else {
                       switch (iStage){
                           // iStage==0: Before initialization disable the "Play" button
                           case 0:
                               Snackbar.make(view, "Please wait until load from cloud completed", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();
                               break;
                           // iStage==1: Read all data from database and update Arraylists with cells, drones, etc
                           case 1:
                               Snackbar.make(view, "Initialization", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();
                               fabplay.setImageResource(android.R.drawable.ic_media_pause); bPlaying = true;
                               DroneInitialization();
                               NextStage();
                               break;
                           case 2:
                               Snackbar.make(view, "Define the Area to Scan", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();
                               fabplay.setImageResource(android.R.drawable.ic_media_pause); bPlaying = true;
                               DefineScanArea();
                               DefineSquares();
                               NextStage();
                               break;
                           case 3:
                               Snackbar.make(view, "A* Algorithm Search", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();
                               fabplay.setImageResource(android.R.drawable.ic_media_pause); bPlaying = true;
                               if(bPaused) {
                                   bPaused = false;
                                   aStarAlgorithm.onResume();
                               } else {
                                   aStarAlgorithm = new AStarAlgorithm(nodeManager);
                                   new Thread(aStarAlgorithm).start();
                               }
                               break;
                           case 4:
                               Snackbar.make(view, "Upload Shortest Path to Cloud", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();
                               nodeManager.UploadShortestPathToCloud(); // todo: remove mark for production
                               // Εδώ θα φτάσει μόνο όταν έχει ολοκληρωθεί και το τελευταίο upload.
                               // Το delay είναι μόνο επειδή γίνεται πολύ γρήγορα.
                               Handler handler = new Handler();
                               handler.postDelayed(new Runnable() {
                                   public void run() {
                                       // Actions to do after 2 seconds
                                       Toast.makeText(MapsActivity.this, "Upload Completed.", Toast.LENGTH_LONG).show();
                                       NextStage();
                                   }
                               }, 2000);
                               break;
                           case 5:
                               Snackbar.make(view, "Drone: Loading Route", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();
                               DroneLoadRoute();
                               NextStage();
                               break;
                           case 6:
                               Snackbar.make(view, "Please wait until load from cloud completed", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();
                               break;
                           case 7:
                               databaseReferencePath.removeEventListener(valueEventListener);
                               Snackbar.make(view, "Drone: Begin Route", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();
                               fabplay.setImageResource(android.R.drawable.ic_media_pause); bPlaying = true;
                               if(bPaused) {
                                   bPaused = false;
                                   droneAlgorithm.onResume();
                               } else {
                                   droneAlgorithm = new DroneAlgorithm(nodeManager);
                                   new Thread(droneAlgorithm).start();
                               }
                               break;
                           case 8:
                               Double dTime = nodeManager.ResultInSeconds();
                               int iTime = (int) Math.round(dTime);
                               int iMin = (int)(iTime/60);
                               int iSec = iTime-iMin*60;
                               MessageBox("Συνολικός Χρόνος Διαδρομής", String.valueOf(iMin)+" min "+String.valueOf(iSec)+" sec");
/*
                               Snackbar.make(view, "Scan the visible Area", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();
                               fabplay.setImageResource(android.R.drawable.ic_media_pause); bPlaying = true;
*/
                               //DroneInitialization(); viewBounds
                               //NextStage();
                               break;

                       }

//                       mMap.addMarker(
//                               {
//                               position: map.getCenter(),
//                               map: map,
//                               title: "MY MARKER",
//                               icon: {
//                           path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
//                                   scale: 4,
//                                   strokeColor: '#00F',
//                                   rotation: 0,
//                       }}
//                       );
                   }
               }
           }
        );


        cloud = new Cloud();
        cloud.loginAccount("gtparas@gmail.com", "removed");
        cloud.setListener(new Cloud.ChangeListener() {
            @Override
            public void onSuccessfulLogin() {
/*                // Create a record for base table and add it on database
                DatabaseReference createNewBase = cloud.database.getReference("base");;
                String key = createNewBase.push().getKey();
                base = new Base(key,
                        "Marathon",
                        38.15708075,
                        23.95799518,
                        49.0,
                        30f,
                        2f,
                        1.8f,
                        18f,
                        79f,
                        4f,
                        3.6f,
                        38.03999658,
                        23.94802809,
                        1000.0);
                base.WriteRecord(createNewBase);*/

                // Διάβασε τα δεδομένα από την Βάση
                DatabaseReference mDatabase = cloud.database.getReference("base");
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for(DataSnapshot child:children){
                            base = new Base(child);
                            System.out.println(base.Print());

                            mMap.addMarker(new MarkerOptions().position(new LatLng(base.latitude, base.longitude)).title(base.name).icon(BitmapDescriptorFactory.fromResource(R.drawable.home)));
                            mMap.addMarker(new MarkerOptions().position(new LatLng(base.target_latitude, base.target_longitude)).title("Moni Daou Pentelis"));
                            mMap.addCircle(new CircleOptions()
                                    .clickable(false)
                                    .center(new LatLng(base.target_latitude, base.target_longitude))
                                    .radius(base.target_radius) // meters
                                    .strokeWidth(0f)
                                    .fillColor(colorBlue));


                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(new LatLng(base.latitude, base.longitude));
                            builder.include(new LatLng(base.target_latitude, base.target_longitude));
                            LatLngBounds bounds = builder.build();
                            // get display pixels
                            Display display = getWindowManager().getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);
                            int width = size.x;
                            int height = size.y;

                            int padding = (int) width/3; // offset from edges of the map in pixels
                            //int padding = 0; // offset from edges of the map in pixels

                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.animateCamera(cu);


                        }
                        iLoad++;
                        // iStage==1: Read all data from database and update Arraylists with cells, drones, etc
                        if(iLoad==4 & iStage==0){
                            Toast.makeText(MapsActivity.this, "Load Completed.", Toast.LENGTH_LONG).show();
                            iStage++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

/*
                // Διάβασε τις κυψέλες από την βάση
                DatabaseReference databaseReferenceCell = cloud.database.getReference("cell");
                databaseReferenceCell.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //καθαρίζει το arraylist
                        cells.clear(); maxCell = 0;
                        //καθαρίζει τους κύκλους των κυψελών από τον χάρτη
                        for(Circle circle:circles){
                            circle.remove();
                        }
                        circles.clear();

                        //για κάθε DataSnapshot ελέγχει τα κριτήρια εφόσον υπάρξουν (αρχικά δεν υπάρχουν)
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for(DataSnapshot child:children){
                            Cell cell = new Cell(child);
                            System.out.println(cell.Print());

                            // Update the Array of Cells
                            cells.add(cell);
                            maxCell += 1;

                            // Show Cell
                            circles.add(
                                    mMap.addCircle(new CircleOptions()
                                    .clickable(false)
                                    .center(new LatLng(cell.latitude, cell.longitude))
                                    .radius(cell.radius) // meters
                                    .strokeWidth(0f)
                                    .fillColor(colorGreen))
                            );
                        }

                        SortCells(); //by longitude
                        if(currentCell<=cells.size()){
                            //txtBody.setText(cells.get(currentCell-1).body);
*/
/*
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(details.get(currentDetail-1).latitude, details.get(currentDetail-1).longitude)) // Sets the center of the map to detail's possition
                                    .zoom(details.get(currentDetail-1).zoom) // Sets the zoom
                                    .build();                                // Creates a CameraPosition from the builder
*//*

                            //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        } else {
                            //txtBody.setText("New");
                        }
                        iLoad++;
                        // iStage==1: Read all data from database and update Arraylists with cells, drones, etc
                        if(iLoad==4 & iStage==0){
                            Toast.makeText(MapsActivity.this, "Load Completed.", Toast.LENGTH_LONG).show();
                            iStage++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
*/

                // Διάβασε τα Drones από την Βάση
                DatabaseReference databaseReferenceDrone = cloud.database.getReference("drone");
                databaseReferenceDrone.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //καθαρίζει το arraylist
                        drones.clear(); maxDrone = 0;
                        //καθαρίζει τους markers των drones από τον χάρτη
                        for(Marker marker:markers){
                            marker.remove();
                        }
                        markers.clear();

                        //για κάθε DataSnapshot ελέγχει τα κριτήρια εφόσον υπάρξουν (αρχικά δεν υπάρχουν)
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for(DataSnapshot child:children){
                            Drone drone = new Drone(child);
                            System.out.println(drone.Print());

                            // Update the Array of Drones
                            drones.add(drone);
                            maxDrone += 1;

                            // Show Drone
                            drone.marker = mMap.addMarker(new MarkerOptions().position(new LatLng(drone.latitude, drone.longitude))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
                                    .anchor(0.5f, 0.5f)
                                    .flat(true)
                                    .rotation(FixAngleRealToIcon(drone.angle))
                            );
                            markers.add(drone.marker);
                            //markers.get(maxDrone-1).setRotation(FixAngleRealToIcon(drone.angle));
                        }
                        iLoad++;
                        // iStage==1: Read all data from database and update Arraylists with cells, drones, etc
                        if(iLoad==4 & iStage==0){
                            Toast.makeText(MapsActivity.this, "Load Completed.", Toast.LENGTH_LONG).show();
                            iStage++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // Διάβασε τα Obstacle Squares από την Βάση
                DatabaseReference databaseReferenceObstacleSquare = cloud.database.getReference("obstacleSquare");
                databaseReferenceObstacleSquare.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //καθαρίζει το arraylist
                        obstacleSquares.clear(); maxObstacleSquare = 0;
                        //καθαρίζει τους markers των drones από τον χάρτη
                        for (int x=0; x<nodeManager.max_x; x++) {
                            for (int y = 0; y < nodeManager.max_y; y++) {
                                if(nodeManager.getNode(x,y).polygon.getFillColor()==colorPurple){
                                    nodeManager.getNode(x,y).polygon.setFillColor(0);
                                }
                            }
                        }

                        //για κάθε DataSnapshot ελέγχει τα κριτήρια εφόσον υπάρξουν (αρχικά δεν υπάρχουν)
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for(DataSnapshot child:children){
                            ObstacleSquare obstacleSquare = new ObstacleSquare(child);
                            System.out.println(obstacleSquare.Print());

                            // Update the Array of ObstacleSquares
                            obstacleSquares.add(obstacleSquare);
                            maxObstacleSquare += 1;

                            // Show ObstacleSquare
                            try {
                                nodeManager.getNode(obstacleSquare.x,obstacleSquare.y).polygon.setFillColor(colorPurple);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        iLoad++;
                        // iStage==1: Read all data from database and update Arraylists with cells, drones, etc
                        if(iLoad==4 & iStage==0){
                            Toast.makeText(MapsActivity.this, "Load Completed.", Toast.LENGTH_LONG).show();
                            iStage++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // Διάβασε τον Παραμετρικό από την Βάση
                DatabaseReference databaseReferenceParameter;
                switch(DefaultScenario) {
                    case "1":
                        databaseReferenceParameter = cloud.database.getReference("Parameter1");
                        break;
                    case "2":
                        databaseReferenceParameter = cloud.database.getReference("Parameter2");
                        break;
                    default:
                        databaseReferenceParameter = cloud.database.getReference("Parameter");
                        break;
                }
                databaseReferenceParameter.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //καθαρίζει το arraylist
                        parameters.clear(); maxParameter = 0;

                        //για κάθε DataSnapshot ελέγχει τα κριτήρια εφόσον υπάρξουν (αρχικά δεν υπάρχουν)
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for(DataSnapshot child:children){
                            Parameter parameter = new Parameter(child);

                            Node n = nodeManager.getNodeFromIndex(parameter.c_to);
                            if (n!=null){
                                // Προσοχή! Όταν αλλάζουμε τιμές speed από την κονσόλα εμφανίζει πάντα τον τελευταίο κόμβο αλλά μπορεί από άλλες πλευρές να είναι άλλη η ταχύτητα.
                                // Η αλλαγή θα πρέπει να γίνεται μόνο από την εφαρμογή αν θέλουμε να βλέπουμε σωστά τα χρώματα.
                                System.out.println("from "+String.valueOf(parameter.c_from)+" to "+String.valueOf(parameter.c_to)+" purple is " + String.valueOf(250-(int)(50*parameter.speed))+" for ("+String.valueOf(n.x)+","+String.valueOf(n.y)+")");
                                n.purple = 250-(int)(50*parameter.speed);
                                nodeManager.nodes.get(n.x).get(n.y).purple = n.purple;
                                nodeManager.nodes.get(n.x).get(n.y).setColor((n.purple & 0xff) << 24 | (n.purple & 0xff) << 16 | (0 & 0xff) << 8 | (n.purple & 0xff));// A & RGB
                            }

                            // Update the Array of Parameter
                            parameters.add(parameter);
                            System.out.println(parameter.Print());
                            maxParameter += 1;
                        }
                        iLoad++;
                        // iStage==1: Read all data from database and update Arraylists with cells, drones, etc
                        if(iLoad==4 & iStage==0){
                            Toast.makeText(MapsActivity.this, "Load Completed.", Toast.LENGTH_LONG).show();
                            iStage++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }

/*    private void SortCells() {
        Collections.sort(cells, new Comparator<Cell>() {
            @Override
            public int compare(Cell o1, Cell o2)
            {
                return (int) (o1.longitude - o2.longitude);
            }
        });
    }*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings config = mMap.getUiSettings();
        config.setMapToolbarEnabled(false);
        config.setZoomControlsEnabled(false);

/*        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                int x = Math.abs((int)((TopLeft.longitude-polyline.getPoints().get(0).longitude)/distanceUnitWidth));
                int y = Math.abs((int)((TopLeft.latitude-polyline.getPoints().get(0).latitude)/distanceUnitHeight));
                System.out.println("1: You click on ("+String.valueOf(x)+", "+String.valueOf(y)+")");
            }
        });
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                int x = Math.abs((int)((TopLeft.longitude-polygon.getPoints().get(0).longitude)/distanceUnitWidth));
                int y = Math.abs((int)((TopLeft.latitude-polygon.getPoints().get(0).latitude)/distanceUnitHeight));
                System.out.println("2: You click on ("+String.valueOf(x)+", "+String.valueOf(y)+")");
            }
        });*/

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (marker==null) {
                    marker = mMap.addMarker( new MarkerOptions().position(point).title(
                            "latitude:"+String.valueOf(String.format("%.7f",point.latitude)) + ", " +
                             "longitude:"+String.valueOf(String.format("%.7f",point.longitude))
                    ));
                } else {
                    marker.setPosition(point);
                    marker.setTitle(
                            "latitude:"+String.valueOf(String.format("%.7f",point.latitude)) + ", " +
                            "longitude:"+String.valueOf(String.format("%.7f",point.longitude))
                    );
                }
                onClick(point);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                viewBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                onClick(new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude));
            }
        });

/*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
*/

    }

    private void onClick(LatLng latLng){
        if (bPurpleMarkerOn || bEraserMarkerOn) {
            int x = Math.abs((int)((TopLeft.longitude-latLng.longitude)/distanceUnitWidth));
            int y = Math.abs((int)((TopLeft.latitude-latLng.latitude)/distanceUnitHeight));
            System.out.println("You click on ("+String.valueOf(x)+", "+String.valueOf(y)+")");
            if (x>=0 & y>=0 & x<=nodeManager.max_x & y<=nodeManager.max_y){
                // Change the speed for the parameter c_to
                int index = nodeManager.getNode(x,y).index();
                DatabaseReference mDatabase;// = cloud.database.getReference("Parameter");
                switch(DefaultScenario) {
                    case "1":
                        mDatabase = cloud.database.getReference("Parameter1");
                        break;
                    case "2":
                        mDatabase = cloud.database.getReference("Parameter2");
                        break;
                    default:
                        mDatabase = cloud.database.getReference("Parameter");
                        break;
                }
                Map<String, Object> childUpdates = new HashMap<>();
                for (Parameter parameter : parameters) {
                    if (parameter.c_to==index){
                        if(bPurpleMarkerOn && parameter.speed>0) {
                            parameter.speed-=1;
                            HashMap<String, Object> detailTmp = parameter.toHashMap();
                            childUpdates.put("/" + parameter.key, detailTmp);
                        }
                        if(bEraserMarkerOn && parameter.speed<DefaultSpeed) {
                            parameter.speed+=1;
                            HashMap<String, Object> detailTmp = parameter.toHashMap();
                            childUpdates.put("/" + parameter.key, detailTmp);
                        }
                    }
                }
                mDatabase.updateChildren(childUpdates);


/*
                nodeManager.getNode(x,y).purple += 50;
                if (nodeManager.getNode(x,y).purple > 255) {nodeManager.getNode(x,y).purple=255;}
                nodeManager.getNode(x,y).setColor((nodeManager.getNode(x,y).purple & 0xff) << 24 | (nodeManager.getNode(x,y).purple & 0xff) << 16 | (0 & 0xff) << 8 | (nodeManager.getNode(x,y).purple & 0xff));// A & RGB
*/
            }
        }
    }

    // Το default του εικονιδίου είναι προς τα πάνω ενώ εμείς θέλουμε να είναι προς τα κάτω
    private static Float FixAngleRealToIcon(Float realangle){ return 90f-realangle; }

    private void DeleteExceptOne(){
        DatabaseReference databaseReference = cloud.database.getReference("drone");;
        for (int i=2; i<=maxDrone; i++){
            databaseReference.child(drones.get(i-1).key).removeValue();
        }

    }

    // Reset All Drones Or Create #totalDrone number of drones in database
    private void DroneInitialization(){
        DatabaseReference databaseReference = cloud.database.getReference("drone");;
        Map<String, Object> childUpdates = new HashMap<>();
        for (int i=1; i<=maxDrone; i++){
            drones.get(i-1).role = 0;
            drones.get(i-1).ready = 1;
            drones.get(i-1).flying = 0;
            drones.get(i-1).battery = 99f;
            drones.get(i-1).latitude = base.latitude;
            drones.get(i-1).longitude = base.longitude;
            drones.get(i-1).angle = 270f;
            drones.get(i-1).target_latitude = 0.0;
            drones.get(i-1).target_longitude = 0.0;
            drones.get(i-1).target_radius = 0.0;
            drones.get(i-1).timestamp = new Timestamp(System.currentTimeMillis());
            HashMap<String, Object> detailTmp = drones.get(i-1).toHashMap();
            childUpdates.put("/" + drones.get(i-1).key, detailTmp);
        }
        databaseReference.updateChildren(childUpdates);
        Toast.makeText(MapsActivity.this, "Drone Reset.", Toast.LENGTH_LONG).show();

        if(maxDrone<totalDrone){
            int totalNewDrones = totalDrone - maxDrone;
            DatabaseReference createNewDrone = cloud.database.getReference("drone");;
            for (int i=1; i<=totalNewDrones; i++){
                // Create a record for drone table and add it on database
                String keyDrone = createNewDrone.push().getKey();
                drone = new Drone(keyDrone,
                        0,
                        1,
                        0,
                        99f,
                        base.latitude,
                        base.longitude,
                        270f,
                        0.0,
                        0.0,
                        0.0,
                        new Timestamp(System.currentTimeMillis()));
                drone.WriteRecord(createNewDrone);
            }
            Toast.makeText(MapsActivity.this, String.valueOf(totalNewDrones) + " drones Created.", Toast.LENGTH_LONG).show();
        }
    }

    // Define Rectangular Area to Scan (bordered by screen)
    private void DefineScanArea(){
        Double dWidth = Math.abs(base.longitude - base.target_longitude);
        Double dHeight = Math.abs(base.latitude - base.target_latitude);
        Double dScreenWidth = Math.abs(viewBounds.southwest.longitude-viewBounds.northeast.longitude);
        Double dScreenHeight = Math.abs(viewBounds.southwest.latitude-viewBounds.northeast.longitude);
        Double dAddWidth = 0d;
        Double dAddHeight = 0d;

        if (dHeight<dWidth){
            if(dWidth<dScreenHeight){
                dAddHeight = (dWidth-dHeight)/2;
            } else {
                dAddHeight = (dScreenHeight-dHeight)/2;
            }
        }
        if (dWidth<dHeight){
            if (dHeight<dScreenWidth){
                dAddWidth = (dHeight-dWidth)/2;
            } else {
                dAddWidth = (dScreenWidth-dWidth)/2;
            }
        }
        if (base.longitude<base.target_longitude) {
            dAddWidth *= -1;
        }
        if (base.target_latitude<base.latitude) {
            dAddHeight *= -1;
        }
        if(base.latitude-dAddHeight>base.target_latitude+dAddHeight) {
            if(base.longitude+dAddWidth<base.target_longitude-dAddWidth){
                TopLeft = new LatLng(base.latitude-dAddHeight, base.longitude+dAddWidth);
            } else {
                TopLeft = new LatLng(base.latitude-dAddHeight, base.target_longitude-dAddWidth);
            }
        } else {
            if(base.longitude+dAddWidth<base.target_longitude-dAddWidth){
                TopLeft = new LatLng(base.target_latitude+dAddHeight, base.longitude+dAddWidth);
            } else {
                TopLeft = new LatLng(base.target_latitude+dAddHeight, base.target_longitude-dAddWidth);
            }
        }
        if(base.latitude-dAddHeight<base.target_latitude+dAddHeight) {
            if(base.longitude+dAddWidth>base.target_longitude-dAddWidth){
                BottomRight = new LatLng(base.latitude-dAddHeight, base.longitude+dAddWidth);
            } else {
                BottomRight = new LatLng(base.latitude-dAddHeight, base.target_longitude-dAddWidth);
            }
        } else {
            if(base.longitude+dAddWidth>base.target_longitude-dAddWidth){
                BottomRight = new LatLng(base.target_latitude+dAddHeight, base.longitude+dAddWidth);
            } else {
                BottomRight = new LatLng(base.target_latitude+dAddHeight, base.target_longitude-dAddWidth);
            }
        }

        /* Μετέτρεψε τις διαστάσεις σύμφωνα με την μονάδα μέτρησης. Default = 1000 meters */
        Double distanceMetersWidth;
        Double distanceMetersHeight;

        float[] fdist = new float[1];
        Double distanceRadians;
        distanceRadians = BottomRight.longitude-TopLeft.longitude;
        Location.distanceBetween(TopLeft.latitude, TopLeft.longitude, TopLeft.latitude, BottomRight.longitude, fdist);
        distanceMetersWidth = Double.valueOf(fdist[0]);
        distanceUnitWidth = distanceRadians*DefaultSqareDimension/distanceMetersWidth;
        countUnitsWidth = (int) Math.round(distanceMetersWidth/DefaultSqareDimension);
        distanceRadians = BottomRight.latitude-TopLeft.latitude;
        Location.distanceBetween(TopLeft.latitude, TopLeft.longitude, BottomRight.latitude, TopLeft.longitude, fdist);
        distanceMetersHeight = Double.valueOf(fdist[0]);
        distanceUnitHeight = distanceRadians*DefaultSqareDimension/distanceMetersHeight;
        countUnitsHeight = (int) Math.round(distanceMetersHeight/DefaultSqareDimension);

        BottomRight = new LatLng(TopLeft.latitude+distanceUnitHeight * countUnitsHeight, TopLeft.longitude+distanceUnitWidth * countUnitsWidth);

        // Τώρα μπορούμε να βρούμε τις συντεταγμένες της βάσης και του target στην περιοχή
        nodeManager.base_x = Math.abs((int)((TopLeft.longitude-base.longitude)/distanceUnitWidth));
        nodeManager.base_y = Math.abs((int)((TopLeft.latitude-base.latitude)/distanceUnitHeight));
        nodeManager.target_x = Math.abs((int)((TopLeft.longitude-base.target_longitude)/distanceUnitWidth));
        nodeManager.target_y = Math.abs((int)((TopLeft.latitude-base.target_latitude)/distanceUnitHeight));
        System.out.println("base ("+String.valueOf(nodeManager.base_x+1)+", "+String.valueOf(nodeManager.base_y+1)+")");
        System.out.println("target ("+String.valueOf(nodeManager.target_x+1)+", "+String.valueOf(nodeManager.target_y+1)+")");
        nodeManager.max_x = countUnitsWidth;
        nodeManager.max_y = countUnitsHeight;

/*
        mMap.addMarker( new MarkerOptions().position(TopLeft).title("TopLeft"));
        mMap.addMarker( new MarkerOptions().position(BottomRight).title("BottomRight"));
        mMap.addMarker( new MarkerOptions().position(new LatLng(TopLeft.latitude, BottomRight.longitude)).title("TopRight"));
        mMap.addMarker( new MarkerOptions().position(new LatLng(BottomRight.latitude, TopLeft.longitude)).title("BottomLeft"));
*/
        PolylineOptions rectOptions = new PolylineOptions()
                .add(new LatLng(TopLeft.latitude, TopLeft.longitude))
                .add(new LatLng(TopLeft.latitude, BottomRight.longitude))
                .add(new LatLng(BottomRight.latitude, BottomRight.longitude))
                .add(new LatLng(BottomRight.latitude, TopLeft.longitude))
                .add(new LatLng(TopLeft.latitude, TopLeft.longitude));
        rectOptions.clickable(false);
/*
        PolylineOptions rectOptions = new PolylineOptions()
                .add(new LatLng(base.latitude-dAddHeight, base.longitude+dAddWidth))
                .add(new LatLng(base.target_latitude+dAddHeight, base.longitude+dAddWidth))  // North of the previous point, but at the same longitude
                .add(new LatLng(base.target_latitude+dAddHeight, base.target_longitude-dAddWidth))  // Same latitude, and 30km to the west
                .add(new LatLng(base.latitude-dAddHeight, base.target_longitude-dAddWidth))  // Same longitude, and 16km to the south
                .add(new LatLng(base.latitude-dAddHeight, base.longitude+dAddWidth)); // Closes the polyline.
*/
        Polyline polyline = mMap.addPolyline(rectOptions.color(colorBlue));
    }

    /*
    private function calcBounds(center, size) {
        var n = google.maps.geometry.spherical.computeOffset(center, size.height/2, 0).lat(),
                s = google.maps.geometry.spherical.computeOffset(center, size.height/2, 180).lat(),
                e = google.maps.geometry.spherical.computeOffset(center, size.width/2, 90).lng(),
                w = google.maps.geometry.spherical.computeOffset(center, size.width/2, 270).lng();
        return new google.maps.LatLngBounds(new google.maps.LatLng(s,w),
                new google.maps.LatLng(n,e))
    }
    */

    // Define Squares on the Area to Scan
    private void DefineSquares(){
        int count=0;
        LatLng CurrentTopLeft;
        for (int x=0; x<nodeManager.max_x; x++){
            ArrayList<Node> nodes_x = new ArrayList<>(); // create a new node arraylist for current x
            CurrentTopLeft = new LatLng(TopLeft.latitude, TopLeft.longitude + distanceUnitWidth * x);
            for (int y=0; y<nodeManager.max_y; y++){
                Node node = new Node(x, y); // create a node for current y
                CurrentTopLeft = new LatLng(TopLeft.latitude+ distanceUnitHeight * y, CurrentTopLeft.longitude );
                PolygonOptions rectOptions = new PolygonOptions()
                        .add(new LatLng(CurrentTopLeft.latitude, CurrentTopLeft.longitude))
                        .add(new LatLng(CurrentTopLeft.latitude, CurrentTopLeft.longitude+distanceUnitWidth))
                        .add(new LatLng(CurrentTopLeft.latitude+distanceUnitHeight, CurrentTopLeft.longitude+distanceUnitWidth))
                        .add(new LatLng(CurrentTopLeft.latitude+distanceUnitHeight, CurrentTopLeft.longitude));
                rectOptions.clickable(false);
                Polygon polygon;
                if ((x==nodeManager.base_x & y==nodeManager.base_y) || (x==nodeManager.target_x & y==nodeManager.target_y)) {
                    polygon = mMap.addPolygon(rectOptions.strokeColor(colorBlue)
                            .fillColor(colorTarget));
                } else {
                    polygon = mMap.addPolygon(rectOptions.strokeColor(colorBlue));
                }
                node.setPolygon(polygon);
                if (!parameters.isEmpty()){
                    if (parameters.get(FindParameter(x,y)).speed<DefaultSpeed) {
                        node.purple = 250-(int)(50*parameters.get(FindParameter(x,y)).speed);
                        node.setColor((node.purple & 0xff) << 24 | (node.purple & 0xff) << 16 | (0 & 0xff) << 8 | (node.purple & 0xff));// A & RGB
                    }
                }
                node.LatLngCenter = new LatLng(CurrentTopLeft.latitude+distanceUnitHeight/2, CurrentTopLeft.longitude+distanceUnitWidth/2);
                node.textMarker = mMap.addMarker(new MarkerOptions().position(node.LatLngCenter).icon(createTextIcon(" ")));
                //showText(nodes.get(x).get(y), String.valueOf(String.format("%.2f",nodes.get(x).get(y).f)));
                nodeManager.showText(node, String.valueOf(count));
                count++;
                nodes_x.add(node); // add the y node to current x arraylist
            }
            nodeManager.nodes.add(nodes_x); // add the current x arraylist to the nodes arraylist
        }

        // Μόνο την πρώτη φορά
        if (parameters.isEmpty() || ResetScenario) {
            // Καθαρίζει όσα Parameter υπάρχουν στην βάση
            DatabaseReference databaseReference;// = cloud.database.getReference("Parameter");
            switch(DefaultScenario) {
                case "1":
                    databaseReference = cloud.database.getReference("Parameter1");
                    break;
                case "2":
                    databaseReference = cloud.database.getReference("Parameter2");
                    break;
                default:
                    databaseReference = cloud.database.getReference("Parameter");
                    break;
            }
            //for (int i=1; i<=maxParameter; i++){
            //    databaseReference.child(parameters.get(i-1).key).removeValue();
            //}
            if (databaseReference!=null) {
                databaseReference.removeValue();
            }
            nodeManager.UploadNodesToCloud();
        }

        for(ObstacleSquare obstacleSquare:obstacleSquares) {
            nodeManager.getNode(obstacleSquare.x, obstacleSquare.y).color = colorPurple;
            nodeManager.getNode(obstacleSquare.x, obstacleSquare.y).polygon.setFillColor(colorPurple);
            nodeManager.getNode(obstacleSquare.x, obstacleSquare.y).weight = obstacleSquare.weight;
        }
    }

    private void DroneLoadRoute(){
        // Διάβασε το Path από την Βάση (Δεν ανήκει στο βασικό Load. Αποτελεί κομμάτι της
        // εφαρμογής πάνω στο Drone.)
        databaseReferencePath = cloud.database.getReference("Path");
        valueEventListener = databaseReferencePath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //καθαρίζει το arraylist
                Paths.clear(); maxPath = 0;

                //για κάθε DataSnapshot ελέγχει τα κριτήρια εφόσον υπάρξουν (αρχικά δεν υπάρχουν)
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child:children){
                    Path path = new Path(child);
                    System.out.println(path.Print());

                    // Update the Array of Path
                    Paths.add(path);
                    maxPath += 1;
                }

                Toast.makeText(MapsActivity.this, "Load Completed.", Toast.LENGTH_LONG).show();
                NextStage();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int FindParameter(int x, int y){
        int index = -1;
        Float minSpeed = DefaultSpeed;
        Node n = new Node(x,y);
        int i = 0;
        for (Parameter parameter : parameters) {
            if (parameter.c_to==n.index()){
                //Αν βρει παραμετρικό που να έχει μικρότερο από την default speed το κρατάει ως σημαντικότερο
                if (parameter.speed<=minSpeed) {
                    index = i;
                }
            }
            i++;
        }
        return index;
    }

    public static BitmapDescriptor createTextIcon(String text) {
        Paint paint = new Paint();
        paint.setTypeface(typeface);
        paint.setTextSize(fontSize);

        float textWidth = paint.measureText(text);
        float textHeight = paint.getTextSize();
        int width = (int) (textWidth);
        int height = (int) (textHeight);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        paint.setColor(Color.WHITE);
        drawCenterText(canvas, paint, text);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(image);
        return icon;
    }

    public static void drawCenterText(Canvas canvas, Paint paint, String text) {
        Rect r = new Rect();
        String[] aText = text.split("/n");
        int lineHeight = -1;
        for (int i = 0; i < aText.length; i++) {
            if(aText[i]!=""){
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.getClipBounds(r);
                int cHeight = r.height();
                int cWidth = r.width();
                paint.getTextBounds(aText[i],0,aText[i].length(),r);
                float x = cWidth/2f-r.width()/2f-r.left;
                float y = cHeight/2f+r.height()/2f-r.bottom;

                Paint paint2 = new Paint();
                paint2.setTypeface(paint.getTypeface());
                paint2.setTextSize(paint.getTextSize());
                paint2.setColor(Color.GRAY);
                paint2.setStyle(Paint.Style.STROKE);
                paint2.setStrokeWidth(2);

                Paint paint3 = new Paint();
                paint3.setTypeface(paint.getTypeface());
                paint3.setTextSize(paint.getTextSize());
                paint3.setColor(Color.BLACK);
                paint3.setStyle(Paint.Style.STROKE);
                paint3.setStrokeWidth(6);

                if(lineHeight==-1){lineHeight=r.height();}
                canvas.drawText(aText[i], x, y+i*lineHeight, paint3);
                canvas.drawText(aText[i], x, y+i*lineHeight, paint2);
                canvas.drawText(aText[i], x, y+i*lineHeight, paint);
            }
        }
    }

    public int getFontSize(float size) {
        Typeface typeface;
        typeface = FileStreamTypeface(R.raw.itcclearface);
        Paint paint = new Paint();
        paint.setTypeface(typeface);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int SCREEN_HEIGHT = dm.heightPixels;

        Rect r = new Rect();
        float scale = 0;
        int fontSize = 23;
        while (scale<size) {
            fontSize++;
            paint.setTextSize(fontSize);
            paint.getTextBounds("A",0,1,r);
            scale = (float)(r.width())/(float)(SCREEN_HEIGHT);
        }
        return fontSize;
    }

    private Typeface FileStreamTypeface(int resource){
        Typeface tf = null;

        InputStream is = getResources().openRawResource(resource);
        String path = getFilesDir().getAbsolutePath();
        File f = new File(path);
        if (!f.exists())
        {
            if (!f.mkdirs())
                return null;
        }

        String outPath = path + "/tmp.raw";

        try
        {
            byte[] buffer = new byte[is.available()];
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outPath));

            int l = 0;
            while((l = is.read(buffer)) > 0)
            {
                bos.write(buffer, 0, l);
            }
            bos.close();

            tf = Typeface.createFromFile(outPath);

            File f2 = new File(outPath);
            f2.delete();
        }
        catch (IOException e)
        {
            return null;
        }

        return tf;
    }

    public static void NextStage(){
        fabplay.setImageResource(android.R.drawable.ic_media_play); bPlaying = false;
        iStage++;
    }

    private void MessageBox(String title, String text){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage(text)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setTitle(title)
                .show();
    }

}
