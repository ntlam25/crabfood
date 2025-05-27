package com.example.crabfood.websocket;

import android.content.Context;
import android.util.Log;

import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.model.LocationUpdateResponse;
import com.google.gson.Gson;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClient {
    private static final String TAG = "WebSocketClient";
    private org.java_websocket.client.WebSocketClient client;
    private final Context context;
    private final long orderId;
    private final Gson gson = new Gson();
    private LocationUpdateListener listener;
    private SessionManager sessionManager;

    public WebSocketClient(Context context, long orderId) {
        this.context = context;
        this.orderId = orderId;
        this.sessionManager = new SessionManager(context);
    }

    public void connect() {
        try {
            // Get WebSocket URL from config
            String wsBaseUrl = "ws://your-api-url/ws"; // Update with your server's WebSocket URL
            URI uri = new URI(wsBaseUrl + "/stomp");

            client = new org.java_websocket.client.WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(TAG, "WebSocket connection opened");

                    // Subscribe to order-specific location updates
                    subscribeToLocationUpdates();
                }

                @Override
                public void onMessage(String message) {
                    Log.d(TAG, "WebSocket message received: " + message);

                    try {
                        // Parse the message based on STOMP protocol
                        if (message.startsWith("MESSAGE")) {
                            // Extract JSON payload from STOMP message
                            int bodyStart = message.indexOf("\n\n") + 2;
                            if (bodyStart > 1 && bodyStart < message.length()) {
                                String body = message.substring(bodyStart);

                                // Parse the location update
                                LocationUpdateResponse locationUpdate = gson.fromJson(body, LocationUpdateResponse.class);

                                // Notify listener
                                if (listener != null) {
                                    listener.onLocationUpdate(locationUpdate);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing WebSocket message", e);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "WebSocket connection closed: " + reason);

                    // Attempt to reconnect after a delay if closed unexpectedly
                    if (remote && code != 1000) {
                        try {
                            Thread.sleep(5000);
                            connect();
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Reconnection interrupted", e);
                        }
                    }
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "WebSocket error", ex);
                    if (listener != null) {
                        listener.onConnectionError(ex.getMessage());
                    }
                }
            };

            // Connect to the WebSocket server
            client.connect();

        } catch (URISyntaxException e) {
            Log.e(TAG, "Invalid WebSocket URI", e);
            if (listener != null) {
                listener.onConnectionError("Invalid WebSocket URI: " + e.getMessage());
            }
        }
    }

    private void subscribeToLocationUpdates() {
        if (client == null || !client.isOpen()) return;

        // STOMP subscription message
        String subscribeFrame = "SUBSCRIBE\n" +
                "id:sub-" + orderId + "\n" +
                "destination:/topic/location/" + orderId + "\n" +
                "\n\0";

        client.send(subscribeFrame);
        Log.d(TAG, "Subscribed to location updates for order ID: " + orderId);
    }

    public void disconnect() {
        if (client != null && client.isOpen()) {
            try {
                // STOMP disconnect message
                String disconnectFrame = "DISCONNECT\n\n\0";
                client.send(disconnectFrame);

                // Close WebSocket connection
                client.close();
                Log.d(TAG, "WebSocket disconnected");
            } catch (Exception e) {
                Log.e(TAG, "Error disconnecting WebSocket", e);
            }
        }
    }

    public void setLocationUpdateListener(LocationUpdateListener listener) {
        this.listener = listener;
    }

    // Interface for location update callbacks
    public interface LocationUpdateListener {
        void onLocationUpdate(LocationUpdateResponse locationUpdate);

        void onConnectionError(String message);
    }
}