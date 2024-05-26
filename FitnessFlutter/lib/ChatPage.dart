import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class ChatPage extends StatefulWidget {
  final String roomId;

  ChatPage({required this.roomId});

  @override
  _ChatPageState createState() => _ChatPageState();
}

class _ChatPageState extends State<ChatPage> {
  late String myId;
  late String myName;
  late String otherId;
  late String otherName;
  late String timeStamp;
  late TextEditingController messageController;

  @override
  void initState() {
    super.initState();
    messageController = TextEditingController();
    fetchChatRoomDetails();
  }

  Future<void> fetchChatRoomDetails() async {
    final response = await http.get(Uri.parse('http://localhost:8080/chat/room/${widget.roomId}/details'));

    if (response.statusCode == 200) {
      final chatRoom = jsonDecode(response.body);
      setState(() {
        myId = chatRoom['myId'];
        myName = chatRoom['myName'];
        otherId = chatRoom['otherId'];
        otherName = chatRoom['otherName'];
        timeStamp = chatRoom['timeStamp'];
      });
    } else {
      throw Exception('Failed to load chat room details');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Chat Room ${widget.roomId}'),
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Expanded(
            child: Container(
              padding: EdgeInsets.all(10.0),
              decoration: BoxDecoration(
                border: Border.all(color: Colors.grey),
              ),
              child: ListView(
                children: [
                  // Messages will be displayed here
                ],
              ),
            ),
          ),
          Padding(
            padding: EdgeInsets.all(10.0),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: messageController,
                    decoration: InputDecoration(
                      hintText: 'Type your message',
                    ),
                  ),
                ),
                SizedBox(width: 10.0),
                ElevatedButton(
                  onPressed: sendMessage,
                  child: Text('Send'),
                ),
                SizedBox(width: 10.0),
                ElevatedButton(
                  onPressed: confirmLeaveRoom,
                  child: Text('Leave Room'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  void sendMessage() {
    final content = messageController.text.trim();
    if (content.isNotEmpty) {
      // Implement sending message functionality
      messageController.clear();
    }
  }

  void confirmLeaveRoom() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Leave Room'),
          content: Text('Are you sure you want to leave this chat room?'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: Text('Cancel'),
            ),
            TextButton(
              onPressed: leaveRoom,
              child: Text('Leave'),
            ),
          ],
        );
      },
    );
  }

  void leaveRoom() {
    // Implement leaving room functionality
    Navigator.of(context).pop();
  }
}

class ChatRoomListPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('My Chat Rooms'),
      ),
      body: Center(
        child: Text('Chat Room List Page'),
      ),
    );
  }
}
