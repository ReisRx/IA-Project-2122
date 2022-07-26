package screenpac.controllers;

import screenpac.model.Node;
import screenpac.features.NodeScore;
import screenpac.model.GameStateInterface;

import java.awt.*;

/**
 * Class that finds the closest Node if given the "Starter" Node
 */
public class NearestPowerDistance implements NodeScore { // NearestPowerDistance is the same as NearestPillDistance, only difference being getPowers() instead of getPills()
    static int max = Integer.MAX_VALUE;

    public Node closest = null;

    
    /** 
     * Finds closest Node to node given and sets it in Node closest
     * 
     * @param gs GameStateInterface
     * @param node Node
     * @return double
     */
    public double score(GameStateInterface gs, Node node) {
        int minDist = max;
        for (Node n : gs.getMaze().getPowers()) {
            if (gs.getPowers().get(n.powerIndex)) {
                if (gs.getMaze().dist(node, n) < minDist) {
                    minDist = gs.getMaze().dist(node, n);
                    closest = n;
                }
            }
        }
        if (closest != null) {
            closest.col = Color.green;
        }
        return minDist;
    }
}
