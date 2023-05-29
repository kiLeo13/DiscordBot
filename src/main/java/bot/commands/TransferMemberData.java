package bot.commands;

import bot.data.BotData;
import bot.util.Bot;
import bot.util.annotations.CommandPermission;
import bot.util.Messages;
import bot.util.economy.Balance;
import bot.util.economy.EconomyManager;
import bot.util.interfaces.CommandExecutor;
import bot.util.requests.DiscordManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@CommandPermission(permission = Permission.MANAGE_SERVER)
public class TransferMemberData implements CommandExecutor {
    private static final DiscordManager manager = DiscordManager.NewManager();
    private static final HashMap<String, List<Role>> roleHistory = new HashMap<>();

    @Override
    public void run(Message message) {

        String content = message.getContentRaw();
        String[] args = content.split(" ");
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();

        if (args.length < 3) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            return;
        }

        // Sometimes we just want to revert something, right?
        if (content.endsWith("--undo") || content.endsWith("--revert")) {
            Member target = Bot.member(guild, args[1]);

            if (undo(target))
                channel.sendMessage("Removendo cargos dados para `" + target.getUser().getAsTag() + "` na última operação...").queue();
            else
                Bot.tempMessage(channel, "Não encontramos o usuário fornecido no histórico! Se for um caso extremo, use `" + BotData.PREFIX + "removeroles <@user>` para remover todos os cargos do usuário.", 10000);
        }

        manager.members(guild, args[1], args[2]).onSuccess(l -> {
            if (l.size() < 2) {
                Bot.tempMessage(channel, "Um ou mais membros não foram encontrados.", 10000);
                return;
            }

            Member first = l.get(0);
            Member second = l.get(1);

            if (first.getPermissions().size() < second.getPermissions().size() && !content.endsWith("--ignore")) {
                Bot.tempMessage(channel, "O membro `" + first.getUser().getAsTag() + "` tem mais permissões que `" + first.getUser().getAsTag() + "`. Caso queira ignorar este aviso e aplicar os cargos mesmo assim, adicione `--ignore` no fim do comando.", 20000);
                return;
            }

            if (apply(first, second)) {
                channel.sendMessage("Cargos de `" + first.getUser().getAsTag() + "` foram transferidos para `" + second.getUser().getAsTag() + "` com sucesso!").queue(m -> {
                    final Balance balanceUpdate = updateBalance(first, second);

                    if (balanceUpdate.total() < 1000)
                        m.editMessage(m.getContentRaw() + "\nSaldo do banco não alterado. Motivo: valor insignificante.").queue();
                    else
                        m.editMessage(m.getContentRaw() + "\nSaldo alterado com sucesso! Total: `" + balanceUpdate.total() + "` (rank `#" + balanceUpdate.rank() + "`)").queue();
                });
            } else {
                Bot.tempMessage(channel, "Algo deu errado! Talvez seja algum cargo que eu não possa dar ou você forneceu o mesmo membro nos dois argumentos.", 10000);
            }
        });
    }

    private boolean undo(Member member) {
        Guild guild = member.getGuild();

        if (!roleHistory.containsKey(member.getId()))
            return false;

        guild.modifyMemberRoles(member, null, roleHistory.get(member.getId())).queue();
        roleHistory.remove(member.getId());
        return true;
    }

    private boolean apply(Member first, Member second) {
        final List<Role> roles = first.getRoles();
        Guild guild = first.getGuild();

        if (first.getIdLong() == second.getIdLong())
            return false;

        try {
            guild.modifyMemberRoles(second, roles, null).queue();
            roleHistory.put(second.getId(), getNotPresentRoles(first, second));
            return true;
        } catch (HierarchyException e) {
            return false;
        }
    }

    private List<Role> getNotPresentRoles(Member first, Member second) {
        List<Role> secondRoles = second.getRoles();
        final List<Role> added = new ArrayList<>();

        for (Role r : first.getRoles()) {
            if (!secondRoles.contains(r))
                added.add(r);
        }

        return Collections.unmodifiableList(added);
    }

    private Balance updateBalance(Member first, Member second) {
        final EconomyManager manager = new EconomyManager(BotData.UNBELIEVABOAT_TOKEN);

        Balance balance = manager.getBalance(first);

        // Members will only receive 30% of their money
        long newTotal = (long) Math.floor(balance.total() / 0.3);

        if (newTotal >= 1000)
            manager.updateBalance(second, newTotal, 0, null);

        return balance;
    }
}