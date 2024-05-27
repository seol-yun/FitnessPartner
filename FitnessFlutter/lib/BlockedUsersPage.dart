import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class BlockedUsersPage extends StatefulWidget {
  final String token;

  BlockedUsersPage({required this.token});

  @override
  _BlockedUsersPageState createState() => _BlockedUsersPageState();
}

class _BlockedUsersPageState extends State<BlockedUsersPage> {
  List<dynamic> blockedUsers = [];

  @override
  void initState() {
    super.initState();
    fetchBlockedUsers();
  }

  Future<void> fetchBlockedUsers() async {
    try {
      final response = await http.get(
        Uri.parse('http://localhost:8080/api/blocks/all'), // 차단된 사용자 목록 API 호출
        headers: {'Authorization': 'Bearer ${widget.token}'},
      );
      if (response.statusCode == 200) {
        setState(() {
          blockedUsers = json.decode(utf8.decode(response.bodyBytes));
        });
      } else {
        print('Failed to load blocked users');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> unblockUser(String blockMemberId) async {
    final requestBody = json.encode({
      'blockMemberId': blockMemberId,
    });

    try {
      final response = await http.post(
        Uri.parse('http://localhost:8080/api/blocks/unBlock'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${widget.token}',
        },
        body: requestBody,
      );

      print('Response status: ${response.statusCode}');
      print('Response body: ${response.body}');

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('차단 해제 성공!')));
        fetchBlockedUsers(); // 차단 해제 후 목록 갱신
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('차단 해제 실패!')));
      }
    } catch (e) {
      print('Error: $e');
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('차단 해제 중 오류가 발생했습니다.')));
    }
  }

  void confirmUnblockUser(String blockMemberId) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('차단 해제'),
          content: Text('해당 사용자의 차단을 해제하시겠습니까?'),
          actions: <Widget>[
            TextButton(
              child: Text('취소'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
            TextButton(
              child: Text('확인'),
              onPressed: () {
                Navigator.of(context).pop();
                unblockUser(blockMemberId);
              },
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('차단된 사용자 목록'),
      ),
      body: ListView.builder(
        itemCount: blockedUsers.length,
        itemBuilder: (context, index) {
          final user = blockedUsers[index];
          return ListTile(
            title: Text('${user['blockedName']} (${user['blockId']})'),
            trailing: IconButton(
              icon: Icon(Icons.block),
              onPressed: () => confirmUnblockUser(user['blockId']),
            ),
          );
        },
      ),
    );
  }
}
