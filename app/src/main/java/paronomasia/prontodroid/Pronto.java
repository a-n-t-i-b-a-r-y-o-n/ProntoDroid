package paronomasia.prontodroid;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Braden 9/9/17.
 */


public class Pronto {

    private float frequency;        // Frequency at which the light should flash
    private float pulse;            // Length of a given "unit"
    private ArrayList<String> hex;  // Tokenized list of each hex number
    float duration = 0;         // Total duration in seconds.
    int pairCount = 0;
    int c1LeadInOn = 0;
    int c1LeadInOff = 0;
    int c1On[] = {0, 0};
    int c1Off[] = {0, 0};
    int c1LeadOutOn = 0;
    int c1LeadOutOff = 0;


    // Pass in raw Pronto Hex to the constructor, it'll do the rest.
    Pronto(String hex){
        if(hex != null) {
            this.hex = new ArrayList<>();
            Scanner scanner = new Scanner(hex);
            try {
                do {
                    this.hex.add(scanner.next());
                } while (scanner.hasNext());
            } catch (Exception e){
                Log.d("AUDIOIR", "Error reading in pronto hex values.");
            }

            // Formula: 1000000 / (N * .241246) where N=2nd pronto number in decimal
            this.frequency = (float) (1000000 / (Integer.parseInt(this.hex.get(1), 16) * 0.241246));


            this.pulse = (float) 1 / this.frequency;




            // Identify total duration for c1

            int numPulses = 0;
            int pairCount = (Integer.parseInt(this.hex.get(2), 16) ) * 2;
            // hex digits = (num pairs + 1 lead in + 1 lead out) * 2
            for(int i = 0; i < pairCount; i++) {
                numPulses += Integer.parseInt(this.hex.get(4 + i), 16);
            }
            this.duration = numPulses * this.pulse;

            analyze();
        }
    }


    public boolean analyze(){
        // Identify total duration for c1 and the On/Off values.
        // I think this may only be valid for NEC formats?

        int numPulses = 0;
        this.pairCount = (Integer.parseInt(this.hex.get(2), 16) ) * 2;
        // hex digits = (num pairs + 1 lead in + 1 lead out) * 2
        for(int i = 0; i < pairCount; i++) {
            int currentValue = Integer.parseInt(this.hex.get(4 + i), 16);
            numPulses = numPulses + currentValue;
            // if you're counting anything past the lead in pair
            if((i > 2 && ((c1On[0] == 0 && c1On[1] == 0 ) || (c1Off[0] == 0 && c1Off[1] == 0))) && (5+i <= this.hex.size())){
                int nextValue = Integer.parseInt(this.hex.get(5 + i), 16);
                if(currentValue == nextValue){
                    c1On[0] = currentValue;
                    c1On[1] = currentValue;
                }
                else if (nextValue > currentValue){
                    c1Off[0] = currentValue;
                    c1Off[1] = nextValue;
                }
            }
        }
        this.duration = numPulses * this.pulse;



        // Identify Lead in on & off
        this.c1LeadInOn = Integer.parseInt(this.hex.get(4), 16);
        this.c1LeadInOff = Integer.parseInt(this.hex.get(5), 16);
        this.c1LeadOutOn = Integer.parseInt(this.hex.get(4 + pairCount - 2), 16);
        this.c1LeadOutOff = Integer.parseInt(this.hex.get(4 + pairCount - 1), 16);



	    return true;
    }


    // messing around with threads here...
    public void play(Context context){
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                if(duration == 0){
                    analyze();
                }

                // Use the generate_*_Samples() functions below and combine their output into an AudioTrack

                int count = (int) (44100.0 * 2.0 * (duration)) & ~1;
                short[] samples = new short[count];
                int offset = 0;
                for(int i = 0; i < pairCount; i++){
                    int currentVal = Integer.parseInt(hex.get(4 + i), 16);
                    short[] generated;
                    if(i % 2 == 0){
                        generated = generateTones(pulse * currentVal);
                    }
                    else {
                        generated = generateSilence(pulse * currentVal);
                    }
                    for(int j = 0; j < generated.length; j++){
                        samples[offset + j] = generated[j];
                    }
                    offset += generated.length;
                }

                AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                        AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                        count * (Short.SIZE / 8), AudioTrack.MODE_STREAM);


                try {
                    // Set up the audio track and adjust volumes:

                    AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);
                    int origVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);


                    // set a marker so we know when it stops playing
                    track.setNotificationMarkerPosition(count * (Short.SIZE / 8));
                    track.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                        @Override
                        public void onMarkerReached(AudioTrack audioTrack) {
                            // reset the volume to its original value
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, origVol, 0);
                        }

                        @Override
                        public void onPeriodicNotification(AudioTrack audioTrack) {
                            // stub
                        }
                    });


                    // do the thing
                    try {
                        if (track.getState() != AudioTrack.STATE_UNINITIALIZED) {
                            track.play();
                            track.write(samples, 0, count);
                        }
                    } catch (Exception e) {
                        Log.d("AUDIOIR", "caught exception at internal try/catch\nMessage: " + e.getMessage());
                    }


                } catch (NullPointerException e) {
                    Log.d("AUDIOIR", "NullPointerException in Pronto playing.\nMessage: +" +
                            e.getMessage());
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    Log.d("AUDIOIR", "IllegalStateException in Pronto playing.\nMessage: +" +
                            e.getMessage());
                    e.printStackTrace();
                    track.flush();

                } catch (Exception e) {
                    Log.d("AUDIOIR", "Uhhh.... other exception.\nMessage: " + e.getMessage());
                }
            }
        };

        handler.post(r);

    }


    // See here: https://gist.github.com/slightfoot/6330866

    private short[] generateTones(float duration) {
        int count = (int)(44100.0 * 2.0 * duration) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i += 2){
            short sample = (short)(Math.sin(2 * Math.PI * i / (44100.0 / this.frequency)) * 0x7FFF);
            samples[i] = sample;
            samples[i + 1] = (short) (-1 * sample);
        }
        return samples;
    }

    private short[] generateSilence(float duration){
        int count = (int)(44100.0 * 2.0 * duration) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i+=2){
            short sample = 0;
            samples[i] = 0;
            samples[i + 1] = 0;
        }
        return samples;
    }


    protected void debugPrintHex(){
        Log.d("AUDIOIR", this.hex.toString());
        Log.d("AUDIOIR", "Duration: " + this.duration);
        Log.d("AUDIOIR", "Frequency: " + this.frequency);
        Log.d("AUDIOIR", "Pulse length: " + this.pulse);
        Log.d("AUDIOIR", "Lead In: " + this.c1LeadInOn + ", " + this.c1LeadInOff);
        Log.d("AUDIOIR", "Lead Out: " + this.c1LeadOutOn + ", " + this.c1LeadOutOff);
        Log.d("AUDIOIR", "1 pair: " + this.c1On[0] + ", " + this.c1On[1]);
        Log.d("AUDIOIR", "0 pair: " + this.c1Off[0] + ", " + this.c1Off[1]);
    }



}
