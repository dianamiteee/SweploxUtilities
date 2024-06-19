package me.kattenvenus.swpxutil.listeners;

import me.kattenvenus.swpxutil.commands.Autoreply;
import me.kattenvenus.swpxutil.datatypes.Messages;
import me.kattenvenus.swpxutil.utilities.ManageServerData;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ModalListener extends ListenerAdapter {
    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().contains("replyAdd")) {

            String replyAddKeyRes = event.getValue("replyAddKeyRes").getAsString();
            String replyAddValueRes = event.getValue("replyAddValueRes").getAsString();

            int type = Character.getNumericValue(event.getModalId().toCharArray()[event.getModalId().length()-1]);

            Autoreply.addMessage(replyAddKeyRes, replyAddValueRes, type);
            event.reply("**Autoreply added! uwu**").setEphemeral(true).queue();
        }
    }

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        if (event.getComponentId().equals("replyAddChannel")) {
            Autoreply.addChannel(event);
        }
    }

    @Override
    public void onMessageContextInteraction(MessageContextInteractionEvent event) {

        System.out.println("[" + event.getGuild().getName() + "] " + "[" + event.getChannel().getName() + "] " + event.getMember().getUser().getEffectiveName() + " PRESSED APP: " + event.getName());

        if (event.getName().equals("Add as reaction autoreply")) {

            if (!ManageServerData.checkPermission(event, "autoreply")) {
                event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                return;
            }

            Autoreply.addReaction(event, 2);
        }

        if (event.getName().equals("Add as reaction autoreply EXACT")) {

            if (!ManageServerData.checkPermission(event, "autoreply")) {
                event.reply(Messages.NOTPERMITTED).setEphemeral(true).queue();
                return;
            }

            Autoreply.addReaction(event, 3);
        }
    }

}
