# Bandos FAHHHH

A RuneLite plugin that plays your chosen **FAHHHH** clip whenever General Graardor starts his ground slam (ranged attack).

## Run it

Requires Java 11 or newer; Java 17 or 21 is recommended for development.

```sh
./gradlew run
```

This starts a separate RuneLite development client with the plugin loaded. In its plugin settings, search for **Bandos FAHHHH** and enable it. The selected FAHHHH clip is bundled and plays automatically on ground slams. Open the **FAH** sidebar and click **Play FAHHHH** to check playback. Adjust **Volume** in the plugin's settings. You can also choose a custom WAV and restore the bundled clip with **Use original FAHHHH**. Your selection is saved in the current RuneLite configuration profile.

Use a short, uncompressed PCM WAV file. MP3 is not supported directly. On macOS you can convert a local clip with:

```sh
afconvert -f WAVE -d LEI16 '/full/path/to/fah.mp3' '/full/path/to/fah.wav'
```

The bundled clip is the user-selected [Fahhh from Myinstants](https://www.myinstants.com/en/instant/fahhh-42300/), uploaded there by aleki1, converted from MP3 to PCM WAV. This plays alongside normal game audio and has independent volume. Each slam restarts the clip so long clips do not overlap.

## Build and check

```sh
./gradlew test jar
```

The plugin JAR is written to `build/libs/bandos-fah-1.0.0.jar`. Use the development launcher above to load it; copying a JAR into a normal RuneLite installation does not install an external plugin. This project has not been submitted to the RuneLite Plugin Hub.

## Trigger

The plugin listens for `AnimationChanged` on NPC `2215` (General Graardor), animation `7021` (ground slam). These match RuneLite's generated `GODWARS_BANDOS_AVATAR` and `GODWARS_BANDOS_RANGED` identifiers. Repeated notifications for the same NPC in the same game tick play only once. Every visible Graardor slam triggers, including when he targets another player in your team.

Audio loads on a dedicated worker and is buffered before the fight. Playback and disk reads do not run on the game thread. Disabling the plugin closes its audio resources and removes the sidebar.

References: [RuneLite example plugin](https://github.com/runelite/example-plugin), [animation definitions](https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/gameval/AnimationID.java), [NPC definitions](https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/gameval/NpcID.java).

Unit tests cover slam detection, duplicate notifications, subsequent slams, and filtering other actors/animations. Live boss timing and audible playback still need an in-game check with the selected clip.

## Submit to RuneLite's Plugin Hub

1. Test the plugin in-game with `./gradlew run` and check the preview and a live Graardor ground slam. Confirm permission to distribute the bundled audio (see [asset notices](THIRD_PARTY_NOTICES.md)).
2. Fork [runelite/plugin-hub](https://github.com/runelite/plugin-hub) on GitHub and create a branch such as `codex/add-bandos-fah` in your fork.
3. In that fork, add a text file named `plugins/bandos-fah` with exactly two lines:

   ```properties
   repository=https://github.com/cam-petrie/bandos-fah.git
   commit=PASTE_FULL_40_CHARACTER_PLUGIN_COMMIT_SHA_HERE
   ```

   Obtain the full commit SHA by running `git rev-parse HEAD` in this plugin repository after committing and pushing all changes.
4. Commit the manifest in your fork and open a pull request to `runelite/plugin-hub` with the title `Add Bandos FAHHHH`. Describe the ground-slam sound effect and your testing.
5. Address the automated build/check results and reviewer feedback. If you change the plugin code, push those changes here and update the manifest's `commit=` value in the same submission PR.
6. RuneLite maintainers review and merge the submission before it becomes available in the Plugin Hub. Future updates follow the same process with a newer commit hash.

You submit the manifest pointing to this source repository; RuneLite builds the plugin. Follow the [official submission instructions](https://github.com/runelite/plugin-hub#submitting-a-plugin) for current requirements.
