import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'HomePage.dart';
import 'ExpertMatchingPage.dart';
import 'ProfilePage.dart'; // 프로필 페이지 임포트

class PartnerMatchingPage extends StatefulWidget {
  @override
  _PartnerMatchingPageState createState() => _PartnerMatchingPageState();
}

class _PartnerMatchingPageState extends State<PartnerMatchingPage> {
  List<dynamic> partners = [];
  String filter = 'exerciseType';

  @override
  void initState() {
    super.initState();
    fetchPartners();
  }

  Future<void> fetchPartners() async {
    try {
      final response = await http.get(Uri.parse("http://localhost:8080/api/members/info"));

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        setState(() {
          partners = data.where((partner) => !partner['trainer']).toList();
        });
      } else {
        throw Exception('Failed to load partners');
      }
    } catch (error) {
      print('Error: $error');
    }
  }

  void _filterPartners(String filter) {
    setState(() {
      this.filter = filter;
      partners.sort((a, b) {
        if (filter == 'exerciseType') {
          return a['exerciseType'].compareTo(b['exerciseType']);
        } else {
          // 거리 순으로 정렬하는 로직 추가 필요
          return 0;
        }
      });
    });
  }

  void _onItemTapped(int index) {
    if (index == 0) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => HomePage()),
      );
    } else if (index == 1) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => PartnerMatchingPage()),
      );
    } else if (index == 2) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => ExpertMatchingPage()),
      );
    }
    // 다른 인덱스에 대해서도 페이지 이동을 설정할 수 있습니다.
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        flexibleSpace: Center(
          child: Image.asset(
            'assets/Logo.png',
            height: kToolbarHeight - 8, // AppBar의 높이에 맞게 조정
          ),
        ),
        actions: [
          PopupMenuButton<String>(
            onSelected: _filterPartners,
            itemBuilder: (BuildContext context) {
              return {'exerciseType', 'distance'}.map((String choice) {
                return PopupMenuItem<String>(
                  value: choice,
                  child: Text(choice == 'exerciseType' ? '선호 운동 순' : '거리 순'),
                );
              }).toList();
            },
          ),
        ],
      ),
      body: ListView.builder(
        itemCount: partners.length,
        itemBuilder: (context, index) {
          final partner = partners[index];
          return Card(
            child: ListTile(
              leading: CircleAvatar(
                backgroundImage: NetworkImage('http://localhost:8080/api/members/profileImage/${partner['id']}'),
              ),
              title: Text(partner['name']),
              subtitle: Text('운동: ${partner['exerciseType']}\n성별: ${partner['gender']}\n주소: ${partner['address']}'),
              trailing: Icon(Icons.arrow_forward),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => ProfilePage(partner: partner),
                  ),
                );
              },
            ),
          );
        },
      ),
      bottomNavigationBar: BottomNavigationBar(
        items: [
          BottomNavigationBarItem(
            icon: Icon(Icons.home),
            label: '홈',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.group),
            label: '운동 파트너 매칭',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.school),
            label: '전문가 매칭',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.chat),
            label: '채팅',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person),
            label: '내 정보',
          ),
        ],
        currentIndex: 1,
        selectedItemColor: Colors.black,
        unselectedItemColor: Colors.grey,
        showSelectedLabels: true,
        showUnselectedLabels: true,
        selectedIconTheme: IconThemeData(color: Colors.black, opacity: 1.0),
        unselectedIconTheme: IconThemeData(color: Colors.grey, opacity: 0.5),
        onTap: _onItemTapped,
      ),
    );
  }
}
