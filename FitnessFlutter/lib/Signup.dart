import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class SignupPage extends StatefulWidget {
  @override
  _SignupPageState createState() => _SignupPageState();
}

class _SignupPageState extends State<SignupPage> {
  final TextEditingController idController = TextEditingController();
  final TextEditingController pwController = TextEditingController();
  final TextEditingController nameController = TextEditingController();
  final TextEditingController emailController = TextEditingController();
  final TextEditingController addressController = TextEditingController();
  final TextEditingController exerciseTypeController = TextEditingController();
  String gender = "";
  String isTrainer = "";

  void _signup(BuildContext context) async {
    final String id = idController.text;
    final String pw = pwController.text;
    final String name = nameController.text;
    final String email = emailController.text;
    final String address = addressController.text;
    final String exerciseType = exerciseTypeController.text;

    if (gender.isEmpty || isTrainer.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('모든 항목을 입력해주세요.')),
      );
      return;
    }

    final formData = {
      'id': id,
      'pw': pw,
      'name': name,
      'email': email,
      'address': address,
      'gender': gender,
      'exerciseType': exerciseType,
      'isTrainer': isTrainer,
    };

    try {
      final response = await http.post(
        Uri.parse("http://localhost:8080/api/members/signup"),
        headers: {'Content-Type': 'application/json'},
        body: json.encode(formData),
      );

      if (response.statusCode == 200) {
        // final data = json.decode(response.body);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('회원가입 성공!')),
        );
        Navigator.pop(context);
      } else {
        throw Exception('응답 실패.');
      }
    } catch (error) {
      print('Error: $error');
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('회원가입에 실패했습니다(error).')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('회원가입'),
      ),
      body: SingleChildScrollView(
        padding: EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            TextField(
              controller: idController,
              decoration: InputDecoration(
                labelText: '아이디(이메일)',
                hintText: '이메일 주소',
              ),
            ),
            SizedBox(height: 16.0),
            TextField(
              controller: pwController,
              obscureText: true,
              decoration: InputDecoration(
                labelText: '비밀번호',
                hintText: '비밀번호',
              ),
            ),
            SizedBox(height: 16.0),
            TextField(
              controller: nameController,
              decoration: InputDecoration(
                labelText: '이름',
                hintText: '실명을 입력하세요',
              ),
            ),
            SizedBox(height: 16.0),
            TextField(
              controller: emailController,
              decoration: InputDecoration(
                labelText: '이메일',
                hintText: '이메일 주소를 입력하세요',
              ),
            ),
            SizedBox(height: 16.0),
            TextField(
              controller: addressController,
              decoration: InputDecoration(
                labelText: '주소',
                hintText: '주소를 입력하세요',
              ),
            ),
            SizedBox(height: 16.0),
            Text('성별'),
            ListTile(
              title: Text('남성'),
              leading: Radio<String>(
                value: 'male',
                groupValue: gender,
                onChanged: (String? value) {
                  setState(() {
                    gender = value!;
                  });
                },
              ),
            ),
            ListTile(
              title: Text('여성'),
              leading: Radio<String>(
                value: 'female',
                groupValue: gender,
                onChanged: (String? value) {
                  setState(() {
                    gender = value!;
                  });
                },
              ),
            ),
            SizedBox(height: 16.0),
            TextField(
              controller: exerciseTypeController,
              decoration: InputDecoration(
                labelText: '선호운동',
                hintText: '운동 유형을 입력하세요',
              ),
            ),
            SizedBox(height: 16.0),
            Text('트레이너 여부'),
            ListTile(
              title: Text('운동전문가'),
              leading: Radio<String>(
                value: 'True',
                groupValue: isTrainer,
                onChanged: (String? value) {
                  setState(() {
                    isTrainer = value!;
                  });
                },
              ),
            ),
            ListTile(
              title: Text('일반사용자'),
              leading: Radio<String>(
                value: 'False',
                groupValue: isTrainer,
                onChanged: (String? value) {
                  setState(() {
                    isTrainer = value!;
                  });
                },
              ),
            ),
            SizedBox(height: 32.0),
            ElevatedButton(
              onPressed: () => _signup(context),
              child: Text('회원가입하기'),
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
    );
  }
}
