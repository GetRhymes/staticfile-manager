
Deployment:
```
```

Storage structure:
```
  /help
    |- welcome.html
    |- idea
    |   |- 0.0.1
    |      |- welcome.html
    |      |- point1.html
    |      |- point2.html
    |   |- 0.0.2
    |      |- welcome.html
    |      |- point1.html
    |      |- point2.html
    |- tools
    |   |- tools.json
    |- kotlin
    |   |- welcome.html
    |   |- features
    |       |- feature1.html
    |       |- feature2.html
```

Test cases:

    Case №1
        http://localhost:8080/help -> http://localhost:8080/help/welcome.html

    Case №2
        http://localhost:8080/help/idea -> http://localhost:8080/help/idea/0.0.2/welcome.html
        http://localhost:8080/help/idea/0.0.1 -> http://localhost:8080/help/idea/0.0.1/welcome.html
        http://localhost:8080/help/idea/0.0.1/point1.html -> http://localhost:8080/help/idea/0.0.1/point1.html

    Case №3
        http://localhost:8080/help/tools -> http://localhost:8080/help/tools/tools.json

    Case №4
        http://localhost:8080/help/kotlin -> http://localhost:8080/help/kotlin/welcome.html
        http://localhost:8080/help/kotlin/features -> http://localhost:8080/help/kotlin/features/feature1.html
        http://localhost:8080/help/kotlin/features/feature1.html -> http://localhost:8080/help/kotlin/features/feature1.html
        http://localhost:8080/help/kotlin/features/feature2.html -> http://localhost:8080/help/kotlin/features/feature2.html
    
    Case №5
        http://localhost:8080/help/kotlin/sdk -> http://localhost:8080/help/not-found.html
        http://localhost:8080/help/0.0.1 -> http://localhost:8080/help/not-found.html
        http://localhost:8080/help/feature1.html -> http://localhost:8080/help/not-found.html