package me.kattenvenus.swpxutil.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import me.kattenvenus.swpxutil.datatypes.*;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManageJSON {

    private static ServerData serverData;
    public static ServerData getServerData() {
        return serverData;
    }

    private static BannerVoteDataJSON bannerVoteData;
    public static ArrayList<BannerVoteData> getBannerVoteData() { return bannerVoteData.getBannerVoteData(); } //To make more logical syntax when saving

    public static boolean isOnDeathList(User user) {

        if (serverData.getKnifePartyBanList() == null) {
            serverData.setKnifePartyBanList(new ArrayList<>());
        }

        for (String s : serverData.getKnifePartyBanList()) {

            if (user.getAsMention().equalsIgnoreCase(s)) {
                return true;
            }

        }

        return false;

    }

    public static void toggleDeathList(User user) {

        if (serverData.getKnifePartyBanList() == null) {
            serverData.setKnifePartyBanList(new ArrayList<>());
        }

        for (String s : serverData.getKnifePartyBanList()) {

            if (user.getAsMention().equalsIgnoreCase(s)) {

                serverData.getKnifePartyBanList().remove(s);
                save();
                return;

            }

        }

        serverData.getKnifePartyBanList().add(user.getAsMention());
        save();

    }

    public static void load() {
        load(null);
    }

    public static void load(SlashCommandInteractionEvent event) {

        Gson gson = new Gson();

        try {  //SERVERDATA

            JsonReader reader = new JsonReader(new FileReader("userdata.json"));
            serverData = gson.fromJson(reader, ServerData.class);


            BufferedReader br = new BufferedReader(new FileReader("userdata.json"));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            if (event != null) {
                event.reply("ServerData JSON has been reloaded!").setEphemeral(true).queue();
            }
            LogHandler.printSystemMessage("ServerData JSON has been loaded!");

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            init();
            LogHandler.printSystemMessage("No Serverdata config found, Config created!");
        }

        try {  //BANNERVOTE

            JsonReader reader = new JsonReader(new FileReader("bannervote.json"));
            bannerVoteData = (gson.fromJson(reader, BannerVoteDataJSON.class));

            if (event != null) {
                event.reply("Bannervote JSON has been reloaded!").setEphemeral(true).queue();
            }
            LogHandler.printSystemMessage("Bannervote JSON has been loaded!");

        } catch (IOException | NullPointerException e) {

            bannerVoteData = new BannerVoteDataJSON();

            try (FileWriter writer = new FileWriter("bannervote.json")) {
                gson.toJson(bannerVoteData, writer);

                if (event != null) {
                    event.reply("Created new Bannervote JSON config").setEphemeral(true).queue();
                }

            } catch (IOException ex) {
                if (event != null) {
                    event.reply(Messages.GENERICFATALERROR + " Failed to create bannervote JSON: " + ex).setEphemeral(true).queue();
                    ex.printStackTrace();
                }
                ex.printStackTrace();
            }

            LogHandler.printSystemMessage("No Bannervote data found, Config created!");
        }

    }


    public static void save() {

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        try (FileWriter writer = new FileWriter("userdata.json")) {
            gson.toJson(serverData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson2 = new GsonBuilder()
                .serializeNulls()
                .create();

        try (FileWriter writer = new FileWriter("bannervote.json")) {
            gson2.toJson(bannerVoteData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void init() {
        init(null);
    }

    public static void init(SlashCommandInteractionEvent event) {

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        ServerData data = new ServerData();

        //Default values
        data.setKnifeParty(false);
        data.setVerifiedChannel("854503502121271328");
        data.setVerifiedRole("293113388668813313");
        data.setVerifiedRole("1228113693162602599");

        for (var i : Constants.PERMISSION_NODES) { //Loads in all permissions to the new JSON Serverdata config

            data.getPermissions().put(i, new ArrayList<>(Arrays.asList(Constants.OWNER_ID)));

        }

        try (FileWriter writer = new FileWriter("userdata.json")) {
            gson.toJson(data, writer);

            if (event != null) {
                event.reply("Created new JSON config").setEphemeral(true).queue();
            }

            serverData = data;

        } catch (IOException e) {
            if (event != null) {
                event.reply(Messages.GENERICFATALERROR + " Failed to create config: " + e).setEphemeral(true).queue();
                e.printStackTrace();
            }
            e.printStackTrace();
        }

    }

    public static boolean checkPermission(SlashCommandInteractionEvent event, String permissionNode) {

        List<Role> roles = new ArrayList<>();

        try {
            roles = event.getMember().getRoles();
        } catch (NullPointerException ignored) {
        }

        return checkPermission(roles, event.getUser(), permissionNode);
    }

    public static boolean checkPermission(StringSelectInteractionEvent event, String permissionNode) {

        List<Role> roles = new ArrayList<>();

        try {
            roles = event.getMember().getRoles();
        } catch (NullPointerException ignored) {
        }

        return checkPermission(roles, event.getUser(), permissionNode);
    }

    public static boolean checkPermission(ButtonInteractionEvent event, String permissionNode) {

        List<Role> roles = new ArrayList<>();

        try {
            roles = event.getMember().getRoles();
        } catch (NullPointerException ignored) {
        }

        return checkPermission(roles, event.getUser(), permissionNode);
    }

    public static boolean checkPermission(MessageContextInteractionEvent event, String permissionNode) {

        List<Role> roles = new ArrayList<>();

        try {
            roles = event.getMember().getRoles();
        } catch (NullPointerException ignored) {
        }

        return checkPermission(roles, event.getUser(), permissionNode);
    }


    public static boolean checkPermission(List<Role> roleList, User user, String permissionNode) {


        var permissionRoles = serverData.getPermissions().get(permissionNode);
        var adminRoles = serverData.getPermissions().get("administrator");

        if (permissionRoles == null) {
            System.out.println("[Sweplox Util] ERROR PERMISSIONS ARE NULL");
            LogHandler.printErrorMessage("ERROR PERMISSIONS ARE NULL");
            return false;
        }

        if (permissionRoles.isEmpty()) {
            return false;
        }

        for (var s : permissionRoles) {

            for (Role ss : roleList) { //Checks role perms
                //System.out.println("ROLE: " + s + " " + ss.getAsMention().replace("<@&", "").replace(">", ""));
                if (ss.getAsMention().replace("<@&", "").replace(">", "").equals(s)) return true;
            }

            //Checks for user perms
            if (user.getAsMention().replace("<@", "").replace(">", "").equals(s)) return true;

        }

        for (var s : adminRoles) {

            for (Role ss : roleList) { //Checks role perms
                //System.out.println("ROLE: " + s + " " + ss.getAsMention().replace("<@&", "").replace(">", ""));
                if (ss.getAsMention().replace("<@&", "").replace(">", "").equals(s)) return true;
            }

            //Checks for user perms
            if (user.getAsMention().replace("<@", "").replace(">", "").equals(s)) return true;

        }

        return false;

    }


}
