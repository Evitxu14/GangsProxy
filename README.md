# Setup / Configuración — Gang'sProxy


---

## System Requirements / Requisitos del Sistema

- Linux, Windows or Mac / Linux, Windows o Mac
- Recommended VPS / VPS Recomendado: [DigitalOcean](https://www.digitalocean.com)
- Minimum RAM / RAM mínima:
  - Canal `linux`: ~250MB RAM
  - Canal `java`: ~600MB RAM
- Not enough RAM? / ¿Poca RAM? → [Create a Linux swap file / Crear swap en Linux](https://linuxize.com/post/create-a-linux-swap-file/)

---

## Downloads / Descargas

Go to the releases page and download the file for your system:
Ve a la página de releases y descarga el archivo para tu sistema:

**👉 https://github.com/Evitxu14/GangsProxy/releases**

| Platform / Plataforma | File / Archivo |
|---|---|
| Windows x64 | `GangsProxy-launcher-windows-python-amd64.zip` |
| Linux x64 | `GangsProxy-launcher-linux-amd64.zip` |
| Linux aarch64 (ARM) | `GangsProxy-launcher-linux-aarch64.zip` |
| Mac M-series | `GangsProxy-launcher-macos-aarch64.zip` |
| Mac x64 (Intel) | `GangsProxy-launcher-macos-amd64.zip` |
| Alpine Linux x64 | `GangsProxy-launcher-alpine-amd64.zip` |
| Universal Python | `GangsProxy-launcher-windows-python-amd64.zip` |

---

## Instructions / Instrucciones

1. Download the launcher for your OS / Descarga el launcher para tu OS
2. Unzip the file / Descomprime el archivo
3. Run in a terminal / Ejecuta en una terminal:

**Windows:**
```
.\launch.bat
```

**Linux / Mac:**
```
./launch
```

**Python (Universal):**
```
./launch.sh
```

---

## Usage / Uso

The launcher will ask for configuration on first launch.
El launcher pedirá configuración la primera vez.

Use the `connect` command to link your MC account.
Usa el comando `connect` para vincular tu cuenta de MC.

### Command Prefixes / Prefijos de Comandos

| Source / Fuente | Prefix / Prefijo | Example / Ejemplo |
|---|---|---|
| Discord | `.` | `.help` |
| In-game / En juego | `/` or/o `!` | `/help` |
| Terminal | *(none / ninguno)* | `help` |

### Re-run Setup / Repetir Configuración
```
./launch --setup
```

### Exit / Salir
```
Ctrl + C
```

---

## Release Channels / Canales de Release

### Platforms / Plataformas

| Channel / Canal | Description / Descripción |
|---|---|
| `java` | Works on all systems. Supports Plugins. / Funciona en todos los sistemas. Soporta Plugins. |
| `linux` | Native Linux x64. ~50% less RAM, instant startup. / Linux nativo x64. ~50% menos RAM, arranque instantáneo. |

### MC Versions / Versiones de MC

| Version | Notes / Notas |
|---|---|
| `1.21.4` | Current 2b2t version (default) / Versión actual de 2b2t (por defecto) |

### Select a channel / Seleccionar un canal:
```
channel set java 1.21.4
```

### View current channel / Ver canal actual:
```
status
```

---

## Running on Linux / Ejecutar en Linux

Use a terminal multiplexer so Gang'sProxy keeps running after closing SSH.
Usa un multiplexor de terminal para que Gang'sProxy siga corriendo al cerrar SSH.

| Tool | Command |
|---|---|
| tmux *(recommended / recomendado)* | `tmux new -s gangsproxy` |
| screen | `screen -S gangsproxy` |

---

## Multiple Instances / Múltiples Instancias

Create a new folder for each instance with its own copy of the launcher files.
Crea una carpeta nueva para cada instancia con su propia copia del launcher.

Each instance needs its own:
Cada instancia necesita su propio:
- Discord bot / Bot de Discord
- Port / Puerto
- Config files / Archivos de configuración
- Terminal session / Sesión de terminal

---

## 2b2t Limits / Límites de 2b2t

2b2t limits accounts without priority queue based on:
2b2t limita cuentas sin priority queue según:

- Accounts connected per IP / Cuentas conectadas por IP
- In-game session time excluding queue / Tiempo de sesión en juego excluyendo cola

---

## Launcher CLI Options / Opciones CLI del Launcher

| Flag | Description / Descripción |
|---|---|
| `--setup` | Interactive setup wizard / Asistente de configuración |
| `--unattended` | Headless mode for Docker / Modo headless para Docker |
| `--env-config` | Set config.json values from env vars / Configura config.json desde variables de entorno |

### `--unattended` Environment Variables / Variables de Entorno

| Variable | Required / Requerido | Description / Descripción |
|---|---|---|
| `GANGS_DISCORD_TOKEN` | ✅ | Bot token |
| `GANGS_DISCORD_CHANNEL_ID` | ✅ | Management channel / Canal de gestión |
| `GANGS_DISCORD_ROLE_ID` | ✅ | Admin role / Rol admin |
| `GANGS_DISCORD_CHAT_RELAY_CHANNEL` | ❌ | Chat relay channel / Canal de chat relay |
| `GANGS_DISCORD_DISABLED` | ❌ | Disable Discord / Desactivar Discord (`true`) |
| `GANGS_MC_VERSION` | ❌ | Default: `1.21.4` |
| `GANGS_PLATFORM` | ❌ | Default: `linux` or/o `java` |
| `GANGS_PORT` | ❌ | Default: `25565` |
| `GANGS_IP` | ❌ | Default: `localhost` |

### `--env-config` Example / Ejemplo:
```
GANGS_CONFIG_server_bind_port="25565"
GANGS_CONFIG_server_proxyIP="localhost"
```

---

## Links / Enlaces

- 💬 Discord: https://discord.gg/M5U8yfDbdw
- 📦 GitHub: https://github.com/Evitxu14/GangsProxy
- 📖 Wiki: https://github.com/Evitxu14/GangsProxy/wiki/Discord‐Bot‐Guide
