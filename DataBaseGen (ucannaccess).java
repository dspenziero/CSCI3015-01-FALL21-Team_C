
/**
 * Write a description of DataBaseGen here.
 * 
 * @author David Spenziero
 * @author Hassan Ul Haq
 * @version 20/11/2021
 */
/**
 * Licensed under creative commons license 3.0.
 * Here's a url: https://creativecommons.org/licenses/by-sa/3.0/
 *
 *
 */
package projectTeamC;
import java.sql.*;
import java.util.*;


/**
 * Created by Paul Olsen, August 3rd, 2017.
 * Data generator for my altered version of the employee example postgres database.
 * It creates people then simulates their lives and interactions with a fictitious corporation.
 */
public class DataBaseGen {
    private int empNo;
    private String firstName;
    private String lastName;
    private GregorianCalendar birthDate;
    private GregorianCalendar hireDate;
    private int salary;
    private int title;
    private int departmentid;
    private GregorianCalendar lastMoveDate;

    private static String[] titles = new String[] {
            "Tech Lev 1",
            "Tech Lev 2",
            "Programmer Lev 1",
            "Programmer Lev 2",
            "Junior Architect",
            "Senior Architect",
            "Strategist"
    };

    private static String[] departments = new String[] {
            "BOAT",
            "HAMMER",
            "JOIST",
            "FROG",
            "OPERA",
            "ABSTRACT",
            "HELP",
            "RANDOM",
            "WORDS",
            "ARE",
            "FUN",
            "DELETE",
            "TABLE",
            "POSTGRESQL"
    };

    private static String[] departmentIDs = new String[departments.length];
    private static Integer[] departmentHeads = new Integer[departments.length];
    private static String databaseURL = "jdbc:ucanaccess://Database.accdb";

    public static void main(String[] args) {
        double probabilityOfBirthOrHire = (double)NumEmployees*3/(double)(365*70);
        double probabilityOfTitleChange = (double)NumEmployees*2/(double)(365*70);
        double probabilityOfMove = (double)NumEmployees*2/(double)(365*70);


        LinkedList<DataBaseGen> unhired = new LinkedList<DataBaseGen>();
        LinkedList<DataBaseGen> hired = new LinkedList<DataBaseGen>();
        for(int year = (2017-70); year <= 2017; year++) {
            for(int month = 1; month <= 12; month++) {
                @SuppressWarnings("unused")
				Calendar c = new GregorianCalendar(year, month, 1);
                for(int day = 1; day <= 27; day++) {
                    if(Math.random() < probabilityOfBirthOrHire) {
                        if (unhired.size() > 0 && oldEnough(unhired.peekFirst(), year, month, day)) {
                            DataBaseGen e = unhired.removeFirst();
                            hired.add(e);
                            if (month == 0) throw new RuntimeException("" + month);
                            e.hire(year, month, day);
                            e.setSalary(10000 + random.nextInt(50000), year, month, day);
                            e.setTitle(0, year, month, day);
                            e.setDepartmentID(random.nextInt(departments.length), year, month, day);

                        } else {
                            unhired.add(new DataBaseGen(year, month, day));
                        }
                    }
                    else if(Math.random() < probabilityOfTitleChange && hired.size() > 0) {
                        DataBaseGen e = hired.get(random.nextInt(hired.size()));
                        if(Math.random() > .9) {
                            if(e.title != 0) e.setTitle(e.title - 1, year, month, day);
                        } else if(e.title != titles.length - 1){
                            e.setTitle(e.title + 1, year, month, day);
                        }
                    }
                    else if(Math.random() < probabilityOfMove && hired.size() > 0) {
                        DataBaseGen e = hired.get(random.nextInt(hired.size()));
                        int did = random.nextInt(departments.length);
                        if(did != e.departmentid) {
                            e.setDepartmentID(did, year, month, day);
                        }
                    }
                }
            }
        }
    }

    private void hire(int year, int month, int day) {
        hireDate = new GregorianCalendar(year, month, day);
        lastMoveDate = hireDate;
        System.out.println("INSERT INTO employees VALUES (" +
                empNo +  ", " +
                string(birthDate) + ", '" +
                firstName + "', '" +
                lastName + "', " +
                string(hireDate) + ");");
        try {		
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to " + databaseURL);
			
			String sql = "INSERT INTO employees (emp_no, birthDate, FirstName, LastName, hireDate) VALUES (?,?,?,?,?)"; 
			PreparedStatement preSql = connection.prepareStatement(sql);
			preSql.setInt(1, empNo);
			preSql.setDate(2, java.sql.Date.valueOf(string(birthDate)));
		
			preSql.setString(3, firstName);
			preSql.setString(4, lastName);
			preSql.setDate(5, java.sql.Date.valueOf(string(hireDate)));
			preSql.executeUpdate();
			connection.close();
        }
        catch (SQLException e) {
        	e.printStackTrace();			
        }
    }
    
    
    private static String string(GregorianCalendar date) {
        return  (date.get(GregorianCalendar.YEAR) + 1) + "-" +
                (date.get(GregorianCalendar.MONTH) + 1) + "-" +
                (date.get(GregorianCalendar.DAY_OF_MONTH) + 1);
    }

    private static boolean oldEnough(DataBaseGen employee, int year, int month, int day) {
        GregorianCalendar gc = (GregorianCalendar)employee.birthDate.clone();
        gc.add(GregorianCalendar.YEAR, 20);
        return gc.before(new GregorianCalendar(year, month, day));
    }

    private static Random random = new Random();
    private static int NumEmployees = 100;
    private static int startEmployeeID = 54321;

    public DataBaseGen(int year, int month, int day) {
        empNo = ++startEmployeeID;
        firstName = randomFirstName();
        lastName = randomLastName();
        birthDate = new GregorianCalendar(year, month, day);
    }

    public void setSalary(int amount, int year, int month, int day) {
        GregorianCalendar today = new GregorianCalendar(year, month, day);
        this.salary = amount;
        System.out.println("INSERT INTO salaries VALUES ('" +
                empNo + "', " +
                salary + ", " +
                string(today) + ");"
        );
        try {
        	Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to " + databaseURL);
			
			String sql = "INSERT INTO salaries (emp_no, salary, from_date) VALUES (?,?,?)"; 
			PreparedStatement preSql = connection.prepareStatement(sql);
			preSql.setInt(1, empNo);
			preSql.setInt(2, salary);
			preSql.setDate(3, java.sql.Date.valueOf(string(today)));
			preSql.executeUpdate();
			connection.close();
        }
        catch (SQLException e) {
        	e.printStackTrace();			
        }
    }

    public void setTitle(int title, int year, int month, int day) {
        GregorianCalendar today = new GregorianCalendar(year, month, day);
        this.title = title;
        System.out.println("INSERT INTO titles VALUES ( '" +
                empNo + "', '" +
                titles[title] + "', " +
                string(today) + ");"
        );
        try {		
        	Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to " + databaseURL);
			
			String sql = "INSERT INTO titles (emp_no, title, from_date) VALUES (?,?,?)"; 
			PreparedStatement preSql = connection.prepareStatement(sql);
			preSql.setInt(1, empNo);
			preSql.setString(2, titles[title]);
			preSql.setDate(3, java.sql.Date.valueOf(string(today)));
			preSql.executeUpdate();
			connection.close();
        }
        catch (SQLException e) {
        	e.printStackTrace();			
        }
    }

    private String randomFirstName() {
        String[] names = { "Alice",
            "Bob",
            "Charlotte",
            "Dagwood",
            "Edward",
            "Felicity",
            "Garry",
            "Heather",
            "Ivan",
            "Jada",
            "Kyle",
            "Lindsay",
            "Mary",
            "Nathan",
            "Olivia",
            "Paul",
            "Quentin",
            "Rachel",
            "Samuel",
            "Tanya",
            "Ursula",
            "Vanessa",
            "William",
            "Xander",
            "Yvonne",
            "Zelda",
            "Konstantin",
            "Kitty",
            "Anna"
        };
        return names[random.nextInt(names.length)];
    }

    private String randomLastName() {
        String[] names = new String[] {
                "Anderson",
                "Belleci",
                "Cameron",
                "Diaz",
                "Einstein",
                "Feynman",
                "Gyser",
                "Hyte",
                "Jordan",
                "Karamazov",
                "Linderman",
                "Mason",
                "Oblansky",
                "Pierce",
                "Raskolnikov",
                "Scherbatsky",
                "Tileman",
                "Urx",
                "Vanderhund",
                "Waxman",
                "Yewbeam",
        };
        return names[random.nextInt(names.length)];
    }

    public void setDepartmentID(int departmentid, int year, int month, int day) {
        moveOut(year, month, day);
        this.departmentid = departmentid;
        moveIn(year, month, day);
        GregorianCalendar gc = new GregorianCalendar(year, month, day);
        System.out.println("INSERT INTO works_in VALUES (" +
                empNo + "', '" +
                departmentIDs[departmentid] + "', " +
                string(gc) + ", " +
                "null);"
        );
        
        try {		
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to " + databaseURL);				
			String sql = "INSERT INTO works_in VALUES (emp_no, department_ID, from_date, to_date) VALUES (?,?,?,?)"; 
			PreparedStatement preSql = connection.prepareStatement(sql);
			preSql.setInt(1, empNo);
			preSql.setString(2, departmentIDs[departmentid]);
			preSql.setDate(3, java.sql.Date.valueOf(string(gc)));
			preSql.setDate(4, null);
			preSql.executeUpdate();
			connection.close();
        }
        catch (SQLException e) {
        	e.printStackTrace();			
        }
    }

    private void moveOut(int year, int month, int day) {
        if(departmentHeads[departmentid] != null && departmentHeads[departmentid] == empNo) {
            System.out.println("UPDATE dept_manager SET to_date = " + string(new GregorianCalendar(year, month, day)) +
                    " WHERE emp_no = " + empNo + " AND from_date = " + string(lastMoveDate) + ";"
            );
            try {		
            	Connection connection = DriverManager.getConnection(databaseURL);
    			System.out.println("Connected to " + databaseURL);
    			
    			String sql = "UPDATE dept_manager SET to_date = ? WHERE emp_no = ? AND from_date = ?"; 
    			PreparedStatement preSql = connection.prepareStatement(sql);
    			preSql.setDate(1, java.sql.Date.valueOf(string(new GregorianCalendar(year, month, day))));
    			preSql.setInt(2, empNo);
    			preSql.setDate(3, java.sql.Date.valueOf(string(lastMoveDate)));
    			preSql.executeUpdate();
    			connection.close();
            }
            catch (SQLException e) {
            	e.printStackTrace();			
            }
            departmentHeads[departmentid] = null;
        }
        System.out.println("UPDATE works_in SET to_date = " + string(new GregorianCalendar(year, month, day)) +
                " WHERE emp_no = " + empNo + " AND from_date = " + string(lastMoveDate) + ";");
                try {		
        			Connection connection = DriverManager.getConnection(databaseURL);
        			System.out.println("Connected to " + databaseURL);
        			
        			String sql2 = "UPDATE works_in SET to_date = ? WHERE emp_no = ? AND from_date = ?"; 
        			PreparedStatement preSql2 = connection.prepareStatement(sql2);
        			preSql2.setDate(1, java.sql.Date.valueOf(string(new GregorianCalendar(year, month, day))));
        			preSql2.setInt(2, empNo);
        			preSql2.setDate(3, java.sql.Date.valueOf(string(lastMoveDate)));
        			preSql2.executeUpdate();
        			connection.close();
                }
                catch (SQLException e) {
                	e.printStackTrace();			
                }
    }

    private void moveIn(int year, int month, int day) {
        GregorianCalendar gc = new GregorianCalendar(year, month, day);
        lastMoveDate = gc;
        String id = String.format("%04d", departmentid);
        if(departmentIDs[departmentid] == null) {
        	 try {		
     			Connection connection = DriverManager.getConnection(databaseURL);
     			System.out.println("Connected to " + databaseURL);
     			
     			String sql = "INSERT INTO departments VALUES ('" +
                        id + "', '" +
                        departments[departmentid] + "');";
     			Statement statement = connection.createStatement();
     			statement.executeUpdate(sql);
     			statement.close();
     			connection.close();
             }
             catch (SQLException e) {
             	e.printStackTrace();			
             }
            System.out.println("INSERT INTO departments VALUES ('" +
                    id + "', '" +
                    departments[departmentid] + "');"
            );
            departmentIDs[departmentid] = id;
        }
        if(departmentHeads[departmentid] == null) {
        	try {		
     			Connection connection = DriverManager.getConnection(databaseURL);
     			System.out.println("Connected to " + databaseURL);
     			
     			String sql2 = "INSERT INTO dept_manager VALUES (emp_no, department_ID, from_date, to_date) VALUES (?,?,?,?)"; 
    			PreparedStatement preSql = connection.prepareStatement(sql2);
    			preSql.setInt(1, empNo);
    			preSql.setString(2, departmentIDs[departmentid]);
    			preSql.setDate(3, java.sql.Date.valueOf(string(gc)));
    			preSql.setDate(4, null);
    			preSql.executeUpdate(sql2);
    			connection.close();
             }
             catch (SQLException e) {
             	e.printStackTrace();			
             }
            System.out.println("INSERT INTO dept_manager VALUES ('" +
                    departmentIDs[departmentid] + "', " +
                    empNo + ", " +
                    string(gc) + ", " +
                    "null);"
            );
            departmentHeads[departmentid] = empNo;
        }
    }
}
