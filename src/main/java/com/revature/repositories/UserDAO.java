package com.revature.repositories;

import com.revature.exceptions.NoSuchUserException;
import com.revature.exceptions.RegistrationUnsuccessfulException;
import com.revature.exceptions.UsernameNotUniqueException;
import com.revature.models.Role;
import com.revature.models.User;
import com.revature.services.AuthService;
import com.revature.util.ConnectionFactory;
import java.sql.*;
import java.util.Optional;
import java.util.OptionalInt;

public class UserDAO {
    Connection con = ConnectionFactory.getInstance().getConnection();
    Optional<User> resultOp = Optional.empty();
    static int rowChecker=0;
    /**
     * Should retrieve a User from the DB with the corresponding username or an empty optional if there is no match.
     */
    public Optional<User> getByUsername(String username) {
        User temp = new User();
        //SQL query to retrieve users with matching username
        String query = "SELECT * FROM ers_users WHERE ers_username=?";
        try {
            //initialize prepared statement set ? to username value passed in, then execute the query and store it in a result set
            PreparedStatement preparedStatement= con.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet= preparedStatement.executeQuery();
            //Load result set values into a temp user
            while(resultSet.next()) {
                temp.setId(resultSet.getInt(1));
                temp.setUsername(resultSet.getString(2));
                temp.setPassword(resultSet.getString(3));
                temp.setFirstName(resultSet.getString(4));
                temp.setLastName(resultSet.getString(5));
                temp.setEmail(resultSet.getString(6));
                temp.setRoleID(resultSet.getInt(7));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //SQL query to retrieve user roles with matching role id
        String query2 = "SELECT * FROM ers_user_roles WHERE ers_user_role_id=?";
        //initialize prepared statement set ? to the RoleID value stored in temp User, then execute the query and store it in a result set
        try {
            PreparedStatement preparedStatement= con.prepareStatement(query2);
            preparedStatement.setInt(1,temp.getRoleID());
            ResultSet resultSet=preparedStatement.executeQuery();
            //Convert role string from DB into Role, then store it into temp User
            while(resultSet.next()){
                String role = resultSet.getString(2);
                switch(role){
                    case "Employee":
                        temp.setRole(Role.EMPLOYEE);
                        break;
                    case "Finance Manager":
                        temp.setRole(Role.FINANCE_MANAGER);
                        break;
                }
            }
            //Store user into empty Optional then return it
            resultOp= Optional.ofNullable(temp);
            return resultOp;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultOp;
    }


    /**
     * Should retrieve a User from the DB with the corresponding user id or an empty optional if there is no match.
     */
    public Optional<User> getByUserID(int Id) {
        User temp = new User();
        //SQL query to retrieve users with matching username
        String query = "SELECT * FROM ers_users WHERE ers_users_id=?";
        try {
            //initialize prepared statement set ? to user id value passed in, then execute the query and store it in a result set
            PreparedStatement preparedStatement= con.prepareStatement(query);
            preparedStatement.setInt(1, Id);
            ResultSet resultSet= preparedStatement.executeQuery();
            //Load result set values into a temp user
            while(resultSet.next()) {
                temp.setId(resultSet.getInt(1));
                temp.setUsername(resultSet.getString(2));
                temp.setPassword(resultSet.getString(3));
                temp.setFirstName(resultSet.getString(4));
                temp.setLastName(resultSet.getString(5));
                temp.setEmail(resultSet.getString(6));
                temp.setRoleID(resultSet.getInt(7));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //SQL query to retrieve user roles with matching role id
        String query2 = "SELECT * FROM ers_user_roles WHERE ers_user_role_id=?";
        //initialize prepared statement set ? to the RoleID value stored in temp User, then execute the query and store it in a result set
        try {
            PreparedStatement preparedStatement= con.prepareStatement(query2);
            preparedStatement.setInt(1,temp.getRoleID());
            ResultSet resultSet=preparedStatement.executeQuery();
            //Convert role string from DB into Role, then store it into temp User
            while(resultSet.next()){
                String role = resultSet.getString(2);
                switch(role){
                    case "Employee":
                        temp.setRole(Role.EMPLOYEE);
                        break;
                    case "Finance Manager":
                        temp.setRole(Role.FINANCE_MANAGER);
                        break;
                }
            }
            //Store user into empty Optional then return it
            resultOp= Optional.ofNullable(temp);
            return resultOp;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultOp;
    }













    /**
     * <ul>
     *     <li>Should Insert a new User record into the DB with the provided information.</li>
     *     <li>Should throw an exception if the creation is unsuccessful.</li>
     *     <li>Should return a User object with an updated ID.</li>
     * </ul>
     */
    public User create(User userToBeRegistered) {

        //SQL query to store User Role ID and Role into DB
        String query2 ="INSERT INTO ers_user_roles (user_role) VALUES (?)";
        try {
            //Set values in SQL statement to Role ID and Role of User that was passed in
            PreparedStatement preparedStatement=con.prepareStatement(query2);
            preparedStatement.setString(1, userToBeRegistered.getRole().toString());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        //SQL query to store User information to DB
        String query ="INSERT INTO ers_users(ers_username, ers_password, user_first_name, user_last_name, user_email) VALUES (?, ?, ?, ?, ?)";
        try {
            //Stores prepared SQL statement into st
            PreparedStatement st = con.prepareStatement(query);
            //sets vales called sent into prepared statement
            st.setString(1, userToBeRegistered.getUsername());
            st.setString(2, userToBeRegistered.getPassword());
            st.setString(3, userToBeRegistered.getFirstName());
            st.setString(4, userToBeRegistered.getLastName());
            st.setString(5, userToBeRegistered.getEmail());

            rowChecker=st.executeUpdate();

        }catch (SQLException e) {
            System.out.println("Couldn't add user \n"+ e.getMessage() +"\n"+ e.getErrorCode());

            throw new RegistrationUnsuccessfulException("Error occured when registering account");
        }
        String query3 = "SELECT ers_users_id FROM ers_users WHERE ers_username=?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query3);
            preparedStatement.setString(1,userToBeRegistered.getUsername());
            ResultSet resultSet= preparedStatement.executeQuery();
            while(resultSet.next()){
                userToBeRegistered.setId(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RegistrationUnsuccessfulException("Error occured when registering account");
        }
        String query4 ="UPDATE ers_users SET user_role_id = ers_user_roles.ers_user_role_id FROM ers_user_roles WHERE ers_users_id = ers_user_roles.ers_user_role_id";
        String query5="SELECT user_role_id FROM ers_users WHERE ers_username=?";
        try {
            PreparedStatement preparedStatement= con.prepareStatement(query4);

            preparedStatement.executeUpdate();
            preparedStatement= con.prepareStatement(query5);
            preparedStatement.setString(1, userToBeRegistered.getUsername());
            ResultSet resultSet= preparedStatement.executeQuery();
            while(resultSet.next()){
                userToBeRegistered.setRoleID(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RegistrationUnsuccessfulException("Error occured when registering account");
        }


        return userToBeRegistered;
    }

    public void delete(User userToBeDeleted){
        String deleteRole="DELETE FROM ers_user_roles WHERE ers_user_role_id=?";
        String deleteUser="DELETE FROM ers_users WHERE ers_users_id=?";
        try {
            PreparedStatement deleter= con.prepareStatement(deleteUser);
            deleter.setInt(1, userToBeDeleted.getId());
            deleter.executeUpdate();

            deleter= con.prepareStatement(deleteRole);
            deleter.setInt(1, userToBeDeleted.getRoleID());
            deleter.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User update(User userToUpdate){
        String updateRole="UPDATE ers_user_roles SET user_role=? WHERE ers_user_role_id=?";
        String updateUser="UPDATE ers_users SET ers_username=?, ers_password=?, user_first_name=?, user_last_name=?, user_email=? WHERE ers_users_id=?";
        try {
            PreparedStatement updater= con.prepareStatement(updateRole);
            updater.setString(1, userToUpdate.getRole().toString());
            updater.setInt(2, userToUpdate.getRoleID());
            updater.executeUpdate();

            updater= con.prepareStatement(updateUser);
            updater.setString(1, userToUpdate.getUsername());
            updater.setString(2, userToUpdate.getPassword());
            updater.setString(3, userToUpdate.getFirstName());
            updater.setString(4, userToUpdate.getLastName());
            updater.setString(5, userToUpdate.getEmail());
            updater.setInt(6, userToUpdate.getId());
            updater.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating user. User may not exist");
            e.printStackTrace();
            throw new NoSuchUserException();
        }
        return userToUpdate;
    }
}
