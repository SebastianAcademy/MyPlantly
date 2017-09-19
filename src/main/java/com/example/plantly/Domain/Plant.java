package com.example.plantly.Domain;

public class Plant {
    public String plantSpecies;
    public String plantGenus;
    public String plantInfo;
    public String water;
    public String temperature;
    public String humidity;
    public String flowering;
    public String pests;
    public String diseases;
    public String soil;
    public String potSize;
    public String poisonous;
    public int daysUntilWatering;
    public String fertilizer;
    public String light;
    public String lightNeeded;
    public int plantId;

    public Plant(String plantSpecies, String plantGenus, String plantInfo, String water, String temperature, String humidity, String flowering, String pests, String diseases, String soil, String potSize, String poisonous, int daysUntilWatering, String fertilizer, String light, String lightNeeded, int plantId) {
        this.plantSpecies = plantSpecies;
        this.plantGenus = plantGenus;
        this.plantInfo = plantInfo;
        this.water = water;
        this.temperature = temperature;
        this.humidity = humidity;
        this.flowering = flowering;
        this.pests = pests;
        this.diseases = diseases;
        this.soil = soil;
        this.potSize = potSize;
        this.poisonous = poisonous;
        this.daysUntilWatering = daysUntilWatering;
        this.fertilizer = fertilizer;
        this.light = light;
        this.lightNeeded = lightNeeded;
        this.plantId = plantId;
    }

    public Plant(String plantSpecies, int plantId) {
        this.plantSpecies = plantSpecies;
        this.plantId = plantId;
    }

    public Plant(String plantSpecies, String plantGenus, int plantId) {
        this.plantSpecies = plantSpecies;
        this.plantGenus = plantGenus;
        this.plantId = plantId;
    }
}
