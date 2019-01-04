import java.util.InputMismatchException;
import java.util.Scanner;

public class BurgerMain {
	
	//Instance Variables
	private Tile [][] board;
	private final static int size = 20;
	private final static double houseRatio = 0.6;
	private final static double roadRatio = 0.33;
	private final static int TO_UPPERCASE = 65;
	private final static int TO_LOWERCASE = 97;
	private final String letters = "abcdefghijklmnopqrstABCDEFGHIJKLMNOPQRST"; //For 20-size Board
	private String townName;
	private int money;
	private String companyName;
	private Scanner sc;
	private int date;
	private boolean gameActive;
	
	//Constructor
	public BurgerMain() { 
		sc = new Scanner(System.in);
		//size = userIntInput(1, 100, "\n" + "Input A Board Size (1 - 100)");
		date = 0;
		money = 5000;
		
		startGame();
		
		//Run Main Game
//		gameActive = true;
//		while (gameActive) {
//			turn();
//		}
		
		System.out.println("Thanks for Playing Burger Tycoon.");
		sc.close();
	}
	
	//Main Game Startup Actions
	private void startGame() {
		
		//Build Board
		int roadCount = 0;
		while (roadCount < 2) {
			roadCount = constructBoard();
		}
		System.out.println("\nWelcome to town!");
		viewBoard(true);
		
		//Name Town
		townName = userStringInput("Give this town a name:", false);
	
		//Name Company
		companyName = userStringInput("Give your company a name:", false);
		
		//Place First Restaurant
		System.out.println("\n" + companyName + " needs its first restaurant. Place it on an empty tile:");
		boolean placed = false;
		boolean first = true;
		while (!placed) {
			if (!first) {
				System.out.println("That tile is occupied. Choose an empty tile.");
			}
			first = false;
			viewBoard(true);
			
			int col = (userStringInput("Column:", true).charAt(0)) - TO_LOWERCASE;
			int row = userIntInput(0, size, "Row:");
			
			placed = placeRestaurant(row, col);
		}	
		//Place First Restaurant
		System.out.println("\n" + companyName + " needs its first restaurant. Place it on an empty tile:");
	    placed = false;
		first = true;
		while (!placed) {
			if (!first) {
				System.out.println("That tile is occupied. Choose an empty tile.");
			}
			first = false;
			viewBoard(true);
			
			int col = (userStringInput("Column:", true).charAt(0)) - TO_LOWERCASE;
			int row = userIntInput(0, size, "Row:");
			
			placed = placeRestaurant(row, col);
		}	
		
		System.out.println("\nCongratulations! " + companyName + " has inagurated its first location!");
		viewBoard(false);
		viewProximity();
		

	}
	
	
	//Build Starting Town
	private int constructBoard() {
		board = new Tile[size][size];
		int roadCount = 0;
		//Place Houses
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				board[row][col] = new Tile();
				if (Math.random() < houseRatio) {
					board[row][col].type = '^';
				}
			}
		}
		
		//Horizontal Roads
		for (int row = 2; row < size - 2; row++) {
			if (Math.random() < roadRatio) {
				roadCount++;
				for (int col = 0; col < size; col++) {
					board[row][col].type = '—';
				}
				row+= 2;
			}
		}
		
		//Vertical Roads
		for (int col = 2; col < size - 2; col++) {
			if (Math.random() < roadRatio) {
				roadCount++;
				for (int row = 0; row < size; row++) {
					//if (board[row][col].type == '—') {
					//	board[row][col].type = '+';
					//} else {
						board[row][col].type = '|';
					//}
				}
				col+= 2;
			}
		}
	
		return roadCount;
	}
	
	
	
	//Display the board
	//True to display selection helper, false otherwise
	private void viewBoard(boolean selection) {
		if (selection) {
			System.out.print("\n");
			for (int row = 0; row < size; row++) {
				System.out.print((char)(row + TO_UPPERCASE) + " ");
			}
			System.out.print("\n" + "\n");
		}
		
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				System.out.print(board[row][col].type);
				System.out.print(" ");
			}
			if (selection) {
				System.out.print("  " + row);
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	//Data View: Awareness
	private void viewAwareness() {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				System.out.print(board[row][col].getAwareness());
				System.out.print(" ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	private void viewProximity() {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) { 
				System.out.print(board[row][col].getProximity());
				System.out.print(" ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	//Status bar shown at the beginning of round
	private void viewStatus() {
		System.out.println("Day " + date);
		System.out.println(companyName + "   $" + money);
		
	}
	
	//Place a restaurant on the map
	//Adjusts Awareness Values appropriately
	//Return false if restaurant cannot be placed on given tile
	private boolean placeRestaurant(int row, int col) {
		
		//Tile Occupied
		if (!(board[row][col].type == ' ')) {
			return false;
		
		//Place Restaurant
		} else {
			board[row][col].type = 'B';
			
			//Change Awareness for Surrounding Area
			changeAwarenessHelper(row - 2, row + 2, col - 2, col + 2, 1);
			changeAwarenessHelper(row - 1, row + 1, col - 1, col + 1, 1);	
			
			//Change Awareness for road adjacency
			//Horizontal Below
			if (row < (size - 1) && board[row + 1][col].type == '—') {
				changeAwarenessHelper(row, row + 2, 0, size - 1, 1);
			} 
			//Horizontal Above
			if (row > 0 && board[row - 1][col].type == '—') {
				changeAwarenessHelper(row - 2, row, 0, size - 1, 1);
			}
			//Vertical to the Left
			if (col > 0 && board[row][col - 1].type == '|') {
				changeAwarenessHelper(0, size - 1, col - 2, col, 1);
			}
			//Vertical to the Right
			if (col < (size - 1) && board[row][col + 1].type == '|') {
				changeAwarenessHelper(0, size - 1, col, col + 2, 1);
			}
			
			//Update Proximity
			calculateProximity();
			
			return true;
		}
	}
	
	//Recalculate Proximity Values
	private void calculateProximity() {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board[row][col].type == '^') {
					int[] result = {10};
					calculateProximityHelper(row, col, -2, result);
					board[row][col].changeProximity(result[0]);
				}
			}
		}
	}
	
	//Recursive Method for Above
	private void calculateProximityHelper(int row, int col, int distance, int[] result) {
		//Check bounds
		if(row < 0 || row >= size || col < 0 || col >= size) {
			return;
		}
		
		//Update distance
		if (board[row][col].type == '|' || board[row][col].type == '—' || board[row][col].type == 'B') {
			distance += 1;
		} else {
			distance += 2;
		}
		
		//Base Case 1: At restaurant
		if (board[row][col].type == 'B') {
			if (distance < result[0]) {
				result[0] = distance;
			}
			return;
			
		//Base Case 2: Out of Moves
		} else if (distance > 9) {
			return;
			
		//Recurse
		} else {
			calculateProximityHelper(row + 1, col, distance, result);
			calculateProximityHelper(row - 1, col, distance, result);
			calculateProximityHelper(row, col + 1, distance, result);
			calculateProximityHelper(row, col - 1, distance, result);
		}
	}
	
	//Change Awareness of a Section, Start and Stop indices inclusive
	//Avoids OutOfBoundsException
	private void changeAwarenessHelper(int rowStart, int rowStop, int colStart, int colStop, int change) {
		for (int row = rowStart; row <= rowStop; row++) {
			for (int col = colStart; col <= colStop; col++) {
				if (row >= 0 && row < size && col >= 0 && col < size) {
					board[row][col].changeAwareness(change);
				}
			}
		}
	}
	
	//Return user input when specifically asking for an Integer.
	//Lower and Upper bounds inclusive
	private int userIntInput(int lowerBound, int upperBound, String message) {
		int input = -1;
		while (!(input >= lowerBound && input <= upperBound)) {
		    try {
		        System.out.println(message);
		        input = sc.nextInt();
		    } catch (InputMismatchException e) {
		        System.out.println("Invalid: Please Enter A Number Between " + lowerBound + " and " + upperBound);
		    }	
		    sc.nextLine();
		}		
		return input;
	}
	
	//Return user input when asking for a string
	//Restricted: Limit response to letters in range
	//Unrestricted: Free response
	private String userStringInput(String message, boolean restricted) {
		String input = "";
		System.out.println(message);
		
		//Unrestricted
		if (!restricted) {	
			input = sc.nextLine();	
			return input;
			
		//Restricted to Letter
		} else {
			boolean first = true;
			while (letters.indexOf(input) == -1 || input.length() != 1) {
				if (!first) {
				System.out.println("Please input a letter between " + letters.charAt(0) + " and " + letters.substring(letters.length() - 1).toLowerCase());
				}
				first = false;
			    input = sc.nextLine();
			}		
			return input.toLowerCase();
		}
	}
}
