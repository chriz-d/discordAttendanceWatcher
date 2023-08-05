package discordattendancewatcher;

import java.io.File;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import discordattendancewatcher.raceEvent.ReactionListener;
import discordattendancewatcher.raceEvent.WatchedMessageManager;

import java.io.FileInputStream;
import java.io.IOException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class App {
    
    public static JDA jda;
    
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException {
        String token;
        try {
            token = Files.readString(Path.of("token.txt")).strip();
        } catch (IOException e) {
            System.out.println("Please provide a bot token.");
            return;
        }
        
        TemplateLoader.loadTemplate("templates/default.txt");
        
        // Create bot instance
        jda = JDABuilder.createDefault(token)
            .setStatus(OnlineStatus.ONLINE)
            .setActivity(Activity.watching("your attendance"))
            .addEventListeners(new CommandRegister())
            .build();
        jda.awaitReady();

        WatchedMessageManager msgMan;
        File f = new File("currentWatched.ser");
        if(f.exists() && f.isFile()) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("currentWatched.ser"));
            msgMan = (WatchedMessageManager) objectInputStream.readObject();
            objectInputStream.close();
        } else {
            msgMan = new WatchedMessageManager();
            System.out.println("No old messages found, creating new MessageManager");
        }
        
        jda.addEventListener(new CommandListener(msgMan), new ReactionListener(msgMan));
    }
}
