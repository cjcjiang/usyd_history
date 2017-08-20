package Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import oracle.jdbc.pool.OracleDataSource;
import Business.Issue;
import Presentation.IRepositoryProvider;

import java.sql.*;

import java.util.regex.*;

/**
 * Encapsulates create/read/update/delete operations to Oracle database
 * 
 * @author Yuming JIANG
 *
 */
public class OracleRepositoryProvider implements IRepositoryProvider {
	// connection parameters - ENTER YOUR LOGIN AND PASSWORD HERE
	private final String userid = "yjia4072";
	private final String passwd = "Mima4072";
	private final String database = "soit-db-pro-5.ucc.usyd.edu.au:1521:COMP9120";

	private Connection conn = null;
	private PreparedStatement stmt;

	OracleRepositoryProvider() {
		try {
			/* load Oracle's JDBC driver */
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("JDBC class is found");
		} catch (ClassNotFoundException no_class_ex) {
			/* error handling when no JDBC class is found */
			System.out.println("no JDBC class is found");
		}
	}

	/**
	 * Update the details for a given issue
	 * 
	 * @param issue
	 *            : the issue for which to update details
	 */
	@Override
	public void updateIssue(Issue issue) {
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@" + database, userid, passwd);
		} catch (SQLException sql_ex) {
			System.out.println("database connection fail");
		}

		int id = issue.getId();
		String title = issue.getTitle();
		String description = issue.getDescription();
		int creator = issue.getCreator();
		int resolver = issue.getResolver();
		int verifier = issue.getVerifier();

		try {
//			stmt = conn.prepareStatement(
//					" update A3_ISSUE set TITLE=?,DESCRIPTION=?,CREATOR=?,RESOLVER=?,VERIFIER=? where ID=? ");
//			stmt.setString(1, title);
//			stmt.setString(2, description);
//			stmt.setInt(3, creator);
//			stmt.setInt(4, resolver);
//			stmt.setInt(5, verifier);
//			stmt.setInt(6, id);
//			stmt.executeUpdate();
//			stmt.close();
			
			conn.setAutoCommit(false);
			CallableStatement callstmt = conn.prepareCall("{call UPDATEPROCEDURE(?,?,?,?,?,?)}");
			callstmt.setString(1, title);
			callstmt.setString(2, description);
			callstmt.setInt(3, creator);
			callstmt.setInt(4, resolver);
			callstmt.setInt(5, verifier);
			callstmt.setInt(6, id);
			callstmt.executeUpdate();
			conn.commit();
			callstmt.close();
			
			if (conn == null)
				System.err.println("You are not connected to Oracle!");
			else {
				conn.close(); // close the connection again after usage!
				conn = null;
			}

		} catch (SQLException e) {
			try{
				conn.rollback();
			}catch(SQLException ex){}
			System.out.println("update issue fail");
		}

	}

	/**
	 * Find the issues associated in some way with a user Issues which have the
	 * id parameter below in any one or more of the creator, resolver, or
	 * verifier fields should be included in the result
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Vector<Issue> findUserIssues(int id) {
		Vector<Issue> issue = new Vector<Issue>();
		try {
			try {
				conn = DriverManager.getConnection("jdbc:oracle:thin:@" + database, userid, passwd);
			} catch (SQLException sql_ex) {
				System.out.println("database connection fail");
			}

			stmt = conn.prepareStatement(" SELECT * FROM A3_ISSUE ");
			ResultSet rset = stmt.executeQuery();

			int num = 0;
			Vector<Issue> temp = new Vector<Issue>();
			while (rset.next()) {
				num++;
				Issue member = new Issue();
				member.setId(rset.getInt("ID"));
				member.setTitle(rset.getString("TITLE"));
				member.setDescription(rset.getString("DESCRIPTION"));
				member.setCreator(rset.getInt("CREATOR"));
				member.setResolver(rset.getInt("RESOLVER"));
				member.setVerifier(rset.getInt("VERIFIER"));
				temp.add(member);
				System.out.println("issue add");
			}
			if (num == 0)
				System.out.println("No entries found.");

			Iterator<Issue> ite = temp.iterator();
			while (ite.hasNext()) {
				Issue checkIssue = ite.next();
				int creator = checkIssue.getCreator();
				int resolver = checkIssue.getResolver();
				int verifier = checkIssue.getVerifier();
				if (creator == id || resolver == id || verifier == id) {
					issue.add(checkIssue);
				}
			}

			stmt.close();
			if (conn == null)
				System.err.println("You are not connected to Oracle!");
			else
				try {
					conn.close(); // close the connection again after usage!
					conn = null;
				} catch (SQLException sql_ex) { /* error handling */
					System.out.println("database disconnect fail");
				}

		} catch (SQLException sql_ex) {
			/* error handling */

			System.out.println("FIND USER ISSUE FAIL");

		}
		return issue;
		// return null;
	}

	/**
	 * Add the details for a new issue to the database
	 * 
	 * @param issue:
	 *            the new issue to add
	 */
	@Override
	public void addIssue(Issue issue) {
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@" + database, userid, passwd);
		} catch (SQLException sql_ex) {
			System.out.println("database connection fail");
		}

		String title = issue.getTitle();
		String description = issue.getDescription();
		int creator = issue.getCreator();
		int resolver = issue.getResolver();
		int verifier = issue.getVerifier();

		try {
//			stmt = conn.prepareStatement(
//					" Insert into A3_ISSUE (TITLE,DESCRIPTION,CREATOR,RESOLVER,VERIFIER) values (?,?,?,?,?) ");
//			stmt.setString(1, title);
//			stmt.setString(2, description);
//			stmt.setInt(3, creator);
//			stmt.setInt(4, resolver);
//			stmt.setInt(5, verifier);
//			stmt.executeUpdate();
//			stmt.close();
			
			conn.setAutoCommit(false);
			CallableStatement callstmt = conn.prepareCall("{call ADDPROCEDURE(?,?,?,?,?)}");
			callstmt.setString(1, title);
			callstmt.setString(2, description);
			callstmt.setInt(3, creator);
			callstmt.setInt(4, resolver);
			callstmt.setInt(5, verifier);
			callstmt.executeUpdate();
			conn.commit();
			callstmt.close();
			
			
			if (conn == null)
				System.err.println("You are not connected to Oracle!");
			else {
				conn.close(); // close the connection again after usage!
				conn = null;
			}

		} catch (SQLException e) {
			try{
				conn.rollback();
			}catch(SQLException ex){}
			System.out.println("add issue fail");
		}

	}

	/**
	 * Given an expression searchString like myFirst words|my second words this
	 * method should return any issues associated with a user based on userId
	 * that either: contain 1 or more of the phrases separated by the '|'
	 * character in the issue title OR contain 1 or more of the phrases
	 * separated by the '|' character in the issue description OR
	 * 
	 * @param searchString:
	 *            the searchString to use for finding issues in the database
	 *            based on the issue titles and descriptions. searchString may
	 *            either be a single phrase, or a phrase separated by the '|'
	 *            character. The searchString is used as described above to find
	 *            matching issues in the database.
	 * @param userId:
	 *            used to first find issues associated with userId on either one
	 *            or more of the creator/resolver/verifier fields. Once a user's
	 *            issues are identified, the search would then take place on the
	 *            user's associated issues.
	 * @return
	 */
	@Override
	public Vector<Issue> findIssueBasedOnExpressionSearchedOnTitleAndDescription(String searchString, int userId) {
		Vector<Issue> temp = new Vector<Issue>();
		Vector<Issue> issue = new Vector<Issue>();
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@" + database, userid, passwd);
		} catch (SQLException sql_ex) {
			System.out.println("database connection fail");
		}

		if (searchString.equals("")) {
			int num = 0;
			try {
				stmt = conn.prepareStatement(" SELECT * FROM A3_ISSUE ");
				ResultSet rset = stmt.executeQuery();

				while (rset.next()) {
					num++;
					Issue member = new Issue();
					member.setId(rset.getInt("ID"));
					member.setTitle(rset.getString("TITLE"));
					member.setDescription(rset.getString("DESCRIPTION"));
					member.setCreator(rset.getInt("CREATOR"));
					member.setResolver(rset.getInt("RESOLVER"));
					member.setVerifier(rset.getInt("VERIFIER"));
					temp.add(member);
					System.out.println("issue add");
				}

				stmt.close();
				if (conn == null)
					System.err.println("You are not connected to Oracle!");
				else {
					conn.close(); // close the connection again after usage!
					conn = null;
				}

			} catch (SQLException e) {
				System.out.println("In search method, SELECT * FROM A3_ISSUE fail");
			}

			if (num == 0)
				System.out.println("No entries found.");

			Iterator<Issue> ite = temp.iterator();
			while (ite.hasNext()) {
				Issue checkIssue = ite.next();
				int creator = checkIssue.getCreator();
				int resolver = checkIssue.getResolver();
				int verifier = checkIssue.getVerifier();
				if (creator == userId || resolver == userId || verifier == userId) {
					issue.add(checkIssue);
				}
			}
		}

		if (!searchString.equals("") && searchString.indexOf("@") == -1) {
			if (searchString.indexOf("|") == -1) {
				String pattern = ".*" + searchString + ".*";
				System.out.println(pattern);
				Pattern searchPattern = Pattern.compile(pattern);
				int num = 0;
				try {
					stmt = conn.prepareStatement(" SELECT * FROM A3_ISSUE ");
					ResultSet rset = stmt.executeQuery();

					while (rset.next()) {
						num++;
						Issue member = new Issue();
						member.setId(rset.getInt("ID"));
						member.setTitle(rset.getString("TITLE"));
						member.setDescription(rset.getString("DESCRIPTION"));
						member.setCreator(rset.getInt("CREATOR"));
						member.setResolver(rset.getInt("RESOLVER"));
						member.setVerifier(rset.getInt("VERIFIER"));
						temp.add(member);
						System.out.println("issue add");
					}

					stmt.close();
					if (conn == null)
						System.err.println("You are not connected to Oracle!");
					else {
						conn.close(); // close the connection again after usage!
						conn = null;
					}

				} catch (SQLException e) {
					System.out.println("In search method, SELECT * FROM A3_ISSUE fail");
				}

				if (num == 0)
					System.out.println("No entries found.");

				Vector<Issue> tempRE = new Vector<Issue>();
				Iterator<Issue> ite = temp.iterator();
				while (ite.hasNext()) {
					Issue checkIssue = ite.next();
					int creator = checkIssue.getCreator();
					int resolver = checkIssue.getResolver();
					int verifier = checkIssue.getVerifier();
					if (creator == userId || resolver == userId || verifier == userId) {
						tempRE.add(checkIssue);
					}
				}

				Iterator<Issue> iteRE = tempRE.iterator();
				while (iteRE.hasNext()) {
					Issue checkIssue = iteRE.next();
					String title = checkIssue.getTitle();
					String description = checkIssue.getDescription();
					Matcher searchTitleMatcher = searchPattern.matcher(title);
					Matcher searchDesMatcher = searchPattern.matcher(description);
					if (searchTitleMatcher.matches() || searchDesMatcher.matches()) {
						issue.add(checkIssue);
						System.out.println("one matched");
					}
					System.out.println("matching");
				}
			}

			if (searchString.indexOf("|") != -1) {
				int firstOrIndex = searchString.indexOf("|");
				int lastOrIndex = searchString.lastIndexOf("|");
				if (firstOrIndex == lastOrIndex) {
					String searchStringOne = searchString.substring(0, firstOrIndex);
					String searchStringTwo = searchString.substring(firstOrIndex + 1);
					String patterOne = ".*" + searchStringOne + ".*";
					String patterTwo = ".*" + searchStringTwo + ".*";
					System.out.println(patterOne + " " + patterTwo);
					Pattern searchPatternOne = Pattern.compile(patterOne);
					Pattern searchPatternTwo = Pattern.compile(patterTwo);

					int num = 0;
					try {
						stmt = conn.prepareStatement(" SELECT * FROM A3_ISSUE ");
						ResultSet rset = stmt.executeQuery();

						while (rset.next()) {
							num++;
							Issue member = new Issue();
							member.setId(rset.getInt("ID"));
							member.setTitle(rset.getString("TITLE"));
							member.setDescription(rset.getString("DESCRIPTION"));
							member.setCreator(rset.getInt("CREATOR"));
							member.setResolver(rset.getInt("RESOLVER"));
							member.setVerifier(rset.getInt("VERIFIER"));
							temp.add(member);
							System.out.println("issue add");
						}

						stmt.close();
						if (conn == null)
							System.err.println("You are not connected to Oracle!");
						else {
							conn.close(); // close the connection again after
											// usage!
							conn = null;
						}

					} catch (SQLException e) {
						System.out.println("In search method, SELECT * FROM A3_ISSUE fail");
					}

					if (num == 0)
						System.out.println("No entries found.");

					Vector<Issue> tempRE = new Vector<Issue>();
					Iterator<Issue> ite = temp.iterator();
					while (ite.hasNext()) {
						Issue checkIssue = ite.next();
						int creator = checkIssue.getCreator();
						int resolver = checkIssue.getResolver();
						int verifier = checkIssue.getVerifier();
						if (creator == userId || resolver == userId || verifier == userId) {
							tempRE.add(checkIssue);
						}
					}

					Iterator<Issue> iteRE = tempRE.iterator();
					while (iteRE.hasNext()) {
						Issue checkIssue = iteRE.next();
						String title = checkIssue.getTitle();
						String description = checkIssue.getDescription();
						Matcher searchTitleMatcherOne = searchPatternOne.matcher(title);
						Matcher searchTitleMatcherTwo = searchPatternTwo.matcher(title);
						Matcher searchDesMatcherOne = searchPatternOne.matcher(description);
						Matcher searchDesMatcherTwo = searchPatternTwo.matcher(description);
						if (searchTitleMatcherOne.matches() || searchTitleMatcherTwo.matches()
								|| searchDesMatcherOne.matches() || searchDesMatcherTwo.matches()) {
							issue.add(checkIssue);
							System.out.println("one matched");
						}
						System.out.println("matching");
					}
				}
				if (firstOrIndex != lastOrIndex) {
					String[] divededString=searchString.split("\\|");
					int numOfDivededString = divededString.length;
					int count = 0;
					ArrayList<Pattern> patternList = new ArrayList();
					while(count<numOfDivededString){
						String pattern = ".*" + divededString[count] + ".*";
						System.out.println(pattern);
						Pattern searchPattern = Pattern.compile(pattern);
						patternList.add(searchPattern);
						count++;
					}
					
					int num = 0;
					try {
						stmt = conn.prepareStatement(" SELECT * FROM A3_ISSUE ");
						ResultSet rset = stmt.executeQuery();

						while (rset.next()) {
							num++;
							Issue member = new Issue();
							member.setId(rset.getInt("ID"));
							member.setTitle(rset.getString("TITLE"));
							member.setDescription(rset.getString("DESCRIPTION"));
							member.setCreator(rset.getInt("CREATOR"));
							member.setResolver(rset.getInt("RESOLVER"));
							member.setVerifier(rset.getInt("VERIFIER"));
							temp.add(member);
							System.out.println("issue add");
						}

						stmt.close();
						if (conn == null)
							System.err.println("You are not connected to Oracle!");
						else {
							conn.close(); // close the connection again after usage!
							conn = null;
						}

					} catch (SQLException e) {
						System.out.println("In search method, SELECT * FROM A3_ISSUE fail");
					}

					if (num == 0)
						System.out.println("No entries found.");

					Vector<Issue> tempRE = new Vector<Issue>();
					Iterator<Issue> ite = temp.iterator();
					while (ite.hasNext()) {
						Issue checkIssue = ite.next();
						int creator = checkIssue.getCreator();
						int resolver = checkIssue.getResolver();
						int verifier = checkIssue.getVerifier();
						if (creator == userId || resolver == userId || verifier == userId) {
							tempRE.add(checkIssue);
						}
					}

					Iterator<Issue> iteRE = tempRE.iterator();
					while (iteRE.hasNext()) {
						Issue checkIssue = iteRE.next();
						String title = checkIssue.getTitle();
						String description = checkIssue.getDescription();
						ArrayList<Matcher> matcherList = new ArrayList();
						Iterator<Pattern> itePattern = patternList.iterator();
						while(itePattern.hasNext()){
							Matcher searchTitleMatcher = itePattern.next().matcher(title);
							matcherList.add(searchTitleMatcher);
						}
						Iterator<Pattern> itePatternRE = patternList.iterator();
						while(itePatternRE.hasNext()){
							Matcher searchDesMatcher = itePatternRE.next().matcher(description);
							matcherList.add(searchDesMatcher);
						}
						boolean matchCheckFlag = false;
						Iterator<Matcher> iteMatcher = matcherList.iterator();
						while(iteMatcher.hasNext()){
							Matcher matcher = iteMatcher.next();
							if(matcher.matches()){
								matchCheckFlag = true;
							}
						}
						if (matchCheckFlag) {
							issue.add(checkIssue);
							System.out.println("one matched");
						}
						System.out.println("matching");
					}
				}
			}

		}

		if (!searchString.equals("") && searchString.indexOf("@") != -1) {
			int searchID = -1;
			int firstAtIndex = searchString.indexOf("@");
			int lastAtIndex = searchString.lastIndexOf("@");
			String searchName = searchString.substring(firstAtIndex + 1, lastAtIndex);
			String searchStringNoName = searchString.substring(lastAtIndex + 1);
			System.out.println(searchName + " " + searchStringNoName);
			try {
				stmt = conn.prepareStatement(" select ID from A3_USER where FIRSTNAME=? or LASTNAME=? ");
				stmt.setString(1, searchName);
				stmt.setString(2, searchName);
				ResultSet rset = stmt.executeQuery();
				while (rset.next()) {
					searchID = rset.getInt("ID");
				}
				stmt.close();
				System.out.println(searchID);
			} catch (SQLException e) {
				System.out.println("select from A3_USER fail");
			}

			if (searchStringNoName.equals("")) {
				try {
					stmt = conn.prepareStatement(" SELECT * FROM A3_ISSUE ");
					ResultSet rset = stmt.executeQuery();

					while (rset.next()) {
						Issue member = new Issue();
						member.setId(rset.getInt("ID"));
						member.setTitle(rset.getString("TITLE"));
						member.setDescription(rset.getString("DESCRIPTION"));
						member.setCreator(rset.getInt("CREATOR"));
						member.setResolver(rset.getInt("RESOLVER"));
						member.setVerifier(rset.getInt("VERIFIER"));
						temp.add(member);
						System.out.println("issue add");
					}

					stmt.close();
					if (conn == null)
						System.err.println("You are not connected to Oracle!");
					else {
						conn.close(); // close the connection again after usage!
						conn = null;
					}

				} catch (SQLException e) {
					System.out.println("In search method, SELECT * FROM A3_ISSUE fail");
				}

				Vector<Issue> tempRE = new Vector<Issue>();
				Iterator<Issue> ite = temp.iterator();
				while (ite.hasNext()) {
					Issue checkIssue = ite.next();
					int creator = checkIssue.getCreator();
					int resolver = checkIssue.getResolver();
					int verifier = checkIssue.getVerifier();
					if (creator == userId || resolver == userId || verifier == userId) {
						tempRE.add(checkIssue);
					}
				}
				Iterator<Issue> iteRE = tempRE.iterator();
				while (iteRE.hasNext()) {
					Issue checkIssue = iteRE.next();
					int creator = checkIssue.getCreator();
					int resolver = checkIssue.getResolver();
					int verifier = checkIssue.getVerifier();
					if (creator == searchID || resolver == searchID || verifier == searchID) {
						issue.add(checkIssue);
						System.out.println("one matched with name");
					}
					System.out.println("matching with name");
				}
			}

			if (!searchStringNoName.equals("")) {
				if (searchStringNoName.indexOf("|") == -1) {
					String pattern = ".*" + searchStringNoName + ".*";
					System.out.println(pattern);
					Pattern searchPattern = Pattern.compile(pattern);
					int num = 0;
					try {
						stmt = conn.prepareStatement(" SELECT * FROM A3_ISSUE ");
						ResultSet rset = stmt.executeQuery();

						while (rset.next()) {
							num++;
							Issue member = new Issue();
							member.setId(rset.getInt("ID"));
							member.setTitle(rset.getString("TITLE"));
							member.setDescription(rset.getString("DESCRIPTION"));
							member.setCreator(rset.getInt("CREATOR"));
							member.setResolver(rset.getInt("RESOLVER"));
							member.setVerifier(rset.getInt("VERIFIER"));
							temp.add(member);
							System.out.println("issue add");
						}

						stmt.close();
						if (conn == null)
							System.err.println("You are not connected to Oracle!");
						else {
							conn.close(); // close the connection again after
											// usage!
							conn = null;
						}

					} catch (SQLException e) {
						System.out.println("In search method with @Name@, SELECT * FROM A3_ISSUE fail");
					}

					if (num == 0)
						System.out.println("No entries found.");

					Vector<Issue> tempRE = new Vector<Issue>();
					Iterator<Issue> ite = temp.iterator();
					while (ite.hasNext()) {
						Issue checkIssue = ite.next();
						int creator = checkIssue.getCreator();
						int resolver = checkIssue.getResolver();
						int verifier = checkIssue.getVerifier();
						if (creator == userId || resolver == userId || verifier == userId) {
							tempRE.add(checkIssue);
						}
					}

					Vector<Issue> tempREName = new Vector<Issue>();
					Iterator<Issue> iteRE = tempRE.iterator();
					while (iteRE.hasNext()) {
						Issue checkIssue = iteRE.next();
						String title = checkIssue.getTitle();
						String description = checkIssue.getDescription();
						Matcher searchTitleMatcher = searchPattern.matcher(title);
						Matcher searchDesMatcher = searchPattern.matcher(description);
						if (searchTitleMatcher.matches() || searchDesMatcher.matches()) {
							tempREName.add(checkIssue);
							System.out.println("one matched with title or des");
						}
						System.out.println("matching with title or des");
					}
					Iterator<Issue> iteREName = tempREName.iterator();
					while (iteREName.hasNext()) {
						Issue checkIssue = iteREName.next();
						int creator = checkIssue.getCreator();
						int resolver = checkIssue.getResolver();
						int verifier = checkIssue.getVerifier();
						if (creator == searchID || resolver == searchID || verifier == searchID) {
							issue.add(checkIssue);
							System.out.println("one matched with name");
						}
						System.out.println("matching with name");
					}
				}

				if (searchStringNoName.indexOf("|") != -1) {
					int firstOrIndex = searchStringNoName.indexOf("|");
					int lastOrIndex = searchStringNoName.lastIndexOf("|");
					if (firstOrIndex == lastOrIndex) {
						String searchStringOne = searchStringNoName.substring(0, firstOrIndex);
						String searchStringTwo = searchStringNoName.substring(firstOrIndex + 1);
						String patterOne = ".*" + searchStringOne + ".*";
						String patterTwo = ".*" + searchStringTwo + ".*";
						System.out.println(patterOne + " " + patterTwo);
						Pattern searchPatternOne = Pattern.compile(patterOne);
						Pattern searchPatternTwo = Pattern.compile(patterTwo);

						int num = 0;
						try {
							stmt = conn.prepareStatement(" SELECT * FROM A3_ISSUE ");
							ResultSet rset = stmt.executeQuery();

							while (rset.next()) {
								num++;
								Issue member = new Issue();
								member.setId(rset.getInt("ID"));
								member.setTitle(rset.getString("TITLE"));
								member.setDescription(rset.getString("DESCRIPTION"));
								member.setCreator(rset.getInt("CREATOR"));
								member.setResolver(rset.getInt("RESOLVER"));
								member.setVerifier(rset.getInt("VERIFIER"));
								temp.add(member);
								System.out.println("issue add");
							}

							stmt.close();
							if (conn == null)
								System.err.println("You are not connected to Oracle!");
							else {
								conn.close(); // close the connection again
												// after
												// usage!
								conn = null;
							}

						} catch (SQLException e) {
							System.out.println("In search method, SELECT * FROM A3_ISSUE fail");
						}

						if (num == 0)
							System.out.println("No entries found.");

						Vector<Issue> tempRE = new Vector<Issue>();
						Iterator<Issue> ite = temp.iterator();
						while (ite.hasNext()) {
							Issue checkIssue = ite.next();
							int creator = checkIssue.getCreator();
							int resolver = checkIssue.getResolver();
							int verifier = checkIssue.getVerifier();
							if (creator == userId || resolver == userId || verifier == userId) {
								tempRE.add(checkIssue);
							}
						}

						Vector<Issue> tempREName = new Vector<Issue>();
						Iterator<Issue> iteRE = tempRE.iterator();
						while (iteRE.hasNext()) {
							Issue checkIssue = iteRE.next();
							String title = checkIssue.getTitle();
							String description = checkIssue.getDescription();
							Matcher searchTitleMatcherOne = searchPatternOne.matcher(title);
							Matcher searchTitleMatcherTwo = searchPatternTwo.matcher(title);
							Matcher searchDesMatcherOne = searchPatternOne.matcher(description);
							Matcher searchDesMatcherTwo = searchPatternTwo.matcher(description);
							if (searchTitleMatcherOne.matches() || searchTitleMatcherTwo.matches()
									|| searchDesMatcherOne.matches() || searchDesMatcherTwo.matches()) {
								tempREName.add(checkIssue);
								System.out.println("one matched");
							}
							System.out.println("matching");
						}
						Iterator<Issue> iteREName = tempREName.iterator();
						while (iteREName.hasNext()) {
							Issue checkIssue = iteREName.next();
							int creator = checkIssue.getCreator();
							int resolver = checkIssue.getResolver();
							int verifier = checkIssue.getVerifier();
							if (creator == searchID || resolver == searchID || verifier == searchID) {
								issue.add(checkIssue);
								System.out.println("one matched with name");
							}
							System.out.println("matching with name");
						}
					}
					if (firstOrIndex != lastOrIndex) {
						String[] divededString=searchStringNoName.split("\\|");
						int numOfDivededString = divededString.length;
						int count = 0;
						ArrayList<Pattern> patternList = new ArrayList();
						while(count<numOfDivededString){
							String pattern = ".*" + divededString[count] + ".*";
							System.out.println(pattern);
							Pattern searchPattern = Pattern.compile(pattern);
							patternList.add(searchPattern);
							count++;
						}
						
						int num = 0;
						try {
							stmt = conn.prepareStatement(" SELECT * FROM A3_ISSUE ");
							ResultSet rset = stmt.executeQuery();

							while (rset.next()) {
								num++;
								Issue member = new Issue();
								member.setId(rset.getInt("ID"));
								member.setTitle(rset.getString("TITLE"));
								member.setDescription(rset.getString("DESCRIPTION"));
								member.setCreator(rset.getInt("CREATOR"));
								member.setResolver(rset.getInt("RESOLVER"));
								member.setVerifier(rset.getInt("VERIFIER"));
								temp.add(member);
								System.out.println("issue add");
							}

							stmt.close();
							if (conn == null)
								System.err.println("You are not connected to Oracle!");
							else {
								conn.close(); // close the connection again after usage!
								conn = null;
							}

						} catch (SQLException e) {
							System.out.println("In search method, SELECT * FROM A3_ISSUE fail");
						}

						if (num == 0)
							System.out.println("No entries found.");

						Vector<Issue> tempRE = new Vector<Issue>();
						Iterator<Issue> ite = temp.iterator();
						while (ite.hasNext()) {
							Issue checkIssue = ite.next();
							int creator = checkIssue.getCreator();
							int resolver = checkIssue.getResolver();
							int verifier = checkIssue.getVerifier();
							if (creator == userId || resolver == userId || verifier == userId) {
								tempRE.add(checkIssue);
							}
						}

						Vector<Issue> tempREName = new Vector<Issue>();
						Iterator<Issue> iteRE = tempRE.iterator();
						while (iteRE.hasNext()) {
							Issue checkIssue = iteRE.next();
							String title = checkIssue.getTitle();
							String description = checkIssue.getDescription();
							ArrayList<Matcher> matcherList = new ArrayList();
							Iterator<Pattern> itePattern = patternList.iterator();
							while(itePattern.hasNext()){
								Matcher searchTitleMatcher = itePattern.next().matcher(title);
								matcherList.add(searchTitleMatcher);
							}
							Iterator<Pattern> itePatternRE = patternList.iterator();
							while(itePatternRE.hasNext()){
								Matcher searchDesMatcher = itePatternRE.next().matcher(description);
								matcherList.add(searchDesMatcher);
							}
							boolean matchCheckFlag = false;
							Iterator<Matcher> iteMatcher = matcherList.iterator();
							while(iteMatcher.hasNext()){
								Matcher matcher = iteMatcher.next();
								if(matcher.matches()){
									matchCheckFlag = true;
								}
							}
							if (matchCheckFlag) {
								tempREName.add(checkIssue);
								System.out.println("one matched");
							}
							System.out.println("matching");
						}
						
						Iterator<Issue> iteREName = tempREName.iterator();
						while (iteREName.hasNext()) {
								Issue checkIssue = iteREName.next();
								int creator = checkIssue.getCreator();
								int resolver = checkIssue.getResolver();
								int verifier = checkIssue.getVerifier();
								if (creator == searchID || resolver == searchID || verifier == searchID) {
									issue.add(checkIssue);
									System.out.println("one matched with name");
								}
								System.out.println("matching with name");
							}
					}
				}

			}

		}

		return issue;

	}
}
