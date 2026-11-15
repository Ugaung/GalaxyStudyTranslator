# Galaxy Study Translator — GitHub Actions APK builder

일반 학습용 화면 OCR 및 한국어 번역 오버레이 Android 앱 예제입니다.

## 갤럭시만으로 빌드하기

1. GitHub에서 **새 public 또는 private repository**를 만듭니다. README 자동 추가는 선택하지 않아도 됩니다.
2. 이 ZIP을 **압축 해제**하고, `app/`, `.github/`, `build.gradle.kts`, `settings.gradle.kts`, `README.md`를 **저장소 최상위**에 업로드합니다. `.github` 폴더가 숨겨져 있으면 파일 관리자에서 숨김 파일 표시를 켜세요.
   - 모바일 GitHub 웹에서 폴더 업로드가 어렵다면 Chrome의 **데스크톱 사이트**를 켜거나 GitHub 웹 편집기 `github.dev`를 이용하세요.
   - 중요한 점: GitHub에 ZIP 파일 하나만 올리면 빌드되지 않습니다.
3. GitHub 저장소에서 **Actions** 탭 → **Build Android APK** → **Run workflow** → **Run workflow**.
4. 실행 항목을 열어 초록색 성공 표시를 확인합니다.
5. 페이지 아래 **Artifacts**에서 `GalaxyStudyTranslator-debug-apk`를 다운로드합니다. 다운로드된 ZIP의 압축을 풀면 `app-debug.apk`가 있습니다.
6. 갤럭시에서 APK를 열고 설치 권한을 허용합니다. Android의 '자동 차단' 설정이 설치를 막을 수 있으며 보안 경고는 확인 후 신중히 진행하세요.

## 참고

- **빌드 성공 및 실기기 동작은 아직 검증되지 않았습니다.** GitHub Actions에서 오류가 나면 실패한 단계의 로그를 확인해야 합니다.
- 앱은 일반 학습용이며 시험·평가 중 사용하지 마세요.
- 화면 공유, 다른 앱 위에 표시 권한이 필요합니다. 일부 앱은 화면 캡처를 제한합니다.
- 첫 실행 시 번역 모델 다운로드가 필요합니다.
- Debug APK는 개인 테스트용으로만 사용하세요. 공개 배포용 서명 릴리스가 아닙니다.
