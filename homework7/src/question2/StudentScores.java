package question2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import java.util.Scanner;

/**
 * This is a score tracking program. The StudentScores allows an instructor to
 * display the scores for students in the class, to add new scores, and to
 * modify scores.
 */
public class StudentScores {
	private Connection conn;
	private Scanner in;

	public static void main(String args[]) throws IOException, SQLException,
			ClassNotFoundException {
		if (args.length == 0) {
			System.out.println("Usage: java StudentScores propertiesFile");
			System.exit(0);
		}

		StudentScores db = new StudentScores(args[0]);
		db.run();
	}

	/**
	 * Constructs a StudentScores object and makes a connection to the database.
	 * 
	 * @param propfile
	 *            the properties file for database access
	 */
	public StudentScores(String propfile) throws IOException, SQLException,
			ClassNotFoundException {
		SimpleDataSource.init(propfile);

		conn = SimpleDataSource.getConnection();
		in = new Scanner(System.in);
	}

	/**
	 * Runs the grade book service.
	 */
	public void run() throws SQLException {
		try {
			boolean done = false;
			while (!done) {
				System.out
						.println("I) Initialize database  D)isplay scores  A)dd/modify score S) Add/modify student Q)uit");
				String input = in.nextLine().toUpperCase();
				if (input.equals("I")) {
					System.out
							.println("Enter 'YES' to remove all existing data: ");
					String answer = in.nextLine();
					if (answer.equalsIgnoreCase("YES"))
						initializeDatabase();
					else
						System.out.println("OK, existing data preserved");
				} else if (input.equals("D"))
					displayScores();
				else if (input.equals("A"))
					addScore();
				else if (input.equals("S"))
					addStudent();
				else if (input.equals("Q"))
					done = true;
			}
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	private void initializeDatabase() throws SQLException {
		// Remove any existing Students and Scores tables
		Statement stat = conn.createStatement();
		try {
			try {
				stat.execute("DROP TABLE Students");
			} catch (SQLException e) {
				System.out
						.println("Notice: Exception during DROP TABLE Students: "
								+ e.getMessage());
			}
			try {
				stat.execute("DROP TABLE Scores");
			} catch (SQLException e) {
				System.out
						.println("Notice: Exception during DROP TABLE Grades: "
								+ e.getMessage());
			}
			// Students table: Student ID, Name
			stat.execute("CREATE TABLE Students (Student_ID INTEGER, Name VARCHAR(64))");
			stat.execute("CREATE TABLE Scores (Student_ID INTEGER, Assignment VARCHAR(25), Score DOUBLE)");
			// Add default list of students and grades
			final String[] students = { "1, 'Adams, Arnold'",
					"2, 'Baker, Jane'", "3, 'Conner, Jason'" };
			for (String s : students) {
				stat.execute("INSERT INTO Students (Student_ID, Name) VALUES ("
						+ s + ")");
				System.out.println("Notice: inserted student " + s);
			}
			final String[] scores = { "1, 'Homework 1', 100",
					"1, 'Homework 2', 85", "1, 'Homework 3', 75",
					"2, 'Homework 1', 76", "2, 'Homework 2', 86",
					"2, 'Homework 3', 96", "3, 'Homework 1', 84",
					"3, 'Homework 2', 85", "3, 'Homework 3', 86" };
			for (String g : scores) {
				stat.execute("INSERT INTO Scores (Student_ID, Assignment, Score) VALUES ("
						+ g + ")");
				System.out.println("Notice: inserted score " + g);
			}
		} finally {
			stat.close();
		}
	}

	/**
	 * Display all the scores for a student.
	 */
	public void displayScores() throws SQLException {
		System.out.print("Enter Student ID: ");
		int sid = in.nextInt();
		in.nextLine();

		String name = getStudentName(sid);

		if (name == null) {
			System.out.println("No such student ID.");
			return;
		}

		// Variables used to compute the average score.
		int scoreCount = 0;
		double scoreTotal = 0;

		System.out.println(name);
		System.out.printf("%-25s %-7s\n", "Assignment Name", "Score");
		System.out.println("------------------------- -------");

		// Prepare and execute the query to get the assignment and score
		// from the Scores table for the specified student ID.
		String query = "SELECT Scores.Assignment, Scores.Score FROM Scores WHERE Student_ID="
				+ sid + "";
		PreparedStatement stat = conn.prepareStatement(query);
		ResultSet result = stat.executeQuery();

		// Loop over the rows in the result set and print the
		// assignment name and score. Add the score to the scoreTotal
		// and increment the scoreCount so we can print the
		// average at the end.

		while (result.next()) {
			scoreCount++;
			String assignmentName = result.getString(1);
			int score = result.getInt(2);
			scoreTotal += score;
			System.out.printf("%-25s %-7s\n", assignmentName, score);
		}

		
		System.out.println();
		if (scoreCount > 0) {
			System.out.printf("%-25s %7.2f\n", "Average score:", scoreTotal
					/ scoreCount);
		} else {
			System.out.println("No scores found, so no average computed.");
		}
		System.out.println();
	}

	/**
	 * Add/modify a score.
	 */
	public void addScore() throws SQLException {
		System.out.print("Student ID: ");
		int sid = in.nextInt();
		in.nextLine(); // consume newline

		String name = getStudentName(sid);

		if (name == null) {
			System.out.println("No such student ID.");
			return;
		}

		System.out.print("Enter Assignment Name: ");
		String assignment = in.nextLine();

		System.out.print("Enter Score (out of 100): ");
		String scoreString = in.nextLine();
		double score = Double.parseDouble(scoreString);

		PreparedStatement stat = conn
				.prepareStatement("UPDATE Scores SET Score = ? WHERE Student_ID = ? AND Assignment = ?");
		stat.setDouble(1, score);
		stat.setInt(2, sid);
		stat.setString(3, assignment);
		if (stat.executeUpdate() == 1)
			System.out.println("Grade modified.");
		else {
			stat = conn.prepareStatement("INSERT INTO Scores VALUES(?, ?, ?)");
			stat.setInt(1, sid);
			stat.setString(2, assignment);
			stat.setDouble(3, score);
			stat.executeUpdate();
			System.out.println("Score added.");
		}
	}

	/**
	 * Add/modify a student.
	 */
	public void addStudent() throws SQLException {
		boolean insert = false;

		System.out.print("Student ID: ");
		int sid = in.nextInt();
		in.nextLine(); // consume newline

		String name = getStudentName(sid);

		if (name == null) {
			System.out.println("Adding a student.");
			insert = true;
		}

		if (!insert)
			System.out.print("Enter Student Name (was \"" + name + "\": ");
		else
			System.out.print("Enter Student Name: ");
		String newName = in.nextLine();

		if (!insert) {
			PreparedStatement stat = conn
					.prepareStatement("UPDATE Students SET Name = ? WHERE Student_ID = ?");
			stat.setString(1, newName);
			stat.setInt(2, sid);
			if (stat.executeUpdate() == 1) {
				System.out.println("Student modified.");
			} else {
				System.err.println("Student was not modified.");
			}
		} else /* Insert */
		{
			PreparedStatement stat = conn
					.prepareStatement("INSERT INTO Students VALUES(?, ?)");
			stat.setInt(1, sid);
			stat.setString(2, newName);
			stat.executeUpdate();
			System.out.println("Student added.");
		}
	}

	/**
	 * Gets the name of a student in the database.
	 * 
	 * @param id
	 *            the student ID
	 * @return the name, or null if there is no student with the given ID
	 */
	public String getStudentName(int id) throws SQLException {
		PreparedStatement stat = conn
				.prepareStatement("SELECT Name FROM Students WHERE Student_ID = ?");
		stat.setInt(1, id);
		ResultSet result = stat.executeQuery();
		if (result.next())
			return result.getString(1).trim();
		else
			return null;
	}
	
}
