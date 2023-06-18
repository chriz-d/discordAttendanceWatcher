package discordattendancewatcher;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class App {
    
    public static void main(String[] args) throws InterruptedException {
        if(args.length == 0) {
            System.out.println("Please provide the bot token as an argument.");
            return;
        }
        String token = args[0];
        
        // Create bot instance
        JDA bot = JDABuilder.createDefault(token)
            .setStatus(OnlineStatus.ONLINE)
            .addEventListeners(new CommandListener(), new ReactionListener())
            .setActivity(Activity.watching("attendance"))
            .build();
        bot.awaitReady();
    }
}
