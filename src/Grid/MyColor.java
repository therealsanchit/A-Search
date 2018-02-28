package Grid;

import java.awt.Color;

public enum MyColor {
	White(Color.white, "White"), Black(Color.black, "Black"), Gray(Color.gray, "Gray"), Blue(Color.blue,
			"Blue"), Yellow(Color.yellow,
					"Yellow"), Cyan(Color.cyan, "Cyan"), Red(Color.red, "red"), Orange(Color.orange, "orange"), Pink(Color.pink, "pink"), Green(Color.green, "green");

	private Color color;
	private String name;

	private MyColor(Color color, String name) {
		this.color = color;
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}