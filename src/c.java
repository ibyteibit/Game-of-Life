import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class c {

	public static int inputErrorLoop(int[] validInputs, String inputMessage, String errorMessage) {
		Scanner in = new Scanner(System.in);
		int input = 0;

		do {
			System.out.print("\n" + inputMessage);

			input = in.nextInt();

			if (countOccurrences(input, validInputs) > 0)
				break;

			System.out.print("\n" + errorMessage + "\n");

		} while (countOccurrences(input, validInputs) == 0);
		System.out.println();
		in.close();
		return input;
	}
	
	public static int countOccurrences(int num, int[] arr) {
		int count = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == num)
				count ++;
		}
		return count;
	}

	public static void print2d(int[][] arr) {
		String x = "";
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				x += "|" + arr[i][j];
				for (int k = 0; k < findNumLength(findMaxin2dArr(arr)) - findNumLength(arr[i][j]); k++)
					x += " ";
				x += "|";
			}
			x += "\n";
		}
		System.out.println(x);
	}
	
	public static void print2d(char[][] arr) {
		String x = "";
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				x += "|" + arr[i][j] + "|";
			}
			x += "\n";
		}
		System.out.println(x);
	}
	
	public static void print2d(Object[][] arr) {
		String x = "";
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				x += "|" + arr[i][j].toString();
				for (int k = 0; k <  findLongestToString2DArr(arr).toString().length() - arr[i][j].toString().length(); k++)
					x += " ";
				x += "|";
			}
			x += "\n";
		}
		System.out.println(x);
	}

	public static int findMaxinArr(int[] arr) {
		int max = arr[0];
		for (int j = 1; j < arr.length; j++) {
			if (arr[j] > max)
				max = arr[j];
		}
		return max;
	}
	
	public static int findMaxin2dArr(int[][] arr) {
		int max = arr[0][0];
		for (int i = 0; i < arr.length; i ++) {
			if (findMaxinArr(arr[i]) > max)
				max = findMaxinArr(arr[i]);
		}
		
		return max;
	}
	
	public static String findLongestStringArr(String[] arr) {
		String max = arr[0];
		for (int i = 1; i < arr.length; i ++) {
			if (arr[i].length() > max.length())
				max = arr[i];
		}
		return max;
	}
	
	public static String findLongestString2dArr(String[][] arr) {
		String max = arr[0][0];
		for (int i = 0; i < arr.length; i ++) {
			if (findLongestStringArr(arr[i]).length() > max.length())
				max = findLongestStringArr(arr[i]);
		}
		return max;
	}


	public static int findMaxArrayList(ArrayList<Integer> arr) {
		int max = 0;
		int size = arr.size();
		for (int j = 1; j < size; j++) {
			if (arr.get(j) > max)
				max = arr.get(j);
		}
		return max;
	}
	
	public static String[] append(String str, String[] arr) {
		String[] newStringArr = new String[arr.length + 1];
		for (int i = 0; i < arr.length; i ++) {
			newStringArr[i] = arr[i];
		}
		newStringArr[arr.length] = str;
		
		return newStringArr;
		
	}
	
	public static int[] append(int str, int[] arr) {
		int[] newStringArr = new int[arr.length + 1];
		for (int i = 0; i < arr.length; i ++) {
			newStringArr[i] = arr[i];
		}
		newStringArr[arr.length] = str;
		
		return newStringArr;
		
	}


	public static String sha256(String str) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hash = md.digest(str.getBytes(StandardCharsets.UTF_8));

		BigInteger number = new BigInteger(1, hash);

		StringBuilder hexString = new StringBuilder(number.toString(16));

		while (hexString.length() < 32) {
			hexString.insert(0, '0');
		}

		return hexString.toString();
	}

	public static String randomWord() throws IOException {
		return readFile("dictionary.txt")[(int) (Math.random() * readFile("dictionary.txt").length)];
	}

	public static String[] readFile(String infile) throws IOException {

		Path path = Paths.get(infile);
		String[] arr = new String[0];
		arr = Files.readAllLines(path).toArray(arr);
		return arr;

	}

	public static void writeFile(String outfile, String[] text) throws FileNotFoundException {
		PrintWriter output = new PrintWriter(new File(outfile));
		for (String str : text)
			output.println(str);
		output.close();
	}

	public static int loopedIndex(int arrLength, int index) {
		while (index > arrLength - 1)
			index -= arrLength;
		return index;
	}

	public static void selectionSort(int[] arr) {
		int min;
		int minIndex = 0;

		for (int i = 0; i < arr.length - 1; i++) {
			min = arr[i];
			minIndex = i;

			for (int j = i + 1; j < arr.length; j++) {
				if (arr[j] < min) {
					min = arr[j];
					minIndex = j;
				}
			}

			if (i != minIndex) {
				swap(arr, i, minIndex);
			}
		}
	}

	public static void insertionSort(int[] arr, boolean ascending) {
		int position;
		for (int i = 1; i < arr.length; i++) {
			position = i;
			if (ascending) {
				while (position > 0 && arr[position] < arr[position - 1]) {
					swap(arr, position, position - 1);
					position--;
				}
			}
			else {
				while (position > 0 && arr[position] > arr[position - 1]) {
					swap(arr, position, position - 1);
					position--;
				}
			}
		}

	}

	public static void bubbleSort(int[] arr) {
		boolean swaps;
		do {
			swaps = false;
			for (int i = 0; i < arr.length - 1; i++) {
				
				if (arr[i] > arr[i + 1]) {
					swap(arr, i, i+1);
					swaps = true;	
				}
			}
		} while(swaps);
		
	}
	
	public static int binarySearch(int[] arr, int element) {
		int sIndex = 0;
		int endIndex = arr.length - 1;
		int loc = arr.length/2;
		while (sIndex <= endIndex) {
			if (arr[loc] == element)
				return loc;
			if (element > arr[loc])
				sIndex = loc + 1;
			else
				endIndex = loc - 1;
			loc = (sIndex + endIndex) / 2;
		}
		if (element == arr[loc])
			return loc;
		return -1;
	}
	
	public static String formatMoney(double money) {
		money = Math.round(money * 100);
		money /= 100;
		String mon = "" + money;
		if (mon.charAt(mon.length() - 2) == '.')
			mon += "0";
		return mon;
	}

	public static void swap(int[] arr, int index1, int index2) {
		int temp = arr[index1];
		arr[index1] = arr[index2];
		arr[index2] = temp;
	}
	
	public static Object findLongestToStringArr(Object[] arr) {
		Object max = arr[0];
		for (int i = 1; i < arr.length; i ++) {
			if (arr[i].toString().length() > max.toString().length())
				max = arr[i];
		}
		return max;
	}
	
	public static Object findLongestToString2DArr(Object[][] arr) {
		Object max = arr[0][0];
		for (int i = 0; i < arr.length; i ++) {
			if (findLongestToStringArr(arr[i]).toString().length() > max.toString().length())
				max = findLongestToStringArr(arr[i]);
		}
		return max;
	}
	
	public static int indexOf(Object[] arr, Object c) {
		for (int i = 0; i < arr.length; i ++) 
			if (c.equals(arr[i]))
				return i;
		return -1;
	}
	
	
	public static Point indexOf2D(Object[][] arr, Object c) {
		for (int i = 0; i < arr.length; i ++) {
			if (indexOf(arr[i], c) != -1)
				return new Point(i, indexOf(arr[i], c));
		}
		return new Point(-1, -1);
	}
	
	public static Integer[] intArrToIntegerArr(int[] arr) {
		Integer[] arr1 = new Integer[arr.length];
		for (int i = 0; i < arr.length; i++) {
			arr1[i] = arr[i];
		}
		return arr1;
	}
	
	public static Character[] charArrToCharacterArr(char[] arr) {
		Character[] arr1 = new Character[arr.length];
		for (int i = 0; i < arr.length; i++) {
			arr1[i] = arr[i];
		}
		return arr1;
	}
	
	private static int findNumLength(int num) {
		return ("" + num).length();
	}

}




