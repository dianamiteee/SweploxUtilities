package me.kattenvenus.swpxutil.commands;

import me.kattenvenus.swpxutil.datatypes.Messages;
import me.kattenvenus.swpxutil.utilities.ManageServerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Autoreply {

    private static Map<String, String> getMessages(int type) {

        //0: Messages, 1: Messages Exact, 2: Reaction, 3: Reaction Exact, 4: Channel

        System.out.println("GETMESSAGE" + type);

        switch (type) {

            case 0:
                return ManageServerData.getCurrentData().getAutoReplyMessages();
            case 1:
                return ManageServerData.getCurrentData().getAutoReplyMessagesExact();
            case 2:
                return ManageServerData.getCurrentData().getAutoReplyEmojis();
            case 3:
                return ManageServerData.getCurrentData().getAutoReplyEmojisExact();
            case 4:
                return ManageServerData.getCurrentData().getAutoReplyAllowedChannels();

        }

        return null;

    }

    public static void turnPage (ButtonInteractionEvent event, int page, int type) {

        //Turns the page left or right on the removal message page
        //-1 is left, 1 is right uwuwu


        //Looks after the test "page: 1 / 2" and gets the Max page and current
        Pattern pattern = Pattern.compile("page:\\s*(\\d+)\\s*\\/\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(event.getMessage().getContentRaw());
        boolean matchFound = matcher.find();
        if(matchFound) {

            int currentPage = Integer.parseInt(matcher.group(1));
            int maxPage = Integer.parseInt(matcher.group(2));

            if (currentPage + page >= maxPage + 1) { //Stops if user tries to go over maxpage
                event.reply("Hey! **NO PAGE LEFT AAAA**").setEphemeral(true).queue();
                return;
            }

            if (currentPage + page <= 0) {//Stops if user tries to go under 0
                event.reply("Hey! **NO PAGE LEFT AAAA**").setEphemeral(true).queue();
                return;
            }

            StringSelectMenu menu = getRemoveMessage(currentPage + page, type); //Gets the message list

            if (menu == null) {
                event.reply(Messages.GENERICFATALERROR + " " + Thread.currentThread().getStackTrace()[1].getMethodName()).setEphemeral(true).queue();
                return;
            }


            event.getMessage().editMessage("Choose which reply to **REMOVE** \n" + getPageText(currentPage + page, type)).queue();

            if (maxPage > 1){ //Only show the buttons if theres more than one page
                Button button1 = Button.primary("replyRemoveBackwards"+type, "< Prev page");
                Button button2 = Button.success("replyRemoveForwards"+type, "Next page >");
                event.getMessage().editMessageComponents(ActionRow.of(menu), ActionRow.of(button1, button2)).queue();
                event.deferEdit().queue();
                return;
            }

            event.getMessage().editMessageComponents(ActionRow.of(menu)).queue();
            event.deferEdit().queue(); //shhhh

        } else {
            event.reply(Messages.GENERICFATALERROR + " " + Thread.currentThread().getStackTrace()[1].getMethodName()).setEphemeral(true).queue();
        }
    }

    public static void reply(MessageReceivedEvent event) {

        if (event.getMember() == null) {
            return;
        }

        if (event.getMember().getUser().getId().equals(event.getJDA().getSelfUser().getId())){ //Bot no reply to itself
            return;
        }

        boolean allowedChannel = false;

        for (String s : ManageServerData.getCurrentData().getAutoReplyAllowedChannels().keySet()) { //Checks if the message was sent in an allowed channel
            if (s.equalsIgnoreCase(event.getChannel().getId())) allowedChannel = true;
        }

        if (!allowedChannel) return;


        for (String s: ManageServerData.getCurrentData().getAutoReplyMessagesExact().keySet()) {
            if (s.equalsIgnoreCase(event.getMessage().getContentRaw().toLowerCase())) {
                event.getMessage().reply(ManageServerData.getCurrentData().getAutoReplyMessagesExact().get(s)).queue();
                return;
            }
        }

        for (String s: ManageServerData.getCurrentData().getAutoReplyMessages().keySet()) {
            if (event.getMessage().getContentRaw().toLowerCase().contains(s.toLowerCase())) {
                event.getMessage().reply(ManageServerData.getCurrentData().getAutoReplyMessages().get(s)).queue();
                return;
            }
        }

        for (String s: ManageServerData.getCurrentData().getAutoReplyEmojisRaw().keySet()) {
            if (event.getMessage().getContentRaw().toLowerCase().contains(s.toLowerCase())) {

                for (String ss : ManageServerData.getCurrentData().getAutoReplyEmojisRaw().get(s)) {

                    event.getMessage().addReaction(Emoji.fromFormatted(ss)).submit()
                            .whenComplete((success, error) -> {
                                if (error != null) System.out.println("[ERROR] Tried to add unknown emoji");
                            });

                }

                return;

            }
        }

        for (String s: ManageServerData.getCurrentData().getAutoReplyEmojisExactRaw().keySet()) {
            if (event.getMessage().getContentRaw().equalsIgnoreCase(s)) {

                for (String ss : ManageServerData.getCurrentData().getAutoReplyEmojisExactRaw().get(s)) { //Looks through all emojis from map

                    event.getMessage().addReaction(Emoji.fromFormatted(ss)).submit()
                            .whenComplete((success, error) -> {

                                if (error != null) System.out.println("[ERROR] Tried to add unknown emoji");

                            });

                }

                return;

            }
        }

    }

    public static void listMessages(StringSelectInteractionEvent event, int type) {

        String titlemsg;

        switch (type) {
            case 0:
                titlemsg = "CURRENT AUTO REPLY MESSAGES";
                break;
            case 1:
                titlemsg = "CURRENT EXACT AUTO REPLY MESSAGES";
                break;
            case 2:
                titlemsg = "CURRENT REACTION AUTO REPLY MESSAGES";
                break;
            case 3:
                titlemsg = "CURRENT REACTION EXACT AUTO REPLY MESSAGES";
                break;
            case 4:
                titlemsg = "CURRENT ALLOWED CHANNELS";
                break;
            default:
                titlemsg = "CURRENT AUTO REPLY MESSAGES";
        }

        if (getMessages(type).keySet().size() == 0) {
            event.reply("## No auto replies available for this category!").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(titlemsg, null);
        eb.setColor(new Color(0xe36bd5));

        eb.setAuthor("Sweplox messaging™", null, "https://swpx.se/sweploxpride.jpg");
        eb.setThumbnail("https://swpx.se/sweploxpride.jpg");

        StringBuilder sb = new StringBuilder();

        sb.append("\n");

        int index = 0;
        for (String s: getMessages(type).keySet()) {

            String key = s;
            String value = getMessages(type).get(s);


            if (key.length() > 40) //Messages cant be too long
                key = key.substring(0, 40);

            if (value.length() > 40) //Messages cant be too long
                value = value.substring(0, 40);



            index++;
            sb.append(index).append(". **").append(key).append("**\n");
            sb.append(value);
            sb.append("\n\n");

        }

        eb.addField("Messages, first what to looks for, then the reply", sb.toString(), false);


        event.replyEmbeds(eb.build()).setEphemeral(true).queue();

    }

    public static void removeMessage(StringSelectInteractionEvent event, int page, int type) {

        //Only shows for the first page, otherwise turnPage takes over

        StringSelectMenu menu = getRemoveMessage(page, type);

        if (menu == null) {
            event.reply("**No autoreplies/channels available!**").setEphemeral(true).queue();
            return;
        }

        int pages = ManageServerData.getCurrentData().getAutoReplyMessages().size() / 26;

        System.out.println(pages);
        System.out.println(ManageServerData.getCurrentData().getAutoReplyMessages().size());

        if (pages > 0) { //Only shows buttons if there is more than 1 page
            Button button1 = Button.primary("replyRemoveBackwards"+type, "< Prev page");
            Button button2 = Button.success("replyRemoveForwards"+type, "Next page >");

            event.reply("Choose which reply to **REMOVE** \n" + getPageText(page, type)).addActionRow(menu).addActionRow(button1, button2).setEphemeral(true).queue();
            return;
        }

        event.reply("Choose which reply to **REMOVE** \n" + getPageText(page,type)).addActionRow(menu).setEphemeral(true).queue();
        event.getMessage().delete().queue();

    }

    public static String getPageText(int currentPage, int type) {

        //Gets the small text that says "page 1 / 2"

        int pages = getMessages(type).size() / 26;
        String pageText = "";

        if (pages >= 1){
            int pageUwu = currentPage;
            int pageUwuu = pages+1;
            pageText = "``page: " + pageUwu + " / " + pageUwuu + "``";
        }

        return pageText;

    }


    public static void backendDeleteReply(int pos, StringSelectInteractionEvent event, int type) {

        int index = 0;
        for (String s:getMessages(type).keySet()) {

            if (index == pos - 1) {
                System.out.println(s);

                if (type == 2) { //Reaction autoreplys
                    ManageServerData.getCurrentData().getAutoReplyEmojisRaw().remove(s);
                } else if (type == 3) { //Reaction EXACT autoreplys
                    ManageServerData.getCurrentData().getAutoReplyEmojisExactRaw().remove(s);
                } else {
                    getMessages(type).remove(s);
                }

                ManageServerData.save();
                event.reply("**Auto reply removed!**").setEphemeral(true).queue();
                event.getMessage().delete().queue();
                return;
            }

            index++;

        }

        event.reply("**Couldn't find message!**").queue();

    }



    public static StringSelectMenu getRemoveMessage(int page, int type){

        //Returns the menu with the messages so that it can be used otherwise

        page--;

        System.out.println("HAHAHAHA" + getMessages(type).size() + "  " + page);

        if (getMessages(type).size() == 0) {
            return null;
        }

        StringSelectMenu.Builder menu = StringSelectMenu.create("dynamicRemoveMessage"+type);

        //Max page is where we want to stop, index is where we are rn and I couldnt figure out how to use a normal while loop for this so were skipping indexes until we get to where we want
        //Trueindex always goes up, its the i in a for loop
        //Discord API wont let a menu have more than 25 options

        int maxPage = page*24+24;
        int index = page*24;
        int trueIndex = 0;

        for (String s: getMessages(type).keySet()) {

            if (trueIndex < index) {
                trueIndex++;
                continue;
            }

            trueIndex++;

            String key = s;
            String value = getMessages(type).get(s);

            if (s.length() > 99) //Messages cant be too long
                key = key.substring(0, 99);

            if (value.length() > 99) //Nor the value
                value = value.substring(0, 99);

            menu.addOption(key,"dynamicRemoveMessageListing" + trueIndex, value);

            if (index >= maxPage) { //Looks if its over max number of pages, if so return
                return menu.build();
            }

            index++;

        }

        return menu.build();

    }

    public static void addReaction(MessageContextInteractionEvent event, int type) {

        Map<String, String[]> map;

        switch (type) {
            case 2:
                map = ManageServerData.getCurrentData().getAutoReplyEmojisRaw();
                break;
            case 3:
                map = ManageServerData.getCurrentData().getAutoReplyEmojisExactRaw();
                break;
            default:
                event.reply(Messages.GENERICFATALERROR + " " + Thread.currentThread().getStackTrace()[1].getMethodName()).setEphemeral(true).queue();
                return;
        }

        String msg = event.getTarget().getContentRaw();
        var reactions = event.getTarget().getReactions();

        if (reactions.size() == 0) {
            event.reply("**No reactions on message!!** Kinda feels useless reaction with it then lmao").setEphemeral(true).queue();
            return;
        }

        for (String s: map.keySet()) {

            if (s.equalsIgnoreCase(msg)) {
                event.reply("**The bot is already looking for that word!**\nRemove that before trying to add this new one!").setEphemeral(true).queue();
                return;
            }

        }

        ArrayList<String> reactionsParsed = new ArrayList<>();

        for (var s : reactions) {
            reactionsParsed.add(s.getEmoji().getFormatted());
        }

        map.put(msg, reactionsParsed.toArray(new String[0]));
        ManageServerData.save();

        event.reply("**Reaction autoreply added!**").setEphemeral(true).queue();

    }



    public static void startMenu(SlashCommandInteractionEvent event, int menu) {

        //0: add, 1: remove, 2: list

        StringSelectMenu menuReply;

        String replyMsg = "";

        switch (menu) {

            case 0:

                Emoji zakFfs;

                try {
                    zakFfs = Emoji.fromFormatted("<:yeah:1229457091299905658>");
                } catch (Exception e) {
                    zakFfs = Emoji.fromUnicode("U+1F602");
                }

                menuReply = StringSelectMenu.create("replyAdd")
                        .addOption("Message","replyMessage", "For beauties like \"TROR DET GÖR MAN I KIORYUKAN!\"", Emoji.fromUnicode("U+1F521"))
                        .addOption("Message EXACT","replyMessageExact", "Looks for an EXACT message, otherwise it wont respond", Emoji.fromUnicode("U+1F520"))
                        .addOption("Reaction","replyReaction", "If you feel like the only way of expression is emojis", Emoji.fromUnicode("U+1F602"))
                        .addOption("Reaction EXACT","replyReactionExact", "Looks for an EXACT message, otherwise it wont respond", zakFfs)
                        .addOption("Allowed Channels","replyChannel", "Chooses which channels the bot replies to", Emoji.fromUnicode("U+1F480"))
                        .build();

                replyMsg = "**Choose which auto reply category you want to add**";

                break;

            case 1:

                menuReply = StringSelectMenu.create("replyRemove")
                        .addOption("Message","replyRemoveMessage", "For removing beauties like \"TROR DET GÖR MAN I KIORYUKAN!\"", Emoji.fromUnicode("U+1F521"))
                        .addOption("Message EXACT","replyRemoveMessageExact", "For removing EXACT BEAUTIES ;;!\"", Emoji.fromUnicode("U+1F521"))
                        .addOption("Reaction","replyRemoveReaction", "DONT KILL THE EMOJIS AAAA", Emoji.fromUnicode("U+1F602"))
                        .addOption("Reaction EXACT","replyRemoveReactionExact", "If the EMOJIS SCREAM AAA", Emoji.fromUnicode("U+1F602"))
                        .addOption("Allowed Channels","replyRemoveChannel", "When autoreply is a little bit too annoying tbh", Emoji.fromUnicode("U+1F480"))
                        .build();

                replyMsg = "**Choose which auto reply category you want to remove**";

                break;

            case 2:

                menuReply = StringSelectMenu.create("replyList")
                        .addOption("Message","replyListMessage", "For LISTING beauties like \"TROR DET GÖR MAN I KIORYUKAN!\"", Emoji.fromUnicode("U+1F521"))
                        .addOption("Message EXACT","replyListMessageExact", "For LISTING EXACT beauties like \"TROR DET GÖR MAN I KIORYUKAN!\"", Emoji.fromUnicode("U+1F521"))
                        .addOption("Reaction","replyListReaction", "Basically emojipedia", Emoji.fromUnicode("U+1F602"))
                        .addOption("Message EXACT","replyListReactionExact", "Our (the peoples) EXACT emojipedia", Emoji.fromUnicode("U+1F602"))
                        .addOption("Allowed Channels","replyListChannel", "WHY IS IT REPLYING THERE AAAAAZ", Emoji.fromUnicode("U+1F480"))
                        .build();

                replyMsg = "**Choose which auto reply category you want to list**";

                break;

            default:

                return;

        }

        event.reply(replyMsg).addActionRow(menuReply).setEphemeral(true).queue();

    }

    public static void addChannel(EntitySelectInteractionEvent event) {

        Channel channel = (Channel)event.getValues().get(0);
        ManageServerData.getCurrentData().getAutoReplyAllowedChannels().put(channel.getId(),channel.getName());
        ManageServerData.save();

        event.reply("**Channel has been allowed!**").setEphemeral(true).queue();
        event.getMessage().delete().queue();

    }

    public static void openRequestPrompt(StringSelectInteractionEvent event, int type) {

        if (type == 2 || type == 3) { //Reaction uses another system
            event.reply("# Can't add an emoji autoreply this way!\n" +
                    "## Instead, follow these steps:\n" +
                    "\n" +
                    "**1.** Write a message with what you want the bot to look for\n" +
                    "**2.** Add up to **5** reactions to that message\n" +
                    "**3.** Right click that message, go to Apps -> ``Add as reaction autoreply / EXACT``\n" +
                    "\n" +
                    "thank c:").setEphemeral(true).queue();
            return;
        }

        if (type == 4) {

            event.reply("## Choose a channel that the bot looks in for autoreply")
                    .addActionRow(
                            EntitySelectMenu.create("replyAddChannel", EntitySelectMenu.SelectTarget.CHANNEL)
                                    .build())
                    .setEphemeral(true).queue();

            return;
        }


        //Opens a modal for the use to enter the auto response in

        TextInput subject = TextInput.create("replyAddKeyRes", "What to look for", TextInputStyle.SHORT)
                .setPlaceholder("What should the bot look for?")
                .setMinLength(1)
                .setMaxLength(1000) // or setRequiredRange(10, 100)
                .build();

        TextInput body = TextInput.create("replyAddValueRes", "Response to the message", TextInputStyle.PARAGRAPH)
                .setPlaceholder("What the bot replies with if it sees the message")
                .setMinLength(1)
                .setMaxLength(1000)
                .build();

        Modal modal = Modal.create("replyAdd"+type, "Add an auto reply")
                .addComponents(ActionRow.of(subject), ActionRow.of(body))
                .build();

        event.replyModal(modal).queue();

    }

    public static void addMessage(String key, String value,int type) {

        getMessages(type).put(key, value);
        ManageServerData.save();

    }

}
