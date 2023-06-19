![header](https://capsule-render.vercel.app/api?type=waving&color=gradient&height=300&section=header&text=Keep%20Fresh&fontSize=90&)
# 2023-1-OPPS1-AUSOL-6
2023년 1학기 공개SW프로젝트_01 아우솔 6조
<br/><br/>

### 프로젝트 소개
<div>
<h4>KeepFresh - 유통기한 관리 애플리케이션</h4>
</div>
<br/>

### 개발환경

| **Component**  | **Version** |
| :---  | :------ |
| Android Studio | 7.4.2 |
| Android SDK(API Level) | Android 11.0(30)+ |
| Realm Database | 10.16.0 |
| nvidia-triton | - |
| YOLO | v8 |
<br/>

### 팀원
|학번|이름|학과|역할|
|----|---|---|---|
|2019112001|김진우 [@jinwoo1234](https://github.com/jinwoo1234)|컴퓨터공학전공|팀장, Frontend|
|2019111098|이인수 [@MaccBass](https://github.com/MaccBass)|컴퓨터공학전공|Frontend|
|2019112043|윤정윤 [@YoonJeongyoon](https://github.com/Yoonjeongyoon)|컴퓨터공학전공|Frontend, 모델구축|
|2019112047|백승진 [@naver0504](https://github.com/naver0504)|컴퓨터공학전공|Backend, 모델서빙|
|2018110571|전은규 [@EunkyuJeon](https://github.com/Eunkyu-Jeon)|컴퓨터공학전공|Backend, 모델서빙|
<br/>

<img src="https://img.shields.io/badge/Android Studio-CC6699?style=flat-square&logo=Android Studio&logoColor=#ffffff"/> <img src="https://img.shields.io/badge/Realm-09D3AC9?style=flat-square&logo=Realm&logoColor=#39477F"/> <img src="https://img.shields.io/badge/Pycharm-EE4C2C?style=flat-square&logo=pycharm&logoColor=#ffffff"/> <img src="https://img.shields.io/badge/amazonaws-FF9900?style=flat-square&logo=amazonaws&logoColor=#ffffff"/> <img src="https://img.shields.io/badge/fastapi-009688?style=flat-square&logo=fastapi&logoColor=#ffffff"/>
- - - - - - - - - - - - - - - - - - - - - - - - - - - -

## 기존 프로젝트 분석

### [SmartCart](https://github.com/CSID-DGU/2020-1-OSSP1-savezone-6)


<br/>

## 기능 설명
#### 1. 클래스별 보관방법 및 추천 보관방법 제시
    - 별도의 파일로 보관방법 정리 ([itemInfo.json](https://github.com/CSID-DGU/2023-1-OPPS1-AUSOL-6/blob/main/app/src/main/assets/itemInfo.json))
    - 클래스별 상온,냉장,냉동 3개의 보관방법 및 보관기간 제시

#### 2. 식품 리스트 출력 방식 변경
    - 보관방법별 다른 레이아웃에 출력
    - 유통기한 기준 오름차순 출력
     
     
<br/>

## 실행화면
![KakaoTalk_20230619_112850258](https://github.com/CSID-DGU/2023-1-OPPS1-AUSOL-6/assets/126742414/22717661-91de-4ed1-9e07-e4df5c738020) ![KakaoTalk_20230619_112850258_01](https://github.com/CSID-DGU/2023-1-OPPS1-AUSOL-6/assets/126742414/9dab4c27-64dd-48b1-89ec-2a29eb65676c)

앱에서 사진을 촬영하면 모델 서버에 사진을 보내고, 서버에서는 식품 객체를 인식해 그의 클래스 번호를 앱으로 불러온다.
불러온 클래스 번호에 따라 보관 방법을 제시하고 유통기한과 보관 팁을 알려준다.

![KakaoTalk_20230619_112850258_03](https://github.com/CSID-DGU/2023-1-OPPS1-AUSOL-6/assets/126742414/a123f31d-e477-43dd-81ec-2e122628a015) ![KakaoTalk_20230619_112850258_04](https://github.com/CSID-DGU/2023-1-OPPS1-AUSOL-6/assets/126742414/1e63d587-b105-4235-be5a-e3f275bd68c7)

보관 방법을 선택하면 저장 항목에 선택한 내용이 자동으로 입력된다.

![KakaoTalk_20230619_113110063](https://github.com/CSID-DGU/2023-1-OPPS1-AUSOL-6/assets/126742414/15aea568-8443-4596-8668-9c38384d42c2) ![KakaoTalk_20230619_112850258_01](https://github.com/CSID-DGU/2023-1-OPPS1-AUSOL-6/assets/126742414/d4f7d323-8e70-4013-a861-92aebed83a9d) ![KakaoTalk_20230619_112703623](https://github.com/CSID-DGU/2023-1-OPPS1-AUSOL-6/assets/126742414/1e540110-2973-4429-9006-20a3c7300581)

사진 촬영 없이 직접 정보를 입력할 수도 있다. 입력한 정보는 사진 촬영할 때와 똑같이 리스트에 저장된다.




- - - - - - - - - - - - - - - - - - - - - - - - - - - -





    

    

    
