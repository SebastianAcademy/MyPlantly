package com.example.plantly.Domain;

import java.util.Date;

public class UserPlant {
    public int UsersPlantsID;
    public String nickName;
    public String plantSpecies;
    public String lightNeeded;
    public int waterDays;
    public String poison;
    public Date wateringDate;
    public Date wateredDate;
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
        this.wateredDate = regDate;

    }

    public UserPlant(int UsersPlantsID, String nickName, String plantSpecies, String lightNeeded, int daysLeft, String poison) {
        this.UsersPlantsID = UsersPlantsID;
        this.nickName = nickName;
        this.plantSpecies = plantSpecies;
        this.lightNeeded = lightNeeded;
        this.waterDaysLeft = daysLeft;
        this.poison = poison;

    }


    public UserPlant(int usersPlantsID, String nickName, String plantSpecies, String lightNeeded, int waterDays, String poison, Date regDate, Date wateringDate, int waterDaysLeft) {
        UsersPlantsID = usersPlantsID;
        this.nickName = nickName;
        this.plantSpecies = plantSpecies;
        this.lightNeeded = lightNeeded;
        this.waterDays = waterDays;
        this.poison = poison;
        this.wateredDate = regDate;
        this.wateringDate = wateringDate;
        this.waterDaysLeft = waterDaysLeft;
    }
}
