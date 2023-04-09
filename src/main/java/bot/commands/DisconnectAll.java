package bot.commands;

import bot.util.Messages;
import bot.util.SlashExecutor;
import bot.util.StaffRoles;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DisconnectAll implements SlashExecutor {
    private Guild guild;

    @Override
    public void runSlash(SlashCommandInteractionEvent event) {

        VoiceChannel voiceChannel = event.getOption("channel").getAsChannel().asVoiceChannel();
        String filter = event.getOption("filter") == null ? "none" : event.getOption("filter").getAsString();
        guild = event.getGuild();
        List<Member> connected = voiceChannel.getMembers();
        String size = connected.size() < 10 ? "0" + connected.size() : String.valueOf(connected.size());
        String suffix = connected.size() == 1 ? "membro" : "membros";

        if (connected.isEmpty()) {
            event.reply(Messages.ERROR_VOICE_CHANNEL_ALREADY_EMPTY.message()).setEphemeral(true).queue();
            return;
        }

        if (filter.equals("none")) {
            event.reply(String.format("""
                    Desconectando `%s` %s de `#%s`...
                    Filtros: `nenhum`.
                    """, size, suffix, voiceChannel.getName())).setEphemeral(true).queue();

            connected.forEach(m -> guild.kickVoiceMember(m).queue());
            return;
        }

        try {
            switch (filter) {
                case "staff" -> disconnect(voiceChannel, event, StaffRoles.ROLE_STAFF);
                case "eventos" -> disconnect(voiceChannel, event, StaffRoles.ROLES_EVENTOS);
                case "radio" -> disconnect(voiceChannel, event, StaffRoles.ROLES_RADIO);

                case "both" -> disconnect(voiceChannel, event, StaffRoles.ROLES_RADIO, StaffRoles.ROLES_EVENTOS);

                default -> event.reply("Nenhum filtro foi encontrado.").setEphemeral(true).queue();
            }
        } catch (RoleNotFoundException e) {
            event.reply("```\n" + e.getMessage() + "\n```").setEphemeral(true).queue();
        }
    }

    private void disconnect(VoiceChannel voiceChannel, SlashCommandInteractionEvent e, StaffRoles... filter) throws RoleNotFoundException {
        List<Role> roles = new ArrayList<>();
        List<Member> connected = voiceChannel.getMembers();
        List<Member> ignored = new ArrayList<>();

        for (StaffRoles f : filter) {
            Role role = guild.getRoleById(f.toId());

            if (role == null) throw new RoleNotFoundException(f.name() + " cannot be null");
            roles.add(role);
        }

        for (Role r : roles) {
            connected.forEach(m -> {
                if (m.getRoles().contains(r)) ignored.add(m);
            });
        }

        int disconnected = connected.size() - ignored.size();
        String strDisconnected = disconnected < 10 ? "0" + disconnected : String.valueOf(disconnected);
        String suffix = disconnected == 1 ? "membro" : "membros";

        connected.forEach(m -> {
            if (!ignored.contains(m)) guild.kickVoiceMember(m).queue();
        });

        if (disconnected == 0) e.reply("Nenhum membro foi desconectado").setEphemeral(true).queue();
        else e.reply(String.format("""
                Desconectando `%s` %s de `#%s`...
                """, strDisconnected, suffix, voiceChannel.getName())).setEphemeral(true).queue();
    }
}