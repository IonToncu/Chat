package com.upt.chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class ChatController {
    private Map<String, String> users = new ConcurrentHashMap<>();
    private  Map<String, List<Message>> chats = new ConcurrentHashMap<>();

    @PostMapping("/chatGenerator")
    public ResponseEntity<String> generateChat(@RequestParam String chatId) {
        if (chats.containsKey(chatId)) {
            return ResponseEntity.badRequest().body("Chat with ID " + chatId + " already exists.");
        } else {
            chats.put(chatId, new ArrayList<>());
            return ResponseEntity.ok("Chat with ID " + chatId + " created successfully.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String name) {
        String userId = UUID.randomUUID().toString(); // Generate unique ID
        users.put(userId, name);
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<Void> sendMessage(@RequestParam String chatId, @RequestParam String userId, @RequestParam String message) {
        String userName = users.get(userId);
        List<Message> currentChat = chats.get(chatId);

        if (userName != null && currentChat != null) {
            Message newMessage = new Message(userId, message);
            currentChat.add(newMessage);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/chat")
    public ResponseEntity<String> getChat(@RequestParam String chatId) {
        List<Message> currentChat = chats.get(chatId);
        if (currentChat != null) {
            StringBuilder chatStringBuilder = new StringBuilder();
            for (Message message : currentChat) {
                chatStringBuilder.append(message.toString()).append("\n"); // Adăugăm fiecare mesaj la șirul de chat
            }
            String chatString = chatStringBuilder.toString();
            return ResponseEntity.ok(chatString);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/users")
    public ResponseEntity<String> getUsers() {
        StringBuilder userStringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : users.entrySet()) {
            String userId = entry.getKey();
            String userName = entry.getValue();
            userStringBuilder.append(userId).append(": ").append(userName).append("\n");
        }
        return ResponseEntity.ok(userStringBuilder.toString());
    }
}
