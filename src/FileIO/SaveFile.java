package FileIO;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import Map.GridCell;

public class SaveFile {

	public static boolean SaveMap(String MapName, GridCell start, GridCell goal, Map<String, String> randomPartialCords,
			GridCell map[][]) throws IOException {

		FileWriter csv_writer = new FileWriter(MapName + ".csv");

		// Write Start
		csv_writer.append("" + start.getxCord());
		csv_writer.append(",");
		csv_writer.append("" + start.getyCord());
		csv_writer.append("\n");

		// Write Goal
		csv_writer.append("" + goal.getxCord());
		csv_writer.append(",");
		csv_writer.append("" + goal.getyCord());
		csv_writer.append("\n");

		// Write Random Partial Cell Coordinates
		Iterator<Entry<String, String>> iter = randomPartialCords.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> values = (Map.Entry<String, String>) iter.next();
			csv_writer.append(values.getKey() + "," + values.getValue() + "\n");
			iter.remove();
		}

		// Write Map
		for (int i = 0; i < 120; i++) {
			for (int j = 0; j < 160; j++) {

				GridCell cell = map[i][j];
				GridCell.cellType type = cell.getType();

				String value = "";
				if (type == GridCell.cellType.Blocked) {
					value = "0";
				} else if (type == GridCell.cellType.Unblocked && !cell.highway()) {
					value = "1";
				} else if (type == GridCell.cellType.Partial && !cell.highway()) {
					value = "2";
				} else if (type == GridCell.cellType.Unblocked && cell.highway()) {
					value = "a";
				} else if (type == GridCell.cellType.Partial && cell.highway()) {
					value = "b";
				}

				csv_writer.append(value);
				csv_writer.append(",");

			}

			csv_writer.append("\n");

		}

		// Close file
		csv_writer.flush();
		csv_writer.close();

		return false;
	}

}
