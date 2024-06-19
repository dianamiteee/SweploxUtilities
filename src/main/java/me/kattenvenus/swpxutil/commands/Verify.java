package me.kattenvenus.swpxutil.commands;

import me.kattenvenus.swpxutil.datatypes.Messages;
import me.kattenvenus.swpxutil.utilities.ManageServerData;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Verify {

    public static void verifyUser(SlashCommandInteractionEvent event) {

        User verifiedUser;
        String reason;

        if (event.getOption("user") != null) {
            verifiedUser = event.getOption("user").getAsUser();
        } else {
            event.reply(Messages.GENERICFATALERROR).setEphemeral(true).queue();
            return;
        }

        if (event.getOption("reason") != null) {
            reason = event.getOption("reason").getAsString();
        } else {
            event.reply(Messages.GENERICFATALERROR).setEphemeral(true).queue();
            return;
        }

        Role verifiedRole;
        try {
            verifiedRole = event.getGuild().getRoleById(ManageServerData.getCurrentData().getVerifiedRole());
        } catch (NullPointerException e) {
            event.reply(Messages.GENERICFATALERROR + " verifyUser").setEphemeral(true).queue();
            return;
        }

        try {
            event.getGuild().addRoleToMember(verifiedUser, verifiedRole).queue();
        } catch (HierarchyException e) {
            event.reply("**Bot can't give role with higher privileges than itself!**").setEphemeral(true).queue();
            e.printStackTrace();
            return;
        } catch (IllegalArgumentException e) {
            event.reply("**No role/channel selected**").setEphemeral(true).queue();
            e.printStackTrace();
            return;
        } catch (Exception e) {
            event.reply("**UNABLE TO VERIFY USER, CONTACT KATTENVENUS**").setEphemeral(true).queue();
            e.printStackTrace();
            return;
        }

        String preparedString = "**User:** " + verifiedUser.getAsMention() + " *(At join: " + verifiedUser.getEffectiveName() + ")* **got verified by:** " + event.getUser().getAsMention() + ", **with reason:** " + reason;

        event.getGuild().getTextChannelById(ManageServerData.getCurrentData().getVerifiedChannel()).sendMessage(preparedString).queue();
        event.reply(verifiedUser.getAsMention() + Messages.NEWVERIFIEDMEMBER).queue();

    }

    public static void setVerifiedChannel(SlashCommandInteractionEvent event) {

        Channel verifiedChannel;

        if (event.getOption("channel") != null) {
            verifiedChannel = event.getOption("channel").getAsChannel();
        } else {
            event.reply("No channel entered!").setEphemeral(true).queue();
            return;
        }

        ManageServerData.getCurrentData().setVerifiedChannel(verifiedChannel.getId());
        ManageServerData.save();
        event.reply("Current verified channel changed!").setEphemeral(true).queue();

    }

    public static void setVerifiedRole(SlashCommandInteractionEvent event) {

        Role verifiedRole;

        if (event.getOption("role") != null) {
            verifiedRole = event.getOption("role").getAsRole();
        } else {
            event.reply("No role entered!").setEphemeral(true).queue();
            return;
        }

        ManageServerData.getCurrentData().setVerifiedRole(verifiedRole.getId());
        ManageServerData.save();
        event.reply("Current verified role changed!").setEphemeral(true).queue();

    }

    public static void edit(SlashCommandInteractionEvent event) {

        //This method takes the old message, removes the reason, and replaces it with a new one from the string newMessage entered by the user

        String messageID;
        String newMessage;

        if (event.getOption("messageid") != null) {
            messageID = event.getOption("messageid").getAsString();
        } else {
            event.reply(Messages.GENERICFATALERROR).setEphemeral(true).queue();
            return;
        }

        if (event.getOption("newmessage") != null) {
            newMessage = event.getOption("newmessage").getAsString();
        } else {
            event.reply(Messages.GENERICFATALERROR).setEphemeral(true).queue();;
            return;
        }

        MessageChannel affectedChannel = event.getGuild().getTextChannelById(ManageServerData.getCurrentData().getVerifiedChannel());

        final String msg = newMessage;

        try {
            affectedChannel.retrieveMessageById(messageID).queue(message -> {

                Pattern pattern = Pattern.compile("^(.*?reason:\\*\\*)", Pattern.CASE_INSENSITIVE); //Takes everything behind the text "reason:**"
                Matcher matcher = pattern.matcher(message.getContentRaw());
                boolean matchFound = matcher.find();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now(); //Get date

                System.out.println(message.getContentRaw());


                if (matchFound) {
                    try {
                        message.editMessage(matcher.group(1) + " " + msg + "\n*(Edited @ " + dtf.format(now) + ")*").queue(); //Actually editing the message
                    } catch (Exception e) {
                        event.reply("Can't edit message, is it one from the Sweplox bot?").setEphemeral(true).queue();
                        System.out.println("Couldn't edit message with id " + messageID);
                        e.printStackTrace();
                        return;
                    }

                    event.reply(Messages.MESSAGEEDITEDVERIFY).setEphemeral(true).queue();
                } else {
                    event.reply("**Can't find message**").setEphemeral(true).queue();
                }

            });
        } catch (Exception e) {
            event.reply("**Can't retrieve message, is the id correct?**").setEphemeral(true).queue();
        }

    }


}
