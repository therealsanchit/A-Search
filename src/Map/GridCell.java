package Map;

import Search.AStar;
import Search.Vertex;

public class GridCell implements Comparable<GridCell> {

	public enum cellType {
		Blocked, Unblocked, Partial
	}
	
	public enum cellDirection{
		Vertical, Horizontal, Diagonal
	}
	
	private boolean canAccelerate = false;
	private boolean isStart = false;
	private boolean isGoal = false;
	private boolean isPath = false;
	
	public boolean visited = false;
	
	//Visualization
	public int consistent = 0; //0: Consistent, 1: Inconsistent Start 2: Inconsistent End
	public boolean seen = false;
	
	public int yCord;
	public int xCord;
	
	
	//public GridCell parent = null;
	
	//public double f;
	//public double g = Double.POSITIVE_INFINITY;
	public double h;
	
	public int times_expanded = 0;
	public boolean expanded_by_anchor = false;
	public double expanded_by_heuristic_at = Double.POSITIVE_INFINITY;
	
	//Sequential and Integrated
	//public int f_index1 = AStar.lastIndex;
	public int f_index = 0;
	public double[] f_array = {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
	public double[] g_array = {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
	public GridCell[] parent_array = {null, null, null, null, null};
	
//	@Override
//	public int compareTo(GridCell other){
//		
//		if((other.f) > (this.f)){
//			return -1;
//		}
//		
//		if((other.f) == (this.f)){
//			if(other.g > this.g) return -1;
//			if(other.g < this.g) return 1;
//			else {
//				if(Math.random() > 0.5) return 1; //look for this again
//				else return -1;
//			}
//		}	
//		
//		else{
//			return 1;
//		}
//	}
	
	@Override
	public int compareTo(GridCell other){
		
		//other.f_index = f_index;
		
		if((other.f_array[AStar.lastIndex]) > (this.f_array[AStar.lastIndex])){
			return -1;
		}
		
		if((other.f_array[AStar.lastIndex]) == (this.f_array[AStar.lastIndex])){
			if(other.g_array[AStar.lastIndex] > this.g_array[AStar.lastIndex]) return -1;
			if(other.g_array[AStar.lastIndex] < this.g_array[AStar.lastIndex]) return 1;
			else {
				if(Math.random() > 0.5) return 1; //look for this again
				else return -1;
			}
		}	
		
		else{
			return 1;
		}
	}
	
	public void calculateF(DiscretizedMap map, int algorithm, double w_val, int heuristic){
		
		GridCell start = map.startCell;
		GridCell goal = map.goalCell;
		
		//////////////////////Heuristics///////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////
		
		//Manhattan Distance - 10% Faster than Diagonal Line, but NOT CONSISTENT
		if(heuristic == 2){
			
			double deltax = Math.abs(this.xCord - goal.xCord);
			double deltay = Math.abs(this.yCord - goal.yCord);
			this.h = (deltax + deltay);
			
		}
		
		//Path is 20% longer but found 90% faster (KEEP ME)
		if(heuristic == 1){
			//Diagonal distance with non highway cells costing more
			int deltax = Math.abs(this.xCord - goal.xCord);
			int deltay = Math.abs(this.yCord - goal.yCord);
			this.h = (deltax + deltay) + (1.41 - 2)*(Math.min(deltax, deltay));
			
			if(!this.canAccelerate) this.h = 4*this.h;
		}
				
		//(CONSISTENT)
		if(heuristic == 0){
			//Diagonal Line Distance with estimated root
			int deltax = Math.abs(this.xCord - goal.xCord);
			int deltay = Math.abs(this.yCord - goal.yCord);
			this.h = (deltax + deltay) + (1.41 - 2)*(Math.min(deltax, deltay));
		}
			
		//NOT CONSISTENT, but weighted = 2 is fast and pretty good
		if(heuristic == 3){
			
			int deltax = Math.abs(this.xCord - goal.xCord);
			int deltay = Math.abs(this.yCord - goal.yCord);
			this.h = .5 *((deltax + deltay) + (1.41 - 2)*(Math.min(deltax, deltay))) + .5*((deltax + deltay));
		
		}
		
		//////////////////////////////////////////////////////////////////////////////////
		
		//Hill Climbing Generation - Used to generate values for heuristic 4
		if(heuristic == 6){
			
			float a = Main.Main.values[0];
			float b = Main.Main.values[1];
			float c = Main.Main.values[2];
			float d = Main.Main.values[3];
			float e = Main.Main.values[4];
//			int f = Main.Main.values[5];
//			int g = Main.Main.values[6];
//			int h = Main.Main.values[7];
			
			float deltax = a * Math.abs(this.xCord - goal.xCord);
			float deltay = b * Math.abs(this.yCord - goal.yCord);
			this.h = c *((deltax + deltay) + d*(Math.min(deltax, deltay))) + e*((deltax * deltay));
			
		}
		
//		double a = 0.3639308879945914;
//		double b = 2.226179864500377;
//		double c = 0.6413129281321133;
//		double d = 0.8057644670890574;
//		double e = 0.9919436328721256;
//		double f = 0.9335790114655934;
//		double g = -0.5212601271836836;
//		double h = 1.0009546418335136;
		
		float a = 0.4058546f;
		float b = -0.058744896f;
		float c = -0.06147058f;
		float d = 0.7910093f;
		float e = 0.20408602f;
		
		//Hill Climbing
		if(heuristic == 4){
						
			float deltax = a * Math.abs(this.xCord - goal.xCord);
			float deltay = b * Math.abs(this.yCord - goal.yCord);
			this.h = c *((deltax + deltay) + d*(Math.min(deltax, deltay))) + e*((deltax + deltay));		
			
		}
		
		int local_g_index = AStar.lastIndex;
		if(AStar.integrated){
			local_g_index = 0;
		}
		
		if(algorithm == 0){
			//Uniform Cost Search
			
			//this.f = this.g;
			this.f_array[AStar.lastIndex] = this.g_array[local_g_index];
		}
		
		else if(algorithm == 1){
			//Unweighted
			
			//this.f = this.g + this.h;
			this.f_array[AStar.lastIndex] = this.g_array[local_g_index] + this.h;
		}
		
		else if(algorithm == 2){
			//Weighted
			
			//this.f = this.g + (w_val * this.h);
			this.f_array[AStar.lastIndex] = this.g_array[local_g_index] + (w_val * this.h);
		}

		
	}
	
	public String toString(){
		return this.xCord + ", " + this.yCord;
	}
	
	
	public boolean isCanAccelerate() {
		return canAccelerate;
	}

	public void setCanAccelerate(boolean canAccelerate) {
		this.canAccelerate = canAccelerate;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	
	public void setPath(boolean isPath) {
		this.isPath = isPath;
	}
	
	public boolean isPath(){
		return this.isPath;
	}

	public boolean isGoal() {
		return isGoal;
	}

	public void setGoal(boolean isGoal) {
		this.isGoal = isGoal;
	}

	public void setyCord(int yCord) {
		this.yCord = yCord;
	}

	public void setxCord(int xCord) {
		this.xCord = xCord;
	}

	public void setType(cellType type) {
		this.type = type;
	}

	public cellType type;
	cellDirection direction;
	
	public GridCell(cellType type, boolean canAccelerate, int x, int y) {
		this.type = type;
		this.canAccelerate = canAccelerate;
		this.xCord = x;
		this.yCord = y;
	}
	
	public double getxCord(){
		return this.xCord;
	}
	
	public double getyCord(){
		return this.yCord;
	}
	
	public cellType getType(){
		return this.type;
	}
	
	public boolean highway(){
		return this.canAccelerate;
	}
	
}
