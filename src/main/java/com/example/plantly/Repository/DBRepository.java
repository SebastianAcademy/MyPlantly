package com.example.plantly.Repository;

import com.example.plantly.Domain.Plant;
import com.example.plantly.Domain.User;
import com.example.plantly.Domain.UserPlant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Queries to the database

@Component
public class DBRepository implements PlantyDBRepository {

    //@SuppressWarnings("SpringJavaAutowiringInspection")

    @Autowired
    private DataSource dataSource;


    @Override
    public boolean userExists(String email, String password) {
        List<User> getAllUsers = getAllUsers();
        for(User u: getAllUsers) {
            if(u.getEmail().equals(email) && u.getPassword().equals(password))
                return true;
        }
        return false;
    }

    @Override
    public boolean addUser(String email, String firstname, String lastname, String password) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO users (email, firstname, lastname, password) values (?,?,?,?) ", new String[]{"UserID"}) ) {
            ps.setString(1, email);
            ps.setString(2, firstname);
            ps.setString(3, lastname);
            ps.setString(4, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
        }
        return false;
    }

    public List<User> getAllUsers() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("Select * From Users")) {
            List<User> users = new ArrayList<>();
            while (rs.next()) users.add(rsUser(rs));
            return users;
        } catch (SQLException e) {
            throw new PlantyRepositoryException(e);
        }
    }

    private User rsUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("UserId"), rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Email"), rs.getString("Password"));
    }

    public User getCurrentUser(String email, String password) {
        List<User> getAllUsers = getAllUsers();
        for(User u: getAllUsers) {
            if(u.getEmail().equals(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }
    @Override
    public void changePassword(int userId, String newPassword){
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("Update Users SET Password = ? WHERE UserID = ?")) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }catch(SQLException e){
            System.out.println("Change password:" + e.getMessage());
        }
    }

    @Override
    public List<String> getPlantName() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT PlantSpecies FROM Plants")) {
            List<String> plants = new ArrayList<>();
            while (rs.next()) plants.add(rsPlants(rs));
            return plants;
        } catch (SQLException e) {
            throw new PlantyRepositoryException(e);
        }
    }

    private String rsPlants(ResultSet rs) throws SQLException {
        return new String(rs.getString("PlantSpecies"));
    }


    @Override
    public Plant getPlantByPlantSpecies (String plantSpecies){
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Plants WHERE PlantSpecies = ?")){
            ps.setString(1, plantSpecies);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Plant plant = new Plant(rs.getString("PlantSpecies"),
                            rs.getString("PlantGenus"),
                            rs.getString("PlantInfo"),
                            rs.getString("Water"),
                            rs.getString("Tempature"),
                            rs.getString("Humidity"),
                            rs.getString("Flowering"),
                            rs.getString("Pests"),
                            rs.getString("Diseases"),
                            rs.getString("Soil"),
                            rs.getString("PotSize"),
                            rs.getString("Poisonous"),
                            rs.getInt("DaysUntilWatering"),
                            rs.getString("Fertilizer"),
                            rs.getString("Light"),
                            rs.getString("LightNeeded"),
                            rs.getInt("plantID"));
                    return plant;
                }
            }catch(SQLException e){
                return null;
            }
        }catch(SQLException e){
            throw new PlantyRepositoryException("Connection in getPlantByPlantSpecies failed!");
        }
        return null;
    }
    public boolean nickNameAlreadyExists(String nickName, int userId){
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT NickName FROM UsersPlants WHERE UserId = ? AND NickName = ?")) {
            ps.setInt(1, userId);
            ps.setString(2,nickName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }else{
                    return false;
                }
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }catch (SQLException e){
            System.out.println("Nick name already exists exception: " + e.getMessage());
        }
        return false;
    }
    @Override
    public void addPlantToUserPlants(String nickName, String photo, int userId, String plantSpecies, java.sql.Date regDate, java.sql.Date waterDate){
        int plantId = getPlantIdFromPlants(plantSpecies);
        if(plantId != 0){
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO UsersPlants(UserID, NickName, Photo, PlantID, RegistrationDate, WateringDate) VALUES(?,?,?,?,?,?)")) {
                ps.setInt(1, userId);
                ps.setString(2, nickName);
                ps.setString(3, photo);
                ps.setInt(4, plantId);
                ps.setDate(5, regDate);
                ps.setDate(6, waterDate);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Add plant to User exception: " + e.getMessage());
            }
        }
    }





    /* DELETE PLANT FROM USER DB */

    public void deletePlantFromUserPlants(String nickName, int userId) {
        if(nickName != null){
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM UsersPlants WHERE NickName = ? AND UserID = ?")) {
                ps.setString(1,nickName);
                ps.setInt(2, userId);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Delete plant from User exception: " + e.getMessage());
            }
        }
    }

    @Override
    public LocalDate getWateredDay(String usersPlantsID) {
        int userPlantsID = Integer.parseInt(usersPlantsID);

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("select WateringDate from [UsersPlants] where UsersPlantsID = ?")) {
            ps.setInt(1, userPlantsID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate nextWater = (rs.getDate("WateringDate")).toLocalDate();
                    return nextWater;
                }
            }catch(SQLException e){
                return null;
            }
        }catch (SQLException e){
            throw new PlantyRepositoryException(e);
        }
        return null;
    }

    @Override
    public void updateDates(String usersPlantsID, LocalDate wateredDay, LocalDate futureDate) {
        int parsedDate = Integer.parseInt(usersPlantsID);

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE [UsersPlants]  SET RegistrationDate = ? , WateringDate = ? WHERE UsersPlantsID = ?")) {
            ps.setDate(1, java.sql.Date.valueOf( wateredDay ));
            ps.setDate(2, java.sql.Date.valueOf( futureDate ) );
            ps.setInt(3, parsedDate);
            ps.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage() );
        }

    }

    @Override
    public List<LocalDate> getAllWDays(int userID) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(" select WateringDate from [UsersPlants] where UserID = ? order by WateringDate")) {
            ps.setInt(1, userID);
            List<LocalDate> listofNextWDays = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                listofNextWDays.add(rsListofDates(rs));
            }
            return listofNextWDays;
        } catch (SQLException e) {
            throw new PlantyRepositoryException(e);
        }

    }

    private LocalDate rsListofDates(ResultSet rs) throws SQLException {
        return ((rs.getDate("WateringDate")).toLocalDate());
    }



    public List<Integer> getDays(int userID) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("select A.PlantID, B.DaysUntilWatering from [UsersPlants] as A\n" +
                    "inner join [Plants] as B on A.PlantID=B.PlantID where A.UserID=?;")) {
            ps.setInt(1, userID);
            List<Integer> days = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                days.add(rsDays(rs));
            }
            return days;
        } catch (SQLException e) {
            throw new PlantyRepositoryException(e);
        }

    }

    private Integer rsDays(ResultSet rs) throws SQLException {
        return new Integer(rs.getInt("DaysUntilWatering"));
    }


    public int getPlantIdFromPlants(String plantSpecies){
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT PlantID FROM Plants WHERE PlantSpecies = ?")) {
            ps.setString(1, plantSpecies);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int plantId = rs.getInt("plantID");
                    return plantId;
                }
            }catch(SQLException e){
                return 0;
            }
        }catch (SQLException e){
            throw new PlantyRepositoryException(e);
        }
        return 0;
    }

    public int getWateringDays(int plantID) {
        int wateringDays =0;
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("select DaysUntilWatering from [Plants] where PlantID=?;"))
        {
            ps.setInt(1, plantID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                     wateringDays = rs.getInt("DaysUntilWatering");
                    return wateringDays;
                }
            }catch(SQLException e){
                System.out.println(e.getMessage());
                return 0;
            }
        }catch (SQLException e){
            throw new PlantyRepositoryException(e);
        }
        return wateringDays;
    }



    @Override
    public List<UserPlant> getUserPlantsInfo(int userId) {
        List<UserPlant> userPlantList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT UsersPlantsID, NickName, PlantSpecies, Poisonous, DaysUntilWatering, LightNeeded, WateringDate " +
                     "FROM UsersPlants " +
                     "JOIN Plants " +
                     "ON UsersPlants.PlantID = Plants.PlantID " +
                     "WHERE UserID = ? " +
                     "ORDER BY DaysUntilWatering")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userPlantList.add(rsUserPlant(rs));

                }
            } catch (SQLException e){
                System.out.println("Get user plants info exception: " + e.getMessage());
            }
        } catch (SQLException e){
            System.out.println("Get user plants info exception: " + e.getMessage());
        }
        return userPlantList;
    }

    public UserPlant rsUserPlant(ResultSet rs) throws SQLException{
       return new UserPlant(rs.getInt("UsersPlantsID"),
               rs.getString("NickName"),
               rs.getString("PlantSpecies"),
               rs.getString("LightNeeded"),
               rs.getInt("DaysUntilWatering"),
               rs.getString("Poisonous"),
               (rs.getTimestamp("WateringDate")));
    }
}