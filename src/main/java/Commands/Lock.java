package Commands;

import Functions.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Lock extends Command {
    public Lock(){
        this.name = "lock";
        this.help = "Locks or Unlocks the bot for non-administrator users";
        this.guildOnly = true;
        this.hidden = false;
        this.aliases = new String[] {"unlock","ul"};
    }

    @Override
    protected void execute(CommandEvent e){

        final TextChannel channel = e.getTextChannel();
        final Member member = e.getMember();

        EmbedBuilder eb;

        // Not me, not you
        if(!(member.getPermissions().contains(Permission.ADMINISTRATOR) || member.getId().equals(UniversalVariables.DJZK))){
            eb = EmbedMaker.embedBuilderDescription(MessageSender.noPermission);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        // Finally
        final boolean unlocked = !UniversalVariables.Unlocked;
        UniversalVariables.Unlocked = unlocked;
        if(unlocked){
            eb = EmbedMaker.embedBuilderDescription("Commands are now unlocked. Commoners can now use me... Happy now?");
        }
        else{
            eb = EmbedMaker.embedBuilderDescription("YOU SHALL NOT PASS!! -Gandalf");
        }
        channel.sendMessage(eb.build()).queue();
    }
}
