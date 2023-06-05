package bot.tickets;

import bot.data.BotData;
import bot.util.Bot;
import bot.util.content.Categories;
import bot.util.content.Channels;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TicketInfoCreation extends ListenerAdapter {
    private static final TicketStorage manager = TicketStorage.create();

    @SubscribeEvent
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

        if (!event.getInteraction().getModalId().equals("create-ticket")) return;

        Guild guild = event.getGuild();
        Member member = event.getMember();
        Category supportCategory = guild.getCategoryById(Categories.SUPPORT.id());

        if (supportCategory == null) {
            event.reply("Não foi possível abrir um ticket. Nossa requipe já está trabalhando para resolver o problema").setEphemeral(true).queue();
            Bot.log("Categoria de 'Support' não foi encontrada. ID: " + Categories.SUPPORT.id(), true);
            TextChannel ajudantes = guild.getTextChannelById(Channels.STAFF_AJUDANTES_CHANNEL.id());

            if (ajudantes != null)
                ajudantes.sendMessage("Não foi possíven encontrar a categoria 'Support' (`" + Categories.SUPPORT.id() + "`). Ignorando ticket enviado por: `" + member.getUser().getAsTag() + "`.").queue();

            return;
        }

        String subject = event.getInteraction().getValue("subject").getAsString();
        String description = event.getInteraction().getValue("description").getAsString();

        supportCategory
                .createTextChannel("🔸｜ticket-" + member.getUser().getName())
                .setTopic("**Assunto: **" + subject)
                .addPermissionOverride(guild.getPublicRole(), null, List.of(Permission.VIEW_CHANNEL))
                .queue(c -> {
                    event.reply("Ticket criado: " + c.getAsMention() + ". Dirija-se até o canal de texto citado para mais informações.")
                            .setEphemeral(true)
                            .queue();

                    // Storing the ticket to the JSON fie

                    c.sendMessageEmbeds(embed(event, subject.replace("`", ""), description.replace("`", ""))).queue(e -> {
                        manager.storeTicket(member, c, subject, description);
                        c.sendMessage(member.getAsMention() + " sinta-se a vontade para nos fornecer mais detalhes ou imagens relacionadas ao seu problema.").queue();
                    });
                });

        // Update their cooldown
        OpenTicket.cooldown.put(member.getId(), System.currentTimeMillis());
    }

    private MessageEmbed embed(ModalInteractionEvent event, String subject, String description) {
        final EmbedBuilder builder = new EmbedBuilder();
        Member issuer = event.getMember();

        builder
                .setAuthor("Ticket de " + issuer.getUser().getAsTag(), null, issuer.getUser().getAvatarUrl())
                .addField("🏷 Assunto", "```\n" + subject + "\n```", false)
                .addField("🏴 Descrição", "```\n" + description + "\n```", false)
                .setColor(BotData.DEFAULT_COLOR);

        return builder.build();
    }
}