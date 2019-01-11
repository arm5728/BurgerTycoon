
public class Restaurant {
	public String location;
	public int staff;
	public int staffPointsLeft;
	public int staffPointsNeeded;
	
	public Restaurant(String location) {
		staff = 2;
		resetStaffPoints();
		this.location = location;
	}
	
	public void resetStaffPoints() {
		staffPointsLeft = staff * 5;
		staffPointsNeeded = 0;
	}
	
	public void attendance(int people) {
		staffPointsLeft -= people;
		staffPointsNeeded += people;
	}
}
