package com.example.plantly.Domain;

import java.sql.Timestamp;

public class UserPlant {
    public int UsersPlantsID;
    public String nickName;
    public String plantSpecies;
    public String lightNeeded;
    public int waterDays;
    public String poison;
    public java.sql.Timestamp regDate;
    public java.sql.Timestamp waterDate;
    public long daysLeft;



    public UserPlant(String nickName, String plantSpecies, String lightNeeded, int waterDays, String poison) {
        this.nickName = nickName;
        this.plantSpecies = plantSpecies;
        this.lightNeeded = lightNeeded;
        this.waterDays = waterDays;
        this.poison = poison;
    }

    public UserPlant(String nickName, String plantSpecies, String lightNeeded, int waterDays, String poison, Timestamp regDate, Timestamp waterDate) {
        this.nickName = nickName;
        this.plantSpecies = plantSpecies;
        this.lightNeeded = lightNeeded;
        this.waterDays = waterDays;
        this.poison = poison;
        this.regDate = regDate;
        this.waterDate = waterDate;

    }

    public UserPlant(int UsersPlantsID, String nickName, String plantSpecies, String lightNeeded, int waterDays, String poison) {
        this.UsersPlantsID = UsersPlantsID;
        this.nickName = nickName;
        this.plantSpecies = plantSpecies;
        this.lightNeeded = lightNeeded;
        this.waterDays = waterDays;
        this.poison = poison;

    }


    public UserPlant(int usersPlantsID, String nickName, String plantSpecies, String lightNeeded, int waterDays, String poison, Timestamp waterDate) {
        UsersPlantsID = usersPlantsID;
        this.nickName = nickName;
        this.plantSpecies = plantSpecies;
        this.lightNeeded = lightNeeded;
        this.waterDays = waterDays;
        this.poison = poison;
        this.waterDate = waterDate;
    }
}
