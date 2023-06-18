package discordattendancewatcher;

import java.util.Set;
import java.util.HashSet;

import net.dv8tion.jda.api.entities.Member;

public class MessageState {
    
    
    public Set<Member> attendees;
    public Set<Member> absentees;
    
    public String date;
    
    public MessageState() {
        attendees = new HashSet<>();
        absentees = new HashSet<>();
    }
    
    public boolean hasReacted(Member member) {
        return attendees.contains(member) || absentees.contains(member);
    }
    
    public void removeReaction(Member member) {
        attendees.remove(member);
        absentees.remove(member);
    }
    
}
