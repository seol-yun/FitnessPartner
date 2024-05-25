import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'PartnerMatchingPage.dart'; // 운동 파트너 매칭 페이지 임포트
import 'ExpertMatchingPage.dart'; // 전문가 매칭 페이지 임포트

class HomePage extends StatefulWidget {
  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  double height = 0.0;
  double weight = 0.0;
  List<FlSpot> exerciseData = [];
  int _selectedIndex = 0;

  @override
  void initState() {
    super.initState();
    fetchPhysicalInfo();
    fetchExerciseInfo();
  }

  Future<void> fetchPhysicalInfo() async {
    try {
      final response = await http.post(
        Uri.parse("http://localhost:8080/api/members/addPhysicalInfo"),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({'date': '2023-05-25', 'height': '180', 'weight': '77'}),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        setState(() {
          height = double.parse(data['height']);
          weight = double.parse(data['weight']);
        });
      } else {
        throw Exception('Failed to load physical info');
      }
    } catch (error) {
      print('Error: $error');
    }
  }

  Future<void> fetchExerciseInfo() async {
    try {
      final response = await http.post(
        Uri.parse("http://localhost:8080/api/members/addExerciseInfo"),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({'date': '2023-05-25', 'exerciseType': 'running', 'durationMinutes': '60'}),
      );

      if (response.statusCode == 200) {
        // final data = json.decode(response.body);
        // 예제 데이터를 기반으로 그래프 데이터를 만듭니다.
        setState(() {
          exerciseData = [
            FlSpot(0, 1000),
            FlSpot(1, 2000),
            FlSpot(2, 4000),
            FlSpot(3, 3000),
            FlSpot(4, 5000),
            FlSpot(5, 3500),
            FlSpot(6, 3000),
          ];
        });
      } else {
        throw Exception('Failed to load exercise info');
      }
    } catch (error) {
      print('Error: $error');
    }
  }

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });

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
      body: SingleChildScrollView(
        child: Column(
          children: [
            SizedBox(height: 20),
            // BMI 지수 원형 그래프
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
            // BMI 정보 텍스트
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
            // 하루 운동량 선형 그래프
            Text('운동량'),
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: AspectRatio(
                aspectRatio: 1.7,
                child: LineChart(
                  LineChartData(
                    gridData: FlGridData(show: true),
                    titlesData: FlTitlesData(
                      bottomTitles: SideTitles(
                        showTitles: true,
                        getTextStyles: (context, value) =>
                        const TextStyle(color: Colors.black, fontSize: 10),
                        getTitles: (value) {
                          switch (value.toInt()) {
                            case 0:
                              return 'MON';
                            case 1:
                              return 'TUE';
                            case 2:
                              return 'WED';
                            case 3:
                              return 'THU';
                            case 4:
                              return 'FRI';
                            case 5:
                              return 'SAT';
                            case 6:
                              return 'SUN';
                          }
                          return '';
                        },
                      ),
                      leftTitles: SideTitles(
                        showTitles: true,
                        getTextStyles: (context, value) =>
                        const TextStyle(color: Colors.black, fontSize: 10),
                        getTitles: (value) {
                          return '${value.toInt()}kcal';
                        },
                      ),
                    ),
                    borderData: FlBorderData(show: true),
                    minX: 0,
                    maxX: 6,
                    minY: 0,
                    maxY: 5000,
                    lineBarsData: [
                      LineChartBarData(
                        isCurved: true,
                        colors: [Colors.lightBlue],
                        barWidth: 5,
                        belowBarData: BarAreaData(show: false),
                        dotData: FlDotData(show: true),
                        spots: exerciseData,
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
