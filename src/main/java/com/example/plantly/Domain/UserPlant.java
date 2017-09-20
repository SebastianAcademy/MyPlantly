package com.example.plantly.Domain;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

public class UserPlant {
    public int UsersPlantsID;
    public String nickName;
    public String plantSpecies;
    public String lightNeeded;
    public int waterDays;
    public String poison;
    public Date regDate;
    public int waterDaysLeft;



    public UserPlant(String nickName, String plantSpecies, String lightNeeded, int waterDays, String poison) {
        this.nickName = nickName;
        this.plantSpecies = plantSpecies;
        this.lightNeeded = lightNeeded;
        this.waterDays = waterDays;
        this.poison = poison;
    }

    public UserPlant(String nickName, String plantSpecies, String lightNeeded, int waterDays, String poison, Date regDate) {
        this.nickName = nickName;
        this.plantSpecies = plantSpecies;
        this.lightNeeded = lightNeeded;
        this.waterDays = waterDays;
        this.poison = poison;
        this.regDate = regDate;

    }

    public UserPlant(int UsersPlantsID, String nickName, String plantSpecies, String lightNeeded, int daysLeft, String poison) {
        this.UsersPlantsID = UsersPlantsID;
        this.nickName = nickName;
        this.plantSpecies = plantSpecies;
        this.lightNeeded = lightNeeded;
        this.waterDaysLeft = daysLeft;
        this.poison = poison;

    }


    public UserPlant(int usersPlantsID, String nickName, String plantSpecies, String lightNeeded, int waterDays, String poison, Date regDate, int waterDaysLeft) {
        UsersPlantsID = usersPlantsID;
        this.nickName = nickName;
        this.plantSpecies = plantSpecies;
        this.lightNeeded = lightNeeded;
        this.waterDays = waterDays;
        this.poison = poison;
        this.regDate = regDate;
        this.waterDaysLeft = waterDaysLeft;
    }
}