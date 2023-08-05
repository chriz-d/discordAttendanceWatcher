package discordattendancewatcher;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandRegister extends ListenerAdapter {

    private final String[] trackNames = {"🇪🇸 Barcelona 🇪🇸",
        "🇦🇺 Bathurst 🇦🇺",
        "🇬🇧 Brands Hatch 🇬🇧",
        "🇺🇸 Circuit of the Americas 🇺🇸",
        "🇬🇧 Donington Park 🇬🇧",
        "🇭🇺 Hungaroring 🇭🇺",
        "🇮🇹 Imola 🇮🇹",
        "🇺🇸 Indianapolis 🇺🇸",
        "🇿🇦 Kyalami 🇿🇦",
        "🇺🇸 Laguna Seca 🇺🇸",
        "🇮🇹 Misano 🇮🇹",
        "🇮🇹 Monza 🇮🇹",
        "🇩🇪 Nürburgring 🇩🇪",
        "🇬🇧 Oulton Park 🇬🇧",
        "🇫🇷 Paul Ricard 🇫🇷",
        "🇬🇧 Silverstone 🇬🇧",
        "🇬🇧 Snetterton 🇬🇧",
        "🇧🇪 Spa-Francorchamps 🇧🇪",
        "🇺🇸 Watkins Glen 🇺🇸",
        "🇯🇵 Suzuka 🇯🇵",
        "🇳🇱 Zandvoort 🇳🇱",
        "🇪🇸 Valencia 🇪🇸",
        "🇧🇪 Zolder 🇧🇪"
    };

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
        event.getGuild().updateCommands().addCommands(postEventCommand, racestatsCommand).queue();
    }
   
    private SlashCommandData getPostEventCommand(GenericGuildEvent event) {
        OptionData date = new OptionData(OptionType.STRING, "date", "Date and time the event will start. (Unix timestamp, that really big number)", true);
        OptionData title = new OptionData(OptionType.STRING, "title", "Title of the event. (E.g. Season 2 - Round 7)", true);
        OptionData track = getTrackOptions();
        OptionData roleToPing = new OptionData(OptionType.ROLE, "role", "Who to ping for the event.", true);
        OptionData reserveRoleToPing = new OptionData(OptionType.ROLE, "reserverole", "Who to ping for the event (reserve).", true);
        OptionData image = new OptionData(OptionType.ATTACHMENT, "image", "The image which will be shown below the post", true);
        OptionData raceFormat = new OptionData(OptionType.STRING, "raceformat", "Textblock for the race format. Use \\n to start a new line.");
        OptionData details = new OptionData(OptionType.STRING, "details", "Misc details. Use \\n to start a new line.");
        SlashCommandData command = Commands.slash("accevent", "Posts a new ACC event.")
                .addOptions(date, title, track, roleToPing, reserveRoleToPing, image, raceFormat, details);
        return command;
    }

    private SlashCommandData getPostRaceStatsCommand(GenericGuildEvent event) {
        OptionData url = new OptionData(OptionType.STRING, "url", "Simresults URL of the race", true);
        OptionData title = new OptionData(OptionType.STRING, "title", "Title of the message (E.g. Race Statistics Season 2 - Round 7:  🇮🇹 Misano 🇮🇹)", true);
        
        SlashCommandData command = Commands.slash("racestats", "Posts screenshots of the race stats of the given URL.")
                .addOptions(url, title);
        return command;
    }

    private OptionData getTrackOptions() {
        List<Choice> choices = new ArrayList<>();
        for(String trackName : trackNames) {
            choices.add(new Choice(trackName, trackName));
        }
        OptionData track = new OptionData(OptionType.STRING, "track", "Track for the event.", true)
            .addChoices(choices);
        return track;
    }
}