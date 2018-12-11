
package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class dbconnection {
    
    private static Connection connDB;
    
    public dbconnection(){}
    
    public static void init(){
        System.out.println("Connecting to the database");
        try{
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            
            connDB = DriverManager.getConnection("jdbc:derby://localhost:1527/343", "APP", "APP");;

        }catch (ClassNotFoundException ce){
            System.out.println("Cannot find the right class.  Did you remember to add the proper library to your Run Configuration?");
            ce.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
}
    }
    
    //Returns Connection
    public static Connection getConn(){
    
        return connDB;
    }
    
    //Closes connections
    public static void closeConn(){
        try{
            connDB.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally{
            System.out.println("Connection closed.");
        }
    }
    
}
