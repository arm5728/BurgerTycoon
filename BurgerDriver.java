import java.util.Scanner;

public class BurgerDriver {
	public static void main (String [] args) {		
		System.out.println("Welcome to Burger Tycoon!\n ");
		Scanner scanner = new Scanner(System.in);
		boolean newGame = true;
		while (newGame) {
			BurgerMain game = new BurgerMain(scanner);
			newGame = game.continueGame();
		}
		scanner.close();
	}
}
