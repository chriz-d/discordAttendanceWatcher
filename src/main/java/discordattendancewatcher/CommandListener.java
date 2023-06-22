package discordattendancewatcher;


import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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
import net.dv8tion.jda.api.entities.Role;

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
            System.out.println(date);
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
        OptionData date = new OptionData(OptionType.STRING, "date", "Date and time the event will start. (Unix timestamp, that really big number)", true);
        OptionData title = new OptionData(OptionType.STRING, "title", "Title of the event. (E.g. Season 2 - Round 7:  🇮🇹 Misano 🇮🇹)", true);
        OptionData roleToPing = new OptionData(OptionType.ROLE, "role", "Who to ping for the event.", true);
        
        SlashCommandData command = Commands.slash("createevent", "Posts a new event while watching for reactions.")
                .addOptions(chosenChannel, date, title, roleToPing);
        event.getGuild().updateCommands().addCommands(command).queue();
    }
}
