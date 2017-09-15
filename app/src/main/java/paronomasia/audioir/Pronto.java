package paronomasia.audioir;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Braden 9/9/17.
 */


public class Pronto {

    private float frequency;        // Frequency at which the light should flash
    private float pulse;            // Length of a given "unit"
    private ArrayList<String> hex;  // Tokenized list of each hex number
    private float duration;         // Total duration in seconds.
    private int pairCount = 0;
    private int c1LeadInOn = 0;
    private int c1LeadInOff = 0;
    private int c1On[] = {0, 0};
    private int c1Off[] = {0, 0};
    private int c1LeadOutOn = 0;
    private int c1LeadOutOff = 0;


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
                Log.d("Pronto", "Error reading in pronto hex values.");
            }

            // Formula: 1000000 / (N * .241246) where N=2nd pronto number in decimal
            this.frequency = (float) (1000000 / (Integer.parseInt(this.hex.get(1), 16) * 0.241246));
            this.pulse = (float) 1 / this.frequency;


            // Identify total duration for c1

            int numPulses = 0;
            int pairCount = (Integer.parseInt(this.hex.get(2), 16) + 2 ) * 2;
            // hex digits = (num pairs + 1 lead in + 1 lead out) * 2
            for(int i = 0; i < pairCount; i++) {
                numPulses = numPulses + Integer.parseInt(this.hex.get(4 + i), 16);
            }
            this.duration = numPulses * this.pulse;

        }
    }


    public boolean analyze(){
        // Identify total duration for c1 and the On/Off values.
        // I think this may only be valid for NEC formats?

        int numPulses = 0;
        this.pairCount = (Integer.parseInt(this.hex.get(2), 16) + 2 ) * 2;
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

    // Consider starting a new thread for this task:
    public void generateAndPlay(){
        if(this.duration == 0){
            if(!analyze()){
	    	// Something's wrong with the code.
		Log.d("PRONTO", "Error handling Pronto Hex");
	    }
	}

        // Use the generate_*_Samples() functions below and combine their output into an AudioTrack

        int count = (int) (44100.0 * 2.0 * (this.duration)) & ~1;
        short[] samples = new short[count];
        int offset = 0;
        for(int i = 0; i < this.pairCount; i++){
            int currentVal = Integer.parseInt(this.hex.get(4 + i), 16);
            short[] generated;
            if(i % 1 == 0){
                generated = generateTones(this.pulse * currentVal);
            }
            else {
                generated = generateSilence(this.pulse * currentVal);
            }
            for(int j = 0; j < generated.length; j++){
                samples[offset + j] = generated[j];
            }
            offset += generated.length;
        }

        // Will this even work?

        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
        track.write(samples, 0, count);

        track.play();

    }


    // See here: https://gist.github.com/slightfoot/6330866

    private short[] generateTones(float duration) {
        int count = (int)(44100.0 * 2.0 * duration) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i += 2){
            short sample = (short)(Math.sin(2 * Math.PI * i / (44100.0 / this.frequency)) * 0x7FFF);
            samples[i] = sample;
            samples[i + 1] = sample;
        }
        return samples;
    }

    private short[] generateSilence(float duration){
        int count = (int)(44100.0 * 2.0 * duration) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i+=2){
            samples[i] = 0;
            samples[i + 1] = 0;
        }
        return samples;
    }


    protected void debugPrintHex(){
        Log.d("CODE", this.hex.toString());
        Log.d("CODE", "Lead In: " + c1LeadInOn + ", " + c1LeadInOff);
        Log.d("CODE", "Lead Out: " + c1LeadOutOn + ", " + c1LeadOutOff);
        Log.d("CODE", "1 pair: " + c1On[0] + ", " + c1On[1]);
        Log.d("CODE", "0 pair: " + c1Off[0] + ", " + c1Off[1]);
    }



}
