package priyamvadatiwari.p1_tiwari_priyamvada;


import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import java.util.ArrayList;

/**
 * summary: launches the main class of this Java project
 */
public class MainActivity extends FragmentActivity {

    Utilities utilities = new Utilities();
    Handler handler = new Handler(); // used for postDelayed(...) functionality
    LinearLayout gameScreen, gameOptionsToolbar;
    GridLayout gameGrid;
    Bitmap gameBmp, missingTile;
    Button newGameBtn;
    ImageView changeImageBtn;
    TextView scoreDisplay;
    ImageView blankTile; // caches the blank tile at any stage in the game
    ChangeImageDialog changeImageDialog;
    PreviewSolutionDialog previewSolutionDialog;

    //linearly held creation ids of the bitmaps that were broken down from the main image (gameBmp)
    ArrayList<Integer> correctTileOrder = new ArrayList<Integer>();

    int scoreValue; //stores the game score
    boolean victoryStatus = false; // keeps track of whether or not to invoke some game/ additional listeners
    int gameRows = 3, // number of rows to be present on the game grid
            gameColumns = 3, // number of columns to be present on the game grid
            mCurrentImageIndex = 1, // the image in current use while playing the game
            imageToggleCount = 1; // the number of times the image has been set/reset on the page


    /**
     * summary: initializes the layout components in the game at the beginning (onCreate)
     */
    private void initComponents()   {
        changeImageDialog = new ChangeImageDialog();
        previewSolutionDialog = new PreviewSolutionDialog();
        gameScreen = (LinearLayout) findViewById(R.id.game_screen);
        gameGrid = (GridLayout) findViewById(R.id.gameGrid);
        gameOptionsToolbar = (LinearLayout) findViewById(R.id.gameOptionsToolbar);
        newGameBtn = (Button) findViewById(R.id.new_game_btn);
        changeImageBtn = (ImageView) findViewById(R.id.change_img_btn);
        scoreDisplay = (TextView) findViewById(R.id.score_value);
        //this.resizeGameGrid();
    }

    /**
     * summary: aims to programmatically resize the gaming grid based on screen size and also screen
     * orientation configuration  when turned on
     * @notWorking fully yet
     */
    private void resizeGameGrid()   {
        int padding = 30,
                tileWidth = (gameScreen.getWidth() - padding)/3,
                tileHeight = (gameScreen.getHeight() - gameOptionsToolbar.getHeight()
                - scoreDisplay.getHeight() - padding)/3;
        tileHeight = tileWidth = Math.min(tileHeight, tileWidth);
        for(int i = 0; i<gameRows*gameColumns; i++) {
            View tile = gameGrid.getChildAt(i);
            ViewGroup.LayoutParams layoutParams = tile.getLayoutParams();
            layoutParams.height = tileHeight;
            layoutParams.width = tileWidth;

            tile.setLayoutParams(layoutParams);
        }
    }

    /**
     * summary: Launches a new game.
     *          Re-initializes all the game state variables.
     *          Draws 8 of the 9 sub-parts of our image on random squares within the gridLayout on screen
     */
    public void newGame() {
        int i=0, j=0, k=0, tileWidth, tileHeight;
        ArrayList<Integer> uniqueRandomsList = utilities.getUniqueRandomsList(gameRows * gameColumns, 0, 9);

        this.updateScore(0);
        victoryStatus = false;
        correctTileOrder.clear();
        this.clearFocusedTiles(gameGrid, Color.LTGRAY);

        this.setGameImage();
        tileWidth = gameBmp.getWidth()/3;
        tileHeight = gameBmp.getHeight()/3;

        for(i = 0; i < gameRows; i++)  {
            for(j = 0; j < gameColumns; j++, k++)    {
                ImageView subImg = (ImageView) gameGrid.getChildAt(uniqueRandomsList.get(k));
                if(k == gameColumns - 1)  {
                    missingTile = Bitmap.createBitmap(gameBmp, tileWidth * j, tileHeight * i, tileWidth, tileHeight);
                    correctTileOrder.add(missingTile.getGenerationId());
                    subImg.setImageBitmap(null);
                    blankTile = subImg;
                }   else    {
                    Bitmap tileBmp = Bitmap.createBitmap(gameBmp, tileWidth * j, tileHeight * i, tileWidth, tileHeight);
                    correctTileOrder.add(tileBmp.getGenerationId());
                    subImg.setImageBitmap(tileBmp);
                }
            }
        }
    }

    /**
     * Helps set the gameImageIndex from an external class
     * @param gameImageIndex - accepted values currently are 1, 2, 3, and 4
     */
    public void setGameImageIndex(int gameImageIndex)   {
        if(gameImageIndex > 0 && gameImageIndex <= R.drawable.class.getFields().length) {
            mCurrentImageIndex = gameImageIndex;
        }
        else mCurrentImageIndex = imageToggleCount%4;
    }

    /**
     * Summary: Responsibe to set the next game image based on the currently applied image from the
     *      image-repository queue
     */
    private void setGameImage() {
        switch (mCurrentImageIndex) {
            case 1:
                gameBmp = BitmapFactory.decodeResource(getResources(), R.drawable.smiley1);
                break;
            case 2:
                gameBmp = BitmapFactory.decodeResource(getResources(), R.drawable.smiley2);
                break;
            case 3:
                gameBmp = BitmapFactory.decodeResource(getResources(), R.drawable.smiley3);
                break;
            case 0:
            case 4:
                gameBmp = BitmapFactory.decodeResource(getResources(), R.drawable.smiley4);
                break;
            default:
                gameBmp = BitmapFactory.decodeResource(getResources(), R.drawable.smiley1);
                break;
        }
        changeImageBtn.setImageBitmap(gameBmp);
    }

    /**
     * Summary: Moves the functionally passed sub-image to the empty tile on screen,
     * thereby making the previous tile become the new blankTile
     *
     * @param subImg - The image that needs to be painted into the blank tile
     */
    private void moveTileToBlank(ImageView subImg) {
        blankTile.setImageBitmap(((BitmapDrawable) subImg.getDrawable()).getBitmap());
        this.clearFocusedTiles(gameGrid, Color.LTGRAY);
        blankTile.setBackgroundColor(Color.BLACK);
        subImg.setImageBitmap(null);

        blankTile = subImg;

        this.updateScore(scoreValue + 1);
        this.checkGameVictory();
    }

    /**
     * Summary: Called at the time of either starting a new game/ game finish
     *      or can be called in order to "focus-out" from the game-grid.
     *      It removes the focusing styling (highlighting border) from the currently focussed tile. The
     *      currently focussed tile denotes the latest tile that has been moved around on the game grid.
     * @param grid - the grid within which tile focus has to be removed
     * @param bkgdColour - the background colour
     */
    public void clearFocusedTiles(GridLayout grid, int bkgdColour)    {
        for(int i = 0; i < grid.getRowCount()* grid.getColumnCount(); i++)   {
            grid.getChildAt(i).setBackgroundColor(bkgdColour);
        }
    }

    /**
     * Summary: After every successful move of a tile to the blank tile, this function checks
     *      if the game has been won after this last move
     */
    private void checkGameVictory() {
        boolean won = true;
        for(int i =0; i < gameRows * gameColumns; i++)  {

            // checking to see if the correctTileOrder variable corresponds to the ids of the puzzle tiles in order
            if(i != gameColumns-1
                    && (((BitmapDrawable) ((ImageView) gameGrid.getChildAt(i)).getDrawable()).getBitmap() == null
                    || ((BitmapDrawable) ((ImageView) gameGrid.getChildAt(i)).getDrawable())
                    .getBitmap().getGenerationId() != correctTileOrder.get(i)))  {
                won = false;
                break;
            }
        }

        if(won) {
            this.winGame();
        }
    }

    /**
     * Summary: Invoked when it it detected that the game has been won.
     *      Changes victoryStatus to true
     *      Adds a victory acknowledgement to the scoreboard
     *      Presents the whole picture along with the 9th piece that couldn't be accesed during the game
     */
    private void winGame()  {
        victoryStatus = true;
        scoreDisplay.setText(String.valueOf(scoreValue) + ' ' + getResources().getText(R.string.message_game_won));
        blankTile.setImageBitmap(missingTile);
        blankTile = null;
    }

    /**
     * Summary: sets the integer value counter for the scoreValue variable
     *
     *  @param newScore - the new score value that needs to be written extensively
     */
    private void updateScore(int newScore)  {
        scoreValue = newScore;
        scoreDisplay.setText(String.valueOf(scoreValue));
    }

    /**
     * Summary: This method attaches event listeners and handlers for different game layout elements
     *      in the very beginning
     */
    private void attachListeners()  {
        final MainActivity that = this;

        // Triggers new game to be launched after clicking on the "New Game" button
        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                that.newGame();
            }
        });

        // Triggers new image to be loaded, new game launched after clicking on the "Change Image" button
        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                imageToggleCount++;
                changeImageDialog.show(getFragmentManager(), "changeImageDialog");
            }
        });

        changeImageBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    view.setBackgroundColor(Color.parseColor("#aaaaaa"));
                    Toast.makeText(getApplicationContext(), R.string.label_solution_preview,
                            Toast.LENGTH_SHORT).show();
                    previewSolutionDialog.show(getFragmentManager(), "ViewSolutionDialog", gameBmp);

                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    previewSolutionDialog.dismiss();
                    view.setBackgroundColor(Color.LTGRAY);
                    imageToggleCount++;
                    changeImageDialog.show(getFragmentManager(), "changeImageDialog");
                }
                return true;
            }
        });

                // Sets listeners for all tiles within the game grid
        for(int i =0; i < gameRows * gameColumns; i++)  {
            final ImageView subImg = (ImageView) gameGrid.getChildAt(i);
            subImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    int currIndex = gameGrid.indexOfChild(subImg),
                            blankIndex = gameGrid.indexOfChild(blankTile);
                    if (!victoryStatus
                            && (Math.abs(currIndex - blankIndex) == gameColumns
                            || (currIndex % gameColumns != 0 && currIndex - 1 == blankIndex)
                            || ((currIndex + 1) % gameColumns != 0 && currIndex + 1 == blankIndex))) {
                        that.moveTileToBlank(subImg);
                    } else if (currIndex != blankIndex) {
                        subImg.setBackgroundColor(Color.RED);
                        Toast.makeText(getApplicationContext(), R.string.message_cannot_move_tile,
                                Toast.LENGTH_SHORT).show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                subImg.setBackgroundColor(Color.LTGRAY);
                            }
                        }, 100);
                    }
                }
            });

            subImg.setOnLongClickListener(new View.OnLongClickListener() {
                /**
                 * Summary: In case user longClicks a valid tile, the destination blank tile is
                 *      highlighted by filling darker clolour
                 *      Also invokes a toast and highlights miscalcuated attempts in red
                 *
                 * @param arg0
                 * @return returns true always here
                 */
                @Override
                public boolean onLongClick(View arg0) {
                    int currIndex = gameGrid.indexOfChild(subImg),
                            blankIndex = gameGrid.indexOfChild(blankTile);
                    if (!victoryStatus
                            && (Math.abs(currIndex - blankIndex) == gameColumns
                            || (currIndex % gameColumns != 0 && currIndex - 1 == blankIndex)
                            || ((currIndex + 1) % gameColumns != 0 && currIndex + 1 == blankIndex))) {
                        blankTile.setBackgroundColor(Color.GRAY);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                blankTile.setBackgroundColor(Color.LTGRAY);
                            }
                        }, 500);

                    } else if (currIndex != blankIndex) {
                        subImg.setBackgroundColor(Color.RED);
                        Toast.makeText(getApplicationContext(), R.string.message_cannot_move_tile,
                                Toast.LENGTH_SHORT).show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                subImg.setBackgroundColor(Color.LTGRAY);
                            }
                        }, 500);
                    }

                    return true;
                }
            });

        }

    }

    /**
     * Summary: Extends the onCreate method from class parent
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To prevent screen repaint on orientation change
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.p1_grid_layout);

        this.initComponents();
        this.newGame();
        this.attachListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
