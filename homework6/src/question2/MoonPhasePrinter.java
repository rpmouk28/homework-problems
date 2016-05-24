package question2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This program shows the phase of the moon base on the year, month and day.
 */
public class MoonPhasePrinter {
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter the year (e.g. 1977): ");
		int year = in.nextInt();
		System.out.println("Please enter the month (e.g. 9): ");
		int month = in.nextInt();
		System.out.println("Please enter the day (e.g. 13): ");
		int day = in.nextInt();

		String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
				"Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

		String monthName = monthNames[month - 1];
		String match = String.format("%s %2d", monthName, day);
		String foundPhase = "none";

		// Build the URL string and open a URLConnection.
		// Be sure to set the year on the URL string!!!
		URL u = new URL(
				"http://aa.usno.navy.mil/cgi-bin/aa_moonphases.pl?year=" + year);
		URLConnection connection = u.openConnection();

		// Get the connection's input stream, and make a Scanner for it
		InputStream instream = connection.getInputStream();
		Scanner input = new Scanner(instream);

		while (input.hasNextLine()) {
			// Read input lines from the scanner into the String named line.
			String line = input.nextLine();
			// Trim leading and trailing whitespace from line.
			String shortLine = line.trim();

			// Check for a month name at the beginning of the line.
			if (shortLine.length() < 3
					|| Arrays.asList(monthNames).indexOf(
							shortLine.substring(0, 3)) == -1) {
				continue;
			}

			// The start of a possible solution:
			// You could search for matching month & day in each of the columns.
			// For example the columns in the table look like these:
			// Nov 25 6 10 Dec 2 9 52 Dec 10 14 36 Dec 18 0 48
			// Dec 24 18 06
			// You could use the String class's indexOf() method to find the
			// match.
			// If the index is 4, then the match was found in the New Moon
			// column.
			// Else if the index is 20, then the match was found in the First
			// Quarter column.
			// Else if the index is 36, then the match was found in the Full
			// Moon column.
			// Else if the index is 52, then the match was found in the Last
			// Quarter column.
			// Set the foundPhase string to the column name of the match, such
			// as "First Quarter".
			if (line.indexOf(match) == 4) {
				foundPhase = "New Moon";
				System.out.println(match + " is a match was found in the "
						+ foundPhase + " Column");
			} else if (line.indexOf(match) == 20) {
				foundPhase = "First Quarter";
				System.out.println(match + " is match was found in the " + foundPhase
						+ " Column");

			} else if (line.indexOf(match) == 36) {
				foundPhase = "Full Moon";
				System.out.println(match + " is match was found in the " + foundPhase
						+ " Column");

			} else if (line.indexOf(match) == 52) {
				foundPhase = "Last Quarter";
				System.out.println(match + " is a match was found in the "
						+ foundPhase + " Column");

			}

		}

		System.out.println("Phase: " + foundPhase);
	}
}
