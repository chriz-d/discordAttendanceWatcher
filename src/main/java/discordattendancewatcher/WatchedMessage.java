package discordattendancewatcher;

import java.util.Set;
import java.util.HashSet;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;

public class WatchedMessage {
    
    private Set<Member> attendees;
    private Set<Member> absentees;
    
    private StandardGuildMessageChannel channel;
    
    private String date;
    private String title;
    private Role roleToPing;
    private Member commentator;
    
    public WatchedMessage(StandardGuildMessageChannel channel, String date, String title, Role roleToPing, Member commentator) {
        this.channel = channel;
        attendees = new HashSet<>();
        absentees = new HashSet<>();
        this.date = date;
        this.title = title;
        this.roleToPing = roleToPing;
        this.commentator = commentator;
    }
    
    public WatchedMessage(StandardGuildMessageChannel channel, String date, String title, Role roleToPing) {
        this(channel, date, title, roleToPing, null);
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

    public Set<Member> getAttendees() {
        return attendees;
    }

    public Set<Member> getAbsentees() {
        return absentees;
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
    
    public StandardGuildMessageChannel getChannel() {
        return channel;
    }
}
