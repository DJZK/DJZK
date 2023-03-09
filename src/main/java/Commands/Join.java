package Commands;

import Functions.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class Join extends Command {
    public static boolean called = false;
    public Join(){
        this.name="join";
        this.aliases = new String[] {"j"};
        this.help="Lets me join to a voice channel";
        this.guildOnly = true;
        this.hidden = false;
    }
    @Override
    protected void execute(CommandEvent e){
        // Objects, again
        final TextChannel channel = e.getTextChannel();
        final Member self = e.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final Member member = e.getMember();
        final AudioManager audioManager = e.getGuild().getAudioManager();

        final MusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        EmbedBuilder eb;


        // Not you, nope...
        if(!(member.getPermissions().contains(Permission.ADMINISTRATOR) || member.getId().equals(UniversalVariables.DJZK) || UniversalVariables.Unlocked)){
            eb = EmbedMaker.embedBuilderDescription(MessageSender.noPermission);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        final GuildVoiceState memberVoiceState = member.getVoiceState();
        final VoiceChannel memberChannel = memberVoiceState.getChannel();

        // am i at voice channel
        if(selfVoiceState.inVoiceChannel()){
            eb = EmbedMaker.embedBuilderDescription("Nope, I'm already at somewhere else... :D");
            channel.sendMessage(eb.build()).queue();
            return;
            }

        // member at voice channel
        if(!memberVoiceState.inVoiceChannel()){
            eb = EmbedMaker.embedBuilderDescription("You gotta join somewhere first.");
            channel.sendMessage(eb.build()).queue();
            return;
        }

        if (!self.hasPermission(memberChannel, Permission.VOICE_CONNECT)) {
            eb = EmbedMaker.embedBuilderDescription("I have no access bud...");
            channel.sendMessage(eb.build()).queue();
            return;

        }

        // Finally

        // For now Playing
        musicManager.scheduler.lastID = channel;
        musicManager.scheduler.guild = e.getGuild();
        musicManager.scheduler.lastUser = member;
        musicManager.scheduler.echo = true;
        musicManager.scheduler.looping = false;
        musicManager.scheduler.loopQueue = false;
        musicManager.scheduler.retryCount = 0;

        // Connecting to VC
        audioManager.openAudioConnection(memberChannel);

        // Output
        eb = EmbedMaker.embedBuilderDescription("I joined <#" + memberChannel.getId() + "> and bound to <#" + musicManager.scheduler.lastID.getId() + ">");
        channel.sendMessage(eb.build()).queue();

        System.out.println("I joined " + memberChannel.getName() + " and bound to " +  musicManager.scheduler.lastID.getName());
    }
}
