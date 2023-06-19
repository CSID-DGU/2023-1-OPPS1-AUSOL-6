![header](https://capsule-render.vercel.app/api?type=waving&color=gradient&height=300&section=header&text=Keep%20Fresh&fontSize=90&)
# 2023-1-OPPS1-AUSOL-6
2023년 1학기 공개SW프로젝트_01 아우솔 6조
<br/><br/>

### 프로젝트 소개
<div>
<h4>KeepFresh - 유통기한 관리 애플리케이션</h4>
</div>
<br/>

### 팀원
|학번|이름|학과|역할|
|----|---|---|---|
|2019112001|김진우 [@jinwoo1234](https://github.com/jinwoo1234)|컴퓨터공학전공|팀장, Frontend|
|2019111098|이인수 [@MaccBass](https://github.com/MaccBass)|컴퓨터공학전공|Frontend|
|2019112043|윤정윤 [@YoonJeongyoon](https://github.com/Yoonjeongyoon)|컴퓨터공학전공|Frontend, 모델구축|
|2019112047|백승진 [@naver0504](https://github.com/naver0504)|컴퓨터공학전공|모델서빙|
|2018110571|전은규 [@EunkyuJeon](https://github.com/Eunkyu-Jeon)|컴퓨터공학전공|모델서빙|
<br/>

### 개발환경

<img src="https://img.shields.io/badge/Android Studio-CC6699?style=flat-square&logo=Android Studio&logoColor=#ffffff"/> <img src="https://img.shields.io/badge/Realm-09D3AC9?style=flat-square&logo=Realm&logoColor=#39477F"/> <img src="https://img.shields.io/badge/Pycharm-EE4C2C?style=flat-square&logo=pycharm&logoColor=#ffffff"/> <img src="https://img.shields.io/badge/amazonaws-FF9900?style=flat-square&logo=amazonaws&logoColor=#ffffff"/> <img src="https://img.shields.io/badge/fastapi-009688?style=flat-square&logo=fastapi&logoColor=#ffffff"/>

| **Component**  | **Version** |
| :---  | :------ |
| Android Studio | 7.4.2 |
| Android SDK(API Level) | Android 11.0(30)+ |
| Realm Database | 10.16.0 |

<br/>

- - - - - - - - - - - - - - - - - - - - - - - - - - - -

## 기존 프로젝트

### [" 똑똑한 소비 습관, SmartCart "](https://github.com/CSID-DGU/2020-1-OSSP1-savezone-6)
<br/>

## 기능 설명

### 1. 클래스별 보관방법 및 추천 보관방법 제시
  - 별도의 파일로 보관방법 정리 ([itemInfo.json](https://github.com/CSID-DGU/2023-1-OPPS1-AUSOL-6/blob/main/app/src/main/assets/itemInfo.json))
    
  - 클래스별 상온,냉장,냉동 3개의 보관방법 및 보관기간 제시

  	![image](https://github.com/CSID-DGU/2023-1-OSSP1-AUSOL-6/assets/24996261/a30634a5-2e43-43a0-9ca6-d739dad23490)

<br/>

### 2. 식품 리스트 출력 방식

  - 보관방법별 다른 레이아웃에 출력
	
    ![image](https://github.com/CSID-DGU/2023-1-OSSP1-AUSOL-6/assets/24996261/5375435d-544c-430d-bc44-828ba73bd62d)
    
  - 유통기한 기준 오름차순 출력
	
     ![image](https://github.com/CSID-DGU/2023-1-OSSP1-AUSOL-6/assets/24996261/62bf60e8-96a2-4b25-90f3-d25594c40c75)

<br/>

### 3. 물품 보관방법별 리스트 출력

  - 직접 추가 
    - 직접입력시 식품명(String), 유통기한(Date), 보관방법(int)을 입력받아서 realm Database에 추가
      
  	![image](https://github.com/CSID-DGU/2023-1-OSSP1-AUSOL-6/assets/24996261/4ea6a62f-6715-49c5-8f19-4c66191079e1)
        
  - 사진으로 추가 
    - 모델 추론 결과가 식품명으로 입력됨
    - 해당 클래스의 추천 보관방법을 팝업창으로 보여준 후 보관방법 선택 시 현재 날짜 기준으로 유통기한을 계산해서 제공
    - 직접 추가 페이지의 빈 내용 대신 제공된 정보로 채워지고 사용자가 직접 수정 가능
      
  	![image](https://github.com/CSID-DGU/2023-1-OSSP1-AUSOL-6/assets/24996261/88d6a70f-e1eb-48b5-ad76-39631c3d7d6c)
    
<br/>
        
### 4. 알람 설정

  - 식품 리스트 DB에 저장된 식품 중 유통기한이 7일 이하로 남은 식품들의 정보를 지정한 시간에 알림 형식으로 발송한다.
  - 알람은 만료 며칠 전, 몇시에 발송할 것인지에 대해 입력 받는다.

	![image](https://github.com/CSID-DGU/2023-1-OSSP1-AUSOL-6/assets/24996261/f42bb8f7-d83c-4148-b62c-f542d53b70e0) ![image](https://github.com/CSID-DGU/2023-1-OSSP1-AUSOL-6/assets/24996261/0199646c-0f0c-46d7-ae84-89c6769fe3d5)
     
<br/>


- - - - - - - - - - - - - - - - - - - - - - - - - - - -

