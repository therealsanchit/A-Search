package Grid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.*;

import Map.DiscretizedMap;
import Map.GridCell;
import Map.GridCell.cellType;

public class ColorGrid extends JPanel {
	private MyColor[][] myColors;
	private JLabel[][] myLabels;
	public static String[][] labelInfo;
	GridCell[][] grid;

	public ColorGrid(int rows, int cols, int cellWidth, DiscretizedMap map) {
		myColors = new MyColor[rows][cols];
		myLabels = new JLabel[rows][cols];
		labelInfo = new String[rows][cols];
		MyMouseListener myListener = new MyMouseListener(this);
		Dimension labelPrefSize = new Dimension(cellWidth, cellWidth);
		setLayout(new GridLayout(rows, cols));

		grid = map.map;

		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[row].length; col++) {
				JLabel myLabel = new JLabel();
				myLabel = new JLabel();
				myLabel.setOpaque(true);
				initalizeLabelInformation(row, col);
				MyColor myColor;

				if (grid[row][col].consistent == 1) {
					myColor = MyColor.Pink;
				} else if (grid[row][col].seen && grid[row][col].getType() != cellType.Blocked
						&& grid[row][col].getType() != cellType.Partial && grid[row][col].isPath() == false
						&& grid[row][col].isCanAccelerate() == false && grid[row][col].isGoal() == false
						&& grid[row][col].isStart() == false) {
					myColor = MyColor.Green;
				} else if (grid[row][col].isStart()) {
					myColor = MyColor.Cyan;
				} else if (grid[row][col].isGoal()) {
					myColor = MyColor.Yellow;
				} else if (grid[row][col].isPath() && grid[row][col].isCanAccelerate()) {
					myColor = MyColor.Orange;
				} else if (grid[row][col].isPath()) {
					myColor = MyColor.Red;
				} else if (grid[row][col].isCanAccelerate()) {
					myColor = MyColor.Blue;
				} else if (grid[row][col].getType() == cellType.Unblocked) {
					myColor = MyColor.White;
				} else if (grid[row][col].getType() == cellType.Partial) {
					myColor = MyColor.Gray;
				}

				else
					myColor = MyColor.Black;

				myColors[row][col] = myColor;
				myLabel.setBackground(myColor.getColor());
				myLabel.addMouseListener(myListener);
				myLabel.setPreferredSize(labelPrefSize);
				myLabel.setBorder(BorderFactory.createLineBorder(Color.black));
				add(myLabel);
				myLabels[row][col] = myLabel;
			}
		}
	}

	public MyColor[][] getMyColors() {
		return myColors;
	}

	private void initalizeLabelInformation(int x, int y) {
		String type = grid[x][y].getType().toString();
		String fVal = (Arrays.toString(grid[x][y].f_array));
		String gVal = (Arrays.toString(grid[x][y].g_array));
		String hVal = Double.toString(grid[x][y].h);
		if (grid[x][y].isGoal()) {
			labelInfo[x][y] = "F Value :  " + fVal + ", G Value :  " + gVal + ", H Value :  " + hVal + ", Type :  "
					+ type + ", Goal Cell";
		} else if (grid[x][y].isStart()) {
			labelInfo[x][y] = "F Value :  " + fVal + ", G Value :  " + gVal + ", H Value :  " + hVal + ", Type :  "
					+ type + ", Start Cell";
		} else if (grid[x][y].isCanAccelerate())
			labelInfo[x][y] = "F Value :  " + fVal + ", G Value :  " + gVal + ", H Value :  " + hVal + ", Type :  "
					+ type + ", Highway";
		else
			labelInfo[x][y] = "F Value :  " + fVal + ", G Value :  " + gVal + ", H Value :  " + hVal + ", Type :  "
					+ type;
	}

	public void labelPressed(JLabel label) {
		for (int row = 0; row < myLabels.length; row++) {
			for (int col = 0; col < myLabels[row].length; col++) {
				if (label == myLabels[row][col]) {
					JOptionPane.showMessageDialog(label.getParent(), labelInfo[row][col]);
				}
			}
		}
	}
}