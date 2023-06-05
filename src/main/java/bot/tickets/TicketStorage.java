package bot.tickets;

import bot.data.BotFiles;
import bot.util.Bot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
    protected String read(File file) {
        try {
            String json = String.join(System.lineSeparator(), Files.readAllLines(Path.of(file.getAbsolutePath())));

            if (!json.isBlank()) return json;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * @return An immutable {@link Map} containing all the basic-information about the tickets.
     */
    protected Map<String, Ticket> getTickets() {
        String json = read(tickets);
        TypeToken<Map<String, Ticket>> token = new TypeToken<>() {};
        Map<String, Ticket> tickets = gson.fromJson(json, token.getType());

        return tickets == null
                ? Map.of()
                : Collections.unmodifiableMap(tickets);
    }

    /**
     * Returns the ticket matching the id you provided.
     *
     * @param id The id of the channel of the ticket.
     * @return A {@link Ticket} object of the ticket containing all the basic-information, null otherwise.
     */
    protected Ticket getTicket(String id) {
        final Map<String, Ticket> tickets = getTickets();

        for (String k : tickets.keySet()) {
            Ticket value = tickets.get(k);
            if (k.equals(id))
                return value;
        }

        return null;
    }

    /**
     * Stores a ticket in the JSON file.
     *
     * @param issuer Who created the ticket.
     * @param subject The subject of the ticket.
     * @param description The description of the ticket that holds more information about it.
     */
    protected void storeTicket(Member issuer, MessageChannel channel, String subject, String description) {
        final Map<String, Ticket> tickets = new HashMap<>(getTickets());
        int ticketId = 0;
        LocalDateTime now = LocalDateTime.now();

        // Finding the greatest value of the keys
        for (Ticket k : tickets.values()) {
            int value = k.id;

            if (value >= ticketId)
                ticketId = value;
        }

        tickets.put(
                channel.getId(),
                new Ticket(
                        issuer.getId(),
                        now.toEpochSecond(ZoneOffset.UTC),
                        ticketId + 1,
                        subject,
                        description,
                        new Reason(false, null)
                )
        );

        String write = gson.toJson(tickets);
        write(write, TicketStorage.tickets);
    }

    /**
     * Deletes a certain ticket.
     *
     * @param id The KEY of the ticket that will have the value changed, NOT the ID of the ticket.
     */
    protected void setRefused(String id, boolean refused, String reason) {
        final Map<String, Ticket> tickets = new HashMap<>(getTickets());

        Ticket initial = tickets.get(id);
        if (initial == null) return;

        tickets.put(id, new Ticket(initial.issuer, initial.creation, initial.id, initial.subject, initial.description, new Reason(refused, reason)));

        String write = gson.toJson(tickets);
        write(write, TicketStorage.tickets);
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

        if (dir.mkdirs())
            Bot.log("<YELLOW>'tickets' directory was not found. Creating a new one.", false);

        if (!dir.isDirectory())
            throw new UnsupportedOperationException("The provided file object is not a directory");

        File file = new File(dir, channel.getId() + ".txt");
        file.createNewFile();

        setSpecs(channel, file);

        return file;
    }

    /**
     * Sets the proper specifications for the temporary file.
     *
     * @param channel The channel of the ticket.
     */
    private void setSpecs(MessageChannel channel, File file) {
        Ticket ticket = getTicket(channel.getId());
        LocalDateTime creation = LocalDateTime.ofEpochSecond(ticket.creation, 0, ZoneOffset.UTC);
        String formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss").format(creation);

        String content = String.format("""
                Quem criou: %s
                Quando: %s
                Assunto: %s
                Descrição: %s
                ------------------------------------------------------------
                """,
                ticket.issuer,
                formatted,
                ticket.subject,
                ticket.description
        );

        write(content, file);
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
        User issuer = message.getMember().getUser();
        MessageChannel channel = message.getChannel();
        String content = message.getContentDisplay();

        File temporary = getTemporary(channel.getId());
        if (temporary == null) temporary = newTemporary(message.getChannel());

        String read = read(temporary);
        String write = String.format(
                "[%s] %s: %s",
                DateTimeFormatter.ofPattern("HH:mm:ss").format(now),
                issuer.isBot() ? issuer.getName() + " [BOT]" : issuer.getName(),
                content
        );

        write(read + "\n" + write, temporary);
    }

    /**
     * @return An immutable {@link List<File>} containing all the open ticket files
     * or an empty {@link List<File>} if no files are found.
     */
    protected List<File> getTemporaries() {
        final File dir = BotFiles.DIR_TICKETS;

        if (dir.mkdirs())
            Bot.log("<YELLOW>'tickets' directory was not found. Creating a new one.", false);

        if (!dir.isDirectory())
            throw new UnsupportedOperationException("The provided file object is not a directory");

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
     * @param id The id of the ticket (the ticket-channel id).
     * @return A {@link File} object of the history of the ticket channel or null if no files are found.
     */
    protected File getTemporary(String id) {
        final List<File> tickets = getTemporaries();

        for (File f : tickets)
            if (f.getName().startsWith(id))
                return f;

        return null;
    }

    /**
     * @param channel The possible-ticket channel to be tested.
     * @return {@code true} if the channel is a ticket channel, {@code false} otherwise.
     */
    protected boolean isFromTicket(MessageChannel channel) {
        final Map<String, Ticket> tickets = getTickets();

        for (String id : tickets.keySet())
            if (id.equals(channel.getId()))
                return true;

        return false;
    }

    protected record Ticket(
            String issuer,
            long creation,
            int id,
            String subject,
            String description,
            Reason refused
    ) {}

    protected record Reason(boolean status, String reason) {
    }
}