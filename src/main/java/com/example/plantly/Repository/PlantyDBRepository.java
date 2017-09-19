package com.example.plantly.Repository;

import com.example.plantly.Domain.Plant;
import com.example.plantly.Domain.User;
import com.example.plantly.Domain.UserPlant;

import java.time.LocalDate;
import java.util.List;

public interface PlantyDBRepository {

    boolean addUser(String firstname, String lastname, String email, String password);
    Plant getPlantByPlantSpecies (String plantSpecies);
    void addPlantToUserPlants(String nickName, String photo, int userId, String plantSpecies, java.sql.Date regDate, java.sql.Date waterDate);
    User getCurrentUser(String email, String password);
    boolean userExists(String email, String password);
    List<UserPlant> getUserPlantsInfo(int userId);
    boolean nickNameAlreadyExists(String nickName, int userId);
    void changePassword(int userId, String newPassword);
    List<String> getPlantName();
    List<Integer> getDays(int userID);
    void deletePlantFromUserPlants(String nickName, int userId);


    LocalDate getWateredDay(String usersPlantsID);

    void updateDates(String usersPlantsID, LocalDate wateredDay, LocalDate futureDate);

    List<LocalDate> getAllWDays(int userID);

    // här kommer vi skriva metod som implementeras i DB Repository
}
