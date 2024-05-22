import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class MemberInfoPage extends StatefulWidget {
  @override
  _MemberInfoPageState createState() => _MemberInfoPageState();
}

class _MemberInfoPageState extends State<MemberInfoPage> {
  String memberId = '';
  String memberName = '';
  String memberEmail = '';

  @override
  void initState() {
    super.initState();
    fetchMemberInfo();
  }

  void fetchMemberInfo() async {
    try {
      final response = await http.get(Uri.parse("/api/members/info"));
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        setState(() {
          memberId = data['id'];
          memberName = data['name'];
          memberEmail = data['email'];
        });
      } else {
        print('Failed to load member info');
      }
    } catch (error) {
      print('Error: $error');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('회원 정보'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              '아이디:',
              style: TextStyle(fontSize: 16.0),
            ),
            Text(
              memberId,
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 16.0),
            Text(
              '이름:',
              style: TextStyle(fontSize: 16.0),
            ),
            Text(
              memberName,
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 16.0),
            Text(
              '이메일:',
              style: TextStyle(fontSize: 16.0),
            ),
            Text(
              memberEmail,
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
          ],
        ),
      ),
    );
  }
}
