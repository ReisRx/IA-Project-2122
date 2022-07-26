package screenpac.controllers;

import screenpac.model.Node;
import screenpac.model.GameStateInterface;
import screenpac.model.GhostState;
import screenpac.model.MazeInterface;

import screenpac.features.NearestPillDistance;
import screenpac.features.Utilities;

import screenpac.extract.Constants;

import java.awt.*;

/**
 * Implements a controller to Ms. Pacman
 */
public class P2G14 implements AgentInterface, Constants {
    /* 
        To get opposite direction just multiply direction by -1: (-1 * dir) or use a switch case
        Useful to use: for(GhostState ghost : gs.GetGhosts()) ghost.current gs.GetMaze().dist(a, b) Node.adj BitSet.cardinality
        Try using edible() and getPossibles() from GhostState.java to avoid Ghosts 
    */
    NearestPillDistance nPillD;
    NearestPowerDistance nPowerD;
    AStar star;
    // int[] randDir = {0, 1, 2, 3, 4};

    final static int EDIBLE_DETECTION_RANGE = 50;
    final static int GHOST_DETECTION_RANGE = 20;

    public P2G14() {
        nPillD = new NearestPillDistance();
        nPowerD = new NearestPowerDistance();
        star = new AStar();
    }

    
    /** 
     * Gets opposite direction of the given
     * 
     * @param dir int
     * @return int
     */
    private int opposite(int dir) { // Get opposite direction
        switch (dir) {
            case UP:
                return DOWN;
        
            case DOWN:
                return UP;

            case LEFT:
                return RIGHT;
            
            case RIGHT:
                return LEFT;

            default:
                return NEUTRAL;
        }
    }

    
    /** 
     * Returns the direction of which Ms. Pacman should take
     * 
     * @param gs GameStateInterface
     * @return int
     */
    public int action(GameStateInterface gs) {
        gs = gs.copy();
        MazeInterface maze = gs.getMaze();
        Node current = gs.getPacman().current;
        Node next = null;
        int ghostIndex = Integer.MAX_VALUE; // Edible ghosts
        int nonEdibleIndex = Integer.MAX_VALUE; // Non-edible ghosts

        for(GhostState ghost : gs.getGhosts()) { 
            if(ghost.edible()) { // Detect if any of the edible ghosts is within EDIBLE_DETECTION_RANGE
                if(maze.dist(current, ghost.current) < ghostIndex) {
                    ghostIndex = maze.dist(current, ghost.current);
                    if(ghostIndex < EDIBLE_DETECTION_RANGE) next = ghost.current;
                }
            }
            else if(!ghost.edible() && !ghost.returning()) { // Detect the closest non-edible and non-returning ghost
                if(maze.dist(current, ghost.current) < nonEdibleIndex) {
                    nonEdibleIndex = maze.dist(current, ghost.current);
                }
            }
        }
        if(next != null) next.col = Color.green;

        if(gs.getPowers().cardinality() > 0 && nonEdibleIndex < GHOST_DETECTION_RANGE) { 
            // Go to the closest PowerPill if there is a non-edible ghost within GHOST_DETECTION_RANGE
            nPowerD.score(gs, current);
            next = nPowerD.closest;
        }
        if(next == null) { // Go to the closest Pill
            nPillD.score(gs, current);
            next = nPillD.closest;
        }
        next = star.findPath(current, next, gs); // Get next from the best path
        int dir;
        if(next == null) {
            dir = opposite(gs.getPacman().getCurDir()); // If there is no path available, invert direction of which pacman is going
            // dir = randDir[rand.nextInt(5)];
        }
        else dir = Utilities.getWrappedDirection(current, next, maze);
        return dir;
    }
}