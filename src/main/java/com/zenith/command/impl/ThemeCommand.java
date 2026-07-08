package com.zenith.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;
import com.zenith.util.config.ConfigColor;

import java.util.Arrays;

import com.zenith.Lang;
import static com.zenith.Globals.CONFIG;
import static com.zenith.command.brigadier.CustomStringArgumentType.getString;

public class ThemeCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("theme")
            .category(CommandCategory.MANAGE)
            .description("""
            Changes the color theme of alerts and messages.
            
            Use `theme list` to see available colors.
            
            Where Colors Are Used:
              * Primary: Most embeds and command responses if not an error.
              * Success: General "this worked" responses, server join, and friends
              * Error: Error responses, server leave, and enemies
              * In Queue: The proxy is in queue, reconnecting, or is in a transitional state
            """)
            .usageLines(
                "list",
                "primary <color>",
                "success <color>",
                "error <color>",
                "inQueue <color>"
            )
            .aliases("color")
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("theme")
            .then(literal("list").executes(c -> {
                var allColors = Arrays.stream(ConfigColor.values())
                    .map(color -> color.name().toLowerCase())
                    .toList();
                c.getSource().getEmbed()
                    .title(Lang.t("Colores Disponibles", "Available Colors"))
                    .description(String.join("\n", allColors));
            }))
            .then(literal("primary").then(argument("color", enumStrings(ConfigColor.values())).executes(c -> {
                var colorStr = getString(c, "color").toUpperCase();
                try {
                    CONFIG.theme.primary = ConfigColor.valueOf(colorStr);
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Color Primario Establecido!", "Primary Color Set!"));
                    return OK;
                } catch (final Throwable e) {
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Color Inválido!", "Invalid Color!"))
                        .addField(Lang.t("Ayuda", "Help"), Lang.t("Usa `theme list` para ver los colores disponibles", "Use `theme list` to see available colors"), false);
                    return ERROR;
                }
            })))
            .then(literal("success").then(argument("color", enumStrings(ConfigColor.values())).executes(c -> {
                var colorStr = getString(c, "color").toUpperCase();
                try {
                    CONFIG.theme.success = ConfigColor.valueOf(colorStr);
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Color de Éxito Establecido!", "Success Color Set!"));
                    return OK;
                } catch (final Throwable e) {
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Color Inválido!", "Invalid Color!"))
                        .addField(Lang.t("Ayuda", "Help"), Lang.t("Usa `theme list` para ver los colores disponibles", "Use `theme list` to see available colors"), false);
                    return ERROR;
                }
            })))
            .then(literal("error").then(argument("color", enumStrings(ConfigColor.values())).executes(c -> {
                var colorStr = getString(c, "color").toUpperCase();
                try {
                    CONFIG.theme.error = ConfigColor.valueOf(colorStr);
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Color de Error Establecido!", "Error Color Set!"));
                    return OK;
                } catch (final Throwable e) {
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Color Inválido!", "Invalid Color!"))
                        .addField(Lang.t("Ayuda", "Help"), Lang.t("Usa `theme list` para ver los colores disponibles", "Use `theme list` to see available colors"), false);
                    return ERROR;
                }
            })))
            .then(literal("inQueue").then(argument("color", enumStrings(ConfigColor.values())).executes(c -> {
                var colorStr = getString(c, "color").toUpperCase();
                try {
                    CONFIG.theme.inQueue = ConfigColor.valueOf(colorStr);
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Color en Cola Establecido!", "In Queue Color Set!"));
                    return OK;
                } catch (final Throwable e) {
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Color Inválido!", "Invalid Color!"))
                        .addField(Lang.t("Ayuda", "Help"), Lang.t("Usa `theme list` para ver los colores disponibles", "Use `theme list` to see available colors"), false);
                    return ERROR;
                }
            })));
    }

    @Override
    public void defaultEmbed(Embed embed) {
        embed
            .primaryColor()
            .addField(Lang.t("Primario", "Primary"), CONFIG.theme.primary.name().toLowerCase(), false)
            .addField(Lang.t("Éxito", "Success"), CONFIG.theme.success.name().toLowerCase(), false)
            .addField(Lang.t("Error", "Error"), CONFIG.theme.error.name().toLowerCase(), false)
            .addField(Lang.t("En Cola", "In Queue"), CONFIG.theme.inQueue.name().toLowerCase(), false);
    }
}
