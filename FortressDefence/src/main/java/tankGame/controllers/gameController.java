package tankGame.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tankGame.model.CellLocation;
import tankGame.model.Game;
import tankGame.model.GameBoard;
import tankGame.restapi.ApiBoardWrapper;
import tankGame.restapi.ApiGameWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import com.google.gson.Gson;

@RestController
public class gameController {

    private List<Game> gameList = new ArrayList<Game>();
    private List<ApiGameWrapper> gameWrapperList = new ArrayList<>();
    private AtomicLong nextID = new AtomicLong();

    @GetMapping("/api/about")
    public String getAbout() {
        System.out.println("Called getAbout()");
        return "John Mitton";
    }

    @GetMapping("api/games")
    public List<ApiGameWrapper> getGames() {
        System.out.println("Called getGames()");
        return gameWrapperList;
    }

    @PostMapping("/api/games")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiGameWrapper makeGame() {
        System.out.println("Called makeGame()");
        Game game = new Game(5);

        gameList.add(game);

        int gameNumber = gameList.size() - 1;
        boolean isGameWon = false;
        boolean isGameLost = false;
        int fortressHealth = game.getFortressHealth();
        int numTanksAlive = game.getNumTanksAlive();
        int[] lastTankDamages = game.getLatestTankDamages();

        System.out.println("Game: " + gameNumber + "  Fortress Health: " + fortressHealth + "  Tanks Remaining: " + numTanksAlive);

        ApiGameWrapper gameWrapper = new ApiGameWrapper(gameNumber, isGameWon, isGameLost, fortressHealth, numTanksAlive, lastTankDamages);
        gameWrapperList.add(gameWrapper);
        return gameWrapper;
    }

    @GetMapping("/api/games/{id}")
    public ApiGameWrapper getGameWrapper(@PathVariable("id") long gameID) {
        System.out.println("Called getGameWrapper()");
        for(ApiGameWrapper game : gameWrapperList) {
            if(game.gameNumber == gameID) {
                return game;
            }
        }
        throw new IllegalArgumentException();
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Request ID Not Found")
    @ExceptionHandler(IllegalArgumentException.class)
    public void badIDHandler() {
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Shot out of bounds")
    @ExceptionHandler(IllegalStateException.class)
    public void badMoveHandler() {
    }

    @GetMapping("api/games/{id}/board")
    public String gameState(@PathVariable("id") long gameID) {
        System.out.println("Called gameState()");
        Gson gson = new Gson();

        System.out.println("gameID: " + gameID);
        System.out.println("gameList.size(): " + gameList.size());

        if((int)gameID < gameList.size()) {
            GameBoard gameBoard = gameList.get((int) gameID).getBoard();
            int boardWidth = gameBoard.NUMBER_COLS;
            int boardHeight = gameBoard.NUMBER_ROWS;
            String[][] cellStates = gameBoard.getGameBoardAsString();

            ApiBoardWrapper boardWrapper = new ApiBoardWrapper(boardWidth, boardHeight, cellStates);

            String json = gson.toJson(boardWrapper);

            return json;
        }
        throw new IllegalArgumentException();
    }

    @PostMapping("/api/games/{id}/moves")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void makeMove(@PathVariable("id") long gameID, @RequestBody String location) {
        System.out.println("Called makeMove()");
        System.out.println("location: " + location);
        JsonObject jsonObjectLocation = JsonParser.parseString(location).getAsJsonObject();

        int y = jsonObjectLocation.get("row").getAsInt();
        int x = jsonObjectLocation.get("col").getAsInt();

        System.out.println("location: (" + x + ", " + y + ")");

        if((int)gameID < 0 || (int)gameID > gameList.size()) {
            throw new IllegalArgumentException();
        }

        Game game = gameList.get((int)gameID);

        if(!game.validShot(x, y)) {
            throw new IllegalStateException();
        }

        game.recordPlayerShot(new CellLocation(y, x));

        game.fireTanks();

        ApiGameWrapper gameWrapper = gameWrapperList.get((int)gameID);

        gameWrapper.isGameWon = game.hasUserWon();
        gameWrapper.isGameLost = game.hasUserLost();
        gameWrapper.fortressHealth = game.getFortressHealth();
        gameWrapper.numTanksAlive = game.getNumTanksAlive();
        gameWrapper.lastTankDamages = game.getLatestTankDamages();
    }

    @PostMapping("/api/games/{id}/cheatstate")
    public void setCheat(@PathVariable("id") long gameID, @RequestBody String cheatString) {
        System.out.println("Called setCheat()");
        if((int)gameID < 0 || (int)gameID > gameList.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if(cheatString.contains("SHOW_ALL")) {
            gameList.get((int) gameID).setCheat();
            throw new ResponseStatusException(HttpStatus.ACCEPTED);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
