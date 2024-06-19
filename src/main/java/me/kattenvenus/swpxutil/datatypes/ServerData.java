package me.kattenvenus.swpxutil.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerData {

    private boolean knifeParty;
    private ArrayList<String> knifePartyBanList;
    private String verifiedChannel;
    private String verifiedRole;
    private Map<String, ArrayList<String>> permissions = new HashMap<>();
    private ArrayList<BannerVoteData> activeBannerVoteData = new ArrayList<>();
    private String BannervoteDefaultChannel;
    private final ArrayList<String> bannervoteBannedUsers = new ArrayList<>();
    private final Map<String, String> autoReplyMessages = new HashMap<>();
    private final Map<String, String> autoReplyMessagesExact = new HashMap<>();
    private final Map<String, String[]> autoReplyEmojis = new HashMap<>();
    private final Map<String, String[]> autoReplyEmojisExact = new HashMap<>();
    private final Map<String, String> autoReplyAllowedChannels = new HashMap<>();

    public String getBannervoteDefaultChannel() {
        return BannervoteDefaultChannel;
    }

    public void setBannervoteDefaultChannel(String bannervoteDefaultChannel) {
        BannervoteDefaultChannel = bannervoteDefaultChannel;
    }

    public ArrayList<String> getBannervoteBannedUsers() {
        return bannervoteBannedUsers;
    }

    public Map<String, String> getAutoReplyMessages() {
        return autoReplyMessages;
    }

    public Map<String, String> getAutoReplyEmojis() {

        Map<String, String> newMap = new HashMap<>();

        for (String s : autoReplyEmojis.keySet()) {

            StringBuilder sb = new StringBuilder();

            for (String ss: autoReplyEmojis.get(s)) {
                sb.append(ss + " ");
            }

            newMap.put(s,sb.toString());

        }

        return newMap;
    }

    public Map<String, String[]> getAutoReplyEmojisRaw() {
        return autoReplyEmojis;
    }

    public Map<String, String[]> getAutoReplyEmojisExactRaw() {
        return autoReplyEmojisExact;
    }


    public Map<String, String> getAutoReplyMessagesExact() {
        return autoReplyMessagesExact;
    }

    public Map<String, String> getAutoReplyEmojisExact() {

        Map<String, String> newMap = new HashMap<>();

        for (String s : autoReplyEmojisExact.keySet()) {

            StringBuilder sb = new StringBuilder();

            for (String ss: autoReplyEmojisExact.get(s)) {
                sb.append(ss + " ");
            }

            newMap.put(s,sb.toString());

        }

        return newMap;
    }

    public Map<String, String> getAutoReplyAllowedChannels() {
        return autoReplyAllowedChannels;
    }

    public String getVerifiedChannel() {
        return verifiedChannel;
    }

    public void setVerifiedChannel(String verifiedChannel) {
        this.verifiedChannel = verifiedChannel;
    }

    public ArrayList<BannerVoteData> getActiveBannervotes() {
        return activeBannerVoteData;
    }

    public String getVerifiedRole() {
        return verifiedRole;
    }

    public void setVerifiedRole(String verifiedRole) {
        this.verifiedRole = verifiedRole;
    }

    public boolean isKnifeParty() {
        return knifeParty;
    }

    public void setKnifeParty(boolean knifeParty) {
        this.knifeParty = knifeParty;
    }

    public ArrayList<String> getKnifePartyBanList() {
        return knifePartyBanList;
    }

    public void setKnifePartyBanList(ArrayList<String> knifePartyBanList) {
        this.knifePartyBanList = knifePartyBanList;
    }

    public Map<String, ArrayList<String>> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, ArrayList<String>> permissions) {
        this.permissions = permissions;
    }
}
