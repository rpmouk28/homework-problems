package question2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * A table for lookups and reverse lookups
 */
public class LookupTable {
	private ArrayList<Item> byName;
	private ArrayList<Item> byNumber;

	/**
	 * Constructs a LookupTable object.
	 */
	public LookupTable() {
		byName = new ArrayList<Item>();
		byNumber = new ArrayList<Item>();
	}

	/**
	 * Reads name and number pairs from the Scanner and adds them to the byName
	 * and byNumber array lists.
	 * 
	 * @param in
	 *            the scanner for reading the input
	 */
	public void read(Scanner in) {
		while (in.hasNextLine()) {
			// Read a line containing the name
			String name = in.nextLine();
			// Read a line containing the number
			String number = in.nextLine();
			// Store the name and number in the byName array list
			byName.add(new Item(name, number));
			// Store the number and name in the byNumber array list
			byNumber.add(new Item(number, name));
		}
		// Sort the byName Items so we can binary search
		Collections.sort(byName);
		// Sort the byNumber Items so we can binary search
		Collections.sort(byNumber);
	}

	/**
	 * Looks up an item in the table.
	 * 
	 * @param k
	 *            the key to find
	 * @return the value with the given key, or null if no such item was found.
	 */
	public String lookup(String k) {
		// Use the Collections.binarySearch() method to find the
		// position of the matching name in the byName array list.
		// Return null if position is less than 0 (not found).
		// Otherwise, return the number for the found name.
		Item newItem = new Item(k, null);
		int find = Collections.binarySearch(byName, newItem);
		if (find < 0) {
			return null;
		} else {
			return byName.get(find).getValue();
		}

	}

	/**
	 * Looks up an item in the table.
	 * 
	 * @param v
	 *            the value to find
	 * @return the key with the given value, or null if no such item was found.
	 */
	public String reverseLookup(String v) {
		// Use the Collections.binarySearch() method to find the
		// position of the matching number in the byNumber array list.
		// Return null if position is less than 0 (not found).
		// Otherwise, return the name for the found number.
		Item newItem = new Item(v, null);
		int find = Collections.binarySearch(byNumber, newItem);
		if (find < 0) {
			return null;
		} else {
			return byNumber.get(find).getValue();
		}
	}
}
