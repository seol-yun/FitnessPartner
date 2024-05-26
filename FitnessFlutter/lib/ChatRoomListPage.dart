import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class ChatRoomListPage extends StatefulWidget {
  @override
  _ChatRoomListPageState createState() => _ChatRoomListPageState();
}

class _ChatRoomListPageState extends State<ChatRoomListPage> {
  Future<List<ChatRoom>> fetchChatRooms() async {
    final response = await http.get(Uri.parse('http://localhost:8080/chat/rooms'));

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
      appBar: AppBar(
        title: Text('My Chat Rooms'),
      ),
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
                return ListTile(
                  title: Text(chatRoom.otherName ?? '(알 수 없음)'),
                  trailing: ElevatedButton(
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => ChatPage(roomId: chatRoom.roomId),
                        ),
                      );
                    },
                    child: Text('Enter'),
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

  ChatRoom({required this.roomId, required this.otherName});

  factory ChatRoom.fromJson(Map<String, dynamic> json) {
    return ChatRoom(
      roomId: json['roomId'],
      otherName: json['otherName'] ?? '(알 수 없음)', // Default to "(알 수 없음)" if otherName is null
    );
  }
}

class ChatPage extends StatelessWidget {
  final String roomId;

  ChatPage({required this.roomId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Chat Room $roomId'),
      ),
      body: Center(
        child: Text('Welcome to chat room $roomId'),
      ),
    );
  }
}
