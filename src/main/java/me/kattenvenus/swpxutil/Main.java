package me.kattenvenus.swpxutil;

import me.kattenvenus.swpxutil.listeners.ButtonListeners;
import me.kattenvenus.swpxutil.listeners.MessageListener;
import me.kattenvenus.swpxutil.listeners.ModalListener;
import me.kattenvenus.swpxutil.utilities.LogHandler;
import me.kattenvenus.swpxutil.utilities.ManageJSON;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;


public class Main {

    private ShardManager shardManager;

    public ShardManager getShardManager() {
        return shardManager;
    }

    public Main() throws LoginException {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(System.getenv("API_KEY"));

        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.listening("Paralyserade (unreleased)"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MODERATION, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        shardManager = builder.build();



        shardManager.addEventListener(new ManageCommands(), new ButtonListeners(), new MessageListener(), new ModalListener());
    }

    public static void main(String[] args) {

            try {
                new Main();
                ManageJSON.load();
                System.out.println("\n#####################################################\n" +
                        "Successfully initialized Sweplox Utilites by Dianamite" +
                        "\nVersion: 1.0.1" +
                        "\n#####################################################\n");
            } catch (InvalidTokenException e) {
                LogHandler.printErrorMessage("Provided bot token is invalid, make sure to set -API_KEY=yourBotApiKey as startup variable!");
            } catch (LoginException e) {
                LogHandler.printErrorMessage("Unable to start bot");
                e.printStackTrace();
            }

    }


}