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

        // Stable, low-latency friendly format
        AudioFormat format = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                44100f,
                16,
                2,
                4,      // frame size
                44100f, // frame rate
                false   // little endian
        );

        // ===== Detect devices =====
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        List<Mixer.Info> inputs = new ArrayList<>();
        List<Mixer.Info> outputs = new ArrayList<>();

        for (Mixer.Info info : mixers) {
            Mixer mixer = AudioSystem.getMixer(info);

            if (mixer.isLineSupported(new DataLine.Info(TargetDataLine.class, format))) {
                inputs.add(info);
            }

            if (mixer.isLineSupported(new DataLine.Info(SourceDataLine.class, format))) {
                outputs.add(info);
            }
        }

        // ===== List devices =====
        System.out.println("=== INPUT DEVICES ===");
        for (int i = 0; i < inputs.size(); i++)
            System.out.println(i + ": " + inputs.get(i).getName());

        System.out.println("\n=== OUTPUT DEVICES ===");
        for (int i = 0; i < outputs.size(); i++)
            System.out.println(i + ": " + outputs.get(i).getName());

        // ===== User chooses =====
        Scanner sc = new Scanner(System.in);

        System.out.print("\nPick input index: ");
        Mixer.Info inputChoice = inputs.get(sc.nextInt());

        System.out.print("Pick output index: ");
        Mixer.Info outputChoice = outputs.get(sc.nextInt());

        // ===== Open lines with explicit low-latency buffer =====

        // Try a small buffer — you can tune this (128, 256, 512)
        int bufferSize = 256;

        DataLine.Info inputInfo = new DataLine.Info(TargetDataLine.class, format, bufferSize);
        DataLine.Info outputInfo = new DataLine.Info(SourceDataLine.class, format, bufferSize);

        TargetDataLine inputLine = (TargetDataLine)
                AudioSystem.getMixer(inputChoice).getLine(inputInfo);
        inputLine.open(format, bufferSize);
        inputLine.start();

        SourceDataLine outputLine = (SourceDataLine)
                AudioSystem.getMixer(outputChoice).getLine(outputInfo);
        outputLine.open(format, bufferSize);
        outputLine.start();

        // Flush initial buffers (reduces startup click + delay)
        outputLine.flush();

        System.out.println("\nRouting audio with low-latency... Press Ctrl+C to stop.");

        // ===== Tight low-latency audio loop =====
        byte[] buffer = new byte[bufferSize];

        while (true) {
            int bytesRead = inputLine.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                outputLine.write(buffer, 0, bytesRead);
            }
        }
    }
}
