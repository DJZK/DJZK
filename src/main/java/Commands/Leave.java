package Commands;

import Functions.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
public class Leave extends Command {
    public Leave(){
        this.name = "leave";
        this.help = "Disconnects me to a voice channel";
        this.aliases = new String [] {"dc"};
        this.guildOnly = true;
        this.hidden = false;
    }
    @Override
    protected void execute(CommandEvent e){
        final TextChannel channel = e.getTextChannel();
        final Member self = e.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final AudioManager audioManager = e.getGuild().getAudioManager();
        final Member member = e.getMember();
        EmbedBuilder eb;

        // Not me. not you
        if(!(member.getPermissions().contains(Permission.ADMINISTRATOR) || member.getId().equals(UniversalVariables.DJZK) || UniversalVariables.Unlocked)){
            eb = EmbedMaker.embedBuilderDescription(MessageSender.noPermission);
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Bot not in VC
        if(!selfVoiceState.inVoiceChannel()){
            eb = EmbedMaker.embedBuilderDescription("You gotta call me, come on..");
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        final GuildVoiceState memberVoiceState = member.getVoiceState();

        // User not in voice channel
        if(!memberVoiceState.inVoiceChannel()){
            eb = EmbedMaker.embedBuilderDescription("Are you sure? Go join a vc first..");
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Not with the bot
        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            eb = EmbedMaker.embedBuilderDescription("What now? You're not even here...");
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Finally
        eb = EmbedMaker.embedBuilderDescription("Leaving..");
        channel.sendMessageEmbeds(eb.build()).queue();
        audioManager.closeAudioConnection();
        System.out.println("Left " + member.getVoiceState().getChannel());

    }
}
