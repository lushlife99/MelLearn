# MelLearn

<div align="center">
  <h3>Front-end</h3>
  <img src="https://img.shields.io/badge/Spotify%20API-1ED760?style=for-the-badge&logo=spotify&logoColor=white" alt="Spotify API">
  <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black" alt="React">
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript">
  <img src="https://img.shields.io/badge/Redux--Toolkit-764ABC?style=for-the-badge&logo=redux&logoColor=white" alt="Redux Toolkit">
  <img src="https://img.shields.io/badge/TailwindCSS-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white" alt="TailwindCSS">

  <h3>Backend</h3>

  
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="JPA">
  <img src="https://img.shields.io/badge/QueryDSL-005E95?style=for-the-badge&logo=code&logoColor=white" alt="QueryDSL">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker">
  <img src="https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white" alt="Nginx">
  <img src="https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white" alt="Jenkins">
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis">
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT">
  
  <h3>🎵 음악 감상과 언어 학습을 결합한 웹 애플리케이션 🎵</h3>
  
  [데모 영상](https://www.youtube.com/watch?v=SIQpdw7hlRE) • 
  [피그마 디자인](https://www.figma.com/file/7MYcOTyrKp69eeh8qOzWSL/music-popsong-education?type=design&node-id=0%3A1&mode=design&t=B708KM8UcvER6kWq-1)

</div>

## 📋 프로젝트 소개

**MelLearn**은 **음악 감상과 언어 학습을 결합한 웹 애플리케이션**입니다. 대학교 캡스톤 디자인 과정에서 진행된 이 프로젝트는 Spotify API와 인공지능 기술을 활용하여, 사용자가 좋아하는 음악을 들으며 자연스럽게 언어를 학습할 수 있는 환경을 제공합니다.

- **개발 기간**: 2024.03 ~ 2024.05
- **최종 평가**: A+

## 👨‍💻 팀원 소개

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/lushlife99">
        <img src="https://avatars.githubusercontent.com/lushlife99" width="100px;" alt="정찬"/>
        <br />
        <sub><b>정찬</b></sub>
      </a>
      <br />
      <sub>백엔드, AI</sub>
    </td>
    <td align="center">
      <a href="https://github.com/hyomin1">
        <img src="https://avatars.githubusercontent.com/hyomin1" width="100px;" alt="이효민"/>
        <br />
        <sub><b>이효민</b></sub>
      </a>
      <br />
      <sub>프론트엔드</sub>
    </td>
    <td align="center">
      <a href="https://github.com/Hodu-moon">
        <img src="https://avatars.githubusercontent.com/Hodu-moon" width="100px;" alt="문영호"/>
        <br />
        <sub><b>문영호</b></sub>
      </a>
      <br />
      <sub>프론트엔드</sub>
    </td>
    <td align="center">
      <a href="https://github.com/DDOONNGGUK">
        <img src="https://avatars.githubusercontent.com/DDOONNGGUK" width="100px;" alt="김동욱"/>
        <br />
        <sub><b>김동욱</b></sub>
      </a>
      <br />
      <sub>AI</sub>
    </td>
  </tr>
</table>

## 🛠️ 기술 스택

<table>
  <tr>
    <td><b>인프라</b></td>
    <td>Docker, Nginx, AWS, Jenkins</td>
  </tr>
  <tr>
    <td><b>프론트엔드</b></td>
    <td>React, TypeScript, Redux-Toolkit, TailwindCSS, Redux</td>
  </tr>
  <tr>
    <td><b>백엔드</b></td>
    <td>Springboot, Spring Data JPA, Querydsl</td>
  </tr>
  <tr>
    <td><b>데이터베이스</b></td>
    <td>Mysql, Redis</td>
  </tr>
  

</table>

## 📊 시스템 아키텍처

<div align="center">
  <img width="1071" height="568" alt="image" src="https://github.com/user-attachments/assets/e76f7ad1-f4be-43e4-a70c-82c2f011e64e" />
</div>

## 🗃️ ERD

<div align="center">
  <img src="https://github.com/lushlife99/MelLearn/assets/101994803/7628dd76-05d8-46d1-b763-b3b2ce05d2ed" alt="ERD" width="80%">
</div>

## ✨ 주요 기능

### 1️⃣ Spotify 통합 및 인증
- Spotify SDK를 활용한 OAuth 2.0 인증 프로세스 구현
- PKCE(Proof Key for Code Exchange) 방식 적용으로 보안 강화
- 사용자 플레이리스트 접근 및 음원 스트리밍 기능 구현

### 2️⃣ 실시간 가사 트래킹
- Spotify Scrapper API를 활용한 실시간 가사 데이터 및 타임스탬프 정보 수신
- 재생 시간에 맞춰 가사 텍스트를 동기화하여 실시간 렌더링

### 3️⃣ 웹 오디오 녹음 시스템
- Web Audio API와 MediaRecorder API를 활용한 고품질 오디오 캡처 구현
- 실시간 음성 시각화로 사용자 피드백 강화
- 녹음된 오디오 데이터를 서버로 전송하여 평가 및 분석

### 4️⃣ 다양한 언어 학습 콘텐츠
- Speaking, Listening, Vocabulary, Reading, Grammar 5가지 유형의 문제 제공
- 가사 기반 맥락 학습 및 맞춤형 오답 해설

### 5️⃣ 학습 관리 시스템
- 사용자 학습 이력 및 진행 상황 추적
- 모의고사 형식의 종합 평가 기능

## 🏆 성과 및 배운 점
- 반응형 웹 디자인 적용으로 다양한 디바이스에서 최적화된 사용자 경험 제공
- Spotify API 및 Web Audio API와 같은 외부 API 통합 경험 축적
- 최종 평가: A+ (반응형 웹 구현에 대한 높은 평가)

## 🎬 시연 영상

[![MelLearn 데모 영상](https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=youtube&logoColor=white)](https://www.youtube.com/watch?v=SIQpdw7hlRE)
