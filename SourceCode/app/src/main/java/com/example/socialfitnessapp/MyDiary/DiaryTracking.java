package com.example.socialfitnessapp.MyDiary;

public class DiaryTracking {

    // All data has to saved as a string to prevent casting issues when using firebase!!
    private String id;
    private String calories;
    private String water;
    private String exercise;


    // By default constructor initialises all variables to 0 (except id)
    public DiaryTracking(String date) {
        setId(date);
        setCalories("0");
        setExercise("0");
        setWater("0");
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getCalories() { return calories; }

    public void setCalories(String calories) { this.calories = calories; }

    public String getWater() { return water; }

    public void setWater(String water) { this.water = water; }

    public String getExercise() { return exercise; }

    public void setExercise(String exercise) { this.exercise = exercise; }


}
