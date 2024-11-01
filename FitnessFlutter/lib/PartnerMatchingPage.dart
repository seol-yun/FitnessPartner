import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class PartnerMatchingPage extends StatefulWidget {
  final String token;
  final String? selectedExerciseType; // 선택된 운동 종류
  final String? selectedAddress; // 선택된 주소

  PartnerMatchingPage({
    required this.token,
    this.selectedExerciseType,
    this.selectedAddress,
  });

  @override
  _PartnerMatchingPageState createState() => _PartnerMatchingPageState();
}

class _PartnerMatchingPageState extends State<PartnerMatchingPage> {
  List<dynamic> partners = [];
  String? myAddress;

  @override
  void initState() {
    super.initState();
    fetchMemberInfo();
  }

  // 주소의 시/도와 구/군만 추출하여 비교할 수 있도록 단순화
  String getSimplifiedAddress(String address) {
    List<String> parts = address.split(' ');
    if (parts.length > 1) {
      return '${parts[0]} ${parts[1]}';
    }
    return address;
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
          myAddress = data['address'];
        });
        fetchAllMembers();
      } else {
        print('Failed to load member info');
      }
    } catch (error) {
      print('Error: $error');
    }
  }

  Future<void> fetchAllMembers() async {
    if (myAddress == null) return;

    try {
      final response = await http.get(
        Uri.parse('http://localhost:8080/api/members/generalUsers'),
        headers: {'Authorization': 'Bearer ${widget.token}'},
      );

      if (response.statusCode == 200) {
        List<dynamic> allMembers = json.decode(utf8.decode(response.bodyBytes));

        setState(() {
          final simplifiedMyAddress = getSimplifiedAddress(myAddress!);
          partners = allMembers.where((member) {
            if (member['id'] == widget.token) return false;

            final memberAddress = member['address'];
            if (memberAddress == null) return false;

            final simplifiedMemberAddress = getSimplifiedAddress(memberAddress);

            if (widget.selectedExerciseType != null || widget.selectedAddress != null) {
              final matchesExercise = widget.selectedExerciseType == null || member['exerciseType'] == widget.selectedExerciseType;
              final matchesAddress = widget.selectedAddress == null || simplifiedMemberAddress == getSimplifiedAddress(widget.selectedAddress!);
              return matchesExercise && matchesAddress;
            }

            return simplifiedMemberAddress == simplifiedMyAddress;
          }).toList();
        });
      } else {
        print('Failed to load members');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> confirmAddFriend(String friendId) async {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('친구 추가'),
        content: Text('이 사용자를 친구로 추가하시겠습니까?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text('취소'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              addFriend(friendId);
            },
            child: Text('확인'),
          ),
        ],
      ),
    );
  }

  Future<void> addFriend(String friendId) async {
    final requestBody = json.encode({
      'friendMemberId': friendId,
    });

    try {
      final response = await http.post(
        Uri.parse('http://localhost:8080/api/friends/add'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${widget.token}',
        },
        body: requestBody,
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('친구 추가 성공!')));
        fetchAllMembers();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('친구 추가 실패!')));
      }
    } catch (e) {
      print('Error: $e');
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('친구 추가 중 오류가 발생했습니다.')));
    }
  }

  Future<void> confirmBlockMember(String blockMemberId) async {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('사용자 차단'),
        content: Text('이 사용자를 차단하시겠습니까?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text('취소'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              blockMember(blockMemberId);
            },
            child: Text('확인'),
          ),
        ],
      ),
    );
  }

  Future<void> blockMember(String blockMemberId) async {
    final requestBody = json.encode({
      'blockMemberId': blockMemberId,
    });

    try {
      final response = await http.post(
        Uri.parse('http://localhost:8080/api/blocks/addBlock'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${widget.token}',
        },
        body: requestBody,
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('사용자 차단 성공!')));
        fetchAllMembers();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('사용자 차단 실패!')));
      }
    } catch (e) {
      print('Error: $e');
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('사용자 차단 중 오류가 발생했습니다.')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: ListView.builder(
        itemCount: partners.length,
        itemBuilder: (context, index) {
          final member = partners[index];
          return Card(
            margin: EdgeInsets.symmetric(vertical: 8, horizontal: 16),
            child: ListTile(
              leading: CircleAvatar(
                backgroundImage: NetworkImage(
                  'http://localhost:8080/api/members/profileImage/${member['id']}',
                ),
                onBackgroundImageError: (_, __) {
                  setState(() {
                    member['hasImageError'] = true;
                  });
                },
                backgroundColor: member['hasImageError'] == true ? Colors.transparent : null,
                child: member['hasImageError'] == true ? Icon(Icons.person) : null,
              ),
              title: Text('${member['name']}'),
              subtitle: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('선호종목: ${member['exerciseType']}'),
                  Text('성별: ${member['gender']}'),
                ],
              ),
              trailing: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  IconButton(
                    icon: Icon(Icons.person_add),
                    onPressed: () => confirmAddFriend(member['id']),
                  ),
                  IconButton(
                    icon: Icon(Icons.block),
                    onPressed: () => confirmBlockMember(member['id']),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}
