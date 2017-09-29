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
    public void addUser(User user) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO users (email, firstname, lastname, password, usertype) values (?,?,?,?,?) ") ) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getLastname());
            ps.setString(4, user.getPassword());
            ps.setString(5, "user");
            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }

    @Override
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
                System.out.println("");
                return null;
            }
        }catch(SQLException e){
            throw new PlantyRepositoryException("Connection in checkUser failed!");
        }
        return null;
    }

    @Override
    public boolean userExists(String email){
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("Select Email From Users WHERE Email = ?")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){ // if the query has worked there will be a next and the user exists else the user is new and we can proceed to adding
                return true;
            }else{
                return false;
            }
        }catch(SQLException e){
            System.out.println("SQLexception userExists: " + e.getMessage());
            return false;
        }
    }

    public boolean setAdminToUser(int userId){ // I´m going to add a form to set a user to admin, this is the prepared SQL to do this
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
    public List<String> getPlantName() { //This method is used to get all the plant species and populate them in a list, then sent to the user page->add a plant->form for plant species
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
    public Plant getPlantByPlantSpecies (String plantSpecies){ //This method is used to get all the information of a specific plant and then sent to the plant info page so the correct plants information can be shown
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
                if (rs.next()) { // If the query got a hit the nick name already exists else we can add the plant with this nick name which will be done in addPlantToUserPlants
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
        int defaultWateringDays = getDaysUntilWateringFromPlants(plantSpecies); // this could be done in SQL,  I´m looking in to that
            long timeadj = defaultWateringDays*24*3600; // Since long is not big enough to calculate the watering sequence in milli seconds we use seconds
            Date waterDate = new Date(regDate.getTime() + timeadj*1000); // To set next watering date by using the watering sequence for a specific plant
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO UsersPlants(UserID, NickName, Photo, PlantID, RegistrationDate, WateringDate, WateredDate) VALUES(?,?,?,(select plantid from plants where plantspecies = ?),?,?,?)")) {
                ps.setInt(1, userId);
                ps.setString(2, nickName);
                ps.setString(3, photo);
                ps.setString(4, plantSpecies);
                ps.setDate(5, regDate); // this date is used so we can calculate how old the plant is, in the future
                ps.setDate(6,waterDate);
                ps.setDate(7, regDate); // We asume that the registration date is the first time the plant got watered, hense last watered date
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Add plant to User exception: " + e.getMessage());
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
        long timeadj = defaultWateringDays*24*3600; //long is not big enough to recalculate the watering sequence into milli seconds so we use seconds
        Date waterDate = new Date(regDate.getTime() + timeadj*1000); //we add the watering sequence in milli seconds to the waterDate which will tell us what date the plant should be watered
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE UsersPlants " +
                     "SET WateredDate = ?, WateringDate = ? " +
                     "WHERE UsersPlantsID = ?")){
            ps.setDate(1, regDate);
            ps.setDate(2, waterDate);
            ps.setInt(3, usersPlantsID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Reset Water Days Left exception: " + e.getMessage());
        }
    }

    private int getDaysUntilWateringFromPlants(String plantSpecies) { //This method could be done in SQL directly in addUserPlants
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
             PreparedStatement ps = conn.prepareStatement("SELECT UsersPlantsID, NickName, PlantSpecies, Poisonous, DaysUntilWatering, LightNeeded, RegistrationDate, WateredDate, WateringDate  " +
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
       return new UserPlant(rs.getInt("usersPlantsID"),
               rs.getString("NickName"),
               rs.getString("PlantSpecies"),
               rs.getString("LightNeeded"),
               rs.getInt("DaysUntilWatering"),
               rs.getString("Poisonous"),
               (rs.getDate("WateredDate")),
               (rs.getDate("WateringDate")),
               waterDaysLeft);
    }
}