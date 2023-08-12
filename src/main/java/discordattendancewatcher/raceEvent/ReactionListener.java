package discordattendancewatcher.raceEvent;

import net.dv8tion.jda.api.entities.User;
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
        User userWhoClicked = event.getUser();
        WatchedMessage ws = msgMan.getWatchedMessage(msgId);
        
        msgMan.markAttendance(msgId, userWhoClicked);


        // Edit message
        event.editMessageEmbeds(MessageBuilder.createMessage(ws)).queue();
        if(ws.getAttendees().size() > WatchedMessage.MAX_DRIVERS) {
            event.getHook().sendMessage("You have marked your attendance, but the event is full already. You have been placed in queue and will be notified once a slot is available").setEphemeral(true).queue();
        } else {
            event.getHook().sendMessage("You have marked your attendance.").setEphemeral(true).queue();
        }
    }

    private void absendButtonPressed(ButtonInteractionEvent event) {
        long msgId = event.getMessageIdLong();
        User userWhoClicked = event.getUser();
        WatchedMessage ws = msgMan.getWatchedMessage(msgId);

        int driverCountBefore = ws.getAttendees().size();
        msgMan.markAbsence(msgId, userWhoClicked);
        int driverCountAfter = ws.getAttendees().size();
        event.editMessageEmbeds(MessageBuilder.createMessage(ws)).queue();
        event.getHook().sendMessage("You have marked your absence.").setEphemeral(true).queue();

        // Someone marked his absence, notify potentially waiting people about slot
        if(driverCountBefore > WatchedMessage.MAX_DRIVERS && driverCountAfter <= WatchedMessage.MAX_DRIVERS) {
            ws.getAttendees().get(WatchedMessage.MAX_DRIVERS - 1).openPrivateChannel().complete()
                .sendMessage("A slot has become available and you have been moved into attending in the following event: " + event.getMessage().getJumpUrl()).queue();
        }
    }
}
