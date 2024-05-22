import 'package:flutter/material.dart';
import 'Login.dart';
import 'Signup.dart';
import 'MemberInfoPage.dart';

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
      initialRoute: '/',
      routes: {
        '/': (context) => LoginPage(),
        '/memberInfo': (context) => MemberInfoPage(), // 회원 정보 페이지
        '/signup': (context) => SignupPage(), // 회원가입 페이지
      },
    );
  }
}
