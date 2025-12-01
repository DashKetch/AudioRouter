package app;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AudioRouter {

     static void main() throws Exception {

        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);

        // --- List devices ---
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        List<Mixer.Info> inputs = new ArrayList<>();
        List<Mixer.Info> outputs = new ArrayList<>();

        for (Mixer.Info info : mixers) {
            Mixer mixer = AudioSystem.getMixer(info);

            if (mixer.getTargetLineInfo().length > 0)
                inputs.add(info);

            if (mixer.getSourceLineInfo().length > 0)
                outputs.add(info);
        }

        // Print devices
        System.out.println("Inputs:");
        for (int i = 0; i < inputs.size(); i++)
            System.out.println(i + ": " + inputs.get(i).getName());

        System.out.println("\nOutputs:");
        for (int i = 0; i < outputs.size(); i++)
            System.out.println(i + ": " + outputs.get(i).getName());

        // Ask user
        Scanner sc = new Scanner(System.in);
        System.out.print("\nPick input index: ");
        Mixer.Info inInfo = inputs.get(sc.nextInt());
        System.out.print("Pick output index: ");
        Mixer.Info outInfo = outputs.get(sc.nextInt());

        // Open lines
        TargetDataLine inputLine = (TargetDataLine)
                AudioSystem.getMixer(inInfo).getLine(new DataLine.Info(TargetDataLine.class, format));
        inputLine.open(format);
        inputLine.start();

        SourceDataLine outputLine = (SourceDataLine)
                AudioSystem.getMixer(outInfo).getLine(new DataLine.Info(SourceDataLine.class, format));
        outputLine.open(format);
        outputLine.start();

        System.out.println("Routing audio... Ctrl+C to stop.");

        // Copy audio
        byte[] buffer = new byte[4096];
        while (true) {
            int bytes = inputLine.read(buffer, 0, buffer.length);
            if (bytes > 0) outputLine.write(buffer, 0, bytes);
        }
    }
}
