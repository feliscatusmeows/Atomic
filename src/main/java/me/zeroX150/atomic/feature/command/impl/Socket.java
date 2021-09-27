package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Socket extends Command {
    static java.net.Socket current = null;

    static {
        new Thread(() -> {
            while (true) {
                Utils.sleep(1000);
                if (current == null) continue;
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(current.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        Utils.Client.sendMessage("[SOCKET] IN " + line);
                    }
                } catch (Exception e) {
                    Utils.Client.sendMessage("[SOCKET] Failed socket READ: " + e.getMessage());
                    Utils.Client.sendMessage("[SOCKET] Closed socket");
                    current = null;
                }
            }
        }).start();
    }

    public Socket() {
        super("Socket", "Opens a connection and sends some data to a remote socket", "socket", "socketsend");
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length < 1) {
            Utils.Client.sendMessage("I need an action");
            Utils.Client.sendMessage("Example: .socket connect 127.0.0.1 8080");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "connect", "c" -> {
                if (args.length < 3) {
                    Utils.Client.sendMessage("I need an address and port");
                    return;
                }
                String addr = args[1];
                int port = Utils.Math.tryParseInt(args[2], -1);
                if (port < 0 || port > 65535) {
                    Utils.Client.sendMessage("Invalid port");
                    return;
                }
                try {
                    current = connect(addr, port);
                } catch (Exception e) {
                    Utils.Client.sendMessage("[SOCKET] Failed socket CONNECT: " + e.getMessage());
                }
            }
            case "write", "w" -> {
                if (current == null) {
                    Utils.Client.sendMessage("Not connected to a socket");
                    return;
                }
                if (args.length < 2) {
                    Utils.Client.sendMessage("I need a message to send");
                    return;
                }
                String[] data = Arrays.copyOfRange(args, 1, args.length);
                String v = String.join(" ", data);
                try {
                    current.getOutputStream().write(v.getBytes(StandardCharsets.UTF_8));
                    Utils.Client.sendMessage("[SOCKET] OUT " + v);
                } catch (IOException e) {
                    Utils.Client.sendMessage("[SOCKET] Failed socket WRITE: " + e.getMessage());
                }
            }
            case "writebytes", "wb" -> {
                if (current == null) {
                    Utils.Client.sendMessage("Not connected to a socket");
                    return;
                }
                if (args.length < 2) {
                    Utils.Client.sendMessage("I need a message to send, encoded in hex");
                    return;
                }
                String[] data = Arrays.copyOfRange(args, 1, args.length);
                byte[] v = new byte[data.length];
                for (int i = 0; i < data.length; i++) {
                    String part = data[i];
                    int a;
                    try {
                        a = Integer.parseInt(part, 16);
                    } catch (Exception ignored) {
                        Utils.Client.sendMessage("Invalid byte\"0x" + part + "\"");
                        return;
                    }
                    v[i] = (byte) a;
                }
                try {
                    current.getOutputStream().write(v);
                    StringBuilder printable = new StringBuilder();
                    for (byte b : v) {
                        printable.append((char) b);
                    }
                    Utils.Client.sendMessage("[SOCKET] OUT " + printable);
                } catch (IOException e) {
                    Utils.Client.sendMessage("[SOCKET] Failed socket WRITE: " + e.getMessage());
                }
            }
            case "disconnect", "dc" -> {
                if (current == null) {
                    Utils.Client.sendMessage("Nothing to disconnect from");
                    return;
                }
                try {
                    current.close();
                } catch (IOException e) {
                    Utils.Client.sendMessage("Failed to close socket: " + e.getMessage());
                }

            }
            default -> Utils.Client.sendMessage("Actions: connect (c), write (w), writebytes (wb), disconnect (dc)");
        }
    }

    java.net.Socket connect(String v, int p) throws Exception {
        java.net.Socket sock = new java.net.Socket(v, p);
        sock.setSoTimeout(20000);
        return sock;
    }
}
