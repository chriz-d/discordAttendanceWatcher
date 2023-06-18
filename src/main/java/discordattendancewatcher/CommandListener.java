package discordattendancewatcher;

import java.util.List;
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
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.api.entities.Member;

public class CommandListener extends ListenerAdapter {
    
    private Map<Long, MessageState> monitoredMessages;
    
    public CommandListener() {
        monitoredMessages = new HashMap<>();
    }
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if(command.equals("watch")) {
            StandardGuildMessageChannel chosenChannel = event.getOption("channel").getAsChannel().asStandardGuildMessageChannel();
            //String date = event.getOption("date").getAsString();
            //String title = event.getOption("title").getAsString();
            //Role roleToPing = event.getOption("role").getAsRole();
            MessageState ms = new MessageState();
            chosenChannel.sendMessage(createMessage(ms))
            .addActionRow(Button.primary("attend", "Attend"), Button.danger("not", "No time"))
            .queue((message) -> {
                long msgId = message.getIdLong();
                monitoredMessages.put(msgId, ms);
            }
            );
            event.reply("Done!").setEphemeral(true).queue();
        }
    }
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        long msgId = event.getMessageIdLong();
        System.out.println(monitoredMessages.size());
        
        if(!monitoredMessages.containsKey(msgId)) {
            event.reply("Something went wrong. Contact chriz.").queue();
            return;
        }
        
        MessageState ms = monitoredMessages.get(msgId);
        Member userWhoClicked = event.getMember();
        if(ms.hasReacted(userWhoClicked)) {
            ms.removeReaction(userWhoClicked);
        }
        
        if(event.getComponentId().equals("attend")) {
            ms.attendees.add(userWhoClicked);
            event.editMessage(rebuildMessage(ms)).queue();
            event.getHook().sendMessage("You have marked your attendance.").setEphemeral(true).queue();
        } else if(event.getComponentId().equals("not")) {
            ms.absentees.add(userWhoClicked);
            event.editMessage(rebuildMessage(ms)).queue();
            event.getHook().sendMessage("You have marked your absence.").setEphemeral(true).queue();
        }
    }
    
    private MessageCreateData createMessage(MessageState ms) {
        MessageCreateBuilder mcb = new MessageCreateBuilder();
        mcb.addContent("Attending:\n");
        for(Member member : ms.attendees) {
            mcb.addContent(member.getAsMention());
            mcb.addContent("\n");
        }
        mcb.addContent("Absent:\n");
        for(Member member : ms.absentees) {
            mcb.addContent(member.getAsMention());
            mcb.addContent("\n");
        }
        return mcb.build();
    }
    
    private MessageEditData rebuildMessage(MessageState ms) {
        MessageEditBuilder meb = new MessageEditBuilder();
        meb.setContent(createMessage(ms).getContent()); // extract raw content, we want to keep the buttons
        return meb.build();
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
        //OptionData date = new OptionData(OptionType.STRING, "date", "Date and time the event will start. (Use <t:XXX:F> if possible)", true);
        //OptionData title = new OptionData(OptionType.STRING, "title", "Title of the event. (E.g. Season 2 - Round 7:  🇮🇹 Misano 🇮🇹)");
        //OptionData roleToPing = new OptionData(OptionType.ROLE, "role", "Who to ping for the event.");
        
        SlashCommandData command = Commands.slash("watch", "Posts a new event while watching for reactions.")
                .addOptions(chosenChannel/*, date, title, roleToPing*/);
        event.getGuild().updateCommands().addCommands(command).queue();
    }
}
