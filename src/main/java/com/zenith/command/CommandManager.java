package com.zenith.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.zenith.Lang;
import com.zenith.command.api.*;
import com.zenith.command.brigadier.CaseInsensitiveLiteralCommandNode;
import com.zenith.command.brigadier.McplBrigadierConverter;
import com.zenith.command.impl.*;
import com.zenith.network.server.ServerSession;
import lombok.Getter;
import lombok.Locked;
import org.geysermc.mcprotocollib.protocol.data.game.command.CommandNode;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.zenith.Globals.*;
import static java.util.Arrays.asList;

@Getter
public class CommandManager {
    private static final java.util.Map<String, String> SPANISH_ALIASES = java.util.Map.ofEntries(
        java.util.Map.entry("actionLimiter", "limitadorAccion"),
        java.util.Map.entry("activeHours", "horasActivas"),
        java.util.Map.entry("antiAFK", "antiAfk"),
        java.util.Map.entry("antiKick", "antiKick"),
        java.util.Map.entry("antiLeak", "antiFuga"),
        java.util.Map.entry("auth", "autenticar"),
        java.util.Map.entry("autoArmor", "autoArmadura"),
        java.util.Map.entry("autoDisconnect", "autoDesconectar"),
        java.util.Map.entry("autoDrop", "autoSoltar"),
        java.util.Map.entry("autoEat", "autoComer"),
        java.util.Map.entry("autoFish", "autoPescar"),
        java.util.Map.entry("autoMend", "autoReparar"),
        java.util.Map.entry("autoOmen", "autoAugurio"),
        java.util.Map.entry("autoReconnect", "autoReconectar"),
        java.util.Map.entry("autoReply", "autoResponder"),
        java.util.Map.entry("autoRespawn", "autoReaparecer"),
        java.util.Map.entry("autoTotem", "autoTotem"),
        java.util.Map.entry("autoUpdate", "autoActualizar"),
        java.util.Map.entry("channel", "canal"),
        java.util.Map.entry("chatHistory", "historialChat"),
        java.util.Map.entry("chatRelay", "retransmisionChat"),
        java.util.Map.entry("chatSchema", "esquemaChat"),
        java.util.Map.entry("click", "clic"),
        java.util.Map.entry("clientConnection", "conexionCliente"),
        java.util.Map.entry("commandConfig", "configComando"),
        java.util.Map.entry("connect", "conectar"),
        java.util.Map.entry("connectionTest", "pruebaConexion"),
        java.util.Map.entry("coordobf", "ofuscarCoord"),
        java.util.Map.entry("database", "baseDatos"),
        java.util.Map.entry("debug", "depurar"),
        java.util.Map.entry("disconnect", "desconectar"),
        java.util.Map.entry("discord", "discord"),
        java.util.Map.entry("discordNotifications", "notificacionesDiscord"),
        java.util.Map.entry("displayCoords", "mostrarCoord"),
        java.util.Map.entry("extraChat", "chatExtra"),
        java.util.Map.entry("friend", "amigo"),
        java.util.Map.entry("help", "ayuda"),
        java.util.Map.entry("ignore", "ignorar"),
        java.util.Map.entry("inventory", "inventario"),
        java.util.Map.entry("jvmArgs", "argumentosJvm"),
        java.util.Map.entry("kick", "expulsar"),
        java.util.Map.entry("killAura", "killAura"),
        java.util.Map.entry("license", "licencia"),
        java.util.Map.entry("map", "mapa"),
        java.util.Map.entry("modulePriority", "prioridadModulo"),
        java.util.Map.entry("multi", "multi"),
        java.util.Map.entry("packetLog", "registroPaquetes"),
        java.util.Map.entry("pathfinder", "buscadorRuta"),
        java.util.Map.entry("pearlLoader", "cargadorPerla"),
        java.util.Map.entry("playtime", "tiempoJuego"),
        java.util.Map.entry("plugins", "plugins"),
        java.util.Map.entry("prio", "prio"),
        java.util.Map.entry("queueStatus", "estadoCola"),
        java.util.Map.entry("queueWarning", "avisoCola"),
        java.util.Map.entry("rateLimiter", "limitadorTasa"),
        java.util.Map.entry("raycast", "raycast"),
        java.util.Map.entry("reconnect", "reconectar"),
        java.util.Map.entry("replay", "repeticion"),
        java.util.Map.entry("requeue", "recolocar"),
        java.util.Map.entry("respawn", "reaparecer"),
        java.util.Map.entry("rotate", "rotar"),
        java.util.Map.entry("seen", "visto"),
        java.util.Map.entry("sendMessage", "enviarMensaje"),
        java.util.Map.entry("server", "servidor"),
        java.util.Map.entry("serverConnection", "conexionServidor"),
        java.util.Map.entry("sessionTimeLimit", "limiteTiempoSesion"),
        java.util.Map.entry("shutdown", "apagar"),
        java.util.Map.entry("skin", "piel"),
        java.util.Map.entry("spammer", "spammer"),
        java.util.Map.entry("spawnPatrol", "patrullaAparicion"),
        java.util.Map.entry("spectator", "espectador"),
        java.util.Map.entry("spectatorEntity", "entidadEspectador"),
        java.util.Map.entry("entityToggle", "alternarEntidad"),
        java.util.Map.entry("playerCam", "camJugador"),
        java.util.Map.entry("swap", "intercambiar"),
        java.util.Map.entry("spook", "spook"),
        java.util.Map.entry("stalk", "acechar"),
        java.util.Map.entry("stats", "estadisticas"),
        java.util.Map.entry("status", "estado"),
        java.util.Map.entry("switch", "cambiar"),
        java.util.Map.entry("tablist", "listaPestanas"),
        java.util.Map.entry("tasks", "tareas"),
        java.util.Map.entry("terminal", "terminal"),
        java.util.Map.entry("theme", "tema"),
        java.util.Map.entry("tickRate", "tasaTicks"),
        java.util.Map.entry("transfer", "transferir"),
        java.util.Map.entry("unsupported", "noSoportado"),
        java.util.Map.entry("update", "actualizar"),
        java.util.Map.entry("via", "via"),
        java.util.Map.entry("visualRange", "rangoVisual"),
        java.util.Map.entry("waypoints", "puntosRuta"),
        java.util.Map.entry("whitelist", "listaBlanca")
    );

    private final List<Command> commandsList = Lists.newArrayList(
        new ActionLimiterCommand(),
        new ActiveHoursCommand(),
        new AntiAFKCommand(),
        new AntiKickCommand(),
        new AntiLeakCommand(),
        new AuthCommand(),
        new AutoArmorCommand(),
        new AutoDisconnectCommand(),
        new AutoDropCommand(),
        new AutoEatCommand(),
        new AutoFishCommand(),
        new AutoMendCommand(),
        new AutoOmenCommand(),
        new AutoReconnectCommand(),
        new AutoReplyCommand(),
        new AutoRespawnCommand(),
        new AutoTotemCommand(),
        new AutoUpdateCommand(),
        new ChatHistoryCommand(),
        new ChatRelayCommand(),
        new ChatSchemaCommand(),
        new ClickCommand(),
        new ClientConnectionCommand(),
        new CommandConfigCommand(),
        new ConnectCommand(),
        new ConnectionTestCommand(),
        new CoordinateObfuscationCommand(),
        new DatabaseCommand(),
        new DebugCommand(),
        new DisconnectCommand(),
        new DiscordManageCommand(),
        new DiscordNotificationsCommand(),
        new DisplayCoordsCommand(),
        new ExtraChatCommand(),
        new FriendCommand(),
        new HelpCommand(),
        new IgnoreCommand(),
        new InventoryCommand(),
        new JvmArgsCommand(),
        new KickCommand(),
        new KillAuraCommand(),
        new LicenseCommand(),
        new MapCommand(),
        new ModulePriorityCommand(),
        new MultiCommand(),
        new PacketLogCommand(),
        new PathfinderCommand(),
        new PearlLoader(),
        new PlaytimeCommand(),
        new PluginsCommand(),
        new PrioCommand(),
        new QueueStatusCommand(),
        new QueueWarningCommand(),
        new RateLimiterCommand(),
        new RaycastCommand(),
        new ReconnectCommand(),
        new ReleaseChannelCommand(),
        new ReplayCommand(),
        new RequeueCommand(),
        new RespawnCommand(),
        new RotateCommand(),
        new SeenCommand(),
        new SendMessageCommand(),
        new ServerCommand(),
        new ServerConnectionCommand(),
        new ServerSwitcherCommand(),
        new SessionTimeLimitCommand(),
        new ShutdownCommand(),
        new SkinCommand(),
        new SpammerCommand(),
        new SpawnPatrolCommand(),
        new SpectatorCommand(),
        new SpectatorEntityCommand(),
        new SpectatorEntityToggleCommand(),
        new SpectatorPlayerCamCommand(),
        new SpectatorSwapCommand(),
        new SpookCommand(),
        new StalkCommand(),
        new StatsCommand(),
        new StatusCommand(),
        new TablistCommand(),
        new TasksCommand(),
        new TerminalCommand(),
        new ThemeCommand(),
        new TickRateCommand(),
        new TransferCommand(),
        new UnsupportedCommand(),
        new UpdateCommand(),
        new ViaVersionCommand(),
        new VisualRangeCommand(),
        new WaypointsCommand(),
        new WhitelistCommand()
    );
    private final CommandDispatcher<CommandContext> dispatcher;
    private @NonNull CommandNode[] mcplCommandNodes = new CommandNode[0];
    private AtomicBoolean mcplCommandNodesStale = new AtomicBoolean(true);

    public CommandManager() {
        this.dispatcher = new CommandDispatcher<>();
        registerCommands();
        mcplCommandNodesStale.set(true);
    }

    public void registerCommands() {
       commandsList.forEach(this::registerCommand);
    }

    public void registerPluginCommand(Command command) {
        if (commandsList.contains(command)) {
            DEFAULT_LOG.warn("Duplicate plugin command being registered: {}", command.commandUsage().getName(), new RuntimeException());
            return;
        }
        registerCommand(command);
        commandsList.add(command);
        mcplCommandNodesStale.set(true);
    }

    public List<Command> getCommands() {
        return commandsList;
    }

    public List<Command> getCommands(final CommandCategory category) {
        return commandsList.stream()
            .filter(command -> category == CommandCategory.ALL || command.commandUsage().getCategory() == category)
            .toList();
    }

    void registerCommand(final Command command) {
        LiteralArgumentBuilder<CommandContext> cmdBuilder = command.register();
        String englishLiteral = cmdBuilder.getLiteral();
        if (dispatcher.getRoot().getChild(englishLiteral) != null) {
            DEFAULT_LOG.warn("Duplicate command being registered: {}", englishLiteral, new RuntimeException());
        }
        if (Lang.isEs()) {
            String spanishLiteral = SPANISH_ALIASES.get(englishLiteral);
            if (spanishLiteral != null) {
                LiteralCommandNode<CommandContext> englishNode = cmdBuilder.build();
                var root = dispatcher.getRoot();
                if (root.getChild(spanishLiteral) == null) {
                    dispatcher.register(command.redirect(spanishLiteral, englishNode));
                }
                command.commandUsage().getAliases().forEach(alias -> {
                    if (root.getChild(alias) == null) {
                        dispatcher.register(command.redirect(alias, englishNode));
                    }
                });
                return;
            }
        }
        final LiteralCommandNode<CommandContext> node = dispatcher.register(cmdBuilder);
        command.commandUsage().getAliases().forEach(alias -> {
            if (dispatcher.getRoot().getChild(alias) == null) {
                dispatcher.register(command.redirect(alias, node));
            }
        });
    }

    @Locked
    public CommandNode[] getMcplCommandNodes() {
        if (mcplCommandNodesStale.compareAndSet(true, false)) {
            syncCommandNodes();
        }
        return mcplCommandNodes;
    }

    void syncCommandNodes() {
        this.mcplCommandNodes = McplBrigadierConverter.toMcpl(this.dispatcher);
    }

    public void execute(final CommandContext context, final ParseResults<CommandContext> parseResults) {
        try {
            execute0(context, parseResults);
        } catch (final CommandSyntaxException e) {
            // fall through
            // errors handled by delegate
            // and if this not a matching root command we want to fallback to original commands
        }
        saveConfigAsync();
    }

    public void execute(final CommandContext context) {
        final ParseResults<CommandContext> parse = parse(context);
        execute(context, parse);
    }

    public ParseResults<CommandContext> parse(final CommandContext context) {
        return this.dispatcher.parse(downcaseFirstWord(context.getInput()), context);
    }

    public boolean hasCommandNode(final ParseResults<CommandContext> parse) {
        return parse.getContext().getNodes().stream().anyMatch(node -> node.getNode() instanceof CaseInsensitiveLiteralCommandNode);
    }

    private String downcaseFirstWord(final String sentence) {
        List<String> words = asList(sentence.split(" "));
        if (words.size() > 1) {
            return words.getFirst().toLowerCase() + sentence.substring(words.getFirst().length());
        } else {
            return sentence.toLowerCase();
        }
    }

    private void execute0(final CommandContext context, final ParseResults<CommandContext> parse) throws CommandSyntaxException {
        if (CONFIG.plugins.enabled && CONFIG.plugins.blockCommandsUntilLoaded && !PLUGIN_MANAGER.isInitialized()) {
            DEFAULT_LOG.warn("Blocked command execution until plugins are loaded: `{}`", context.getInput());
            return;
        }
        var commandNodeOptional = parse.getContext()
            .getNodes()
            .stream()
            .findFirst()
            .map(ParsedCommandNode::getNode)
            .filter(node -> node instanceof CaseInsensitiveLiteralCommandNode)
            .map(node -> ((CaseInsensitiveLiteralCommandNode<CommandContext>) node));
        if (commandNodeOptional.isEmpty()) return;
        var commandNode = commandNodeOptional.get();
        var errorHandler = commandNode.getErrorHandler();
        var successHandler = commandNode.getSuccessHandler();
        var executionErrorHandler = commandNode.getExecutionErrorHandler();
        var executionExceptionHandler = commandNode.getExecutionExceptionHandler();

        if (!parse.getExceptions().isEmpty() || parse.getReader().canRead()) {
            errorHandler.handle(parse.getExceptions(), context);
            return;
        }
        dispatcher.setConsumer((commandContext, success, result) -> {
            if (success) {
                if (result == Command.OK)
                    successHandler.handle(context);
                else
                    executionErrorHandler.handle(context);
            }
            else errorHandler.handle(parse.getExceptions(), context);
        });
        try {
            dispatcher.execute(parse);
        } catch (Exception e) {
            executionExceptionHandler.handle(context, e);
        }
    }

    public CompletableFuture<Suggestions> suggestions(final String input, CommandSource commandSource) {
        var stringReader = new StringReader(downcaseFirstWord(input));
        if (stringReader.canRead() && stringReader.peek() == '/') {
            stringReader.skip();
        }
        final ParseResults<CommandContext> parse = this.dispatcher.parse(stringReader, CommandContext.create(input, commandSource));
        return this.dispatcher.getCompletionSuggestions(parse);
    }

    public CompletableFuture<Suggestions> suggestions(final String input, PlayerCommandSource commandSource, ServerSession session) {
        var stringReader = new StringReader(downcaseFirstWord(input));
        if (stringReader.canRead() && stringReader.peek() == '/') {
            stringReader.skip();
        }
        var ctx = CommandContext.create(input, commandSource);
        ctx.setInGamePlayerInfo(new CommandContext.InGamePlayerInfo(session));
        final ParseResults<CommandContext> parse = this.dispatcher.parse(stringReader, ctx);
        return this.dispatcher.getCompletionSuggestions(parse);
    }
}
