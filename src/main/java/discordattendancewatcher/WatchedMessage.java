package discordattendancewatcher;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;

public class WatchedMessage implements Serializable {
    
    private static final long serialVersionUID = 8798489204520754938L;
    private transient Set<User> attendees;
    private transient Set<User> absentees;
    
    private transient StandardGuildMessageChannel channel;
    
    private long date;
    private String title;
    private transient Role roleToPing;
    
    public WatchedMessage(StandardGuildMessageChannel channel, long date, String title, Role roleToPing) {
        this.channel = channel;
        attendees = Collections.synchronizedSet(new HashSet<>());
        absentees = Collections.synchronizedSet(new HashSet<>());
        this.date = date;
        this.title = title;
        this.roleToPing = roleToPing;
    }
    
    public boolean hasReacted(User user) {
        return attendees.contains(user) || absentees.contains(user);
    }
    
//    public void removeReaction(Member member) {
//        long memberId = member.getIdLong();
//        attendees.remove(memberId);
//        absentees.remove(memberId);
//    }
    
    
    public void markAttendance(User user) {
        absentees.remove(user);
        attendees.add(user);
    }
    
    public void markAbsence(User user) {
        attendees.remove(user);
        absentees.add(user);
    }
    
    public Set<User> getAttendees() {
        return attendees;
    }
    
    public Set<User> getAbsentees() {
        return absentees;
    }
    
    public long getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public Role getRoleToPing() {
        return roleToPing;
    }
    
    public StandardGuildMessageChannel getChannel() {
        return channel;
    }
    
    // JDA does not support serialization, convert all objects to their long ids
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        Set<Long> attendeesLong = new HashSet<>();
        for(User user : attendees) {
            attendeesLong.add(user.getIdLong());
        }
        out.writeObject(attendeesLong);
        Set<Long> absenteesLong = new HashSet<>();
        for(User user : absentees) {
            absenteesLong.add(user.getIdLong());
        }
        out.writeObject(absenteesLong);
        out.writeObject(channel.getIdLong());
        out.writeObject(roleToPing.getIdLong());
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        attendees = Collections.synchronizedSet(new HashSet<>());
        @SuppressWarnings("unchecked")
        Set<Long> attendeesLong = (Set<Long>) in.readObject();
        for(Long user : attendeesLong) {
            attendees.add(App.jda.retrieveUserById(user).complete());
        }
        absentees = Collections.synchronizedSet(new HashSet<>());
        @SuppressWarnings("unchecked")
        Set<Long> absenteesLong = (Set<Long>) in.readObject();
        for(Long user : absenteesLong) {
            absentees.add(App.jda.retrieveUserById(user).complete());
        }
        channel = App.jda.getTextChannelById((long) in.readObject());
        roleToPing = App.jda.getRoleById((long) in.readObject());
        System.out.println("Done reading");
    }
}
