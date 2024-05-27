import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'ChatPage.dart';

class FriendsPage extends StatefulWidget {
  final String token;

  FriendsPage({required this.token});

  @override
  _FriendsPageState createState() => _FriendsPageState();
}

class _FriendsPageState extends State<FriendsPage> {
  List<dynamic> friends = [];

  @override
  void initState() {
    super.initState();
    fetchFriends();
  }

  Future<void> fetchFriends() async {
    try {
      final response = await http.get(
        Uri.parse('http://localhost:8080/api/friends/all'),
        headers: {'Authorization': 'Bearer ${widget.token}'},
      );
      if (response.statusCode == 200) {
        setState(() {
          friends = json.decode(utf8.decode(response.bodyBytes));
        });
      } else {
        print('Failed to load friends');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> confirmDeleteFriend(String friendId) async {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('친구 삭제'),
        content: Text('이 친구를 삭제하시겠습니까?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text('취소'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              deleteFriend(friendId);
            },
            child: Text('확인'),
          ),
        ],
      ),
    );
  }

  Future<void> deleteFriend(String friendId) async {
    final requestBody = json.encode({
      'friendMemberId': friendId,
    });

    try {
      final response = await http.delete(
        Uri.parse('http://localhost:8080/api/friends/delete'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${widget.token}',
        },
        body: requestBody,
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('친구 삭제 성공!')));
        fetchFriends();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('친구 삭제 실패!')));
      }
    } catch (e) {
      print('Error: $e');
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('친구 삭제 중 오류가 발생했습니다.')));
    }
  }

  Future<void> createOrGetChatRoom(String friendId) async {
    final url = 'http://localhost:8080/chat/room?user2=$friendId';

    try {
      print('Request URL: $url');
      final response = await http.post(
        Uri.parse(url),
        headers: {
          'Authorization': 'Bearer ${widget.token}',
        },
      );

      print('Response status: ${response.statusCode}');
      print('Response body: ${response.body}');

      if (response.statusCode == 200) {
        String roomId = response.body;
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => ChatPage(
              roomId: roomId,
              token: widget.token,
            ),
          ),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('채팅룸 생성 실패!')));
      }
    } catch (e) {
      print('Error: $e');
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('채팅룸 생성 중 오류가 발생했습니다.')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('친구 목록'),
      ),
      body: ListView.builder(
        itemCount: friends.length,
        itemBuilder: (context, index) {
          final friend = friends[index];
          return ListTile(
            title: Text(friend['friendName']),
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                IconButton(
                  icon: Icon(Icons.chat),
                  onPressed: () => createOrGetChatRoom(friend['friendId']),
                ),
                IconButton(
                  icon: Icon(Icons.person_remove),
                  onPressed: () => confirmDeleteFriend(friend['friendId']),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
