import java.util.ArrayList;
import java.util.TreeMap;

public class Report {	
	public int revenue;
	public int expenses;
	public TreeMap<String, String> restaurantPerformance;
	public ArrayList<String> news;
	
	public Report() {
		revenue = 0;
		expenses = 0;
		restaurantPerformance = new TreeMap<String, String>();
		news = new ArrayList<String>();
	}
	
	public void printReport() {
		//Main
		System.out.println("~~ YESTERDAY'S REPORT ~~");
		System.out.println("Profit/Loss: " + (revenue - expenses));
		System.out.println("Revenue: " + revenue);
		System.out.println("Expenses: " + expenses);
		
		//Restaurant Reports
		System.out.println("Restaurant Performance: ");
		for (String location : restaurantPerformance.keySet()) {
			System.out.println("\t" + location + ": " + restaurantPerformance.get(location));
		}
		
		//News
		if (!news.isEmpty()) {
			System.out.println("News:");
			for (String newsItem: news) {
				System.out.println("\t" + newsItem);			
			}
		}
	}	
}
