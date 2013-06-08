/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shared.message.FriendshipRequest;

/**
 *
 * @author Ahmed Karasan
 */
public class DatabaseOperator implements DBOperator {
    
    private String mConnectionUrl;
    private String mDBUser;
    private String mDBPassword;

    public DatabaseOperator() {
    	this("jdbc:mysql://localhost/mysql", "root", "");
    }

    public DatabaseOperator(String url, String user, String passw) {
        mConnectionUrl = url;
        mDBUser = user;
        mDBPassword = passw;
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(java.lang.ClassNotFoundException cnfe){
            System.out.println("Class Not Found - " + cnfe.getMessage());
        }      
    }

    @Override
    public boolean registerNewUser(String userName, String password) {
        try {
            Connection connection;
            PreparedStatement preparedStatement;
            boolean isSuccessfull;
            int c;

            connection = DriverManager.getConnection(mConnectionUrl, mDBUser, mDBPassword);

            preparedStatement = connection.prepareStatement("INSERT INTO db.users values (default, ?, ?)");

            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            c = preparedStatement.executeUpdate();

            if (c > 0)
                isSuccessfull = true;
            else
                isSuccessfull = false;

            return isSuccessfull;
            
        } 
        catch (Exception e) {
            System.out.println("Exception ocurred!");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<Long, String> getUserFriends(String userName, String password) {
        try {           
            Connection connection;
            ResultSet resultFromUsers;
            ResultSet resultFromFriends;
            Statement statement;
            Map<Long, String> friendMap = new HashMap<>();

            connection = DriverManager.getConnection(mConnectionUrl, mDBUser, mDBPassword);

            statement = connection.createStatement();
            resultFromUsers = statement.executeQuery("SELECT id FROM db.users WHERE username='" + userName
                    + "'" + " AND " + " password='" + password + "'");

            if (!resultFromUsers.first())
                return null;
            
            long id = resultFromUsers.getLong(1);
            
            friendMap.put(id, userName);

            statement = connection.createStatement();
            resultFromFriends = statement.executeQuery("SELECT * FROM db.friends WHERE userId1='"
                    + String.valueOf(id) + "'" + " OR userId2='" + String.valueOf(id) + "'");

            while (resultFromFriends.next()) {
                long tid;

                if (resultFromFriends.getLong(1) == id)
                    tid = resultFromFriends.getLong(2);
                else
                    tid = resultFromFriends.getLong(1);

                statement = connection.createStatement();
                resultFromUsers = statement.executeQuery("SELECT username FROM db.users WHERE id='"
                        + tid + "'");
                resultFromUsers.first();
                friendMap.put(tid, resultFromUsers.getString(1));
            }
            
            connection.close();

            return friendMap;
            
        } catch (Exception e) {
            System.out.println("Exception ocurred!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean addFriendship(long user_1_id, long user_2_id) {
        try {
            if (user_1_id == user_2_id) 
                return false;
            
            Connection connection;
            Statement statement;
            ResultSet result;
            PreparedStatement preparedStatement;
            boolean isSuccessfull;
            int c;

            connection = DriverManager.getConnection(mConnectionUrl, mDBUser, mDBPassword);

            statement = connection.createStatement();
            
            result = statement.executeQuery("SELECT * FROM db.friends WHERE userId1=" +
                    user_1_id + " AND userId2=" + user_2_id + " OR userId1=" + user_2_id +
                    " AND userId2=" + user_1_id);
                   
            if (result.first())  
                return false;      
            
            preparedStatement = connection.prepareStatement("INSERT INTO db.friends values (?, ?)");

            preparedStatement.setString(1, String.valueOf(user_1_id));
            preparedStatement.setString(2, String.valueOf(user_2_id));
            c = preparedStatement.executeUpdate();

            if (c > 0) {
                isSuccessfull = true;
            } 
            else {
                isSuccessfull = false;
            }

            connection.close();

            return isSuccessfull;
            
        } catch (Exception e) {
            System.out.println("Exception ocurred!");
            return false;
        } 
    }
    
    /* Gets user id from database.
     * returns -1 if user not found 
     */
    
    public long getUserId(String userName)
    {
        try {
            Connection connection;
            Statement statement;
            ResultSet result;
            long id;
         
            connection = DriverManager.getConnection(mConnectionUrl, mDBUser, mDBPassword);

            statement = connection.createStatement();
            result = statement.executeQuery
                    ("SELECT id FROM db.users WHERE username='" + 
                    userName + "'");
            
            if(result.first())
                id = result.getLong(1);
            else 
                id = -1;
            
            connection.close();
            
            return id;
          
        } 
        catch (Exception e) {
            System.out.println("Exception ocurred!");
            return -1;
        }
    }
    
    @Override
    public Map<Long, String> findUsers(String criteria)
    {
       try {           
            Connection connection;
            ResultSet resultFromUsers;
            Statement statement;
            Map<Long, String> criteriaMap = new HashMap<>();

            connection = DriverManager.getConnection(mConnectionUrl, mDBUser, mDBPassword);
                    
            statement = connection.createStatement();
            resultFromUsers = statement.executeQuery("SELECT username FROM db.users");
                        
            
            if (!resultFromUsers.first())
                return null;
            
            while(resultFromUsers.next()) {
                String uname;
                uname = resultFromUsers.getString(1);
                
                if (uname.contains(criteria)) {
                    long id = getUserId(uname);     
                    criteriaMap.put(id, uname);
                }
                    
            }
            
            connection.close();

            return criteriaMap;
    }
       catch (Exception e) {
            System.out.println("Exception ocurred!");
            return null;
        }
    }

	@Override
	public void addFriendshipRequest(long senderId, String senderName,
									long receiverId, String receiverName
									, String msg, Date date
	) {
           try {
            Connection connection;
            PreparedStatement preparedStatement;
         

            connection = DriverManager.getConnection(mConnectionUrl, mDBUser, mDBPassword);

            preparedStatement = connection.prepareStatement("INSERT INTO db.waiting_friendship_requests values (?, ?, ?, ?, ?, ?)");

            preparedStatement.setString(1, String.valueOf(senderId));
            preparedStatement.setString(2, senderName);
            preparedStatement.setString(3, String.valueOf(receiverId));
            preparedStatement.setString(4, String.valueOf(receiverName));
            preparedStatement.setString(5, msg);
            preparedStatement.setString(3, String.valueOf(date));
            
            preparedStatement.executeUpdate();
            
            connection.close();
           
           }
          catch (Exception e) {
            System.out.println("Exception ocurred!");
            }	
	}

	@Override
	public void deleteFriendshipRequests(long userId1, long userId2) {
		// TODO Auto-generated method stub
            
            try {           
            Connection connection;
            Statement statement;
           
            connection = DriverManager.getConnection(mConnectionUrl, mDBUser, mDBPassword);
                    
            statement = connection.createStatement();
            statement.executeQuery("DELETE FROM db.waiting_friendship_requests WHERE sender_id=" +
                    userId1 + " AND receiver_id=" + userId2 );
            
            connection.close();
            }
             catch (Exception e) {
            System.out.println("Exception ocurred!");
            }	
	}

	@Override
	public List<FriendshipRequest> getFriendshipRequestsForUser(long userId) {
        try {           
            Connection connection;
            Statement statement;
            ResultSet resultFromUsers;
            List <FriendshipRequest> list = new ArrayList <>();
            connection = DriverManager.getConnection(mConnectionUrl, mDBUser, mDBPassword);
            
            statement = connection.createStatement();
            resultFromUsers = statement.executeQuery("SELECT * FROM db.db.waiting_friendship_requests WHERE receiver_id=" +
                    userId);
                        
            if (!resultFromUsers.first())
                return null;
            
            while(resultFromUsers.next()) {
                FriendshipRequest request = new FriendshipRequest(resultFromUsers.getLong(3),
                 resultFromUsers.getLong(1), resultFromUsers.getString(5), resultFromUsers.getDate(6));
                               
                list.add(request);
            }
            
            connection.close();
   
            return list;
            }
             catch (Exception e) {
            System.out.println("Exception ocurred!");
            return null;
            }	
	}
}
