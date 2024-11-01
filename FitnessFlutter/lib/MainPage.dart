import 'package:flutter/material.dart';
import 'ChatRoomListPage.dart';
import 'HomePage.dart';
import 'ExpertMatchingPage.dart';
import 'MemberInfoPage.dart';
import 'PartnerMatchingPage.dart';

class MainPage extends StatefulWidget {
  final String token;

  MainPage({required this.token});

  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  int _selectedIndex = 0;
  String? selectedExerciseType;
  String? selectedAddress;
  bool isExpertMatching = false;

  Widget _getSelectedPage() {
    switch (_selectedIndex) {
      case 1:
        final exerciseType = selectedExerciseType;
        final address = selectedAddress;
        selectedExerciseType = null; // 값 초기화
        selectedAddress = null; // 값 초기화
        return PartnerMatchingPage(
          token: widget.token,
          selectedExerciseType: exerciseType,
          selectedAddress: address,
        );
      case 2:
        final exerciseType = selectedExerciseType;
        final address = selectedAddress;
        selectedExerciseType = null; // 값 초기화
        selectedAddress = null; // 값 초기화
        return ExpertMatchingPage(
          token: widget.token,
          selectedExerciseType: exerciseType,
          selectedAddress: address,
        );
      case 3:
        return ChatRoomListPage(token: widget.token);
      case 4:
        return MemberInfoPage(token: widget.token);
      default:
        return HomePage(
          token: widget.token,
          onNavigateToMatching: (String exerciseType, String address, bool isExpert) {
            setState(() {
              selectedExerciseType = exerciseType;
              selectedAddress = address;
              isExpertMatching = isExpert;
              _selectedIndex = isExpert ? 2 : 1; // 전문가 매칭이면 2, 아니면 1
            });
          },
        );
    }
  }

  void _onItemTapped(int index) {
    setState(() {
      if (!isExpertMatching) _selectedIndex = index;
      isExpertMatching = false; // 다른 경우 이동 후 초기화
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        automaticallyImplyLeading: false,
        flexibleSpace: Center(
          child: Image.asset(
            'assets/Logo.png',
            height: kToolbarHeight - 8,
          ),
        ),
      ),
      body: _getSelectedPage(),
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
