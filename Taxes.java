/***************************************************************  
*  file: Taxes.java  
*  author: Bryan Orellana de la Cruz
*  class: CS 1400 – Programming and Problem Solving  
*  
*  assignment: Program 4
*  date last modified: 09/02/2022
*  
*  purpose: This program calculates income tax by getting input from a file
*           containing wages, taxable interest, unemployment compensation,
*           status, and taxes withheld. It prints out the calculations on
*           another file named "TaxOutput.txt" following a format.
*  
****************************************************************/  


import java.util.Scanner;
import java.io.*;

public class Taxes{
	
	//Static variable that can be accessed by other methods.
	private static int count = 0;
	
	//method: main
	//purpose: This method creates a file object from user input and
	//passes the file object to the processFile method.
	public static void main(String[] args) throws IOException{
		//Ask the user for a file that will have input.
		System.out.println("Enter the name of the file: ");
		Scanner scnr = new Scanner(System.in);
		String fileName = scnr.nextLine();
	
		//Create a file object with the given file name.
		File file = new File(fileName);
		
		//If file doesn't exist, ask the user for a file name until it gets one that exists.
		while(file.exists() == false){
			System.out.println("Could not find file. Enter the name of the file: ");
			fileName = scnr.nextLine();
			file = new File(fileName);
		}
		
		//Count how many lines.
		Scanner inputFile = new Scanner(file);
		while(inputFile.hasNext()){
			inputFile.nextLine();
			count++;
		}
		inputFile.close();
		
		//Pass the file object to processFile method.
		processFile(file);
	}
	//method: processFile
	//Purpose: This method processes a file and creates a 2D array that holds info from file.
	//Calls up other methods to get calculations and assigns them to another 2D array.
	//Calls printToFile to print the calculations.
	public static void processFile(File file) throws IOException{
		
		//Create a new Scanner.
		Scanner inputFile = new Scanner(file);
		
		//Creates the 2D array.
		int[][] taxArray = new int[count][5];
		
		//Assigns values from file to 2D array.
		for (int i = 0; i < count; ++i){
			for (int j = 0; j < 5; ++j){
				taxArray[i][j] = inputFile.nextInt();
			}
		}
		
		inputFile.close();
		
		//New 2D array that is assigned the calculations
		int[][] fileArray = new int[count][6];
		for (int i = 0; i < count; ++i){
			//Calls calcAGI method and is assigned return value (AGI).
			fileArray[i][0] = calcAGI(taxArray[i][0], taxArray[i][1], taxArray[i][2]);
			//Calls getDeduction method and is assigned return value (deduction).
			fileArray[i][1] = getDeduction(taxArray[i][3]);
			//Calls calcTaxable method and is assigned return value (taxable income).
			fileArray[i][2] = calcTaxable(fileArray[i][0], fileArray[i][1]);
			//Calls calcTax method and is assigned return value (federal tax).
			fileArray[i][3] = calcTax(taxArray[i][3], fileArray[i][2]);
			//Calls calcTaxDue method and assigns return value (tax due).
			fileArray[i][4] = calcTaxDue(fileArray[i][3], taxArray[i][4]);
			//Assigns household number based on row number in array.
			fileArray[i][5] = i;
		}
		
		/*
		What columns in taxArray contain for each row
		Column: 0           1               2          3        4
		Info: wages, taxable interest, unemployment, status, withheld
		
		What columns in fileArray contain for each row
		Column: 0      1             2             3          4         5
		Info: AGI, deduction, taxable income, federal tax, tax due, household #
		*/
		
		//Calls printToFile method for each household (row).
		for (int i = 0; i < count; ++i){
			printToFile((fileArray[i][5] + 1), fileArray[i][0], fileArray[i][1], fileArray[i][2],
			fileArray[i][3], fileArray[i][4]);
		}
		
	}
	//method: calcAGI
	//purpose: This method calculates AGI (sum of wages, interest, unemployment)
	//and returns the sum.
	public static int calcAGI(int wages, int interest, int unemployment){
		wages = Math.abs(wages);
		interest = Math.abs(interest);
		unemployment = Math.abs(unemployment);
		int agi = wages + interest + unemployment;
		return agi;
	}
	
	//method: getDeduction
	//purpose: This method calculates the deduction based on status
	//and returns the deduction.
	public static int getDeduction(int status){
		//Deduction based on status: 0 = 6000, 1 = 12000, 2 = 24000, other = 6000.
		int deduction;
		if (status == 0){
			deduction = 6000;
		}
		else if (status == 1){
			deduction = 12000;
		}
		else if (status == 2){
			deduction = 24000;
		}
		else{
			deduction = 6000;
		}
		return deduction;
	}
	
	//method: calcTaxable
	//purpose: This method calculates taxable income and returns the value.
	public static int calcTaxable(int agi, int deduction){
		//Taxable income = AGI - deduction, return 0 if negative.
		int taxableIncome = agi - deduction;
		if(taxableIncome < 0){
			return 0;
		}
		else{
			return taxableIncome;
		}
	}
	
	//method: calcTax
	//purpose: This method calculates federal tax based on the table
	//and returns the calculated value.
	public static int calcTax(int status, int taxIncome){
		double tax;
		//Calculation based on married filers.
		if(status == 2){
			if((taxIncome >= 0) && (taxIncome <= 20000)){
				tax = taxIncome * 0.10;
			}
			else if((taxIncome >= 20001) && (taxIncome <= 80000)){
				tax = 2000 + ((taxIncome - 20000) * 0.12);
			}
			else{
				tax = 9200 + ((taxIncome - 80000) * 0.22);
			}
		}
		//Calculation based on dependent or independent filers.
		else{
			if ((taxIncome >= 0) && (taxIncome <= 10000)){
				tax = taxIncome * 0.10;
			}
			else if((taxIncome >= 10001) && (taxIncome <= 40000)){
				tax = 1000.0 + ((taxIncome - 10000) * 0.12);
			}
			else if((taxIncome >= 40001) && (taxIncome <= 85000)){
				tax = 4600.0 + ((taxIncome - 40000) * 0.22);
			}
			else{
				tax = 14500 + ((taxIncome - 85000) * 0.24);
			}
		}
		//Rounds calculation and returns as an int.
		int finalTax = (int) Math.round(tax);
		return finalTax;
	}
	
	//method: calcTaxDue
	//purpose: This method calculates the tax due and returns the value.
	public static int calcTaxDue(int calcTax, int withheld){
		if (withheld < 0){
			withheld = 0;
		}
		int taxDue;
		taxDue = calcTax - withheld;
		return taxDue;
	}
	
	//method: printToFile
	//purpose: This method prints household number, AGI, deduction, taxable income,
	//tax amount, and taxes due to a file named "TaxOutput.txt" in a formatted style.
	public static void printToFile(int title, int agi, int deduction, int income, int amount, int due) throws IOException{
		if (title == 1){
			PrintWriter outputFile = new PrintWriter("TaxOutput.txt");
			outputFile.println("Household " + title + ":");
			outputFile.printf("AGI: $%,d\n", agi);
			outputFile.printf("Deduction: $%,d\n", deduction);
			outputFile.printf("Taxable income: $%,d\n", income);
			outputFile.printf("Federal tax: $%,d\n", amount);
			outputFile.printf("Tax due: $%,d\n", due);
			if (title != count){
				outputFile.println();
			}
			outputFile.close();
		}
		else{
			FileWriter fwriter = new FileWriter("TaxOutput.txt", true);
			PrintWriter outputFile = new PrintWriter(fwriter);
			outputFile.println("Household " + title + ":");
			outputFile.printf("AGI: $%,d\n", agi);
			outputFile.printf("Deduction: $%,d\n", deduction);
			outputFile.printf("Taxable income: $%,d\n", income);
			outputFile.printf("Federal tax: $%,d\n", amount);
			outputFile.printf("Tax due: $%,d\n", due);
			if (title != count){
				outputFile.println();
			}
			fwriter.close();
			outputFile.close();
		}
	}
}