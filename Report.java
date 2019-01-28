import java.util.ArrayList;
import java.util.TreeMap;

public class Report {	
	public int revenue;
	public int expenses;
	public TreeMap<String, String> restaurantPerformance;
	public ArrayList<String> expenseBreakdown;
	public ArrayList<String> news;
	
	public Report() {
		revenue = 0;
		expenses = 0;
		restaurantPerformance = new TreeMap<String, String>();
		news = new ArrayList<String>();
		expenseBreakdown = new ArrayList<String>();
	}
	
	public void printReport() {
		//Main
		System.out.println("~~ YESTERDAY'S REPORT ~~");
		System.out.println("Profit/Loss: \t" + (revenue - expenses));
		System.out.println("Revenue: \t" + revenue);
		System.out.println("Expenses: \t" + expenses);
		
		//Restaurant Reports
		System.out.println("Restaurant Performance: ");
		for (String location : restaurantPerformance.keySet()) {
			System.out.println("\t" + location + ": " + restaurantPerformance.get(location));
		}
		
		//News
		if (!news.isEmpty()) {
			System.out.println("!!!  News:  !!!");
			for (String newsItem: news) {
				System.out.println("\t" + newsItem);			
			}
		}
	}	
	
	public void expenseBreakdown() {
		System.out.println("~ EXPENSES: ~");
		for (String expense: expenseBreakdown) {
			System.out.println(expense);
		}
	}
}
