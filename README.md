# Audio Router CLI  
A simple cross-platform command-line application that detects all available audio input and output devices on your system and routes audio from any selected input to any selected output—independent of the system defaults.

## Features
- Lists all available audio input devices  
- Lists all available audio output devices  
- Allows the user to choose any input and any output  
- Routes audio from the chosen input to the chosen output in real time  
- Works on macOS, Windows, and Linux  
- Packaged as a runnable JAR  
- No GUI—simple terminal interface

## Requirements
- Java 17 or newer  
- IntelliJ IDEA (optional but recommended for development)  
- Maven or Gradle (depending on your build setup)

## How It Works
The application uses **javax.sound.sampled** to:
1. Enumerate all mixers (audio devices)
2. Filter them into input and output device lists
3. Allow the user to choose both
4. Pipe audio from the selected input line into the selected output line

This allows full manual control even if your OS does not allow advanced routing by default.

## Running from Source
Clone the repository:

```bash
git clone https://github.com/yourusername/audio-router-cli.git
cd audio-router-cli
```
Compile and run:

```bash
./mvnw package
java -jar target/audio-router-cli.jar
```
—or if using Gradle—

```bash
./gradlew build
java -jar build/libs/audio-router-cli.jar
```
## Building the JAR
The JAR is built automatically by your build system:

Maven:

```bash
mvn clean package
```
Gradle:

```bash
gradlew clean build
```
The resulting JAR will be found in either:

target/ (Maven)

build/libs/ (Gradle)

## Usage
Run the program:

```bash
java -jar audio-router-cli.jar
```
Follow the terminal prompts to:

Select an input device

Select an output device

Begin routing audio

Press Ctrl+C to stop the stream.

## Platform Notes
macOS
- The app works with all CoreAudio devices.
- Third-party drivers like BlackHole or Loopback also appear automatically.

Linux
- Uses ALSA/JACK-compatible devices.
- If certain devices don't appear, ensure relevant audio packages are installed.

Windows
- Compatible with all standard system audio devices, including virtual devices.
