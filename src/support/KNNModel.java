package support;
import java.io.Serializable;
import java.util.List;

public class KNNModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int room1;
	private int room2;
	private int room3;
	private String classification;
	private int date;
	private int distanceToQueryInstance;
	private int rank;
	private boolean isInNeighbourList;
	private String category;
	
	private List<KNNModel> knnList;

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public List<KNNModel> getKnnList() {
		return knnList;
	}

	public void setKnnList(List<KNNModel> knnList) {
		this.knnList = knnList;
	}

	public void setDistanceToQueryInstance(int distanceToQueryInstance) {
		this.distanceToQueryInstance = distanceToQueryInstance;
	}

	public int getDistanceToQueryInstance() {
		return distanceToQueryInstance;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return rank;
	}

	public void setInNeighbourList(boolean isInNeighbourList) {
		this.isInNeighbourList = isInNeighbourList;
	}

	public boolean isInNeighbourList() {
		return isInNeighbourList;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	public int getRoom1() {
		return room1;
	}

	public void setRoom1(int room1) {
		this.room1 = room1;
	}

	public int getRoom2() {
		return room2;
	}

	public void setRoom2(int room2) {
		this.room2 = room2;
	}

	public int getRoom3() {
		return room3;
	}

	public void setRoom3(int room3) {
		this.room3 = room3;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public int getDate() {
		return date;
	}

}
