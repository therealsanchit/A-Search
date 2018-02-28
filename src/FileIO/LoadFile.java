package FileIO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Map.DiscretizedMap;
import Map.GridCell;
import Map.GridCell.cellType;

public class LoadFile {

	public GridCell[][] tempMap = new GridCell[120][160];
	private String[] startCords = new String[2];
	private String[] goalCords = new String[2];

	public Map<String, String> randomPartialCords = new HashMap<String, String>();

	public Map<String, String> getRandomPartialCords() {
		return randomPartialCords;
	}

	public GridCell[][] getTempMap() {
		return tempMap;
	}
	
	public GridCell getStart(){
		return tempMap[(int) Double.parseDouble(startCords[0])][(int) Double.parseDouble(startCords[1])];
	}
	
	public GridCell getGoal(){
		return tempMap[(int) Double.parseDouble(goalCords[0])][(int) Double.parseDouble(goalCords[1])];
	}

	public void loadFile(String pathToFile) throws IOException {
		String cords[];
		String line;
		int lineNumber = 0;
		BufferedReader fileReader = new BufferedReader(new FileReader(pathToFile));
		int j;

		while ((line = fileReader.readLine()) != null) {
			if (lineNumber < 10) {
				cords = getCords(line);
				if (lineNumber == 0) {
					startCords = cords;
				} else if (lineNumber == 1) {
					goalCords = cords;
				} else {
					randomPartialCords.put(cords[0], cords[1]);
				}
			} else {

				String value[];
				value = line.split(",");
				for (j = 0; j < 160; j++) {
					if (value[j].equalsIgnoreCase("0")) {
						tempMap[lineNumber - 10][j] = new GridCell(cellType.Blocked, false, lineNumber - 10, j);
					} else if (value[j].equalsIgnoreCase("1")) {
						tempMap[lineNumber - 10][j] = new GridCell(cellType.Unblocked, false, lineNumber - 10, j);
					} else if (value[j].equalsIgnoreCase("2")) {
						tempMap[lineNumber - 10][j] = new GridCell(cellType.Partial, false, lineNumber - 10, j);
					} else if (value[j].equalsIgnoreCase("a")) {
						tempMap[lineNumber - 10][j] = new GridCell(cellType.Unblocked, true, lineNumber - 10, j);
					} else if (value[j].equalsIgnoreCase("b")) {
						tempMap[lineNumber - 10][j] = new GridCell(cellType.Partial, true, lineNumber - 10, j);
					} else {
						System.out.println("Erorr.");
						System.exit(0);
					}

				}

			}

			lineNumber++;
		}

		fileReader.close();
		
		GridCell start = tempMap[(int) Double.parseDouble(startCords[0])][(int) Double.parseDouble(startCords[1])];
		GridCell goal = tempMap[(int) Double.parseDouble(goalCords[0])][(int) Double.parseDouble(goalCords[1])];
		start.setStart(true);
		goal.setGoal(true);
		
		System.out.println();
	}

	private String[] getCords(String line) {
		String[] cords = line.split(",");
		return cords;
	}

}
