/*
* Name: Carson Reid
* Date: 22/06/2017
* Filename: Task.java
* Details: CSC225 Assignment 2 Helper class
* Task, to be used as nodes in Task heap
*
* Note: use of public variables was to
* help ease of use in implementing the
* assignment, encapsulation was not
* required.
*/
public class Task {

	public int id;
	public int priority;
	public int timeRemaining;
	public int loc;

	/* Sets id and required time for 
	 a new Task node (O(1))
	*/
	public Task(int i, int timeReq){
		id = i;
		timeRemaining = timeReq;
		priority = 0;
		loc = 0;
	}

	/* Same as above, sets heap
	 index location as well (O(1))
	*/
	public Task(int i, int timeReq, int location){
		id = i;
		timeRemaining = timeReq;
		priority = 0;
		loc = location;
	}

	/* Decrements time remaining and returns
	 how much time is remaining for this Task node (O(1))
	*/
	public int tick(){
		timeRemaining--;
		return timeRemaining;
	}	
}