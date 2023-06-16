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

- - - - - - - - - - - - - - - - - - - - - - - - - - - -

## 기존 프로젝트 분석

### [SmartCart](https://github.com/CSID-DGU/2020-1-OSSP1-savezone-6)


<br/>

## 프로젝트 변경점
#### 1. 클래스별 보관방법 및 추천 보관방법 제시
   * 기존 프로젝트 
      - 앱 실행 시 클래스당 1개의 보관 정보(식품명, 보관방법, 보관기간) 입력<br/>
         [SmartCart/CartList.java](https://github.com/CSID-DGU/2020-1-OSSP1-savezone-6/blob/master/app/src/main/java/com/teamsavezone/smartcart/CartList.java)
          ```
           if(!MyApplication.initExp){
                //ExpList 테이블 생성
                addFood("사과", 1, 21);
                addFood("바나나", 2, 21);
                addFood("오이", 1, 7);
                addFood("양파", 1, 4);
                addFood("무", 1, 7);
                addFood("딸기", 1, 3);
                MyApplication.initExp = true;
            }
          ```

   * 변경점
      - 별도의 파일로 보관방법 정리 ([itemInfo.json](https://github.com/CSID-DGU/2023-1-OPPS1-AUSOL-6/blob/main/app/src/main/assets/itemInfo.json))
      - 클래스별 상온,냉장,냉동 3개의 보관방법 및 보관기간 제시

#### 2. 식품 리스트 출력 방식 변경

   * 기존 프로젝트
     - 저장된 순서대로 식품 출력<br/>
         <img height="500" src="https://github.com/CSID-DGU/2023-1-OPPS1-AUSOL-6/assets/24996261/7567b75f-2322-4001-bbbb-2258da90fa47">
   * 변경점
        * 보관방법별 다른 레이아웃에 출력
        * 유통기한 기준 오름차순 출력
     
     
<br/>

## 실행화면


- - - - - - - - - - - - - - - - - - - - - - - - - - - -





    

    

    
