package discordattendancewatcher.raceEvent;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class MessageBuilder {
    
    public static MessageEmbed createMessage(WatchedMessage ws) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(ws.getTrack(), "https://www.thesimgrid.com/championships/4018");
        eb.setColor(new Color(255, 255, 255));
        eb.setAuthor(ws.getTitle());
        eb.setImage("attachment://" + ws.getImageName());
        eb.setThumbnail("attachment://logo_white.png");
        String eventDate = "Next event will start <t:" + ws.getDate() + ">\n";
        String text = "Please mark your attendance by pressing one of the corresponding buttons.";

        eb.setDescription(eventDate + text);
        StringBuffer attendeesString = new StringBuffer();
        for(User user : ws.getAttendees()) {
            attendeesString.append(user.getAsMention());
            attendeesString.append("\n");
        }
        eb.addField("Attending", attendeesString.toString(), true);
        StringBuffer absenteesString = new StringBuffer();
        for(User user : ws.getAbsentees()) {
            absenteesString.append(user.getAsMention());
            absenteesString.append("\n");
        }
        eb.addField("Not attending", absenteesString.toString(), true);
        //eb.addBlankField(true);

        eb.addField("Race format", ws.getRaceFormat(), false);
        eb.addField("Details", ws.getDetails(), false);
        //eb.addBlankField(true);
        return eb.build();
    }
    
    public static MessageEditData rebuildMessage(WatchedMessage ms) {
        MessageEditBuilder meb = new MessageEditBuilder();
        // meb.setContent(createMessage(ms).); // extract raw content, we want to keep the buttons
        return meb.build();
    }
}
