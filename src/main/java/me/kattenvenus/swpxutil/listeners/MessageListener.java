package me.kattenvenus.swpxutil.listeners;

import me.kattenvenus.swpxutil.commands.Autoreply;
import me.kattenvenus.swpxutil.commands.BannerVote;
import me.kattenvenus.swpxutil.utilities.LogHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        try {
            LogHandler.printGuildMessage(event.getGuild().getName(), event.getChannel().getName(), event.getMember().getUser().getEffectiveName(), event.getMessage().getContentRaw());
        } catch (NullPointerException e) {
            System.out.println("[Failed] Failed to print message");
        }


        BannerVote.updateBannerVotes(event.getGuild());
        Autoreply.reply(event);

    }

}
