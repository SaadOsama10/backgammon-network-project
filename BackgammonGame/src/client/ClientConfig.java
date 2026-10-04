package client;

/**
 * Connection settings for the client.
 * Defaults to localhost:6000; can be overridden with command-line arguments
 * (--host HOST --port PORT) or the BACKGAMMON_HOST / BACKGAMMON_PORT
 * environment variables. The host is used to prefill the "Enter server IP" dialog.
 */
public final class ClientConfig {

    public static final int DEFAULT_PORT = 6000;

    private static String host = envOr("BACKGAMMON_HOST", "localhost");
    private static int port = parsePort(envOr("BACKGAMMON_PORT", String.valueOf(DEFAULT_PORT)));

    private ClientConfig() {
    }

    /**
     * Applies --host / --port command-line arguments (they take precedence over environment variables).
     * @param args the arguments passed to main
     */
    public static void applyArgs(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--host")) {
                host = args[++i];
            } else if (args[i].equals("--port")) {
                port = parsePort(args[++i]);
            }
        }
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }

    private static String envOr(String name, String fallback) {
        String value = System.getenv(name);
        return (value == null || value.trim().isEmpty()) ? fallback : value.trim();
    }

    private static int parsePort(String value) {
        try {
            int p = Integer.parseInt(value.trim());
            if (p >= 1 && p <= 65535) {
                return p;
            }
        } catch (NumberFormatException e) {
            // fall through to the default
        }
        System.out.println("Invalid port '" + value + "', using " + DEFAULT_PORT);
        return DEFAULT_PORT;
    }
}
