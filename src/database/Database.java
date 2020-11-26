package database;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import api.KakaoAPI;
import parser.Item;

public class Database {
    File file;
    FileReader fr;
    BufferedReader in;
    String[] attrName;
    String[] columnsContent;
    private final static String urldb = "jdbc:postgresql://127.0.0.1:5432/postgres";
    private final static String user = "postgres";
    private final static String pw = "0223";
	
    public Database() throws Exception {
	       // write your code here
	       createTable(connect());
	       insert_product(connect());
	       //KakaoAPI kakao = new KakaoAPI();
	       //System.out.println(kakao.addrToCoord("127.043784", "37.279509", "1000"));
	       //Clawler clawler = new Clawler();
	       //clawler.clawler_main();    
	}
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(urldb, user, pw);
            System.out.println("Connected to the PostgreSQL server successfully.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void createTable(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("drop table if exists Client;");
        statement.executeUpdate("drop table if exists Store;");
        statement.executeUpdate("drop table if exists Product;");
        statement.executeUpdate("drop table if exists Applied;");
        statement.executeUpdate("create table if not exists Client(userID varchar(20),pName varchar(20),locX double precision,locY double precision,primary key(userID));");
        statement.executeUpdate("create table if not exists Store(storeID varchar(20), bName varchar(20), sName varchar(20), sAddress varchar(40), pURL varchar(40), locX double precision,locY double precision, primary key(storeID));");
        statement.executeUpdate("create table if not exists Product(pID varchar(40), bName varchar(40), pName varchar(40), price varchar(40), eName varchar(40), primary key(pID));");
        statement.executeUpdate("create table if not exists Applied(userID varchar(20), storeID varchar(20), distance double precision, primary key(userID,storeID));");
    }
    private void loadFromCSV(ArrayList<Item> list) throws IOException {

        File csv = new File("Type_All.csv");
        BufferedReader br = new BufferedReader(new FileReader(csv));
        String line = "";
        int row =0 ,i;
        while ((line = br.readLine()) != null){
            String[] splited = line.split(",", -1);
            for(int j = 0; j<splited.length;j++)
            {
                if(splited[j].contains("'")){
                    String[] temp= splited[j].split("'",-1);
                    for (int k = 0; k<temp.length;k++)
                    {
                        if(k == 0)
                        {
                            splited[j] = temp[k];
                        }
                        else
                        {
                            splited[j]+= temp[k];
                        }
                    }
                }
            }
            list.add(new Item(splited[0],splited[1],splited[2],splited[3]));
        }
        System.out.println("Database load complete!!");
    }
    
    public void insert_product(Connection conn) throws Exception {
        ArrayList<Item> t1_item = new ArrayList<Item>();
        Statement statement = conn.createStatement();
        loadFromCSV(t1_item);
        for(int i = 1;i<t1_item.size();i++)
        {
            statement.executeUpdate("Insert into Product values("+(i)+",'"+t1_item.get(i).getBrand()+"','"+t1_item.get(i).getName()+"','"+t1_item.get(i).getPrice()+"','"+t1_item.get(i).getEvent()+"');");
        }
    }
   
}