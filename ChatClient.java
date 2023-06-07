/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.chat.blocking;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient implements Runnable {
    private static final String SERVER_ADDRESS = "";
    private static final int SERVER_PORT = ChatServer.PORT;
    private ClientSocket clientSocket;
    private Scanner scanner;
    private String currentRoom;

    public ChatClient() {
        scanner = new Scanner(System.in);
    }

    public void start() throws IOException {
        try {
            clientSocket = new ClientSocket(new Socket(SERVER_ADDRESS, SERVER_PORT));
            System.out.println("Cliente conectado ao servidor em " + SERVER_ADDRESS + ":" + SERVER_PORT);
            new Thread(this).start();
            messageLoop();
        } finally {
            clientSocket.close();
        }
    }

    private void messageLoop() throws IOException {
        String msg;
        
        do {
            if (currentRoom == null) {
                System.out.println("\nOpções:");
                System.out.println("1. Criar nova sala");
                System.out.println("2. Entrar em uma sala existente");
            } else {
                System.out.println("\nOpções:");
                System.out.println("1. Enviar mensagem");
                System.out.println("2. Sair da sala");
            }
            
            System.out.print("Escolha uma opção (ou escreva 'sair' para sair): ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                if (currentRoom == null) {
                    createRoom();
                } else {
                    sendMessageLoop();
                }
            } else if (choice.equals("2")) {
                if (currentRoom == null) {
                    joinRoom();
                } else {
                    exitRoom();
                }
            } else if (choice.equalsIgnoreCase("sair")) {
                break;
            } else {
                System.out.println("Opção inválida. Tente novamente.");
            }
        } while (true);
    }

    private void createRoom() {
        System.out.print("Digite o nome da nova sala: ");
        String roomName = scanner.nextLine();
        clientSocket.sendMsg("JOIN:" + roomName);
        currentRoom = roomName;
        System.out.println("Sala '" + roomName + "' criada e você entrou nela.");
    }

    private void joinRoom() {
        System.out.print("Digite o nome da sala que deseja entrar: ");
        String roomName = scanner.nextLine();
        clientSocket.sendMsg("JOIN:" + roomName);
        currentRoom = roomName;
        System.out.println("Você entrou na sala '" + roomName + "'.");
    }

    private void sendMessageLoop() {
        String msg;
        do {
            System.out.print("Digite uma mensagem (ou escreva 'sair' para sair da sala): ");
            msg = scanner.nextLine();
            clientSocket.sendMsg(msg);
        } while (!msg.equalsIgnoreCase("sair"));
        currentRoom = null;
        System.out.println("Você saiu da sala.");
    }
    
    private void exitRoom() {
        clientSocket.sendMsg("sair");
        currentRoom = null;
        System.out.println("Você saiu da sala.");
    }

    @Override
    public void run() {
        String msg;
        while ((msg = clientSocket.getMessage()) != null) {
            System.out.println(msg);
        }
    }

    public static void main(String[] args) {
        try {
            ChatClient client = new ChatClient();
            client.start();
        } catch (IOException ex) {
            System.out.println("Erro ao iniciar cliente: " + ex.getMessage());
        }
    }
}
