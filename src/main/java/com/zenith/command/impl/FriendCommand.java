package com.zenith.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static com.zenith.Globals.PLAYER_LISTS;
import static com.zenith.command.api.CommandOutputHelper.playerListToString;
import static com.zenith.discord.DiscordBot.escape;
import com.zenith.Lang;

public class FriendCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("friend")
            .category(CommandCategory.MANAGE)
            .description("""
            Manage the friend list.
            Friends change behavior for various modules like VisualRange, KillAura, and AutoDisconnect
            """)
            .usageLines(
                "add/del <player>",
                "addAll <player 1>,<player 2>...",
                "list",
                "clear"
            )
            .aliases("f")
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("friend")
            .then(literal("add").then(argument("player", string()).executes(c -> {
                final String player = StringArgumentType.getString(c, "player");
                PLAYER_LISTS.getFriendsList().add(player)
                    .ifPresentOrElse(e ->
                            c.getSource().getEmbed()
                                .title(Lang.t("Amigo anadido", "Friend added")),
                        () -> c.getSource().getEmbed()
                            .title(Lang.t("Error al anadir usuario a amigos: ", "Failed to add user: ") + escape(player) + Lang.t(". No se puede buscar el perfil.", ". Unable to lookup profile.")));
            })))
            .then(literal("addAll").then(argument("playerList", greedyString()).executes(c -> {
                String playerList = getString(c, "playerList");
                String[] split = playerList.split(",");
                if (split.length == 0) {
                    c.getSource().getEmbed()
                        .title(Lang.t("Entrada Invalida", "Invalid Input"))
                        .description(Lang.t("Cada nombre de jugador debe estar delimitado por `,`", "Each player name must be delimited by `,`"));
                    return ERROR;
                }
                List<String> addErrors = new ArrayList<>();
                for (var player : split) {
                    if (PLAYER_LISTS.getFriendsList().add(player).isEmpty()) {
                        addErrors.add(player);
                    }
                }
                c.getSource().getEmbed()
                    .title(Lang.t("Jugadores Anadidos", "Added Players"))
                    .addField(Lang.t("Contador de Jugadores Anadidos", "Added Player Count"), split.length - addErrors.size());
                if (!addErrors.isEmpty()) {
                    c.getSource().getEmbed()
                        .description(Lang.t("Error al anadir ", "Failed adding ") + addErrors.size() + Lang.t(" jugadores: ", " players: ") + String.join(", ", addErrors));
                }
                return OK;
            })))
            .then(literal("del").then(argument("player", string()).executes(c -> {
                final String player = StringArgumentType.getString(c, "player");
                PLAYER_LISTS.getFriendsList().remove(player);
                c.getSource().getEmbed()
                    .title(Lang.t("Amigo eliminado", "Friend deleted"));
            })))
            .then(literal("list").executes(c -> {
                c.getSource().getEmbed()
                    .title(Lang.t("Lista de Amigos", "Friend list"));
            }))
            .then(literal("clear").executes(c -> {
                PLAYER_LISTS.getFriendsList().clear();
                c.getSource().getEmbed()
                    .title(Lang.t("Lista de amigos borrada!", "Friend list cleared!"));
            }));
    }

    @Override
    public void defaultEmbed(final Embed builder) {
        builder
            .description(Lang.t("**Lista de Amigos**\n", "**Friend List**\n") + playerListToString(PLAYER_LISTS.getFriendsList()))
            .primaryColor();
    }
}
