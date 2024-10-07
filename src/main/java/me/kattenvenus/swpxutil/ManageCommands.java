package me.kattenvenus.swpxutil;

import me.kattenvenus.swpxutil.commands.*;
import me.kattenvenus.swpxutil.datatypes.Messages;
import me.kattenvenus.swpxutil.datatypes.ServerData;
import me.kattenvenus.swpxutil.utilities.LogHandler;
import me.kattenvenus.swpxutil.utilities.ManageJSON;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManageCommands extends ListenerAdapter {

    private List<CommandData> commandData = new ArrayList<>();

    public ManageCommands() {

        //TEST
        OptionData optionSet = new OptionData(OptionType.STRING, "set", "Sets your nitrocolor");
        commandData.add(Commands.slash("test", "HAHAHA AOMGUOS HWHHWAWHHWADAHWADWHNHDWAH").addOptions(optionSet));

        //NITROCOLOR
        List<SubcommandData> NCsubCommands = new ArrayList<>();
        NCsubCommands.add(new SubcommandData("set", "Sets your Nitrocolor").addOptions(new OptionData(OptionType.STRING, "hex", "Your desired color in HEX, ex. '3e4eb4' or '#FCBACB'", true)));
        NCsubCommands.add(new SubcommandData("remove", "Removes your Nitrocolor"));
        NCsubCommands.add(new SubcommandData("gethex", "Gets the hex value of your current Nitrocolor").addOptions(new OptionData(OptionType.USER, "user", "Which user you want to see their NC's colour!", false)));
        commandData.add(Commands.slash("nitrocolor", "Changes your nitrocolor").addSubcommands(NCsubCommands));


        //ADMIN NITROCOLOR
        List<SubcommandData> ANCsubCommands = new ArrayList<>();
        ANCsubCommands.add(new SubcommandData("set", "Sets the nitrocolor of a user")
                .addOptions(
                        new OptionData(OptionType.STRING, "hex", "Your desired color in HEX, ex. '3e4eb4' or '#FCBACB'", true),
                        new OptionData(OptionType.USER, "user", "Affected user", true)
                ));
        ANCsubCommands.add(new SubcommandData("remove", "Removes the nitrocolor of a user")
                .addOptions(
                        new OptionData(OptionType.USER, "user", "Affected user", true)
                ));
        ANCsubCommands.add(new SubcommandData("refresh", "Forcibly refreshes Nitrocolors"));
        commandData.add(Commands.slash("adminnitrocolor", "Manage nitrocolors")
                .addSubcommands(ANCsubCommands)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES)));

        //VCROULETTE
        commandData.add(Commands.slash("vcroulette", "Roll the dice of fate for your current channel..."));

        //HANDLEROFCUTLERY
        commandData.add(Commands.slash("handlerofcutlery", "Sometimes the adults need to take the knives away")
                .addSubcommands(new SubcommandData("toggle", "TAKE THE KNIVES AWAY")
                        .addOptions(new OptionData(OptionType.USER, "user", "The naughty (or not naughty) user", true)))
                .addSubcommands(new SubcommandData("list", "List the naughty children"))
                .addSubcommands(new SubcommandData("toggleforall", "NO ONE IS ALLOWED TO PLAY"))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES)));

        //JSONINIT
        commandData.add(Commands.slash("jsoninit", "RESETS JSON FILE")
                .addOptions(new OptionData(OptionType.STRING, "validation", "Type 'I am sure' to validate this isn't a mistake", true))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));

        //JSONRELOAD
        commandData.add(Commands.slash("jsonreload", "Reloads json").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));

        //SWPXPERMS
        List<SubcommandData> PermsSubCommands = new ArrayList<>();
        PermsSubCommands.add(new SubcommandData("togglepermission", "Toggles a permission node for a user or role")
                .addOptions(
                        new OptionData(OptionType.STRING, "permissionnode", "The permission node you want to toggle for", true),
                        new OptionData(OptionType.USER, "user", "Affected user, choose either user or role"),
                        new OptionData(OptionType.ROLE, "role", "Affected role, choose either user or role")
                ));
        PermsSubCommands.add(new SubcommandData("listnodes", "List available permission nodes"));
        commandData.add(Commands.slash("swpxperms", "Manage Sweplox Util permissions")
                .addSubcommands(PermsSubCommands)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));


        //VERIFY
        List<SubcommandData> VerifySubCommands = new ArrayList<>();
        VerifySubCommands.add(new SubcommandData("newuser", "Chooses the user to be verified")
                .addOptions(
                        new OptionData(OptionType.USER, "user", "The user to be verified", true),
                        new OptionData(OptionType.STRING, "reason", "Reason for verifying", true)
                ));
        VerifySubCommands.add(new SubcommandData("edit", "Edits a previous verification in case of error")
                .addOptions(
                        new OptionData(OptionType.STRING, "messageid", "Having developer mode enabled, right click the message and copy id", true),
                        new OptionData(OptionType.STRING, "newmessage", "The text to replace the old reason", true)
                ));
        VerifySubCommands.add(new SubcommandData("setdefaultchannel", "Sets the channel for verified messages to be sent to")
                .addOptions(
                        new OptionData(OptionType.CHANNEL, "channel", "Selected channel", true)
                ));
        VerifySubCommands.add(new SubcommandData("setdefaultrole", "Sets the channel for verified messages to be sent to")
                .addOptions(
                        new OptionData(OptionType.ROLE, "role", "Selected role", true)
                ));
        commandData.add(Commands.slash("verify", "Verify a member").addSubcommands(VerifySubCommands));

        //Bannervote
        List<SubcommandData> BannerVoteSubCommands = new ArrayList<>();
        BannerVoteSubCommands.add(new SubcommandData("new", "Create a new banner vote")
                .addOptions(
                        new OptionData(OptionType.ATTACHMENT, "banner", "The banner for the vote!", true),
                        new OptionData(OptionType.STRING, "description", "If you want to say something about your creation", false)
                ));
        BannerVoteSubCommands.add(new SubcommandData("cancel", "Lookups the reason for verifiction"));
        commandData.add(Commands.slash("bannervote", "Make a vote for the next banner!").addSubcommands(BannerVoteSubCommands));

        //Admin bannervote
        List<SubcommandData> AdminBannerVoteSubCommands = new ArrayList<>();
        AdminBannerVoteSubCommands.add(new SubcommandData("forcecancel", "Cancel another users Bannervote")
                .addOptions(
                        new OptionData(OptionType.USER, "user", "The user to cancel the Bannervote for", true)
                ));
        AdminBannerVoteSubCommands.add(new SubcommandData("ban", "Bans a user from making banner votes")
                .addOptions(
                        new OptionData(OptionType.USER, "user", "The user to ban from banner voting", true)
                ));
        AdminBannerVoteSubCommands.add(new SubcommandData("unban", "Unbans a user from making Bannervotes")
                .addOptions(
                        new OptionData(OptionType.USER, "user", "The user to unban from banner voting", true)
                ));
        AdminBannerVoteSubCommands.add(new SubcommandData("setdefaultchannel", "Sets the channel where Bannervotes are posted")
                .addOptions(
                        new OptionData(OptionType.CHANNEL, "channel", "Said channel", true)
                ));
        AdminBannerVoteSubCommands.add(new SubcommandData("resetbannervotejson", "Removes the bot knowledge of active Bannervotes")
                .addOptions(
                        new OptionData(OptionType.STRING, "validation", "Write \"I am sure\" if you're, well, sure.", true)
                ));
        AdminBannerVoteSubCommands.add(new SubcommandData("listbannedusers", "Lists all banned users"));
        commandData.add(Commands.slash("adminbannervote", "Manage Bannervotes!")
                .addSubcommands(AdminBannerVoteSubCommands)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES)));

        //Autoreply
        List<SubcommandData> autoReplySubCommands = new ArrayList<>();
        autoReplySubCommands.add(new SubcommandData("add", "Starts the add auto reply wizard"));
        autoReplySubCommands.add(new SubcommandData("remove", "Starts the remove auto reply wizard"));
        autoReplySubCommands.add(new SubcommandData("list", "Lists channels, emojis or messages"));
        commandData.add(Commands.slash("autoreply", "Manage auto replies!")
                .addSubcommands(autoReplySubCommands)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES)));


    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        System.out.println("[" + event.getGuild().getName() + "] " + "[" + event.getChannel().getName() + "] " + event.getMember().getUser().getEffectiveName() + " EXECUTED: " + event.getName());

        boolean access = false;

        switch (event.getName()) {

            case "test":

                //Autoreply.listMessages(event);

                break;
            case "nitrocolor":

                try {
                    for (var s : event.getMember().getRoles()) {
                        if (s.getName().equalsIgnoreCase("Nitro Booster")) {
                            access = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    event.reply(Messages.GENERICFATALERROR + "Error checking permission: " + e);
                    e.printStackTrace();
                    return;
                }

                if (!access) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    NitroColor.refreshNitroColors(event.getGuild(), null);
                    return;
                }

                //Nitrocolor SET
                if (Objects.equals(event.getSubcommandName(), "set")) {
                    NitroColor.setNitroColor(event);
                    return;
                }

                //Nitrocolor REMOVE
                if (Objects.equals(event.getSubcommandName(), "remove")) {
                    NitroColor.removeNitroColor(event);
                    return;
                }

                //Nitrocolor GETHEX
                if (Objects.equals(event.getSubcommandName(), "gethex")) {
                    NitroColor.getNitroHex(event);
                    return;
                }

                break;

            case "adminnitrocolor":

                if (!ManageJSON.checkPermission(event, "adminnitrocolor")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }

                //Adminnitrocolor SET
                if (Objects.equals(event.getSubcommandName(), "set")) {
                    NitroColor.setNitroColor(event, event.getOption("user").getAsUser());
                    return;
                }

                //Adminnitrocolor REMOVE
                if (Objects.equals(event.getSubcommandName(), "remove")) {
                    NitroColor.removeNitroColor(event, event.getOption("user").getAsMember());
                    return;
                }

                //Adminnitrocolor REFRESH
                if (Objects.equals(event.getSubcommandName(), "refresh")) {
                    NitroColor.refreshNitroColors(event.getGuild(), null);
                    event.reply("Nitrocolors refreshed!").queue();
                    return;
                }

                break;

            case "verify":


                if (!ManageJSON.checkPermission(event, "verify")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }

                //Verify newuser // original verify
                if (Objects.equals(event.getSubcommandName(), "newuser")) {
                    Verify.verifyUser(event);
                    return;
                }

                //Verify edit
                if (Objects.equals(event.getSubcommandName(), "edit")) {
                    Verify.edit(event);
                    return;
                }

                if (!ManageJSON.checkPermission(event, "verifyadmin")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }

                //AdminVerify setdefaultchannel
                if (Objects.equals(event.getSubcommandName(), "setdefaultchannel")) {
                    Verify.setVerifiedChannel(event);
                    return;
                }

                //AdminVerify setdefaultrole
                if (Objects.equals(event.getSubcommandName(), "setdefaultrole")) {
                    Verify.setVerifiedRole(event);
                    return;
                }

                break;

            case "vcroulette":

                try {

                    if (!ManageJSON.getServerData().isKnifeParty()) {
                        event.reply("No knives are available").queue();
                        return;
                    }

                    if (ManageJSON.isOnDeathList(event.getUser())) {
                        event.reply(event.getUser().getAsMention() + " **tried to play baddie without a knife lmao**").queue();
                        return;
                    }
                } catch (Exception e) {
                    event.reply(Messages.GENERICFATALERROR + "Error checking permissions: " + e);
                    e.printStackTrace();
                    return;
                }

                diceOfDeath(event);
                break;

            case "handlerofcutlery":


                if (!ManageJSON.checkPermission(event, "handlerofcutlery")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }


                if (Objects.equals(event.getSubcommandName(), "toggle")) {

                    handleCutlery(event);

                }

                if (Objects.equals(event.getSubcommandName(), "list")) {

                    listCutlery(event);

                }

                if (Objects.equals(event.getSubcommandName(), "toggleforall")) {

                    ServerData currentData = ManageJSON.getServerData();

                    if (currentData.isKnifeParty()) {
                        currentData.setKnifeParty(false);
                        event.reply("**NO ONE CAN NOW PLAY**").setEphemeral(true).queue();
                    } else {
                        currentData.setKnifeParty(true);
                        event.reply("tihi uwu knives for everyoneeee uwuwuwu").setEphemeral(true).queue();
                    }

                    ManageJSON.save();


                }

                break;

            case "jsonreload":

                if (!ManageJSON.checkPermission(event, "jsonreload")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }

                ManageJSON.load();

                event.reply("JSON Reloaded").setEphemeral(true).queue();
                break;
            case "jsoninit":

                try { //If permissions are fucked itll work by typing the password supersecretpassword uwuwuws
                    if (!ManageJSON.checkPermission(event, "jsoninit")) {
                        event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                        return;
                    }
                } catch (Exception e) {

                    if (!event.getOption("validation").getAsString().equalsIgnoreCase("SuperSecretPassword")) {
                        event.reply("unvalidated").setEphemeral(true).queue();
                        return;
                    }

                    ManageJSON.init(event);
                    break;

                }

                if (!event.getOption("validation").getAsString().equalsIgnoreCase("I am sure")) {
                    event.reply("unvalidated").setEphemeral(true).queue();
                    return;
                }

                ManageJSON.init(event);
                break;
            case "swpxperms":

                if (!ManageJSON.checkPermission(event, "swpxperms")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }

                if (Objects.equals(event.getSubcommandName(), "togglepermission")) {

                    Swpxperms.togglePerms(event);

                }

                if (Objects.equals(event.getSubcommandName(), "listnodes")) {

                    Swpxperms.listPerms(event);

                }

                break;
            case "bannervote":

                if (!ManageJSON.checkPermission(event, "bannervote")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }

                for (String s : ManageJSON.getServerData().getBannervoteBannedUsers()) {

                    if (s.equals(event.getUser().getId())) {
                        event.reply("**Thy liberty, " + event.getUser().getAsMention() + ", to utilize Bannervoting hath been revoked**").queue();
                        return;
                    }

                }


                if (Objects.equals(event.getSubcommandName(), "new")) {

                    BannerVote.initBannerVote(event);

                }

                if (Objects.equals(event.getSubcommandName(), "cancel")) {

                    BannerVote.deleteBannerVoteUser(event);

                }


                break;

            case "adminbannervote":

                if (!ManageJSON.checkPermission(event, "bannervoteadmin")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }

                if (Objects.equals(event.getSubcommandName(), "forcecancel")) {
                    BannerVote.deleteBannerVote(event, null);
                }

                if (Objects.equals(event.getSubcommandName(), "setdefaultchannel")) {
                    BannerVote.setDefaultChannel(event);
                }

                if (Objects.equals(event.getSubcommandName(), "ban")) {
                    BannerVote.banUser(event);
                }

                if (Objects.equals(event.getSubcommandName(), "unban")) {
                    BannerVote.pardonUser(event);
                }

                if (Objects.equals(event.getSubcommandName(), "listbannedusers")) {
                    BannerVote.listBannedUsers(event);
                }

                if (Objects.equals(event.getSubcommandName(), "resetbannervotejson")) {
                    BannerVote.resetBannerVoteData(event);
                }

                break;

            case "autoreply":

                if (!ManageJSON.checkPermission(event, "autoreply")) {
                    event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                    return;
                }

                if (Objects.equals(event.getSubcommandName(), "add")) {
                    Autoreply.startMenu(event, 0);
                }

                if (Objects.equals(event.getSubcommandName(), "remove")) {
                    Autoreply.startMenu(event, 1);
                }

                if (Objects.equals(event.getSubcommandName(), "list")) {
                    Autoreply.startMenu(event, 2);
                }

                break;

            default:

                event.reply("Invalid command").setEphemeral(true).queue();

                break;
        }

    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().updateCommands()
                .addCommands(commandData)
                .addCommands(
                        Commands.context(Command.Type.MESSAGE, "Add as reaction autoreply")
                ).addCommands(
                        Commands.context(Command.Type.MESSAGE, "Add as reaction autoreply EXACT"))
                .queue();

        LogHandler.printSystemMessage("Successfully connected to guild: \"" + event.getGuild().getName() + "\"");

    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        event.getGuild().updateCommands().addCommands(commandData).queue();
        event.getGuild().updateCommands().addCommands(
                Commands.slash("fruit", "find a given fruit")
                        .addOption(OptionType.STRING, "name", "fruit to find", true, true)
        ).queue();
        LogHandler.printSystemMessage("Successfully connected to guild: \"" + event.getGuild().getName() + "\"");
    }


    public static void handleCutlery(SlashCommandInteractionEvent event) {

        try {
            User user = event.getOption("user").getAsUser();

            ManageJSON.toggleDeathList(user);
            event.reply("USER " + user.getAsMention() + " HAS BEEN **TOGGLED** FROM ACCESSING KNIVES").setEphemeral(true).queue();

        } catch (NullPointerException e) {
            e.printStackTrace();
            event.reply("Invalid user").setEphemeral(true).queue();
        }

    }

    public static void listCutlery(SlashCommandInteractionEvent event) {

        String slainPeople = "**LIST OF DENIED PEOPLE:** ";

        try {

            if (ManageJSON.getServerData().getKnifePartyBanList().size() < 1) {
                event.reply("**ALL THE PEOPLE HAVE ACCESS TO KNIVES**").setEphemeral(true).queue();
                return;
            }

            for (String s : ManageJSON.getServerData().getKnifePartyBanList()) {
                slainPeople = slainPeople + s + ", ";
            }

        } catch (NullPointerException e) {
            event.reply("ALL THE PEOPLE HAVE ACCESS TO KNIVES (null uwu)").setEphemeral(true).queue();
            return;
        }

        event.reply(slainPeople).setEphemeral(true).queue();

    }

    public static void diceOfDeath(SlashCommandInteractionEvent event) {

        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.reply("You need to be in a voice channel").setEphemeral(true).queue();
            return;
        }

        List<Member> members = event.getMember().getVoiceState().getChannel().getMembers();

        if (members.size() < 1) {
            event.reply("No members in selected channel").setEphemeral(true).queue();
            return;
        }



        Member deathPatient = members.get((int) (Math.random() * members.size()));

        if (Math.random() * 49 == 20) {
            event.reply("Awwwwww, cutie-patootie " + event.getMember().getAsMention() +" is playing with plastic knives, how adorbsssss uwu \uD83E\uDD70");
            return;
        }

        event.getGuild().kickVoiceMember(deathPatient).queue();



        if (event.getMember() != deathPatient) {
            event.reply("Poor fella " + deathPatient.getAsMention() + " has been **SLAUGHTERED** :knife: :drop_of_blood: by the evil monster known as " + event.getMember().getAsMention() + " :smiling_imp:").queue();
        } else {
            event.reply("This idiot " + deathPatient.getAsMention() + " tripped over their own knife (smh) while trying to kill another innocent member :rofl:  :rofl:  :rofl: ").queue();
        }
    }
}
