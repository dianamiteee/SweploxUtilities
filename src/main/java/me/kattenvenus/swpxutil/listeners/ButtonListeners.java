package me.kattenvenus.swpxutil.listeners;

import me.kattenvenus.swpxutil.commands.Autoreply;
import me.kattenvenus.swpxutil.commands.BannerVote;
import me.kattenvenus.swpxutil.datatypes.BannerVoteData;
import me.kattenvenus.swpxutil.datatypes.Messages;
import me.kattenvenus.swpxutil.utilities.ManageJSON;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ButtonListeners extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {

        System.out.println("[" + event.getGuild().getName() + "] " + "[" + event.getChannel().getName() + "] " + event.getMember().getUser().getEffectiveName() + " Chose: " + event.getValues().get(0));

        switch (event.getComponentId()) {

            case "replyAdd":

                if (!ManageJSON.checkPermission(event, "autoreply")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyMessage")) {
                    event.getMessage().delete().queue();
                    Autoreply.openRequestPrompt(event,0);
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyMessageExact")) {
                    event.getMessage().delete().queue();
                    Autoreply.openRequestPrompt(event,1);
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyReaction")) {
                    event.getMessage().delete().queue();
                    Autoreply.openRequestPrompt(event,2);
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyReactionExact")) {
                    event.getMessage().delete().queue();
                    Autoreply.openRequestPrompt(event,3);
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyChannel")) {
                    event.getMessage().delete().queue();
                    Autoreply.openRequestPrompt(event,4);
                }

                break;

            case "replyRemove":

                if (!ManageJSON.checkPermission(event, "autoreply")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyRemoveMessage")) {
                    Autoreply.removeMessage(event,1, 0);
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyRemoveMessageExact")) {
                    Autoreply.removeMessage(event,1, 1);
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyRemoveReaction")) {
                    Autoreply.removeMessage(event,1, 2);
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyRemoveReactionExact")) {
                    Autoreply.removeMessage(event,1, 3);
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyRemoveChannel")) {
                    Autoreply.removeMessage(event,1, 4);
                }

                break;
            case "replyList":

                if (!ManageJSON.checkPermission(event, "autoreply")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyListMessage")) {
                    Autoreply.listMessages(event, 0);
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyListMessageExact")) {
                    Autoreply.listMessages(event, 1);
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyListReaction")) {
                    Autoreply.listMessages(event, 2);
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyListReactionExact")) {
                    Autoreply.listMessages(event, 3);
                }

                if (event.getValues().get(0).equalsIgnoreCase("replyListChannel")) {
                    Autoreply.listMessages(event, 4);
                }

                break;

        }

        if (event.getComponentId().contains("dynamicRemoveMessage")) {

            int type = Character.getNumericValue(event.getComponentId().toCharArray()[event.getComponentId().length()-1]);

            System.out.println(type);

            Pattern pattern = Pattern.compile("dynamicRemoveMessageListing(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(event.getValues().get(0));
            boolean matchFound = matcher.find();
            if(matchFound) {

                Autoreply.backendDeleteReply(Integer.parseInt(matcher.group(1)), event, type);

            } else {
                event.reply(Messages.GENERICFATALERROR + Thread.currentThread().getStackTrace()[1].getMethodName()).setEphemeral(true).queue();
                return;
            }
        }


    }

    public void onButtonInteraction(ButtonInteractionEvent event) {

        System.out.println("[" + event.getGuild().getName() + "] " + "[" + event.getChannel().getName() + "] " + event.getMember().getUser().getEffectiveName() + " PRESSED: " + event.getInteraction().getButton().getLabel());


        //The buttons id ends with an number for which type, see AutoReply.getMessage(), so we need to check for that
        if (event.getComponentId().contains("replyRemoveForwards")){

            if (!ManageJSON.checkPermission(event, "autoreply")) {
                event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                return;
            }

            int type = Character.getNumericValue(event.getComponentId().toCharArray()[event.getComponentId().length()-1]);

            Autoreply.turnPage(event, 1, type);
        }

        if (event.getComponentId().contains("replyRemoveBackwards")){

            if (!ManageJSON.checkPermission(event, "autoreply")) {
                event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                return;
            }

            int type = Character.getNumericValue(event.getComponentId().toCharArray()[event.getComponentId().length()-1]);

            Autoreply.turnPage(event, -1, type);
        }

        switch (event.getComponentId()){

            case "applyBanner":

                if (!ManageJSON.checkPermission(event, "bannervoteadmin")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }

                String messageID = event.getMessageId();

                BannerVoteData bannerVote = ManageJSON.getBannerVoteData().stream().filter(id -> messageID.equals(id.getMessageID())).findFirst().orElse(null);

                byte[] banner;

                try {
                    banner = bannerVote.getBannerJSON();
                } catch (NullPointerException e) {
                    event.reply(Messages.GENERICFATALERROR + " " + Thread.currentThread().getStackTrace()[1].getMethodName()).setEphemeral(true).queue();
                    e.printStackTrace();
                    return;
                }


                Icon iconBanner;
                iconBanner = Icon.from(banner);

                try {
                    event.getGuild().getManager().setBanner(iconBanner).queue();
                } catch (IllegalStateException e) {
                    event.reply("**Couldn't update banner**").setEphemeral(true).queue();
                    return;
                }

                event.reply("**Banner changed!!**").setEphemeral(true).queue();

                break;

            case "likeBanner":

                BannerVote.changeBannerVoteRatings(1,0,event);

                break;

            case "dislikeBanner":

                BannerVote.changeBannerVoteRatings(0,1,event);

                break;


        }

    }

}
