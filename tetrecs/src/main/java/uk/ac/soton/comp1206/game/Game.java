package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.NextPieceListener;

import java.util.HashSet;
import java.util.Random;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    protected GamePiece currentPiece;

    protected IntegerProperty score = new SimpleIntegerProperty(0);
    protected IntegerProperty level = new SimpleIntegerProperty(0);
    protected IntegerProperty lives = new SimpleIntegerProperty(3);
    protected IntegerProperty multiplier = new SimpleIntegerProperty(1);

    private Multimedia multimedia = new Multimedia();

    protected NextPieceListener nextPieceListener;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    public IntegerProperty livesProperty() {
        return lives;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public IntegerProperty multiplierProperty() {
        return multiplier;
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        currentPiece = spawnPiece();
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        if(grid.canPlayPiece(currentPiece, x, y)) {
            grid.playPiece(currentPiece, x, y);
            nextPiece();
            afterPiece();
        }
    }
    public void afterPiece() {
        int lines = 0;
        HashSet<int[]> blocksToBeCleared = new HashSet<>();

        for(int x=0; x < cols; x++) { //Vertical
            int countX = 0;
            for(int y=0; y < rows; y++) {
                if(grid.get(x,y) == 0) break;
                countX+=1;
            }
            if(countX == rows) {
                lines+=1;
                for(int y=0; y < rows; y++) {
                    int[] coordinate = new int[]{x,y};
                    blocksToBeCleared.add(coordinate);
                }
            }
        }

        for(int y=0; y < rows; y++) {//Horizontal
            int countY = 0;
            for(int x=0; x < cols; x++) {
                if(grid.get(x,y) == 0) break;
                countY+=1;
            }
            if(countY == cols) {
                lines+=1;
                for(int x=0; x < cols; x++) {
                    int[] coordinate = new int[]{x,y};
                    blocksToBeCleared.add(coordinate);
                }
            }
        }

        if(lines>0){
            clear(blocksToBeCleared);
            score(lines, blocksToBeCleared.size());
            this.multiplier.add(1);
        } else {
            this.multiplier.set(1);
        }
    }

    public void clear(HashSet<int[]> blocks) {
        for (int[] block: blocks) {
            grid.set(block[0], block[1], 0);
        }
    }

    public void score(int lines, int blocks){
        int scoreToAdd = lines*blocks*10*this.multiplier.get();
        this.score.add(scoreToAdd);
        int level = this.score.get() / 1000;
        if(this.level.get() != level) {
            this.level.set(level);
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    public GamePiece spawnPiece() {
        Random random = new Random();
        int randomNum = random.nextInt(15);
        GamePiece gamePiece = GamePiece.createPiece(randomNum);
        return gamePiece;
    }

    public void nextPiece() {
        currentPiece = spawnPiece();
        nextPieceListener.nextPiece(currentPiece);
    }

    public void setNextPieceListener(NextPieceListener nextPieceListener) {
        this.nextPieceListener = nextPieceListener;
    }

}
