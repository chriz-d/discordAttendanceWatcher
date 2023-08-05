package discordattendancewatcher;


import java.io.File;

import discordattendancewatcher.raceEvent.MessageBuilder;
import discordattendancewatcher.raceEvent.WatchedMessage;
import discordattendancewatcher.raceEvent.WatchedMessageManager;
import discordattendancewatcher.raceStats.RaceStatsParser;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class CommandListener extends ListenerAdapter {
    
    private static final String ACC_EVENT_CMD = "accevent";
    private static final String RACE_STATS_CMD = "racestats";

    private WatchedMessageManager msgMan;
    
    public CommandListener(WatchedMessageManager msgMan) {
        this.msgMan = msgMan;
    }
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if(command.equals(ACC_EVENT_CMD)) {
            TextChannel chosenChannel = event.getChannel().asTextChannel();
            String date     = event.getOption("date").getAsString();
            String title    = event.getOption("title").getAsString();
            String track = event.getOption("track").getAsString();
            Role roleToPing = event.getOption("role").getAsRole();
            Role reserveRoleToPing = event.getOption("reserverole").getAsRole();
            String raceformat = event.getOption("raceformat", "",  OptionMapping::getAsString).replace("|", "\n"); // Discord strips newline chars from user input
            String details = event.getOption("details", "",  OptionMapping::getAsString).replace("|", "\n");
            Attachment image = event.getOption("image", arg0 -> arg0.getAsAttachment());
            String imagePath = "";
            if(image != null) {
                imagePath = image.getFileName();
                image.getProxy().downloadToFile(new File("assets/" + imagePath)).join(); // Wait for download to finish
            }
            if(!inputsValid(chosenChannel, date, title, roleToPing, reserveRoleToPing, event)) {
                return;
            }
            
            long timestamp = Long.parseLong(date);
            if(!isValidDate(timestamp, event)) {
                return;
            }
            
            WatchedMessage ws = new WatchedMessage(chosenChannel, timestamp, title, track, roleToPing, reserveRoleToPing, raceformat, details, imagePath);
            chosenChannel.sendMessageEmbeds(MessageBuilder.createMessage(ws))
            .addActionRow(Button.primary("attend", "Mark attendance"), Button.danger("absent", "Mark absence"))
            .addContent(ws.getRoleToPing().getAsMention() + " " + ws.getReserveRoleToPing().getAsMention())
            .addFiles(FileUpload.fromData(new File("assets/logo_white.png"), "logo_white.png"), FileUpload.fromData(new File("assets/" + imagePath), imagePath))
            .queue((message) -> {
                long msgId = message.getIdLong();
                msgMan.watchMessage(msgId, ws);
                msgMan.queueMessageDeletion(timestamp, msgId);
            });
            
            event.reply("Done!").setEphemeral(true).queue();
        } else if(command.equals(RACE_STATS_CMD)) {
            event.deferReply().queue();
            String outputPath = "temp/";
            String url = event.getOption("url").getAsString();
            String title = event.getOption("title").getAsString();
            RaceStatsParser.parse(url, outputPath);
            File dir = new File(outputPath);
            if(!dir.exists() || !dir.isDirectory()) {
                event.getHook().sendMessage("Failed to generate images.");
                return;
            } 
            MessageCreateBuilder builder = new MessageCreateBuilder();
            builder.setContent(title);
            for(File file : dir.listFiles()) {
                builder.addFiles(FileUpload.fromData(file, file.getName()));
            }
            String[] entries = dir.list();
            for(String s: entries){
                File currentFile = new File(dir.getPath(), s);
                currentFile.delete();
            }
            dir.delete();
            event.getHook().sendMessage(builder.build()).queue();
        }
    }
    
    private boolean inputsValid(TextChannel chosenChannel, String date, String title, Role roleToPing, Role reserveRoleToPing, SlashCommandInteractionEvent event) {
        if(!isValidDateString(date)) {
            event.reply("The date string was not formatted correctly. See the parameter description for an example.").setEphemeral(true).queue();
            return false;
        }
        if(!chosenChannel.canTalk()) {
            event.reply("No permission to post in given channel.").setEphemeral(true).queue();
            return false;
        }
        if(!roleToPing.isMentionable() || !reserveRoleToPing.isMentionable()) {
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
