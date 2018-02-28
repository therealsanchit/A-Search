package Main;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.plaf.synth.SynthSeparatorUI;

import FileIO.LoadFile;
import FileIO.SaveFile;
import Grid.ColorGrid;
import Map.DiscretizedMap;
import Search.AStar;

public class Main {

	public static Float[] values;
	public static boolean consistent;
	public static double optimal_path = Double.POSITIVE_INFINITY;

	public static void sub_optimal_checker(){
		
		DiscretizedMap map = new DiscretizedMap();
		DiscretizedMap safeMap = new DiscretizedMap();
		DiscretizedMap safestMap = new DiscretizedMap();

		map.initializeMap();
		map.placePartialCells();
		safeMap = map;
		safestMap = map;
		int highwaysCreated = 0;
		int i = 0;

		while (true) {
			if (highwaysCreated == 4)
				break;
			if (i > 5000) {
				highwaysCreated = 0;
				map = safestMap;
			}
			boolean create = map.createHighway(map);
			if (create == true) {
				highwaysCreated++;
				safeMap = map;
			}

			else
				map = safeMap;
			i++;
		}

		map.placeBlockedCells();

		while (map.initalizeStartGoal() == false) {
			map.initalizeStartGoal();
		}
		map.startCell.setStart(true);
		map.goalCell.setGoal(true);
		
		Main.consistent = true;
		map.resetPath();
		Double[] data = AStar.runAStar(map, 0, 0, 0);
		Double path_length = data[0];
		
		System.out.println("Optimal Path Length is: " + path_length);
		Main.optimal_path = path_length;
		
		
		map.resetPath();
		
		AStar.runSeqAStar(map);
		createAndShowGui(map);
		
	}
	
	public static void AStarTester(DiscretizedMap map, int algorithm, double weight, int heuristic, int type) {

		if (type == 0)
			AStar.runAStar(map, algorithm, weight, heuristic);

		if (type == 1)
			AStar.runSeqAStar(map);

		if (type == 2)
			AStar.runIntAStar(map);

		return;
	}

	public static void HillClimbingHeuristic(BufferedReader br) throws IOException {

		Main.values = new Float[] { 10f, 7f, 4f, 3f, 2f };
		Main.consistent = true;

		ArrayList<Float[]> paths = new ArrayList<Float[]>();

		int map_count = 20;

		while (map_count > 0) {

			Float[] best = new Float[] { 1f, 1f, 1f, 1f, 1f };

			double best_path = Double.POSITIVE_INFINITY;
			double best_time = Double.POSITIVE_INFINITY;
			double best_score = Double.POSITIVE_INFINITY;

			// New Map
			int tries = 100;
			int best_count = 0;

			DiscretizedMap map = new DiscretizedMap();
			DiscretizedMap safeMap = new DiscretizedMap();
			DiscretizedMap safestMap = new DiscretizedMap();

			map.initializeMap();
			map.placePartialCells();
			safeMap = map;
			safestMap = map;
			int highwaysCreated = 0;
			int i = 0;

			while (true) {
				if (highwaysCreated == 4)
					break;
				if (i > 5000) {
					highwaysCreated = 0;
					map = safestMap;
				}
				boolean create = map.createHighway(map);
				if (create == true) {
					highwaysCreated++;
					safeMap = map;
				}

				else
					map = safeMap;
				i++;
			}

			map.placeBlockedCells();

			while (map.initalizeStartGoal() == false) {
				map.initalizeStartGoal();
			}
			map.startCell.setStart(true);
			map.goalCell.setGoal(true);
			;

			while (tries > 0 && best_count < 100) {

				System.out.println(Arrays.toString(best));

				Main.consistent = true;
				map.resetPath();
				Double[] data = AStar.runAStar(map, 1, 1, 6);
				Double cycles = data[1];
				Double path = data[0];
				Double time = data[3];

				// Double score = (path * 5) + (cycles) + (time * 10);
				Double score = time;

				// Specify if solution needs to be consistent
				// if(score < best_score){
				if (score < best_score && Main.consistent) {

					best_score = score;
					best_path = path;
					best_time = time;

					best[0] = Main.values[0];
					best[1] = Main.values[1];
					best[2] = Main.values[2];
					best[3] = Main.values[3];
					best[4] = Main.values[4];
					// best[5] = Main.values[5];
					// best[6] = Main.values[6];
					// best[7] = Main.values[7];
					best_count++;

				}

				else {

					Main.values[0] = best[0];
					Main.values[1] = best[1];
					Main.values[2] = best[2];
					Main.values[3] = best[3];
					Main.values[4] = best[4];
					// Main.values[5] = best[5];
					// Main.values[6] = best[6];
					// Main.values[7] = best[7];

					int next = (int) Math.rint((Math.random() * 4));
					float randomNum = -3 + (float) (Math.random() * 3);

					Main.values[next] += randomNum;
					tries = tries - 1;

				}

			}

			paths.add(best);

			map_count--;

		}

		System.out.println("\n\n");
		System.out.println("##########################################");
		System.out.println("Done");
		System.out.println("******");

		String[] Code = { "double a = ", "double b = ", "double c = ", "double d = ", "double e = ", "double f = ",
				"double g = ", "double h = " };

		for (int j = 0; j < paths.size(); j++) {
			System.out.println(Arrays.toString(paths.get(j)));
		}

		System.out.println("------");

		for (int i = 0; i < 5; i++) {

			float total = 0;
			for (int j = 0; j < paths.size(); j++) {
				total = total + paths.get(j)[i];
			}
			total = (float) (total / paths.size());
			System.out.println(Code[i] + total + ";");
		}

	}

	public static void createAndShowGui(DiscretizedMap map) {
		int rows = 120;
		int cols = 160;
		int cellWidth = 20;
		ColorGrid mainPanel = new ColorGrid(rows, cols, cellWidth, map);

		JFrame frame = new JFrame("Color Grid");
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	public static DiscretizedMap newMap(DiscretizedMap map, DiscretizedMap safeMap, DiscretizedMap safestMap,
			int algorithm, int type) {

		map.initializeMap();
		map.placePartialCells();
		safeMap = map;
		safestMap = map;
		int highwaysCreated = 0;
		int i = 0;

		while (true) {
			if (highwaysCreated == 4)
				break;
			if (i > 5000) {
				highwaysCreated = 0;
				map = safestMap;
			}
			boolean create = map.createHighway(map);
			if (create == true) {
				highwaysCreated++;
				safeMap = map;
			}

			else
				map = safeMap;
			i++;
		}
		System.out.println("Highway created in " + i + " tries.");
		map.placeBlockedCells();

		while (map.initalizeStartGoal() == false) {
			map.initalizeStartGoal();
		}
		map.startCell.setStart(true);
		map.goalCell.setGoal(true);
		map.startCell.g_array[0] = 0;
		map.startCell.g_array[1] = 0;
		map.startCell.g_array[2] = 0;
		map.startCell.g_array[3] = 0;
		map.startCell.g_array[4] = 0;

		System.out.println();
		System.out.println("Start coordinates are : " + map.startCell.xCord + " , " + map.startCell.yCord);
		System.out.println("Goal  coordinates are : " + map.goalCell.xCord + " , " + map.goalCell.yCord);
		System.out.println();
		System.out.println();
		int heuristic = 0;

		if (type == 0 && algorithm != 0) {
			System.out.println("Choose a Heuristic");
			System.out.println("0. Diagonal Distance");
			System.out.println("1. Highway Cost Consideration");
			System.out.println("2. Manhattan Distance");
			System.out.println("3. Diagonal Distance & Manhattan Distance");
			System.out.println("4. Hill Climbing Generated Heuristic");

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			try {
				heuristic = Integer.parseInt(br.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		AStarTester(map, algorithm, -1, heuristic, type);
		// AStar.runSeqAStar(map);
		createAndShowGui(map);

		return map;

	}

	public static void Menu1(BufferedReader br) throws IOException {

		System.out.println("Enter one of the numbers below");
		System.out.println("1. New Map");
		System.out.println("2. Load Map");
		System.out.println("3. 50 Run Generator");
		System.out.println("4. Heuristic Generator");

		String value = br.readLine();

		if (value.equals("1")) {
			NewMapMenu(br);
		}

		if (value.equals("2")) {
			LoadMapMenu(br);
		}
		if (value.equals("3")) {
			RunGenerator(br);
		}

		if (value.equals("4")) {
			HillClimbingHeuristic(br);
		}
		
		if(value.equals("5")){
			sub_optimal_checker();
		}

		else {
			Menu1(br);
		}

	}

	public static void RunGenerator(BufferedReader br) throws IOException {

		// First create a new map

		FileWriter writer = new FileWriter("data.csv");

		writer.append(
				"Name,Map Number,Start/Goal Number,Heuristic,Path Length,Cycles,Max Fringe Size,Computation Time (ms),Weight");
		writer.append("\n");

		int run_id = 1;

		DiscretizedMap map = new DiscretizedMap();
		DiscretizedMap safeMap = new DiscretizedMap();
		DiscretizedMap safestMap = new DiscretizedMap();

		String[] names = { "Uniform Cost", "A* Search", "Weighted A* Search", "Sequential A* Search",
				"Integrated A* Search" };

		for (int mapNum = 0; mapNum < 5; mapNum++) {

			// New Map needs to created

			System.out.println("------------------------");
			System.out.println("New Map Created");
			System.out.println("------------------------");

			map.initializeMap();
			map.placePartialCells();
			safeMap = map;
			safestMap = map;
			int highwaysCreated = 0;
			int i = 0;

			while (true) {
				if (highwaysCreated == 4)
					break;
				if (i > 5000) {
					highwaysCreated = 0;
					map = safestMap;
				}
				boolean create = map.createHighway(map);
				if (create == true) {
					highwaysCreated++;
					safeMap = map;
				}

				else
					map = safeMap;
				i++;
			}
			// System.out.println("Highway created in " + i + " tries.");
			map.placeBlockedCells();

			for (int startGoal = 0; startGoal < 10; startGoal++) {

				while (map.initalizeStartGoal() == false) {
					map.initalizeStartGoal();
				}
				map.startCell.setStart(true);
				map.goalCell.setGoal(true);

				double weight = 1.25;
				Double[] data;
				
				for(int run = 0; run < 4; run++){
				
				if(run == 0){AStar.w_val = 1.25; AStar.w2_val = 1.25;}
				if(run == 1){AStar.w_val = 1.25; AStar.w2_val = 2;}
				if(run == 2){AStar.w_val = 2; AStar.w2_val = 1.25;}
				if(run == 3){AStar.w_val = 2; AStar.w2_val = 2;}
					
				System.out.println("\n");
				System.out.println("#############################");
				System.out.println("Algorithm " + run_id + ": " + names[3] + " Weight: " + weight);
				System.out.println("#############################");
				System.out.println("\n");

				map.resetPath();

				data = AStar.runSeqAStar(map);

				writer.append(Integer.toString(run_id) + ": " + names[3]);
				writer.append(",");

				writer.append(Integer.toString(mapNum));
				writer.append(",");

				writer.append(Integer.toString(startGoal));
				writer.append(",");

				writer.append("~");
				writer.append(",");

				writer.append(Double.toString(data[0]));
				writer.append(",");

				writer.append(Double.toString(data[1]));
				writer.append(",");

				writer.append(Double.toString(data[2]));
				writer.append(",");

				writer.append(Double.toString(data[3]));
				writer.append(",");

				writer.append(Double.toString(weight));
				writer.append(",");
				
				writer.append(Double.toString(AStar.w_val));
				writer.append(",");
				writer.append(Double.toString(AStar.w2_val));
				writer.append("\n");

				run_id++;

				weight = 2;

				System.out.println("\n");
				System.out.println("#############################");
				System.out.println("Algorithm " + run_id + ": " + names[4] + " Weight: " + weight);
				System.out.println("#############################");
				System.out.println("\n");

				map.resetPath();

				data = AStar.runIntAStar(map);

				writer.append(Integer.toString(run_id) + ": " + names[4]);
				writer.append(",");

				writer.append(Integer.toString(mapNum));
				writer.append(",");

				writer.append(Integer.toString(startGoal));
				writer.append(",");

				writer.append("~");
				writer.append(",");

				writer.append(Double.toString(data[0]));
				writer.append(",");

				writer.append(Double.toString(data[1]));
				writer.append(",");

				writer.append(Double.toString(data[2]));
				writer.append(",");

				writer.append(Double.toString(data[3]));
				writer.append(",");

				writer.append(Double.toString(weight));
				writer.append(",");
				
				writer.append(Double.toString(AStar.w_val));
				writer.append(",");
				writer.append(Double.toString(AStar.w2_val));
				writer.append("\n");

				run_id++;
				
				}

				for (int algorithm = 0; algorithm < 3; algorithm++) {

					for (int heuristic = 0; heuristic < 5; heuristic++) {

						if (algorithm == 0) {
							heuristic = 5;
						}

						if (algorithm == 2) {
							// Run for each weight

							weight = 1.25;

							System.out.println("\n");
							System.out.println("#############################");
							System.out.println("Algorithm " + run_id + ": " + names[algorithm] + " Weight: " + weight);
							System.out.println("#############################");
							System.out.println("\n");

							map.resetPath();

							data = AStar.runAStar(map, algorithm, weight, heuristic);

							writer.append(Integer.toString(run_id) + ": " + names[algorithm]);
							writer.append(",");

							writer.append(Integer.toString(mapNum));
							writer.append(",");

							writer.append(Integer.toString(startGoal));
							writer.append(",");

							writer.append(Integer.toString(heuristic));
							writer.append(",");

							writer.append(Double.toString(data[0]));
							writer.append(",");

							writer.append(Double.toString(data[1]));
							writer.append(",");

							writer.append(Double.toString(data[2]));
							writer.append(",");

							writer.append(Double.toString(data[3]));
							writer.append(",");

							writer.append(Double.toString(weight));
							writer.append("\n");

							run_id++;

							weight = 2;

							System.out.println("\n");
							System.out.println("#############################");
							System.out.println("Algorithm " + run_id + ": " + names[algorithm] + " Weight: " + weight);
							System.out.println("#############################");
							System.out.println("\n");

							map.resetPath();

							data = AStar.runAStar(map, algorithm, weight, heuristic);

							writer.append(Integer.toString(run_id) + ": " + names[algorithm]);
							writer.append(",");

							writer.append(Integer.toString(mapNum));
							writer.append(",");

							writer.append(Integer.toString(startGoal));
							writer.append(",");

							writer.append(Integer.toString(heuristic));
							writer.append(",");

							writer.append(Double.toString(data[0]));
							writer.append(",");

							writer.append(Double.toString(data[1]));
							writer.append(",");

							writer.append(Double.toString(data[2]));
							writer.append(",");

							writer.append(Double.toString(data[3]));
							writer.append(",");

							writer.append(Double.toString(weight));
							writer.append("\n");

							run_id++;

						}

						else {

							System.out.println("\n");
							System.out.println("#############################");
							System.out.println("Algorithm " + run_id + ": " + names[algorithm] + " " + heuristic);
							System.out.println("#############################");
							System.out.println("\n");

							map.resetPath();
							data = AStar.runAStar(map, algorithm, -1, heuristic);

							writer.append(Integer.toString(run_id) + ": " + names[algorithm]);
							writer.append(",");

							writer.append(Integer.toString(mapNum));
							writer.append(",");

							writer.append(Integer.toString(startGoal));
							writer.append(",");

							writer.append(Integer.toString(heuristic));
							writer.append(",");

							writer.append(Double.toString(data[0]));
							writer.append(",");

							writer.append(Double.toString(data[1]));
							writer.append(",");

							writer.append(Double.toString(data[2]));
							writer.append(",");

							writer.append(Double.toString(data[3]));
							writer.append("\n");
							run_id++;

						}

					}

				}
			}
		}

		writer.flush();
		writer.close();

	}

	public static void NewMapMenu(BufferedReader br) throws IOException {

		System.out.println("Choose an algorithm to run");
		System.out.println("1. Uniform Cost");
		System.out.println("2. Singular Unweighted A*");
		System.out.println("3. Singular Weighted A*");
		System.out.println("4. Sequential Weighted A*");
		System.out.println("5. Integrated Weighted A*");
		System.out.println("6. Back to Main Menu");

		String value = br.readLine();

		if (value.equals("1")) {
			createNewMap(br, 0, 0);
		}
		if (value.equals("2")) {
			createNewMap(br, 1, 0);
		}
		if (value.equals("3")) {
			createNewMap(br, 2, 0);
		}
		if (value.equals("4")) {
			createNewMap(br, 2, 1);
		}
		if (value.equals("5")) {
			createNewMap(br, 2, 2);
		} else {
			Menu1(br);
		}

	}

	public static void createNewMap(BufferedReader br, int algorithm, int type) throws IOException {

		DiscretizedMap map = new DiscretizedMap();
		DiscretizedMap safeMap = new DiscretizedMap();
		DiscretizedMap safestMap = new DiscretizedMap();

		map = newMap(map, safeMap, safestMap, algorithm, type);

		RerunMap(br, map);

	}

	public static void RerunMap(BufferedReader br, DiscretizedMap map) throws IOException {

		System.out.println("Choose an re-run option");
		System.out.println("1. Uniform Cost - Same Start/Goal");
		System.out.println("2. Uniform Cost - New Start/Goal");
		System.out.println();
		System.out.println("3. Singular Unweighted A* - Same Start/Goal");
		System.out.println("4. Singular Unweighted A* - New Start/Goal");
		System.out.println();
		System.out.println("5. Singular Weighted A* - Same Start/Goal");
		System.out.println("6. Singular Weighted A* - New Start/Goal");
		System.out.println();
		System.out.println("7. Sequential Weighted A* - Same Start/Goal");
		System.out.println("8. Sequential Weighted A* - New Start/Goal");
		System.out.println();
		System.out.println("9. Integrated Weighted A* - Same Start/Goal");
		System.out.println("10. Integrated Weighted A* - New Start/Goal");
		System.out.println();
		System.out.println("11. Save Map");
		System.out.println("12. Main Menu");

		int type = 0;
		int algorithm = 0;
		boolean reset = false;

		String value = br.readLine();

		if (value.equals("1")) {
			algorithm = 0;
			reset = false;
		}

		else if (value.equals("2")) {
			algorithm = 0;
			reset = true;
		}

		else if (value.equals("3")) {
			algorithm = 1;
			reset = false;
		}

		else if (value.equals("4")) {
			algorithm = 1;
			reset = true;
		}

		else if (value.equals("5")) {
			algorithm = 2;
			reset = false;
		}

		else if (value.equals("6")) {
			algorithm = 2;
			reset = true;
		}

		else if (value.equals("7")) {
			algorithm = 2;
			reset = false;
			type = 1;

		} else if (value.equals("8")) {
			algorithm = 2;
			reset = true;
			type = 1;
		}

		else if (value.equals("9")) {
			algorithm = 2;
			reset = false;
			type = 2;
		}

		else if (value.equals("10")) {
			algorithm = 2;
			reset = true;
			type = 2;
		}

		else if (value.equals("11")) {
			SaveMenu(br, map);
		}

		else if (value.equals("12")) {
			Menu1(br);
		}

		if (reset) {
			System.out.println("Resetting map with new Start/Goal Pairs");
			System.out.println();
			map.startCell.setStart(false);
			map.goalCell.setGoal(false);

			while (map.initalizeStartGoal() == false) {
				map.initalizeStartGoal();
			}
			map.startCell.setStart(true);
			map.goalCell.setGoal(true);

		}

		int heuristic = 0;
		if (type == 0 && algorithm != 0) {
			System.out.println("Choose a Heuristic");
			System.out.println("0. Diagonal Distance");
			System.out.println("1. Highway Cost Consideration");
			System.out.println("2. Manhattan Distance");
			System.out.println("3. Diagonal Distance & Manhattan Distance");
			System.out.println("4. Hill Climbing Generated Heuristic");
			heuristic = Integer.parseInt(br.readLine());

		}

		map.resetPath();
		AStarTester(map, algorithm, -1, heuristic, type);
		createAndShowGui(map);
		RerunMap(br, map);

	}

	public static void SaveMenu(BufferedReader br, DiscretizedMap map) throws IOException {

		System.out.println("Type a filename to save this map");
		String choice = "";
		try {
			choice = br.readLine();
			if (choice == "") {
				RerunMap(br, map);
			}
			SaveFile.SaveMap(choice, map.startCell, map.goalCell, map.randomPartialCords, map.map);
			System.out.println("Saved " + choice);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RerunMap(br, map);
	}

	public static void LoadMapMenu(BufferedReader br) throws IOException {

		System.out.println("Where would you like to load a map from?");
		String choice = "";
		try {
			choice = br.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DiscretizedMap map = new DiscretizedMap();
		DiscretizedMap safeMap = new DiscretizedMap();
		DiscretizedMap safestMap = new DiscretizedMap();

		LoadFile fileLoader = new LoadFile();

		fileLoader.loadFile(choice);
		// fileLoader.loadFile("/home/okick/Desktop/Eclipse Workspace/AI
		// Project/Map1.csv");
		map.map = fileLoader.getTempMap();
		map.randomPartialCords = fileLoader.getRandomPartialCords();
		// map.printMap(map);

		map.startCell = fileLoader.getStart();
		map.goalCell = fileLoader.getGoal();

		RerunMap(br, map);

	}

	public static void main(String[] args) {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try {
			Menu1(br);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}

}
