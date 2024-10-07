package me.kattenvenus.swpxutil.utilities;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class LogHandler {

    public static void printSystemMessage(String msg) {
        System.out.println("###### [System] " + msg + " ######");
    }

    public static void printErrorMessage(String msg) {
        System.out.println("###### [System] <ERROR> " + msg + " ######");
    }

    public static void printGuildMessage(String guild, String channel, String user, String msg) {
        System.out.println("["+ guild +" @ "+channel+"] " + user + ": " + msg);
    }

    public static void replyErrorMessage(GenericCommandInteractionEvent event, String command, String error) {

        event.reply("`ERROR - " + command.toUpperCase() + "`\n**" + error+ "**").setEphemeral(true).queue();

    }

    public static void replyErrorMessageWithThread(GenericCommandInteractionEvent event, String command, StackTraceElement thread) {

        event.reply("`ERROR - " + command.toUpperCase() + "`\n**To help, show this to an admin:** *" + thread.getMethodName() + " " + thread.getLineNumber()+"*").setEphemeral(true).queue();

    }

    public static void replySystemMessage(GenericCommandInteractionEvent event, String command, String msg) {

        event.reply("`" + command.toUpperCase() + "`\n**" + msg + "**").setEphemeral(true).queue();

    }

}
