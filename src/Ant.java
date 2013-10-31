

import java.util.ArrayList;

/**
 * Implementation of a simple Ant.
 */
public class Ant {    
    private final ArrayList<Point> path;
    private final boolean[][] pathmap;
    private final SubRoute subRoute;
    
    /**
     * Creates new Ant for finding a path between the points of subRoute.
     * @param subRoute 
     */
    public Ant(SubRoute subRoute) {
        this.subRoute = subRoute;
        
        path = new ArrayList<>();
        path.add(subRoute.getStartPoint());
        
        pathmap = new boolean
                [subRoute.getMaze().getHeight()]
                [subRoute.getMaze().getWidth()];
    }
    
    /**
     * Ant determines next step and takes it.
     * @return true if at end point
     */
    public boolean doStep() {
        // If at endPoint (happens if two PTV at same location)
        if(subRoute.getEndPoint().equals(currentPoint()))
            return true;
        
        // Choose next point to move
        Point next = chooseNextPoint();
        
        // Check if point is valid
        if(next == null) return false;
        
        // Add point to route
        path.add(next);

        // Update stepMap
        pathmap[next.getY()][next.getX()] = true;
        
        return false;
    }
    
    /**
     * Determines which of the 4 surrounding nodes the Ant should take.
     * @return 
     */
    public Point chooseNextPoint() {
        // Store current x and y coordinates
        int x = currentPoint().getX();
        int y = currentPoint().getY();
        
        // Create 4 surrounding locations
        Point[] points = new Point[4];
        points[0] = new Point(x, y-1);
        points[1] = new Point(x-1, y);
        points[2] = new Point(x+1, y);
        points[3] = new Point(x, y+1);
        
        // Check each location with maze/path
        float[] w = new float[4];
        for(byte i = 0; i < 4; i++) {
            if(!subRoute.getMaze().isNode(points[i]))
                w[i] = Settings.Ant.factorWall;
            else if(points[i].equals(previousPoint()))
                w[i] = Settings.Ant.factorReverse;
            else if(pathmap[points[i].getY()][points[i].getX()])
                w[i] = Settings.Ant.factorOld;
            else
                w[i] = Settings.Ant.factorNew;
        }
        
        // Calculate total
        float total = w[0] + w[1] + w[2] + w[3];     
        
        // Spin the roulette wheel
        float rand = (float) (Math.random() * total);
        
        // Select point from possibilities
        if(w[0] != 0 && rand < w[0]) return points[0];
        if(w[1] != 0 && rand < w[0]+w[1]) return points[1];
        if(w[2] != 0 && rand < w[0]+w[1]+w[2]) return points[2];
        if(w[3] != 0) return points[3];
        
        // Return null if no step is possible
        return null;
    }
          
    /**
     * Returns the route taken by Ant.
     * @return path
     */
    public ArrayList<Point> getPath() { 
        return path; 
    }
    
    /**
     * Returns current position of Ant.
     * @return Point
     */
    private Point currentPoint() { 
        return path.get(path.size() - 1); 
    }
    
    /**
     * Returns previous position of Ant.
     * @return previous Point
     */
    private Point previousPoint() {
        return (path.size() < 2) ? null : path.get(path.size() - 2);
    }

}