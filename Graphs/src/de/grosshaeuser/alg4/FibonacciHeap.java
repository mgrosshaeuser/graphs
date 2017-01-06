package de.grosshaeuser.alg4;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import de.grosshaeuser.alg4.graph.Node;

public final class FibonacciHeap {

    private Node min;
    private int size;

    
    public FibonacciHeap(){
    	min = null;
    	size = 0;
    }
    
    
    
    public void insert(Node node) {
    	if (node == null){
    		throw new UnsupportedOperationException("Can't insert null.");
    	}
    	
    	if (min == null){
    		min = node;
    	} else {
            Node minNext = min.getNext();
            min.setNext(node.getNext());
            min.getNext().setPrev(min);
            node.setNext(minNext);
            node.getNext().setPrev(node);
        
            if (min.getKey() >= node.getKey()){
            	min = node;
            }
        }
        
        size++;
    }



    public void delete(Node node) {
	    decreaseKey(node, Double.NEGATIVE_INFINITY);
	    extractMin();
	}



	public Node getMin() {
        if (isEmpty())
            throw new NoSuchElementException("No nodes in heap.");
        return min;
    }


    
    public boolean isEmpty() {
        return min == null;
    }



    public int size() {
        return size;
    }


    
    public static FibonacciHeap uniteHeaps(FibonacciHeap one, FibonacciHeap two) {
    	if (one.isEmpty()   &&   two.isEmpty()){
    		throw new UnsupportedOperationException("Can't merge two empty heaps");
    	} else if (one.isEmpty()   &&   ! two.isEmpty()){
    		return two;
    	} else if ( ! one.isEmpty()   &&   two.isEmpty() ){
    		return one;
    	} else {
    		 FibonacciHeap union = new FibonacciHeap();
    		
             Node oneNext = one.min.getNext();
             one.min.setNext(two.min.getNext());
             one.min.getNext().setPrev(one.min);
             two.min.setNext(oneNext);
             two.min.getNext().setPrev(two.min);
             
             if (one.min.getKey() < two.min.getKey()){
            	 union.min = one.min;
             } else {
            	 union.min = two.min;
             }
             
             union.size = one.size + two.size;

             one.size = two.size = 0;
             one.min  = two.min = null;
             
             return union;
    	}
    }


    
    public Node extractMin() {
        if (isEmpty()){
            throw new NoSuchElementException("No nodes in heap.");
        }
        
        size--;

        Node tempMin = min;

        if (min.getNext() == min) { 
            min = null;
        }
        else {
        	min.getPrev().setNext(min.getNext());
            min.getNext().setPrev(min.getPrev());
            min = min.getNext();
        }

        if (tempMin.getChild() != null) {
            Node tempChild = tempMin.getChild();
            do {
                tempChild.setParent(null);
                tempChild = tempChild.getNext();
            } while (tempChild != tempMin.getChild());
        }
        
        if (min == null && tempMin.getChild() == null) { 
            min = null;
        } else if (min == null && tempMin.getChild() != null) {
            min =  tempMin.getChild();
        } else if (min != null && tempMin.getChild() == null) {
        	
        } else {
            Node oneNext = min.getNext();
            min.setNext(tempMin.getChild().getNext());
            min.getNext().setPrev(min);
            tempMin.getChild().setNext(oneNext);
            tempMin.getChild().getNext().setPrev(tempMin.getChild());
        
            if (min.getKey() >= tempMin.getChild().getKey()){
            	min = tempMin.getChild();
            }
        }
        
        if (min == null){
        	return tempMin;
        }

        ArrayList<Node> tempList = new ArrayList<>();
        ArrayList<Node> rootList = new ArrayList<>();

        Node runner = min;
        do {
        	rootList.add(runner);
        	runner = runner.getNext();
        } while (runner != min);
        
        for (Node curr: rootList) {
            while (true) {
                while (curr.getDegree() >= tempList.size())
                    tempList.add(null);

                if (tempList.get(curr.getDegree()) == null) {
                    tempList.set(curr.getDegree(), curr);
                    break;
                }

                Node other = tempList.get(curr.getDegree());
                tempList.set(curr.getDegree(), null);

                Node min = (other.getKey() < curr.getKey())? other : curr;
                Node max = (other.getKey() < curr.getKey())? curr  : other;

                max.getNext().setPrev(max.getPrev());
                max.getPrev().setNext(max.getNext());

                max.setNext(max);
                max.setPrev(max);
                           
                if (min.getChild() == null   &&   max != null){
                	min.setChild(max);
                } else if (min.getChild() != null   &&   max == null){
                	
                } else {
                    Node oneNext = min.getChild().getNext();
	                min.getChild().setNext(max.getNext());
	                min.getChild().getNext().setPrev(min.getChild());
	                max.setNext(oneNext);
	                max.getNext().setPrev(max);
	                
	                if ( min.getChild().getKey() < max.getKey()){
	                	min.setChild(min.getChild());
	                } else {
	                	min.setChild(max);
	                }
                }

                max.setParent(min);
                max.setMarked(false);

                min.incDegree();

                curr = min;
            }
            if (curr.getKey() <= min.getKey()) min = curr;
        }
        return tempMin;
    }

    
    
    public void decreaseKey(Node node, double newKey) {
    	if (newKey > node.getKey()){
	            throw new IllegalArgumentException("Key value isn't allowed to go UP");
		}

		node.setKey(newKey);
	    
		if (node.getParent() != null   &&   (node.getKey() <= node.getParent().getKey()) )
	        cutNode(node);

		if (node.getKey() <= min.getKey())
	        min = node;
	}


    
    private void cutNode(Node node) {
        node.setMarked(false);

        if (node.getParent() == null){
        	return;
        }

        if (node.getNext() != node) {
        	node.getNext().setPrev(node.getPrev());
            node.getPrev().setNext(node.getNext());
        }

        if (node.getParent().getChild() == node) {
            if (node.getNext() != node) {
                node.getParent().setChild(node.getNext());
            } else {
                node.getParent().setChild(null);
            }
        }

        node.getParent().decDegree();

        node.setPrev(node);
        node.setNext(node);
        
   
        Node minNext = min.getNext();
        min.setNext(node.getNext());
        min.getNext().setPrev(min);
        node.setNext(minNext);
        node.getNext().setPrev(node);
        
        if (min.getKey() >= node.getKey()){
        	min = node;
        }

        if (node.getParent().isMarked()){
            cutNode(node.getParent());
        } else {
        	node.getParent().setMarked(true);
        }

        node.setParent(null);
    }
}