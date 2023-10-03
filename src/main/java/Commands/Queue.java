package Commands;

import Functions.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Queue extends Command {
    public Queue(){
        this.name = "queue";
        this.aliases = new String[]{"q"};
        this.help = "Display the queued songs.";
        this.guildOnly = true;
        this.hidden = false;
    }

    @Override
    protected void execute(CommandEvent e) {
        // Objects
        EmbedBuilder eb;
        EmbedBuilder embedQueue = new EmbedBuilder();
        final Member self = e.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final Member member = e.getMember();
        final TextChannel channel = e.getTextChannel();
        final MusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        final int trackCount = Math.min(queue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(queue);
        NowPlaying np = new NowPlaying();

        // Not me, not you
        if(!(member.getPermissions().contains(Permission.ADMINISTRATOR) || member.getId().equals(UniversalVariables.DJZK) || UniversalVariables.Unlocked)){
            eb = EmbedMaker.embedBuilderDescription(MessageSender.noPermission);
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }


        // Bot in VC
        if (!selfVoiceState.inVoiceChannel()) {
            eb = EmbedMaker.embedBuilderDescription("You gotta call me, come on..");
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        // User in voice channel
        if(!memberVoiceState.inVoiceChannel()){
            eb = EmbedMaker.embedBuilderDescription("Are you sure? Go Join a VC first....");
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Not with the bot
        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            eb = EmbedMaker.embedBuilderDescription("What? Come on and join with me first!");
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Finally

        if(!musicManager.scheduler.echo) {
            return;
        }

        if(queue.isEmpty()){
            eb = EmbedMaker.embedBuilderDescription("Nothing's in the queue dude....");
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Queuing and Embedding
        embedQueue.setAuthor("Queue:");
        for(int i = -0; i < trackCount; i++){
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();
            embedQueue.addField("#" + (i + 1) + " " + info.title, formatTime(track.getDuration()),false);
        }

        // Last Track
        if(trackList.size() > trackCount){
            embedQueue.addField("","and " + (trackList.size() - trackCount) + " more...",false);
        }

        channel.sendMessageEmbeds(embedQueue.build()).queue();
        np.execute(e);

    }
    // Time formatted
    private String formatTime(long timeInMillis){
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}