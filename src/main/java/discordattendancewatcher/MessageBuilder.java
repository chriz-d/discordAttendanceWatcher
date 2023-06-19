package discordattendancewatcher;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class MessageBuilder {
    
    public static MessageCreateData createMessage(WatchedMessage ms) {
        MessageCreateBuilder mcb = new MessageCreateBuilder();
        StringBuffer attendeesString = new StringBuffer();
        for(Member member : ms.getAttendees()) {
            attendeesString.append(member.getAsMention());
            attendeesString.append("\n");
        }
        StringBuffer absenteesString = new StringBuffer();
        for(Member member : ms.getAbsentees()) {
            absenteesString.append(member.getAsMention());
            absenteesString.append("\n");
        }
        
        String message = String.format(TemplateLoader.template, ms.getRoleToPing().getAsMention(), 
                ms.getTitle(), ms.getDate(), attendeesString.toString(), absenteesString.toString(),
                ms.getCommentator() == null ? "" : ms.getCommentator().getAsMention(), ms.getRoleToPing().getAsMention());
        mcb.setContent(message);
        return mcb.build();
    }
    
    public static MessageEditData rebuildMessage(WatchedMessage ms) {
        MessageEditBuilder meb = new MessageEditBuilder();
        meb.setContent(createMessage(ms).getContent()); // extract raw content, we want to keep the buttons
        return meb.build();
    }
}
