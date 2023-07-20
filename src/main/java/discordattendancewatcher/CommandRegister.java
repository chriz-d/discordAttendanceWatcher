package discordattendancewatcher;

import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandRegister extends ListenerAdapter {

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        registerCommands(event);
    }
   
    

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        registerCommands(event);
    }
    
    private void registerCommands(GenericGuildEvent event) {
        SlashCommandData postEventCommand = getPostEventCommand(event);
        SlashCommandData racestatsCommand = getPostRaceStatsCommand(event);
        event.getGuild().updateCommands().addCommands(postEventCommand).addCommands(racestatsCommand).queue();;
    }
   
    private SlashCommandData getPostEventCommand(GenericGuildEvent event) {
        OptionData chosenChannel = new OptionData(OptionType.CHANNEL, "channel", "The channel the bot will post in.", true);
        OptionData date = new OptionData(OptionType.STRING, "date", "Date and time the event will start. (Unix timestamp, that really big number)", true);
        OptionData title = new OptionData(OptionType.STRING, "title", "Title of the event. (E.g. Season 2 - Round 7:  🇮🇹 Misano 🇮🇹)", true);
        OptionData roleToPing = new OptionData(OptionType.ROLE, "role", "Who to ping for the event.", true);
        
        SlashCommandData command = Commands.slash("createevent", "Posts a new event while watching for reactions.")
                .addOptions(chosenChannel, date, title, roleToPing);
        return command;
    }

    private SlashCommandData getPostRaceStatsCommand(GenericGuildEvent event) {
        OptionData url = new OptionData(OptionType.STRING, "url", "Simresults URL of the race");
        OptionData title = new OptionData(OptionType.STRING, "title", "Title of the message (E.g. Race Statistics Season 2 - Round 7:  🇮🇹 Misano 🇮🇹)");
        
        SlashCommandData command = Commands.slash("racestats", "Posts screenshots of the race stats of the given URL.")
                .addOptions(url, title);
        return command;
    }
}