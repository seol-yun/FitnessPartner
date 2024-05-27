import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

import 'Login.dart';

class MemberInfoPage extends StatefulWidget {
  final String token;

  MemberInfoPage({required this.token});

  @override
  _MemberInfoPageState createState() => _MemberInfoPageState();
}

class _MemberInfoPageState extends State<MemberInfoPage> {
  String memberId = '';
  String memberName = '';
  String memberEmail = '';
  String memberExerciseType = '';
  String memberAddress = '';
  String memberGender = '';

  @override
  void initState() {
    super.initState();
    fetchMemberInfo();
  }

  Future<void> fetchMemberInfo() async {
    try {
      final response = await http.get(
        Uri.parse("http://localhost:8080/api/members/info"),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${widget.token}',
        },
      );
      if (response.statusCode == 200) {
        final data = json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          memberId = data['id'];
          memberName = data['name'];
          memberEmail = data['email'];
          memberExerciseType = data['exerciseType'];
          memberGender = data['gender'];
          memberAddress = data['address'];
        });
      } else {
        print('Failed to load member info');
      }
    } catch (error) {
      print('Error: $error');
    }
  }

  Future<void> logout() async {
    try {
      final response = await http.post(
        Uri.parse('http://localhost:8080/api/members/logout'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${widget.token}',
        },
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('로그아웃 성공!')));
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => LoginPage()), // 로그아웃 후 로그인 페이지로 이동
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('로그아웃 실패!')));
      }
    } catch (error) {
      print('Error: $error');
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('로그아웃 중 오류가 발생했습니다.')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Center(
              child: Column(
                children: [
                  CircleAvatar(
                    radius: 50,
                    // backgroundImage: AssetImage('assets/profile.png'), // Add your profile image asset here
                  ),
                  SizedBox(height: 8.0),
                  Text(
                    '이름: $memberName',
                    style: TextStyle(fontSize: 16.0),
                  ),
                  Text(
                    '성별: $memberGender',
                    style: TextStyle(fontSize: 16.0),
                  ),
                  Text(
                    '거주지: $memberAddress',
                    style: TextStyle(fontSize: 16.0),
                  ),
                  Text(
                    '선호운동: $memberExerciseType',
                    style: TextStyle(fontSize: 16.0),
                  ),
                ],
              ),
            ),
            SizedBox(height: 32.0),
            ElevatedButton(
              onPressed: () {
                // Navigate to preferred time
              },
              child: Text('선호 시간'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 50),
              ),
            ),
            SizedBox(height: 16.0),
            ElevatedButton(
              onPressed: () {
                // Navigate to current partnership exercise info
              },
              child: Text('현재 파트너십 운동 정보'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 50),
              ),
            ),
            SizedBox(height: 16.0),
            ElevatedButton(
              onPressed: () {
                // Navigate to approximate data of preferred exercise
              },
              child: Text('선호 운동의 대략적인 데이터'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 50),
              ),
            ),
            SizedBox(height: 16.0),
            ElevatedButton(
              onPressed: () {
                // Navigate to preferred exercise history
              },
              child: Text('선호 운동 경력'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 50),
              ),
            ),
            SizedBox(height: 16.0),
            ElevatedButton(
              onPressed: () {
                // Navigate to awards and competition history
              },
              child: Text('수상·입상 경력'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 50),
              ),
            ),
            SizedBox(height: 32.0),
            Center(
              child: ElevatedButton(
                onPressed: logout,
                child: Text('로그 아웃'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.red,
                  foregroundColor: Colors.white,
                  minimumSize: Size(200, 50),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
