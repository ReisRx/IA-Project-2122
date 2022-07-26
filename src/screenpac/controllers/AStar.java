package screenpac.controllers;

import screenpac.model.Node;
import screenpac.model.GameStateInterface;
import screenpac.model.GhostState;
import screenpac.features.Utilities;

import screenpac.extract.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Class that implements the algorithm A* adapted to Ms. Pacman
 */
public class AStar implements Constants {
    static class State {
        private Node layout;
        private State father;
        private int g;
        public State(Node l, State n) {
            layout = l;
            father = n;
            if(father != null)
                g = father.g + 1;
            else g = 0;
        }

        public String toString() {
            return layout.toString();
        }

        public int getG() {
            return g;
        }

        public int getH(Node goal) {
            return Utilities.manhattan(layout, goal);
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj)
                return true;
            if(obj == null || getClass() != obj.getClass())
                return false;
            State s = (State) obj;
            return layout.equals(s.layout);
        }
    
        @Override
        public int hashCode() {
                return super.hashCode();
        }
    }

    
    /** 
     * Verifies if Node start is equal to Node goal
     * 
     * @param start Node
     * @param goal Node
     * @return boolean
     */
    private boolean isGoal(Node start, Node goal) {
        return start.nodeIndex == goal.nodeIndex || (start.x == goal.x && start.y == goal.y);
    }
    
    
    /** 
     * Basic successors function
     * 
     * @param n State
     * @return List<State>
     */
    private List<State> successors(State n) { // Basic sucessors function
        List<State> sucs = new ArrayList<>();
        List<Node> children = n.layout.adj;
        for(Node e : children) {
            if(n.father == null || !e.equals(n.father.layout)) {
                State nn = new State(e, n);
                sucs.add(nn);
            }
        }
        return sucs;
    }

    
    /** 
     * Recursively gets successors, count times
     * 
     * @param father State
     * @param adjs ArrayList<Node>
     * @param sucs List<State>
     * @param count int
     */
    private void successors(State father, ArrayList<Node> adjs, List<State> sucs, int count) {
        count++;
        for(Node e : adjs) {
            State nn = new State(e, father);
            sucs.add(nn);
            if(count < 2) successors(nn, e.adj, sucs, count); // Variable count is to determine how many steps forward to look into
        }
    }

    
    /** 
     * When given a GhostState[], gets possible outcomes
     * 
     * @param ghosts GhostState[]
     * @param result List<State>
     */
    private void ghostAdj(GhostState[] ghosts, List<State> result) { // Adds in a List<State> every state that is possible the non-edible and non-returning ghosts to go
        for(GhostState g : ghosts) {
            if(!g.edible() && !g.returning()) {
                State gg = new State(g.current, null);
                result.add(gg);
                successors(gg, g.getPossibles(), result, 0);
            }
        }
    }

    
    /** 
     * A* algorithm implemented to find path
     * 
     * @param s Node
     * @param goal Node
     * @param gs GameStateInterface
     * @return Node
     */
    public Node findPath(Node s, Node goal, GameStateInterface gs) {
        State actual;
        Queue<State> open = new PriorityQueue<>(10, (s1, s2) -> (int) Utilities.sgn((s1.getG() + s1.getH(goal)) - (s2.getG() + s2.getH(goal))));
        List<State> closed = new ArrayList<>();
        List<State> sucs = new ArrayList<>();
        List<State> ghosts = new ArrayList<>();
        ghostAdj(gs.getGhosts(), ghosts);

        open.add(new State(s, null));
        while (true) {
            if(open.isEmpty()) {
                // System.exit(0);
                return null; // AStar returns null meaning no solutions were found.
            }
            actual = open.poll();

            if (isGoal(actual.layout, goal)) { // If isGoal(), go back to the second step (next step of current position)
                State temp = actual;
                for(; temp.father != null && temp.father.father != null; temp = temp.father)
                    continue;
                return temp.layout;
            }

            closed.add(actual);
            sucs = successors(actual);
            for (State successor : sucs) { // Check if next node is valid (isn't in closedSet, openSet and ghostsPossibles)
                if (!closed.contains(successor) && !open.contains(successor) && !ghosts.contains(successor))
                open.add(successor);
            }
        }
    }
}