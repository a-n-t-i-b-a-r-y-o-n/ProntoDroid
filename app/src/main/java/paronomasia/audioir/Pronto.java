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

    Handler handler = new Handler();

    int sampleRate = 44100;  // ?
    int totalSamples = (int) this.duration * sampleRate; // will that work?
    double sample[];
    double freqOfTone = this.frequency; // hz
    byte generatedSnd[];


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


    public void analyze(){
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
    }


    public void play(){

        // Extensive help from an answer here:
        // https://stackoverflow.com/questions/2413426/playing-an-arbitrary-tone-with-android

        // Read the tokenized hex and play it at the frequency.
        // So far it just plays the single signal frequency steadily for the duration.
        // I still need to get it to play the actual patterns. THIS IS NOT TESTED YET.

        // TODO:
        //  - Start a new thread. In that thread:
        //      - Play like the code below for the on patterns
        //      - Do nothing for the off patterns.

        this.totalSamples = (int) this.duration * sampleRate; // will that work?
        this.sample = new double[totalSamples];
        this.freqOfTone = this.frequency; // hz
        this.generatedSnd = new byte[2 * this.totalSamples];

        /*
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("EVENT", "genTone()");
                genTone();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("EVENT", "playSound()");
                        playSound();
                    }
                });
            }
        });
        thread.start();
        */
        genTone();
        playSound();




}
    void playSound(){
        int minSize = AudioTrack.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, minSize,
                AudioTrack.MODE_STREAM);

        audioTrack.play();
        audioTrack.write(generatedSnd, 0, 2*totalSamples);
    }

    void genTone(){
        // fill out the array
            for(int i = 0; i < this.pairCount; i++) {
                int numSamples = (int)  (this.pulse * Integer.parseInt(this.hex.get(4 + i), 16)) * sampleRate;
                if( i % 2 == 0){
                    for (int j = 0; j < numSamples; ++j) {
                        //  float angular_frequency =
                        sample[j] = Math.sin(2 * Math.PI * j / (sampleRate / freqOfTone));
                    }
                }
                else {
                    for(int j = 0; j < numSamples; ++j)
                        sample[j] = 0; // ?
                }
            }


            int i = 0;

            // convert to 16 bit pcm sound array
            // assumes the sample buffer is normalised.
            for (double dVal : sample) {
                short val = (short) (dVal * 32767);
                generatedSnd[i++] = (byte) (val & 0x00ff);
                generatedSnd[i++] = (byte) ((val & 0xff00) >>> 8);
            }

    }


    protected void debugPrintHex(){
        Log.d("CODE", this.hex.toString());
        Log.d("CODE", "Lead In: " + c1LeadInOn + ", " + c1LeadInOff);
        Log.d("CODE", "Lead Out: " + c1LeadOutOn + ", " + c1LeadOutOff);
        Log.d("CODE", "1 pair: " + c1On[0] + ", " + c1On[1]);
        Log.d("CODE", "0 pair: " + c1Off[0] + ", " + c1Off[1]);
    }



}
