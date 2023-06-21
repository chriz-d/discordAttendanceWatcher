package discordattendancewatcher;

import java.util.regex.Pattern;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class CommandListener extends ListenerAdapter {
    
    WatchedMessageManager msgMan;
    
    public CommandListener(WatchedMessageManager msgMan) {
        this.msgMan = msgMan;
    }
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if(command.equals("watch")) {
            StandardGuildMessageChannel chosenChannel = event.getOption("channel").getAsChannel().asStandardGuildMessageChannel();
            String date     = event.getOption("date").getAsString();
            String title    = event.getOption("title").getAsString();
            Role roleToPing = event.getOption("role").getAsRole();
            
            if(!inputsValid(chosenChannel, date, title, roleToPing, event)) {
                return;
            }
            
            long timestamp = parseDate(date);
            if(!isValidDate(timestamp, event)) {
                return;
            }
            
            WatchedMessage ws = new WatchedMessage(chosenChannel, timestamp, title, roleToPing);
            chosenChannel.sendMessage(MessageBuilder.createMessage(ws))
            .addActionRow(Button.primary("attend", "Mark attendance"), Button.danger("absent", "Mark absence"))
            .queue((message) -> {
                long msgId = message.getIdLong();
                msgMan.watchMessage(msgId, ws);
                msgMan.queueMessageDeletion(msgId, timestamp);
            }
            );
            
            event.reply("Done!").setEphemeral(true).queue();
            
            // extract date and create timer for removal after event ends
            
        }
    }
    
    private boolean inputsValid(StandardGuildMessageChannel chosenChannel, String date, String title, Role roleToPing, SlashCommandInteractionEvent event) {
        if(!isValidDateString(date)) {
            event.reply("The date string was not formatted correctly. See the parameter description for an example.").setEphemeral(true).queue();
            return false;
        }
        if(!chosenChannel.canTalk()) {
            event.reply("No permission to post in given channel.").setEphemeral(true).queue();
            return false;
        }
//        if(!roleToPing.isMentionable()) {
//            event.reply("Given role is not pingable by bot.").setEphemeral(true).queue();
//            return false;
//        }
        return true;
    }
    
    private boolean isValidDateString(String date) {
        Pattern pattern = Pattern.compile("<t:([0-9]|[1-9][0-9]+)(>|:[tTdDfFR]>)");
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }
    
    public long parseDate(String date) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(date);
        matcher.find();
        return Long.parseLong(matcher.group());
    }
    
    private boolean isValidDate(long timestamp, SlashCommandInteractionEvent event) {
        if(((System.currentTimeMillis() / 1000) + 5) > timestamp) {
            event.reply("Invalid date given (past).").setEphemeral(true).queue();
            return false;
        }
        return true;
    }
    
    @Override
    public void onGuildReady(GuildReadyEvent event) {
        registerCommands(event);
    }
    
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        registerCommands(event);
    }
    
    private void registerCommands(GenericGuildEvent event) {
        OptionData chosenChannel = new OptionData(OptionType.CHANNEL, "channel", "The channel the bot will post in.", true);
        OptionData date = new OptionData(OptionType.STRING, "date", "Date and time the event will start. (Use <t:XXX:F> if possible)", true);
        OptionData title = new OptionData(OptionType.STRING, "title", "Title of the event. (E.g. Season 2 - Round 7:  🇮🇹 Misano 🇮🇹)", true);
        OptionData roleToPing = new OptionData(OptionType.ROLE, "role", "Who to ping for the event.", true);
        
        SlashCommandData command = Commands.slash("watch", "Posts a new event while watching for reactions.")
                .addOptions(chosenChannel, date, title, roleToPing);
        event.getGuild().updateCommands().addCommands(command).queue();
    }
}
