package bot.commands;

import bot.Main;
import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

@CommandPermission()
public class AvatarBot implements CommandExecutor {
    private static final List<String> validation = List.of(".png", ".jpg", ".webp");

    @Override
    public void run(Message message) {
        
        List<Long> allowed = List.of(183645448509194240L, 727978798464630824L, 742729586659295283L, 596939790532739075L);
        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        MessageChannelUnion channel = message.getChannel();

        if (!allowed.contains(member.getIdLong())) return;

        List<Attachment> attachments = message.getAttachments();
        InputStream image;

        if (attachments.isEmpty()) {
            if (args.length < 2) {
                Bot.sendGhostMessage(channel, "Não encontrei nenhum arquivo ou link em sua mensagem, por favor forneça uma imagem ou um link válido.", 15000);
            } else {
                String sentence = content.substring(args[0].length() + 1);
                image = Bot.requestFile(sentence);

                try {
                    Icon icon = Icon.from(image);

                    Main.getApi().getSelfUser().getManager().setAvatar(icon).queue(
                            s -> channel.sendMessage("Imagem do bot foi alterada com sucesso.").queue(),
                            exception -> {
                                channel.sendMessage("Algo deu errado, verifique o console para saber mais sobre o erro. Erro: `" + exception.getMessage() + "`.").queue();
                                exception.printStackTrace();
                            });
                } catch (IOException | IllegalArgumentException e) {
                    Bot.sendGhostMessage(channel, "Algo deu errado ao procurar a imagem fornecida. Erro: `" + e.getMessage() + "`.", 10000);
                    return;
                }

            }

            return;
        }

        if (!isFileValid(attachments.get(0).getFileName())) {
            Bot.sendGhostMessage(channel, "O tipo de arquivo fornecido é inválido. Por favor, forneça: `[.png | .jpg | .webp]`.", 15000);
            return;
        }

        try {
            image = attachments.get(0).getProxy().download().get();

            Icon icon = Icon.from(image);
            Main.getApi().getSelfUser().getManager().setAvatar(icon).queue(
                    s -> channel.sendMessage("Imagem do bot foi alterada com sucesso.").queue(),
                    exception -> {
                        channel.sendMessage("Algo deu errado, verifique o console para saber mais sobre o erro. Erro: `" + exception.getMessage() + "`.").queue();
                        exception.printStackTrace();
                    });
        } catch (InterruptedException | ExecutionException | IOException e) {
            Bot.sendGhostMessage(channel, "Algo deu errado ao baixar a imagem. Erro: `" + e.getMessage() + "`.", 10000);
        } catch (NullPointerException e) {
            Bot.sendGhostMessage(channel, "Imagem não foi encontrada.", 10000);
        }
    }

    private boolean isFileValid(String str) {
        for (String s : validation)
            if (str.endsWith(s)) return true;
        
        return false;
    }
}