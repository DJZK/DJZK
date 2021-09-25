package Functions;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;

    public boolean looping = false, loopQueue = false, echo = true;
    public TextChannel lastID;
    public Guild guild;
    public Member lastUser;
    public int retryCount = 0;
    EmbedBuilder eb;

    public TrackScheduler(AudioPlayer player){
        this.player = player;
        this.queue = new LinkedBlockingDeque<>();
    }

    public void queue(AudioTrack track){
        if (!this.player.startTrack(track, true)){

            try {
                this.queue.offer(track);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void nextTrack(){


        try {
            this.player.startTrack(this.queue.poll(),false);
            final AudioTrack audioTrack = this.player.getPlayingTrack();
            final AudioTrackInfo info = audioTrack.getInfo();
            if(echo){
                eb = EmbedMaker.embedBuilderAuthor("Now Playing: " , info.title + "\n" + info.uri);
                MessageSender.sendMessage(guild.getId(), lastID.getId(), eb);
                eb.clear();
                System.out.println("I am now starting to play " + info.title);
            }
        } catch (Exception e){
            System.out.println("There's nothing to play next. *sad bot noises*");
            eb = EmbedMaker.embedBuilderDescription("There's nothing to play next... *sad bot noises*");
            MessageSender.sendMessage(guild.getId(), lastID.getId(), eb);
            eb.clear();
        }

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason){

            final AudioTrackInfo info = track.getInfo();
            // Loop if loop is requested by user
            if (endReason.mayStartNext) {

                if (looping) {
                    System.out.println("Looping " + info.title);
                    this.player.startTrack(track.makeClone(), false);

                    if (echo) {
                        eb = EmbedMaker.embedBuilderAuthor("Now Playing: ", info.title + "\n" + info.uri);
                        MessageSender.sendMessage(guild.getId(), lastID.getId(), eb);
                        eb.clear();
                    }
                    return;
                }

                // Fail Check First
                if (endReason.equals(AudioTrackEndReason.LOAD_FAILED) && retryCount != 5) {
                    System.out.println(info.title + " failed to load. I will retry... \nAttempt: " + retryCount);
                    this.player.startTrack(track.makeClone(), false);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    retryCount++;
                    return;
                }

                if (loopQueue) {
                    System.out.println("Adding back to the end of the queue: ");
                    this.queue.add(track.makeClone());
                }

                System.out.println(info.title + " is done playing...");

                try{
                    nextTrack();
                } catch (Exception err){
                    err.printStackTrace();
                }




                retryCount = 0;

            }
    }
}
