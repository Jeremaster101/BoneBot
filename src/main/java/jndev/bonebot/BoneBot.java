package jndev.bonebot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * BoneBot is a simple discord bot for the ISUCF'V'MB Trombone discord
 *
 * @author JNDev (Jeremaster101)
 */
public class BoneBot extends ListenerAdapter {
    
    /**
     * list of quotes loaded from the quotes file
     */
    private static final ArrayList<String> quotes = new ArrayList<>();
    
    /**
     * list of phrases loaded from the phrases file
     */
    private static final ArrayList<String> phrases = new ArrayList<>();
    
    /**
     * list of memes loaded from the memes folder
     */
    private static final ArrayList<File> memes = new ArrayList<>();
    
    /**
     * create the bot and run it
     *
     * @param args arg 0 is the bot token
     * @throws LoginException when unable to log in to bot account
     */
    public static void main(String[] args) throws LoginException {
        
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }
        // prevent bot from starting without a token
        
        JDABuilder.createLight(args[0])
                .addEventListeners(new BoneBot())
                .setActivity(Activity.playing("Trombone"))
                .build();
        // initialize bot
        
        loadFiles();
    }
    
    /**
     * load all data from files
     */
    private static void loadFiles() {
        try {
            Scanner fileScanner = new Scanner(new File("phrases.txt"));
            phrases.clear();
            while (fileScanner.hasNextLine()) phrases.add(fileScanner.nextLine());
            fileScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // read phrases from file
        
        try {
            Scanner fileScanner = new Scanner(new File("quotes.txt"));
            quotes.clear();
            while (fileScanner.hasNextLine()) quotes.add(fileScanner.nextLine());
            fileScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // read quotes from file
        
        File dir = new File("memes");
        memes.clear();
        memes.addAll(Arrays.asList(dir.listFiles()));
        // load all meme files
    }
    
    /**
     * respond to users when they say certain key words
     *
     * @param e message received event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot() || e.getAuthor().isFake()) return;
        // ignore messages from bots to prevent loops
        
        String msg = e.getMessage().getContentRaw().toLowerCase();
        // convert whole message to lowercase for parsing
        
        if (msg.equals("!reload")) {
            loadFiles();
            e.getMessage().delete().queue();
        }
        // reload all files
        
        for (String phrase : phrases) {
            String[] triggerAndPhrase = phrase.split(" // ");
            String[] triggers = triggerAndPhrase[0].split(" / ");
            int count = 0;
            for(String trigger : triggers) {
                if(msg.contains(trigger)) count++;
            }
            if (count == triggers.length) {
                e.getChannel().sendMessage(triggerAndPhrase[1]).queue();
            }
        }
        // respond to a phrase if a trigger word is said
        
        if (msg.equals("!quote")) {
            Random r = new Random();
            int randInt = r.nextInt(quotes.size());
            e.getMessage().delete().queue();
            e.getChannel().sendMessage(quotes.get(randInt)).queue();
        }
        // send random quote when "!quote" is typed
        
        if (msg.equals("!meme")) {
            Random r = new Random();
            int randInt = r.nextInt(memes.size());
            e.getMessage().delete().queue();
            e.getChannel().sendFile(memes.get(randInt)).queue();
        }
        // send random meme when "!meme" is typed
        
    }
}