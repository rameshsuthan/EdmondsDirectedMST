/**
 * Class that represents an arc in a Graph
 *
 */
public class Edge {
    public Vertex From; // head vertex
    public Vertex To; // tail vertex
    public int Weight;// weight of the arc
    public int currWeight;//current weight
    public Edge superEdge;
    public boolean isActive;

    /**
     * Constructor for Edge
     * 
     * @param u
     *            : Vertex - The head of the arc
     * @param v
     *            : Vertex - The tail of the arc
     * @param w
     *            : int - The weight associated with the arc
     */
    Edge(Vertex u, Vertex v, int w) {
	From = u;
	To = v;
	Weight = w;
	currWeight=Weight;
	isActive=true;
    }

    /**
     * Method to find the other end end of the arc given a vertex reference
     * 
     * @param u
     *            : Vertex
     * @return
     */
    public Vertex otherEnd(Vertex u) {
	// if the vertex u is the head of the arc, then return the tail else return the head
	if (From == u) {
	    return To;
	} else {
	    return From;
	}
    }

    /**
     * Method to represent the edge in the form (x,y) where x is the head of
     * the arc and y is the tail of the arc
     */
    public String toString() {
	return "(" + From + "," + To +"" +")";
	//return "(" + From + "," + To +")=>" + Weight+"#"+ currWeight +" "+isActive;
    }
    
    @Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Edge) {
			Edge otherEdge = (Edge) obj;
			if ( this==otherEdge ||(this.From.name==otherEdge.From.name && this.To.name==otherEdge.To.name)) {
				return true;
			}

		}
		return false;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		int hashCode=this.From.name*this.To.name;
		return hashCode;
	}

    
}