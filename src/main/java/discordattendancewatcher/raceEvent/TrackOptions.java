package discordattendancewatcher.raceEvent;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class TrackOptions {
    
    private static final String[] trackNames = {"🇪🇸 Barcelona 🇪🇸",
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

    public static OptionData getTrackOptions() {
        List<Choice> choices = new ArrayList<>();
        for(String trackName : trackNames) {
            choices.add(new Choice(trackName, trackName));
        }
        OptionData track = new OptionData(OptionType.STRING, "track", "Track for the event.", true)
            .addChoices(choices);
        return track;
    }
}
