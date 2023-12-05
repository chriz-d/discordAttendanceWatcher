package discordattendancewatcher.raceEvent;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class MessageBuilder {
    
    public static MessageEmbed createMessage(WatchedMessage ws) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(ws.getTrack(), "https://www.thesimgrid.com/championships/5461");
        eb.setColor(new Color(255, 255, 255));
        eb.setAuthor(ws.getTitle());
        eb.setImage("attachment://" + ws.getImageName());
        eb.setThumbnail("attachment://logo_white.png");
        String eventDate = "Next event will start <t:" + ws.getDate() + ">\n";
        String text = "If you cannot race please visit simgrid and hit 'withdraw' or else you will loose attendance rating.";
        eb.setDescription(eventDate + text);

        // // Attendees
        // int driverCount = ws.getAttendees().size();
        // addDrivers(eb, "Attending", 0, Math.min(WatchedMessage.MAX_DRIVERS, driverCount), ws.getAttendees(), true, ws);

        // // Absentees
        // addDrivers(eb, "Not attending", 0, ws.getAbsentees().size(), ws.getAbsentees(), true, ws);

        // // Waiting attendees
        // if(driverCount > WatchedMessage.MAX_DRIVERS) {
        //     addDrivers(eb, "Waiting for slot", WatchedMessage.MAX_DRIVERS, ws.getAttendees().size(), ws.getAttendees(), false, ws);
        // }

        if(!ws.getRaceFormat().isEmpty()) {
            eb.addField("Race format", ws.getRaceFormat(), false);
        }
        if(!ws.getDetails().isEmpty()) {
            eb.addField("Details", ws.getDetails(), false);
        }
        return eb.build();
    }
    
    public static MessageEditData rebuildMessage(WatchedMessage ms) {
        MessageEditBuilder meb = new MessageEditBuilder();
        // meb.setContent(createMessage(ms).); // extract raw content, we want to keep the buttons
        return meb.build();
    }

    private static void addDrivers(EmbedBuilder eb, String title, int startIdx, int endIdx, List<Member> list, boolean inline, WatchedMessage ws) {
        // Waiting attendees
        StringBuffer formattedDrivers = new StringBuffer();
        for(int i = startIdx; i < endIdx; i++) {
            formattedDrivers.append(i+1 + ". ");
            if(list.get(i).getRoles().contains(ws.getReserveRoleToPing())) {
                formattedDrivers.append("_" + list.get(i).getAsMention() + "_");
            } else {
                formattedDrivers.append(list.get(i).getAsMention());
            }
            formattedDrivers.append("\n");
        }
        eb.addField(title, formattedDrivers.toString(), inline);
    }
}