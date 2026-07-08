package com.zenith.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.zenith.Globals.CONFIG;
import static com.zenith.Globals.DATABASE;
import static com.zenith.command.brigadier.CustomStringArgumentType.getString;
import static com.zenith.command.brigadier.CustomStringArgumentType.wordWithChars;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;
import com.zenith.Lang;

public class DatabaseCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name(Lang.t("baseDatos", Lang.t("baseDatos", "database")))
            .category(CommandCategory.MANAGE)
            .description("""
            Configures the database module used by https://api.2b2t.vc

            This is disabled by default - no gangsproxy users contribute or collect data
            """)
            .usageLines(
                "on/off",
                "host <host>",
                "port <port>",
                "username <username>",
                "password <password>",
                "redis address <address>",
                "redis username <username>",
                "redis password <password>",
                "queueWait on/off",
                "queueLength on/off",
                "publicChat on/off",
                "joinLeave on/off",
                "deathMessages on/off",
                "restarts on/off",
                "playerCount on/off",
                "tablist on/off",
                "playtime on/off",
                "time on/off"
            )
            .aliases("db")
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("database")
            .then(argument("toggle", toggle()).executes(c -> {
                CONFIG.database.enabled = getToggle(c, "toggle");
                if (CONFIG.database.enabled) DATABASE.start();
                else DATABASE.stop();
                c.getSource().getEmbed()
                    .title(Lang.t("Bases de Datos ", "Databases ") + toggleStrCaps(CONFIG.database.enabled));
                return OK;
            }))
            .then(literal("host").then(argument("hostArg", wordWithChars()).executes(c -> {
                CONFIG.database.host = getString(c , "hostArg");
                if (CONFIG.database.enabled) {
                    DATABASE.stop();
                    DATABASE.start();
                }
                c.getSource().getEmbed()
                    .title(Lang.t("Host Establecido", "Host Set"));
            })))
            .then(literal("port").then(argument("portArg", integer(1, 65535)).executes(c -> {
                CONFIG.database.port = getInteger(c, "portArg");
                if (CONFIG.database.enabled) {
                    DATABASE.stop();
                    DATABASE.start();
                }
                c.getSource().getEmbed()
                    .title(Lang.t("Puerto Establecido", "Port Set"));
            })))
            .then(literal("username").then(argument("usernameArg", wordWithChars()).executes(c -> {
                CONFIG.database.username = getString(c, "usernameArg");
                if (CONFIG.database.enabled) {
                    DATABASE.stop();
                    DATABASE.start();
                }
                c.getSource().getEmbed()
                    .title(Lang.t("Nombre de Usuario Establecido", "Username Set"));
            })))
            .then(literal("password").then(argument("passwordArg", wordWithChars()).executes(c -> {
                CONFIG.database.password = getString(c, "passwordArg");
                if (CONFIG.database.enabled) {
                    DATABASE.stop();
                    DATABASE.start();
                }
                c.getSource().setSensitiveInput(true);
                c.getSource().getEmbed()
                    .title(Lang.t("Contraseña Establecida", "Password Set"));
            })))
            .then(literal("redis")
                .then(literal("address").then(argument("redisAddress", wordWithChars()).executes(c -> {
                    CONFIG.database.lock.redisAddress = getString(c, "redisAddress");
                    if (CONFIG.database.enabled) {
                        DATABASE.stop();
                        DATABASE.start();
                    }
                    c.getSource().getEmbed()
                        .title(Lang.t("Dirección Redis Establecida", "Redis Address Set"));
                })))
                .then(literal("username").then(argument("redisUsername", wordWithChars()).executes(c -> {
                    CONFIG.database.lock.redisUsername = getString(c, "redisUsername");
                    if (CONFIG.database.enabled) {
                        DATABASE.stop();
                        DATABASE.start();
                    }
                    c.getSource().getEmbed()
                        .title(Lang.t("Nombre de Usuario Redis Establecido", "Redis Username Set"));
                })))
                .then(literal("password").then(argument("redisPassword", wordWithChars()).executes(c -> {
                    CONFIG.database.lock.redisPassword = getString(c, "redisPassword");
                    if (CONFIG.database.enabled) {
                        DATABASE.stop();
                        DATABASE.start();
                    }
                    c.getSource().setSensitiveInput(true);
                    c.getSource().getEmbed()
                        .title(Lang.t("Contraseña Redis Establecida", "Redis Password Set"));
                }))))
            .then(literal("queueWait")
                      .then(argument("toggle", toggle()).executes(c -> {
                          CONFIG.database.queueWaitEnabled = getToggle(c, "toggle");
                          if (CONFIG.database.queueWaitEnabled) DATABASE.startQueueWaitDatabase();
                          else DATABASE.stopQueueWaitDatabase();
                          c.getSource().getEmbed()
                              .title(Lang.t("Base de Datos de Espera de Cola ", "Queue Wait Database ") + toggleStrCaps(CONFIG.database.queueWaitEnabled));
                          return OK;
                      })))
            .then(literal("queueLength")
                      .then(argument("toggle", toggle()).executes(c -> {
                          CONFIG.database.queueLengthEnabled = getToggle(c, "toggle");
                          if (CONFIG.database.queueLengthEnabled) DATABASE.startQueueLengthDatabase();
                          else DATABASE.stopQueueLengthDatabase();
                          c.getSource().getEmbed()
                              .title(Lang.t("Base de Datos de Longitud de Cola ", "Queue Length Database ") + toggleStrCaps(CONFIG.database.queueLengthEnabled));
                          return OK;
                      })))
            .then(literal("publicChat")
                      .then(argument("toggle", toggle()).executes(c -> {
                          CONFIG.database.chatsEnabled = getToggle(c, "toggle");
                          if (CONFIG.database.chatsEnabled) DATABASE.startChatsDatabase();
                          else DATABASE.stopChatsDatabase();
                          c.getSource().getEmbed()
                              .title(Lang.t("Base de Datos de Chat Público ", "Public Chat Database ") + toggleStrCaps(CONFIG.database.chatsEnabled));
                          return OK;
                      })))
            .then(literal("joinLeave")
                      .then(argument("toggle", toggle()).executes(c -> {
                          CONFIG.database.connectionsEnabled = getToggle(c, "toggle");
                          if (CONFIG.database.connectionsEnabled) DATABASE.startConnectionsDatabase();
                          else DATABASE.stopConnectionsDatabase();
                          c.getSource().getEmbed()
                              .title(Lang.t("Base de Datos de Conexiones ", "Connections Database ") + toggleStrCaps(CONFIG.database.connectionsEnabled));
                          return OK;
                      })))
            .then(literal("deathMessages")
                      .then(argument("toggle", toggle()).executes(c -> {
                          CONFIG.database.deathsEnabled = getToggle(c, "toggle");
                          if (CONFIG.database.deathsEnabled) DATABASE.startDeathsDatabase();
                          else DATABASE.stopDeathsDatabase();
                          c.getSource().getEmbed()
                              .title(Lang.t("Base de Datos de Mensajes de Muerte ", "Death Messages Database ") + toggleStrCaps(CONFIG.database.deathsEnabled));
                          return OK;
                      })))
            .then(literal("restarts")
                      .then(argument("toggle", toggle()).executes(c -> {
                          CONFIG.database.restartsEnabled = getToggle(c, "toggle");
                          if (CONFIG.database.restartsEnabled) DATABASE.startRestartsDatabase();
                          else DATABASE.stopRestartsDatabase();
                          c.getSource().getEmbed()
                              .title(Lang.t("Base de Datos de Reinicios ", "Restarts Database ") + toggleStrCaps(CONFIG.database.restartsEnabled));
                          return OK;
                      })))
            .then(literal("playerCount")
                      .then(argument("toggle", toggle()).executes(c -> {
                          CONFIG.database.playerCountEnabled = getToggle(c, "toggle");
                          if (CONFIG.database.playerCountEnabled) DATABASE.startPlayerCountDatabase();
                          else DATABASE.stopPlayerCountDatabase();
                          c.getSource().getEmbed()
                              .title(Lang.t("Base de Datos de Conteo de Jugadores ", "Player Count Database ") + toggleStrCaps(CONFIG.database.playerCountEnabled));
                          return OK;
                      })))
            .then(literal("tablist")
                      .then(argument("toggle", toggle()).executes(c -> {
                          CONFIG.database.tablistEnabled = getToggle(c, "toggle");
                          if (CONFIG.database.tablistEnabled) DATABASE.startTablistDatabase();
                          else DATABASE.stopTablistDatabase();
                          c.getSource().getEmbed()
                              .title(Lang.t("Base de Datos de Tablist ", "Tablist Database ") + toggleStrCaps(CONFIG.database.tablistEnabled));
                          return OK;
                      })))
            .then(literal("playtime")
                      .then(argument("toggle", toggle()).executes(c -> {
                          CONFIG.database.playtimeEnabled = getToggle(c, "toggle");
                          if (CONFIG.database.playtimeEnabled) DATABASE.startPlaytimeDatabase();
                          else DATABASE.stopPlaytimeDatabase();
                          c.getSource().getEmbed()
                              .title(Lang.t("Base de Datos de Tiempo de Juego ", "Playtime Database ") + toggleStrCaps(CONFIG.database.playtimeEnabled));
                          return OK;
                      })))
            .then(literal("time")
                      .then(argument("toggle", toggle()).executes(c -> {
                            CONFIG.database.timeEnabled = getToggle(c, "toggle");
                            if (CONFIG.database.timeEnabled) DATABASE.startTimeDatabase();
                            else DATABASE.stopTimeDatabase();
                            c.getSource().getEmbed()
                                .title(Lang.t("Base de Datos de Tiempo ", "Time Database ") + toggleStrCaps(CONFIG.database.timeEnabled));
                            return OK;
                      })));
    }

    @Override
    public void defaultEmbed(final Embed builder) {
        builder
            .addField(Lang.t("Espera de Cola", "Queue Wait"), toggleStr(CONFIG.database.queueWaitEnabled), false)
            .addField(Lang.t("Longitud de Cola", "Queue Length"), toggleStr(CONFIG.database.queueLengthEnabled), false)
            .addField(Lang.t("Chat Público", "Public Chat"), toggleStr(CONFIG.database.chatsEnabled), false)
            .addField(Lang.t("Entrada/Salida", "Join/Leave"), toggleStr(CONFIG.database.connectionsEnabled), false)
            .addField(Lang.t("Mensajes de Muerte", "Death Messages"), toggleStr(CONFIG.database.deathsEnabled), false)
            .addField(Lang.t("Reinicios", "Restarts"), toggleStr(CONFIG.database.restartsEnabled), false)
            .addField(Lang.t("Conteo de Jugadores", "Player Count"), toggleStr(CONFIG.database.playerCountEnabled), false)
            .addField(Lang.t("Tablist", "Tablist"), toggleStr(CONFIG.database.tablistEnabled), false)
            .addField(Lang.t("Tiempo de Juego", "Playtime"), toggleStr(CONFIG.database.playtimeEnabled), false)
            .addField(Lang.t("Tiempo", "Time"), toggleStr(CONFIG.database.timeEnabled), false)
            .primaryColor();
    }
}
