package Commands;

import Functions.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class NowPlaying extends Command {
    public NowPlaying(){
        this.name = "nowplaying";
        this.aliases = new String[]{"np"};
        this.help = "Displays the current playing track";
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
       EmbedBuilder eb;

        // Not me, not you
      //  if(!member.getPermissions().contains(Permission.ADMINISTRATOR) || !(member.getId() == UniversalVariables.DJZK)){
       //     eb = EmbedMaker.embedBuilderDescription(MessageSender.noPermission);
    //        channel.sendMessage(eb.build()).queue();
    //        return;
      //  }

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
            eb = EmbedMaker.embedBuilderDescription("Nothing is playing, *sad bot noises*");
        }

        else{
            eb = EmbedMaker.embedBuilderAuthor("Now Playing", audioPlayer.getPlayingTrack().getInfo().title + "\n" + audioPlayer.getPlayingTrack().getInfo().uri);
        }

        channel.sendMessage(eb.build()).queue();
    }
}
