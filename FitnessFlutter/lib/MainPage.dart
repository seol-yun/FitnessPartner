import 'package:flutter/material.dart';
import 'HomePage.dart';
import 'ExpertMatchingPage.dart';
import 'PartnerMatchingPage.dart';

class MainPage extends StatefulWidget {
  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  int _selectedIndex = 0;

  final List<Widget> _pages = [
    HomePage(),
    PartnerMatchingPage(),
    ExpertMatchingPage(),
  ];

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
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
      ),
      body: Navigator(
        onGenerateRoute: (settings) {
          return MaterialPageRoute(
            builder: (context) => _pages[_selectedIndex],
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
        currentIndex: _selectedIndex,
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
