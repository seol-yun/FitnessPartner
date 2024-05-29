import 'dart:convert';
import 'dart:typed_data';
import 'dart:html' as html;
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class UpdateMemberInfoPage extends StatefulWidget {
  final String token;

  UpdateMemberInfoPage({required this.token});

  @override
  _UpdateMemberInfoPageState createState() => _UpdateMemberInfoPageState();
}

class _UpdateMemberInfoPageState extends State<UpdateMemberInfoPage> {
  final TextEditingController _idController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _addressController = TextEditingController();
  final TextEditingController _exerciseTypeController = TextEditingController();
  String? _gender;
  String? _isTrainer;
  html.File? _profilePic;
  Uint8List? _profilePicBytes;
  String profileImageUrl = '';

  @override
  void initState() {
    super.initState();
    fetchMemberInfo();
    fetchProfileImage();
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
          _idController.text = data['id'];
          _passwordController.text = data['pw'];
          _nameController.text = data['name'];
          _emailController.text = data['email'];
          _addressController.text = data['address'];
          _gender = data['gender'];
          _exerciseTypeController.text = data['exerciseType'];
          _isTrainer = data['trainer'].toString();
        });
      } else {
        print('Failed to load member info');
      }
    } catch (error) {
      print('Error: $error');
    }
  }

  Future<void> fetchProfileImage() async {
    try {
      final response = await http.get(
        Uri.parse("http://localhost:8080/api/members/profileImage/${_idController.text}"),
        headers: {
          'Authorization': 'Bearer ${widget.token}',
        },
      );
      if (response.statusCode == 200) {
        setState(() {
          profileImageUrl = response.body;
        });
      } else {
        print('Failed to load profile image');
      }
    } catch (error) {
      print('Error: $error');
    }
  }

  Future<void> _pickProfilePic() async {
    final picker = html.FileUploadInputElement()..accept = 'image/*';
    picker.click();
    picker.onChange.listen((event) {
      final file = picker.files!.first;
      final reader = html.FileReader();
      reader.readAsArrayBuffer(file);
      reader.onLoadEnd.listen((event) {
        setState(() {
          _profilePic = file;
          _profilePicBytes = reader.result as Uint8List?;
        });
      });
    });
  }

  Future<void> _uploadProfilePic() async {
    if (_profilePic == null || _profilePicBytes == null) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('프로필 사진을 선택해주세요.')));
      return;
    }

    try {
      final uri = Uri.parse('http://localhost:8080/api/members/uploadProfileImage');
      final request = http.MultipartRequest('POST', uri)
        ..headers['Authorization'] = 'Bearer ${widget.token}'
        ..fields['id'] = _idController.text
        ..files.add(http.MultipartFile.fromBytes(
          'file',
          _profilePicBytes!,
          filename: _profilePic!.name,
        ));

      final response = await request.send();

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('프로필 사진 업로드 성공!')));
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('프로필 사진 업로드 실패!')));
      }
    } catch (error) {
      print('Error: $error');
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('프로필 사진 업로드 중 오류가 발생했습니다.')));
    }
  }

  Future<void> updateMemberInfo() async {
    try {
      final response = await http.post(
        Uri.parse('http://localhost:8080/api/members/update'),
        headers: {
          'Authorization': 'Bearer ${widget.token}',
          'Content-Type': 'application/json',
        },
        body: jsonEncode({
          'id': _idController.text,
          'pw': _passwordController.text,
          'name': _nameController.text,
          'email': _emailController.text,
          'address': _addressController.text,
          'gender': _gender,
          'exerciseType': _exerciseTypeController.text,
          'isTrainer': _isTrainer,
        }),
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('회원정보 수정 성공!')));
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('회원정보 수정 실패!')));
      }
    } catch (error) {
      print('Error: $error');
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('회원정보 수정 중 오류가 발생했습니다.')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('회원정보 수정'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            TextField(
              controller: _idController,
              decoration: InputDecoration(
                labelText: '아이디',
              ),
              readOnly: true,
            ),
            TextField(
              controller: _passwordController,
              decoration: InputDecoration(
                labelText: '비밀번호',
              ),
            ),
            TextField(
              controller: _nameController,
              decoration: InputDecoration(
                labelText: '이름',
              ),
            ),
            TextField(
              controller: _emailController,
              decoration: InputDecoration(
                labelText: '이메일',
              ),
            ),
            TextField(
              controller: _addressController,
              decoration: InputDecoration(
                labelText: '주소',
              ),
            ),
            Column(
              children: [
                ListTile(
                  title: const Text('남성'),
                  leading: Radio<String>(
                    value: 'male',
                    groupValue: _gender,
                    onChanged: (value) {
                      setState(() {
                        _gender = value;
                      });
                    },
                  ),
                ),
                ListTile(
                  title: const Text('여성'),
                  leading: Radio<String>(
                    value: 'female',
                    groupValue: _gender,
                    onChanged: (value) {
                      setState(() {
                        _gender = value;
                      });
                    },
                  ),
                ),
              ],
            ),
            TextField(
              controller: _exerciseTypeController,
              decoration: InputDecoration(
                labelText: '선호운동',
              ),
            ),
            Column(
              children: [
                ListTile(
                  title: const Text('운동전문가'),
                  leading: Radio<String>(
                    value: 'True',
                    groupValue: _isTrainer,
                    onChanged: (value) {
                      setState(() {
                        _isTrainer = value;
                      });
                    },
                  ),
                ),
                ListTile(
                  title: const Text('일반사용자'),
                  leading: Radio<String>(
                    value: 'False',
                    groupValue: _isTrainer,
                    onChanged: (value) {
                      setState(() {
                        _isTrainer = value;
                      });
                    },
                  ),
                ),
              ],
            ),
            ElevatedButton(
              onPressed: _pickProfilePic,
              child: Text('프로필 사진 선택'),
            ),
            SizedBox(height: 16.0),
            ElevatedButton(
              onPressed: _uploadProfilePic,
              child: Text('프로필 사진 업로드'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 50),
              ),
            ),
            SizedBox(height: 16.0),
            ElevatedButton(
              onPressed: updateMemberInfo,
              child: Text('회원정보 수정'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 50),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
