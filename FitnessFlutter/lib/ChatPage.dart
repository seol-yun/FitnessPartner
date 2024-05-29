import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:sockjs_client_wrapper/sockjs_client_wrapper.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';

class ChatPage extends StatefulWidget {
  final String roomId;
  final String token;

  ChatPage({required this.roomId, required this.token});

  @override
  _ChatPageState createState() => _ChatPageState();
}

class _ChatPageState extends State<ChatPage> {
  StompClient? stompClient;
  TextEditingController messageController = TextEditingController();
  List<Map<String, String>> messages = [];
  String myId = '';
  String myName = '';
  String otherId = '';
  String otherName = '';
  String timeStamp = '';
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    connectToWebSocket();
    fetchChatRoomDetails();
    fetchMessages();
  }

  void connectToWebSocket() {
    stompClient = StompClient(
      config: StompConfig.SockJS(
        url: 'http://localhost:8080/chat-websocket',
        onConnect: onWebSocketConnect,
        onStompError: onWebSocketError,
        beforeConnect: () async {
          print('Connecting to websocket...');
        },
        onWebSocketError: (dynamic error) => print('WebSocket Error: $error'),
        onDisconnect: (frame) => print('WebSocket Disconnected'),
        onWebSocketDone: () => print('WebSocket Closed'),
        stompConnectHeaders: {'Authorization': 'Bearer ${widget.token}'},
        webSocketConnectHeaders: {'Authorization': 'Bearer ${widget.token}'},
        reconnectDelay: Duration(seconds: 5),
      ),
    );

    stompClient?.activate();
  }

  void onWebSocketConnect(StompFrame frame) {
    print('Connected to WebSocket');
    stompClient?.subscribe(
      destination: '/topic/messages/${widget.roomId}',
      callback: (frame) {
        Map<String, dynamic> message = json.decode(frame.body!);
        setState(() {
          messages.add({
            'sender': message['sender'],
            'content': message['content'],
          });
        });
      },
    );
  }

  void onWebSocketError(StompFrame frame) {
    print('WebSocket error: ${frame.body}');
  }

  Future<void> fetchChatRoomDetails() async {
    try {
      final response = await http.get(
        Uri.parse('http://localhost:8080/chat/room/${widget.roomId}/details'),
        headers: {
          'Authorization': 'Bearer ${widget.token}',
        },
      );

      if (response.statusCode == 200) {
        final chatRoom = json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          myId = chatRoom['myId'];
          myName = chatRoom['myName'];
          otherId = chatRoom['otherId'];
          otherName = chatRoom['otherName'];
          timeStamp = chatRoom['timeStamp'];
        });
      } else {
        print('Failed to load chat room details');
      }
    } catch (e) {
      print('Error fetching chat room details: $e');
    }
  }

  Future<void> fetchMessages() async {
    try {
      final response = await http.get(
        Uri.parse('http://localhost:8080/chat/messages/${widget.roomId}'),
        headers: {
          'Authorization': 'Bearer ${widget.token}',
        },
      );

      if (response.statusCode == 200) {
        final List<dynamic> chatMessages = json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          messages = chatMessages
              .map<Map<String, String>>((msg) => {
            'sender': msg['sender'],
            'content': msg['message']
          })
              .toList();
          isLoading = false;
        });
      } else {
        print('Failed to load messages');
        setState(() {
          isLoading = false;
        });
      }
    } catch (e) {
      print('Error fetching messages: $e');
      setState(() {
        isLoading = false;
      });
    }
  }

  void sendMessage() {
    if (messageController.text.isNotEmpty) {
      stompClient?.send(
        destination: '/app/send-message/${widget.roomId}',
        body: json.encode({
          'content': messageController.text.trim(),
          'sender': myName,
        }),
      );
      messageController.clear();
    }
  }

  Future<void> leaveRoom() async {
    try {
      final response = await http.post(
        Uri.parse('http://localhost:8080/chat/room/leave'),
        headers: {
          'Authorization': 'Bearer ${widget.token}',
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'roomId=${widget.roomId}',
      );

      if (response.statusCode == 200) {
        stompClient?.send(
          destination: '/app/send-message/${widget.roomId}',
          body: json.encode(
              {'content': '($myName님이 채팅에서 나갔습니다.)', 'sender': ''}),
        );

        Navigator.pop(context);
      } else {
        print('Failed to leave chat room');
      }
    } catch (e) {
      print('Error leaving chat room: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(otherName),
        actions: [
          IconButton(
            icon: Icon(Icons.exit_to_app),
            onPressed: leaveRoom,
          ),
        ],
      ),
      body: isLoading
          ? Center(child: CircularProgressIndicator())
          : Column(
        children: [
          Expanded(
            child: ListView.builder(
              itemCount: messages.length,
              itemBuilder: (context, index) {
                final message = messages[index];
                final isMine = message['sender'] == myName;
                return Container(
                  alignment:
                  isMine ? Alignment.centerRight : Alignment.centerLeft,
                  margin:
                  EdgeInsets.symmetric(vertical: 2.0, horizontal: 8.0),
                  child: Column(
                    crossAxisAlignment: isMine
                        ? CrossAxisAlignment.end
                        : CrossAxisAlignment.start,
                    children: [
                      if (!isMine)
                        Text(message['sender']!,
                            style: TextStyle(fontWeight: FontWeight.bold)),
                      Container(
                        padding: EdgeInsets.all(10.0),
                        constraints: BoxConstraints(
                            maxWidth:
                            MediaQuery.of(context).size.width * 0.6),
                        decoration: BoxDecoration(
                          color: isMine
                              ? Colors.lightBlue[50]
                              : Colors.grey[200],
                          borderRadius: BorderRadius.circular(8.0),
                        ),
                        child: Text(message['content']!),
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
          Container(
            color: Colors.lightBlue[50],
            padding: const EdgeInsets.all(8.0),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: messageController,
                    decoration: InputDecoration(
                      hintText: '여기에 메세지를 입력하세요.',
                    ),
                  ),
                ),
                IconButton(
                  icon: Icon(Icons.send),
                  onPressed: sendMessage,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    messageController.dispose();
    stompClient?.deactivate();
    super.dispose();
  }
}
