# SFC를 활용한 피트니스 파트너 매칭 어플리케이션

## 시스템 아키텍쳐(3 tier architecture)

![image](https://github.com/user-attachments/assets/cd3affa9-49b4-4b86-922d-b288e507f6ef)

1. **프레젠테이션 계층** : 플러터
2. **어플리케이션 계층**: 자바 스프링 부트(jdk 17)
3. **데이터 계층**: 오라클 데이터베이스(테스트 환경은 h2)

## 서버 구성

서버는 다음과 같이 3개로 구성된다.

1. **Security 서버**:
    - DDoS 및 비정상적인 트래픽을 탐지 및 차단한다.
    - 사용자 요청을 검증하고 안전한 데이터를 인증 서버로 전달한다.
2. **Login 서버**:
    - JWT(Json Web Token)를 생성하고, 사용자의 로그인 및 인증을 처리한다.
    - 인증되지 않은 접근을 차단한다.
3. **Main 서버**:
    - 사용자와의 상호작용을 처리하며, 매칭 알고리즘을 실행한다.
    - 데이터베이스와 직접 연결되어 피트니스 파트너 정보 및 매칭 데이터를 관리한다.
  
## Docker

1. 도커 네트워크 생성
   
```
docker network create --subnet=172.18.0.0/16 fitness-net
```

2. - **컨테이너 실행 및 네트워크 설정**:
    
| **컨테이너 이름** | **포트 매핑** | **IP 주소** | **역할** |
| --- | --- | --- | --- |
| fitness-security | 8082:8080 | 172.18.0.4 | 비정상 트래픽 탐지 및 차단 |
| fitness-login | 8081:8080 | 172.18.0.3 | 사용자 인증 및 JWT 생성 |
| fitness-main | 8080:8080 | 172.18.0.2 | 매칭 알고리즘 및 사용자 상호작용 처리 |

```
docker run -d --name fitness-security -p 8082:8080 --net fitness-net --ip 172.18.0.4 fitness-security
docker run -d --name fitness-login -p 8081:8080 --net fitness-net --ip 172.18.0.3 fitness-login
docker run -d -p 8080:8080 -v C:/FitnessImage:/app/FitnessImage --name fitness-main --net fitness-net --ip 172.18.0.2 fitness-main
```

## SFC 적용화면
### 정상 요청

왼쪽 위 보안 서버, 왼쪽 아래 로그인 서버, 오른쪽 메인 서버

1. 서버 초기 실행 화면

![image](https://github.com/user-attachments/assets/5e75f1d5-b97a-411d-85ba-ece4727b8f76)

1. 정상적으로 로그인 후 메인 서버 사용

![image](https://github.com/user-attachments/assets/7998f79f-adba-467e-bbab-e1bd86ef0f26)

보안 서버에서 다른 서버로 패킷을 전달함

### 일정 시간 안에 일정 횟수 이상 로그인을 시도할 때

![image](https://github.com/user-attachments/assets/208d6884-6129-486d-9c4c-8b758e7b3c8c)


ip를 차단하고 더 이상 다른 서버에 패킷을 전달하지 않는다.

### 특정 ip 차단

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/74aed667-2d91-49a8-9181-6404c1afa8e5/e5069b70-0582-4d5b-8c5c-c9cf05ae2048/image.png)

차단된 ip에서 요청이 올 경우 보안 서버에서 패킷을 드랍함




