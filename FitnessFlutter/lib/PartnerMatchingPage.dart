import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class PartnerMatchingPage extends StatefulWidget {
  final String token;

  PartnerMatchingPage({required this.token});

  @override
  _PartnerMatchingPageState createState() => _PartnerMatchingPageState();
}

class _PartnerMatchingPageState extends State<PartnerMatchingPage> {
  List<dynamic> partners = [];

  @override
  void initState() {
    super.initState();
    fetchAllMembers();
  }

  Future<void> fetchAllMembers() async {
    try {
      final response = await http.get(
        Uri.parse('http://localhost:8080/api/members/all'),
        headers: {'Authorization': 'Bearer ${widget.token}'},
      );
      if (response.statusCode == 200) {
        setState(() {
          partners = json.decode(utf8.decode(response.bodyBytes));
        });
      } else {
        print('Failed to load members');
      }
    } catch (e) {
      print('Error: $e');
    }
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
      appBar: AppBar(
        title: Text('파트너 정보'),
      ),
      body: ListView.builder(
        itemCount: partners.length,
        itemBuilder: (context, index) {
          final member = partners[index];
          return ListTile(
            title: Text('${member['name']} (${member['email']})'),
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                IconButton(
                  icon: Icon(Icons.person_add),
                  onPressed: () => addFriend(member['id']),
                ),
                IconButton(
                  icon: Icon(Icons.block),
                  onPressed: () => blockMember(member['id']),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
