package Commands;


import Functions.EmbedMaker;
import Functions.MusicManager;
import Functions.PlayerManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jlyrics.LyricsClient;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Lyrics extends Command {

    public Lyrics(){
        this.name = "lyrics";
        this.aliases = new String[]{"ly", "lyr"};
        this.help = "Shows the lyrics of the current playing track";
        this.guildOnly = true;
        this.hidden = false;
    }

    @Override
    protected void execute(CommandEvent e){
        // Objects
        final TextChannel channel = e.getTextChannel();
        final Member self = e.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final Member member = e.getMember();
        final MusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        LyricsClient client = new LyricsClient();
        EmbedBuilder eb;

        // Bot in VC
        if (!selfVoiceState.inVoiceChannel()) {
            eb = EmbedMaker.embedBuilderDescription("You gotta call me, come on..");
            channel.sendMessage(eb.build()).queue();
            return;
        }
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        // User in voice channel
        if(!memberVoiceState.inVoiceChannel()){
            eb = EmbedMaker.embedBuilderDescription("Are you sure? Go Join a VC first....");
            channel.sendMessage(eb.build()).queue();
            return;
        }

        // Not with the bot
        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            eb = EmbedMaker.embedBuilderDescription("What? Come on and join with me first!");
            channel.sendMessage(eb.build()).queue();
            return;
        }

        // Finally
        if(audioPlayer.getPlayingTrack() == null){
            eb = EmbedMaker.embedBuilderDescription("What the hell am I gonna search? Jeez");
            channel.sendMessage(eb.build()).queue();
        }
        else{
            try {
                com.jagrosh.jlyrics.Lyrics lyrics = client.getLyrics(audioPlayer.getPlayingTrack().getInfo().title).get();
                eb = EmbedMaker.embedBuilderAuthor(audioPlayer.getPlayingTrack().getInfo().title, lyrics.getContent());
                channel.sendMessage(eb.build()).queue();
            } catch (Exception err) {
                err.printStackTrace();
                eb = EmbedMaker.embedBuilderDescription("Oops. I think I searched the wrong thing, in a place where i shouldn't go... Can you try again...?");
                channel.sendMessage(eb.build()).queue();
            }
        }
    }

}
