package discordattendancewatcher;

import java.util.Map;
import java.util.HashMap;

import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
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
            
            WatchedMessage ms;
            if(event.getOption("commentator") != null) {
                Member commentator = event.getOption("commentator").getAsMember();
                ms = new WatchedMessage(date, title, roleToPing, commentator);
            } else {
                ms = new WatchedMessage(date, title, roleToPing);
            }
            
            chosenChannel.sendMessage(MessageBuilder.createMessage(ms))
            .addActionRow(Button.primary("attend", "Attend"), Button.danger("not", "No time"))
            .queue((message) -> {
                long msgId = message.getIdLong();
                msgMan.addWatchedMessage(msgId, ms);
            }
            );
            event.reply("Done!").setEphemeral(true).queue();
        }
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
        OptionData commentator = new OptionData(OptionType.MENTIONABLE, "commentator", "Who will commentate the event.", false);
        
        SlashCommandData command = Commands.slash("watch", "Posts a new event while watching for reactions.")
                .addOptions(chosenChannel, date, title, roleToPing, commentator);
        event.getGuild().updateCommands().addCommands(command).queue();
    }
}
