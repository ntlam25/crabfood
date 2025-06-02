package com.example.crabfood.websocket;

import android.content.Context;
import android.util.Log;

import com.example.crabfood.helpers.SessionManager;
import com.example.crabfood.model.LocationUpdateResponse;
import com.example.crabfood.model.WebSocketMessage;
import com.example.crabfood.model.WebSocketMessageType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {
    private static final String TAG = "WebSocketClient";
    private static final String WS_BASE_URL = "ws://10.0.2.2:8080/ws";
    
    private final Context context;
    private final long orderId;
    private final Gson gson;
    private LocationUpdateListener listener;
    private SessionManager sessionManager;
    private WebSocket webSocket;

    public WebSocketClient(Context context, long orderId) {
        this.context = context;
        this.orderId = orderId;
        this.sessionManager = new SessionManager(context);
        this.gson = new Gson();
    }

    public void connect() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        // Connect to the main WebSocket endpoint
        Request request = new Request.Builder()
                .url(WS_BASE_URL)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket Connected");
                // Subscribe to order tracking updates
                subscribeToOrderTracking();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received message: " + text);
                try {
                    Type messageType = new TypeToken<WebSocketMessage<LocationUpdateResponse>>(){}.getType();
                    WebSocketMessage<LocationUpdateResponse> message = gson.fromJson(text, messageType);
                    
                    if (message.getType() == WebSocketMessageType.RIDER_LOCATION_UPDATE) {
                        LocationUpdateResponse locationUpdate = message.getPayload();
                        if (listener != null) {
                            listener.onLocationUpdate(locationUpdate);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing message", e);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket Error", t);
                if (listener != null) {
                    listener.onConnectionError(t.getMessage());
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket Closed");
            }
        });
    }

    private void subscribeToOrderTracking() {
        if (webSocket == null) return;

        // Create subscription message
        WebSocketMessage<Long> subscribeMessage = new WebSocketMessage<>(
            WebSocketMessageType.ORDER_TRACKING_UPDATE,
            orderId
        );

        // Send subscription message
        String message = gson.toJson(subscribeMessage);
        webSocket.send(message);
        Log.d(TAG, "Subscribed to order tracking for order ID: " + orderId);
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Normal closure");
            webSocket = null;
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