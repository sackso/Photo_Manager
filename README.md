# 스마트 갤러리 앱

안드로이드 스마트기기의 이미지를 관리하고 GPS 정보를 활용한 지도 기능을 제공하는 앱입니다.

## 주요 기능

### 1. 사진 갤러리
- 현재 스마트기기의 모든 사진을 리스트로 표시
- 사진 클릭 시 확대 이미지 보기
- 이미지에 대한 설명 입력 및 저장 (3줄 이내)
- 같은 이름의 txt 파일이 있으면 자동으로 설명 로드
- 설명 저장 시 이미지와 같은 이름의 txt 파일 생성

### 2. 지도 기능
- GPS 정보가 있는 이미지만 필터링하여 표시
- 이미지 클릭 시 구글 맵에서 해당 위치 표시
- 지도 위에 이미지 썸네일을 마커로 표시
- 구글 맵 URL 링크 생성으로 외부 맵 앱 연동

## 기술 스택

- **언어**: Java
- **최소 SDK**: API 24 (Android 7.0)
- **타겟 SDK**: API 34 (Android 14)
- **라이브러리**:
  - Google Maps API
  - Glide (이미지 로딩)
  - Material Design Components
  - AndroidX

## 프로젝트 구조

```
app/src/main/java/com/example/myapplication/
├── MainActivity.java              # 메인 액티비티 (탭 구조)
├── GalleryActivity.java           # 갤러리 액티비티
├── ImageDetailActivity.java       # 이미지 상세보기 액티비티
├── MapActivity.java               # 지도 액티비티
├── model/
│   ├── GPSData.java              # GPS 정보 모델
│   └── ImageData.java            # 이미지 정보 모델
├── adapter/
│   ├── ImageAdapter.java         # 갤러리 이미지 어댑터
│   └── MapImageAdapter.java      # 지도 이미지 어댑터
├── utils/
│   ├── FileUtils.java            # 파일 처리 유틸리티
│   ├── GPSUtils.java             # GPS 처리 유틸리티
│   └── ImageUtils.java           # 이미지 처리 유틸리티
└── fragment/
    ├── GalleryFragment.java      # 갤러리 프래그먼트
    └── MapFragment.java          # 지도 프래그먼트
```

## 필요한 권한

- `READ_EXTERNAL_STORAGE`: 이미지 읽기
- `WRITE_EXTERNAL_STORAGE`: 설명 파일 저장 (API 28 이하)
- `ACCESS_FINE_LOCATION`: GPS 정보 접근
- `ACCESS_COARSE_LOCATION`: 대략적 위치 정보
- `INTERNET`: 구글 맵 API 사용
- `ACCESS_NETWORK_STATE`: 네트워크 상태 확인

## 설정 방법

### 1. Google Maps API Key 설정
1. [Google Cloud Console](https://console.cloud.google.com/)에서 프로젝트 생성
2. Maps SDK for Android API 활성화
3. API Key 생성
4. `app/src/main/AndroidManifest.xml`에서 `YOUR_GOOGLE_MAPS_API_KEY`를 실제 API Key로 교체

### 2. 빌드 및 실행
```bash
# 프로젝트 빌드
./gradlew build

# 디버그 APK 생성
./gradlew assembleDebug

# 릴리즈 APK 생성
./gradlew assembleRelease
```

## 주요 클래스 설명

### ImageData
이미지의 메타데이터를 저장하는 모델 클래스
- 이미지 경로, 이름, 크기, 해상도
- GPS 정보 (GPSData 객체)
- 설명 텍스트
- 파일 정보 (촬영 날짜, MIME 타입 등)

### GPSData
GPS 좌표 정보를 저장하는 모델 클래스
- 위도, 경도, 고도
- 위치 이름
- 구글 맵 URL 생성 기능

### ImageUtils
이미지 관련 유틸리티 함수들
- 갤러리에서 모든 이미지 가져오기
- GPS가 있는 이미지 필터링
- 썸네일 생성
- 이미지 회전 보정

### GPSUtils
GPS 관련 유틸리티 함수들
- EXIF 데이터에서 GPS 정보 추출
- DMS(도분초) 형식을 DD(십진도) 형식으로 변환
- 구글 맵 URL 생성
- 좌표 유효성 검사

## 사용법

1. **갤러리 보기**: 하단 탭에서 "사진" 선택
2. **이미지 상세보기**: 갤러리에서 이미지 클릭
3. **설명 편집**: 상세보기 화면에서 설명 입력 후 "설명 저장" 버튼 클릭
4. **지도 보기**: 하단 탭에서 "지도" 선택
5. **GPS 위치 확인**: 지도에서 이미지 클릭하여 해당 위치로 이동

## 주의사항

- Android 13+ (API 33) 이상에서는 `READ_MEDIA_IMAGES` 권한 사용 권장
- 대용량 이미지 처리 시 메모리 관리 주의
- GPS 정보가 없는 이미지는 지도에서 표시되지 않음
- 설명 파일은 이미지와 같은 디렉토리에 저장됨

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
