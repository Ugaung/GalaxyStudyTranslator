# 갤럭시 탭 + Chrome만으로 GitHub Actions APK 빌드

이 프로젝트는 **일반 영어 학습용 화면 번역 앱**의 Android 소스코드입니다. Chrome 확장 프로그램을 APK로 변환하는 것은 아니며, 별도 안드로이드 앱입니다. 시험/평가 중에는 사용하지 마세요.

## 준비
1. 갤럭시 탭의 Chrome에서 github.com 로그인.
2. github.com/new 에서 `GalaxyStudyTranslator` 저장소를 생성 (README 자동 추가하지 않아도 됨).
3. ZIP을 내 파일에서 압축 해제.
4. Chrome > 점 3개 > 데스크톱 사이트. 저장소에서 **Add file > Upload files**를 열고 프로젝트 파일을 업로드합니다. 모바일 파일 선택기에서는 폴더 업로드가 제한될 수 있습니다. ZIP 자체만 올리면 빌드되지 않습니다. 폴더 구조를 유지하지 못하면 GitHub 웹 편집기의 **Add file > Create new file**로 아래 상대경로를 포함하여 각 파일을 생성하고 내용을 붙여 넣으세요.

필수 파일 목록:
- `.github/workflows/build-apk.yml`
- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle.properties`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/example/galaxystudy/MainActivity.java`
- `app/src/main/java/com/example/galaxystudy/CaptureService.java`

5. **Commit changes** 클릭. 저장소에서 Actions 탭을 열어 워크플로 사용 승인 요청이 있다면 승인.
6. **Build Android APK** > **Run workflow** > Run workflow. `main` 브랜치에 push했다면 자동 실행될 수도 있습니다.
7. 성공(초록 체크)한 실행 상세의 **Artifacts**에서 `GalaxyStudyTranslator-debug-apk` 다운로드. ZIP을 풀면 `app-debug.apk`가 나옵니다.
8. 갤럭시 탭의 내 파일에서 APK를 선택하고, 필요한 경우 해당 파일 관리자에 '이 출처 허용' 권한을 부여한 후 설치합니다.

## 오류 해결
- Actions 탭에 항목이 없으면 `.github/workflows/build-apk.yml`의 경로를 확인합니다.
- 빌드가 실패하면 Actions > 실패한 실행 > build > 빨간 단계의 로그를 확인합니다.
- GitHub는 소스코드 빌드만 수행하며, APK 실행/화면 공유/번역 품질은 실제 기기에서 별도 검증해야 합니다.
- 화면 공유 및 다른 앱 위에 표시 권한이 필요하며, 앱이 실제로 작동하는지 보장되지 않습니다.
