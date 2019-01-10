
public class Restaurant {
	public int staff;
	private int staffPointsLeft;
	
	public Restaurant() {
		staff = 2;
		resetStaffPoints();
	}
	
	public void resetStaffPoints() {
		staffPointsLeft = staff * 5;
	}
	
	public int getStaffPointsLeft() {
		return staffPointsLeft;
	}
	
	public void attendance(int people) {
		staffPointsLeft -= people;
	}
	
}
