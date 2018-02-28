package Map;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Map.GridCell;
import Map.GridCell.cellDirection;
import Map.GridCell.cellType;
import Search.AStar;
import Search.Fringe;

import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;

import FileIO.LoadFile;
import FileIO.SaveFile;
import Grid.ColorGrid;

public class DiscretizedMap {

	public GridCell map[][] = new GridCell[120][160];

	public GridCell startCell;
	public GridCell goalCell;

	int firstxCord;
	int firstyCord;
	int firstDirection;

	public Map<String, String> randomPartialCords = new HashMap<String, String>();

	public void initializeMap() {

		for (int i = 0; i < 120; i++) {
			for (int j = 0; j < 160; j++) {
				map[i][j] = new GridCell(cellType.Unblocked, false, i, j);
			}
		}
	}

	public void placePartialCells() {

		int randomXcord;
		int randomYcord;

		int xSize = 119;
		int ySize = 159;

		for (int i = 0; i < 8; i++) {

			randomXcord = ThreadLocalRandom.current().nextInt(0, xSize);
			randomYcord = ThreadLocalRandom.current().nextInt(0, ySize);

			if (randomPartialCords.containsKey(Integer.toString(randomXcord))) {
				if (randomPartialCords.get(Integer.toString(randomXcord)) == Integer.toString(randomYcord)) {
					i--;
					continue;
				}
			}

			int xBoxSize = 30;
			int yBoxSize = 30;

			int xStart = randomXcord - 15;
			int yStart = randomYcord - 15;

			if (xStart < 0) {
				xBoxSize = 30 + xStart;
				xStart = 0;
			}
			if (yStart < 0) {
				yBoxSize = 30 + yStart;
				yStart = 0;
			}

			randomPartialCords.put(Double.toString(randomXcord), Double.toString(randomYcord));
			for (int x = xStart; x < xStart + xBoxSize && x < 120; x++) {
				for (int y = yStart; y < yStart + yBoxSize && y < 160; y++) {
					if (Math.random() >= 0.5) {
						map[x][y].setType(cellType.Partial);
					}
				}
			}

		}

	}

	public void placeHardCells() {

		double randomXcord;
		double randomYcord;

		double regionXLimit;
		double regionYLimit;
		// since 31x31, need to atleast travel 15 cells up and down from center
		int xCordLimit1 = 119 - 15;
		int xCordLimit2 = 15;

		int yCordLimit1 = 159 - 15;
		int yCordLimit2 = 15;

		for (int i = 0; i < 8; i++) {
			randomXcord = ThreadLocalRandom.current().nextInt(xCordLimit2, xCordLimit1 + 1);
			randomYcord = ThreadLocalRandom.current().nextInt(yCordLimit2, yCordLimit1 + 1);

			regionYLimit = randomYcord + 15;
			regionXLimit = randomXcord - 15;

			while (regionYLimit > 160) {
				randomYcord = ThreadLocalRandom.current().nextInt(yCordLimit2, yCordLimit1 + 1);
				regionYLimit = randomYcord + 15;
			}
			regionXLimit = randomXcord - 15;
			while (regionXLimit < 0) {
				randomXcord = ThreadLocalRandom.current().nextInt(xCordLimit2, xCordLimit1 + 1);
				regionXLimit = randomXcord - 15;
			}

			randomPartialCords.put(Double.toString(randomXcord), Double.toString(randomYcord));
			for (int k = (int) regionYLimit; k > regionYLimit - 30; k--) {
				for (int j = (int) regionXLimit; j < regionXLimit + 30; j++) {
					if (Math.random() >= 0.5) {
						map[j][k].setType(cellType.Partial);
					}

				}
			}

		}

	}

	public void resetPath() {

		AStar.integrated = false;
		for (int x = 0; x < 120; x++) {
			for (int y = 0; y < 160; y++) {

				map[x][y].f_array[0] = 0;
				map[x][y].f_array[1] = 0;
				map[x][y].f_array[2] = 0;
				map[x][y].f_array[3] = 0;
				map[x][y].f_array[4] = 0;
				map[x][y].h = 0;
				map[x][y].parent_array[0] = null;
				map[x][y].parent_array[1] = null;
				map[x][y].parent_array[2] = null;
				map[x][y].parent_array[3] = null;
				map[x][y].parent_array[4] = null;
				
				map[x][y].setPath(false);
				map[x][y].consistent = 0;
				map[x][y].times_expanded = 0;
				map[x][y].expanded_by_anchor = false;
				map[x][y].expanded_by_heuristic_at = Double.POSITIVE_INFINITY;
				
				map[x][y].g_array[0] = Double.POSITIVE_INFINITY;
				map[x][y].g_array[1] = Double.POSITIVE_INFINITY;
				map[x][y].g_array[2] = Double.POSITIVE_INFINITY;
				map[x][y].g_array[3] = Double.POSITIVE_INFINITY;
				map[x][y].g_array[4] = Double.POSITIVE_INFINITY;
				map[x][y].seen = false;

			}
		}

	}

	private boolean reachedGrid(double xCord, double yCord) {

		if ((yCord == 160 || yCord == 0))
			return true;
		else if ((xCord == 120 || xCord == -1))
			return true;
		else
			return false;
	}

	public boolean createHighway(DiscretizedMap map) {
		// select a random x coordinate with static y coordinate = 160 or 0

		GridCell[][] tempMap = new GridCell[120][160];
		learnMap(tempMap, map);
		// Main.Main.createAndShowGui(map);
		int i;
		int xCord = 0;
		int yCord = 0;

		int totalDistance = 0;

		//int direction; // 1 for vertical, 0 for horizontal.

		//double initialDirectionProbability = Math.random();
		int direction = (int)Math.rint((Math.random() * 3));
		
		if(direction == 0){xCord = 0; yCord = ThreadLocalRandom.current().nextInt(0, 160);} //Left Side
		if(direction == 2){xCord = 120-1; yCord = ThreadLocalRandom.current().nextInt(0, 160);} //Right Side
		if(direction == 1){xCord = ThreadLocalRandom.current().nextInt(0, 120); yCord = 160 - 1;} //Bottom Side
		if(direction == 3){xCord = ThreadLocalRandom.current().nextInt(0, 120); yCord = 0;} //Top Side

//		if (initialDirectionProbability >= 0.5) { // will have to move up/down,
//													// so we choose a point on
//													// the grid where y coord is
//													// 0/160, x cord can be
//													// anything.
//			direction = 1;
//			xCord = ThreadLocalRandom.current().nextInt(0, 120); // Bottom of
//																	// the grid
//																	// with
//																	// random x
//																	// coordinate.
//			yCord = 160 - 1;
//		} else {
//			direction = 0;
//			xCord = 0; // Leftmost border of the grid with random y coordiante.
//			yCord = ThreadLocalRandom.current().nextInt(0, 160);
//		}
		
		firstDirection = direction;
		firstxCord = xCord;
		firstyCord = yCord;
		boolean highwayCreated = false;

		while (highwayCreated == false) {
			
			for (i = 0; i < 20; i++) {
				if (direction == 0) {
					
					//Left Side
					if (xCord + i == 120)
						break;
					if (yCord - i == 0)
						break;
					if (tempMap[xCord + i][yCord].isCanAccelerate() == true) {
						return false;
					}
					tempMap[xCord + i][yCord].setCanAccelerate(true);
				} 
				
				else if (direction == 1){
					
					//Bottom Side
					if (yCord - i == 0)
						break;
					if (xCord == 120)
						break;
					if (tempMap[xCord][yCord - i].isCanAccelerate() == true) {
						return false;
					}

					tempMap[xCord][yCord - i].setCanAccelerate(true);
				}
				else if(direction == 2){
					
					//Right Side
					if (xCord - i == 0)
						break;
					if (yCord - i == 0)
						break;
					if (tempMap[xCord - i][yCord].isCanAccelerate() == true) {
						return false;
					}
					tempMap[xCord - i][yCord].setCanAccelerate(true);
					
				}
				else if(direction == 3){
					
					//Top Side
					if (yCord + i == 160)
						break;
					if (xCord == 120)
						break;
					if (tempMap[xCord][yCord + i].isCanAccelerate() == true) {
						return false;
					}

					tempMap[xCord][yCord + i].setCanAccelerate(true);
					
				}
			}

			totalDistance += i;
			// System.out.println("Total Distance is : " + totalDistance);

			if (direction == 1)
				//Bottom Side
				yCord -= i;
			else if(direction == 0)
				//Left Side
				xCord += i;
			else if(direction == 2)
				//Right Side
				xCord -= i;
			if (direction == 3)
				//Top Side
				yCord += i;

			if (reachedGrid(xCord, yCord)) {
				if (yCord == 0) {
					tempMap[xCord][yCord].setCanAccelerate(true);
				}
				if (totalDistance >= 100) {
					highwayCreated = true;
					setMap(tempMap, map);
					return true;
				} else {
					return false;
				}
			}

			double probability = Math.random();

			if (probability <= 0.4) {
				if (direction == 1)
					direction = (Math.random() > .5) ? 0 : 2;
				if (direction == 0)
					direction = (Math.random() > .5) ? 3 : 1;
				if (direction == 2)
					direction = (Math.random() > .5) ? 3 : 1;
				if (direction == 3)
					direction = (Math.random() > .5) ? 0 : 2;
			}
		}
		return false;
	}

	public void setMap(GridCell[][] grid, DiscretizedMap map) {

		map.map = grid;
	}

	public void placeBlockedCells() {
		int availableBlockedCells = 3840;

		while (availableBlockedCells > 0) {

			int randomXPoint;
			int randomYPoint;

			randomXPoint = ThreadLocalRandom.current().nextInt(0, 120);
			randomYPoint = ThreadLocalRandom.current().nextInt(0, 160);

			if (map[randomXPoint][randomYPoint].isCanAccelerate() == false) {
				map[randomXPoint][randomYPoint].setType(cellType.Blocked);
				availableBlockedCells--;
			}

		}

	}

	private void learnMap(GridCell[][] u, DiscretizedMap map) {

		for (int i = 0; i < u.length; i++) {
			for (int j = 0; j < u[i].length; j++) {
				GridCell cell = map.map[i][j];
				u[i][j] = new GridCell(cell.getType(), cell.isCanAccelerate(), cell.xCord, cell.yCord);
			}
		}
	}

	public boolean initalizeStartGoal() {

		double initialProbability = Math.random();

		int randomXcord;
		int randomYcord;
		int randomXcord1;
		int randomYcord1;

		if (initialProbability >= .75) { // top 20 rows
			randomXcord = ThreadLocalRandom.current().nextInt(0, 120);
			randomYcord = ThreadLocalRandom.current().nextInt(0, 20);
		} else if (initialProbability >= .50) {// bottom 20 rows
			randomXcord = ThreadLocalRandom.current().nextInt(0, 120);
			randomYcord = ThreadLocalRandom.current().nextInt(140, 160);
		} else if (initialProbability >= .25) {// leftmost 20 cols
			randomXcord = ThreadLocalRandom.current().nextInt(0, 20);
			randomYcord = ThreadLocalRandom.current().nextInt(0, 160);
		} else {// rightmost 20 cols
			randomXcord = ThreadLocalRandom.current().nextInt(100, 120);
			randomYcord = ThreadLocalRandom.current().nextInt(0, 160);
		}

		startCell = map[randomXcord][randomYcord];
		if (startCell.type == cellType.Blocked)
			return false;

		initialProbability = Math.random();

		if (initialProbability >= .75) { // top 20 rows
			randomXcord1 = ThreadLocalRandom.current().nextInt(0, 120);
			randomYcord1 = ThreadLocalRandom.current().nextInt(0, 20);
		} else if (initialProbability >= .50) {// bottom 20 rows
			randomXcord1 = ThreadLocalRandom.current().nextInt(0, 120);
			randomYcord1 = ThreadLocalRandom.current().nextInt(140, 160);
		} else if (initialProbability >= .25) {// leftmost 20 cols
			randomXcord1 = ThreadLocalRandom.current().nextInt(0, 20);
			randomYcord1 = ThreadLocalRandom.current().nextInt(0, 160);
		} else {// rightmost 20 cols
			randomXcord1 = ThreadLocalRandom.current().nextInt(100, 120);
			randomYcord1 = ThreadLocalRandom.current().nextInt(0, 160);
		}

		goalCell = map[randomXcord1][randomYcord1];
		if (goalCell.type == cellType.Blocked)
			return false;

		double distance = Math.hypot(randomXcord1 - randomXcord, randomYcord1 - randomYcord);
		if (distance >= 100)
			return true;

		return false;
	}

	public void printMap(DiscretizedMap map) {

		for (int x = 0; x < 120; x++) {
			for (int y = 0; y < 160; y++) {
				if (map.map[x][y].isPath()) {
					System.out.print("#");
				}

				else if (map.map[x][y].isCanAccelerate() == true)
					System.out.print("|");
				else if (map.map[x][y].getType() == cellType.Unblocked) {
					if (map.map[x][y].isStart() == true)
						System.out.print("S");
					else if (map.map[x][y].isGoal() == true)
						System.out.print("G");
					else
						System.out.print(" ");
				} else if (map.map[x][y].getType() == cellType.Partial) {
					if (map.map[x][y].isStart() == true)
						System.out.print("S");
					else if (map.map[x][y].isGoal() == true)
						System.out.print("G");
					else
						System.out.print("o");
				}

				else {
					System.out.print("x");
				}
			}
			System.out.println(x);
		}
	}

}
