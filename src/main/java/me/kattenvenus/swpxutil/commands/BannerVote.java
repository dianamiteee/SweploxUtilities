package me.kattenvenus.swpxutil.commands;

import me.kattenvenus.swpxutil.datatypes.BannerVoteData;
import me.kattenvenus.swpxutil.datatypes.Messages;
import me.kattenvenus.swpxutil.utilities.LogHandler;
import me.kattenvenus.swpxutil.utilities.ManageJSON;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class BannerVote {

    public static void changeBannerVoteRatings(int likes, int dislikes, ButtonInteractionEvent event) {

        String messageID = event.getMessageId();
        BannerVoteData bannerVote = ManageJSON.getBannerVoteData().stream().filter(id -> messageID.equals(id.getMessageID())).findFirst().orElse(null);

        String userID = event.getUser().getId();

        ArrayList<String> reactedUsers;

        try {
            reactedUsers = bannerVote.getReactedUsers();
        } catch (Exception e) {
            event.reply(Messages.GENERICFATALERROR + "changeBannerVoteRatings").setEphemeral(true).queue();
            e.printStackTrace();
            return;
        }

        for (String s : reactedUsers) {

            if (s.equalsIgnoreCase(userID)) {
                event.reply("You have already voted!").setEphemeral(true).queue();
                return;
            }

        }

        reactedUsers.add(userID);

        final int newLikes = bannerVote.getLikes() + likes;
        final int newDislikes = bannerVote.getDislikes() + dislikes;

        bannerVote.setLikes(newLikes);
        bannerVote.setDislikes(newDislikes);

        ManageJSON.save();

        String channelID = bannerVote.getChannelID();

        event.getGuild().getTextChannelById(channelID).retrieveMessageById(messageID).queue(result -> {

            MessageEmbed newEmbed = BannerVote.createEmbed(bannerVote.getDescription(), bannerVote.getBannerUrl(), newLikes, newDislikes, bannerVote.getUserID(), event.getGuild().getMemberById(bannerVote.getUserID()).getUser());
            result.editMessageEmbeds(newEmbed).queue();
            event.reply("Reaction added!").setEphemeral(true).queue();

        });

    }

    public static void setDefaultChannel(SlashCommandInteractionEvent event) {

        Channel channel;

        if (event.getOption("channel") != null) {
            channel = event.getOption("channel").getAsChannel();
        } else {
            LogHandler.replyErrorMessageWithThread(event, "setDefaultChannel", Thread.currentThread().getStackTrace()[1]);
            return;
        }

        setDefaultChannel(channel.getId());
        event.reply("**Bannervote** default channel updated!").setEphemeral(true).queue();
    }

    public static void setDefaultChannel(String channelID) {

        ManageJSON.getServerData().setBannervoteDefaultChannel(channelID);
        ManageJSON.save();

    }

    public static void deleteBannerVoteUser(SlashCommandInteractionEvent event) {

        deleteBannerVote(event, event.getUser());

    }
    
    public static void resetBannerVoteData(SlashCommandInteractionEvent event) {

        ManageJSON.getBannerVoteData().clear();
        ManageJSON.save();

        event.reply("**Bannervote JSON Reset!**").queue();

    }

    public static void updateBannerVotes(Guild guild) {

        for (BannerVoteData s : ManageJSON.getBannerVoteData()) {

            if (s.getUnixTime() + 86400 < System.currentTimeMillis() / 1000L) {

                String channelID = s.getChannelID();
                guild.getTextChannelById(channelID).retrieveMessageById(s.getMessageID()).queue(

                        (message) -> {
                            message.delete().queue();
                            ManageJSON.getBannerVoteData().remove(s);
                            ManageJSON.save();
                        },

                        (failure) -> {

                            LogHandler.printErrorMessage("Couldn't find bannervote message of " + s.getUserID() + "only deleting bannervote on backend.");
                            ManageJSON.getBannerVoteData().remove(s);
                            ManageJSON.save();

                        });

            }

        }

    }

    public static void deleteBannerVote(SlashCommandInteractionEvent event, User externalUser) {

        User user;

        if (externalUser == null) {
            if (event.getOption("user") != null) {
                user = event.getOption("user").getAsUser();
            } else {
                event.reply("No user entered!").setEphemeral(true).queue();
                return;
            }
        } else {
            user = externalUser;
        }

        String userID = user.getId();
        BannerVoteData bannerVote = ManageJSON.getBannerVoteData().stream().filter(id -> userID.equals(id.getUserID())).findFirst().orElse(null);

        if (bannerVote == null) {
            event.reply("No active Bannervote").setEphemeral(true).queue();
            return;
        }

        String channelID = bannerVote.getChannelID();
        try {
            event.getGuild().getTextChannelById(channelID).retrieveMessageById(bannerVote.getMessageID()).queue(result -> {

                ManageJSON.getBannerVoteData().remove(bannerVote);
                ManageJSON.save();
                result.delete().queue();
                event.reply("Bannervote deleted!").setEphemeral(true).queue();

            });

        } catch (Exception e) {
            event.reply("Can't find message!").queue();
            e.printStackTrace();
        }

    }

    public static void banUser(SlashCommandInteractionEvent event) {

        User user;

        if (event.getOption("user") != null) {
            user = event.getOption("user").getAsUser();
        } else {
            event.reply(Messages.GENERICFATALERROR).setEphemeral(true).queue();
            return;
        }

        ManageJSON.getServerData().getBannervoteBannedUsers().add(user.getId());
        ManageJSON.save();

        event.reply("**User " + user.getAsMention() + " has been banned from bannervoting**").setEphemeral(true).queue();

    }

    public static void listBannedUsers(SlashCommandInteractionEvent event) {

        StringBuilder sb = new StringBuilder();

        for (String s : ManageJSON.getServerData().getBannervoteBannedUsers()) {

            sb.append("**");
            sb.append(event.getGuild().getMemberById(s).getUser().getEffectiveName());
            sb.append("** *(@");
            sb.append(event.getGuild().getMemberById(s).getUser().getName());
            sb.append(")* *");
            sb.append(s);
            sb.append("*\n");
        }

        if (sb.isEmpty()) {
            sb.append("**NO BANNED USERS**");
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("BANNED BANNERVOTE USERS", null);
        eb.setColor(new Color(0xdc143c));
        eb.setAuthor("Sweplox BAN MANAGEMENT SYSTEM™", null, "https://swpx.se/sweploxpride.jpg");
        eb.setThumbnail("https://swpx.se/sweploxpride.jpg");

        eb.setDescription(sb);

        event.replyEmbeds(eb.build()).setEphemeral(true).queue();

    }

    public static void pardonUser(SlashCommandInteractionEvent event) {

        User user;

        if (event.getOption("user") != null) {
            user = event.getOption("user").getAsUser();
        } else {
            event.reply(Messages.GENERICFATALERROR).setEphemeral(true).queue();
            return;
        }

        ManageJSON.getServerData().getBannervoteBannedUsers().remove(user.getId());
        ManageJSON.save();

        event.reply("**User " + user.getAsMention() + " has been unbanned from bannervoting**").setEphemeral(true).queue();

    }

    public static void initBannerVote(SlashCommandInteractionEvent event) {

        Message.Attachment banner;
        String description = null;

        if (event.getOption("banner") != null) {
            banner = event.getOption("banner").getAsAttachment();
        } else {
            event.reply(Messages.GENERICFATALERROR).setEphemeral(true).queue();
            return;
        }

        if (event.getOption("description") != null) {
            description = event.getOption("description").getAsString();
        }


        Button button = Button.success("likeBanner", "WE NEED");
        Button button2 = Button.danger("dislikeBanner", "NAAH NOT THIS");
        Button button3 = Button.primary("applyBanner", "Staff: Apply banner");

        final String desc = description;

        //Error checking
        if (!banner.getFileExtension().equalsIgnoreCase("jpg") && !banner.getFileExtension().equalsIgnoreCase("png")) {
            event.reply("**Invalid file type**").setEphemeral(true).queue();
            return;
        }

        if (banner.getSize() > 5000000) {
            event.reply("**File cannot be larger than 5mb**").setEphemeral(true).queue();
            return;
        }

        for (BannerVoteData s : ManageJSON.getBannerVoteData()) {

            if(s.getUserID().equals(event.getUser().getId())) {

                event.reply("**You already have an active Bannervote!** Use ``/bannervote cancel`` first to create a new one!").setEphemeral(true).queue();
                return;

            }

        }


        banner.getProxy().download().whenComplete((bannerData,th) -> { //Downloads the image


            String channelID = ManageJSON.getServerData().getBannervoteDefaultChannel();
            try {
                event.getGuild().getTextChannelById(channelID)// Sends the image
                        .sendMessageEmbeds(createEmbed(desc, banner.getUrl(), 0, 0, event.getUser().getId(), event.getUser()))
                        .setActionRow(button, button2, button3).queue(msg -> {

                            //Creates a local version of the vote, we need both a link to the image and the Base65 version of it as well
                            BannerVoteData bannerVoteData = new BannerVoteData(event.getUser().getId(),
                                    0, 0, (desc == null) ? "A staff need to approve it but **WE CAN VOTE FOR IT WOOO**" : desc,
                                    "", banner.getUrl(), "", ManageJSON.getServerData().getBannervoteDefaultChannel());

                            try {
                                bannerVoteData.setBannerJSON(bannerData); //Adds the image which will be converted to base64
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            bannerVoteData.setMessageID(msg.getId());
                            ManageJSON.getBannerVoteData().add(bannerVoteData);
                            ManageJSON.save();
                        });
            } catch (Exception e) {
                event.reply("**Couldnt start banner vote**").setEphemeral(true).queue();
                e.printStackTrace();
                return;
            }

            event.reply("**Banner vote created!** It'll be active for 24hrs or if a staff applies it").setEphemeral(true).queue();

        });





    }

    public static MessageEmbed createEmbed(String description, String bannerUrl, int likes, int dislikes, String userID, User user) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("DO WE WANT THIS AS A BANNER?", null);
        eb.setColor(new Color(0x1a9ce8));
        eb.setDescription((description == null) ? "A staff need to approve it but **WE CAN VOTE FOR IT WOOO**" : description);

        eb.addField("Ratings:", ":blue_heart: "+ likes +" \u200E \u200E \u200E :x: " + dislikes, false);

        eb.setAuthor("Sweplox voting system™", null, "https://swpx.se/sweploxpride.jpg");

        eb.setImage(bannerUrl);
        eb.setThumbnail("https://swpx.se/timetovote.png");

        eb.setFooter("Submitted by " + user.getEffectiveName(), user.getEffectiveAvatarUrl());

        return eb.build();


    }



}
