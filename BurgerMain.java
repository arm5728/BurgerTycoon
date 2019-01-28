import java.util.TreeMap;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BurgerMain {
	
	//"Under the Hood" Variables, Class Constants
	private String townName;
	private String companyName;
	private boolean tutorial;
	private boolean gameActive;
	private boolean restartGame;
	private TreeMap <String , Restaurant> restaurants = new TreeMap <String , Restaurant>();
	private Report yesterdaysReport;
	private Scanner sc;
	private final static int TO_UPPERCASE = 65;
	private final static int TO_LOWERCASE = 97;
	
	//Board Variables & Constants
	private Tile [][] board;
	private final static double HOUSE_RATIO = 0.55;
	private final static double ROAD_RATIO = 0.3;
	private final static int BOARD_SIZE = 20;
	private final static String LETTERS = "abcdefghijklmnopqrstABCDEFGHIJKLMNOPQRST"; //20 possible letters for a 20-size Board
	
	//Game Variables & Constants
	private int money;
	private int date;
	private int burgersSold;
	private final static int RESTAURANT_COST = 1000;
	private final static int STAFF_HIRE_COST = 50;
	private final static int SEVERANCE_COST = 20;
	private final static int STAFF_WORK = 5;
	private final static int STAFF_WAGE = 25;
	private final static int BURGER_COST = 10;
	private final static int RENT = 50;
	private final static int STARTING_MONEY = 2000;
	
	//Ad Campaigns Variables & Constants
	private int campaignTicker;
	private String campaignType;
	private int campaignPayment;
	private int campaignEffect;
	private final static int INTERNET_AD_COST = 25;
	private final static int MAGAZINE_AD_COST = 50;
	private final static int RADIO_AD_COST = 100;
	private final static int TV_AD_COST = 250;
	private final static int CAMPAIGN_DURATION = 5;

	//Bank Variables & Constants
	private int owedToBank;
	private int creditRating;
	private double loanPayments;
	private int loanPaymentsRemaining;
	private int bankruptcyTicker;
	private final static int STARTING_CREDIT_RATING = 10;
	private final static double CREDIT_VALUE = 100.0;
	private final static double LOAN_TIME = 10.0;
	private final static double INTEREST_RATE = 1.20;
	private final static int BANKRUPTCY_LIMIT = 5;

	
	//Constructor
	public BurgerMain(Scanner scanner) { 
		sc = scanner;
		
		//Initialize required variables
		money = STARTING_MONEY;
		bankruptcyTicker = BANKRUPTCY_LIMIT;
		creditRating = STARTING_CREDIT_RATING;
		date = 1;
		campaignTicker = 0;
		campaignPayment = 0;
		owedToBank = 0;
		loanPayments = 0;

		
		//Initial Actions
		startGame();
		
		//Run Main Game
		gameActive = true;
		while (gameActive) {
			menu();
		}
		
		//End Game Actions
		System.out.println("\nThanks for Playing Burger Tycoon.");
		restartGame = (userStringInput("Press Any Key to Start New Game.", false) != null);
	}
	
	
	//Allows game to restart from driver
	public boolean continueGame() {
		return restartGame;
	}
	
	
	//Simulate this day
	private void simulate() {
		//Overwrite Previous Report
		yesterdaysReport = new Report();
		
		//Track Wages and Rent
		int totalWages = 0;
		int totalRent = 0;
		
		//Loans
		if (loanPaymentsRemaining > 0) {
			money -= loanPayments;
			owedToBank -= loanPayments;
			creditRating += (loanPayments / CREDIT_VALUE);
		}
		
		//COSTS:
		//Rent and Staff
		for(String restaurant : restaurants.keySet()) {
			money -= RENT;
			money -= restaurants.get(restaurant).staff * STAFF_WAGE;
			totalWages += restaurants.get(restaurant).staff * STAFF_WAGE;
			totalRent += RENT;
			
			//Write to Main Report
			yesterdaysReport.expenses += (RENT + restaurants.get(restaurant).staff * STAFF_WAGE);
		}
		
		if (loanPaymentsRemaining != 0) {
			yesterdaysReport.expenses += loanPayments;
			loanPaymentsRemaining--;
			yesterdaysReport.news.add("Loan Payments Remaining: " + loanPaymentsRemaining + " at $" + (int)loanPayments + " each");
		} else {
			loanPayments = 0;
			owedToBank = 0;
		}
		
		//Marketing
		if (campaignTicker != 0) {
			money -= campaignPayment;
			yesterdaysReport.expenses += campaignPayment;
		}
		
		//Write to ExpenseBreakdown
		yesterdaysReport.expenseBreakdown.add("Rent: \t\t$" + totalRent);
		yesterdaysReport.expenseBreakdown.add("Wages: \t\t$" + totalWages);
		if(campaignPayment != 0) {
			yesterdaysReport.expenseBreakdown.add("Marketing: \t$" + campaignPayment);			
		} else if (owedToBank != 0) {
			yesterdaysReport.expenseBreakdown.add("Loan Payments: \t$" + (int)loanPayments);
		}
		
		
		simulateSales(false);
		
		//Write Restaurant Attendance to Report
		//Reset Staff Points After that
		for (String location : restaurants.keySet()) {
			Restaurant restaurant = restaurants.get(location);
			
			//Staff Usage Report
			String performance = "";
			if (restaurant.visitors > restaurant.staff * STAFF_WORK) {
				performance = restaurant.visitors + " Visitors / Over Capacity! (" + (restaurant.visitors - (restaurant.staff * STAFF_WORK)) + " Customer(s) Unserved)";
			} else {
				performance = restaurant.visitors + " Visitors / All Served.";
			}
				
			yesterdaysReport.restaurantPerformance.put(location, performance);
			
			
			//Reset Staff Points
			restaurant.resetStaffPoints();
		}
		
		
		//Tick Campaign Duration
		if (campaignTicker == 1) {
			campaignTicker--;
			campaignPayment = 0;
			changeAwarenessHelper(0, BOARD_SIZE, 0, BOARD_SIZE, (campaignEffect * -1));
			campaignEffect = 0;
			yesterdaysReport.news.add((campaignType + " campaign has ended!"));
		} else if (campaignTicker != 0) {
			campaignTicker--;
			yesterdaysReport.news.add("Days left in " + campaignType + " campaign: " + campaignTicker);
		} 
		
		//Check for Bankruptcy or Recovery From Bankruptcy
		if (money >= 0) {		
			bankruptcyTicker = BANKRUPTCY_LIMIT;
		} else {
			bankruptcy();
		}
		
		if(gameActive) {
			date++;
			yesterdaysReport.printReport();
		}
	}
	
	private void simulateSales(boolean debug) {
		//Simulate Sales
		
		if (debug) viewClosest();
		
		//Loop through board
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				
				Tile current = board[row][col];
				if (current.type == '^' && current.getProximity() != '0' && current.getAwareness() != '0') {
					
					//Calculate Attendance Probability
					double attendanceChance = ((charToInt(current.getAwareness()) * 2) + (10 - charToInt(current.getProximity()))) / 27.0;
					if (debug) System.out.println(getLocationCode(row, col) + ": " + attendanceChance);
					
					//House Decides to Go to Restaurant
					if (Math.random() < attendanceChance) {
						Restaurant location = restaurants.get(current.getNearestRestaurant());
						
						//Loop through people
						for (int people = 0; people < charToInt(current.getMembers()); people++) {
							location.attendance(1);
							if (location.staffPointsLeft >= 0) {
								burgersSold++;
								money += BURGER_COST;
								yesterdaysReport.revenue += BURGER_COST; 
							}
						}
					}
				}
			}
		}
	}
	
	
	private void bankruptcy() {
		if (bankruptcyTicker == BANKRUPTCY_LIMIT) {
			yesterdaysReport.news.add("You are in debt! You have " + BANKRUPTCY_LIMIT + " days to clear it.");
		} else if (bankruptcyTicker == 0) { 
			System.out.println("You have gone bankrupt! Game Over.\n\nDays In Business: " + date + "\nBurgers Sold: " + burgersSold);
			gameActive = false;
		} else {
			yesterdaysReport.news.add("You are still in debt! You have " + bankruptcyTicker + " day(s) to clear it");
		}
		bankruptcyTicker--;
	}
	
	//Main Game Startup Actions
	private void startGame() {
		
		//Enable/Disable Tutorial
		System.out.println("Enable Tutorial? (Highly Recommended for First Game)");
		tutorial = userBinaryInput("Input Y or N");
		if (tutorial) {
			System.out.println("Tutorial Enabled\n");
		} else {
			System.out.println("Tutorial Disabled\n");
		}
		
		//Build Board
		int roadCount = 0;
		while (roadCount < 2) {
			roadCount = constructBoard();
		}
		viewBoard(false);
		
		//Name Town
		townName = userStringInput("Give this town a name:", false);
	
		//Name Company
		companyName = userStringInput("Give your company a name:", false);
		
		//Place First Restaurant
		System.out.println("\n" + companyName + " needs its first restaurant. Place it on an empty tile:");
		placeRestaurant(true);
		System.out.println("\nCongratulations! " + companyName + " has inagurated its first location!");
		viewBoard(false);	
	}
		
	//Build Starting Town
	private int constructBoard() {
		board = new Tile[BOARD_SIZE][BOARD_SIZE];
		int roadCount = 0;
		//Place Houses
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				board[row][col] = new Tile();
				if (Math.random() < HOUSE_RATIO) {
					board[row][col].type = '^';
				}
			}
		}
		
		//Horizontal Roads
		for (int row = 2; row < BOARD_SIZE - 2; row++) {
			if (Math.random() < ROAD_RATIO) {
				roadCount++;
				for (int col = 0; col < BOARD_SIZE; col++) {
					board[row][col].type = '—';
				}
				row+= 2;
			}
		}
		
		//Vertical Roads
		for (int col = 2; col < BOARD_SIZE - 2; col++) {
			if (Math.random() < ROAD_RATIO) {
				roadCount++;
				for (int row = 0; row < BOARD_SIZE; row++) {
						board[row][col].type = '|';
				}
				col+= 2;
			}
		}
	
		//Set number of family members for each house
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				if (board[row][col].type == '^') {
					double rng = Math.random();
					int members;
					if (rng >= 0 && rng <= 0.33) {
						members = 1;
					} else if (rng > 0.33 && rng <= 0.66) {
						members = 2;
					} else if (rng > 0.66 && rng <= 0.8) {
						members = 3;
					} else if (rng > 0.8 && rng <= 0.9) {
						members = 4;
					} else if (rng > 0.9 && rng <= 0.975) {
						members = 5;
					} else if (rng > 0.975 && rng <= 0.990) {
						members = 6;
					} else if (rng > 0.990 && rng <= 0.995) {
						members = 7;
					} else if (rng > 0.995 && rng <= 0.9985) {
						members = 8;
					} else {
						members = 9;
					}
							
					board[row][col].setMembers(members);
				}
			}
		}
		return roadCount;
	}
	
	private void menu() {
		System.out.println("\n~~~ MAIN MENU ~~~\t\t" + companyName + " | Day " + date + " | $" + money + 
				"\n1 - Reports   \t\t\t2 - Data Views\n3 - Place New Restaurant   \t4 - Manage Restaurants\n5 - Marketing   \t\t6 - Tutorial\n7 - Bank\t\t\t8 - Simulate Next Day");
		int input = userIntInput(1, 8, " ");
		switch (input) {
			case 1: //REPORT
				if (yesterdaysReport == null) {
					System.out.println("First Day Not Simulated Yet- No Report!");
				} else {
					yesterdaysReport();
				};
				break;
			case 2: dataViewMenu();
				break;
			case 3: placeRestaurant(false);
				break;
			case 4: manageRestaurantSelector();
				break;
			case 5: marketingMenu();
				break;
			case 6: tutorialMenu();
				break;
			case 7: bank();
				break;
			case 8: simulate();
				break;
		}
	}
	
	private void bank() {
		System.out.print("\n~~ BANK ~~\nCredit Rating: " + creditRating + "\t\tMoney Owed: $" + owedToBank + 
				"\n1 - Take Out A Loan\t\t2 - Repay Loan (Save $" + (owedToBank - (int)(owedToBank/INTEREST_RATE)) + " By Paying Early)\n3 - Go Back");
		if(tutorial) {
			System.out.println("\n\n- Borrow money to finance your expansion or save you from bankruptcy\n- Higher Credit Ratings Allow You to Borrow More.\n- Repaying Loans Through Installents Improves Your Credit Rating.\n- Loan Installments must be paid daily\n- Repaying a loan manually is cheaper than paying it off through installments");
		}
		int input = userIntInput(1, 3, " ");
		switch (input) {
			case 1: takeLoan();
				break;
			case 2: repayLoan();
				break;
			case 3: menu();
				break;
		}
	}
	
	private void takeLoan() {
		if (owedToBank > 0) {
			System.out.println("You have already taken out a loan!");
		} else {
			System.out.println("\n~ TAKE OUT A LOAN ~\nYou may borrow up to $" + (int)(creditRating * CREDIT_VALUE) + ", to be paid over " + LOAN_TIME + " days at " + (int)(((INTEREST_RATE - 1.00) * 100) + 1) + "% interest.\nBorrow Amount:\n ");
			double taken = userIntInput((int)(creditRating), (int)(creditRating * CREDIT_VALUE), "Please Input a Value Between " + (int)(creditRating) + " and " + (int)(creditRating * CREDIT_VALUE) + ".");
			money += taken;
			owedToBank += (taken) * INTEREST_RATE;
			loanPayments = (owedToBank / LOAN_TIME) + 1;
			System.out.println("Successfully borrowed $" + (int)taken + ". Amount Owed: $" + owedToBank);
			loanPaymentsRemaining = 10;
		}	
	}
	
	private void repayLoan() {
		if (money < owedToBank) {
			System.out.println("Can't afford to repay loan. You Owe $" + owedToBank);
		} else if (owedToBank == 0){ 
			System.out.println("No Active Loan.");
		} else {
			money -= (owedToBank / INTEREST_RATE);
			owedToBank = 0;
			System.out.println("Loan Repaid.");
		}
	}
		
	private void yesterdaysReport() {
		System.out.println("\n~~ REPORTS ~~\n1 - Yesterday's Report\t\t2 - Expenses Breakdown\n3 - Go Back");
		int input = userIntInput(1, 3, " ");
		switch (input) {
			case 1: yesterdaysReport.printReport(); 
				break;
			case 2: yesterdaysReport.expenseBreakdown(); 
				break;
			case 3: menu();
				break;
		}
	}
	
	private void tutorialMenu() {
		System.out.println("\n~~ TUTORIAL ~~\n1 - Turn Tutorial On/Off\n2 - Go Back");
		int input = userIntInput(1, 2, " ");
		switch(input) {
			case 1: //Turn Tutorial On/Off 
				if (tutorial) {
					tutorial = false;
					System.out.println("Tutorial has been turned off.");
				} else {
					tutorial = true;
					System.out.println("Tutorial has been turned on.");
				}
				break;
			case 2: menu();
				break;
		} 
	}

	private void dataViewMenu() {
		System.out.println("\n~~ DATA VIEWS ~~\n1 - View Map\t\t2 - View Brand Awareness\n3 - View Proximity\t4 - View Population\n5 - Go Back");
		int input = userIntInput(1, 5, " ");
		switch (input) {
			case 1: viewBoard(false);
				break;
			case 2: viewAwareness();
				break;
			case 3: viewProximity();
				break;
			case 4: viewPopulation();
				break;
			case 5: menu();
				break;
		}
	}
	
	private void marketingMenu() {
		System.out.println("~~ MARKETING ~~\n1 - Media Campaign (Global Effect)\n2 - Go Back"); 
		if (tutorial) {
			System.out.println("\n- Marketing raises brand awareness, making people visit your restaurants more.");
		}
		int input = userIntInput(1, 2, " ");
		switch (input) {
			case 1: //Media Campaign
				if (campaignTicker == 0) {
					mediaAdvertisement();
				} else {
					System.out.println("Media Campaign already active: " + campaignType);
					marketingMenu();
				}
				break;
			case 2: menu();
				break;
		}
	}
	
	private void mediaAdvertisement() {
		System.out.println("\n~ MEDIA ADVERTISING ~\n" + "1 - Website Banners \t(+1 Global Brand Awareness, $" + INTERNET_AD_COST + "/week)\n2 - Magazine Column \t(+2 Global Brand Awareness, $" + MAGAZINE_AD_COST + "/week)"
				+ "\n3 - Radio Segment \t(+3 Global Brand Awareness, $" + RADIO_AD_COST + "/week)\n4 - Primetime TV Spot \t(+5 Global Brand Awareness, $" + TV_AD_COST + "/week)\n5 - Go Back.");
		if (tutorial) {
			System.out.println("\n- Each campaign has a duration of " + CAMPAIGN_DURATION + " days\n- Media Campaigns raise the brand awareness of ALL houses by a constant value\n- Only 1 media campaign may run at once.");
		}
		int input = userIntInput(1, 5, " ");
		switch(input) {
			case 1: startMediaAdvertisement("Website Banners", INTERNET_AD_COST, 1);
				break;
			case 2: startMediaAdvertisement("Magazine Column", MAGAZINE_AD_COST, 2);
				break;
			case 3: startMediaAdvertisement("Radio Segment", RADIO_AD_COST, 3);
				break;
			case 4: startMediaAdvertisement("Primetime TV Spot", TV_AD_COST, 5);
				break;
			case 5: menu();
				break;
		}
		
	}
	
	private void startMediaAdvertisement(String type, int payment, int effect) {
		campaignTicker = CAMPAIGN_DURATION;
		campaignType = type;
		campaignPayment = payment;
		campaignEffect = effect;
		changeAwarenessHelper(0, BOARD_SIZE, 0, BOARD_SIZE, effect);
		System.out.println(campaignType + " campaign launched.");
	}
	
	private void manageRestaurantSelector() {
		//What Restaurant to Manage
		System.out.println("\n~~ SELECT RESTAURANT ~~\n" + companyName + " Locations:");
		if (tutorial) {
			System.out.println("\n- Each staff member can handle " + STAFF_WORK + " visitors per day,\n- check reports to ensure your restaurants can handle their traffic.");
		}
		
		ArrayList<String> choices = new ArrayList<String>();
		
		int index = 1;
		for (String restaurant : restaurants.keySet()) {
			System.out.println(index + " - " + restaurant);
			index++;
			choices.add(restaurant);
		}
		
		int input = userIntInput(1, restaurants.size(), " ");
		manageRestaurantMenu(restaurants.get(choices.get(input - 1)));	
	}
	
	private void manageRestaurantMenu(Restaurant restaurant) {
		System.out.println("~ MANAGE " + restaurant.location + " ~");
		boolean loop = true;
		while (loop) {
			System.out.println("Staff: " + restaurant.staff + "   Daily Customer Capacity: " + (restaurant.staff * STAFF_WORK));
			System.out.println("\n1 - Hire 1 Employee ($" + STAFF_HIRE_COST + " To Train, $" + STAFF_WAGE + " Weekly Salary)\n2 - Fire 1 Employee ($" + SEVERANCE_COST + " Severance)\n3 - Go Back");
			int input = userIntInput(1, 3, " ");
			switch (input) {
				case 1: restaurant.staff++;
					money -= STAFF_HIRE_COST;
					restaurant.resetStaffPoints();
					break;
				case 2: restaurant.staff--;
					money -= SEVERANCE_COST;
					restaurant.resetStaffPoints();
					break;
				case 3: loop = false;
					menu();
					break;
			}
		}
	}
	
	//Display the board
	//True to display selection helper, false otherwise
	private void viewBoard(boolean selection) {
		if (townName != null && !selection) {
			System.out.println("\n---" + townName + "---");
		}
		if (selection) {
			System.out.print("\n");
			for (int row = 0; row < BOARD_SIZE; row++) {
				System.out.print((char)(row + TO_UPPERCASE) + " ");
			}
			System.out.print("\n" + "\n");
		}
		
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
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
		System.out.println("~ BRAND AWARENESS ~");
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				System.out.print(board[row][col].getAwareness() + " ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
		if(tutorial) {
			System.out.println("- Households with high brand awareness are perpetually aware of " + companyName + "'s food,\n- making them more likely to visit a restaurant.\n- Households with a 0 don't know " + companyName + " exists. They won't visit a restaurant. \n- Remember you can raise these values through marketing.");
		}
	}
	
	//Data View: Proximity
	private void viewProximity() {
		System.out.println("~ PROXIMITY ~");
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) { 
				System.out.print(board[row][col].getProximity() + " ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
		if(tutorial) {
			System.out.println("- Households with a low proximity value live close to a restaurant,\n- making them more likely to visit a restaurant.\n- However, households with a 0 live too far from a restaurant to even consider visiting.\n- In path calculation, roads cost 1 to traverse, 3 for other tiles.");
		}
	}
	
	//Data View: Population
	private void viewPopulation() {
		System.out.println("~ POPULATION ~");
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) { 
				System.out.print(board[row][col].getMembers() + " ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
		if(tutorial) {
			System.out.println("- Households can have between 1 and 9 people living in them.\n- If a household visits a restaurant, all members go.");
		}
	}
	
	//Debug data view
	private void viewClosest() { 	
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) { 
				if (board[row][col].type == '^' && !(board[row][col].getNearestRestaurant().equals("A0"))) {
					System.out.print(board[row][col].getNearestRestaurant());
					if (board[row][col].getNearestRestaurant().length() == 2) {
						System.out.print(" ");
					}
				} else {
					System.out.print(" " + board[row][col].type + " ");
				}
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	

	//Place a restaurant on the map
	//@param - Set required to true if restaurant MUST be placed
	private void placeRestaurant(boolean required) {
		
		if (tutorial) {
			System.out.println("- Building a Restaurant Costs $" + RESTAURANT_COST + ". Rent is $" + RENT + "/week.\n- Comes with 1 Staff Member, costing $" + STAFF_WAGE + "/week in wages.\n- Placing Restaurants next to roads is recommended.");
		}
		
		boolean placed = false;
		boolean first = true;
		while (!placed) {	
			if (!first) {
				System.out.println("Choose an Empty Tile.");
			}
			
			viewBoard(true);
			int col = (userStringInput("Column:", true).charAt(0)) - TO_LOWERCASE;
			int row = userIntInput(0, BOARD_SIZE, "Row:");
		
			if (board[row][col].type == ' ') {
				
				board[row][col].type = 'B';
				viewBoard(false);
				if (userBinaryInput("Place Restaurant Here? (Y or N)"))  {
					
					//Change Awareness for Surrounding Area
					changeAwarenessHelper(row - 2, row + 2, col - 2, col + 2, 1);
					changeAwarenessHelper(row - 1, row + 1, col - 1, col + 1, 1);	
					
					//Change Awareness for road adjacency
					//Horizontal Below
					if (row < (BOARD_SIZE - 1) && board[row + 1][col].type == '—') {
						changeAwarenessHelper(row, row + 2, 0, BOARD_SIZE - 1, 1);
					} 
					//Horizontal Above
					if (row > 0 && board[row - 1][col].type == '—') {
						changeAwarenessHelper(row - 2, row, 0, BOARD_SIZE - 1, 1);
					}
					//Vertical to the Left
					if (col > 0 && board[row][col - 1].type == '|') {
						changeAwarenessHelper(0, BOARD_SIZE - 1, col - 2, col, 1);
					}
					//Vertical to the Right
					if (col < (BOARD_SIZE - 1) && board[row][col + 1].type == '|') {
						changeAwarenessHelper(0, BOARD_SIZE - 1, col, col + 2, 1);
					}
						
					//Add The Restaurant to the Hashmap
					Restaurant newShop = new Restaurant(getLocationCode(row, col));
					restaurants.put(getLocationCode(row, col) , newShop);
						
					//Update Proximity
					calculateProximity();
					
					money -= RESTAURANT_COST;
					placed = true;
				} else {
					board[row][col].type = ' ';
					if (!required) {
						placed = true;
					}
				}
			} else {
				first = false;
			}
		}
	}
	
	//Recalculate Proximity Values
	private void calculateProximity() {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				Tile current = board[row][col];
				if (current.type == '^') {
					int[] result = {10};
					int[] location = {0,0};
					calculateProximityHelper(row, col, -3, result, location);
					current.changeProximity(result[0]);
					current.setNearesRestaurant(getLocationCode(location[0], location[1]));
				}
			}
		}
	}
	
	//Recursive Method for Above
	private void calculateProximityHelper(int row, int col, int distance, int[] result, int[] location) {
		//Check bounds
		if(row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
			return;
		}
		
		//Update distance
		if (board[row][col].type == '|' || board[row][col].type == '—' || board[row][col].type == 'B') {
			distance += 1;
		} else {
			distance += 3;
		}
		
		//Base Case 1: At restaurant
		if (board[row][col].type == 'B') {
			if (distance < result[0]) {
				result[0] = distance;
				
				//Send back Location of This Restaurant
				location[0] = row;
				location[1] = col;
			}
			return;
			
		//Base Case 2: Out of Moves
		} else if (distance > 9) {
			return;
			
		//Recurse
		} else {
			calculateProximityHelper(row + 1, col, distance, result, location);
			calculateProximityHelper(row - 1, col, distance, result, location);
			calculateProximityHelper(row, col + 1, distance, result, location);
			calculateProximityHelper(row, col - 1, distance, result, location);
		}
	}
	
	

	//Change Awareness of a Section, Start and Stop indices inclusive
	//Avoids OutOfBoundsException
	private void changeAwarenessHelper(int rowStart, int rowStop, int colStart, int colStop, int change) {
		for (int row = rowStart; row <= rowStop; row++) {
			for (int col = colStart; col <= colStop; col++) {
				if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
					board[row][col].changeAwareness(change);
				}
			}
		}
	}
	
	private int charToInt(char input) {
		return (((int)input) - 48);
	}
	
	private String getLocationCode(int row, int col) {
		return "" + ((char)(col + TO_UPPERCASE)) + row; 
	}
	
	//Returns a boolean
	//Useful for simple y/n queries, such as tutorial yes/no
	private boolean userBinaryInput(String message) {
		String options = "YyNn";
		boolean loop = true;
		String input = "";
		boolean toReturn = true;
		while (loop) {
			System.out.println(message);
			input = sc.nextLine();
			
			//Yes
			if (input.equals(options.substring(0, 1)) || input.equals(options.substring(1, 2))){			
				toReturn = true;
				loop = false;
				
			//No
			} else if (input.equals(options.substring(2, 3)) || input.equals(options.substring(3, 4))) {
				toReturn = false;
				loop = false;
			}
		}
		return toReturn;
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
			while (LETTERS.indexOf(input) == -1 || input.length() != 1) {
				if (!first) {
				System.out.println("Please input a letter between " + LETTERS.charAt(0) + " and " + LETTERS.substring(LETTERS.length() - 1).toLowerCase());
				}
				first = false;
			    input = sc.nextLine();
			}		
			return input.toLowerCase();
		}
	}
}
