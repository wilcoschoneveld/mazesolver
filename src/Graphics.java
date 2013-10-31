

import java.util.ArrayList;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/**
 * Contains all graphical tools.
 */
public class Graphics implements Runnable {
    private final Director director;
    private final Maze maze;

    /**
     * Constructs new graphics handler.
     * @param director 
     */
    public Graphics(Director director) {
        this.director = director;
        this.maze = director.getMaze();
    }

    /**
     * Build new display.
     */
    @Override
    public void run() {
        // Create a new LWJGL window        
        DisplayMode dm = new DisplayMode(
                Settings.Graphics.width, Settings.Graphics.height);
        
        try {
            Display.setDisplayMode(dm);
            Display.create();
        } catch (LWJGLException e) {
            director.interrupted = true;
            System.exit(0);
        }

        // Set the display title
        Display.setTitle(Settings.Graphics.title);
        
        // Setup opengl viewing space
        GL11.glClearColor(0, 0, 0, 0);
        
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, maze.getWidth(), maze.getHeight(), 0, -1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Initialize drawTime with offset for instant first draw
        long drawTime = -Settings.Graphics.drawWaitTime + System.currentTimeMillis();
        
        // Update loop      
        while (!director.interrupted && !Display.isCloseRequested()) {
            // Only draw at certain delta time
            if((System.currentTimeMillis() - drawTime) > Settings.Graphics.drawWaitTime) {
                // Draw to screen
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

                // Draw the maze
                drawMaze();

                // Draw best route so far
                GL11.glLineWidth(Settings.Graphics.lineWidth);
                GL11.glColor3f(
                        Settings.Graphics.lineColor[0],
                        Settings.Graphics.lineColor[1],
                        Settings.Graphics.lineColor[2]);
                drawPath(director.getBestRoute());

                // Process input
                handleInput();

                // Update screen
                Display.update(false);
                
                drawTime += Settings.Graphics.drawWaitTime;
            }
            
            // Process messages and sync
            Display.processMessages();
            Display.sync(Settings.Graphics.framesPerSecond);
        }
        
        Display.setTitle(Display.getTitle() + " | STOPPED!");

        // Stop maze iterations
        director.interrupted = true;

        // Keep showing until closed again
        while (!Display.isCloseRequested()) {
            handleInput();
            Display.processMessages();
            Display.sync(Settings.Graphics.framesPerSecond);
        }

        // Destroy the window
        Display.destroy();

        // Thread ends here
        Helper.log("Display destroyed");
    }
    
    /**
     * Handles mouse events.
     */
    private void handleInput() {
        while(Mouse.next()) {
            if(Mouse.getEventButtonState() == true) {
                // Get mouse coordinates
                int x = Mouse.getEventX();
                int y = Display.getHeight() - Mouse.getEventY();
                
                // Transform to maze coordinates
                int mx = maze.getWidth() * x / Display.getWidth();
                int my = maze.getHeight() * y / Display.getHeight();
                
                // Print coordinates 
                Helper.log("Clicked at: " + mx + "," + my);
            }
        }
    }
    
    /**
     * Draws maze on screen.
     */
    private void drawMaze() {        
        GL11.glBegin(GL11.GL_QUADS);
        
        // Draw the nodes
        GL11.glColor3f(1, 1, 1);
        for(int x = 0; x < maze.getWidth(); x++) {
            for(int y = 0; y < maze.getHeight(); y++) {
                if(maze.isNode(x, y)) drawNode(x, y);
            }
        }
        
        // Draw the startPoint
        GL11.glColor3f(0.275f, 0.537f, 0.4f);
        drawPoint(maze.getStartPoint(), 0.8f);
        
        // Draw the endPoint
        GL11.glColor3f(0.557f, 0.157f, 0);
        drawPoint(maze.getEndPoint(), 0.8f);
        
        // Draw the venues
        GL11.glColor3f(1, 0.69f, 0.231f);
        for (Venue ptv : maze.getVenues()) {
            drawPoint(ptv.getLocation(), 0.7f);
        }
        
        GL11.glEnd();        
    }
    
    /**
     * Draws point on screen.
     * @param p
     * @param size 
     */
    private static void drawPoint(Point p, float size) {
        drawQuad(p.getX(), p.getY(), size);
    }
    
    /**
     * Draws node on screen.
     * @param x
     * @param y 
     */
    private static void drawNode(int x, int y) {
        drawQuad(x, y, 1);
    }
    
    /**
     * Draws square on screen.
     * @param x
     * @param y
     * @param size 
     */
    private static void drawQuad(int x, int y, float size) {
        float half = (1 - size) / 2;
        GL11.glVertex2f(x + half, y + half);
        GL11.glVertex2f(x + 1 - half, y + half);
        GL11.glVertex2f(x + 1 - half, y + 1 - half);
        GL11.glVertex2f(x + half, y + 1 - half);
    }
    
    /**
     * Draws path on screen.
     * @param path 
     */
    private static void drawPath(ArrayList<Point> path) {
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (Point point : path)
            GL11.glVertex2f(point.getX()+0.5f, point.getY()+0.5f);
        GL11.glEnd();
    }
}
