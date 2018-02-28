package Search;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import Map.GridCell.cellType;

public class Fringe {

	double root;

	ArrayList<Double> Heap;

	public Fringe() {
		Heap = new ArrayList<Double>();
	}

	public void Insert(Double Vertex) {

		if (Heap.isEmpty()) {
			Heap.add(Vertex);
			return;
		}

		Heap.add(Vertex);

		int position = Heap.size(); // Last position (Newly added number)
		int parent_slot = position / 2; // Parent

		position = (position == 0) ? 0 : (position - 1);
		parent_slot = (parent_slot == 0) ? 0 : (parent_slot - 1);

		while (Heap.get(position) < Heap.get(parent_slot)) {

			double parent = Heap.get(parent_slot);
			double child = Heap.get(position);

			Heap.set(parent_slot, child);
			Heap.set(position, parent);

			position = parent_slot;
			parent_slot = position / 2;

		}

		root = Heap.get(0);

		System.out.println(Heap.toString());

	}

	public Double Pop() {

		double return_value = root;

		// Resort Tree

		Heap.set(0, Heap.get(Heap.size() - 1)); // Put last element as root
		Heap.remove(Heap.size() - 1); // Remove last element
		Heap.remove(Heap.size() - 1); // Remove last element

		int location = 0;
		int left_child = (2 * location) + 1;
		int right_child = (2 * location) + 2;

		while (Heap.get(location) > Heap.get(left_child) || Heap.get(location) > Heap.get(right_child)) {

			if (Heap.get(location) > Heap.get(left_child)) {
				// Swap with left child

			} else if (Heap.get(location) > Heap.get(right_child)) {
				// Swap with right child
			}

		}

		System.out.println(Heap.toString());

		return return_value;
	}

	public boolean Remove(Double Vertex) {
		return false;
	}

	public String toString() {
		return Heap.toString();
	}

}
