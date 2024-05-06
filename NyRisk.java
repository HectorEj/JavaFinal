import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class NyRisk {
    class Player {
        private String name;
        private String color;
        private int armies;
        private ArrayList<Territory> territories;

        public Player(String name, String color, int armies, ArrayList<Territory> territories) {
            this.name = name;
            this.color = color;
            this.armies = armies;
            this.territories = territories;
        }

        public void setName(String newName) {
            this.name = newName;
        }

        public String getName() {
            return name;
        }

        public void addTerritory(Territory territory) {
            territories.add(territory);
        }

        public void setArmies(int armies) {
            this.armies = armies;
        }

        public int getArmies() {
            return armies;
        }

        public ArrayList<Territory> getTerritories() {
            ArrayList<Territory> playerTerritories = new ArrayList<>();
            for (Territory territory : territories) {
                if (territory.getOwner() == this) {
                    playerTerritories.add(territory);
                }
            }
            return playerTerritories;
        }

    }

    class Territory {
        private String name;
        private Player owner;
        private int numArmies;
        private ArrayList<Territory> neighbors;

        public Territory(String name, Player owner, int numArmies, ArrayList<Territory> neighbors) {
            this.name = name;
            this.owner = owner;
            this.numArmies = numArmies;
            this.neighbors = neighbors;
        }

        public String getName() {
            return name;
        }

        public void addNumArmies(int num) {
            numArmies += num;
        }

        public int getNumArmies() {
            return numArmies;
        }

        public Player getOwner() {
            return owner;
        }

        public void transferOwner(Player newOwner) {
            this.owner = newOwner;
            newOwner.addTerritory(this);
        }

        public ArrayList<Territory> getTerritoryNeighbor(Player player) {
            ArrayList<Territory> neighborTerritories = new ArrayList<>();
            for (Territory territory : neighbors) {
                // System.out.println("Neighbor territory: " + territory.getName());
                if (player == null || territory.getOwner() == player) {
                    neighborTerritories.add(territory);
                }
            }
            return neighborTerritories;
        }

        public void addNeighbor(Territory neighbor) {
            neighbors.add(neighbor);
        }

    }

    class GameBoard {
        private ArrayList<Territory> territories;
        private ArrayList<Player> players;
        private Player turn;
        private Boolean gameOver;
        private Player winner;

        public GameBoard(ArrayList<Territory> territories, ArrayList<Player> players, Player turn, Boolean gameOver,
                Player winner) {
            this.territories = territories;
            this.players = players;
            this.turn = turn;
            this.gameOver = gameOver;
            this.winner = winner;

        }

        public void initializeGame() {
            Territory bronx = new Territory("The Bronx", null, 0, new ArrayList<>());
            Territory manhattan = new Territory("Manhattan", null, 0, new ArrayList<>());
            Territory brooklyn = new Territory("Brooklyn", null, 0, new ArrayList<>());
            Territory queens = new Territory("Queens", null, 0, new ArrayList<>());

            territories.add(bronx);
            territories.add(manhattan);
            territories.add(brooklyn);
            territories.add(queens);

            for (Territory territory : territories) {
                for (Territory otherTerritory : territories) {
                    if (!territory.equals(otherTerritory)) {
                        territory.addNeighbor(otherTerritory);
                    }
                }
            }

            distributeTerritories(territories, players);

            distributeStartArmies(players);

            setupPhase();

            setIsGameOver();
        }

        private void setupPhase() {
            for (Player player : players) {
                System.out.println(player.getName() + ", place your armies on your territories.");

                int remainingArmies = player.getArmies();
                Scanner scanner = new Scanner(System.in);

                while (remainingArmies > 0) {
                    System.out.println("Remaining armies: " + remainingArmies);
                    System.out.println("Your territories:");
                    for (Territory territory : player.getTerritories()) {
                        System.out.println("- " + territory.getName() + " (Armies: " + territory.getNumArmies() + ")");
                    }
                    System.out.print("Enter the name of the territory to place armies on: ");
                    String territoryName = scanner.nextLine();

                    int numArmies = 0;

                    System.out.print("Enter the number of armies to place: ");
                    while (!scanner.hasNextInt()) {
                        System.out.println("Enter a valid number, dummy");
                        System.out.print("Enter the number of armies to place: ");
                        scanner.next();
                    }
                    numArmies = scanner.nextInt();
                    scanner.nextLine();

                    Territory selectedTerritory = null;
                    for (Territory territory : player.getTerritories()) {
                        if (territory.getName().equalsIgnoreCase(territoryName)) {
                            selectedTerritory = territory;
                            break;
                        }
                    }

                    if (selectedTerritory != null && numArmies <= remainingArmies) {
                        selectedTerritory.addNumArmies(numArmies);
                        remainingArmies -= numArmies;
                    } else {
                        System.out.println("Invalid territory or number of armies. Please try again.");
                    }
                }
            }
        }

        private void distributeTerritories(ArrayList<Territory> territories, ArrayList<Player> players) {
            for (int i = 0; i < territories.size(); i++) {
                Territory territory = territories.get(i);
                Player player = players.get(i % players.size());
                territory.transferOwner(player);
                // player.addTerritory(territory);
            }
        }

        private void distributeStartArmies(ArrayList<Player> players) {
            int totalArmies = 20;
            int armiesPerPlayer = totalArmies / players.size();
            for (Player player : players) {
                player.setArmies(armiesPerPlayer);
            }
        }

        private void distributeArmies(Player player) {
            int remainingArmies = player.getArmies();
            Scanner scanner = new Scanner(System.in);

            while (remainingArmies > 0) {
                System.out.println("Remaining armies: " + remainingArmies);
                System.out.println("Your territories:");
                for (Territory territory : player.getTerritories()) {
                    System.out.println("- " + territory.getName() + " (Armies: " + territory.getNumArmies() + ")");
                }

                System.out.print("Enter the name of the territory to place armies on: ");
                String territoryName = scanner.nextLine();

                System.out.print("Enter the number of armies to place: ");
                int numArmies = scanner.nextInt();
                scanner.nextLine();

                Territory selectedTerritory = null;
                for (Territory territory : player.getTerritories()) {
                    if (territory.getName().equalsIgnoreCase(territoryName)) {
                        selectedTerritory = territory;
                        break;
                    }
                }

                if (selectedTerritory != null && numArmies <= remainingArmies) {
                    selectedTerritory.addNumArmies(numArmies);
                    remainingArmies -= numArmies;
                } else {
                    System.out.println("Invalid territory or number of armies. Please try again.");
                }
            }
        }

        private void nextTurn() {
            int currentPlayer = players.indexOf(turn);
            int nextPlayer = (currentPlayer + 1) % players.size();
            turn = players.get(nextPlayer);
        }

        private void resolveAttack(Player attacker, Territory attackSource, Territory attackDest, int numArmies) {
            Random rand = new Random();

            int attacker_roll = Math.min(numArmies, 3);
            int attacker_high_roll = 0;
            for (int i = 0; i < attacker_roll; i++) {
                int new_roll = rand.nextInt(6) + 1;
                attacker_high_roll = Math.max(attacker_high_roll, new_roll);
                System.out.println(attacker.getName() + " rolled " + new_roll);
            }

            int defender_roll = Math.min(attackDest.getNumArmies(), 2);
            int defender_high_roll = 0;
            for (int i = 0; i < defender_roll; i++) {
                int new_roll = rand.nextInt(6) + 1;
                defender_high_roll = Math.max(defender_high_roll, new_roll);
                System.out.println(attackDest.getOwner().getName() + " rolled " + new_roll);
            }

            if (attacker_high_roll >= defender_high_roll) {
                System.out.println(attackDest.getOwner().getName() + " lost 1 army!");
                attackDest.addNumArmies(-1);
            } else {
                System.out.println(attackSource.getOwner().getName() + " lost 1 army!");
                attackSource.addNumArmies(-1);
            }

            if (attackDest.getNumArmies() <= 0) {
                attackDest.transferOwner(attacker);
                attackDest.addNumArmies(4);
                System.out.println(attacker.getName() + " took " + attackDest.getName());
            }
        }

        public boolean attackTerritory(Player attacker, String attackSourceName, String attackDestName, int numArmies) {
            Territory attackSource = null;
            Territory attackDest = null;
            for (Territory territory : territories) {
                if (territory.getName().equalsIgnoreCase(attackSourceName)) {
                    attackSource = territory;
                }
                if (territory.getName().equalsIgnoreCase(attackDestName)) {
                    attackDest = territory;
                }
            }

            if (attackSource == null || attackDest == null) {
                System.out.println("Invalid source or destination territory.");
                return false;
            }

            if (!attackSource.getOwner().equals(attacker)) {
                System.out.println("Source territory is not owned by the current player.");
                return false;
            }

            if (attackDest.getOwner().equals(attacker)) {
                System.out.println("Destination Territory is owned by the current player.");
            }

            boolean isNeighbor = false;
            for (Territory territory : attackSource.getTerritoryNeighbor(null)) {
                // System.out.println(territory.getName());
                if (territory.getName().equalsIgnoreCase(attackDestName)) {
                    isNeighbor = true;
                }
            }

            if (!isNeighbor) {
                System.out.println("Destination territory is not adjacent to the source territory.");
                return false;
            }

            if (attackSource.getNumArmies() <= numArmies) {
                System.out.println("Insufficient armies in the source territory.");
                return false;
            }

            while (attackSource.getNumArmies() > 1 && attackDest.getNumArmies() > 0
                    && attackDest.getOwner() != attackSource.getOwner()) {
                resolveAttack(attacker, attackSource, attackDest, numArmies);
            }

            return true;
        }

        private boolean checkForWinner(Player player) {
            if (player.getTerritories().isEmpty()) {
                return false;
            }
            for (Territory territory : territories) {
                if (!territory.getOwner().equals(player)) {
                    return false;
                }
            }
            return true;
        }

        private void Reinforcements(Player player) {
            int numOfArmies = player.getTerritories().size() / 3;
            player.setArmies(player.getArmies() + numOfArmies);

        }

        private void moveArmies(Territory source, Territory destination, int numArmies) {
            System.out.println("Source territory name: " + source.getName());
            System.out.println("Destination territory name: " + destination.getName());
            System.out.println("Source territory owner: " + source.getOwner().getName());
            System.out.println("Destination territory owner: " + destination.getOwner().getName());
            if (source.getOwner() != turn) {
                System.out.println("Source territory is not owned by the current player.");
                return;
            }
            if (source.getOwner() != destination.getOwner()) {
                System.out.println("Source and destination territories are not owned by the player.");
                return;
            }
            if (source.getNumArmies() <= numArmies) {
                System.out.println("Insufficient armies in the source territory.");
                return;
            }
            if (numArmies <= 0) {
                System.out.println("Invalid number of armies to move.");
                return;
            }

            source.addNumArmies(-numArmies);
            destination.addNumArmies(numArmies);
        }

        public void endGame(Player winner) {
            gameOver = true;
            this.winner = winner;
            System.out.println("Game Over! The winner is: " + winner.getName());
        }

        public void OwnedTerritories(Player player) {
            System.out.println("Territories owned by " + player.getName() + ":");
            for (Territory territory : player.getTerritories()) {
                System.out.println(territory.getName());
            }
        }

        public ArrayList<Player> getPlayers() {
            return players;
        }

        public Player getWinner() {
            return winner;
        }

        public void setIsGameOver() {
            gameOver = false;
        }

        public boolean isGameOver() {
            if (gameOver == true) {
                return true;
            }
            return false;
        }

        public void playerInfo(Player player) {
            System.out.println("Player: " + player.getName());
            System.out.println("Owned Territories:");
            ArrayList<Territory> tempTerritories = new ArrayList<Territory>();
            for (Territory territory : player.getTerritories()) {
                if (!tempTerritories.contains(territory)) {
                    tempTerritories.add(territory);
                }
            }
            for (Territory territory : tempTerritories) {
                System.out.println("- " + territory.getName() + " - Armies: " + territory.getNumArmies());
            }
            System.out.println("Territories That Can Be Attacked:");
            for (Territory territory : player.getTerritories()) {
                ArrayList<Territory> neighbors = territory.getTerritoryNeighbor(null);
                for (Territory neighbor : neighbors) {
                    if (!neighbor.getOwner().equals(player)) {
                        System.out.println("- " + neighbor.getName() + " - Armies: " + neighbor.getNumArmies());
                    }
                }
            }
        }

    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.print("Enter Player 1's name: ");
        String p1Name = scan.nextLine();

        System.out.print("Enter Player 1's color: ");
        String p1Color = scan.nextLine();

        System.out.print("Enter Player 2's name: ");
        String p2Name = scan.nextLine();

        System.out.print("Enter Player 2's color: ");
        String p2Color = scan.nextLine();

        NyRisk nyRisk = new NyRisk();
        NyRisk.Player player1 = nyRisk.new Player(p1Name, p1Color, 20, new ArrayList<>());
        NyRisk.Player player2 = nyRisk.new Player(p2Name, p2Color, 20, new ArrayList<>());

        ArrayList<NyRisk.Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        ArrayList<NyRisk.Territory> territories = new ArrayList<>();
        NyRisk.GameBoard gameBoard = nyRisk.new GameBoard(territories, players, null, false, null);
        gameBoard.initializeGame();

        boolean is_first_turn = true;

        while (!gameBoard.isGameOver()) {
            for (NyRisk.Player currentPlayer : gameBoard.getPlayers()) {
                System.out.println(currentPlayer.getName() + "'s turn.");

                if (!is_first_turn) {
                    gameBoard.Reinforcements(currentPlayer);
                    gameBoard.distributeArmies(currentPlayer);
                }

                boolean hasAttacked = false;
                while (!hasAttacked) {
                    gameBoard.playerInfo(currentPlayer);

                    System.out.print("Do you want to attack? (yes/no): ");
                    String attackChoice = scan.nextLine().toLowerCase();

                    if (attackChoice.equals("yes")) {

                        System.out.print("Enter source territory: ");
                        String sourceName = scan.nextLine().toLowerCase();
                        System.out.print("Enter destination territory: ");
                        String destName = scan.nextLine().toLowerCase();
                        System.out.print("Enter number of armies to move: ");
                        while (!scan.hasNextInt()) {
                            System.out.print("Enter number of armies to move: ");
                            scan.next();
                        }
                        int numArmies = scan.nextInt();

                        if (gameBoard.attackTerritory(currentPlayer, sourceName, destName, numArmies)) {
                            hasAttacked = true;
                        }
                    } else if (attackChoice.equals("no")) {
                        break;
                    } else {
                        System.out.println("Invalid choice. Please enter 'yes' or 'no'.");
                    }

                }
                if (gameBoard.checkForWinner(currentPlayer)) {
                    gameBoard.endGame(currentPlayer);
                    break;
                }

                gameBoard.nextTurn();
            }
            is_first_turn = false;
        }
    }
}