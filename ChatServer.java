/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chat.blocking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChatServer {
    public static final int PORT = 4000;
    private ServerSocket serverSocket;
    private final Map<String, List<ClientSocket>> rooms = new HashMap<>();
    private final Map<ClientSocket, String> clientRooms = new HashMap<>();

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado na porta " + PORT);
        clientConnectionLoop();
    }

    private void clientConnectionLoop() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            ClientSocket clientSocket = new ClientSocket(socket);
            new Thread(() -> clientMessageLoop(clientSocket)).start();
        }
    }

    private void clientMessageLoop(ClientSocket clientSocket) {
        String msg;
        try {
            while ((msg = clientSocket.getMessage()) != null) {
                if (msg.startsWith("AUTH:")) {
                    handleAuthentication(clientSocket, msg);
                } else if (msg.startsWith("JOIN:")) {
                    handleRoomJoin(clientSocket, msg);
                } else if (msg.equalsIgnoreCase("sair")) {
                    handleClientExit(clientSocket);
                    return;
                } else {
                    System.out.printf("Mensagem recebida do cliente %s na sala %s: %s\n", clientSocket.getRemoteSocketAddress(), clientRooms.get(clientSocket), msg);
                    sendMsgToRoom(clientSocket, msg);
                    logMessage(clientSocket, msg);
                }
            }
        } finally {
            clientSocket.close();
        }
    }

    private void handleAuthentication(ClientSocket clientSocket, String authMessage) {
        String username = authMessage.substring(5); // Remove o prefixo "AUTH:"
        // Realize a verificação da autenticação com base no nome de usuário fornecido
        boolean authenticated = verifyAuthentication(username);
        if (authenticated) {
            clientSocket.sendMsg("authenticated");
        } else {
            clientSocket.sendMsg("authentication_failed");
            clientSocket.sendMsg("sair");
        }
    }

    private boolean verifyAuthentication(String username) {
        // Verifique a autenticação do usuário com base no nome de usuário fornecido
        // Retorne true se a autenticação for bem-sucedida, caso contrário, retorne false
        return username.equals("usuário") && getPassword(username).equals("senha");
    }

    private String getPassword(String username) {
        // Recupere a senha do usuário com base no nome de usuário fornecido
        // Você pode substituir essa implementação para buscar as informações de autenticação em um banco de dados, por exemplo
        if (username.equals("usuário")) {
            return "senha";
        } else {
            return "";
        }
    }

    private void handleRoomJoin(ClientSocket clientSocket, String joinMessage) {
        String roomName = joinMessage.substring(5); // Remove o prefixo "JOIN:"
        if (!rooms.containsKey(roomName)) {
            rooms.put(roomName, new LinkedList<>());
        }
        List<ClientSocket> roomClients = rooms.get(roomName);
        roomClients.add(clientSocket);
        clientRooms.put(clientSocket, roomName);
        sendMsgToRoom(clientSocket, "Cliente " + clientSocket.getRemoteSocketAddress() + " entrou na sala '" + roomName + "'.");
    }

    private void handleClientExit(ClientSocket clientSocket) {
        String roomName = clientRooms.get(clientSocket);
        if (roomName != null) {
            List<ClientSocket> roomClients = rooms.get(roomName);
            roomClients.remove(clientSocket);
            clientRooms.remove(clientSocket);
            sendMsgToRoom(clientSocket, "Cliente " + clientSocket.getRemoteSocketAddress() + " saiu da sala '" + roomName + "'.");
        }
    }

    private void sendMsgToRoom(ClientSocket sender, String msg) {
        String roomName = clientRooms.get(sender);
        if (roomName != null) {
            List<ClientSocket> roomClients = rooms.get(roomName);
            Iterator<ClientSocket> iterator = roomClients.iterator();
            while (iterator.hasNext()) {
                ClientSocket clientSocket = iterator.next();
                if (!sender.equals(clientSocket)) {
                    if (!clientSocket.sendMsg("Cliente " + sender.getRemoteSocketAddress() + " na sala '" + roomName + "': " + msg)) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    private void logMessage(ClientSocket clientSocket, String msg) {
        String roomName = clientRooms.get(clientSocket);
        if (roomName != null) {
            String log = String.format("[%s] Cliente %s na sala '%s': %s", getCurrentDateTime(), clientSocket.getRemoteSocketAddress(), roomName, msg);
            System.out.println("Log: " + log);
            // Escreva o log em um arquivo
        }
    }

    private String getCurrentDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer();
            server.start();
        } catch (IOException ex) {
            System.out.println("Erro ao iniciar o servidor: " + ex.getMessage());
        }
        System.out.println("Servidor finalizado");
    }
}
