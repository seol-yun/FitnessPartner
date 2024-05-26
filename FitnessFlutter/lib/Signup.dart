import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:geolocator/geolocator.dart';
import 'package:geocoding/geocoding.dart';
import 'package:universal_io/io.dart';
import 'dart:convert';

class SignupPage extends StatefulWidget {
  @override
  _SignupPageState createState() => _SignupPageState();
}

class _SignupPageState extends State<SignupPage> {
  TextEditingController idController = TextEditingController();
  TextEditingController passwordController = TextEditingController();
  TextEditingController nameController = TextEditingController();
  TextEditingController emailController = TextEditingController();
  TextEditingController addressController = TextEditingController();
  TextEditingController genderController = TextEditingController();
  TextEditingController exerciseTypeController = TextEditingController();
  bool isTrainer = false;

  Future<void> signUp() async {
    String id = idController.text;
    String password = passwordController.text;
    String name = nameController.text;
    String email = emailController.text;
    String address = addressController.text;
    String gender = genderController.text;
    String exerciseType = exerciseTypeController.text;

    try {
      var url = Uri.parse('http://localhost:8080/api/members/signup');
      var response = await http.post(url, body: {
        'id': id,
        'pw': password,
        'name': name,
        'email': email,
        'address': address,
        'gender': gender,
        'exerciseType': exerciseType,
        'isTrainer': isTrainer.toString(),
      });

      if (response.statusCode == 200) {
        var data = response.body;
        if (data == '중복') {
          showDialog(
            context: context,
            builder: (BuildContext context) {
              return AlertDialog(
                title: Text('중복'),
                content: Text('이미 가입된 아이디입니다.'),
                actions: [
                  TextButton(
                    onPressed: () {
                      Navigator.of(context).pop();
                    },
                    child: Text('확인'),
                  ),
                ],
              );
            },
          );
        } else {
          showDialog(
            context: context,
            builder: (BuildContext context) {
              return AlertDialog(
                title: Text('성공'),
                content: Text(data), // 서버에서 받은 메시지 출력
                actions: [
                  TextButton(
                    onPressed: () {
                      Navigator.of(context).pop(); // 다이얼로그를 닫습니다.
                      Navigator.pushReplacementNamed(context, '/login'); // 로그인 페이지로 이동합니다.
                    },
                    child: Text('확인'),
                  ),
                ],
              );
            },
          );

        }
      }

    } catch (error) {
      print(error); // 에러 출력
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: Text('에러'),
            content: Text('회원가입에 실패하였습니다.'),
            actions: [
              TextButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: Text('확인'),
              ),
            ],
          );
        },
      );
    }
  }

  Future<void> setAddressFromLocation() async {
    if (Platform.isAndroid || Platform.isIOS) {
      await _getAddressFromGeolocator();
    } else {
      await _getAddressFromIP();
    }
  }

  Future<void> _getAddressFromGeolocator() async {
    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return Future.error('Location services are disabled.');
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return Future.error('Location permissions are denied');
      }
    }

    if (permission == LocationPermission.deniedForever) {
      return Future.error('Location permissions are permanently denied, we cannot request permissions.');
    }

    try {
      Position position = await Geolocator.getCurrentPosition(
          desiredAccuracy: LocationAccuracy.high);
      List<Placemark> placemarks = await placemarkFromCoordinates(position.latitude, position.longitude);
      Placemark place = placemarks[0];
      String address = '${place.locality}, ${place.postalCode}, ${place.country}';
      addressController.text = address;
    } catch (e) {
      print(e);
      // 위치를 가져오지 못했을 때의 처리
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: Text('에러'),
            content: Text('현재 위치를 가져오지 못했습니다.'),
            actions: [
              TextButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: Text('확인'),
              ),
            ],
          );
        },
      );
    }
  }

  Future<void> _getAddressFromIP() async {
    try {
      var response = await http.get(Uri.parse('http://ip-api.com/json'));
      if (response.statusCode == 200) {
        var data = jsonDecode(response.body);
        String address = '${data['city']}, ${data['zip']}, ${data['country']}';
        addressController.text = address;
      } else {
        throw Exception('Failed to get location from IP');
      }
    } catch (e) {
      print(e);
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: Text('에러'),
            content: Text('현재 위치를 가져오지 못했습니다.'),
            actions: [
              TextButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: Text('확인'),
              ),
            ],
          );
        },
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
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              TextField(
                controller: idController,
                decoration: InputDecoration(labelText: '아이디'),
              ),
              TextField(
                controller: passwordController,
                obscureText: true,
                decoration: InputDecoration(labelText: '비밀번호'),
              ),
              TextField(
                controller: nameController,
                decoration: InputDecoration(labelText: '이름'),
              ),
              TextField(
                controller: emailController,
                decoration: InputDecoration(labelText: '이메일'),
              ),
              Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: addressController,
                      decoration: InputDecoration(labelText: '주소'),
                    ),
                  ),
                  IconButton(
                    icon: Icon(Icons.location_on),
                    onPressed: () {
                      setAddressFromLocation();
                    },
                  ),
                ],
              ),
              TextField(
                controller: exerciseTypeController,
                decoration: InputDecoration(labelText: '선호운동'),
              ),
              CheckboxListTile(
                title: Text('트레이너 여부'),
                value: isTrainer,
                onChanged: (value) {
                  setState(() {
                    isTrainer = value!;
                  });
                },
              ),
              SizedBox(height: 20),
              Center(
                child: ElevatedButton(
                  onPressed: () {
                    signUp();
                  },
                  child: Text('회원가입'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}