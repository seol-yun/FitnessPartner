import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '로그인',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      initialRoute: '/', // 초기 경로 설정
      routes: {
        '/': (context) => LoginPage(), // 로그인 페이지
        '/memberInfo': (context) => MemberInfoPage(), // 회원 정보 페이지
      },
    );
  }
}

class LoginPage extends StatelessWidget {
  final TextEditingController idController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  void _login(BuildContext context) async {
    final String id = idController.text;
    final String password = passwordController.text;

    final formData = {'id': id, 'pw': password};

    try {
      final response = await http.post(
        Uri.parse("http://localhost:8080/api/members/login"),
        headers: {'Content-Type': 'application/json'},
        body: json.encode(formData),
      );


      if (response.statusCode == 200) {
        final data = response.body;
        if (data == '0') {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('로그인 실패')),
          );
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('로그인 성공!')),
          );
          // 회원 정보 페이지로 이동
          Navigator.pushReplacementNamed(context, '/memberInfo');
        }
      } else {
        throw Exception('응답실패.');
      }
    } catch (error) {
      print('Error: $error');
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('로그인에 실패했습니다(error).')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('로그인'),
      ),
      body: Padding(
        padding: EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              '아이디:',
              style: TextStyle(fontSize: 16.0),
            ),
            TextField(
              controller: idController,
              decoration: InputDecoration(
                hintText: '아이디를 입력하세요',
              ),
            ),
            SizedBox(height: 16.0),
            Text(
              '비밀번호:',
              style: TextStyle(fontSize: 16.0),
            ),
            TextField(
              controller: passwordController,
              obscureText: true,
              decoration: InputDecoration(
                hintText: '비밀번호를 입력하세요',
              ),
            ),
            SizedBox(height: 16.0),
            ElevatedButton(
              onPressed: () => _login(context),
              child: Text('로그인'),
            ),
          ],
        ),
      ),
    );
  }
}



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