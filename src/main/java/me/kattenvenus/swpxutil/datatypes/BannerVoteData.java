package me.kattenvenus.swpxutil.datatypes;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;

public class BannerVoteData {

    private final String userID;
    private int likes;
    private int dislikes;
    private final String description;
    private String bannerJSON;
    private String bannerUrl;
    private String messageID;
    private ArrayList<String> reactedUsers = new ArrayList<>();
    private final long unixTime = System.currentTimeMillis() / 1000L;
    private final String channelID;

    public BannerVoteData(String userID, int likes, int dislikes, String description, String bannerJSON, String bannerUrl, String messageID, String channelID) {
        this.userID = userID;
        this.likes = likes;
        this.dislikes = dislikes;
        this.description = description;
        this.bannerJSON = bannerJSON;
        this.bannerUrl = bannerUrl;
        this.messageID = messageID;
        this.channelID = channelID;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getUserID() {
        return userID;
    }

    public byte[] getBannerJSON() {
        return Base64.getDecoder().decode(this.bannerJSON);
    }

    public void setBannerJSON(InputStream bannerJSON) throws IOException {

        final var bytes = bannerJSON.readAllBytes();

        this.bannerJSON = Base64.getEncoder().encodeToString(bytes);
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public String getDescription() {
        return description;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public ArrayList<String> getReactedUsers() {
        return reactedUsers;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
