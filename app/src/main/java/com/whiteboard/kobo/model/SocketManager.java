package com.whiteboard.kobo.model;

import io.socket.client.Socket;

public class SocketManager {
    private static Socket socket;

    public static synchronized Socket getSocket() {
        return socket;
    }

    public static synchronized void setSocket(Socket newSocket) {
        socket = newSocket;
    }
}
