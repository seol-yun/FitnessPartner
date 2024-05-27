import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'MainPage.dart';  // 메인 페이지를 임포트
import 'Signup.dart';  // 회원가입 페이지를 임포트

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
        final Map<String, dynamic> data = json.decode(response.body);
        if (data['token'] == null) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('로그인 실패')),
          );
        } else {
          String token = data['token'];
          // 토큰을 로컬 스토리지에 저장
          SharedPreferences prefs = await SharedPreferences.getInstance();
          await prefs.setString('token', token);

          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('로그인 성공!')),
          );
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (context) => MainPage(token: token)),
          );
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
      body: Center(
        child: SingleChildScrollView(
          padding: EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Image.asset(
                'assets/Logo.png',
                height: 100,
              ),
              SizedBox(height: 30.0),
              Padding(
                padding: EdgeInsets.symmetric(horizontal: 16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    TextField(
                      controller: idController,
                      decoration: InputDecoration(
                        hintText: '아이디',
                        border: UnderlineInputBorder(),
                      ),
                    ),
                    SizedBox(height: 16.0),
                    TextField(
                      controller: passwordController,
                      obscureText: true,
                      decoration: InputDecoration(
                        hintText: '비밀번호',
                        border: UnderlineInputBorder(),
                      ),
                    ),
                    SizedBox(height: 16.0),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Expanded(
                          child: TextButton(
                            onPressed: () {
                              // 아이디 찾기
                            },
                            child: Text('아이디 찾기'),
                          ),
                        ),
                        Text('|'),
                        Expanded(
                          child: TextButton(
                            onPressed: () {
                              // 비밀번호 찾기
                            },
                            child: Text('비밀번호 찾기'),
                          ),
                        ),
                        Text('|'),
                        Expanded(
                          child: TextButton(
                            onPressed: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(builder: (context) => SignupPage()),
                              );
                            },
                            child: Text('회원가입'),
                          ),
                        ),
                      ],
                    ),
                    SizedBox(height: 32.0),
                    ElevatedButton(
                      onPressed: () => _login(context),
                      child: Text('로그인'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.lightBlue,
                        foregroundColor: Colors.white,
                        minimumSize: Size(double.infinity, 50),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(8.0),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
