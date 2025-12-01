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

        // Detect devices
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        List<Mixer.Info> inputs = new ArrayList<>();
        List<Mixer.Info> outputs = new ArrayList<>();

        for (Mixer.Info info : mixers) {
            Mixer mixer = AudioSystem.getMixer(info);

            // Check if mixer supports an actual audio input line
            if (mixer.isLineSupported(new DataLine.Info(TargetDataLine.class, null))) {
                inputs.add(info);
            }

            // Check if mixer supports an actual audio output line
            if (mixer.isLineSupported(new DataLine.Info(SourceDataLine.class, null))) {
                outputs.add(info);
            }
        }

        // List
        System.out.println("=== INPUT DEVICES ===");
        for (int i = 0; i < inputs.size(); i++)
            System.out.println(i + ": " + inputs.get(i).getName());

        System.out.println("\n=== OUTPUT DEVICES ===");
        for (int i = 0; i < outputs.size(); i++)
            System.out.println(i + ": " + outputs.get(i).getName());

        Scanner sc = new Scanner(System.in);

        System.out.print("\nPick input index: ");
        Mixer.Info inputChoice = inputs.get(sc.nextInt());

        System.out.print("Pick output index: ");
        Mixer.Info outputChoice = outputs.get(sc.nextInt());

        // Open lines
        TargetDataLine inputLine = (TargetDataLine)
                AudioSystem.getMixer(inputChoice)
                        .getLine(new DataLine.Info(TargetDataLine.class, format));
        inputLine.open(format);
        inputLine.start();

        SourceDataLine outputLine = (SourceDataLine)
                AudioSystem.getMixer(outputChoice)
                        .getLine(new DataLine.Info(SourceDataLine.class, format));
        outputLine.open(format);
        outputLine.start();

        System.out.println("\nRouting audio... Press Ctrl+C to stop.");

        // Pass-through loop
        byte[] buffer = new byte[4096];
        while (true) {
            int bytesRead = inputLine.read(buffer, 0, buffer.length);
            if (bytesRead > 0)
                outputLine.write(buffer, 0, bytesRead);
        }
    }
}
