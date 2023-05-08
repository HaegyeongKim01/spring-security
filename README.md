# Spring Security with session 
### ⚙️개발 환경
**Oracle OpenJDK version 17.0.5   
빌드관리도구: Maven**

### 📚시나리오
**index.html**   
👇 이름과 권한 출력. 로그인이 안 되어있는 경우 anonymousUser Default 출력<img src = "https://user-images.githubusercontent.com/110768149/236743008-71d2fea8-07e5-45e1-b773-402b8a8b64b4.png"/>     
    
       
👇 `/me` 로 접근 시 login page로 우회, 로그인 시 로그인된 사용자 정보나온다.   
<img src = "https://user-images.githubusercontent.com/110768149/236743033-f7c5fcdc-57d8-4f84-9890-cfa64b0f9631.png">   
**👩‍👩‍👧‍👦사용자 등록**       
application.yml 파일에 security: user: 로 등록이 가능하기도 하지만 `@Configuration` `@EnableWebSecurity` 어노테이션을 가지고 WebSecurityConfigurerAdapter를 상속 받는 클래스에서 auth.inMemoryAuthentication().withUser로 로그인 가능한 사용자들을 등록해주었다.    

**`/asyncHello`**   
👇 Simplecontroller에 GetMapping으로 되어있다. 
로그인 되어있지 않으면 오류 page 발생, log로 찍어서 확인하도록 함. 로그인 되어있다면 Hello <사용자id> 출력
<img src = "https://user-images.githubusercontent.com/110768149/236743031-d1a1b77f-4317-4c3a-8c40-c5a87a8f1586.png">   

**WebSecurityConfigurer**   
👇 WebSecurityConfigrurerAdapter를 상속받는 클래스 
로그인 성공의 경우 path 지정, Cookie 기반의 자동 로그인, BasicAuthenticationFilter 적용, 예외처리 핸들러, 로그아웃 등 설정    
   
`/me`는 USER, ADMIN권한을 가진 경우만 접근이 가능하도록, `/admin`은 ADMIN 권한이 있어야 이 page를 호출할 수 있도록 코드 작성    
AccessDeniedHandler accessDeniedHandler() @Bean으로 등록하여 사용    
<img src = "https://user-images.githubusercontent.com/110768149/236743035-41c61b6d-ae2e-4b51-be9a-eea510fe5616.png">
<img src = "https://user-images.githubusercontent.com/110768149/236743038-2e12a7a3-fa64-426c-9de8-6aa27d82eae5.png">
