package bot.commands;

import bot.data.BotData;
import bot.util.interfaces.SlashExecutor;
import bot.util.managers.economy.Balance;
import bot.util.managers.economy.EconomyManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TransferMemberData implements SlashExecutor {
    private static final EconomyManager manager = new EconomyManager(BotData.UNBELIEVABOAT_TOKEN);
    private static final HashMap<String, Backup> history = new HashMap<>();
    private long oldTotal;
    private long newTotal;

    @Override
    public void process(SlashCommandInteractionEvent event) {

        OptionMapping actionInput = event.getOption("action");
        Member from = event.getOption("from").getAsMember();
        Member current = event.getOption("to").getAsMember();
        String action = actionInput == null ? "none" : actionInput.getAsString();

        // Sometimes we just want to revert something, right?
        if (action.equals("revert")) {
            if (undo(current))
                event.reply("Removendo cargos dados para `" + current.getUser().getName() + "` na última operação...").setEphemeral(true).queue();
            else
                event.reply("Não encontramos o usuário fornecido no histórico! Se for um caso extremo, use `" + BotData.PREFIX + "removeroles <@user>` para remover todos os cargos do usuário.").setEphemeral(true).queue();
            return;
        }

        if (from.getPermissions().size() > current.getPermissions().size() && !action.equals("ignore")) {
            event.reply("O membro `" + from.getUser().getName() + "` tem mais permissões que `" + current.getUser().getName() + "`. Para ignorar e prosseguir mesmo assim, defina `Ignore` no argumento `action`.").setEphemeral(true).queue();
            return;
        }

        StringBuilder response = new StringBuilder();

        if (apply(from, current))
            response.append("Enviando cargos de `" + from.getUser().getName() + "` para `" + current.getUser().getName() + "`.\n");
        else {
            event.reply("Algo deu errado! Talvez você forneceu o mesmo usuário em ambos os argumentos ou eu não tenho permissão para dar tais cargos.").setEphemeral(true).queue();
            return;
        }

        oldTotal = manager.getBalance(from).total();

        if (oldTotal < 1000)
            response.append("Nenhuma quantia de dinheiro foi recuperada pois o valor é insignificante.");
        else {
            newTotal = (int) Math.floor(oldTotal / 0.3); // 30% of their old balance
            Balance newBalance = manager.setBalance(current, newTotal, 0, "Perdeu a conta.");
            response.append("Quantia recuperada: `" + newBalance.total() + "`.");
            backup(from, current, oldTotal);
        }

        event.reply(response.toString()).setEphemeral(true).queue();
    }

    private boolean undo(Member member) {
        Guild guild = member.getGuild();

        if (!history.containsKey(member.getId()))
            return false;

        final List<Role> roles = history.get(member.getId()).roles;

        guild.modifyMemberRoles(member, null, roles).queue();
        history.remove(member.getId());
        return true;
    }

    private void backup(Member old, Member current, long oldTotal) {
        history.put(
                current.getId(),
                new Backup(getNotPresentRoles(old, current), oldTotal)
        );
    }

    private boolean apply(Member old, Member current) {
        final List<Role> roles = old.getRoles();
        Guild guild = old.getGuild();

        if (old.getIdLong() == current.getIdLong())
            return false;

        try {
            guild.modifyMemberRoles(current, roles, null).queue();
            return true;
        } catch (HierarchyException e) {
            return false;
        }
    }

    private List<Role> getNotPresentRoles(Member old, Member current) {
        List<Role> secondRoles = current.getRoles();
        final List<Role> added = new ArrayList<>();

        for (Role r : old.getRoles()) {
            if (!secondRoles.contains(r))
                added.add(r);
        }

        return Collections.unmodifiableList(added);
    }

    private record Backup(
        List<Role> roles,
        long oldAmount
    ) {}
}