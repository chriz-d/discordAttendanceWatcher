package discordattendancewatcher.raceEvent;

import java.util.Set;

import discordattendancewatcher.App;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;

public class WatchedMessage implements Serializable {
    
    public static final int MAX_DRIVERS = 32;

    private static final long serialVersionUID = 8798489204520754938L;
    private transient List<User> attendees;
    private transient List<User> absentees;
    
    private transient TextChannel channel;
    
    private long date;
    private String title;
    private String track;
    private transient Role roleToPing;
    private transient Role reserveRoleToPing;
    private String raceFormat;
    private String details;
    private String imageName;
    
    public WatchedMessage(TextChannel channel, long date, String title, String track, Role roleToPing, 
        Role reserveRoleToPing, String raceFormat, String details, String imagePath) {
        this.channel = channel;
        attendees = Collections.synchronizedList(new ArrayList<>());
        absentees = Collections.synchronizedList(new ArrayList<>());
        this.date = date;
        this.title = title;
        this.track = track;
        this.roleToPing = roleToPing;
        this.reserveRoleToPing = reserveRoleToPing;
        this.raceFormat = raceFormat;
        this.details = details;
        this.imageName = imagePath;
    }
    
    public boolean hasReacted(User user) {
        return attendees.contains(user) || absentees.contains(user);
    }
    
    public void markAttendance(User user) {
        absentees.remove(user);
        if(attendees.contains(user)) {
            return;
        }
        Guild guild = channel.getGuild();
        List<Role> userRoles =  guild.getMember(user).getRoles();
        boolean isFullTimeDriver = userRoles.contains(roleToPing);
        if(isFullTimeDriver) {
            int i = 0;
            while(!guild.getMember(attendees.get(i)).getRoles().contains(roleToPing) && i < attendees.size()) {
                i++;
            }
            attendees.add(i, user);
        } else {
            attendees.add(user); // reserve driver, lowest prio
        }
    }
    
    public void markAbsence(User user) {
        attendees.remove(user);
        if(!absentees.contains(user)) {
            absentees.add(user);
        }
    }
    
    public List<User> getAttendees() {
        return attendees;
    }
    
    public List<User> getAbsentees() {
        return absentees;
    }
    
    public long getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getTrack() {
        return track;
    }

    public Role getRoleToPing() {
        return roleToPing;
    }
    
    public Role getReserveRoleToPing() {
        return reserveRoleToPing;
    }
    
    public StandardGuildMessageChannel getChannel() {
        return channel;
    }

    public String getRaceFormat() {
        return raceFormat;
    }

    public String getDetails() {
        return details;
    }

    public String getImageName() {
        return imageName;
    }

    // JDA does not support serialization, convert all objects to their long ids
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        List<Long> attendeesLong = new ArrayList<>();
        for(User user : attendees) {
            attendeesLong.add(user.getIdLong());
        }
        out.writeObject(attendeesLong);
        List<Long> absenteesLong = new ArrayList<>();
        for(User user : absentees) {
            absenteesLong.add(user.getIdLong());
        }
        out.writeObject(absenteesLong);
        out.writeObject(channel.getIdLong());
        out.writeObject(roleToPing.getIdLong());
        out.writeObject(reserveRoleToPing.getIdLong());
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        attendees = Collections.synchronizedList(new ArrayList<>());
        @SuppressWarnings("unchecked")
        List<Long> attendeesLong = (List<Long>) in.readObject();
        for(Long user : attendeesLong) {
            attendees.add(App.jda.retrieveUserById(user).complete());
        }
        absentees = Collections.synchronizedList(new ArrayList<>());
        @SuppressWarnings("unchecked")
        List<Long> absenteesLong = (List<Long>) in.readObject();
        for(Long user : absenteesLong) {
            absentees.add(App.jda.retrieveUserById(user).complete());
        }
        channel = App.jda.getTextChannelById((long) in.readObject());
        roleToPing = App.jda.getRoleById((long) in.readObject());
        reserveRoleToPing = App.jda.getRoleById((long) in.readObject());
    }
}
