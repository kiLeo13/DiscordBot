package bot.tickets;

import bot.data.BotFiles;
import bot.util.Bot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketStorage {
    private static final File tickets = new File(BotFiles.DIR, "tickets.json");
    private static TicketStorage instance;
    private static final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    private TicketStorage() {}

    protected static TicketStorage create() {
        if (instance == null) instance = new TicketStorage();
        return instance;
    }

    /**
     * Reads the content of the specified file.
     *
     * @param file The file you want to read the content from.
     * @return A {@link String} object of the content of the provided {@link File}.
     */
    protected String readFile(File file) {
        try {
            String json = String.join("", Files.readAllLines(Path.of(file.getAbsolutePath())));

            if (!json.isBlank()) return json;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * @return An immutable {@link Map} containing all the closed-tickets.
     */
    protected Map<String, Ticket> getTickets() {
        String json = readFile(tickets);
        TypeToken<Map<String, Ticket>> token = new TypeToken<>() {};
        Map<String, Ticket> tickets = gson.fromJson(json, token.getType());

        return tickets == null
                ? Map.of()
                : Collections.unmodifiableMap(tickets);
    }

    /**
     * Stores a ticket in the JSON file.
     *
     * @param issuer Who created the ticket.
     * @param subject The subject of the ticket.
     * @param description The description of the ticket that holds more information about it.
     */
    protected void storeTicket(Member issuer, String subject, String description) {
        final Map<String, Ticket> tickets = new HashMap<>(getTickets());
        int max = Integer.MIN_VALUE;
        LocalDateTime now = LocalDateTime.now();

        // Finding the greatest value of the keys
        for (String k : tickets.keySet()) {
            int value = Integer.parseInt(k);

            if (value >= max)
                max = value;
        }

        tickets.put(
                String.valueOf(max + 1),
                new Ticket(issuer.getId(), now.toEpochSecond(ZoneOffset.UTC), subject, description)
        );

        String write = gson.toJson(tickets);
        write(write, TicketStorage.tickets);
    }

    /**
     * Deletes a certain ticket.
     *
     * @param id The id of the ticket to be deleted.
     */
    protected void deleteTicket(String id) {
        final Map<String, Ticket> tickets = new HashMap<>(getTickets());

        tickets.remove(id);

        String json = gson.toJson(tickets);
        write(json, TicketStorage.tickets);
    }

    /**
     * Writes information to a file.
     *
     * @param content The content to be written to the file.
     * @param file The {@link File} in which you want to store the {@code content}.
     */
    protected void write(String content, File file) {
        try (
                OutputStream out = Files.newOutputStream(Path.of(file.getAbsolutePath()));
                Writer writer = new OutputStreamWriter(out)
        ) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param channel The channel of the ticket.
     * @return The {@link File} object representation of the created file.
     * @throws IOException If an I/O error occurred.
     */
    protected File newTemporary(MessageChannel channel) throws IOException {
        final File dir = BotFiles.DIR_TICKETS;

        if (!dir.isDirectory())
            throw new UnsupportedOperationException("The provided file object is not a directory");

        if (!dir.mkdirs())
            Bot.log("<YELLOW>'tickets' directory was not found. Creating a new one.", false);

        File file = new File(dir, channel.getId() + ".txt");
        file.createNewFile();

        return file;
    }

    /**
     * A temporary file is a file that will exist while a ticket is open
     * to store all the content/messages of that conversation.
     * <p>These files are deleted after the ticket get closed.
     * <p>This method stores the message to the correct file.
     *
     * @param message The message you want to store.
     * @throws IOException If an I/O error occurred.
     */
    protected void storeTemporary(Message message) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        Member issuer = message.getMember();
        MessageChannel channel = message.getChannel();
        String content = message.getContentDisplay();
        int secs = now.getSecond();
        int mins = now.getMinute();
        int hrs = now.getHour();

        File temporary = getTemporary(channel);
        if (temporary == null) temporary = newTemporary(message.getChannel());

        String read = readFile(temporary);
        String write = String.format(
                "[%s:%s:%s] %s: %s",
                hrs < 10 ? "0" + secs : secs,
                mins < 10 ? "0" + mins : mins,
                secs < 10 ? "0" + secs : secs,
                issuer.getUser().getName(),
                content
        );

        if (read.isBlank())
            write(write, temporary);
        else
            write(read + "\n" + write, temporary);
    }

    /**
     * @return An immutable {@link List<File>} containing all the open ticket files
     * or an empty {@link List<File>} if no files are found.
     */
    protected List<File> getTemporaries() {
        final File dir = BotFiles.DIR_TICKETS;

        if (!dir.isDirectory())
            throw new UnsupportedOperationException("The provided file object is not a directory");

        if (!dir.mkdirs())
            Bot.log("<YELLOW>'tickets' directory was not found. Creating a new one.", false);

        File[] files = dir.listFiles();

        return files == null || files.length == 0
                ? List.of()
                : List.of(files);
    }

    /**
     * Gets the File object storing all the ticket history messages.
     * <p><b>This method will NOT create a new file if no files are found.</b>
     * <p>If you want to create a new file if the one does not exist, use {@link #newTemporary(MessageChannel)}
     *
     * @param channel The channel of the ticket.
     * @return A {@link File} object of the history of the ticket channel or null if no files are found.
     */
    protected File getTemporary(MessageChannel channel) {
        final List<File> tickets = getTemporaries();

        for (File f : tickets)
            if (f.getName().startsWith(channel.getId()))
                return f;

        return null;
    }

    /**
     * @param channel The possible-ticket channel to be tested.
     * @return {@code true} if the channel is a ticket channel, {@code false} otherwise.
     */
    protected boolean isFromTicket(MessageChannel channel) {
        final List<File> tickets = getTemporaries();

        for (File f : tickets)
            // It's easier to just check startsWith()
            // Cause the file extension ending would dismatch the channel id
            if (f.getName().startsWith(channel.getId()))
                return true;

        return false;
    }

    protected record Ticket(
            String issuer,
            long creation,
            String subject,
            String description
    ) {}
}