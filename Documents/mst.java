package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.PartialTree;
import structures.Vertex;
import structures.MinHeap;


/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++;
    }

    /**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
	
		/* COMPLETE THIS METHOD */
		
		if(graph==null) {
			return null;
		}
		
		PartialTreeList list = new PartialTreeList();
		int i = 0; 
		while(i<graph.vertices.length) {
			
			Vertex v = graph.vertices[i];
			
			PartialTree T = new PartialTree(v);
			
			MinHeap<Arc> P = new MinHeap<Arc>();
			
			while(v.neighbors!=null){
				Arc t= new Arc(v, v.neighbors.vertex, v.neighbors.weight);
				P.insert(t);
				v.neighbors=v.neighbors.next;
			}
			T.getArcs().merge(P);
			list.append(T);
			i++; 
		}
		
		return list;
		
	}
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList list) {
		
		/* COMPLETE THIS METHOD */

	ArrayList<Arc> arr= new ArrayList<Arc>();
		
	
		if(list==null){
			return arr;
		}
	
		if(list.size()==1 ){
			Arc a = list.remove().getArcs().getMin();
			arr.add(a);
		}
		
		
		while(list.size() > 1){
			
			PartialTree PTX =list.remove(); 
			
			MinHeap<Arc> PQX = PTX.getArcs(); 
			
			if(PQX.isEmpty()){
				return null;
			}
			
			Arc a = PQX.getMin(); 
			
			Vertex v2= a.getv2();
				
			Vertex temp=v2;
		
			do{
				Vertex v = PTX.getRoot();
				if(temp==v){
					PQX.deleteMin();
					a=PQX.getMin();
					v2=a.getv2();
					temp=v2;
					continue;
				}
			
				if(temp==temp.parent){
					if(v!=v2) {
						break;
					}
				}
				
				temp=temp.parent;
			}while(PQX.size() > 2);
			
			PartialTree PTY = list.removeTreeContaining(v2); 
			arr.add(a);
			PTX.merge(PTY);
			
			list.append(PTX);
		}
		

		return arr;
	}
	
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = rear.next.tree;
    	if (rear.next == rear) {
    		rear = null;
    	} else {
    		rear.next = rear.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException {
    	
    	/* COMPLETE THIS METHOD */
    	Vertex v = vertex; 
		Iterator<PartialTree> it = iterator();
		if(!it.hasNext()) {
			return null; 
		}else{
			Node prev = rear;
			Node ptr = rear.next;
			int is = size;
			PartialTree t = null;
			while (it.hasNext()){
				t = it.next();
				Vertex a = t.getRoot();
				if (a == v.getRoot() && ptr ==rear.next){
						prev.next = prev.next.next;
						size--;
						break;
				}else if(a==v.getRoot() && ptr == rear){
						prev.next = rear.next;
						rear = prev;
						size--;
				}else if(a==v.getRoot()){
						prev.next = ptr.next;
						size--;
						break;
				}else{
					prev = ptr;
					ptr = ptr.next;
				}
			}
			if (size == is){
				throw new NoSuchElementException();
			}
		return t;
		}
		
    }

    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}