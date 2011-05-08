package support;
import java.io.Serializable;
import java.util.List;

/**
 * @author lakpa
 * 
 * The Class KNNModel.
 */
public class KNNModel implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The room1. */
	private int room1;
	
	/** The room2. */
	private int room2;
	
	/** The room3. */
	private int room3;
	
	/** The classification. */
	private String classification;
	
	/** The date. */
	private int date;
	
	/** The distance to query instance. */
	private int distanceToQueryInstance;
	
	/** The rank. */
	private int rank;
	
	/** The is in neighbour list. */
	private boolean isInNeighbourList;
	
	/** The category. */
	private String category;
	
	/** The knn list. */
	private List<KNNModel> knnList;

	/**
	 * Gets the classification.
	 *
	 * @return the classification
	 */
	public String getClassification() {
		return classification;
	}

	/**
	 * Sets the classification.
	 *
	 * @param classification the new classification
	 */
	public void setClassification(String classification) {
		this.classification = classification;
	}

	/**
	 * Gets the knn list.
	 *
	 * @return the knn list
	 */
	public List<KNNModel> getKnnList() {
		return knnList;
	}

	/**
	 * Sets the knn list.
	 *
	 * @param knnList the new knn list
	 */
	public void setKnnList(List<KNNModel> knnList) {
		this.knnList = knnList;
	}

	/**
	 * Sets the distance to query instance.
	 *
	 * @param distanceToQueryInstance the new distance to query instance
	 */
	public void setDistanceToQueryInstance(int distanceToQueryInstance) {
		this.distanceToQueryInstance = distanceToQueryInstance;
	}

	/**
	 * Gets the distance to query instance.
	 *
	 * @return the distance to query instance
	 */
	public int getDistanceToQueryInstance() {
		return distanceToQueryInstance;
	}

	/**
	 * Sets the rank.
	 *
	 * @param rank the new rank
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * Gets the rank.
	 *
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Sets the in neighbour list.
	 *
	 * @param isInNeighbourList the new in neighbour list
	 */
	public void setInNeighbourList(boolean isInNeighbourList) {
		this.isInNeighbourList = isInNeighbourList;
	}

	/**
	 * Checks if is in neighbour list.
	 *
	 * @return true, if is in neighbour list
	 */
	public boolean isInNeighbourList() {
		return isInNeighbourList;
	}

	/**
	 * Sets the category.
	 *
	 * @param category the new category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Gets the room1.
	 *
	 * @return the room1
	 */
	public int getRoom1() {
		return room1;
	}

	/**
	 * Sets the room1.
	 *
	 * @param room1 the new room1
	 */
	public void setRoom1(int room1) {
		this.room1 = room1;
	}

	/**
	 * Gets the room2.
	 *
	 * @return the room2
	 */
	public int getRoom2() {
		return room2;
	}

	/**
	 * Sets the room2.
	 *
	 * @param room2 the new room2
	 */
	public void setRoom2(int room2) {
		this.room2 = room2;
	}

	/**
	 * Gets the room3.
	 *
	 * @return the room3
	 */
	public int getRoom3() {
		return room3;
	}

	/**
	 * Sets the room3.
	 *
	 * @param room3 the new room3
	 */
	public void setRoom3(int room3) {
		this.room3 = room3;
	}

	/**
	 * Sets the date.
	 *
	 * @param date the new date
	 */
	public void setDate(int date) {
		this.date = date;
	}

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public int getDate() {
		return date;
	}

}
