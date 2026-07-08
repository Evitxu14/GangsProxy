package com.zenith.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.zenith.Globals.CONFIG;
import static com.zenith.Globals.PLAYER_LISTS;
import static com.zenith.command.api.CommandOutputHelper.playerListToString;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;
import static com.zenith.discord.DiscordBot.escape;
import com.zenith.Lang;

public class StalkCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name(Lang.t("vigilar", Lang.t("vigilar", "stalk")))
            .category(CommandCategory.MODULE)
            .description("""
            Sends alerts when players join or leave
            """)
            .usageLines(
                "on/off",
                "list",
                "add/del <player>"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("stalk")
            .then(argument("toggle", toggle()).executes(c -> {
                CONFIG.client.extra.stalk.enabled = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title(Lang.t("Vigilar ", "Stalk ") + toggleStrCaps(CONFIG.client.extra.stalk.enabled));
                return OK;
            }))
            .then(literal("list").executes(c -> {
                c.getSource().getEmbed()
                    .title(Lang.t("Lista de Vigilancia", "Stalk List"));
            }))
            .then(literal("add").then(argument("player", string()).executes(c -> {
                final String player = StringArgumentType.getString(c, "player");
                PLAYER_LISTS.getStalkList().add(player).ifPresentOrElse(e ->
                    c.getSource().getEmbed()
                            .title(Lang.t("Jugador añadido: ", "Added player: ") + escape(e.getUsername()) + Lang.t(" A la Lista de Vigilancia", " To Stalk List")),
                        () -> c.getSource().getEmbed()
                            .title(Lang.t("Error al añadir jugador: ", "Failed to add player: ") + escape(player) + Lang.t(". No se pudo buscar el perfil.", " to stalk list. Unable to lookup profile.")));
                return OK;
            })))
            .then(literal("del").then(argument("player", string()).executes(c -> {
                final String player = StringArgumentType.getString(c, "player");
                PLAYER_LISTS.getStalkList().remove(player);
                c.getSource().getEmbed()
                    .title(Lang.t("Jugador eliminado: ", "Removed player: ") + escape(player) + Lang.t(" De la Lista de Vigilancia", " From Stalk List"));
                return OK;
            })));
    }

    @Override
    public void defaultEmbed(final Embed builder) {
        builder
            .addField(Lang.t("Vigilar", "Stalk"), toggleStr(CONFIG.client.extra.stalk.enabled), false)
            .description(Lang.t("**Lista de Vigilancia**\n", "**Stalk List**\n") + playerListToString(PLAYER_LISTS.getStalkList()))
            .primaryColor();
    }
}
