package com.zenith.discord;

import com.zenith.Lang;
import com.zenith.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.zenith.Globals.CONFIG;

public class DiscordRPC {

    private static final Logger LOG = LoggerFactory.getLogger("GangsProxy-RPC");
    private static final String CLIENT_ID = "1513177751203614852";
    private static final String ICON_KEY = "gangsproxy_icon";
    private static final String DISCORD_INVITE = "https://discord.gg/M5U8yfDbdw";

    private RandomAccessFile pipe;
    private boolean connected = false;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "Gang'sProxy RPC");
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> updateTask;
    private long startTimestamp;

    public void start() {
        scheduler.execute(this::connect);
    }

    public void stop() {
        if (updateTask != null) updateTask.cancel(true);
        scheduler.shutdownNow();
        disconnect();
    }

    private void connect() {
        for (int i = 0; i < 10; i++) {
            try {
                String pipePath = getPipePath(i);
                pipe = new RandomAccessFile(pipePath, "rw");
                LOG.info(Lang.t("Gang'sProxy RPC conectado al pipe de Discord {}", "Gang'sProxy RPC connected to Discord pipe {}"), i);
                handshake();
                startTimestamp = System.currentTimeMillis() / 1000L;
                updateTask = scheduler.scheduleAtFixedRate(this::updatePresence, 0, 15, TimeUnit.SECONDS);
                return;
            } catch (Exception e) {
                // try next pipe
            }
        }
        LOG.warn(Lang.t("Gang'sProxy RPC: No se pudo conectar al pipe de Discord", "Gang'sProxy RPC: Could not connect to Discord pipe"));
    }

    private String getPipePath(int i) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "\\\\.\\pipe\\discord-ipc-" + i;
        }
        String[] bases = {
            System.getenv("XDG_RUNTIME_DIR"),
            System.getenv("TMPDIR"),
            System.getenv("TMP"),
            System.getenv("TEMP"),
            "/tmp"
        };
        for (String base : bases) {
            if (base != null) return base + "/discord-ipc-" + i;
        }
        return "/tmp/discord-ipc-" + i;
    }

    private void handshake() throws Exception {
        String handshake = "{\"v\":1,\"client_id\":\"" + CLIENT_ID + "\"}";
        write(0, handshake);
        read();
    }

    private void updatePresence() {
        if (!connected) return;
        try {
            String details = getDetails();
            String state = getState();
            String payload = buildPayload(details, state);
            write(1, payload);
            read();
        } catch (Exception e) {
            LOG.warn(Lang.t("Gang'sProxy RPC: Error actualizando presencia: {}", "Gang'sProxy RPC: Error updating presence: {}"), e.getMessage());
            connected = false;
            scheduler.schedule(this::connect, 30, TimeUnit.SECONDS);
        }
    }

    private String getDetails() {
        try {
            Proxy proxy = Proxy.getInstance();
            if (proxy.isConnected()) {
                String username = CONFIG.authentication.username;
                if (username != null && !username.isEmpty()) {
                    return "Jugando como / Playing as: " + username;
                }
            }
            return "Gang'sProxy";
        } catch (Exception e) {
            return "Gang'sProxy";
        }
    }

    private String getState() {
        try {
            Proxy proxy = Proxy.getInstance();
            if (proxy.isConnected()) {
                if (proxy.isOn2b2t()) {
                    int queuePos = proxy.getQueuePosition();
                    if (queuePos > 0) {
                        return "En cola / In queue: #" + queuePos;
                    }
                    return "Dentro del servidor / In server";
                }
                return "Conectado / Connected";
            }
            return "Inactivo / Idle";
        } catch (Exception e) {
            return "Inactivo / Idle";
        }
    }

    private String buildPayload(String details, String state) {
        String nonce = UUID.randomUUID().toString();
        return "{" +
            "\"cmd\":\"SET_ACTIVITY\"," +
            "\"args\":{" +
                "\"pid\":" + ProcessHandle.current().pid() + "," +
                "\"activity\":{" +
                    "\"details\":\"" + escapeJson(details) + "\"," +
                    "\"state\":\"" + escapeJson(state) + "\"," +
                    "\"timestamps\":{\"start\":" + startTimestamp + "}," +
                    "\"assets\":{" +
                        "\"large_image\":\"" + ICON_KEY + "\"," +
                        "\"large_text\":\"Gang'sProxy\"" +
                    "}," +
                    "\"buttons\":[{\"label\":\"Unirse al Discord / Join Discord\",\"url\":\"" + DISCORD_INVITE + "\"}]" +
                "}" +
            "}," +
            "\"nonce\":\"" + nonce + "\"" +
        "}";
    }

    private void write(int opcode, String json) throws Exception {
        byte[] jsonBytes = json.getBytes("UTF-8");
        ByteBuffer buf = ByteBuffer.allocate(8 + jsonBytes.length);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(opcode);
        buf.putInt(jsonBytes.length);
        buf.put(jsonBytes);
        pipe.write(buf.array());
        if (opcode == 0) connected = true;
    }

    private String read() throws Exception {
        byte[] header = new byte[8];
        pipe.readFully(header);
        ByteBuffer buf = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN);
        buf.getInt();
        int length = buf.getInt();
        byte[] data = new byte[length];
        pipe.readFully(data);
        return new String(data, "UTF-8");
    }

    private void disconnect() {
        connected = false;
        if (pipe != null) {
            try { pipe.close(); } catch (Exception ignored) {}
            pipe = null;
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
