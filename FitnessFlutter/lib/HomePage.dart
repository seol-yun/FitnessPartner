import 'package:flutter/material.dart';
import 'PartnerMatchingPage.dart';

class HomePage extends StatefulWidget {
  final String token;
  final Function(String, String, bool) onNavigateToMatching;

  HomePage({required this.token, required this.onNavigateToMatching});

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final List<String> iconsLabels = [
    '런닝', '싸이클', '수영', '골프', '테니스', '배드민턴',
    '요가', '헬스', '필라테스', '크로스핏', '등산', '기타'
  ];

  final List<IconData> icons = [
    Icons.directions_run,          // '런닝'
    Icons.pedal_bike,              // '싸이클'
    Icons.pool,                    // '수영'
    Icons.sports_golf,             // '골프'
    Icons.sports_tennis,           // '테니스'
    Icons.flash_on,                // '배드민턴'
    Icons.self_improvement,        // '요가'
    Icons.fitness_center,          // '헬스'
    Icons.accessibility_new,       // '필라테스'
    Icons.sports_handball,         // '크로스핏'
    Icons.terrain,                 // '등산'
    Icons.more_horiz,              // '기타'
  ];

  String? selectedCity;
  String? selectedDistrict;
  bool isExpertMatching = false;

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

  void showLocationDialog(String exerciseType) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('$exerciseType 위치 선택'),
          content: StatefulBuilder(
            builder: (BuildContext context, StateSetter setState) {
              return Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  DropdownButtonFormField<String>(
                    decoration: InputDecoration(labelText: '시/도'),
                    value: selectedCity,
                    onChanged: (value) {
                      setState(() {
                        selectedCity = value;
                        selectedDistrict = null;
                      });
                    },
                    items: cities.map((city) => DropdownMenuItem(
                      value: city,
                      child: Text(city),
                    )).toList(),
                  ),
                  DropdownButtonFormField<String>(
                    decoration: InputDecoration(labelText: '시/군/구'),
                    value: selectedDistrict,
                    onChanged: (value) {
                      setState(() {
                        selectedDistrict = value;
                      });
                    },
                    items: selectedCity != null && districts.containsKey(selectedCity)
                        ? districts[selectedCity]!.map((district) => DropdownMenuItem(
                      value: district,
                      child: Text(district),
                    )).toList()
                        : [],
                  ),
                  CheckboxListTile(
                    title: Text("전문가 매칭"),
                    value: isExpertMatching,
                    onChanged: (value) {
                      setState(() {
                        isExpertMatching = value ?? false;
                      });
                    },
                  ),
                ],
              );
            },
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: Text('취소'),
            ),
            TextButton(
              onPressed: () {
                Navigator.pop(context);
                widget.onNavigateToMatching(
                  exerciseType,
                  '${selectedCity ?? ''} ${selectedDistrict ?? ''}'.trim(),
                  isExpertMatching,
                );
              },
              child: Text('확인'),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: GridView.builder(
        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 4,
          childAspectRatio: 1,
        ),
        itemCount: icons.length,
        itemBuilder: (context, index) {
          return GestureDetector(
            onTap: () => showLocationDialog(iconsLabels[index]),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(icons[index], size: 50),
                SizedBox(height: 5),
                Text(iconsLabels[index]),
              ],
            ),
          );
        },
      ),
    );
  }
}
