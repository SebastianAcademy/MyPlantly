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

    public User checkUser(String email, String password){
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("Select * From Users WHERE Email = ? AND Password = ?")) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User(rs.getInt("UserId"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Email"),
                            rs.getString("Password"),
                            rs.getString("UserType"));
                    return user;
                }
            }catch(SQLException e){
                return null;
            }
        }catch(SQLException e){
            throw new PlantyRepositoryException("Connection in getPlantByPlantSpecies failed!");
        }
        return null;
    }

    @Override
    public boolean addUser(String email, String firstname, String lastname, String password) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO users (email, firstname, lastname, password, usertype) values (?,?,?,?,?) ", new String[]{"UserID"}) ) {
            ps.setString(1, email);
            ps.setString(2, firstname);
            ps.setString(3, lastname);
            ps.setString(4, password);
            ps.setString(5, "user");
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
        }
        return false;
    }

    public boolean setAdminToUser(int userId){
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("Update Users SET UserType = ? WHERE UserID = ?")) {
            ps.setString(1, "admin");
            ps.setInt(2, userId);
            ps.executeUpdate();
        }catch(SQLException e){
            System.out.println("Change password:" + e.getMessage());
            return false;
        }
        return true;
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
        return new User(rs.getInt("UserId"),
                rs.getString("FirstName"),
                rs.getString("LastName"),
                rs.getString("Email").toLowerCase(),
                rs.getString("Password"),
                rs.getString("UserType"));
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
    public void addPlantToUserPlants(String nickName, String photo, int userId, String plantSpecies, Date regDate){
        int plantId = getPlantIdFromPlants(plantSpecies);
        int defaultWateringDays = getDaysUntilWateringFromPlants(plantSpecies);
        if(plantId != 0){
            long timeadj = defaultWateringDays*24*60*60*1000;
            Date waterDate = new Date(regDate.getTime() + timeadj);
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO UsersPlants(UserID, NickName, Photo, PlantID, RegistrationDate, WateringDate, WaterDaysLeft) VALUES(?,?,?,?,?,?,?)")) {
                ps.setInt(1, userId);
                ps.setString(2, nickName);
                ps.setString(3, photo);
                ps.setInt(4, plantId);
                ps.setDate(5, regDate);
                ps.setDate(6,waterDate);
                ps.setInt(7, defaultWateringDays);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Add plant to User exception: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean addPlantToPlants(Plant plant) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Plants (PlantSpecies, PlantGenus, PlantInfo, Water, Tempature, Humidity, Flowering" +
                     ", Pests, Diseases, Soil, PotSize, Poisonous, DaysUntilWatering, Fertilizer, Light, LightNeeded) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ") ) {
                ps.setString(1, plant.plantSpecies);
                ps.setString(2, plant.plantGenus);
                ps.setString(3, plant.plantInfo);
                ps.setString(4, plant.water);
                ps.setString(5, plant.temperature);
                ps.setString(6, plant.humidity);
                ps.setString(7, plant.flowering);
                ps.setString(8, plant.pests);
                ps.setString(9, plant.diseases);
                ps.setString(10, plant.soil);
                ps.setString(11, plant.potSize);
                ps.setString(12, plant.poisonous);
                ps.setInt(13, plant.daysUntilWatering);
                ps.setString(14, plant.fertilizer);
                ps.setString(15, plant.light);
                ps.setString(16, plant.lightNeeded);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void resetWaterDate(int usersPlantsID, Date regDate, int defaultWateringDays){
        long timeadj = defaultWateringDays*24*60*60*1000;
        Date waterDate = new Date(regDate.getTime() + timeadj);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE UsersPlants " +
                     "SET RegistrationDate = ?, WateringDate = ? " +
                     "WHERE UsersPlantsID = ?")){
            ps.setDate(1, regDate);
            ps.setDate(2, waterDate);
            ps.setInt(3, usersPlantsID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Reset Water Days Left exception: " + e.getMessage());
        }
    }
    //Not used for now, will probably use dates instead
    private int getDaysUntilWateringFromPlants(String plantSpecies) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT DaysUntilWatering FROM Plants WHERE PlantSpecies = ?")) {
            ps.setString(1, plantSpecies);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int daysUntilWatering = rs.getInt("DaysUntilWatering");
                    return daysUntilWatering;
                }
            }catch(SQLException e){
                return 0;
            }
        }catch (SQLException e){
            throw new PlantyRepositoryException(e);
        }
        return 0;
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

    /* DELETE PLANT FROM USER DB */

    public void deletePlantFromUserPlants(int usersPlantsID) {
        if(usersPlantsID > 0){
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM UsersPlants WHERE UsersPlantsID = ?")) {
                ps.setInt(1, usersPlantsID);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Delete plant from User exception: " + e.getMessage());
            }
        }
    }

    @Override
    public List<UserPlant> getUserPlantsInfo(int userId) {
        List<UserPlant> userPlantList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT UsersPlantsID, NickName, PlantSpecies, Poisonous, DaysUntilWatering, LightNeeded, RegistrationDate, WaterDaysLeft, WateringDate  " +
                     "FROM UsersPlants " +
                     "JOIN Plants " +
                     "ON UsersPlants.PlantID = Plants.PlantID " +
                     "WHERE UserID = ? " +
                     "ORDER BY WateringDate")) {
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
        Date today = Date.valueOf(LocalDate.now());
        Date wateringDate = rs.getDate("WateringDate");
        long diff = (wateringDate.getTime() - today.getTime())/86400000;
        int waterDaysLeft = (int)diff;
       return new UserPlant(rs.getInt("UsersPlantsID"),
               rs.getString("NickName"),
               rs.getString("PlantSpecies"),
               rs.getString("LightNeeded"),
               rs.getInt("DaysUntilWatering"),
               rs.getString("Poisonous"),
               (rs.getDate("RegistrationDate")),
               (rs.getDate("WateringDate")),
               waterDaysLeft);
    }
}