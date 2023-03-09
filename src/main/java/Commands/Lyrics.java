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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

// THIS IS DEPRECATED
@Deprecated
public class Lyrics extends Command {

    public Lyrics() {
        this.name = "lyrics";
        this.aliases = new String[]{"ly", "lyr"};
        this.help = "Shows the lyrics of the current playing track";
        this.guildOnly = true;
        this.hidden = false;
    }

    @Override
    protected void execute(CommandEvent e) {
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

        // Finally
        String[] msg = e.getMessage().getContentRaw().split(" ", 2);
        if (msg.length == 1) {
            eb = EmbedMaker.embedBuilderDescription("What the hell am I searching? Use, lyrics <Artist Name - Song Name> or... whatever");
            e.reply(eb.build());
        } else {
            System.out.println(msg[1]);
            int loop = 1;

            do{
                switch(loop){
                    case 1:
                        try {
                            com.jagrosh.jlyrics.Lyrics lyrics = client.getLyrics(msg[1]).get();

                            e.reply("**" + lyrics.getTitle() +" by " + lyrics.getAuthor() + "** \n \n" + lyrics.getContent() +"\n\nSource: " + lyrics.getSource() + "\n" + lyrics.getURL());
                            loop = 4;
                        } catch (Exception err) {
                            loop ++;
                        }
                        break;
                    case 2:
                        try {
                            com.jagrosh.jlyrics.Lyrics lyrics = client.getLyrics(msg[1],"Genius").get();
                            e.reply("**" + lyrics.getTitle() +" by " + lyrics.getAuthor() + "** \n \n" + lyrics.getContent() +"\n\nSource: " + lyrics.getSource() + "\n" + lyrics.getURL());
                            loop = 4;
                        } catch (Exception err) {
                            loop ++;
                        }
                        break;
                }
            }while (loop < 3);

            if(loop == 3){
                eb = EmbedMaker.embedBuilderDescription(" think you need to try that again? I'm kinda choking here...");
                e.reply(eb.build());
                return;
            }
        }

    }
}