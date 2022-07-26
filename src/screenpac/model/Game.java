package screenpac.model;

import games.pacman.maze.MazeOne;
import screenpac.sound.PlaySound;
import utilities.JEasyFrame;
import utilities.ElapsedTimer;
import utilities.StatSummary;
import screenpac.extract.Constants;
import screenpac.controllers.AgentInterface;
import screenpac.controllers.P2G14;
import screenpac.controllers.SimplePillEater;
import screenpac.controllers.RandomAgent;
import screenpac.controllers.RandomNonReverseAgent;
import screenpac.ghosts.GhostTeamController;
import screenpac.ghosts.RandTeam;
import screenpac.ghosts.PincerTeam;
import screenpac.ghosts.LegacyTeam;

public class Game implements Constants {
    // this class brings together the agent
    // controllers together with the model
    // and may also be responsible for taking
    // actions that depend on the game state
    static int delay = 40;
    static boolean visual = true;

    
    /** 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        AgentInterface agent = new SimplePillEater();
        agent = new P2G14();
        // agent = new SmartPillEater();
        // agent = new RandomNonReverseAgent();
        GhostTeamController ghostTeam;
        ghostTeam = new RandTeam();
        ghostTeam = new LegacyTeam();
        ghostTeam = new PincerTeam();

        // agent = null;
        if (visual) runVisual(agent, ghostTeam);
        else runDark(agent, ghostTeam);
    }

    
    /** 
     * @param agentController
     * @param ghostTeam
     * @throws Exception
     */
    public static void runDark (AgentInterface agentController, GhostTeamController ghostTeam) throws Exception {
        Maze maze = new Maze();
        maze.processOldMaze(MazeOne.getMaze());
        GameState gs = new GameState(maze);
        gs.reset();
        Game game = new Game(gs, null, agentController, ghostTeam);
        ElapsedTimer t = new ElapsedTimer();
        int nRuns = 100;
        PlaySound.disable();
        StatSummary ss = new StatSummary();
        for (int i=0; i<nRuns; i++) {
            game.gs.reset();
            game.run();
            ss.add(game.gs.score);
            System.out.println("Final score: " + game.gs.score + ", ticks = " + game.gs.gameTick);
        }
        System.out.println(t);
        System.out.println(ss);
    }

    
    /** 
     * @param agentController
     * @param ghostTeam
     * @throws Exception
     */
    public static void runVisual(AgentInterface agentController, GhostTeamController ghostTeam) throws Exception {
        GameState gs = new GameState();
        gs.nextLevel();
        // gs.nextLevel();
        // gs.nextLevel();
        gs.nLivesRemaining = 4;
        // gs.reset();
        GameStateView gsv = new GameStateView(gs);
        // PlaySound.enable();  // ATIVAR O SOM
        PlaySound.disable();    // DESATIVAR O SOM
        JEasyFrame fr = new JEasyFrame(gsv, "Pac-Man vs Ghosts", true);
        KeyController kc = new KeyController();
        fr.addKeyListener(kc);
        // set the key controller if the agent is null
        if (agentController == null) agentController = kc;
        Game game = new Game(gs, gsv, agentController, ghostTeam);
        game.frame = fr;
        game.run();
        // use line below to run for a max number of cycles
        // game.run(100);
        System.out.println("Final score: " + game.gs.score);
    }

    
    /** 
     * @throws Exception
     */
    public void run() throws Exception {
        // System.out.println("nLives left: " + gs.nLivesRemaining);
        while(!gs.terminal()) {
            cycle();
            // System.out.println(gs.pills.cardinality() + " : " + gs.powers.cardinality());
        }
        System.out.println("nLives left: " + gs.nLivesRemaining);
    }

    
    /** 
     * @param n
     * @throws Exception
     */
    public void run(int n) throws Exception {
        int i=0;
        while(i++ < n && !gs.terminal()) {
            cycle();
        }
    }

    
    /** 
     * @throws Exception
     */
    public void cycle() throws Exception {
        // update the game state
        gs.next(agentController.action(gs), ghostTeam.getActions(gs));
        if (gsv != null) {
            gsv.repaint();
            if (frame != null) frame.setTitle("Score: " + gs.score);
            Thread.sleep(delay);
        }
    }

    public Game(GameState gs, GameStateView gsv, AgentInterface agentController, GhostTeamController ghostTeam) {
        this.gs = gs;
        this.gsv = gsv;
        this.agentController = agentController;
        this.ghostTeam = ghostTeam;
    }

    GameState gs;
    GameStateView gsv;
    AgentInterface agentController;
    GhostTeamController ghostTeam;
    JEasyFrame frame;

}
