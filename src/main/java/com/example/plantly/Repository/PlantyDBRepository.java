package com.example.plantly.Repository;

import com.example.plantly.Domain.Plant;
import com.example.plantly.Domain.User;
import com.example.plantly.Domain.UserPlant;

import java.sql.Date;
import java.util.List;

public interface PlantyDBRepository {

    boolean addUser(String firstname, String lastname, String email, String password);
    Plant getPlantByPlantSpecies (String plantSpecies);
    void addPlantToUserPlants(String nickName, String photo, int userId, String plantSpecies, Date regDate);
    User getCurrentUser(String email, String password);
    boolean userExists(String email, String password);
    List<UserPlant> getUserPlantsInfo(int userId);
    boolean nickNameAlreadyExists(String nickName, int userId);
    void changePassword(int userId, String newPassword);
    List<String> getPlantName();
    void deletePlantFromUserPlants(int usersPlantsID);
    void resetWaterDaysLeft(int usersPlantsID, Date regDate, int defaultWateringDays);
}
