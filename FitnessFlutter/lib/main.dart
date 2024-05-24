import 'package:flutter/material.dart';
import 'Login.dart';
import 'Signup.dart';  // 회원가입 페이지 임포트

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
        '/signup': (context) => SignupPage(),  // 회원가입 페이지 라우트 추가
      },
    );
  }
}
