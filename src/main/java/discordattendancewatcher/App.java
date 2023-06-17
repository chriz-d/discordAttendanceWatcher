package discordattendancewatcher;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class App implements EventListener {
    
    public static void main(String[] args) throws InterruptedException {
        if(args.length == 0) {
            System.out.println("Please provide the bot token as args.");
            return;
        }
        String token = args[0];
        System.out.println(args[0]);
        JDA bot = JDABuilder.createDefault(token)
            .setStatus(OnlineStatus.ONLINE)
            .addEventListeners(new App())
            .build();
        bot.awaitReady();
    }


    @Override
    public void onEvent(GenericEvent event) {
        if(event instanceof MessageReceivedEvent) {
            MessageReceivedEvent msgEvent = (MessageReceivedEvent) event;
            System.out.println(msgEvent.getMessage().getContentStripped());
            msgEvent.getJDA().shutdown();
        }
    }
}
