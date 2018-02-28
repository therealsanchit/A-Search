package Search;

public class Vertex implements Comparable<Vertex> {

	public int xCord;
	public int yCord;

	Vertex parent;

	public double g;
	public double h;

	public int compareTo(Vertex other) {

		if ((other.g + other.h) > (this.g + this.h)) {
			return 1;
		}

		if ((other.g + other.h) == (this.g + this.h)) {
			return 0;
		}

		else {
			return 1;
		}
	}

}
