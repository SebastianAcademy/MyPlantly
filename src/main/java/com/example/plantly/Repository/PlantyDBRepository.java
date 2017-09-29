package com.example.plantly.Repository;

import com.example.plantly.Domain.Plant;
import com.example.plantly.Domain.User;
import com.example.plantly.Domain.UserPlant;

import java.sql.Date;
import java.util.List;

public interface PlantyDBRepository {

    void addUser(User user);
    boolean userExists(String email);
    Plant getPlantByPlantSpecies (String plantSpecies);
    void addPlantToUserPlants(String nickName, String photo, int userId, String plantSpecies, Date regDate);
    List<UserPlant> getUserPlantsInfo(int userId);
    boolean nickNameAlreadyExists(String nickName, int userId);
    void changePassword(int userId, String newPassword);
    List<String> getPlantName();
    void deletePlantFromUserPlants(int usersPlantsID);
    void resetWaterDate(int usersPlantsID, Date regDate, int defaultWateringDays);
    boolean addPlantToPlants(Plant plant);
    User checkUser(String email, String password);
}
