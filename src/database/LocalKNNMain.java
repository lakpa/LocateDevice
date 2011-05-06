package database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import support.KNNModel;

public class LocalKNNMain {

	private List<KNNModel> knnList = null;
	private List<Integer> assignedRank = null;
	private String highestClassification = "";
	public List<KNNModel> getKnnList() {
		return knnList;
	}

	public void setKnnList(List<KNNModel> knnList) {
		this.knnList = knnList;
	}

	public LocalKNNMain() {
//		localDB = new KnnDatabaseActivity();
		assignedRank = new ArrayList<Integer>();
	}

	public String classifiedQueryInstance(List<KNNModel> knnModelList,
			KNNModel queryInstance, int k) {
		
		String category = "";
		List<KNNModel> knnList1 = setSquareDistance(knnModelList, queryInstance);
		knnList = setRank(knnList1);
		knnList = checkIncludedInNeighbourList(knnList, k);
		printList(knnList1);
		int nearTC354 = 0, inTC357 = 0, inTC364 = 0, nearTC360 = 0, nearTC357 = 0;
		for (int i = 0; i < knnList.size(); i++) {
			if (knnList.get(i).getRank() <= k) {
				
				if (knnList.get(i).getRank() == 1)
					highestClassification = knnList.get(i).getClassification();
				
				
				if (knnList.get(i).getClassification().equals("In TC364")) {
					inTC364++;
				} else if (knnList.get(i).getClassification().equals("In TC357")){
					inTC357++;
				} else if (knnList.get(i).getClassification().equals("Near TC360")){
					nearTC360++;
				}  else if (knnList.get(i).getClassification().equals("Near TC357")){
					nearTC357++;
				}  else if (knnList.get(i).getClassification().equals("In TC356")){
					nearTC354++;
				}
			} 
		}

		System.out.println("After--");
		printList(knnList);
		int a[] = {nearTC354,inTC357,inTC364,nearTC360,nearTC357};
		int index = findGreatest(a);
		if (index != -1) {
			switch (index) {
			case 0:
				category = "Near TC354";
				break;
			case 1:
				category = "In TC357";
				break;
			case 2:
				category = "In TC364";
				break;
			case 3:
				category = "Near TC360";
				break;
			case 4:
				category = "Near TC357";
				break;
			}
		} else {
			if (!highestClassification.equals("")) {
				return highestClassification;
			}
		}
		
		return category;
	}
	
	
	public int findGreatest(int...val) {
		int largest = val[0];
		boolean isLargest = false;
		int index = 0;
		for (int i = 0; i < val.length; i++) {
			if (i != 0) {
				if (val[i] > largest) {
					if (val[i] > 1) {	
						largest = val[i];
						index = i;
						isLargest = true;
					}
				}
			}
		}
		if (isLargest)
			return index;
		else 
			return -1;
	}

	private void printList(List<KNNModel> knnList) {
		for (int i = 0; i < knnList.size(); i++) {
			KNNModel model = knnList.get(i);
			System.out.println(model.getRoom1() + " "
					+ model.getRoom2() + " " + model.getClassification()
					+ " " + model.getDistanceToQueryInstance() + " "
					+ model.getRank() + " " + model.isInNeighbourList() + " "
					+ model.getCategory());
		}
	}

	private List<KNNModel> checkIncludedInNeighbourList(List<KNNModel> km, int k) {
		for (int i = 0; i < km.size(); i++) {
			if (km.get(i).getRank() <= k) {
				km.get(i).setInNeighbourList(true);
			}
		}
		return km;
	}

	private List<KNNModel> setSquareDistance(List<KNNModel> knnModelList,
			KNNModel queryInstance) {
		for (int i = 0; i < knnModelList.size(); i++) {
			int squareDistance = squareDistance(knnModelList.get(i),
					queryInstance);
			// set square distance to query instance
			knnModelList.get(i).setDistanceToQueryInstance(squareDistance);
		}
		return knnModelList;
	}

	private List<KNNModel> setRank(List<KNNModel> listModel) {
		int[] intArray = sortedArray(listModel);
		for (int i = 0; i < listModel.size(); i++) {
			for (int j = intArray.length - 1; j >= 0; j--)
				if (listModel.get(i).getDistanceToQueryInstance() == intArray[j]) {
					int rank = j+1;
					if (!isDuplicate(rank)) {
						listModel.get(i).setRank(rank);
						assignedRank.add(rank);
					} 
					else {
						rank += 1;
						listModel.get(i).setRank(rank);
					}
				}
		}
		return listModel;
	}
	
	private boolean isDuplicate(int rank) {
		for(int i=0; i<assignedRank.size(); i++) {
			if (assignedRank.get(i).intValue() == rank)
				return true;
		}
		return false;
	}
	
	private int[] sortedArray(List<KNNModel> list) {
		int[] a = new int[list.size()];
		for (int i = 0; i < list.size(); i++)
			a[i] = list.get(i).getDistanceToQueryInstance();
		Arrays.sort(a);
		return a;
	}

	// return square distance between query string and training data
	private int squareDistance(KNNModel training, KNNModel queryInstance) {
		int room1, room2, room3 ;
		room1 = (int) Math.pow((training.getRoom1() - queryInstance.getRoom1()), 2);
		room2 = (int) Math.pow((training.getRoom2() - queryInstance.getRoom2()), 2);
		room3 = (int) Math.pow((training.getRoom3() - queryInstance.getRoom3()), 2);
		return room1 + room2 + room3;
	}

}
