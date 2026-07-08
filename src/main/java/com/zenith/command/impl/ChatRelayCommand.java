package com.zenith.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.*;
import com.zenith.discord.Embed;
import com.zenith.util.MentionUtil;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.zenith.Globals.*;
import static com.zenith.command.brigadier.CustomStringArgumentType.getString;
import static com.zenith.command.brigadier.CustomStringArgumentType.wordWithChars;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;
import static java.util.Arrays.asList;
import com.zenith.Lang;

public class ChatRelayCommand extends Command {
    private static final Pattern CHANNEL_ID_PATTERN = Pattern.compile("<#\\d+>");

    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name(Lang.t("relayChat", Lang.t("relayChat", "chatRelay")))
            .category(CommandCategory.MANAGE)
            .description("""
            Configures the Discord ChatRelay feature.

            The ChatRelay is a live feed of chat messages and/or connection messages from the server to a Discord channel.

            Mentions can be configured when a whisper is received or your name is seen in chat.

            Messages typed in the ChatRelay discord channel will be sent as chat messages in-game
            Discord message replies will be sent as whispers in-game.

            Ignore regex will filter out messages, see here for help writing regex: https://regex101.com/ (Java flavor)
            """)
            .usageLines(
                "on/off",
                "channel <channelId>",
                "connectionMessages on/off",
                "whispers on/off",
                "publicChat on/off",
                "deathMessages on/off",
                "serverMessages on/off",
                "whisperMentions on/off",
                "nameMentions on/off",
                "mentionsWhileConnected on/off",
                "ignoreQueue on/off",
                "sendMessages on/off",
                "ignoreRegex add <regex>",
                "ignoreRegex del <index>",
                "ignoreRegex list",
                "ignoreRegex clear"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("chatRelay")
            .requires(c -> Command.validateCommandSource(c, asList(CommandSources.DISCORD, CommandSources.TERMINAL)))
            .then(argument("toggle", toggle()).executes(c -> {
                CONFIG.discord.chatRelay.enable = getToggle(c, "toggle");
                if (CONFIG.discord.chatRelay.enable && CONFIG.discord.chatRelay.channelId.isEmpty()) {
                    c.getSource().getEmbed()
                        .title(Lang.t("Error", "Error"))
                        .description(Lang.t("El canal de Chat Relay debe configurarse: `chatRelay channel <channelId>`", "Chat Relay channel must be set: `chatRelay channel <channelId>`"))
                        .errorColor();
                    CONFIG.discord.chatRelay.enable = false;
                    return OK;
                }
                EXECUTOR.execute(this::restartDiscordBot);
                c.getSource().getEmbed()
                    .title(Lang.t("Chat Relay ", "Chat Relay ") + toggleStrCaps(CONFIG.discord.chatRelay.enable));
                return OK;
            }))
            .then(literal("channel").requires(Command::validateAccountOwner).then(argument("channelId", wordWithChars()).executes(c -> {
                String channelId = getString(c, "channelId");
                if (CHANNEL_ID_PATTERN.matcher(channelId).matches())
                    channelId = channelId.substring(2, channelId.length() - 1);
                try {
                    Long.parseUnsignedLong(channelId);
                } catch (final Exception e) {
                    // invalid id
                    c.getSource().getEmbed()
                        .title(Lang.t("ID de Canal Inválido", "Invalid Channel ID"))
                        .description(Lang.t("El ID de canal proporcionado no es válido", "The channel ID provided is invalid"))
                        .errorColor();
                    return OK;
                }
                if (channelId.equals(CONFIG.discord.channelId)) {
                    c.getSource().getEmbed()
                        .title(Lang.t("ID de Canal Inválido", "Invalid Channel ID"))
                        .description(Lang.t("No se puede usar el mismo ID de canal para el relay y el canal principal", "Cannot use the same channel ID for both the relay and main channel"))
                        .errorColor();
                    return OK;
                }
                CONFIG.discord.chatRelay.channelId = channelId;
                c.getSource().getEmbed()
                    .title(Lang.t("¡Canal configurado!", "Channel set!"))
                    .primaryColor()
                    .description(Lang.t("El bot de Discord se reiniciará ahora si está habilitado", "Discord bot will now restart if enabled"));
                if (DISCORD.isRunning())
                    EXECUTOR.schedule(this::restartDiscordBot, 3, TimeUnit.SECONDS);
                return OK;
            })))
            .then(literal("connectionMessages")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.discord.chatRelay.connectionMessages = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Mensajes de Conexión ", "Connection Messages ") + toggleStrCaps(CONFIG.discord.chatRelay.connectionMessages));
                })))
            .then(literal("whispers")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.discord.chatRelay.whispers = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Susurros ", "Whispers ") + toggleStrCaps(CONFIG.discord.chatRelay.whispers));
                })))
            .then(literal("publicChat")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.discord.chatRelay.publicChats = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Chat Público ", "Public Chat ") + toggleStrCaps(CONFIG.discord.chatRelay.publicChats));
                })))
            .then(literal("deathMessages")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.discord.chatRelay.deathMessages = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Mensajes de Muerte ", "Death Messages ") + toggleStrCaps(CONFIG.discord.chatRelay.deathMessages));
                })))
            .then(literal("serverMessages")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.discord.chatRelay.serverMessages = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Mensajes del Servidor ", "Server Messages ") + toggleStrCaps(CONFIG.discord.chatRelay.serverMessages));
                })))
            .then(literal("whisperMentions")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.discord.chatRelay.mentionRoleOnWhisper = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Menciones de Susurro ", "Whisper Mentions ") + toggleStrCaps(CONFIG.discord.chatRelay.mentionRoleOnWhisper));
                })))
            .then(literal("nameMentions")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.discord.chatRelay.mentionRoleOnNameMention = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Menciones de Nombre ", "Name Mentions ") + toggleStrCaps(CONFIG.discord.chatRelay.mentionRoleOnNameMention));
                })))
            .then(literal("mentionsWhileConnected")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.discord.chatRelay.mentionWhileConnected = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Menciones Mientras Conectado ", "Mentions While Connected ") + toggleStrCaps(CONFIG.discord.chatRelay.mentionWhileConnected));
                })))
            .then(literal("ignoreQueue")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.discord.chatRelay.ignoreQueue = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Ignurar Cola ", "Ignore Queue ") + toggleStrCaps(CONFIG.discord.chatRelay.ignoreQueue));
                })))
            .then(literal("sendMessages")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.discord.chatRelay.sendMessages = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Enviar Mensajes ", "Send Messages ") + toggleStrCaps(CONFIG.discord.chatRelay.sendMessages));
                })))
            .then(literal("ignoreRegex")
                .then(literal("add").then(argument("regex", greedyString()).executes(c -> {
                    c.getSource().getData().put("noDefaultEmbed", true);
                    String regexInput = getString(c, "regex");
                    try {
                        Pattern.compile(regexInput);
                    } catch (Exception e) {
                        c.getSource().getEmbed()
                            .title(Lang.t("Regex Inválido", "Invalid Regex"))
                            .description(e.toString());
                        return ERROR;
                    }
                    CONFIG.discord.chatRelay.ignoreRegex.add(regexInput);
                    c.getSource().getEmbed()
                        .title(Lang.t("Regex Añadido", "Regex Added"));
                    return OK;
                })))
                .then(literal("del").then(argument("index", integer(0)).executes(c -> {
                    c.getSource().getData().put("noDefaultEmbed", true);
                    int index = getInteger(c, "index");
                    if (index < 0 || index >= CONFIG.discord.chatRelay.ignoreRegex.size()) {
                        c.getSource().getEmbed()
                            .title(Lang.t("Índice Inválido", "Invalid Index"))
                            .description(Lang.t("Índice fuera de límites", "Index out of bounds"));
                        return ERROR;
                    }
                    CONFIG.discord.chatRelay.ignoreRegex.remove(index);
                    c.getSource().getEmbed()
                        .title(Lang.t("Regex Eliminado", "Regex Removed"));
                    return OK;
                })))
                .then(literal("clear").executes(c -> {
                    c.getSource().getData().put("noDefaultEmbed", true);
                    CONFIG.discord.chatRelay.ignoreRegex.clear();
                    c.getSource().getEmbed()
                        .title(Lang.t("Ignurar Regex Borrado", "Ignore Regex Cleared"));
                }))
                .then(literal("list").executes(c -> {
                    c.getSource().getData().put("noDefaultEmbed", true);
                    var sb = new StringBuilder();
//                    sb.append("**Ignore Regex List**\n\n");
                    for (int i = 0; i < CONFIG.discord.chatRelay.ignoreRegex.size(); i++) {
                        var regex = CONFIG.discord.chatRelay.ignoreRegex.get(i);
                        sb.append("**")
                            .append(i)
                            .append("**: `")
                            .append(regex)
                            .append("`\n");
                    }
                    c.getSource().getEmbed()
                        .title(Lang.t("Ignurar Regex Lista", "Ignore Regex List"))
                        .description(sb.toString());
                })));
    }

    @Override
    public void defaultHandler(final CommandContext c) {
        if (!c.getData().containsKey("noDefaultEmbed")) {
            c.getEmbed()
                .addField(Lang.t("Chat Relay", "Chat Relay"), toggleStr(CONFIG.discord.chatRelay.enable))
                .addField(Lang.t("Canal", "Channel"), getChannelMention(CONFIG.discord.chatRelay.channelId))
                .addField(Lang.t("Mensajes de Conexión", "Connection Messages"), toggleStr(CONFIG.discord.chatRelay.connectionMessages))
                .addField(Lang.t("Chats Públicos", "Public Chats"), toggleStr(CONFIG.discord.chatRelay.publicChats))
                .addField(Lang.t("Susurros", "Whispers"), toggleStr(CONFIG.discord.chatRelay.whispers))
                .addField(Lang.t("Mensajes de Muerte", "Death Messages"), toggleStr(CONFIG.discord.chatRelay.deathMessages))
                .addField(Lang.t("Mensajes del Servidor", "Server Messages"), toggleStr(CONFIG.discord.chatRelay.serverMessages))
                .addField(Lang.t("Menciones de Susurro", "Whisper Mentions"), toggleStr(CONFIG.discord.chatRelay.mentionRoleOnWhisper))
                .addField(Lang.t("Menciones de Nombre", "Name Mentions"), toggleStr(CONFIG.discord.chatRelay.mentionRoleOnNameMention))
                .addField(Lang.t("Menciones Mientras Conectado", "Mentions While Connected"), toggleStr(CONFIG.discord.chatRelay.mentionWhileConnected))
                .addField(Lang.t("Ignurar Cola", "Ignore Queue"), toggleStr(CONFIG.discord.chatRelay.ignoreQueue))
                .addField(Lang.t("Enviar Mensajes", "Send Messages"), toggleStr(CONFIG.discord.chatRelay.sendMessages));
        }
        c.getEmbed()
            .primaryColor();
    }

    private String getChannelMention(final String channelId) {
        try {
            return MentionUtil.forChannel(channelId);
        } catch (final Exception e) {
            // these channels might be unset on purpose
            DEFAULT_LOG.debug("Invalid channel ID: {}", channelId, e);
            return "";
        }
    }

    private void restartDiscordBot() {
        DISCORD_LOG.info(Lang.t("Reiniciando bot de discord", "Restarting discord bot"));
        try {
            DISCORD.stop(false);
            if (CONFIG.discord.enable) {
                DISCORD.start();
                DISCORD.sendEmbedMessage(Embed.builder()
                    .title(Lang.t("Bot de Discord Reiniciado", "Discord Bot Restarted"))
                    .successColor());
            } else {
                DISCORD_LOG.info(Lang.t("El bot de Discord está deshabilitado, no iniciando", "Discord bot is disabled, not starting"));
            }
        } catch (final Exception e) {
            DISCORD_LOG.error(Lang.t("Error al reiniciar el bot de discord", "Failed to restart discord bot"), e);
        }
    }
}
