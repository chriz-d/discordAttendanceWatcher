package discordattendancewatcher;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import okhttp3.internal.ws.RealWebSocket.Message;

public class CommandListener extends ListenerAdapter {
    
    private WatchedMessageManager msgMan;
    
    public CommandListener(WatchedMessageManager msgMan) {
        this.msgMan = msgMan;
    }
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if(command.equals("createevent")) {
            TextChannel chosenChannel = event.getOption("channel").getAsChannel().asTextChannel();
            String date     = event.getOption("date").getAsString();
            String title    = event.getOption("title").getAsString();
            Role roleToPing = event.getOption("role").getAsRole();
            if(!inputsValid(chosenChannel, date, title, roleToPing, event)) {
                return;
            }
            
            long timestamp = Long.parseLong(date);
            if(!isValidDate(timestamp, event)) {
                return;
            }
            
            WatchedMessage ws = new WatchedMessage(chosenChannel, timestamp, title, roleToPing);
            chosenChannel.sendMessage(MessageBuilder.createMessage(ws))
            .addActionRow(Button.primary("attend", "Mark attendance"), Button.danger("absent", "Mark absence"))
            .queue((message) -> {
                long msgId = message.getIdLong();
                msgMan.watchMessage(msgId, ws);
                msgMan.queueMessageDeletion(timestamp, msgId);
            });
            
            event.reply("Done!").setEphemeral(true).queue();
        } else if(command.equals("racestats")) {
            event.deferReply().queue();
            String outputPath = "temp/";
            String url = event.getOption("url").getAsString();
            String title = event.getOption("title").getAsString();
            Process p;
            String s;
            try {
                p = Runtime.getRuntime().exec(new String[] {".venv/bin/python", "src/main/python/screenshot.py", url, outputPath});
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((s = br.readLine()) != null)
                    System.out.println("line: " + s);
                p.waitFor();
                System.out.println ("exit: " + p.exitValue());
                p.destroy();
            } catch(Exception e) {
                e.printStackTrace();
            }
            MessageCreateBuilder builder = new MessageCreateBuilder();
            builder.setContent(title);
            File dir = new File(outputPath);
            if(dir.exists() && dir.isDirectory()) {
                for(File file : dir.listFiles()) {
                    builder.addFiles(FileUpload.fromData(file, file.getName()));
                }                
            }
            event.getHook().sendMessage(builder.build()).queue();
        }
    }
    
    private boolean inputsValid(TextChannel chosenChannel, String date, String title, Role roleToPing, SlashCommandInteractionEvent event) {
        if(!isValidDateString(date)) {
            event.reply("The date string was not formatted correctly. See the parameter description for an example.").setEphemeral(true).queue();
            return false;
        }
        if(!chosenChannel.canTalk()) {
            event.reply("No permission to post in given channel.").setEphemeral(true).queue();
            return false;
        }
        if(!roleToPing.isMentionable()) {
            event.reply("Given role is not pingable by bot.").setEphemeral(true).queue();
            return false;
        }
        return true;
    }
    
    private boolean isValidDateString(String date) {
        try {
            Long.parseLong(date);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    private boolean isValidDate(long timestamp, SlashCommandInteractionEvent event) {
        if(((System.currentTimeMillis() / 1000) + 5) > timestamp) {
            event.reply("Invalid date given (past).").setEphemeral(true).queue();
            return false;
        }
        return true;
    }
}
