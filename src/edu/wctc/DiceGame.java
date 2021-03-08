package edu.wctc;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiceGame {
    private final List<Player> players = new ArrayList<>();
    private final List<Die> dice = new ArrayList<>();
    private final int maxRolls;
    private Player currentPlayer;

    public DiceGame(int countPlayers, int countDice, int maxRolls) throws IllegalArgumentException {
        if (countPlayers < 2) {
            throw new IllegalArgumentException();
        } else {
            this.maxRolls = maxRolls;

            for (int i = 0; i < countPlayers; i++) {
                Player playerToAdd = new Player();
                players.add(playerToAdd);
            }

            for (int i = 0; i < countDice; i++) {
                Die dieToAdd = new Die(6);
                dice.add(dieToAdd);
            }
        }
    }

    private boolean allDiceHeld() {
        List<Die> diceHeld = dice.stream()
                .filter(Die::isBeingHeld)
                .collect(Collectors.toList());

        if ((long) diceHeld.size() < 3) {
            return false;
        }

        Predicate<Die> diceMatch = d -> d.getFaceValue() >= 4 && d.getFaceValue() <= 6;

        return diceHeld.stream()
                .allMatch(diceMatch);
    }

    public boolean autoHold(int faceValue) {
        Optional<Die> matchingDie = dice.stream()
                .filter(d -> d.getFaceValue() == faceValue)
                .findFirst();

        if (matchingDie.isPresent()) {
            Die die = matchingDie.get();
            if (!die.isBeingHeld()) {
                die.holdDie();
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean currentPlayerCanRoll() {
        return currentPlayer.getRollsUsed() < maxRolls && !allDiceHeld();
    }

    public int getCurrentPlayerNumber() {
        return currentPlayer.getPlayerNumber();
    }

    public int getCurrentPlayerScore() {
        return currentPlayer.getScore();
    }

    public String getDiceResults() {
        return Stream.of(dice)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    public String getFinalWinner() {
        Player winner = Collections.max(players,
                Comparator.comparingInt(Player::getWins));

        return winner.toString();
    }

    public String getGameResults() {
        List<Player> sortedByScore = players.stream()
                .sorted(Comparator.comparing(Player::getScore).reversed())
                .collect(Collectors.toList());

        Player scoreMax = Collections.max(sortedByScore, Comparator.comparingInt(Player::getScore));
        int maxScore = scoreMax.getScore();

        sortedByScore.forEach(p -> {
            if (p.getScore() == maxScore) {
                p.addWin();
            } else {
                p.addLoss();
            }
        });

        return Stream.of(sortedByScore)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    private boolean isHoldingDie(int faceValue) {
        Optional<Die> matchingDie = dice.stream()
                .filter(d -> d.getFaceValue() == faceValue)
                .findFirst();

        return matchingDie.isPresent();
    }

    public boolean nextPlayer() {
        int currentPlayerNum = currentPlayer.getPlayerNumber();

        if (currentPlayerNum < players.size()) {
            currentPlayer = players.get(currentPlayerNum);
            return true;
        } else {
            return false;
        }
    }

    public void playerHold(char dieNum) {
        Optional<Die> givenDie = dice.stream()
                .filter(d -> d.getDieNum() == (int) dieNum)
                .findFirst();

        if (givenDie.isPresent()) {
            Die die = givenDie.get();

            die.holdDie();
        }
    }

    public void resetDice() {
        dice.forEach(Die::resetDie);
    }

    public void resetPlayers() {
        players.forEach(Player::resetPlayer);
    }

    public void rollDice() {
        currentPlayer.roll();
        dice.forEach(Die::rollDie);
    }

    public void scoreCurrentPlayer() {
        List<Integer> invalidDice = new ArrayList<>(Arrays.asList(4, 5, 6));

        if (allDiceHeld()) {
            int scoreToAssign = 0;

            for (Die d : dice) {
                if(!d.isBeingHeld()) {
                    scoreToAssign += d.getFaceValue();
                }
            }

            currentPlayer.setScore(currentPlayer.getScore() + scoreToAssign);
        }
    }

    public void startNewGame() {
        currentPlayer = players.get(0);

        resetPlayers();
    }
}
