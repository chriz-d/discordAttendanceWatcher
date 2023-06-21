package discordattendancewatcher;

import java.io.File;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.io.FileInputStream;
import java.io.IOException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class App {
    
    public static JDA jda;
    
    
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException {
        if(args.length == 0) {
            System.out.println("Please provide the bot token as an argument.");
            return;
        }
        String token = args[0];
        TemplateLoader.loadTemplate("templates/default.txt");
        
        // Create bot instance
        jda = JDABuilder.createDefault(token)
            .setStatus(OnlineStatus.ONLINE)
            .setActivity(Activity.watching("attendance"))
            .build();
        jda.awaitReady();
        
        WatchedMessageManager msgMan;
        File f = new File("currentWatched.ser");
        if(f.exists() && f.isFile()) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("currentWatched.ser"));
            msgMan = (WatchedMessageManager) objectInputStream.readObject();
            objectInputStream.close();
            msgMan.rescheduleMessageDeletion();
            System.out.printf("Loaded %d previous messages\n", msgMan.watchedMessages.size());
            for(WatchedMessage msg : msgMan.watchedMessages.values()) {
                System.out.printf("Attendeees:\n");
                System.out.println(Arrays.toString(msg.getAttendees().toArray()));
                
                System.out.printf("Absentees:\n");
                System.out.println(Arrays.toString(msg.getAbsentees().toArray()));
            }
        } else {
            msgMan = new WatchedMessageManager();
            System.out.println("No old messages found, creating new MessageManager");
        }
        
        jda.addEventListener(new CommandListener(msgMan), new ReactionListener(msgMan));
    }
}
