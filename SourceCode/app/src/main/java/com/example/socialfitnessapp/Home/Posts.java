package com.example.socialfitnessapp.Home;

public class Posts {
    private String datePost, postDate, postDesc, postImageUrl, username, userProfileImageUrl;

    public Posts() {
    }

    public Posts(String datePost, String postDate, String postDesc, String postImageUrl, String username, String userProfileImageUrl) {
        this.datePost = datePost;
        this.postDate = postDate;
        this.postDesc = postDesc;
        this.postImageUrl = postImageUrl;
        this.username = username;
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getDatePost() { return datePost; }

    public void setDatePost(String datePost) { this.datePost = datePost; }

    public String getPostDate() { return postDate; }

    public void setPostDate(String postDate) { this.postDate = postDate; }

    public String getPostDesc() { return postDesc; }

    public void setPostDesc(String postDesc) { this.postDesc = postDesc; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPostImageUrl() { return postImageUrl; }

    public void setPostImageUrl(String postImageUrl) { this.postImageUrl = postImageUrl; }

    public String getUserProfileImageUrl() { return userProfileImageUrl; }

    public void setUserProfileImageUrl(String userProfileImageUrl) { this.userProfileImageUrl = userProfileImageUrl; }
}
