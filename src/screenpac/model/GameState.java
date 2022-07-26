package screenpac.model;

import screenpac.extract.Constants;
import screenpac.features.Utilities;
import screenpac.sound.PlaySound;

import java.util.BitSet;

public class GameState implements GameStateInterface, Constants {

    
    /** 
     * @return int
     */
    // todo: vary the edible time depending on the level
    // simply have a level counter, then define a function
    // which returns the edible time

    // todo: also consider changing the scoring function to give points for time staying alive

    // todo: replace the reference to the Maze object with an integer that specifies
    // the maze - should make it much easier to serialize

    // todo: mention in the talk: variations on Pac-Man e.g. new sensor model
    // for ghosts - the down corridor look
    // find slim shady video - Pac-Man could send out decoys!

    public int getLivesRemaining() {
        return nLivesRemaining;
    }

    
    /** 
     * @return Agent
     */
    public Agent getPacman() {
        return pacMan;
    }

    
    /** 
     * @return Maze
     */
    public Maze getMaze() {
        return maze;
    }

    
    /** 
     * @return BitSet
     */
    public BitSet getPills() {
        return pills;
    }

    
    /** 
     * @return BitSet
     */
    public BitSet getPowers() {
        return powers;
    }

    
    /** 
     * @return GhostState[]
     */
    public GhostState[] getGhosts() {
        return ghosts;
    }

    
    /** 
     * @return int
     */
    public int getScore() {
        return score;
    }

    
    /** 
     * @return int
     */
    public int getLevel() {
        return level;
    }

    
    /** 
     * @return int
     */
    public int getGameTick() {
        return gameTick;
    }

    
    /** 
     * @return int
     */
    public int getEdibleGhostScore() {
        return edibleGhostScore;
    }

    public Maze maze;
    public BitSet powers;
    public BitSet pills;
    public Agent pacMan;// what is the difference between a ghost and a Pac-Man?
    public GhostState[] ghosts;
    public int score = 0;
    int level;
    public int edibleGhostScore;
    int gameTick;
    public int nLivesRemaining;

    
    /** 
     * @return GameStateInterface
     */
    public GameStateInterface copy() {
        // return a deep copy of the current game state
        // just copy a reference for the ones that don't change
        GameState gs = new GameState();
        gs.maze = maze;
        gs.powers = (BitSet) powers.clone();
        gs.pills = (BitSet) pills.clone();
        gs.pacMan = pacMan.copy();
        for (int i = 0; i < ghosts.length; i++) {
            gs.ghosts[i] = ghosts[i].copy();
        }
        gs.score = score;
        gs.level = level;
        gs.edibleGhostScore = edibleGhostScore;
        gs.gameTick = gameTick;
        gs.nLivesRemaining = nLivesRemaining;
        return gs;
    }


    public GameState() {
        this(Level.getMaze(0));
    }

    public GameState(Maze maze) {
        this.maze = maze;
        ghosts = new GhostState[nGhosts];
        pills = new BitSet(maze.getPills().size());
        powers = new BitSet(maze.getPowers().size());
        reset();
    }

    public void reset() {
        resetScores();
        maze = Level.getMaze(level);
        resetAgents();
        resetPills();
    }

    public void resetAgents() {
        pacMan = new Agent(maze.getMap().get(0));
        pacMan.current = maze.pacStart();
        for (int i = 0; i < nGhosts; i++) {
            ghosts[i] = new GhostState(initGhostDelay[i]);
            ghosts[i].current = maze.ghostStart();
        }
    }

    public void resetPills() {
        pills.set(0, maze.getPills().size());
        powers.set(0, maze.getPowers().size());
    }

    public void resetScores() {
        gameTick = 0;
        score = 0;
        level = 0;
        nLivesRemaining = nLives;
    }

    public void nextLevel() {
        // advance to next level
        level++;
        maze = Level.getMaze(level);
        // edible time is queried directly when needed
        resetPills();
        resetAgents();
    }

    
    /** 
     * @param pacDir
     * @param ghostDirs
     */
    public void next(int pacDir, int[] ghostDirs) {
        // calculate the next game state for agent and ghosts
        // System.out.println("nPills = " + pills.cardinality() + " : " + powers.cardinality());
        pacMan.next(pacDir, maze);
        tryEatPill();
        tryEatPower();
        eatGhosts();
        if (ghostReversal()) {
            reverseGhosts();
        } else {
            moveGhosts(ghostDirs);
        }
        if (pillsCleared()) {
            nextLevel();
            System.out.println("Pills cleared!");
        } else {
            if (agentDeath()) {
                nLivesRemaining--;
                resetAgents();
            }
        }
        gameTick++;
    }

    
    /** 
     * @return boolean
     */
    public boolean ghostReversal() {
        return rand.nextDouble() < 0.005;
    }

    public void reverseGhosts() {
        for (GhostState g : ghosts) g.reverse();
    }

    
    /** 
     * @param ghostDirs
     */
    public void moveGhosts(int[] ghostDirs) {
        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i].next(ghostDirs[i], this);
        }
    }

    public void tryEatPill() {
        int ix = pacMan.current.pillIndex;
        if (ix >= 0) {
            if (pills.get(ix)) {
                pills.clear(ix);
                score += pillScore;
                PlaySound.eatPill();
            }
        }
    }

    public void tryEatPower() {
        int ix = pacMan.current.powerIndex;
        if (ix >= 0) {
            if (powers.get(ix)) {
                powers.clear(ix);
                // also trigger edibility
                reverseGhosts();
                setEdible();
                score += powerScore;
                PlaySound.eatPower();
            }
        }
    }

    public void setEdible() {
        // each ghost is initialised to it's edible state
        // and keeps track of it's own time
        edibleGhostScore = edibleGhostStartingScore;
        for (GhostState g : ghosts) {
            g.edibleTime = Level.edibleTime(level);
        }
    }

    
    /** 
     * @return boolean
     */
    // now define a series of tests on the game state

    public boolean pillsCleared() {
        
        return pills.isEmpty() && powers.isEmpty();
    }

    
    /** 
     * @return boolean
     */
    public boolean agentDeath() {
        for (GhostState g : ghosts) {
            if (!g.edible() && !g.returning() && overlap(g, pacMan)) {
                PlaySound.loseLife();
                return true;
            }
        }
        return false;
    }

    public void eatGhosts() {
        // eat any edible ghosts
        // this involves incrementing the score, doubling the ghost score
        // for the next ghost to be consumed, and setting the ghost
        // to the "returning" state
        for (GhostState g : ghosts) {
            if (g.edible() && overlap(g, pacMan)) {
                score += edibleGhostScore;
                edibleGhostScore *= 2;
                PlaySound.eatGhost();
                g.returnNode = maze.ghostStart();
                g.setPredatory();
            }
        }
    }

    
    /** 
     * @return boolean
     */
    public boolean terminal() {
        return nLivesRemaining <= 0;
    }

    
    /** 
     * @param g
     * @param agent
     * @return boolean
     */
    public static boolean overlap(GhostState g, Agent agent) {
        return Utilities.manhattan(g.current, agent.current)
                <= agentOverlapDistance;
    }
}
