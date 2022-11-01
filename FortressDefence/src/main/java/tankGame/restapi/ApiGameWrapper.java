package tankGame.restapi;

import tankGame.model.Game;

/**
 * Wrapper class for the REST API to define object structures required by the front-end.
 * HINT: Create static factory methods (or constructors) which help create this object
 *       from the data stored in the model, or required by the model.
 */
public class ApiGameWrapper {
    public int gameNumber;
    public boolean isGameWon;
    public boolean isGameLost;
    public int fortressHealth;
    public int numTanksAlive;

    // Amount of damage that the tanks did on the last time they fired.
    // If tanks have not yet fired, then it should be an empty array (0 size).
    public int[] lastTankDamages;

    public ApiGameWrapper(int gameNumber, boolean isGameWon, boolean isGameLost, int fortressHealth, int numTanksAlive, int[] lastTankDamages) {
        this.gameNumber = gameNumber;
        this.isGameWon = isGameWon;
        this.isGameLost = isGameLost;
        this.fortressHealth = fortressHealth;
        this.numTanksAlive = numTanksAlive;
        this.lastTankDamages = lastTankDamages;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }

    public boolean isGameWon() {
        return isGameWon;
    }

    public void setGameWon(boolean gameWon) {
        isGameWon = gameWon;
    }

    public boolean isGameLost() {
        return isGameLost;
    }

    public void setGameLost(boolean gameLost) {
        isGameLost = gameLost;
    }

    public int getFortressHealth() {
        return fortressHealth;
    }

    public void setFortressHealth(int fortressHealth) {
        this.fortressHealth = fortressHealth;
    }

    public int getNumTanksAlive() {
        return numTanksAlive;
    }

    public void setNumTanksAlive(int numTanksAlive) {
        this.numTanksAlive = numTanksAlive;
    }

    public int[] getLastTankDamages() {
        return lastTankDamages;
    }

    public void setLastTankDamages(int[] lastTankDamages) {
        this.lastTankDamages = lastTankDamages;
    }
}
