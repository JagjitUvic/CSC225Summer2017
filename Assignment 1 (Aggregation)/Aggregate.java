/*
* Name: Carson Reid
* Date: 05/05/2017
* Filename: Aggregate.java
* Details: CSC225 Assignment 1 Aggregation
*
* This program will perform aggregation on a
* spreadsheet, and output the results to the
* command line. It is constructed to run in
* O(nlogn) time on n input rows, provided the
* width of rows is bounded.
*
* This program takes arguments as follows:
* 1: the aggregation function (one of 'count', 'sum', or 'avg')
* 2: the name of the column to aggregate
* 3: a .csv file with comma separated values
* 4+: any number of column names for grouping results
*
* There is little to no error checking done, all
* code assumes input is properly structured and
* arguments are valid.
*/

import java.util.*;
import java.io.*;

public class Aggregate {

	public Aggregate() {

	}
	
	public String[][] parseFile(String fileName){
		int rows = 0;
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(fileName));
		}
		catch(FileNotFoundException e){
			System.exit(1);
		}
		String inLine = null;
		ArrayList<String[]> bufferedInput = new ArrayList<String[]>();
		try{
			while((inLine = reader.readLine()) != null){
				boolean whiteLine = true;
				for(int i = 0; i < inLine.length(); i++){
					if(!(Character.isWhitespace(inLine.charAt(i)))){
						whiteLine = false;
					}
				}
				if(!whiteLine){
					String[] splitLine = inLine.split(","); 
					bufferedInput.add(splitLine);
					rows++;
				}
			}
		}	
		catch(IOException e){
			System.exit(1);
		}
		int cols = bufferedInput.get(0).length;
		String[][] arr = new String[rows][cols];
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				arr[i][j] = bufferedInput.get(i)[j];
			}
		}	
		return arr;
	}

	private int compRow(String[] in1, String[] in2){
		int rval = 0;
		for(int i = 0; i < in1.length-1; i++){
			if(!(in1[i].equals(in2[i]))){
				rval = 1;
			}
		}
		return rval;
	}

	public String[][] prune_columns(String[][] arrayIn, int[] prunedCols){
		int rows = arrayIn.length, cols = 0;
		cols = prunedCols.length;

		String[][] builder = new String[rows][cols];
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < prunedCols.length; j++){
				builder[i][j] = arrayIn[i][prunedCols[j]];		
			}			
		}
		return builder;
	}

	public String[][] aggregation(String op, String[][] arrayIn, String aggColumn){

		String[] nameRow = new String[arrayIn[0].length];
		for(int i = 0; i < arrayIn[0].length-1; i++){ //saves first row labels
			nameRow[i] = arrayIn[0][i];
		}
		nameRow[nameRow.length-1] = op;

		String[][] chopped = new String[arrayIn.length-1][arrayIn[0].length];
		for(int i = 1; i < arrayIn.length; i++){ //makes an array without first row labels in it
			for(int j = 0; j < arrayIn[0].length; j++){
				chopped[i-1][j] = arrayIn[i][j];
			}
		}

		//some custom comparator syntax taken from https://stackoverflow.com/questions/3699141/how-to-sort-an-array-of-ints-using-a-custom-comparator
		Arrays.sort(chopped, new Comparator<String[]>(){
			@Override
			public int compare(final String[] first, final String[] second){
				int rval = 0;
				for(int i = 0; i < first.length; i++){
					rval = first[i].compareTo(second[i]);
					if (rval != 0){
						return rval;
					}
					
				}
				return 0;
			}
		});

		String[][] aggregated = new String[arrayIn.length][arrayIn[0].length];		
		int sum = 0, count = 0, aggregatedIndex = 0;

		for(int i = 0; i < chopped.length; i++){
			count++;
			if(op.equals("sum") || op.equals("avg")){
				sum += Integer.parseInt(chopped[i][chopped[i].length-1]);
			}
			if(i == chopped.length-1 || (compRow(chopped[i], chopped[i+1]) != 0)){
				for(int j = 0; j < chopped[0].length-1; j++){
					aggregated[aggregatedIndex][j] = chopped[i][j];
				}				
				if(op.equals("count")){
					aggregated[aggregatedIndex][aggregated[0].length-1] = Integer.toString(count);
				}
				else if(op.equals("sum")){
					aggregated[aggregatedIndex][aggregated[0].length-1] = Integer.toString(sum);
				}
				else { //avg
					float avg = (float)sum/(float)count;
					String val = String.format("%.3f", avg);
					aggregated[aggregatedIndex][aggregated[0].length-1] = val;
				}				
				count = 0;
				sum = 0;
				aggregatedIndex++;
			} //if done
			
		}//done loop
		String[][] outArray = new String[aggregatedIndex+1][aggregated[0].length];
		for(int i = 0; i < nameRow.length-1; i++){
			outArray[0][i] = nameRow[i];
		}
		outArray[0][nameRow.length-1] = op+"("+aggColumn+")";

		for(int i = 0; i < aggregatedIndex; i++){
			for(int j = 0; j < aggregated[0].length; j++){
				outArray[i+1][j] = aggregated[i][j];
			}
		}		
		return outArray;
	}
				
	public static void main(String[] args) {
		
		String operation = args[0];
		String aggColumn = args[1];
		String fileName = args[2];

		Aggregate agg = new Aggregate();
		String[][] inArray = agg.parseFile(fileName);
		
		int[] pruneCols = new int[args.length-2];
		int pcIndex = 0;
		for(int i = 3; i < args.length; i++){
			for(int j = 0; j < inArray[0].length; j++){
				if(inArray[0][j].equals(args[i])){
					pruneCols[pcIndex] = j;
					pcIndex++;
					break;
				}
			}
		}
		for(int i = 0; i < inArray[0].length; i++){
			if(inArray[0][i].equals(aggColumn)){
				pruneCols[pruneCols.length-1] = i;
				break;
			}
		}

		String[][] pruned;
		pruned = agg.prune_columns(inArray, pruneCols);
		
		String[][] outArray;
		outArray = agg.aggregation(operation, pruned, aggColumn);

		for(int i = 0; i < outArray.length; i++){
			for(int j = 0; j < outArray[0].length; j++){
				if(j != (outArray[0].length-1)){
					System.out.print(outArray[i][j]+",");
				}
				else {
					System.out.print(outArray[i][j]);
				}
			}
			System.out.println();
		}
	}
}