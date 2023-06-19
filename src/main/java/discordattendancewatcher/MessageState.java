package discordattendancewatcher;

import java.util.Set;
import java.util.HashSet;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class MessageState {
    
    
    private Set<Member> attendees;
    private Set<Member> absentees;
    
    private String date;
    private String title;
    private Role roleToPing;
    private Member commentator;
    
    public MessageState(String date, String title, Role roleToPing, Member commentator) {
        attendees = new HashSet<>();
        absentees = new HashSet<>();
        this.date = date;
        this.title = title;
        this.roleToPing = roleToPing;
        this.commentator = commentator;
    }
    
    public MessageState(String date, String title, Role roleToPing) {
        attendees = new HashSet<>();
        absentees = new HashSet<>();
        this.date = date;
        this.title = title;
        this.roleToPing = roleToPing;
    }
    
    public boolean hasReacted(Member member) {
        return attendees.contains(member) || absentees.contains(member);
    }
    
    public void removeReaction(Member member) {
        attendees.remove(member);
        absentees.remove(member);
    }

    public Set<Member> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<Member> attendees) {
        this.attendees = attendees;
    }

    public Set<Member> getAbsentees() {
        return absentees;
    }

    public void setAbsentees(Set<Member> absentees) {
        this.absentees = absentees;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public Role getRoleToPing() {
        return roleToPing;
    }
    
    public Member getCommentator() {
        return commentator;
    }
}
