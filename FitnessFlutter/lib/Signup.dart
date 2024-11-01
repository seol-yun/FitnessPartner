import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:geolocator/geolocator.dart';
import 'package:geocoding/geocoding.dart';
import 'package:universal_io/io.dart';
import 'dart:convert';
import 'Login.dart';

class SignupPage extends StatefulWidget {
  @override
  _SignupPageState createState() => _SignupPageState();
}

class _SignupPageState extends State<SignupPage> {
  TextEditingController idController = TextEditingController();
  TextEditingController passwordController = TextEditingController();
  TextEditingController nameController = TextEditingController();
  TextEditingController emailController = TextEditingController();
  bool isTrainer = false;
  String selectedGender = '남성'; // 초기값 설정
  String? selectedCity;
  String? selectedDistrict;
  String? selectedExerciseType;

  // 시/도 데이터
  final List<String> cities = ['서울', '경기', '인천', '강원', '제주', '부산', '경남', '대구', '경북', '울산', '대전', '충남', '충북', '광주', '전남', '전북'];

  // 시/군/구 데이터
  final Map<String, List<String>> districts = {
    '서울': ['강남/역삼/삼성/논현/선릉', '서초/신사/방배', '잠실/방이', '잠실새내/신천', '영등포/여의도', '구로/금천/오류/신도림', '강서/화곡/까치산역/목동', '천호/길동/둔촌',
    '서울대/신림/사당/동작', '종로/대학로', '용산/중구/명동/이태원/서울역', '성신여대/성북/월곡', '노원/도봉/창동', '강북/수유/미아', '왕십리/성수/금호', '건대/광진/구의', '동대문/장안/청량리/답십리',
    '중랑/상봉/면목/태릉', '신촌/홍대/서대문/마포', '은평/연신내/불광/응암'],
    '경기': ['수원/인계', '수원시청/권선/영통', '수원역/세류/팔달문/구운/장안', '대부도/제부도', '평택/송탄/고덕/안성', '오산/화성/동탄', '파주/금촌/헤이리/통일동산', '김포/장기/구래/대명항',
    '고양/일산', '의정부', '부천/중동/상동/원종/역곡', '안양/평촌/인덩원/과천', '군포/금정/산본/의왕', '안산', '광명/철산/시흥신천역', '시흥/월곶/정왕/오이도/거북섬', '용인',
    '이천/광주/여주/곤지암', '성남/분당', '구리/하남', '남양주', '가평/양평', '양주/동두천/연천/장흥', '포천'],
    '인천': ['부평', '주안', '구월/소래포구', '동암/간석', '을왕리/인천공항', '월미도/차이나타운/신포/동인천', '작전/경인교대', '용현/숭의/도화/송림', '송도/연수', '서구/검단/검단신도시', '강화/웅진/백령도'],
    '강원': ['강릉역/교통택지/옥계', '경포대/사천/주문진', '양양/낙산/하초대/인계', '속초/설악/동명항/고성', '춘천/홍천/철원/화천', '원주/횡성', '정동진/동해/삼척', '평창/영월/정선/태백'],
    '제주': ['제주시/함덕', '애월/협재', '서귀포/중문/성산/마라도'],
    '부산': ['해운대/센텀시티/재송', '송정/기정/정관', '서면/초읍/양정/부산시민공원/범천', '남포동/중앙동/자갈치/송도/태종대/영도', '부산역/범일동/부산진역/부산국제여객터미널',
    '광안리/수영/민락', '경성대/대연/용호/문현', '동래/온천장/부산대/구서/사직', '연산/토곡', '사상(공항경전철)/학장/엄궁', '강서/하단/사하/명지/신호/다대포/괴정/지사', '덕천/만덕/구포/화명/북구'],
    '경남': ['김해/장유/율하', '양산/밀양', '거제/통영/고성군', '진주', '사천/남해/하동', '창원 상남/용호/중앙', '창원 명서/팔용/봉곡/북면', '마산/진해', '거창/함안/창녕/합천/의령'],
    '대구': ['동성로/시청/서문시장', '대구역/경북대/엑스코/칠곡3지구/태전동/금호지구', '동대구역/신천/동촌유원지/대구공항/혁신도시/팔공산/군위', '수성구/수성못/범어/알파시티/남구/대명/봉덕/성당못', '두류공원/두류/본리/죽전/감삼',
    '서대구역/평리/내당/비산/원대', '성서공단/계명대/이곡/상인/월배/대곡/달성군/현풍/가창'],
    '경북': ['경주/보문단지/황리단길/불국사/안강/김포/양남', '구미/인동/원평/옥계/봉곡/금오산/구미역', '포항남구/시청/시외버스터미널/구룡포/문덕/오천', '포항북구/영일대/죽도시장/여객터미널', '울진/울릉도/청송/영덕', '경산/하양/영남대/갓바위/영천/청도',
    '문경/상주/영주/예천', '안동/경북도청/하회마을/도산서원', '김천/칠곡/왜관/성주/의성'],
    '울산': ['동구/북구/울주군/일산/정자/진하/영남알프스', '남구/중구/삼산/성남/무거/신정/달동'],
    '대전': ['유성/봉명/도안/장대', '중구 은행/대흥/선화/유천', '동구 용전/복합터미널', '대덕구 신탄진/중리', '서구 둔산/용문/월평'],
    '충남': ['천안 서북구', '천안 동남구', '계룡/금산/논산', '공주/동학사', '아산', '태안/안면도', '서산', '당진', '서천/부여', '대천/보령', '예산/청양/홍성', '세종'],
    '충북': ['청주 흥덕구/서원구', '청주 상당구/청원구', '진천/음성', '제천/단양', '충주/수안보', '증평/괴산/영동/보은/옥천'],
    '광주': ['북구/챔피언스필드/광주역/전남대학교', '서구/상무지구/금호지구/풍암지구/유스퀘어', '동구/남구/국립아시아문화전당/충장로', '광산구 하남/송정역', '광산구 첨단지구'],
    '전남': ['여수', '순천', '광양', '목포/무안/영암', '나주/담양/곡성/구례/영광/장성/함평', '화순/보성/해남/완도/강진/고흥/진도'],
    '전북': ['전주 덕진구', '전주 완산구/완주', '군산/비응도', '남원/임실/순창/무주/진안/장수','익산/익산터미널/익산역', '김제/부안/고창/정읍'],
  };

  // 운동 종류 데이터
  final List<String> exerciseTypes = ['런닝', '싸이클', '수영', '골프', '테니스', '배드민턴', '요가', '헬스', '필라테스', '크로스핏', '등산', '기타'];

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
                    child: DropdownButtonFormField<String>(
                      decoration: InputDecoration(labelText: '시/도'),
                      value: selectedCity,
                      onChanged: (String? newValue) {
                        setState(() {
                          selectedCity = newValue;
                          selectedDistrict = null; // 시/도가 변경될 때 구/동 초기화
                        });
                      },
                      items: cities
                          .map<DropdownMenuItem<String>>((String city) {
                        return DropdownMenuItem<String>(
                          value: city,
                          child: Text(city),
                        );
                      }).toList(),
                    ),
                  ),
                  SizedBox(width: 10),
                  Expanded(
                    child: DropdownButtonFormField<String>(
                      decoration: InputDecoration(labelText: '시/군/구'),
                      value: selectedDistrict,
                      onChanged: (String? newValue) {
                        setState(() {
                          selectedDistrict = newValue;
                        });
                      },
                      items: selectedCity != null
                          ? districts[selectedCity]!
                          .map<DropdownMenuItem<String>>((String district) {
                        return DropdownMenuItem<String>(
                          value: district,
                          child: Text(district),
                        );
                      }).toList()
                          : [],
                    ),
                  ),
                ],
              ),
              DropdownButtonFormField<String>(
                decoration: InputDecoration(labelText: '성별'),
                value: selectedGender,
                onChanged: (String? newValue) {
                  setState(() {
                    selectedGender = newValue!;
                  });
                },
                items: <String>['남성', '여성']
                    .map<DropdownMenuItem<String>>((String value) {
                  return DropdownMenuItem<String>(
                    value: value,
                    child: Text(value),
                  );
                }).toList(),
              ),
              DropdownButtonFormField<String>(
                decoration: InputDecoration(labelText: '선호운동'),
                value: selectedExerciseType,
                onChanged: (String? newValue) {
                  setState(() {
                    selectedExerciseType = newValue;
                  });
                },
                items: exerciseTypes
                    .map<DropdownMenuItem<String>>((String type) {
                  return DropdownMenuItem<String>(
                    value: type,
                    child: Text(type),
                  );
                }).toList(),
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
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.lightBlue[50],
                    foregroundColor: Colors.deepPurple,
                    minimumSize: Size(double.infinity, 50),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8.0),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> signUp() async {
    String id = idController.text;
    String password = passwordController.text;
    String name = nameController.text;
    String email = emailController.text;
    String address = '${selectedCity ?? ''} ${selectedDistrict ?? ''}'.trim();
    String gender = selectedGender; // 선택된 성별 사용
    String exerciseType = selectedExerciseType ?? '';

    try {
      var url = Uri.parse('http://localhost:8080/api/auth/signup');
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
                      Navigator.pushReplacement(
                        context,
                        MaterialPageRoute(builder: (context) => LoginPage()),
                      ); // 로그인 페이지로 이동합니다.
                    },
                    child: Text('확인'),
                  ),
                ],
              );
            },
          );
        }
      } else {
        throw Exception('Failed to sign up.');
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
}
