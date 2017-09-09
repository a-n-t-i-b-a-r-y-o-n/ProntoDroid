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



            // Identify total duration for c1:

            int numPulses = 0;
            // hex digits = (num pairs + 1 lead in + 1 lead out) * 2
            for(int i = 0; i < (Integer.parseInt(this.hex.get(2), 16) + 2 ) * 2; i++) {
                numPulses = numPulses + Integer.parseInt(this.hex.get(4 + i), 16);
            }
            this.duration = numPulses * this.pulse;

        }
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


        int sampleRate = 44100;  // ?
        int numSamples = (int) this.duration * sampleRate; // will that work?
        double sample[] = new double[numSamples];
        double freqOfTone = this.frequency; // hz
        byte generatedSnd[] = new byte[2 * numSamples];

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STREAM);

        audioTrack.play();

        // fill out the array
        while (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            for (int i = 0; i < numSamples; ++i) {
                //  float angular_frequency =
                sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
            }
            int i = 0;

            // convert to 16 bit pcm sound array
            // assumes the sample buffer is normalised.
            for (double dVal : sample) {
                short val = (short) (dVal * 32767);
                generatedSnd[i++] = (byte) (val & 0x00ff);
                generatedSnd[i++] = (byte) ((val & 0xff00) >>> 8);
            }
            audioTrack.write(generatedSnd, 0, numSamples);
        }

}


    protected void debugPrintHex(){
        Log.d("DEBUG", this.hex.toString());
    }



}
