public class Tile {
	public char type;
	private int awareness;
	private int leftoverAwareness;
	private int proximity;
	private String nearestRestaurant;
	private int members;
	
	public Tile () {
		type = ' ';
		awareness = 0;
		proximity = 0;
		nearestRestaurant = "A0";
		members = 0;
		leftoverAwareness = 0;
	}
	
	//Get awareness value for house, or tile type otherwise
	//Returns a char representation
	public char getAwareness() {
		if (type == '^') {
			return (char) (awareness + 48);
		} else {
			return type;
		}
	}
	
	//Update Awareness value for this tile
	//Value will remain between 0 and 9
	public void changeAwareness(int change) {
		awareness += change;
		awareness += leftoverAwareness;
		leftoverAwareness = 0;
		if (awareness > 9) {
			leftoverAwareness += awareness - 9;
			awareness = 9;
		} else if (awareness < 0) {
			awareness = 0;
		}
	}
	
	//Returns Proximity Value of House
	public char getProximity() {
		if (type == '^') {
			if (proximity == 10) {
				return (char) (proximity + 38);
			} else {
				return (char) (proximity + 48);
			}
		} else {
			return type;
		}
	}
	
	//Update Proximity Value for this tile
	public void changeProximity(int value) {
		proximity = value;
	}
	
	public void setNearestRestaurant(String location) {
		nearestRestaurant = location;
	}
	
	public String getNearestRestaurant() {
		return nearestRestaurant;
	}
	
	public void setMembers(int people) {
		members = people;
	}
	
	//Returns number of people in Household
	public char getMembers() {
		if (type == '^') {
			return (char) (members + 48);
		} else {
			return type;
		}
	}
}
