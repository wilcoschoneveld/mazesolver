

/**
 * Main class, application entry point.
 */
public class Main {
    
    /**
     * Creates the new maze, and starts the director.
     * @param args 
     */
    public static void main(String[] args) {
        // Create a new Maze
        Maze maze = Maze.readMaze(
                Settings.Main.mazeFile,
                Settings.Main.coordinatesFile,
                Settings.Main.venuesFile,
                Settings.Main.visitsFile);
        
        Helper.log("Maze created of size " +
                maze.getWidth() + "x" + maze.getHeight());
        
        Settings.Ant.maxSteps = maze.getHeight() * maze.getWidth();
        
        // Create new Director
        Director director = new Director(maze);        
        
        // Create graphics thread and start it
        Thread graphics = new Thread(new Graphics(director));
        graphics.start();
        
        Helper.log("Graphics thread created and started");
        
        // Do iterations while not interrupted
        Helper.log("Starting iterations"); int i = 0;
        while(!director.interrupted && i < Settings.Main.maxIterations) {
            director.oneIteration(++i);
        }

        // Cancel all remaining threads
        director.interrupted = true;
        
        // Announce application end
        Helper.log("Iteration cycle stopped");
        
        // Write best path to file
        director.writeRouteToFile();
    }
}