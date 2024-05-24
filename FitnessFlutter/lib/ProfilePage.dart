import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class ProfilePage extends StatelessWidget {
  final dynamic partner;

  ProfilePage({required this.partner});

  void _addFriend(BuildContext context) async {
    try {
      final response = await http.post(
        Uri.parse("http://localhost:8080/api/friends/add"),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'memberId': 'your_member_id', // 현재 로그인한 사용자 ID
          'friendMemberId': partner['id'],
        }),
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('친구 추가 완료')),
        );
      } else {
        throw Exception('Failed to add friend');
      }
    } catch (error) {
      print('Error: $error');
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('친구 추가 실패')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('프로필'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Center(
              child: CircleAvatar(
                radius: 50,
                backgroundImage: NetworkImage('http://localhost:8080/api/members/profileImage/${partner['id']}'),
              ),
            ),
            SizedBox(height: 16),
            Text('이름: ${partner['name']}', style: TextStyle(fontSize: 18)),
            SizedBox(height: 8),
            Text('나이: 29살', style: TextStyle(fontSize: 18)), // 나이 정보 추가 필요
            SizedBox(height: 8),
            Text('거주지: ${partner['address']}', style: TextStyle(fontSize: 18)),
            SizedBox(height: 8),
            Text('운동: ${partner['exerciseType']}', style: TextStyle(fontSize: 18)),
            SizedBox(height: 8),
            // 추가 정보가 있다면 여기에 추가
            SizedBox(height: 16),
            Center(
              child: ElevatedButton(
                onPressed: () => _addFriend(context),
                child: Text('친구 추가'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.lightBlue,
                  foregroundColor: Colors.white,
                  minimumSize: Size(150, 50),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8.0),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
