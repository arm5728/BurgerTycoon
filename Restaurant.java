
public class Restaurant {
	private final static int STAFF_WORK = 5;
	public String location;
	public int staff;
	public int staffPointsLeft;
	public int visitors;
	
	public Restaurant(String location) {
		staff = 2;
		resetStaffPoints();
		this.location = location;
	}
	
	public void resetStaffPoints() {
		staffPointsLeft = staff * STAFF_WORK;
		visitors = 0;
	}
	
	public void attendance(int people) {
		staffPointsLeft -= people;
		visitors += people;
	}
}
