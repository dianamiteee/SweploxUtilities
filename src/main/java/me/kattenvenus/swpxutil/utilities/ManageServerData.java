package me.kattenvenus.swpxutil.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import me.kattenvenus.swpxutil.datatypes.Constants;
import me.kattenvenus.swpxutil.datatypes.Messages;
import me.kattenvenus.swpxutil.datatypes.ServerData;
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

public class ManageServerData {

    private static ServerData currentData;

    public static ServerData getCurrentData() {
        return currentData;
    }

    public static void setCurrentData(ServerData currentData) {
        ManageServerData.currentData = currentData;
    }

    public static boolean isOnDeathList(User user) {

        if (currentData.getKnifePartyBanList() == null) {
            currentData.setKnifePartyBanList(new ArrayList<>());
        }

        for (String s : currentData.getKnifePartyBanList()) {

            if (user.getAsMention().equalsIgnoreCase(s)) {
                return true;
            }

        }

        return false;

    }

    public static void toggleDeathList(User user) {

        if (currentData.getKnifePartyBanList() == null) {
            currentData.setKnifePartyBanList(new ArrayList<>());
        }

        for (String s : currentData.getKnifePartyBanList()) {

            if (user.getAsMention().equalsIgnoreCase(s)) {

                currentData.getKnifePartyBanList().remove(s);
                save();
                return;

            }

        }

        currentData.getKnifePartyBanList().add(user.getAsMention());
        save();

    }

    public static void load() {
        load(null);
    }

    public static void load(SlashCommandInteractionEvent event) {

        Gson gson = new Gson();

        try {

            JsonReader reader = new JsonReader(new FileReader("userdata.json"));
            currentData = gson.fromJson(reader, ServerData.class);

            BufferedReader br = new BufferedReader(new FileReader("userdata.json"));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }


            System.out.println(currentData.isKnifeParty());

            if (event != null) {
                event.reply("JSON has been reloaded!").setEphemeral(true).queue();
            }
            LogHandler.printSystemMessage("Json has been loaded!");

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            init();
            LogHandler.printSystemMessage("No config found, Config created!");
        }

    }


    public static void save() {

        Gson gson = new GsonBuilder()
                .create();

        try (FileWriter writer = new FileWriter("userdata.json")) {
            gson.toJson(currentData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void init() {
        init(null);
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


        var permissionRoles = currentData.getPermissions().get(permissionNode);
        var adminRoles = currentData.getPermissions().get("administrator");

        if (permissionRoles == null) {
            System.out.println("[Sweplox Util] ERROR PERMISSIONS ARE NULL");
            return false;
        }

        if (permissionRoles.size() < 1) {
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

    public static void init(SlashCommandInteractionEvent event) {

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        ServerData data = new ServerData();

        data.setKnifeParty(false);
        data.setVerifiedChannel("854503502121271328");
        data.setVerifiedRole("293113388668813313");
        data.setVerifiedRole("1228113693162602599");

        for (var i : Constants.PERMISSION_NODES) {

            data.getPermissions().put(i, new ArrayList<>(Arrays.asList(Constants.OWNER_ID)));

        }

        try (FileWriter writer = new FileWriter("userdata.json")) {
            gson.toJson(data, writer);

            if (event != null) {
                event.reply("Created new JSON config").setEphemeral(true).queue();
            }

            currentData = data;

        } catch (IOException e) {
            if (event != null) {
                event.reply(Messages.GENERICFATALERROR + " Failed to create config: " + e).setEphemeral(true).queue();
                e.printStackTrace();
            }
            e.printStackTrace();
        }

    }

}
