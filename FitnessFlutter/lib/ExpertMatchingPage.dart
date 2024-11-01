import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class ExpertMatchingPage extends StatefulWidget {
  final String token;
  final String? selectedExerciseType;
  final String? selectedAddress;

  ExpertMatchingPage({required this.token, this.selectedExerciseType, this.selectedAddress});

  @override
  _ExpertMatchingPageState createState() => _ExpertMatchingPageState();
}

class _ExpertMatchingPageState extends State<ExpertMatchingPage> {
  List<dynamic> trainers = [];
  String? myAddress;

  @override
  void initState() {
    super.initState();
    fetchMemberInfo();
  }

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
        fetchTrainerMembers();
      } else {
        print('Failed to load member info');
      }
    } catch (error) {
      print('Error: $error');
    }
  }

  Future<void> fetchTrainerMembers() async {
    if (myAddress == null) return;

    try {
      final response = await http.get(
        Uri.parse('http://localhost:8080/api/members/trainerUsers'),
        headers: {'Authorization': 'Bearer ${widget.token}'},
      );

      if (response.statusCode == 200) {
        List<dynamic> allTrainers = json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          final simplifiedMyAddress = getSimplifiedAddress(myAddress!);
          trainers = allTrainers.where((trainer) {
            final trainerAddress = trainer['address'];
            if (trainerAddress == null) {
              return false;
            }

            final simplifiedTrainerAddress = getSimplifiedAddress(trainerAddress);
            final matchesAddress = widget.selectedAddress == null
                ? simplifiedTrainerAddress == simplifiedMyAddress
                : simplifiedTrainerAddress == widget.selectedAddress;
            final matchesExercise = widget.selectedExerciseType == null ||
                widget.selectedExerciseType == trainer['exerciseType'];

            return matchesAddress && matchesExercise;
          }).toList();
        });
      } else {
        print('Failed to load trainers');
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
        fetchTrainerMembers();
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
        fetchTrainerMembers();
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
        itemCount: trainers.length,
        itemBuilder: (context, index) {
          final member = trainers[index];
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
                  Text('전문종목: ${member['exerciseType']}'),
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
