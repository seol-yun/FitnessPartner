import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'ChatPage.dart';

class ChatRoomListPage extends StatefulWidget {
  final String token;

  ChatRoomListPage({required this.token});

  @override
  _ChatRoomListPageState createState() => _ChatRoomListPageState();
}

class _ChatRoomListPageState extends State<ChatRoomListPage> {
  Future<List<ChatRoom>> fetchChatRooms() async {
    final response = await http.get(
      Uri.parse('http://localhost:8080/chat/rooms'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ${widget.token}',
      },
    );

    if (response.statusCode == 200) {
      List<dynamic> data = jsonDecode(utf8.decode(response.bodyBytes)); // utf8.decode 추가
      return data.map((chatRoom) => ChatRoom.fromJson(chatRoom)).toList();
    } else {
      throw Exception('Failed to load chat rooms');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: FutureBuilder<List<ChatRoom>>(
        future: fetchChatRooms(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return Center(child: Text('No chat rooms available'));
          } else {
            return ListView.builder(
              itemCount: snapshot.data!.length,
              itemBuilder: (context, index) {
                final chatRoom = snapshot.data![index];
                return GestureDetector(
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => ChatPage(
                          roomId: chatRoom.roomId,
                          token: widget.token, // Pass the token to the ChatPage
                        ),
                      ),
                    );
                  },
                  child: Container(
                    margin: EdgeInsets.symmetric(vertical: 4.0, horizontal: 8.0),
                    padding: EdgeInsets.all(16.0),
                    decoration: BoxDecoration(
                      border: Border.all(color: Colors.transparent), // Invisible border
                      borderRadius: BorderRadius.circular(8.0),
                      color: Colors.grey[200], // Background color for visibility
                    ),
                    child: ListTile(
                      title: Text(chatRoom.otherName ?? '(알 수 없음)'),
                      trailing: chatRoom.newMessageCount > 0
                          ? CircleAvatar(
                        radius: 12,
                        backgroundColor: Colors.red,
                        child: Text(
                          '${chatRoom.newMessageCount}',
                          style: TextStyle(color: Colors.white, fontSize: 12),
                        ),
                      )
                          : null,
                    ),
                  ),
                );
              },
            );
          }
        },
      ),
    );
  }
}

class ChatRoom {
  final String roomId;
  final String otherName;
  final int newMessageCount; // Added for new message count

  ChatRoom({required this.roomId, required this.otherName, required this.newMessageCount});

  factory ChatRoom.fromJson(Map<String, dynamic> json) {
    return ChatRoom(
      roomId: json['roomId'],
      otherName: json['otherName'] ?? '(알 수 없음)', // Default to "(알 수 없음)" if otherName is null
      newMessageCount: json['newMessageCount'] ?? 0, // Default to 0 if newMessageCount is null
    );
  }
}
