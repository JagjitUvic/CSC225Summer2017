/*
* Name: Carson Reid
* Date: 05/05/2017
* Filename: Aggregate.java
* Details: CSC225 Assignment 2 Heaps
*
* Credit to Bill Bird, UVic Summer 2017 for
* all PUBLIC function prototypes (assignment
* skeleton).
*
* This program is an implementation of a
* priority queue in the form of a heap,
* used to 'simulate' CPU task scheduling.
* As nodes in the heap, Task objects are
* used. It is constructed so that all
* functions run in O(logn) time on n
* active tasks. Time complexities of each
* function is labeled above their implementation.
*
* This program does not take arguments.
*
* There is little to no error checking done, all
* code assumes input is properly structured and
* arguments are valid.
*/

import java.io.*;

public class NiceSimulator{

	public static final int SIMULATE_IDLE = -2;
	public static final int SIMULATE_NONE_FINISHED = -1;
	private int tasksRemaining;
	private int maxTasks;
	private int nextIndex;
	private Task[] idArray;
	private Task[] tHeap;
	
	/* Initializes NiceSimulator variables, then
	 creates an array-based Task heap tHeap as well as
	 an array of Tasks where they will be indexed by
	 their id for allowing constant-time Task location
	 in the heap
	*/
	public NiceSimulator(int maxTasks){
		tasksRemaining = 0;
		this.maxTasks = maxTasks;
		idArray = new Task[maxTasks];
		tHeap = new Task[maxTasks+1];
		nextIndex = 1;
	}
	
	/* O(1), checks a number and directly acesses an array
	*/
	public boolean taskValid(int taskID){
		if(taskID < 0 || taskID > maxTasks-1){
			return false;
		}
		else {
			if(idArray[taskID] != null) return true;
		}
		return false;
	}
	
	// O(1), directly accesses an array
	public int getPriority(int taskID){
		return idArray[taskID].priority;
	}
	
	// O(1), directly accesses an array
	public int getRemaining(int taskID){
		return idArray[taskID].timeRemaining;
	}
	
	/* Creates a task to put in the heap, puts it at the
	 next heap index as well as the idArray, then calls
	 reHeap once (O(logn)) and increments counters
	 Therefore O(logn)
	*/
	public void add(int taskID, int time_required){
		Task adder = new Task(taskID, time_required, nextIndex);
		tHeap[nextIndex] = adder;
		idArray[taskID] = adder;
		nextIndex++;
		reHeap(adder.loc/2);
		tasksRemaining++;
	}

	/* 'bubbles' up an element until it is in the right spot: O(logn)
	 since there is at most one constant time swap done at each 
	 level of the heap*/
	private void reHeap(int index){ //this function only gets called on indices that have at least one child
		int li = index*2, ri = (index*2)+1;
		if(index > 0){ //node will actually exist
			if(hasRC(index)){ //must have 2 children
				if(tHeap[li].priority < tHeap[ri].priority){ //left priority wins
					if(tHeap[li].priority < tHeap[index].priority || (tHeap[li].priority == tHeap[index].priority && tHeap[li].id < tHeap[index].id)){ //must be swapped
						swap(li, index);
						reHeap(index/2);
					}
				}
				else if(tHeap[li].priority > tHeap[ri].priority){//right priority wins
					if(tHeap[ri].priority < tHeap[index].priority || (tHeap[ri].priority == tHeap[index].priority && tHeap[ri].id < tHeap[index].id)){ //must be swapped
						swap(ri, index);
						reHeap(index/2);
					}
				}
				else if(tHeap[li].priority == tHeap[ri].priority){ // priority tie
					if(tHeap[li].id < tHeap[ri].id){ // left wins
						if(tHeap[li].priority < tHeap[index].priority || (tHeap[li].priority == tHeap[index].priority && tHeap[li].id < tHeap[index].id)){
							swap(li, index);
							reHeap(index/2);
						}
					}
					else { //right wins
						if(tHeap[ri].priority < tHeap[index].priority || (tHeap[ri].priority == tHeap[index].priority && tHeap[ri].id < tHeap[index].id)){
							swap(ri, index);
							reHeap(index/2);
						}
					}
				}
			} 
			else{
				if(tHeap[li].priority < tHeap[index].priority || (tHeap[li].priority == tHeap[index].priority && tHeap[li].id < tHeap[index].id)){ //need to swap
					swap(li, index);
					reHeap(index/2);
				}
			}
		}
	}

	/* swaps two 'nodes' in heap: O(1) since the
	 nodes are accessed directly in an array
	 */
	private void swap(int ind1, int ind2){
		Task temp = tHeap[ind1];
		tHeap[ind1] = tHeap[ind2];
		tHeap[ind2] = temp;

		tHeap[ind1].loc = ind1;
		tHeap[ind2].loc = ind2;
	}

	//returns true if 'node' at index has a right child: O(1)
	private boolean hasRC(int index){
		if((index*2)+1 >= nextIndex){
			return false;
		}
		return true;
	}
		
	/* Swaps the killed Task (O(1)) with the last entry in the heap,
	 then 'bubbles' it down until it is in the right location (O(logn))
	 and decrements counters appropriately
	*/
	public void kill(int taskID){
		nextIndex--;
		tasksRemaining--;
		int swapLoc = idArray[taskID].loc;
		swap(swapLoc, nextIndex);
		tHeap[nextIndex] = null;
		idArray[taskID] = null;
		reHeapDown(swapLoc);
		
	}

	/*'bubbles' down a node until it is in the right spot: O(logn)
		since there is at most one constant time swap done at each 
		level of the heap*/
	private void reHeapDown(int index){
		int li = index*2, ri = (index*2)+1;
		if(hasRC(index)){ // has two children
			if(tHeap[li].priority < tHeap[ri].priority){//left child wins
				if(tHeap[li].priority < tHeap[index].priority || (tHeap[li].priority == tHeap[index].priority && tHeap[li].id < tHeap[index].id)){ //needs to be swapped
					swap(li, index);
					reHeapDown(li);
				}
			}
			else if(tHeap[li].priority > tHeap[ri].priority){//right child wins
				if(tHeap[ri].priority < tHeap[index].priority || (tHeap[ri].priority == tHeap[index].priority && tHeap[ri].id < tHeap[index].id)){//needs to be swapped
					swap(ri, index);
					reHeapDown(ri);
				}
			}
			else if(tHeap[li].priority == tHeap[ri].priority){//tie
				if(tHeap[li].id < tHeap[ri].id){//left child wins based on id
					if(tHeap[li].priority < tHeap[index].priority || (tHeap[li].priority == tHeap[index].priority && tHeap[li].id < tHeap[index].id)){ //actually need to swap
						swap(li, index);
						reHeapDown(li);
					}	
				}
				else { //right child wins based on id
					if(tHeap[ri].priority < tHeap[index].priority || (tHeap[ri].priority == tHeap[index].priority && tHeap[ri].id < tHeap[index].id)){ //actually need to swap
						swap(ri, index);
						reHeapDown(ri);
					}	
				}
			}
		}
		else if(li < nextIndex){ // has only left child
			if(tHeap[index].priority > tHeap[li].priority || (tHeap[li].priority == tHeap[index].priority && tHeap[li].id < tHeap[index].id)){ //need to swap
				swap(index, li);
				//don't need to reheap, must be at bottom
			}
		}
	}	
	
	/* Changes the node's priority, then decides if it must be moved
	   up or down in the heap (O(1)) and calls either reHeap (O(logn))
	   or reHeapDown (O(logn))
	*/
	public void renice(int taskID, int new_priority){
		idArray[taskID].priority = new_priority;
		int taskLoc = idArray[taskID].loc;
		if(taskLoc/2 > 0){ //not the root, must have a parent
			if(tHeap[taskLoc/2].priority >= tHeap[taskLoc].priority){
				reHeap(taskLoc/2);
			}
			else { //doesn't go up, might go down
				reHeapDown(taskLoc);
			}
		}
		else { //is root
			reHeapDown(taskLoc);
		}
	}
	
	/* Checks a number (O(1)), then calls a O(1) function on 
	 an element directly accessed in an array (O(1)), then 
	 possibly calls kill if a process finished (O(logn))
	*/
	public int simulate(){
		int returnVal = SIMULATE_NONE_FINISHED;
		if(tasksRemaining == 0){
			return SIMULATE_IDLE;
		}
		else {
			int tTimeRemaining = tHeap[1].tick();
			
			if(tTimeRemaining <= 0){
				returnVal = tHeap[1].id;
				kill(tHeap[1].id);
			}
		}
		return returnVal;
	}
}