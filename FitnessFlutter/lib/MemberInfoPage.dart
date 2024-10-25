import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'Login.dart';
import 'FriendsPage.dart';  // 친구 목록 페이지 임포트
import 'BlockedUsersPage.dart';  // 차단된 사용자 목록 페이지 임포트
import 'UpdateMemberInfoPage.dart';
import 'PhysicalInfoPage.dart';

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
  String profileImageUrl = '';
  Image? profileImage;

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
          profileImageUrl = 'http://localhost:8080/api/members/profileImage/$memberId';
        });
        fetchProfileImage();
      } else {
        print('Failed to load member info');
      }
    } catch (error) {
      print('Error: $error');
    }
  }

  Future<void> fetchProfileImage() async {
    try {
      final response = await http.get(Uri.parse(profileImageUrl));
      if (response.statusCode == 200) {
        setState(() {
          profileImage = Image.memory(response.bodyBytes);
        });
      } else {
        print('Failed to load profile image');
      }
    } catch (error) {
      print('Error: $error');
    }
  }

  Future<void> logout() async {
    try {
      final response = await http.post(
        Uri.parse('http://localhost:8081/api/auth/logout'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${widget.token}',
        },
      );

      if (response.statusCode == 200) {
        // 로컬 스토리지에서 토큰 삭제
        SharedPreferences prefs = await SharedPreferences.getInstance();
        await prefs.remove('token');

        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('로그아웃 성공!')));
        Navigator.of(context).pushAndRemoveUntil(
          MaterialPageRoute(builder: (context) => LoginPage()),
              (Route<dynamic> route) => false,
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
            Row(
              children: [
                CircleAvatar(
                  radius: 50,
                  backgroundImage: profileImage?.image,
                ),
                SizedBox(width: 16.0),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '$memberName · $memberGender',
                      style: TextStyle(fontSize: 16.0),
                    ),
                    Text(
                      '$memberAddress',
                      style: TextStyle(fontSize: 16.0),
                    ),
                    Text(
                      '$memberExerciseType',
                      style: TextStyle(fontSize: 16.0),
                    ),
                  ],
                ),
              ],
            ),
            SizedBox(height: 16.0),
            Divider(color: Colors.grey.withOpacity(0.5)), // 반투명한 선 추가
            SizedBox(height: 16.0),
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => PhysicalInfoPage(token: widget.token)),
                );
              },
              child: Text('신체정보 추가'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 50),
              ),
            ),
            SizedBox(height: 5.0), // 5px 간격 추가
            ElevatedButton(
              onPressed: () {
                // 친구 목록 페이지로 이동
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => FriendsPage(token: widget.token)),
                );
              },
              child: Text('친구 목록'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 50),
              ),
            ),
            SizedBox(height: 5.0), // 5px 간격 추가
            ElevatedButton(
              onPressed: () {
                // 차단된 사용자 목록 페이지로 이동
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => BlockedUsersPage(token: widget.token)),
                );
              },
              child: Text('차단된 사용자 목록'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 50),
              ),
            ),
            SizedBox(height: 5.0), // 5px 간격 추가
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => UpdateMemberInfoPage(token: widget.token)),
                );
              },
              child: Text('회원정보 수정'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 50),
              ),
            ),
            SizedBox(height: 32.0),
            Center(
              child: ElevatedButton(
                onPressed: logout,
                child: Text('로그아웃'),
                style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.red,
                    foregroundColor: Colors.white,
                    minimumSize: Size(double.infinity, 50),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8.0),
                    )
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
