package me.taon2.chatfilter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

import me.taon2.chatfilter.commands.ChatFilterCommand;
import me.taon2.chatfilter.listeners.PlayerMessageListener;

public class ChatFilter extends JavaPlugin {
	
	private ArrayList<String> censoredWords = new ArrayList<String>();
	private ArrayList<String> neverCensoredWords = new ArrayList<String>();
	private Connection connection, con;
    private String host, database, username, password, jdbcDriver, dbAddress;
    private int port;
    private Statement statement;
    
	public void onEnable() {
		host = "localhost";
		port = 3306;
		database = "practice";
		username = "root";
		password = "practice";
		
		//checks if these data tables exist, if they don't then they will be created.
		try {
			ArrayList<ArrayList<String>> tables = new ArrayList<ArrayList<String>>();
			ArrayList<String> table1 = new ArrayList<String>(); table1.add("censoredwordstable"); table1.add("censoredwords");
			ArrayList<String> table2 = new ArrayList<String>(); table2.add("nevercensoredwordstable"); table2.add("nevercensoredwords");
			
			for (ArrayList<String> tempList : tables) {
				for (int i = 0; i <= tempList.size()-2; i++) {
					openConnection();
					tableExist(tempList.get(i), tempList.get(i+1));
				}
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//loads the data from the database into the plugin.
				try {     
					openConnection();
					statement = connection.createStatement();
		            loadCensoredData();
		            loadNeverCensoredData();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				this.getCommand("cf").setExecutor(new ChatFilterCommand(this));
				this.getServer().getPluginManager().registerEvents(new PlayerMessageListener(this), this);
	}
	
	public void onDisabled() {
		try {
			closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//checks if these data tables exist, if they don't then they will be created.
	public boolean tableExist(String tableName, String columnName) throws Exception {
	    boolean tExists = false;
	    try (ResultSet rs = connection.getMetaData().getTables(null, null, tableName, null)) {
	        while (rs.next()) { 
	            String tName = rs.getString("TABLE_NAME");
	            if (tName != null && tName.equals(tableName)) {
	                tExists = true;
	                break;
	            }
	        }
	    }
	    if (!tExists) {
	    	createTable(tableName, columnName);
	    }
	    return tExists;
	}
	
	//creates missing tables, if any.
	public void createTable(String tableName, String columnName) throws Exception {
        try {
        	jdbcDriver = "com.mysql.jdbc.Driver";
        	dbAddress = ("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database);
            Class.forName(jdbcDriver).newInstance();
            con = DriverManager.getConnection(dbAddress, username, password);
            Statement stmt = con.createStatement();
            String sql = ("CREATE TABLE IF NOT EXISTS " + tableName + " (id int NOT NULL AUTO_INCREMENT, " + columnName + " varchar(255), PRIMARY KEY(id))");
            stmt.executeUpdate(sql);
            System.out.println("Created table " + tableName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
}
	
	public void openConnection() throws Exception {
	    if (connection != null && !connection.isClosed()) {
	        return;
	    }
	 
	    synchronized (this) {
	        if (connection != null && !connection.isClosed()) {
	            return;
	        } 
	        Class.forName("com.mysql.jdbc.Driver");
	        connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
	    }
	}
	
	public void closeConnection() throws Exception {
		connection.close();
	}
		
	//loads the data for censored words.
	public void loadCensoredData() throws SQLException {
		ArrayList<String> tempList = new ArrayList<String>();
		ResultSet result = getStatement().executeQuery("SELECT * FROM censoredwordstable");
		while (result.next()) {
		    String word = result.getString("censoredwords");
		    tempList.add(word);
		}
		setCensoredWords(tempList);
	}
	
	//loads the data for never censored words.
	public void loadNeverCensoredData() throws SQLException {
		ArrayList<String> tempList = new ArrayList<String>();
		ResultSet result = getStatement().executeQuery("SELECT * FROM nevercensoredwordstable");
		while (result.next()) {
		    String word = result.getString("nevercensoredwords");
		    tempList.add(word);
		}
		setNeverCensoredWords(tempList);
	}

	public ArrayList<String> getNeverCensoredWords() {
		return neverCensoredWords;
	}

	public void setNeverCensoredWords(ArrayList<String> neverCensoredWords) {
		this.neverCensoredWords = neverCensoredWords;
	}

	public ArrayList<String> getCensoredWords() {
		return censoredWords;
	}

	public void setCensoredWords(ArrayList<String> censoredWords) {
		this.censoredWords = censoredWords;
	}
	
	public void addCensoredWord(String word) {
		censoredWords.add(word);
	}
	
	public void addNeverCensoredWord(String word) {
		neverCensoredWords.add(word);
	}
	
	public void removeCensoredWord(String word) {
		censoredWords.remove(word);
	}
	
	public void removeNeverCensoredWord(String word) {
		neverCensoredWords.remove(word);
	}

	public Statement getStatement() {
		return statement;
	}
}
