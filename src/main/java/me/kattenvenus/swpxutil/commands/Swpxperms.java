package me.kattenvenus.swpxutil.commands;

import me.kattenvenus.swpxutil.utilities.ManageJSON;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.Map;

public class Swpxperms {

    public static void togglePerms(SlashCommandInteractionEvent event) {

        if (event.getOption("user") == null && event.getOption("role") == null) {
            event.reply("No user or role selected").setEphemeral(true).queue();
            return;
        }

        if (ManageJSON.getServerData().getPermissions().get(event.getOption("permissionnode").getAsString()) == null) {
            event.reply("Invalid permission node").setEphemeral(true).queue();
            return;
        }

        String reply = "Successfully executed:";

        if (event.getOption("user") != null) {
            boolean activated = managePerms(event.getOption("user").getAsUser().getAsMention(), event.getOption("permissionnode").getAsString());

            reply = reply + " User " + event.getOption("user").getAsUser().getAsMention() + " got node ``" + event.getOption("permissionnode").getAsString() + "`` " + ((!activated) ? "**ACTIVATED**" : "**DEACTIVATED**");

        }

        if (event.getOption("role") != null) {
            boolean activated = managePerms(event.getOption("role").getAsRole().getAsMention(), event.getOption("permissionnode").getAsString());

            reply = reply + " Role " + event.getOption("role").getAsRole().getAsMention() + " got node ``" + event.getOption("permissionnode").getAsString() + "`` " + ((!activated) ? "**ACTIVATED**" : "**DEACTIVATED**");

        }

        event.reply(reply).setEphemeral(true).queue();

    }

    public static void listPerms(SlashCommandInteractionEvent event) {

        Map<String, ArrayList<String>> map = ManageJSON.getServerData().getPermissions();

        StringBuilder sb = new StringBuilder();

        sb.append("**LIST OF ALL PERMISSION NODES:**\n\n");

        for (String key : map.keySet()) {
            sb.append("``" + key + "``\n");
        }

        event.reply(sb.toString()).setEphemeral(true).queue();

    }

    private static boolean managePerms(String snowflake, String permissionNode) {

        ArrayList<String> perms = ManageJSON.getServerData().getPermissions().get(permissionNode);

        boolean exists = false;
        snowflake = snowflake.replaceAll("\\D", "");

        for (var s : perms) {

            if (s.equalsIgnoreCase(snowflake)) {
                exists = true;
                break;
            }

        }

        if (exists) {
            ManageJSON.getServerData().getPermissions().get(permissionNode).remove(snowflake);
            ManageJSON.save();
            return true;
        } else {
            ManageJSON.getServerData().getPermissions().get(permissionNode).add(snowflake);
            ManageJSON.save();
            return false;
        }

    }

}
