import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;

class UserDataDTO {
  final DateTime date;
  final int height;
  final int weight;

  UserDataDTO({required this.date, required this.height, required this.weight});

  factory UserDataDTO.fromJson(Map<String, dynamic> json) {
    return UserDataDTO(
      date: DateTime.parse(json['date']),
      height: json['height'],
      weight: json['weight'],
    );
  }
}

class HomePage extends StatefulWidget {
  final String token;

  HomePage({required this.token});

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  double height = 0.0;
  double weight = 0.0;
  List<FlSpot> exerciseData = [];
  List<UserDataDTO> physicalInfo = [];
  int _selectedIndex = 0;

  @override
  void initState() {
    super.initState();
    fetchPhysicalInfo(widget.token);
  }

  @override
  void dispose() {
    // 여기에 타이머나 애니메이션 콜백을 취소하는 코드를 추가
    super.dispose();
  }

  Future<void> fetchPhysicalInfo(String token) async {
    try {
      final response = await http.get(
        Uri.parse('http://localhost:8080/api/userdata/physicalinfo'),
        headers: {
          'Authorization': 'Bearer $token',
        },
      );

      if (response.statusCode == 200) {
        List<dynamic> body = json.decode(response.body);
        physicalInfo = body.map((dynamic item) => UserDataDTO.fromJson(item)).toList();

        physicalInfo.sort((a, b) => a.date.compareTo(b.date));

        if (physicalInfo.isNotEmpty) {
          height = physicalInfo.last.height.toDouble();
          weight = physicalInfo.last.weight.toDouble();
        }

        if (mounted) {
          setState(() {
            exerciseData = physicalInfo
                .map((data) => FlSpot(
              data.date.millisecondsSinceEpoch.toDouble(),
              data.weight.toDouble(),
            ))
                .where((spot) => !spot.y.isNaN)
                .toList();
          });
        }
      } else {
        throw Exception('Failed to load physical info');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  String getFormattedDate(double value) {
    DateTime date = DateTime.fromMillisecondsSinceEpoch(value.toInt());
    return '${date.month}/${date.day}';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(
        child: Column(
          children: [
            SizedBox(height: 20),
            Text('BMI'),
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: AspectRatio(
                aspectRatio: 1.5,
                child: PieChart(
                  PieChartData(
                    sections: [
                      PieChartSectionData(
                        color: Colors.green,
                        value: height,
                        title: '키(CM)',
                        radius: 100,
                      ),
                      PieChartSectionData(
                        color: Colors.blue,
                        value: weight,
                        title: '몸무게(KG)',
                        radius: 100,
                      ),
                    ],
                    sectionsSpace: 0,
                    centerSpaceRadius: 40,
                  ),
                ),
              ),
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Column(
                  children: [
                    Text('키(CM) $height'),
                    Text('몸무게(KG) $weight'),
                  ],
                ),
              ],
            ),
            SizedBox(height: 20),
            Text('몸무게 변화'),
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: AspectRatio(
                aspectRatio: 1.7,
                child: exerciseData.isNotEmpty
                    ? LineChart(
                  LineChartData(
                    gridData: FlGridData(show: true),
                    titlesData: FlTitlesData(
                      bottomTitles: SideTitles(
                        showTitles: true,
                        getTextStyles: (context, value) =>
                        const TextStyle(
                            color: Colors.black, fontSize: 10),
                        getTitles: (value) => getFormattedDate(value),
                      ),
                      leftTitles: SideTitles(
                        showTitles: true,
                        getTextStyles: (context, value) =>
                        const TextStyle(
                            color: Colors.black, fontSize: 10),
                        getTitles: (value) {
                          return '${value.toInt()} kg';
                        },
                      ),
                      topTitles: SideTitles(showTitles: false),
                      rightTitles: SideTitles(showTitles: false),
                    ),
                    borderData: FlBorderData(show: true),
                    minX: exerciseData.first.x,
                    maxX: exerciseData.last.x,
                    minY: 0,
                    maxY: 200,
                    lineBarsData: [
                      LineChartBarData(
                        isCurved: false,
                        colors: [Colors.lightBlue],
                        barWidth: 5,
                        belowBarData: BarAreaData(show: false),
                        dotData: FlDotData(show: true),
                        spots: exerciseData,
                      ),
                    ],
                  ),
                )
                    : Center(
                  child: Text('데이터가 없습니다.'),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
