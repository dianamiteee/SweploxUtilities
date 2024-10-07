package me.kattenvenus.swpxutil.commands;

import me.kattenvenus.swpxutil.datatypes.Messages;
import me.kattenvenus.swpxutil.utilities.LogHandler;
import me.kattenvenus.swpxutil.utilities.ManageJSON;
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
            LogHandler.replyErrorMessageWithThread(event, "verify", Thread.currentThread().getStackTrace()[1]);
            return;
        }

        if (event.getOption("reason") != null) {
            reason = event.getOption("reason").getAsString();
        } else {
            LogHandler.replyErrorMessageWithThread(event, "verify", Thread.currentThread().getStackTrace()[1]);
            return;
        }

        Role verifiedRole;
        try {
            verifiedRole = event.getGuild().getRoleById(ManageJSON.getServerData().getVerifiedRole());
        } catch (NullPointerException e) {
            LogHandler.replyErrorMessageWithThread(event, "verify", Thread.currentThread().getStackTrace()[1]);
            return;
        }

        try { //Adds member role
            event.getGuild().addRoleToMember(verifiedUser, verifiedRole).queue();
        } catch (HierarchyException e) {
            LogHandler.replyErrorMessage(event, "verify", "Bot can't give role with higher privileges than itself!");
            e.printStackTrace();
            return;
        } catch (IllegalArgumentException e) {
            LogHandler.replyErrorMessage(event, "verify", "No role/channel selected");
            e.printStackTrace();
            return;
        } catch (Exception e) {
            LogHandler.replyErrorMessageWithThread(event, "verify", Thread.currentThread().getStackTrace()[1]);
            e.printStackTrace();
            return;
        }

        try { //Removes unverified role
            event.getGuild().removeRoleFromMember(verifiedUser,event.getGuild().getRolesByName("unverified",true).get(0)).queue();
        } catch (Exception e) {

        }

        String preparedString = "**User:** " + verifiedUser.getAsMention() + " *(At join: " + verifiedUser.getEffectiveName() + ")* **got verified by:** " + event.getUser().getAsMention() + ", **with reason:** " + reason;

        event.getGuild().getTextChannelById(ManageJSON.getServerData().getVerifiedChannel()).sendMessage(preparedString).queue();
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

        ManageJSON.getServerData().setVerifiedChannel(verifiedChannel.getId());
        ManageJSON.save();
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

        ManageJSON.getServerData().setVerifiedRole(verifiedRole.getId());
        ManageJSON.save();
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

        MessageChannel affectedChannel = event.getGuild().getTextChannelById(ManageJSON.getServerData().getVerifiedChannel());

        final String msg = newMessage;

        try {
            affectedChannel.retrieveMessageById(messageID).queue(message -> {

                Pattern pattern = Pattern.compile("^(.*?reason:\\*\\*)", Pattern.CASE_INSENSITIVE); //Takes everything behind the text "reason:**"
                Matcher matcher = pattern.matcher(message.getContentRaw());
                boolean matchFound = matcher.find();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now(); //Get date


                if (matchFound) {
                    try {
                        message.editMessage(matcher.group(1) + " " + msg + "\n*(Edited @ " + dtf.format(now) + ")*").queue(); //Actually editing the message
                    } catch (Exception e) {
                        event.reply("Can't edit message, is it one from the Sweplox bot?").setEphemeral(true).queue();
                        LogHandler.printErrorMessage("Verify: Couldn't edit message with id " + messageID);
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
