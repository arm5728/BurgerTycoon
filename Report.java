import java.util.TreeMap;

public class Report {	
	public int revenue;
	public int expenses;
	public TreeMap<String, String> restaurantPerformance;
	
	public Report() {
		revenue = 0;
		expenses = 0;
		restaurantPerformance = new TreeMap<String, String>();
	}
	
	public void printReport() {
		//Main
		System.out.println("Yesterday's Profit/Loss: " + (revenue - expenses));
		System.out.println("Revenue: " + revenue);
		System.out.println("Expenses: " + expenses);

		//Reports
		for (String location : restaurantPerformance.keySet()) {
			System.out.println(location + ": " + restaurantPerformance.get(location));
		}
	}	
}
