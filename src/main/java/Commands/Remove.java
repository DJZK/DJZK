package Commands;

import Functions.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Remove extends Command {
    public Remove() {
        this.name = "remove";
        this.aliases = new String[]{"r"};
        this.help = "removes the music track via it's position";
        this.guildOnly = true;
        this.hidden = false;
    }

    @Override
    protected void execute(CommandEvent e) {
        EmbedBuilder eb;

        final TextChannel channel = e.getTextChannel();
        final MusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        final List<AudioTrack> trackList = new ArrayList<>(queue);
        final Member self = e.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final Member member = e.getMember();
        String message[] = e.getMessage().getContentRaw().split(" ");

        // Not me, not you
        if(!(member.getPermissions().contains(Permission.ADMINISTRATOR) || member.getId().equals(UniversalVariables.DJZK) || UniversalVariables.Unlocked)){
            eb = EmbedMaker.embedBuilderDescription(MessageSender.noPermission);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        // Bot in VC
        if (!selfVoiceState.inVoiceChannel()) {
            eb = EmbedMaker.embedBuilderDescription("You gotta call me, come on..");
            channel.sendMessage(eb.build()).queue();
            return;
        }
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        // User in voice channel
        if (!memberVoiceState.inVoiceChannel()) {
            eb = EmbedMaker.embedBuilderDescription("Are you sure? Go Join a VC first....");
            channel.sendMessage(eb.build()).queue();
            return;
        }

        // Not with the bot
        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            eb = EmbedMaker.embedBuilderDescription("What? Come on and join with me first!");
            channel.sendMessage(eb.build()).queue();
            return;
        }

        // No params
        if (e.getArgs().isEmpty()) {
            eb = EmbedMaker.embedBuilderDescription("That's not how that works, tsk tsk....");
            channel.sendMessage(eb.build()).queue();
            return;
        }

        // Finally
        try {
            AudioTrack aud = trackList.get((Integer.parseInt(message[1]) - 1));
            eb = EmbedMaker.embedBuilderAuthor("Removed from queue: ", aud.getInfo().title);
            channel.sendMessage(eb.build()).queue();
            eb.clear();
            queue.remove(aud);
            System.out.println("Removed from queue: " + aud.getInfo().title);
        }
        // Error of course
        catch (Exception err) {
            // err.printStackTrace();
            eb = EmbedMaker.embedBuilderDescription("You stupid or something?");
            channel.sendMessage(eb.build()).queue();
            eb.clear();
        }
    }
}
