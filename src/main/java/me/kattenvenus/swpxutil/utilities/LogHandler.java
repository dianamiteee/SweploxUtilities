package me.kattenvenus.swpxutil.utilities;

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

}
