# DronePath-A-Star-Experiment

Android application for a shortest path finding experiment based on drones.

Uses the A-Star algorithm to calculate routes under different conditions and compared the results.

Three basic scenarios are considered, differ in the number of obstacles, and three different methods of shortest path finding, which are:
* the shortest path in straight line ignoring the obstacles
* the A-Star algorithm ignoring the cost in time to change direction
* the A-Star algorithm considering the cost of changing direction

The application is design to be able to adapt on a biggest future project, that involves centralized management based on cloud, to store and exchange information. For this, it uses Firebase to store the structural parameter table for each scenario, and to upload every calculated path as GPS coordinates (latitude and longitude). So, it is possible for any drone with the ability to access this information, to follow in real-time the uploaded path.

The execution takes place on a Google Map activity, where the user can follow the calculations of the shortest-path algorithm in slow motion, while the network nodes are represented as squares with distinct colors. In addition to that, the A-Star algorithm is developed as a runnable class and can be paused by pressing a button while execution takes place, giving the user the opportunity to inspect each condition at any time.

It is developed with the Android Studio 3.4.1

Contact me at gtparas@gmail.com
