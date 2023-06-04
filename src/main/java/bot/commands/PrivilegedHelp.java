package bot.commands;

import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;

@CommandPermission(permissions = Permission.MANAGE_SERVER)
public class PrivilegedHelp implements CommandExecutor {

    @Override
    public void run(Message message) {
        
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();
        Role booster = guild.getBoostRole();

        MessageCreateBuilder send = new MessageCreateBuilder();
        MessageEmbed embed = new EmbedBuilder()
                .setColor(booster == null ? new Color(255, 255, 255) : booster.getColor())
                .setThumbnail(guild.getIconUrl())
                .setTitle("✨ Privileged Help")
                .addField("⚙️ Tags Usadas", "`<>` = `Obrigatório`\n`[]` = `Opcional`", false)
                .addField("💻 Commands", """
                `.avatar <@user> [--server]`
                *Todos.*
                
                `.avatar-bot <link or file>`
                *Anjo, Myuu (both), Leo13.*
                
                `/.banner <@user>`
                *Manage Server.*
                
                `.bigo`
                *Negão ou quem tem Manage Messages.*
                
                `/stream <name> <link>`
                *Manage Server.*
                
                `.clear <amount>`
                *Manage Messages.*
                
                `/.disconnect` ou `.dd`
                *Todos.*
                
                `/disconnectall`
                *Manage Roles.*
                
                `.format <format> <input>`
                *Todos.*
                
                `.ip <ip>`
                *Todos.*
                
                `.linff [swearing]`
                *Ter cargo Salada.*
                
                `.nerd [@user]`
                *Ter cargo Salada ou Alfea.*
                
                `/.ping`
                *Manage Server.*
                
                `.p-help`
                *Manage Server.*
                
                ~~`.puta [@user]`~~
                ~~Manage Messages + alguns chats~~
                
                `.randomize (In dev stages)`
                *Todos.*
                
                `r!roles`
                *Admin.*
                
                `r!take`
                *Manage Roles, fora do chat registro.*
                
                `.among <@user>`
                *Ajudantes Superior+, no chat #ajudantes-staff.*
                
                `.roleinfo <role>`
                *Todos.*
                
                `.say <text or image>`
                *Manage Server.*
                
                `.serverinfo`
                *Todos.*
                
                `/shutdown`
                *Manage Server.*
                
                `.uptime`
                *Manage Server.*
                
                `.userinfo <@user>`
                *Todos.*
                
                `/moveall`
                *Manage Server.*
                """, false)
                .setFooter(guild.getName(), guild.getIconUrl())
                .build();

        send.setEmbeds(embed);
        send.setContent("<@" + member.getId() + ">");

        channel.sendMessage(send.build()).queue();
    }
}