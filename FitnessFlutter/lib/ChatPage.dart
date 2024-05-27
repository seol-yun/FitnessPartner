import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:web_socket_channel/io.dart';

class ChatPage extends StatefulWidget {
  final String roomId;
  final String token;

  ChatPage({Key? key, required this.roomId, required this.token}) : super(key: key);

  @override
  _ChatPageState createState() => _ChatPageState();
}

class _ChatPageState extends State<ChatPage> {
  TextEditingController _messageController = TextEditingController();
  List<String> _messages = [];
  late String _myId;
  late String _myName;
  late String _otherId;
  late String _otherName;
  late String _timeStamp;
  late IOWebSocketChannel _channel;

  @override
  void initState() {
    super.initState();
    fetchChatRoomDetails();
    fetchMessages();
    _channel = IOWebSocketChannel.connect('ws://localhost:8080/chat-websocket?roomId=${widget.roomId}&token=${widget.token}');
    _channel.stream.listen((message) {
      setState(() {
        _messages.add(message);
      });
    });
  }

  @override
  void dispose() {
    _channel.sink.close();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Simple Chat'),
      ),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              itemCount: _messages.length,
              itemBuilder: (context, index) {
                return ListTile(
                  title: Text(_messages[index]),
                );
              },
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _messageController,
                    decoration: InputDecoration(
                      hintText: 'Type your message',
                    ),
                  ),
                ),
                SizedBox(width: 8.0),
                ElevatedButton(
                  onPressed: () {
                    sendMessage();
                  },
                  child: Text('Send'),
                ),
                SizedBox(width: 8.0),
                ElevatedButton(
                  onPressed: () {
                    confirmLeaveRoom(context);
                  },
                  child: Text('Leave Room'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  void fetchMessages() async {
    try {
      final response = await http.get(
        Uri.parse('http://localhost:8080/chat/messages/${widget.roomId}'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${widget.token}',
        },
      );
      if (response.statusCode == 200) {
        List<dynamic> messages = jsonDecode(utf8.decode(response.bodyBytes));
        setState(() {
          _messages = messages.map((msg) => '${msg['sender']}: ${msg['content']}').toList();
        });
      } else {
        throw Exception('Failed to load messages');
      }
    } catch (error) {
      print('Error loading messages: $error');
    }
  }

  void fetchChatRoomDetails() async {
    try {
      final response = await http.get(
        Uri.parse('http://localhost:8080/chat/room/${widget.roomId}/details'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${widget.token}',
        },
      );
      if (response.statusCode == 200) {
        final data = jsonDecode(utf8.decode(response.bodyBytes));
        setState(() {
          _myId = data['myId'];
          _myName = data['myName'];
          _otherId = data['otherId'];
          _otherName = data['otherName'];
          _timeStamp = data['timeStamp'];
        });
      } else {
        throw Exception('Failed to load chat room details');
      }
    } catch (error) {
      print('Error loading chat room details: $error');
    }
  }

  void sendMessage() async {
    String content = _messageController.text.trim();
    if (content.isNotEmpty) {
      try {
        final response = await http.post(
          Uri.parse('http://localhost:8080/app/send-message/${widget.roomId}'),
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ${widget.token}',
          },
          body: jsonEncode({'content': content, 'sender': _myName}),
        );
        if (response.statusCode == 200) {
          // 메시지 전송 성공
          _messageController.clear();
          sendMessageToServer(content, _myName);
        } else {
          throw Exception('Failed to send message');
        }
      } catch (error) {
        print('Error sending message: $error');
      }
    }
  }

  void confirmLeaveRoom(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text('Leave Room'),
          content: Text('Do you really want to leave the chat room?'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: Text('Cancel'),
            ),
            TextButton(
              onPressed: () {
                leaveRoom(context);
              },
              child: Text('Leave'),
            ),
          ],
        );
      },
    );
  }

  void leaveRoom(BuildContext context) async {
    try {
      final response = await http.post(
        Uri.parse('http://localhost:8080/chat/room/leave'),
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'Authorization': 'Bearer ${widget.token}',
        },
        body: {'roomId': widget.roomId},
      );
      if (response.statusCode == 200) {
        final exitMessage = '($_myName left the chat)';
        sendMessageToServer(exitMessage, '');
        Navigator.of(context).pop();
        Navigator.of(context).pop();
      } else {
        throw Exception('Failed to leave chat room');
      }
    } catch (error) {
      print('Error leaving chat room: $error');
    }
  }

  void sendMessageToServer(String content, String sender) {
    _channel.sink.add(jsonEncode({'content': content, 'sender': sender}));
  }
}
