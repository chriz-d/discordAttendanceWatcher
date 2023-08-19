package discordattendancewatcher.raceEvent;


import discordattendancewatcher.App;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;

public class WatchedMessage implements Serializable {
    
    public static final int MAX_DRIVERS = 32;

    private static final long serialVersionUID = 8798489204520754938L;
    private transient List<Member> attendees;
    private transient List<Member> absentees;
    
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
    
    public boolean hasReacted(Member member) {
        return attendees.contains(member) || absentees.contains(member);
    }
    
    public void markAttendance(Member member) {
        absentees.remove(member);
        if(attendees.contains(member)) {
            return;
        }
        List<Role> userRoles =  member.getRoles();
        boolean isFullTimeDriver = userRoles.contains(roleToPing);
        if(isFullTimeDriver && attendees.size() > 0) {
            int i = 0;
            while(i < attendees.size() && attendees.get(i).getRoles().contains(roleToPing)) {
                i++;
            }
            attendees.add(i, member);
        } else {
            attendees.add(member); // reserve driver, lowest prio
        }
    }
    
    public void markAbsence(Member member) {
        attendees.remove(member);
        if(!absentees.contains(member)) {
            absentees.add(member);
        }
    }
    
    public List<Member> getAttendees() {
        return attendees;
    }
    
    public List<Member> getAbsentees() {
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
        for(Member member : attendees) {
            attendeesLong.add(member.getIdLong());
        }
        out.writeObject(attendeesLong);
        List<Long> absenteesLong = new ArrayList<>();
        for(Member member : absentees) {
            absenteesLong.add(member.getIdLong());
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

        absentees = Collections.synchronizedList(new ArrayList<>());
        @SuppressWarnings("unchecked")
        List<Long> absenteesLong = (List<Long>) in.readObject();

        channel = App.jda.getTextChannelById((long) in.readObject());
        roleToPing = App.jda.getRoleById((long) in.readObject());
        reserveRoleToPing = App.jda.getRoleById((long) in.readObject());
        Guild guild = channel.getGuild();
        for(Long user : attendeesLong) {
            attendees.add(guild.retrieveMemberById(user).complete());
        }
        for(Long user : absenteesLong) {
            absentees.add(guild.retrieveMemberById(user).complete());
        }
    }
}
