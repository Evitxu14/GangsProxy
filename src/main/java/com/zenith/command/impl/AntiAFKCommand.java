package com.zenith.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;
import com.zenith.module.impl.AntiAFK;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.zenith.Globals.CONFIG;
import static com.zenith.Globals.MODULE;
import static com.zenith.command.brigadier.TimeArgument.time;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;
import com.zenith.Lang;

public class AntiAFKCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name(Lang.t("antiAfk", Lang.t("antiAfk", "antiAFK")))
            .category(CommandCategory.MODULE)
            .description("""
            Configures the AntiAFK module.

            To avoid being kicked on 2b2t the only required action is swing OR walk.

            The walk action will move the player roughly in a square shape. To avoid falling down any ledges, enable safeWalk

            For delay settings, 1 tick = 50ms
            """)
            .usageLines(
                "on/off",
                "rotate on/off",
                "rotate delay <ticks>",
                "swing on/off",
                "swing delay <ticks>",
                "walk on/off",
                "walk delay <ticks>",
                "safeWalk on/off",
                "walkDistance <ticks>",
                "jump on/off",
                "jump onlyInWater on/off",
                "jump delay <ticks>",
                "sneak on/off",
                "sneak delay <ticks>"
            )
            .aliases(
                "afk"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("antiAFK")
            .then(argument("toggle", toggle()).executes(c -> {
                CONFIG.client.extra.antiafk.enabled = getToggle(c, "toggle");
                MODULE.get(AntiAFK.class).syncEnabledFromConfig();
                c.getSource().getEmbed()
                    .title(Lang.t("AntiAFK " + toggleStrCaps(CONFIG.client.extra.antiafk.enabled), "AntiAFK " + toggleStrCaps(CONFIG.client.extra.antiafk.enabled)));
                return OK;
            }))
            .then(literal("rotate")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.rotate = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Rotar " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.rotate), "Rotate " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.rotate)));
                    return OK;
                }))
                .then(literal("delay").then(argument("delay", time(0, 50000)).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.rotateDelayTicks = getInteger(c, "delay");
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Retardo de rotación establecido!", "Rotate Delay Set!"));
                    return OK;
                }))))
            .then(literal("swing")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.swingHand = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Balanceo " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.swingHand), "Swing " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.swingHand)));
                    return OK;
                }))
                .then(literal("delay").then(argument("delay", time(0, 50000)).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.swingDelayTicks = getInteger(c, "delay");
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Retardo de balanceo establecido!", "Swing Delay Set!"));
                    return OK;
                }))))
            .then(literal("walk")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.walk = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Caminar " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.walk), "Walk " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.walk)));
                    return OK;
                }))
                .then(literal("delay").then(argument("delay", time(0, 50000)).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.walkDelayTicks = getInteger(c, "delay");
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Retardo de caminata establecido!", "Walk Delay Set!"));
                    return OK;
                }))))
            .then(literal("safeWalk")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.safeWalk = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Caminata segura " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.safeWalk), "SafeWalk " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.safeWalk)));
                    return OK;
                })))
            .then(literal("walkDistance")
                .then(argument("walkdist", integer(1)).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.walkDistance = getInteger(c, "walkdist");
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Distancia de caminata establecida!", "Walk Distance Set!"));
                    return OK;
                })))
            .then(literal("jump")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.jump = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Saltar " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.jump), "Jump " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.jump)));
                    return OK;
                }))
                .then(literal("onlyInWater").then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.jumpOnlyInWater = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Saltar solo en agua " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.jumpOnlyInWater), "Jump Only In Water " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.jumpOnlyInWater)));
                    return OK;
                })))
                .then(literal("delay").then(argument("delay", time(0, 50000)).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.jumpDelayTicks = getInteger(c, "delay");
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Retardo de salto establecido!", "Jump Delay Set!"));
                    return OK;
                }))))
            .then(literal("sneak")
                .then(argument("toggle", toggle()).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.sneak = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title(Lang.t("Agacharse " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.sneak), "Sneak " + toggleStrCaps(CONFIG.client.extra.antiafk.actions.sneak)));
                    return OK;
                }))
                .then(literal("delay").then(argument("delay", time(0, 50000)).executes(c -> {
                    CONFIG.client.extra.antiafk.actions.sneakDelayTicks = getInteger(c, "delay");
                    c.getSource().getEmbed()
                        .title(Lang.t("¡Retardo de agacharse establecido!", "Sneak Delay Set!"));
                }))));
    }

    @Override
    public void defaultEmbed(final Embed embedBuilder) {
        embedBuilder
            .addField(Lang.t("AntiAFK", "AntiAFK"), toggleStr(CONFIG.client.extra.antiafk.enabled))
            .addField(Lang.t("Rotar", "Rotate"), toggleStr(CONFIG.client.extra.antiafk.actions.rotate)
                + " - " + Lang.t("Retardo: ", "Delay: ") + CONFIG.client.extra.antiafk.actions.rotateDelayTicks)
            .addField(Lang.t("Balanceo", "Swing"), toggleStr(CONFIG.client.extra.antiafk.actions.swingHand)
                + " - " + Lang.t("Retardo: ", "Delay: ") + CONFIG.client.extra.antiafk.actions.swingDelayTicks)
            .addField(Lang.t("Caminar", "Walk"), toggleStr(CONFIG.client.extra.antiafk.actions.walk)
                + " - " + Lang.t("Retardo: ", "Delay: ") + CONFIG.client.extra.antiafk.actions.walkDelayTicks)
            .addField(Lang.t("Caminata segura", "Safe Walk"), toggleStr(CONFIG.client.extra.antiafk.actions.safeWalk))
            .addField(Lang.t("Distancia de caminata", "Walk Distance"), CONFIG.client.extra.antiafk.actions.walkDistance)
            .addField(Lang.t("Saltar", "Jump"), toggleStr(CONFIG.client.extra.antiafk.actions.jump)
                + " - " + Lang.t("Solo en agua: ", "Only In Water: ") + toggleStr(CONFIG.client.extra.antiafk.actions.jumpOnlyInWater)
                + " - " + Lang.t("Retardo: ", "Delay: ") + CONFIG.client.extra.antiafk.actions.jumpDelayTicks)
            .addField(Lang.t("Agacharse", "Sneak"), toggleStr(CONFIG.client.extra.antiafk.actions.sneak)
                + " - " + Lang.t("Retardo: ", "Delay: ") + CONFIG.client.extra.antiafk.actions.sneakDelayTicks)
            .primaryColor();
    }
}
