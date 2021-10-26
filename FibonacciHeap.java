import java.util.stream.IntStream;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap
{
	
	private HeapNode min;
	private int marked_nodes=0;
	public static int total_cuts=0;
	public static int total_links=0;
	private int size=0;

   /**
    * public boolean empty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean empty()
    {
    	if (min==null) return true;
    	return false;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains 
    * the given key, and inserts it into the heap. 
    */
    public HeapNode insert(int key)
    {    
    	HeapNode new_node = new HeapNode(key,null,null,null,null);
    	if(empty()) {
    		this.min=new_node;
    		new_node.next=new_node.prev=new_node;
    	}
    	else {
    		new_node.next=this.min;
    		new_node.prev=this.min.prev;
    		this.min.prev.next=new_node;
    		this.min.prev=new_node;
    		if(min.getKey()>new_node.getKey()) this.min=new_node;
    	}
    	this.size+=1;
    	return new_node;
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
  public void deleteMin()
    {
     	HeapNode Min=this.min;
     	if (Min!=null) {
     		addChildrenToRoots();
     		// remove the min from the root list 
     		Min.prev.next=Min.next;
     		Min.next.prev=Min.prev;
            if (Min == Min.next) {
                min = null;
            } else {
                this.min = Min.next;
                this.Consolidation();
            }
            // reduce size by one
            this.size--;
            Min.prev = null;
            Min.next = null;
            Min.rank = 0;
            Min.child = null;
            Min.mark = false;
     	}
    } 
    
    /**
     * private void addChildrenToRoots()
     *
     * adds the children of the minimum to the root list
     *
     */
   private void addChildrenToRoots() {
	   int numChildren=min.rank;
	   HeapNode child=min.child;
	   HeapNode tmpNext;
	   // for the children of Min, do:
	   while (numChildren >0) {
			tmpNext=child.getNext();
			//remove child from children list
			child.prev.next=child.next;
			child.next.prev=child.prev;
			//add child to root list
			child.prev=min;
			child.next=min.next;
			min.next=child;
			
			child.next.prev=child;
			// set the parent to null
           child.parent = null;
           child = tmpNext;
           numChildren--;	
		}
	
}

/**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)   // should re-check 
    {
    	if(heap2.empty() || this.empty()) return ;
    	this.marked_nodes= this.marked_nodes+heap2.marked_nodes;
    	this.size=this.size+heap2.size;
    	this.min.next.prev=heap2.min;
    	heap2.min.next=this.min.next;
    	heap2.min.prev=this.min;
    	this.min.next=heap2.min;
    	this.min=this.min.getKey()<heap2.min.getKey() ? this.min : heap2.min; // update min
    	
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return size; // should be replaced by student code
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    int[] null_tree_arr = new int[1];
    if(this.empty()) return null_tree_arr; // if Heap is empty
    
	int MaxRank = this.min.getRank();
	HeapNode current = this.min;
	while(current.next!=this.min) { // determine the length of the array by the max rank
		if(current.next.getRank()>MaxRank) {
			MaxRank=current.next.getRank();
		}
		current=current.getNext();
	}
	
	int[] arr = new int[MaxRank+1];
	current = this.min;
	arr[min.getRank()]+=1;
	while(current.getNext()!=this.min) {
		arr[current.getNext().getRank()]+=1;
		current=current.getNext();
	}
    return arr;
    
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	if (x.mark) marked_nodes-=1;
    	decreaseKey(x,Integer.MIN_VALUE);
    	this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.key-=delta;

        HeapNode y = x.parent;

        if ((y != null) && (x.key < y.key)) {
            // tester cut(x, y);
            cascading_cut(x,y);
        }

        if (x.key < min.key) {
            min = x;
        }
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	int[] C_arr = this.countersRep();
    	int arr_sum = IntStream.of(C_arr).sum();
    	return(arr_sum+(2*marked_nodes));
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return total_links;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return total_cuts;
    }
    /**
     * public void cut(HeapNode x,HeapNode y)
     *
     * cuts x from its parent (y)  
     */
    public void cut(HeapNode x,HeapNode y) {
    	total_cuts++;
    	x.parent=null;
    	if (x.mark) marked_nodes--;
    	x.mark=false;
    	y.rank=y.rank-1;
    	if (x.next==x) y.child=null;
    	else {
    		y.child=x.next;
    		x.prev.next=x.next;
    		x.next.prev=x.prev;
    	}
    	x.prev = min;
        x.next = min.next;
        min.next = x;
        x.next.prev = x;
    }
    /**
     * public void cascading_cut(HeapNode x,HeapNode y)
     *
     * cuts x from its parent (y) and then does the same for y recursively 
     */
    public void cascading_cut(HeapNode x,HeapNode y){
    	cut(x,y);
    	if (y.parent!=null) {
    		if (!y.mark) {
    			y.mark=true;
    			marked_nodes++;
    		}
    		else cascading_cut(y,y.parent);
    	}
    }
    /**
     * public void Consolidation() 
     *
     * goes over the root list and links those with equal ranks 
     */
    public void Consolidation() {
    	
    	HeapNode[] arr = initBuckets();
        int  numRoots =countRoots();
        actualConsolidation(arr,numRoots);
        //empty the root list
        this.min = null;
        //rebuild root list
        for( int i = 0; i < arr.length; i++ ) {
        	HeapNode node=arr[i];
            if( node != null ) {
                if( this.min != null ) {
                    node.prev.next = node.next;
                    node.next.prev =node.prev;
                    node.prev = min;
                    node.next = min.next;
                    this.min.next = node;
                    node.next.prev = node;
                    if( node.key < this.min.key ) {
                        this.min = node;
                    }
                }
                else {
                    this.min = node;
                }
            }
        }
    }
    /**
     * public void actualConsolidation() 
     *
     * does the actual consolidation
     */
    public void actualConsolidation(HeapNode[] arr,int numRoots) {
    	HeapNode x = this.min;
        // for each root do this:
        while( numRoots > 0 ) {
        	HeapNode next = x.next;
            int r = x.rank;
            //if there is already a root with same rank
            while( arr[r] != null ) {
            	HeapNode y = arr[r];
            	if (x.key>y.key) {
            		HeapNode tmp=y;
            		y=x;
            		x=tmp;
            	}
            	Link(x,y);
                arr[r] = null;
                r++;
                
            }
            //if there isn't a root with this rank up until now
            arr[r] = x;
            x = next;
            numRoots--;
        }
		
	}
    /**
     * rivate int countRoots() 
     *
     * returns the number of roots in heap
     */
	private int countRoots() {
    	int  numRoots = 0;
    	HeapNode x = this.min;
        //count number of roots
        if( x != null ) {
            numRoots++;
            x = x.next;
            while(x !=this.min ) {
                numRoots++;
                x = x.next;
            }
        }
        return numRoots;
	}
	/**
     * private HeapNode[] initBuckets() 
     *
     * returns a HeapNode[] array initialized to null
     */
	private HeapNode[] initBuckets() {
    	//size+1 upper bound on number of roots
    	HeapNode[] arr = new HeapNode[size];
    	//initialize
        for( int i = 0; i < arr.length; i++ ) {
            arr[i] = null;
        }
        return arr;
	}

	/**
     * public void Link(HeapNode x,HeapNode y) 
     *
     * gets as input two nodes of the same rank, and generates 
     * a tree of rank bigger by one, by hanging the tree which has larger
     * value in its root on the tree which has smaller in its root (x)
     */
    public void Link(HeapNode x,HeapNode y) {
    	this.total_links++;
    	
    	//remove y from root list
    	y.prev.next = y.next;
        y.next.prev = y.prev;

        // make y a child of x
        y.parent = x;

        if (x.child == null) {
            x.child = y.next = y.prev =y;
        } else {
            y.prev = x.child;
            y.next = x.child.next;
            x.child.next = y;
            y.next.prev = y;
        }

        // increase rank
        x.rank++;
        y.mark = false;
        
    	
    }
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{
    	
    public boolean mark=false;
    public HeapNode parent;
    public HeapNode next;
    public HeapNode prev;
    public HeapNode child;
    public int rank;
	public int key;
	
	/**
	 * A key restricted constructor of a node in the 
	 * Fibonacci heap (everything besides key equals the default value of the parameter).
	 * 
	 */
	public HeapNode(int key) {
		this.key=key;
	}
	/**
	 * 
	 * @param key
	 * @param prev
	 * @param parent
	 * @param next
	 * @param child
	 * 
	 * A specific constructor of a node in the Fibonacci heap .
	 */
  	public HeapNode(int key,HeapNode prev,HeapNode parent,HeapNode next,HeapNode child) {
	    this.key = key;
	    this.prev=prev;
	    this.parent=parent;
	    this.next = next;
	    this.child=child;
	    this.rank=0;
      }
  	/**
  	 * 
  	 * @return the key of the node
  	 */
	public int getKey() {
	    return this.key;
      }
	/**
	 * 
	 * @return the rank of the node.
	 */
  	public int getRank() {
  		return this.rank;
  	}
  	/**
  	 * 
  	 * @return true if the node is marked, otherwise false.
  	 */
  	public boolean getMark() {
  		return this.mark;
  	}
  	/**
  	 * 
  	 * @return the next node of the node.
  	 */
  	public HeapNode getNext() {
  		return this.next;
  	}
  	/**
  	 * 
  	 * @return the parent of the node.
  	 */
  	public HeapNode getParent() {
  		return this.parent;
  	}
  	/**
  	 * 
  	 * @return the previous node of the node.
  	 */
  	public HeapNode getPrev() {
  		return this.prev;
  	}
  	/**
  	 * 
  	 * @return the child of the node.
  	 */
  	public HeapNode getChild() {
  		return this.child;
  	}
  	
    }
}
