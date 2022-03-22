package com.example.socialfitnessapp.MyDiary;

public class DiaryTracking {

    private String id;
    private int calories;
    private int water;
    private int exercise;

    // By default constructor initialises all variables to 0 (except id)
    public DiaryTracking(String date) {
        setId(date);
        setCalories(0);
        setExercise(0);
        setWater(0);
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public int getCalories() { return calories; }

    public void setCalories(int calories) { this.calories = calories; }

    public int getWater() { return water; }

    public void setWater(int water) { this.water = water; }

    public int getExercise() { return exercise; }

    public void setExercise(int exercise) { this.exercise = exercise; }
}
