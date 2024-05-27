import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class PhysicalInfoPage extends StatefulWidget {
  final String token;

  PhysicalInfoPage({required this.token});

  @override
  _PhysicalInfoPageState createState() => _PhysicalInfoPageState();
}

class _PhysicalInfoPageState extends State<PhysicalInfoPage> {
  final TextEditingController _dateController = TextEditingController();
  final TextEditingController _heightController = TextEditingController();
  final TextEditingController _weightController = TextEditingController();
  final TextEditingController _exerciseTypeController = TextEditingController();
  final TextEditingController _durationMinutesController = TextEditingController();

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime(2000),
      lastDate: DateTime(2101),
    );
    if (picked != null) {
      setState(() {
        _dateController.text = picked.toIso8601String().split('T')[0]; // yyyy-MM-dd 형식으로 저장
      });
    }
  }

  Future<void> addPhysicalInfo() async {
    final requestBody = {
      'date': _dateController.text,
      'height': _heightController.text,
      'weight': _weightController.text,
    };

    final uri = Uri.http('localhost:8080', '/api/members/addPhysicalInfo', requestBody);

    try {
      final response = await http.post(
        uri,
        headers: {
          'Authorization': 'Bearer ${widget.token}',
        },
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('신체정보 추가 성공!')));
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('신체정보 추가 실패!')));
      }
    } catch (error) {
      print('Error: $error');
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('신체정보 추가 중 오류가 발생했습니다.')));
    }
  }

  Future<void> addExerciseInfo() async {
    final requestBody = {
      'date': _dateController.text,
      'exerciseType': _exerciseTypeController.text,
      'durationMinutes': _durationMinutesController.text,
    };

    final uri = Uri.http('localhost:8080', '/api/members/addExerciseInfo', requestBody);

    try {
      final response = await http.post(
        uri,
        headers: {
          'Authorization': 'Bearer ${widget.token}',
        },
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('운동정보 추가 성공!')));
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('운동정보 추가 실패!')));
      }
    } catch (error) {
      print('Error: $error');
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('운동정보 추가 중 오류가 발생했습니다.')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('신체 및 운동 정보 추가'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            TextField(
              controller: _dateController,
              decoration: InputDecoration(
                labelText: '날짜',
                suffixIcon: IconButton(
                  icon: Icon(Icons.calendar_today),
                  onPressed: () => _selectDate(context),
                ),
              ),
              readOnly: true,
            ),
            TextField(
              controller: _heightController,
              decoration: InputDecoration(
                labelText: '키',
              ),
              keyboardType: TextInputType.number,
            ),
            TextField(
              controller: _weightController,
              decoration: InputDecoration(
                labelText: '몸무게',
              ),
              keyboardType: TextInputType.number,
            ),
            ElevatedButton(
              onPressed: addPhysicalInfo,
              child: Text('신체정보 추가'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 50),
              ),
            ),
            SizedBox(height: 16.0),
            TextField(
              controller: _exerciseTypeController,
              decoration: InputDecoration(
                labelText: '운동종류',
              ),
            ),
            TextField(
              controller: _durationMinutesController,
              decoration: InputDecoration(
                labelText: '수행시간(분단위)',
              ),
              keyboardType: TextInputType.number,
            ),
            ElevatedButton(
              onPressed: addExerciseInfo,
              child: Text('운동정보 추가'),
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
