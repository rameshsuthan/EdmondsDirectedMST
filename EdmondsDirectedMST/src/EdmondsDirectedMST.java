import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 *  
 *@author Ramesh Suthan Palani,Rahul Aravind Mehalingam,Sanjana Ramakrishnan,Anandan Sundar
 *
 */
public class EdmondsDirectedMST {

	public static long wmst = 0;
	public static int tot = 0;

	/**
	 * Method to create the superVertex for the given path and return the cycle;
	 * @param g
	 * @param path
	 * @return
	 */
	public static ArrayList<Edge> createSuperVertex(Graph g,
			ArrayList<Edge> path) {
		// Remove the unnecessary edge from the path to capture the cycle
		Vertex cycleStartVertex = path.get(path.size() - 1).From;
		//System.out.println("Actual Path:" + path);

		//remove the unnecessary edges from the begining till the start of the cycle
		while (cycleStartVertex != path.get(0).To) {
			path.remove(0);
		}
		//System.out.println("Cycle: " + path);
		Edge minIncomingEdge = null;

		Vertex superVertex = new Vertex(++g.numNodes);
		superVertex.isSuper = true;
		g.verts.add(superVertex);

		//deactivate all the vertex with in the cycle
		for (Edge cycleEdge : path) {
			Vertex u = cycleEdge.From;
			// System.out.print(" DeActivate: " + u);
			u.isActive = false;
			u.superVert = superVertex;
		}

		//Map to store the min edges for  adj and rev adj list 
		HashMap<Vertex, Edge> adjMap = new HashMap<Vertex, Edge>();
		HashMap<Vertex, Edge> revAdjMap = new HashMap<Vertex, Edge>();

		for (Edge cycleEdge : path) {
			Vertex u = cycleEdge.From;

			for (Edge e : u.Adj) {
				if (e.To.isActive) {
					//Add or update the edges in adj map for the supervertex
					Edge oldEdge = null;
					if ((oldEdge = adjMap.get(e.To)) != null) {
						if (oldEdge.currWeight > e.currWeight) {
							oldEdge.currWeight = e.currWeight;
							oldEdge.superEdge = e;
							if (e.currWeight == 0) {
								e.To.zeroRevEdge = oldEdge;
								// System.out.println("Adding e.To.zeroRevEdge: "+newEdge);
							}

						}
					} else {
						Edge newEdge = g.addDirectedEdge(superVertex.name,
								e.To.name, e.currWeight);
						newEdge.superEdge = e.superEdge;
						if (e.currWeight == 0) {
							e.To.zeroRevEdge = newEdge;
							// System.out.println("Adding e.To.zeroRevEdge: "+newEdge);
						}

					}

				}

			}

			for (Edge e : u.revAdj) {

				if (e.From.isActive) {
					//Add or update the revadj map for the supervertex
					Edge oldEdge = null;
					if ((oldEdge = revAdjMap.get(e.From)) != null) {
						if (oldEdge.currWeight > e.currWeight) {
							oldEdge.currWeight = e.currWeight;
							oldEdge.superEdge = e;
						}
					} else {
						Edge newEdge = g.addDirectedEdge(e.From.name,
								superVertex.name, e.currWeight);
						newEdge.superEdge = e;

					}

					//storing the minimum incoming edge for the cycle from outside the circuit 
					//for breaking the circuit
					if (minIncomingEdge == null) {
						minIncomingEdge = e;
					} else if (minIncomingEdge.currWeight > e.currWeight) {
						minIncomingEdge = e;

					}

				}
			}
		}

		//updating the adj and revadj list for the supervertex
		superVertex.Adj.addAll(adjMap.values());
		superVertex.revAdj.addAll(revAdjMap.values());
		
		//updating the cycle and minIncoming edge for the supervertex for expansion phase
		superVertex.prop = new SuperVertexPropery();
		superVertex.prop.cycle = path;
		superVertex.prop.minInEdge = minIncomingEdge;
		
		//System.out.println("minEdge: " + minIncomingEdge);
		
		//minimize the weight for the supervertex incoming edges
		minimizeWeight(superVertex);

		return path;
	}
	
	

	/**
	 * Method to find the cycle for the unreachable node provided by the BFS
	 * @param g
	 * @param start
	 * @return
	 */
	public static ArrayList<Edge> findCycle(Graph g, Vertex start) {
		ArrayList<Edge> path = new ArrayList<Edge>();

		for (Vertex u : g) {
			u.visited = false;
		}

		Vertex u = start, v = null;
		while (u != null) {

			Edge e = null;
			if ((e = u.zeroRevEdge) != null) {
				path.add(e);
				v = e.From;
				if (v.visited == true) {
					// System.out.println("cycle found..." + path);
					return path;
				}
				u.visited = true;
				u = v;
			} else {
				break;
			}

		}

		return null;

	}

	/**
	 * Method to return top level supervertex for the given vertex
	 * return null if there is no supervertex
	 * @param v
	 * @return
	 */
	public static Vertex getSuperiorVertex(Vertex v) {
		Vertex superVertex = null;
		while (v.superVert != null) {
			superVertex = v.superVert;
			v = v.superVert;
		}

		return superVertex;
	}

	/**
	 * Method for modified bfs using zero outgoing edges and return the unreachable node
	 * @param g
	 * @param src
	 * @return
	 */
	public static Vertex BFS(Graph g, Vertex src) {

		for (Vertex u : g) {
			u.seen = false;
		}

		Queue<Vertex> queue = new LinkedList<Vertex>();
		queue.add(src);
		src.seen = true;
		// System.out.print("BFS ->");

		while (!queue.isEmpty()) {
			Vertex u = queue.remove();
			for (Edge e : u.Adj) {
				Vertex v = e.To;
				// including condition for Super Vertex
				if (v.isActive == false) {
					// System.err.println("Critical Error"+e);
					continue;
				}

				if (e.currWeight == 0 & !v.seen) {
					v.seen = true;
					v.parent = u;
					// System.out.print(v+" ");
					queue.add(v);
				}
			}
		}

		for (Vertex u : g) {
			if (u.isActive == true && u.seen == false) {
				return u;
			}
		}
		// System.out.println();
		return null;
	}

	/**
	 * Method to minimize the incoming vertex for the given Vertex.
	 * 
	 * @param v
	 */
	public static void minimizeWeight(Vertex v) {
		Edge minEdge = v.revAdj.get(0);
		for (Edge edge : v.revAdj) {
			if (minEdge.currWeight > edge.currWeight) {
				minEdge = edge;
			}
		}

		// System.out.println("minEdgeWeight" + minEdge.currWeight);
		wmst += minEdge.currWeight;

		for (Edge edge : v.revAdj) {
			edge.currWeight = edge.currWeight - minEdge.currWeight;
			if (edge.currWeight == 0) {
				v.zeroRevEdge = edge;
			}
		}
	}

	/**
	 * 
	 * Method to initialize the graph by minimizing the incoming edges for all
	 * the edges except root Vertex
	 * 
	 * @param g
	 * @param src
	 */
	public static void init(Graph g, Vertex src) {

		for (Vertex v : g) {
			if (v != src) {
				minimizeWeight(v);

			}
		}

	}

	public static void printGraphRevAdj(Graph g) {
		System.out.println("Graph-->RevAdjList");
		for (Vertex v : g) {
			if (v.isActive == true)
				System.out.println(v + "-->" + v.revAdj);

		}
	}

	public static void printGraphAdj(Graph g) {
		System.out.println("Graph-->AdjList");
		for (Vertex v : g) {
			// if (v.isActive == true)
			System.out.println(v + "-->" + v.Adj);

		}
	}

	/**
	 * Method to get the final MST for the graph
	 * 
	 * @param g
	 * @param src
	 * @return
	 */
	public static ArrayList<Edge> getMST(Graph g, Vertex src) {

		for (Vertex u : g) {
			u.seen = false;
		}

		ArrayList<Edge> edgeList = new ArrayList<Edge>();

		Queue<Vertex> queue = new LinkedList<Vertex>();
		queue.add(src);
		src.seen = true;
		int vCount = 0;
		// System.out.print("BFS ->");

		while (!queue.isEmpty()) {
			Vertex u = queue.remove();
			for (Edge e : u.Adj) {
				if (e.isActive == false)
					continue;
				Vertex v = e.To;

				if (e.currWeight == 0 && !v.seen) {
					v.seen = true;
					v.parent = u;

					if (!v.isSuper) {
						vCount++;
						edgeList.add(e);
						queue.add(v);
					}
				}
			}
		}

				//System.out.println("vCount" + vCount);

		/*
		 * for (Vertex u : g) { if (u.isActive == true && u.seen == false) {
		 * return u; } }
		 */
		//System.out.println();
		return edgeList;
	}

	/**
	 * Method to get the real edge from the superVertex edge
	 * 
	 * @param e
	 * @return
	 */
	public static Edge getSuperiorEdge(Edge e) {
		Edge superEdge = null;

		while (e.superEdge != null) {
			superEdge = e.superEdge;
			e = e.superEdge;
		}

		return superEdge != null ? superEdge : e;
	}

	/**
	 * Method to Expand the super vertex cycle
	 * 
	 * @param s
	 */
	public static void expandCycle(Vertex s) {
		ArrayList<Edge> cycle = s.prop.cycle;
		Edge minEdge = s.prop.minInEdge;
		Vertex u = minEdge.To;

		// get the superior Edge(Real Edge) in the graph and set the weight to zero
		Edge superEdge = getSuperiorEdge(minEdge);
		superEdge.currWeight = 0;

		for (Edge e : cycle) {
			if (u == e.To) {
				e.isActive = false;
				//System.out.println("Deactivation Edge" + e);
			}

		}

	}

	/**
	 * Method to expand the super vertexes
	 * 
	 * @param g
	 */
	public static void expand(Graph g) {
		int size = g.numNodes;
		Vertex nextSuper = g.verts.get(size);

		while (nextSuper != null) {
			if (nextSuper.isSuper) {
				expandCycle(nextSuper);
			}
			nextSuper = g.verts.get(--size);

		}
	}

	/**
	 * Method to print MST if there is less than 50 edges
	 * @param edgeList
	 * @param vertCount
	 */
	public static void printMST(ArrayList<Edge> edgeList,int vertCount ){
		
		if(vertCount<=50){
			Collections.sort(edgeList, new Comparator<Edge>() {

				@Override
				public int compare(Edge o1, Edge o2) {
					// TODO Auto-generated method stub
					return (o1.To.name - o2.To.name);
				}

			});

			
			for(Edge e:edgeList){
				System.out.println(e);
			}
		}

		
	}
	/**
	 * Driver method for the Edmond's Directed MST Algorithm  
	 * @param g
	 */
	public static void mstEdmondAlgorithm(Graph g) {

		Vertex root = g.verts.get(1);
		int vertCount=g.numNodes;
		//Initialize the graph by minimizing the incoming edges
		init(g, root);
		Vertex startNode = null;

		//System.out.println("----------Starting Edmond's Algorithm----------");
		int iter = 0;

		while ((startNode = BFS(g, root)) != null) {
			
			//find cycle from the unreachable node(start Node)
			ArrayList<Edge> path = findCycle(g, startNode);
			//shrink and create new superVertex
			createSuperVertex(g, path);
			iter++;
		}
		
		System.out.println(wmst);

		expand(g);
		ArrayList<Edge> edgeList = getMST(g, root);
		printMST(edgeList, vertCount);
		
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in = null;

		if (args.length > 0) {
			File inputFile = new File(args[0]);
			in = new Scanner(inputFile);
		} else {
			in = new Scanner(System.in);
		}
		Graph g = Graph.readGraph(in, true);
		long startTime=System.currentTimeMillis();
		mstEdmondAlgorithm(g);
		long endTime=System.currentTimeMillis();
		System.out.println("Total Time Taken(ms):"+(endTime-startTime));
	}

}
