package entry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

public class Main {
	
	public static double INCREMENT = 1E-4;
	public static double negativeInfinity = -500;
	
	public static void main(String[] args) throws IOException { 
		
		//This is our function to be used in the integral
		Function func = new Function() {
			@Override
			public double f(double x) {
				return Math.pow(Math.E,(-0.5)*Math.pow(x, 2));
			}
		};
		
		//--Setup--
		DecimalFormat df = new DecimalFormat("#.#########");
		DecimalFormat df2 = new DecimalFormat("#.####");
		double min = 0;
		double max = 9;
		double increment = 0.01;
		double roundFactor = 100.0;
		double roundFactorPercent = 1000.0;
		boolean mainMenu = true;
		Scanner mainSc = new Scanner(System.in);
		
		System.out.println("----");
		System.out.println("This program is based on work done by (Finney and Stevens 1948) \n");
		System.out.println("Citation: Finney, D. J., and W. L. Stevens. “A Table for the Calculation of Working Probits and Weights in Probit Analysis.” Biometrika, vol. 35, no. 1/2, 1948, pp. 191–201. JSTOR, www.jstor.org/stable/2332639. Accessed 22 Jan. 2020. \n");
		System.out.println("TO QUIT OR GO BACK IN THE PROGRAM TYPE IN: quit \n");
		
		while(mainMenu) {
			System.out.println("available functions: \n-->create table(WARNING: will delete old table & takes a long time, about 1 hour) \n-->find percentages(uses the formula from (Finney and Stevens 1948) to get a probit) \n-->find probits(gets a probit from a percentage using the table. WARNING: table must be made first)");
			
			String userInput = mainSc.nextLine();
			
			if(userInput.equals("create table")) {
				CreateTable(min, max, increment, roundFactor, roundFactorPercent, df, df2, func);
			} else if(userInput.equals("find percentages")) {
				UserProgram(df, df2, func, mainSc, roundFactorPercent);
			} else if(userInput.equals("find probits")) {
				FindProbits(mainSc);
			} else if(userInput.equals("quit")) {
				mainMenu = false;
			} else {
				System.out.println("Invalid Input \n");
			}
		}
		
		mainSc.close();

			
	}
	
	public static void FindProbits(Scanner sc) throws IOException {
		Boolean run = true;
		Scanner read = new Scanner(new File("data.txt"));
		String rawData = "";
		
		while(read.hasNextLine()) {
			String readLine = read.nextLine();
			//System.out.println(readLine);
			rawData += readLine;
		}
		read.close();
		
		String[] dataArray = rawData.split(",");
		double[] keys = new double[dataArray.length];
		double[] values = new double[dataArray.length];
		for(int i = 0; i < dataArray.length; i++) {
			String value = dataArray[i].substring(0, dataArray[i].indexOf(":"));
			String key = dataArray[i].substring(dataArray[i].indexOf(":")+1);
			keys[i] = Double.valueOf(key);
			values[i] = Double.valueOf(value);
		}

	    while(run) {
	    	System.out.println("Please enter in percentages in the format %,%,%,... ");
	    	String input = sc.nextLine();
	    	String[] inputArray = input.split(",");
	    	for(int k = 0; k < inputArray.length; k++) {
	    		try {
		    		double find = Double.valueOf(inputArray[k]);
		    		int check = searchArray(keys, find);
		    		if(check > 0 && check < keys.length) {
		    			System.out.println("Found: " + values[check] + " for " + find + "%");
		    		} else {
		    			System.out.println("Could not find probit for that percentage");
		    		}
		    	} catch(NumberFormatException e) {
		    		if(input.equals("quit")) {
		    			run = false;
		    		} else {
		    			System.out.println("Invalid Input");
		    		}
		    	}
	    	}
	    	
	    	System.out.println("\n");
	    }
	}
	
	public static int searchArray(double[] keys, double find) {
		double[] minVals = new double[keys.length];
		for(int i = 0; i < keys.length; i++) {
			double look = keys[i];
			minVals[i] = Math.abs(look - find);
		}
		double min = minVals[0];
		int minLocation = -1;
		for(int i = 0; i < minVals.length; i++) {
			if(minVals[i] < min) {
				min = minVals[i];
				minLocation = i;
			}
		}
		return minLocation;
	}
	
	public static void CreateTable(double min, double max, double increment, double roundFactor, double roundFactorPercent, DecimalFormat df, DecimalFormat df2, Function func) throws IOException {
		File myFile = new File("data.txt");
		FileWriter myWriter = new FileWriter("data.txt");
		
		//If file exists delete it and make an empty one
		if(!myFile.createNewFile()) {
			myFile.delete();
			myFile.createNewFile();
		}
		
		for(double i = min; i < max; i+= increment) {
			double Y = Math.round(i*roundFactor)/roundFactor;
			double calculatedIntegral = Integral(negativeInfinity, Y - 5, func);
			double output = (1/(Math.sqrt(2*Math.PI)));
			double answer = output*calculatedIntegral*100.0;
			String formatedAnswer = df.format(answer);
			String formatedPercentage = df2.format(answer);
			System.out.println(	"Probit: " + Y + 
								" - Probit%: " + formatedAnswer +
								" = " + formatedPercentage + "%");
			String writeString = (String.valueOf(Y) + ":" + String.valueOf(formatedPercentage) + ",");
			myWriter.write(writeString);
			myWriter.flush();
		}
		System.out.println("Finished!");
		myWriter.close();
	}
	
	public static void UserProgram(DecimalFormat df, DecimalFormat df2, Function func, Scanner sc, double roundFactorPercent) {
		boolean running = true;
		boolean init = true;
		//--Body--
		while(running == true) {
			
			//--Get_Setup_Variables--
			if(init == true) {
				System.out.println("This program uses the Trapezoidal Method for integration. Enter an Increment value to use. Note: smaller numbers take longer to compute (recommended: 0.0001, press enter for default) ");
				boolean initDone = false;
				while(initDone == false) {
					String input = sc.nextLine();
					if(input.equals("quit")) {
						running = false;
						initDone = true;
						break;
					} else if(input.equals("")) {
						initDone = true;
					} else {
						try {
							INCREMENT = Double.valueOf(input);
							initDone = true;
						} catch(NumberFormatException e) {
							System.out.println("Invalid Input");
						}
					}
				}
				if(running != false) {
					initDone = false;
					System.out.println("Typed in: " + INCREMENT);
					
					System.out.println("Enter in the value to be used for -Infinity. Note: smaller numbers take longer to compute (recommended: -500, press enter for default)");
					
					while(initDone == false) {
						String input = sc.nextLine();
						if(input.equals("quit")) {
							running = false;
							initDone = true;
							break;
						} else if(input.equals("")) {
							initDone = true;
						} else {
							try {
								negativeInfinity = Double.valueOf(input);
								initDone = true;
							} catch(NumberFormatException e) {
								System.out.println("Invalid Input");
							}
						}
					}
					System.out.println("Typed in: " + negativeInfinity);
					init = false;
					System.out.println("\n");
				}
				
			}
			
			System.out.println("Enter Data in format: data,data,data,...");
			String dataString = sc.nextLine();  // Read user input
			
			
			if(dataString.equals("quit")) { // Exit Program
				running = false;
			} else { // Calculate percentage
				System.out.println("Calculating! Please be patient, it may take a while...");
				String[] dataArray = dataString.split(",");
				for(int k = 0; k < dataArray.length; k++) {
					try {
						double Y = Double.valueOf(dataArray[k]);
						double calculatedIntegral = Integral(negativeInfinity, Y - 5, func);
						double output = (1/(Math.sqrt(2*Math.PI)));
						double answer = output*calculatedIntegral*100.0;
						String formatedAnswer = df.format(answer);
						String formatedPercentage = df2.format(answer);
						System.out.println(	"Input: " + Y + 
											" - Probit%: " + formatedAnswer +
											" = " + formatedPercentage + "%");
					} catch(NumberFormatException e) {
						System.out.println("Invalid Input");
					}
				}
				System.out.println("");
			}
		}
	}
	
	//Using Trapezoidal Rule
	public static double Integral(double a, double b, Function function) {
		double area = 0;
		double modifier = 1; //Lets us do negative integrals
		if(a > b) {
			double tempA = a;
			a = b;
			b = tempA;
			modifier = -1;
		}
		for(double i = a + INCREMENT; i < b; i+= INCREMENT) {
			double dFromA = i - a; //Distance from A
			area += (INCREMENT/2) * (function.f(a + dFromA) + (function.f(a + dFromA - INCREMENT)));
		}
		return area;
	}
}
