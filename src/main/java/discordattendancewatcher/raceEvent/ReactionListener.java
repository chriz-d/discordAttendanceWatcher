package discordattendancewatcher.raceEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter {
    
    private WatchedMessageManager msgMan;
    
    public ReactionListener(WatchedMessageManager msgMan) {
        this.msgMan = msgMan;
    }
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        long msgId = event.getMessageIdLong();

        if(!msgMan.isWatchingMessage(msgId)) {
            event.reply("Something went wrong. Please contact the bot owner.").queue();
            return;
        }

        if(event.getComponentId().equals("attend")) {
            attendButtonPressed(event);
        } else if(event.getComponentId().equals("absent")) {
            absendButtonPressed(event);
        }
    }

    private void attendButtonPressed(ButtonInteractionEvent event) {
        long msgId = event.getMessageIdLong();
        Member memberWhoClicked = event.getMember();
        WatchedMessage ws = msgMan.getWatchedMessage(msgId);
        // Member lastDriverBefore = ws.getAttendees().get(WatchedMessage.MAX_DRIVERS - 1);
        msgMan.markAttendance(msgId, memberWhoClicked);
        // Member lastDriverAfter = ws.getAttendees().get(WatchedMessage.MAX_DRIVERS - 1);

        // Edit message
        event.editMessageEmbeds(MessageBuilder.createMessage(ws)).queue();

        // Respond to action
        if((ws.getAttendees().indexOf(memberWhoClicked) + 1) > WatchedMessage.MAX_DRIVERS) {
            event.getHook().sendMessage("You have marked your attendance, but the event is full already. You have been placed in a waiting queue.").setEphemeral(true).queue();
        } else {
            event.getHook().sendMessage("You have marked your attendance.").setEphemeral(true).queue();
        }

        // By attending someone may has been pushed into the waiting queue, notify them
        // if(lastDriverBefore.equals(ws.getAttendees().get(WatchedMessage.MAX_DRIVERS))) {
        //     ws.getAttendees().get(WatchedMessage.MAX_DRIVERS).getUser().openPrivateChannel().complete()
        //         .sendMessage("Due to priority of full time drivers you have been moved into the waiting queue in the following event: " + event.getMessage().getJumpUrl()).queue();
        // }
    }

    private void absendButtonPressed(ButtonInteractionEvent event) {
        long msgId = event.getMessageIdLong();
        Member memberWhoClicked = event.getMember();
        WatchedMessage ws = msgMan.getWatchedMessage(msgId);

        // Member lastDriverBefore = ws.getAttendees().get(WatchedMessage.MAX_DRIVERS - 1);
        msgMan.markAbsence(msgId, memberWhoClicked);
        // Member lastDriverAfter = ws.getAttendees().get(WatchedMessage.MAX_DRIVERS - 1);
        event.editMessageEmbeds(MessageBuilder.createMessage(ws)).queue();
        event.getHook().sendMessage("You have marked your absence.").setEphemeral(true).queue();

        // Someone marked his absence, notify potentially waiting people about slot
        // if(!lastDriverBefore.equals(lastDriverAfter)) {
        //     lastDriverAfter.getUser().openPrivateChannel().complete()
        //         .sendMessage("A slot has become available and you have been moved into attending in the following event: " + event.getMessage().getJumpUrl()).queue();
        // }
    }
}
