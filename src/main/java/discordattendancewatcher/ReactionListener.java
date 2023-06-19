package discordattendancewatcher;

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
        
        WatchedMessage ws = msgMan.getWatchedMessage(msgId);
        Member userWhoClicked = event.getMember();
        if(ws.hasReacted(userWhoClicked)) {
            ws.removeReaction(userWhoClicked);
        }
        
        if(event.getComponentId().equals("attend")) {
            ws.getAttendees().add(userWhoClicked);
            event.editMessage(MessageBuilder.rebuildMessage(ws)).queue();
            event.getHook().sendMessage("You have marked your attendance.").setEphemeral(true).queue();
        } else if(event.getComponentId().equals("not")) {
            ws.getAbsentees().add(userWhoClicked);
            event.editMessage(MessageBuilder.rebuildMessage(ws)).queue();
            event.getHook().sendMessage("You have marked your absence.").setEphemeral(true).queue();
        }
    }
}
