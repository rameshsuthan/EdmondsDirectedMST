/**
 * Class to represent a vertex of a graph
 * 
 *
 */

import java.util.*;

public class Vertex {
	public Integer name; // name of the vertex
	public boolean seen; // flag to check if the vertex has already been visited
	public Vertex parent; // parent of the vertex
	public int distance; // distance to the vertex from the source vertex
	public List<Edge> Adj, revAdj; // adjacency list; use LinkedList or
									// ArrayList
	public Edge zeroRevEdge;
	public boolean visited;
	public boolean isSuper;
	public Vertex superVert;
	public boolean isActive;
	public SuperVertexPropery prop;

	/**
	 * Constructor for the vertex
	 * 
	 * @param n
	 *            : int - name of the vertex
	 */
	Vertex(int n) {
		name = n;
		seen = false;
		parent = null;
		Adj = new ArrayList<Edge>();
		revAdj = new ArrayList<Edge>(); /* only for directed graphs */
		zeroRevEdge = null;
		isActive = true;
	}

	/**
	 * Method to represent a vertex by its name
	 */
	public String toString() {
		return Integer.toString(name);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (!(obj instanceof Vertex))
			return false;
		Vertex v = (Vertex) obj;
		return this.name.equals(v.name);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return name.hashCode();
	}
}