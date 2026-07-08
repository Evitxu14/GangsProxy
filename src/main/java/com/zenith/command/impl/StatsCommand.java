package com.zenith.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.Proxy;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.feature.api.vcapi.VcApi;
import com.zenith.feature.api.vcapi.model.StatsResponse;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.time.Duration;
import java.util.Optional;

import static com.zenith.command.brigadier.CustomStringArgumentType.wordWithChars;
import static com.zenith.util.math.MathHelper.formatDurationLong;
import com.zenith.Lang;

public class StatsCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name(Lang.t("estadisticas", Lang.t("estadisticas", "stats")))
            .category(CommandCategory.INFO)
            .description("Gets the 2b2t stats of a player using https://api.2b2t.vc")
            .usageLines(
                "<playerName>"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("stats")
            .then(argument("playerName", wordWithChars()).executes(c -> {
                final String playerName = c.getArgument("playerName", String.class);
                final Optional<StatsResponse> statsResponse = VcApi.INSTANCE.getStats(playerName);
                if (statsResponse.isEmpty()) {
                    c.getSource().getEmbed()
                        .title(playerName + " not found");
                    return ERROR;
                }
                final StatsResponse playerStats = statsResponse.get();
                c.getSource().getEmbed()
                    .title(Lang.t("Estadísticas del Jugador", "Player Stats"))
                    .primaryColor()
                    .addField("Player", playerName, true)
                    .addField("\u200B", "\u200B", true)
                    .addField("\u200B", "\u200B", true)
                    .addField(Lang.t("Entradas", "Joins"), playerStats.joinCount(), true)
                    .addField(Lang.t("Salidas", "Leaves"), playerStats.leaveCount(), true)
                    .addField("\u200B", "\u200B", true)
                    .addField(Lang.t("Primera Vez", "First Seen"), TimeFormat.DATE_TIME_SHORT.format(playerStats.firstSeen().toInstant()), true)
                    .addField(Lang.t("Última Vez", "Last Seen"), TimeFormat.DATE_TIME_SHORT.format(playerStats.lastSeen().toInstant()), true)
                    .addField("\u200B", "\u200B", true)
                    .addField(Lang.t("Tiempo de Juego", "Playtime"), formatDurationLong(Duration.ofSeconds(playerStats.playtimeSeconds())), true)
                    .addField(Lang.t("Tiempo de Juego (Últimos 30 Días)", "Playtime (Last 30 Days)"), formatDurationLong(Duration.ofSeconds(playerStats.playtimeSecondsMonth())), true)
                    .addField("\u200B", "\u200B", true)
                    .addField(Lang.t("Muertes", "Deaths"), playerStats.deathCount(), true)
                    .addField(Lang.t("Eliminaciones", "Kills"), playerStats.killCount(), true)
                    .addField("\u200B", "\u200B", true)
                    .addField(Lang.t("Chats", "Chats"), playerStats.chatsCount(), true)
                    .addField(Lang.t("Cola Prioritaria", "Priority Queue"), playerStats.prio() ? Lang.t("Sí (probablemente)", "Yes (probably)") : Lang.t("No (probablemente no)", "No (probably not)"), true)
                    .addField("\u200B", "\u200B", true)
                    .thumbnail(Proxy.getInstance().getPlayerHeadURL(playerName).toString());
                return OK;
            }));
    }
}
