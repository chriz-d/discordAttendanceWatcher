package discordattendancewatcher;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class MessageBuilder {
    
    public static MessageCreateData createMessage(WatchedMessage ms) {
        MessageCreateBuilder mcb = new MessageCreateBuilder();
        StringBuffer attendeesString = new StringBuffer();
        for(User user : ms.getAttendees()) {
            attendeesString.append(user.getAsMention());
            attendeesString.append("\n");
        }
        StringBuffer absenteesString = new StringBuffer();
        for(User user : ms.getAbsentees()) {
            absenteesString.append(user.getAsMention());
            absenteesString.append("\n");
        }
        
        String message = String.format(TemplateLoader.template, ms.getRoleToPing().getAsMention(), 
                ms.getTitle(), ms.getDate(), attendeesString.toString(), absenteesString.toString(),
                ms.getAttendees().size(), ms.getAbsentees().size(), ms.getRoleToPing().getAsMention());
        mcb.setContent(message);
        return mcb.build();
    }
    
    public static MessageEditData rebuildMessage(WatchedMessage ms) {
        MessageEditBuilder meb = new MessageEditBuilder();
        meb.setContent(createMessage(ms).getContent()); // extract raw content, we want to keep the buttons
        return meb.build();
    }
}
