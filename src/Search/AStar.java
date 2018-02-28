package Search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

import Main.Main;
import Map.DiscretizedMap;
import Map.GridCell;
import Map.GridCell.cellType;

public class AStar {
	public static double w_val = 1.25;
	public static double w2_val = 1.25;
	public static int lastIndex = 0;
	public static boolean integrated = false;

	public static Double[] runIntAStar(DiscretizedMap map) {
		
		AStar.integrated = true;

		System.out.println("Running Integrated AStar...");

		GridCell startCell = map.startCell;
		GridCell goalCell = map.goalCell;

		ArrayList<PriorityQueue<GridCell>> open = new ArrayList<>();
		ArrayList<HashMap<String, GridCell>> closed = new ArrayList<>();

		startCell.parent_array[0] = null;
		goalCell.parent_array[0] = null;

		startCell.g_array[0] = 0;
		goalCell.g_array[0] = Double.POSITIVE_INFINITY;

		closed.add(new HashMap<String, GridCell>()); // Anchor Search
		closed.add(new HashMap<String, GridCell>()); // Inadmissible

		// U start?

		for (int t = 0; t < 5; t++) {
			open.add(new PriorityQueue<GridCell>());

			//startCell.f_index = t;
			AStar.lastIndex = t;
			startCell.calculateF(map, 2, w_val, t);
			open.get(t).add(startCell);
		}

		int cycle_count = 0;
		int max_fringe_size = 0;
		double startTime = System.currentTimeMillis();

		while (open.get(0).peek().f_array[0] < Double.POSITIVE_INFINITY) {
			cycle_count++;
			max_fringe_size = Math.max(max_fringe_size, open.get(0).size());
			for (int i = 1; i < 5; i++) {
				
				if (open.get(i).peek() != null && open.get(i).peek().f_array[i] <= w2_val * open.get(0).peek().f_array[0]) {
					
					if (goalCell.g_array[0] <= open.get(i).peek().f_array[i]) {
						if (goalCell.g_array[0] < Double.POSITIVE_INFINITY) {
							double endTime = System.currentTimeMillis();
							printStats(endTime - startTime, cycle_count, max_fringe_size, goalCell.g_array[0]);
							makePath(map, startCell, goalCell, 0);
							Double[] data = { goalCell.g_array[0], (double) cycle_count, (double) max_fringe_size,
									(endTime - startTime) };
							return data;
							 
						}
					} else {

						int final_i = i;
						// open.get(i).forEach((gc) -> gc.f_index = final_i);
						//lastIndex = final_i;
						
						AStar.lastIndex = i;
						GridCell cell = open.get(i).poll();
						cell.seen = true;
						
						if(cell.expanded_by_anchor == true){
							System.out.println("Error: Cell was expanded by anchor and expanded again");
							return null;
						}
						
						//cell.expanded_by_heuristic = true;
						cell.expanded_by_heuristic_at = cell.g_array[0];
						
						//For part j of phase 2
						cell.times_expanded++;
						if(cell.times_expanded > 2){
							System.out.println("Error: Cell was expanded more than twice");
							return null;
						}
						
						expandIntStates(cell, i, open, closed, map);
						closed.get(1).put(cell.xCord + "," + cell.yCord, cell);
					}
				} else {
					
					if (goalCell.g_array[0] <= open.get(0).peek().f_array[0]) {
						if (goalCell.g_array[0] < Double.POSITIVE_INFINITY) {
							double endTime = System.currentTimeMillis();
							printStats(endTime - startTime, cycle_count, max_fringe_size, goalCell.g_array[0]);
							makePath(map, startCell, goalCell, 0);
							Double[] data = { goalCell.g_array[0], (double) cycle_count, (double) max_fringe_size,
									(endTime - startTime) };
							return data;
						}
					} else {
						// open.get(0).forEach((gc) -> gc.f_index = 0);
						//lastIndex = 0;
						
						AStar.lastIndex = 0;
						GridCell cell = open.get(0).poll();
						cell.seen = true;
						
						if(cell.expanded_by_anchor == true){
							System.out.println("Error: Cell was expanded by anchor and expanded again");
							return null;
						}
						
						cell.expanded_by_anchor = true;
						
						if(!(cell.g_array[0] < cell.expanded_by_heuristic_at)){
							System.out.println("Error: Cell was expanded by anchor search after Heuristic search - not at a lower g value");
							return null;
						}
						
						//For part j of phase 2
						cell.times_expanded++;
						if(cell.times_expanded > 2){
							System.out.println("Error: Cell was expanded more than twice");
							return null;
						}
						
						expandIntStates(cell, i, open, closed, map);
						closed.get(0).put(cell.xCord + "," + cell.yCord, cell);
					}
				}
				max_fringe_size = Math.max(max_fringe_size, open.get(i).size());
			}
		}
		return null;

	}

	private static void expandIntStates(GridCell cell, int i, ArrayList<PriorityQueue<GridCell>> open,
			ArrayList<HashMap<String, GridCell>> closed, DiscretizedMap map) {

		// Remove cell from open
		open.forEach(pq -> pq.remove(cell));
		//open.get(i).remove(cell);
		
		ArrayList<GridCell> neighbors = AStar.getNeighbors(cell, map, 2, i);

		// v(s) = g(s)??

		for (GridCell neighbor : neighbors) {

			// Check this
			if (!closed.get(1).containsValue(neighbor) && !closed.get(0).containsValue(neighbor)) {
				neighbor.g_array[0] = Double.POSITIVE_INFINITY;
				neighbor.parent_array[0] = null;
				// v(s)??
			}

			double lineDistance = getCost(cell, neighbor);
			if (neighbor.g_array[0] > cell.g_array[0] + lineDistance) {
				neighbor.g_array[0] = cell.g_array[0] + lineDistance;
				neighbor.parent_array[0] = cell;
				if (!closed.get(0).containsValue(neighbor)) {
					AStar.lastIndex = 0;
					neighbor.calculateF(map, 2, w_val, 0);

					if (open.get(0).contains(neighbor)) {
						open.get(0).remove(neighbor);
					}
					open.get(0).add(neighbor);

					if (!closed.get(1).containsValue(neighbor)) {
						for (int j = 1; j < 5; j++) {
							
							AStar.lastIndex = j;
							neighbor.calculateF(map, 2, w_val, j);
							if (neighbor.f_array[j] <= w2_val * neighbor.f_array[0]) {
								//neighbor.f_index = j;
								AStar.lastIndex = j;
								neighbor.calculateF(map, 2, w_val, j);

								if (open.get(j).contains(neighbor)) {
									open.get(j).remove(neighbor);
								}
								open.get(j).add(neighbor);
							}
						}
					}

				}
			}
		}
	}

	public static Double[] runSeqAStar(DiscretizedMap map) {

		System.out.println("Running Sequential A*...");

		GridCell startCell = map.startCell;
		GridCell goalCell = map.goalCell;
		
		
		if(startCell == null) System.out.println("SDfasdf");

		ArrayList<PriorityQueue<GridCell>> open = new ArrayList<>();
		ArrayList<HashMap<String, GridCell>> closed = new ArrayList<>();

		// 5 heuristics
		for (int i = 0; i < 5; i++) {
			open.add(new PriorityQueue<GridCell>());
			closed.add(new HashMap<String, GridCell>());

			// Testing
			startCell.g_array[i] = 0;
			goalCell.g_array[i] = Double.POSITIVE_INFINITY;
			startCell.parent_array[i] = null;
			goalCell.parent_array[i] = null;

			//startCell.f_index = i;
			
			AStar.lastIndex = i;
			startCell.calculateF(map, 2, w_val, i);
			open.get(i).add(startCell);
		}

		int cycle_count = 0;
		int max_fringe_size = 0;
		double startTime = System.currentTimeMillis();

		while (open.get(0).peek().f_array[0] < Double.POSITIVE_INFINITY) {
			cycle_count++;
			max_fringe_size = Math.max(max_fringe_size, open.get(0).size());
			for (int i = 1; i < 5; i++) {
				if (open.get(i).peek().f_array[i] <= w2_val * open.get(0).peek().f_array[0]) {
					if (goalCell.g_array[i] <= open.get(i).peek().f_array[i]) {
						if (goalCell.g_array[i] < Double.POSITIVE_INFINITY) {
							double endTime = System.currentTimeMillis();
							printStats(endTime - startTime, cycle_count, max_fringe_size, goalCell.g_array[i]);
							makePath(map, startCell, goalCell, i);
							Double[] data = { goalCell.g_array[i], (double) cycle_count, (double) max_fringe_size,
									(endTime - startTime) };
							return data;
						}
					} else { // expand states
						final int final_i = i;
						// open.get(i).forEach((gc) -> gc.f_index = final_i);
						//lastIndex = final_i;
						AStar.lastIndex = i;
						GridCell cell = open.get(i).poll();
						cell.seen = true;
						expandStates(cell, i, open, closed, map);
					}
				} else {
					if (open.get(0).peek().f_array[0] >= goalCell.g_array[0]) {
						if (goalCell.g_array[0] < Double.POSITIVE_INFINITY) {
							double endTime = System.currentTimeMillis();
							printStats(endTime - startTime, cycle_count, max_fringe_size, goalCell.g_array[0]);
							makePath(map, startCell, goalCell, 0);
							Double[] data = { goalCell.g_array[0], (double) cycle_count, (double) max_fringe_size,
									(endTime - startTime) };
							return data;
						}
					} else {
						// open.get(0).forEach((gc) -> gc.f_index = 0);
						//lastIndex = 0;
						AStar.lastIndex = 0;
						GridCell cell = open.get(0).poll();
						cell.seen = true;
						
						//Check for part j
						assert cell.f_array[0] <= w_val * Main.optimal_path;
						
						expandStates(cell, 0, open, closed, map);
					}
				}
				max_fringe_size = Math.max(max_fringe_size, open.get(i).size());
			}
		}
		return null;

	}

	private static void printStats(double d, int cycle_count, int max_fringe_size, double g_array) {

		System.out.println();
		System.out.println("-------------Stats--------------");
		System.out.println("Path Length: " + g_array);
		System.out.println("Cycles: " + cycle_count);
		System.out.println("Max Fringe Size: " + max_fringe_size);
		System.out.println("Time(ms) to compute path: " + d);
		System.out.println();
		System.out.println();
	}

	private static void makePath(DiscretizedMap map, GridCell start, GridCell goal, int i) {
		System.out.println("Making path");
		GridCell pointer = goal;

		while (pointer != start) {

			// System.out.println(pointer.xCord + "," + pointer.yCord);
			map.map[(int) pointer.parent_array[i].xCord][(int) pointer.parent_array[i].yCord].setPath(true);
			pointer = pointer.parent_array[i];
		}
	}

	private static void expandStates(GridCell cell, int i, ArrayList<PriorityQueue<GridCell>> open,
			ArrayList<HashMap<String, GridCell>> closed, DiscretizedMap map) {

		// Is this needed?
		open.get(i).remove(cell);

		ArrayList<GridCell> neighbors = AStar.getNeighbors(cell, map, 2, i);
		for (GridCell gridCell : neighbors) {

			if (!closed.get(i).containsValue(gridCell)) {
				// if (!closed.get(i).containsValue(gridCell)) {
				gridCell.g_array[i] = Double.POSITIVE_INFINITY;
				gridCell.parent_array[i] = null;
			}
			double lineDistance = getCost(cell, gridCell);
			if (gridCell.g_array[i] > cell.g_array[i] + lineDistance) {
				gridCell.g_array[i] = cell.g_array[i] + lineDistance;
				gridCell.parent_array[i] = cell;

				if (!closed.get(i).containsValue(gridCell)) {
					//gridCell.f_index = i;
					
					AStar.lastIndex = i;
					gridCell.calculateF(map, 2, w_val, i);

					if (open.get(i).contains(gridCell)) {
						open.get(i).remove(gridCell);
					}
					open.get(i).add(gridCell);

				}
			}
		}

		closed.get(i).put(cell.xCord + "," + cell.yCord, cell);
	}

	public static Double[] runAStar(DiscretizedMap map, int algorithm, double weight, int heuristic) {

		w_val = weight;

		if (weight == -1 && algorithm == 2) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String somevalue;
			try {
				System.out.println("Enter an integer for weight");
				somevalue = br.readLine();
				w_val = Integer.parseInt(somevalue);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		HashMap<String, GridCell> Visited = new HashMap<String, GridCell>();

		GridCell start = map.startCell;
		GridCell goal = map.goalCell;

		start.g_array[0] = 0;
		start.parent_array[0] = start;

		PriorityQueue<GridCell> Fringe = new PriorityQueue<GridCell>();

		//start.f_index = 0;
		
		AStar.lastIndex = 0;
		start.calculateF(map, algorithm, w_val, heuristic);
		Fringe.add(start);

		int cycle_count = 0;
		int max_fringe_size = 0;

		double startTime = System.currentTimeMillis();

		while (true) {

			cycle_count += 1;
			max_fringe_size = Math.max(max_fringe_size, Fringe.size());

			if (Fringe.size() == 0) {
				System.out.println("Goal is unreachable from Start Point");
				return null;
				// break;
			}

			GridCell currentCell = Fringe.poll();

			while (Visited.containsKey(currentCell.xCord + "," + currentCell.yCord)) {
				currentCell = Fringe.poll();
			}

			currentCell.seen = true;

			if (currentCell == goal) {

				double endTime = System.currentTimeMillis();

				printStats(endTime - startTime, cycle_count, max_fringe_size, currentCell.g_array[0]);

				GridCell pointer = goal;

				while (pointer != start) {

					map.map[(int) pointer.parent_array[0].xCord][(int) pointer.parent_array[0].yCord].setPath(true);

					pointer = pointer.parent_array[0];
				}

				Double[] data = { currentCell.g_array[0], (double) cycle_count, (double) max_fringe_size,
						(endTime - startTime) };
				return data;
				// break;
			}

			Visited.put(currentCell.xCord + "," + currentCell.yCord, currentCell);

			ArrayList<GridCell> neighbors = getNeighbors(currentCell, map, algorithm, heuristic);

			for (int i = 0; i < neighbors.size(); i++) {
				GridCell currentNeighbor = neighbors.get(i);
				double lineDistance = getCost(currentCell, currentNeighbor);

				if (currentCell.g_array[0] + lineDistance < currentNeighbor.g_array[0]) {
					currentNeighbor.g_array[0] = currentCell.g_array[0] + lineDistance;
					currentNeighbor.parent_array[0] = currentCell;

					if (Fringe.contains(currentNeighbor)) {
						Fringe.remove(currentNeighbor);

						//currentNeighbor.f_index = 0;
						AStar.lastIndex = 0;
						currentNeighbor.calculateF(map, algorithm, w_val, heuristic);
						Fringe.add(currentNeighbor);
					} else {

						//currentNeighbor.f_index = 0;
						AStar.lastIndex = 0;
						currentNeighbor.calculateF(map, algorithm, w_val, heuristic);
						Fringe.add(currentNeighbor);
					}

				}

				double value = lineDistance + currentNeighbor.h;
				double absValue = Math.abs(currentCell.h - value);

				// Consistency Check
				if (!(currentCell.h <= lineDistance + currentNeighbor.h) && currentNeighbor != goal && algorithm != 0) {
					if (absValue > 0) {
						if (absValue > 0.001) {
							currentCell.consistent = 1;
							Main.consistent = false;
						}
					}
				}
			}

		}

	}

	private static double getCost(GridCell currentCell, GridCell currentNeighbor) {
		double lineDistance = 1;

		if (currentCell.getType() == cellType.Unblocked && currentNeighbor.getType() == cellType.Unblocked) {
			if (currentCell.xCord == currentNeighbor.xCord || currentCell.yCord == currentNeighbor.yCord) {
				lineDistance = 1;
			} else {
				lineDistance = Math.sqrt(2);
			}
		}

		if (currentCell.getType() == cellType.Partial && currentNeighbor.getType() == cellType.Partial) {
			if (currentCell.xCord == currentNeighbor.xCord || currentCell.yCord == currentNeighbor.yCord) {
				lineDistance = 2;
			} else {
				lineDistance = Math.sqrt(8);
			}
		}

		else if ((currentCell.getType() == cellType.Partial && currentNeighbor.getType() == cellType.Unblocked)
				|| (currentCell.getType() == cellType.Unblocked && currentNeighbor.getType() == cellType.Partial)) {

			if (currentCell.xCord == currentNeighbor.xCord || currentCell.yCord == currentNeighbor.yCord) {
				lineDistance = 1.5;
			} else {
				lineDistance = (Math.sqrt(2) + Math.sqrt(8)) / 2;
			}

		}

		lineDistance = lineDistance * 4;

		if (currentCell.highway() && currentNeighbor.highway()) {
			lineDistance = lineDistance / 4;
		}

		return lineDistance;
	}

	private static ArrayList<GridCell> getNeighbors(GridCell current, DiscretizedMap map, int algorithm,
			int heuristic) {

		ArrayList<GridCell> neighbors = new ArrayList<GridCell>();

		int x = (int) current.xCord;
		int y = (int) current.yCord;

		// Get eight cells around current
		if (x + 1 < 120 && map.map[x + 1][y].getType() != cellType.Blocked) {
			neighbors.add(map.map[x + 1][y]);
		}
		if (y + 1 < 160 && map.map[x][y + 1].getType() != cellType.Blocked) {
			neighbors.add(map.map[x][y + 1]);
		}
		if (x + 1 > 120 && y + 1 < 160 && map.map[x + 1][y + 1].getType() != cellType.Blocked) {
			neighbors.add(map.map[x + 1][y + 1]);
		}
		if (x - 1 >= 0 && map.map[x - 1][y].getType() != cellType.Blocked) {
			neighbors.add(map.map[x - 1][y]);
		}
		if (y - 1 >= 0 && map.map[x][y - 1].getType() != cellType.Blocked) {
			neighbors.add(map.map[x][y - 1]);
		}
		if (x - 1 >= 0 && y - 1 > -0 && map.map[x - 1][y - 1].getType() != cellType.Blocked) {
			neighbors.add(map.map[x - 1][y - 1]);
		}
		if (x - 1 >= 0 && y + 1 < 160 && map.map[x - 1][y + 1].getType() != cellType.Blocked) {
			neighbors.add(map.map[x - 1][y + 1]);
		}
		if (x + 1 < 120 && y - 1 >= 0 && map.map[x + 1][y - 1].getType() != cellType.Blocked) {
			neighbors.add(map.map[x + 1][y - 1]);
		}

		for (int i = 0; i < neighbors.size(); i++) {
			GridCell cell = neighbors.get(i);

			//cell.f_index = 0;
			AStar.lastIndex = 0;
			cell.calculateF(map, algorithm, w_val, heuristic);
		}

		return neighbors;
	}

}
