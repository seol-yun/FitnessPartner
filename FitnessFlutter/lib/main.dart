import 'package:flutter/material.dart';
import 'Login.dart';
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
      initialRoute: '/', // 초기 경로 설정
      routes: {
        '/': (context) => LoginPage(), // 로그인 페이지
        '/memberInfo': (context) => MemberInfoPage(), // 회원 정보 페이지
      },
    );
  }
}
