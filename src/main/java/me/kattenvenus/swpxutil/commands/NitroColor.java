package me.kattenvenus.swpxutil.commands;

import me.kattenvenus.swpxutil.datatypes.Messages;
import me.kattenvenus.swpxutil.utilities.ConvertColor;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NitroColor {

    public static void setNitroColor(SlashCommandInteractionEvent event) {
        setNitroColor(event, null);
    }

    public static void setNitroColor(SlashCommandInteractionEvent event, User preUser) {

        if (preUser == null) {
            preUser = event.getUser();
        }

        final User user = preUser; //Final to be parsed through lambda

        Guild guild = event.getGuild();
        Color hex;

        try { //Checks for valid hex
            hex = ConvertColor.getColorFromString(event.getOption("hex").getAsString());

        } catch (NumberFormatException e) {
            event.reply(Messages.INVALIDHEX).setEphemeral(true).queue();
            return;
        }

        Role nitroColorRole = nitroRoleExisting(guild, hex);

        if (nitroColorRole == null) { //If a nitrocolor role of said hex already exists

            guild.createRole()
                    .setName("NC")
                    .setColor(hex)
                    .setMentionable(false)
                    .queue(role -> {

                        System.out.println("CREATED NEW ROLE WITH HEX " + ConvertColor.getStringFromColor(hex));
                        try {

                            event.getGuild().modifyRolePositions(true)
                                    .selectPosition(role.getPosition())
                                    .moveTo(event.getGuild().getRolesByName("NCSTART", true).get(0).getPosition() - 1)
                                    .queue();

                        } catch (Exception e) {
                            e.printStackTrace();
                            event.reply(Messages.GENERICFATALERROR + "Do the server have a 'NCSTART' role?: " + e).queue();
                            return;
                        }

                        addRoleToMember(event, role, user);


                    });

        } else {
            addRoleToMember(event, nitroColorRole, user);
        }

    }

    public static void removeNitroColor(SlashCommandInteractionEvent event) {
        removeNitroColor(event, event.getMember());
    }

    public static void removeNitroColor(SlashCommandInteractionEvent event, Member member) {

        List<Role> roles = member.getRoles();

        boolean roleRemoved = false;

        for (var s : roles) {
            if (s.getName().equalsIgnoreCase("NC")) {
                System.out.println(s.getId());
                event.getGuild().removeRoleFromMember(member, s).complete();

                removeEmptyNitroColorsFuckCache(event.getGuild(), s, s);
                roleRemoved = true;
            }
        }

        event.reply((roleRemoved ? Messages.NITROCOLORREMOVED : Messages.NONITROCOLOR)).queue();

    }

    public static void getNitroHex(SlashCommandInteractionEvent event) {

        Member user = event.getGuild().getMember(event.getUser());

        if (Objects.equals(event.getSubcommandName(), "gethex")) {
            if (event.getOption("user") != null)
                user = event.getGuild().getMember(event.getOption("user").getAsUser());
        }

        List<Role> roles = user.getRoles();

        System.out.println("PRINTED HEX OF USER " + event.getUser().getName());

        for (var s : roles) {
            if (s.getName().equalsIgnoreCase("NC")) {
                event.reply(user.getAsMention() + Messages.CURRENTNITROCOLORHEX + ConvertColor.getStringFromColor(s.getColor()) + "``").queue();
                return;
            }
        }

        event.reply((Messages.NONITROCOLOR)).queue();

    }

    private static void addRoleToMember(SlashCommandInteractionEvent event, Role role, User user) {

        try {

            boolean hasRoles = true;
            List<Role> roleList = new ArrayList<>();

            try {
                roleList = event.getGuild().getMember(user).getRoles();
            } catch (NullPointerException e) {
                hasRoles = false;
            }


            final List<Role> finalRoleList = roleList;
            final boolean finalHasRoles = hasRoles;

            event.getGuild().addRoleToMember(user, role).queue(action -> { //After role added

                Role removedRole = null;

                try { //Checks if user already has a nitrocolor role

                    if (finalHasRoles) {


                        for (Role s : finalRoleList) {

                            if (s.getName().equalsIgnoreCase("NC")) {

                                if (s.getColor() == null) {
                                    removedRole = s;
                                    try {
                                        event.getGuild().removeRoleFromMember(user, s).queue();
                                        System.out.println("REMOVED ROLE WITH NO COLOR");
                                    } catch (ErrorResponseException e) {
                                        System.out.println("Couldnt find role");
                                    }
                                    continue;

                                }

                                if (s.getColor().getRGB() != role.getColor().getRGB()) {
                                    removedRole = s;
                                    try {
                                        event.getGuild().removeRoleFromMember(user, s).queue();
                                        System.out.println("REMOVED ROLE WITH HEX: " + ConvertColor.getStringFromColor(s.getColor()));
                                    } catch (ErrorResponseException e) {
                                        System.out.println("Couldnt find role");
                                    }

                                }

                            }
                        }
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    event.reply(Messages.GENERICFATALERROR + "addRoleToMember").queue();
                }

                event.reply("Successfully granted " + user.getAsMention() + " a **Nitrocolor** role of ``" + ConvertColor.getStringFromColor(role.getColor()) + "``" ).queue();
                refreshNitroColors(event.getGuild(), role, removedRole);

            });

        } catch (Exception e) {
            event.reply(Messages.GENERICFATALERROR + "addNCToMember: " + e).queue();
            refreshNitroColors(event.getGuild(), null);
            e.printStackTrace();
        }




    }

    public static void removeEmptyNitroColors(Guild guild, Role role) {

        //System.out.println(ConvertColor.getStringFromColor(role.getColor()) + ": " + guild.getMembersWithRoles(role).size());

        if (guild.getMembersWithRoles(role).isEmpty()) {
            role.delete().queue();
        }

    }

    public static void removeEmptyNitroColorsFuckCache(Guild guild, Role role, Role removedRole) {

        //If a role has been removed from a user, this class will remove that count from getMembersWithRoles since the cache is ass slow

        //System.out.println(ConvertColor.getStringFromColor(role.getColor()) + ": " + guild.getMembersWithRoles(role).size());

        int memberCount = guild.getMembersWithRoles(role).size();

        if (role == removedRole) memberCount--;

        if (memberCount < 1) {
            role.delete().queue();
        }

    }

    public static void refreshNitroColors(Guild guild) {
        refreshNitroColors(guild, null, null);
    }

    public static void refreshNitroColors(Guild guild, Role bypass) {
        refreshNitroColors(guild, bypass, null);
    }
    public static void refreshNitroColors(Guild guild, Role bypass, Role removedRole) {

        System.out.println("--REFRESHING NITROCOLORS--");

        List<Role> roles = guild.getRolesByName("NC", true);

        List<Member> users = new ArrayList<>();

        for (var s : roles) {
            users.addAll(guild.getMembersWithRoles(s));
        }

        if (bypass != null) { //Removes a newly added role from the list
            roles.removeIf(s -> s == bypass);
        }

        if (removedRole != null) {
            for (var s : roles) { //Checks for empty nitrocolor roles
                removeEmptyNitroColorsFuckCache(guild, s, removedRole);
            }
        } else {
            for (var s : roles) { //Checks for empty nitrocolor roles
                removeEmptyNitroColors(guild, s);
            }
        }


        for (var s : users) { //Checks for users without nitrobooster anymore

            boolean hasNB = false;

            ArrayList<Role> NCRoles = new ArrayList<>();

            for (var ss : s.getRoles()) {

                if (ss.getName().equalsIgnoreCase("Nitro Booster")) {
                    hasNB = true;
                    break;
                }

                if (ss.getName().equalsIgnoreCase("NC")) {
                    NCRoles.add(ss);
                }

            }

            if (!hasNB) {

                for (Role ss : NCRoles) {

                    System.out.println("REMOVED NITROCOLOR OF USER " + s.getUser().getName() + " " + ss.getColorRaw() + " // NO NITROBOOSTER ANYMORE");
                    guild.removeRoleFromMember(s, ss).queue();
                    removeEmptyNitroColors(guild, ss);

                }

            }

        }

    }

    public static Role nitroRoleExisting(Guild guild, Color clr) {

        List<Role> roles = guild.getRoles();

        for (Role s : roles) {

            try {

                if (s.getColor().getRGB() == clr.getRGB() && s.getName().equalsIgnoreCase("NC")) {
                    return s;
                }

            } catch (NullPointerException e) { //If theres no color on the role
            }

        }

        return null;

    }

}
