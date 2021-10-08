/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021. 0x150 and contributors
 */

package me.zeroX150.atomic.helper;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.AltManager;
import me.zeroX150.atomic.mixin.game.IMinecraftClientAccessor;
import net.minecraft.client.util.Session;
import net.minecraft.util.Util;
import org.apache.logging.log4j.Level;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


/**
 * @author dustin - https://github.com/DustinRepo/JexClient/blob/main/src/main/java/me/dustin/jex/helper/network/MicrosoftLogin.java
 * thanks for the code and allowing me to take this
 */
public class MicrosoftLogin {
    HttpServer server;

    public void login(String accessToken, String refreshToken) {
        new Thread(() -> {
            try {
                verifyStore(accessToken, refreshToken);
                Atomic.log(Level.INFO, "Refreshing msa login tokens...");
                if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
                    accountManagerScreen.feedback = "Refreshing login tokens";
                }
                URI uri = new URI("https://login.live.com/oauth20_token.srf");

                Map<Object, Object> map = Maps.newHashMap();
                map.put("client_id", "54fd49e4-2103-4044-9603-2b028c814ec3");
                map.put("refresh_token", refreshToken);
                map.put("grant_type", "refresh_token");
                map.put("redirect_uri", "http://localhost:59125");

                HttpRequest request = HttpRequest.newBuilder(uri).POST(ofFormData(map)).build();

                HttpClient.newBuilder().build().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(resp -> {
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        String body = resp.body();
                        JsonObject object = new Gson().fromJson(body, JsonObject.class);
                        String accessToken1 = object.get("access_token").getAsString();
                        String refreshToken1 = object.get("refresh_token").getAsString();
                        authenticateXboxLive(accessToken1, refreshToken1);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void startLoginProcess() {
        new Thread(() -> {
            try {
                String done = "<style>body {font-family: Arial;}</style><body>You can close this window, login done</body>";
                server = HttpServer.create(new InetSocketAddress(59125), 0);
                server.createContext("/", exchange -> {
                    Atomic.client.execute(() -> GLFW.glfwFocusWindow(Atomic.client.getWindow().getHandle()));
                    exchange.getResponseHeaders().add("Location", "http://localhost:59125/end");
                    exchange.sendResponseHeaders(302, -1L);
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        Atomic.LOGGER.error("query=null error");
                    } else if (query.startsWith("code=")) {
                        try {
                            getAccessToken(query.replace("code=", ""));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    } else if (query.equalsIgnoreCase("error=access_denied&error_description=The user has denied access to the scope requested by the client application.")) {
                        Atomic.LOGGER.error("Access request denied");
                    } else {
                        Atomic.LOGGER.error("Something went wrong");
                    }
                });
                server.createContext("/end", ex -> {
                    try {
                        byte[] b = done.getBytes(StandardCharsets.UTF_8);
                        ex.getResponseHeaders().put("Content-Type", List.of("text/html; charset=UTF-8"));
                        ex.sendResponseHeaders(200, b.length);
                        OutputStream os = ex.getResponseBody();
                        os.write(b);
                        os.flush();
                        os.close();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    stopLoginProcess();
                });
                server.start();
                Util.getOperatingSystem().open("https://login.live.com/oauth20_authorize.srf?client_id=54fd49e4-2103-4044-9603-2b028c814ec3&response_type=code&scope=XboxLive.signin%20XboxLive.offline_access&redirect_uri=http://localhost:59125&prompt=consent");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void stopLoginProcess() {
        if (server != null)
            server.stop(0);
    }

    private void getAccessToken(String code) throws URISyntaxException {
        Atomic.LOGGER.info("Grabbing Access Token");
        if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
            accountManagerScreen.feedback = "Grabbing Access Token";
        }
        URI uri = new URI("https://login.live.com/oauth20_token.srf");
        Map<Object, Object> map = Maps.newHashMap();
        map.put("client_id", "54fd49e4-2103-4044-9603-2b028c814ec3");
        map.put("code", code);
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://localhost:59125");
        map.put("scope", "XboxLive.signin XboxLive.offline_access");

        HttpRequest request = HttpRequest.newBuilder(uri).header("Content-Type", "application/x-www-form-urlencoded").POST(ofFormData(map)).build();
        HttpClient.newBuilder().build().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(resp -> {
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                String body = resp.body();
                JsonObject jsonObject = new Gson().fromJson(body, JsonObject.class);
                String accessToken = jsonObject.get("access_token").getAsString();
                String refreshToken = jsonObject.get("refresh_token").getAsString();
                authenticateXboxLive(accessToken, refreshToken);
            } else {
                if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
                    accountManagerScreen.feedback = "Error Grabbing Access Token";
                }
            }
        });
    }

    private void authenticateXboxLive(String accessToken, String refreshToken) {
        Atomic.LOGGER.info("Authenticating Xbox Live");
        if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
            accountManagerScreen.feedback = "Authenticating Xbox Live";
        }

        try {
            URI uri = new URI("https://user.auth.xboxlive.com/user/authenticate");
            Map<Object, Object> data = Map.of(
                    "Properties", Map.of(
                            "AuthMethod", "RPS",
                            "SiteName", "user.auth.xboxlive.com",
                            "RpsTicket", "d=" + accessToken
                    ),
                    "RelyingParty", "http://auth.xboxlive.com",
                    "TokenType", "JWT"
            );

            HttpRequest request = HttpRequest.newBuilder(uri).header("Content-Type", "application/json").header("Accept", "application/json").POST(ofJSONData(data)).build();
            HttpClient.newBuilder().build().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(resp -> {
                if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                    String body = resp.body();
                    Atomic.LOGGER.info(resp.body());
                    JsonObject jsonObject = new Gson().fromJson(body, JsonObject.class);
                    String xblToken = jsonObject.get("Token").getAsString();
                    xstsAuthenticate(xblToken, refreshToken);
                } else {
                    Atomic.LOGGER.info("Status code: " + resp.statusCode() + " : " + resp.body());
                    if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
                        accountManagerScreen.feedback = "Error Authenticating Xbox Live";
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void xstsAuthenticate(String token, String refreshToken) {
        Atomic.LOGGER.info("Authenticating XSTS");
        if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
            accountManagerScreen.feedback = "Authenticating XSTS";
        }
        try {
            URI uri = new URI("https://xsts.auth.xboxlive.com/xsts/authorize");

            Map<Object, Object> data = Map.of(
                    "Properties", Map.of(
                            "SandboxId", "RETAIL",
                            "UserTokens", List.of(token)
                    ),
                    "RelyingParty", "rp://api.minecraftservices.com/",
                    "TokenType", "JWT"
            );

            HttpRequest request = HttpRequest.newBuilder(uri).header("Content-Type", "application/json").header("Accept", "application/json").POST(ofJSONData(data)).build();

            HttpClient.newBuilder().build().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(resp -> {
                if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                    String body = resp.body();
                    JsonObject jsonObject = new Gson().fromJson(body, JsonObject.class);
                    String xblXsts = jsonObject.get("Token").getAsString();
                    JsonObject claims = jsonObject.get("DisplayClaims").getAsJsonObject();
                    JsonArray xui = claims.get("xui").getAsJsonArray();
                    String uhs = (xui.get(0)).getAsJsonObject().get("uhs").getAsString();
                    authenticateMinecraft(uhs, xblXsts, refreshToken);
                } else {
                    Atomic.LOGGER.info(resp.body());
                    if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
                        accountManagerScreen.feedback = "Error Authenticating XSTS";
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void authenticateMinecraft(String userHash, String newToken, String refreshToken) {
        Atomic.LOGGER.info("Authenticating with Minecraft");
        if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
            accountManagerScreen.feedback = "Authenticating with Minecraft";
        }
        try {
            URI uri = new URI("https://api.minecraftservices.com/authentication/login_with_xbox");
            Map<Object, Object> data = Map.of(
                    "identityToken", "XBL3.0 x=" + userHash + ";" + newToken
            );
            HttpRequest request = HttpRequest.newBuilder(uri).header("Content-Type", "application/json").header("Accept", "application/json").POST(ofJSONData(data)).build();

            HttpClient.newBuilder().build().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(resp -> {
                if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                    String body = resp.body();
                    JsonObject jsonObject = new Gson().fromJson(body, JsonObject.class);
                    String mcAccessToken = jsonObject.get("access_token").getAsString();
                    verifyStore(mcAccessToken, refreshToken);
                } else {
                    Atomic.LOGGER.info(resp.body());
                    if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
                        accountManagerScreen.feedback = "Error Authenticating with Minecraft";
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void verifyStore(String token, String refreshToken) {
        Atomic.LOGGER.info("Verifying with MS Store");
        if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
            accountManagerScreen.feedback = "Verifying with MS Store";
        }
        try {
            URI uri = new URI("https://api.minecraftservices.com/entitlements/mcstore");
            HttpRequest request = HttpRequest.newBuilder(uri).header("Authorization", "Bearer " + token).GET().build();

            HttpClient.newBuilder().build().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(resp -> {
                if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                    verifyProfile(token, refreshToken);
                } else {
                    Atomic.log(Level.INFO, resp.body());
                    if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
                        accountManagerScreen.feedback = "Error Verifying MS Store";
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void verifyProfile(String token, String refresh) {
        Atomic.log(Level.INFO, "Grabbing Minecraft profile");
        if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
            accountManagerScreen.feedback = "Grabbing Minecraft Profile";
        }
        try {
            URI uri = new URI("https://api.minecraftservices.com/minecraft/profile");

            HttpRequest request = HttpRequest.newBuilder(uri).header("Authorization", "Bearer " + token).GET().build();

            HttpClient.newBuilder().build().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(resp -> {
                if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                    String body = resp.body();
                    JsonObject jsonObject = new JsonParser().parse(body).getAsJsonObject();
                    String name = jsonObject.get("name").getAsString();
                    String uuid = jsonObject.get("id").getAsString();
                    setSession(name, uuid, token, refresh);
                } else {
                    Atomic.log(Level.INFO, resp.body());
                    if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
                        accountManagerScreen.feedback = "Error Grabbing Minecraft Profile";
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setSession(String name, String uuid, String token, String refresh) {
        Atomic.client.execute(() -> {
            ((IMinecraftClientAccessor) Atomic.client).setSession(new Session(name, uuid, token, "mojang"));
            Atomic.client.setScreen(new AltManager());
            if (Atomic.client.currentScreen instanceof AltManager accountManagerScreen) {
                accountManagerScreen.feedback = "Logged in as " + name;
            }
        });
        Atomic.log(Level.INFO, "Login success. Name: " + name);
    }

    private HttpRequest.BodyPublisher ofJSONData(Map<Object, Object> data) {
        return HttpRequest.BodyPublishers.ofString(new Gson().toJson(data));
    }

    private HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}

