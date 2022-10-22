Сервис раздачи статических файлов

Стек: Kotlin, Spring-Boot, Docker, AmazonS3, MinIO

Описание: 

Данный сервис позволяет раздавать статические файлы, сохраненные в хранилище (MinIO, AmazonS3).
    
Сервис отлавливает GET запросы (с помощью реализованного контроллера FileManagerController.kt) и передает необходимую информацию адаптеру. 
   
Адаптер в зависимости от полученной информации возвращает:
- Запрашиваемый файл 
- Путь для редиректа
- Старницу (404 - Not found)
   
Если адаптер вернул страницу, то она отображается у пользователя.

Если адаптер вернул редирект путь, то сервис сам выполняет переадресацию на нужную страницу и в конечном счете возращает результат пользователю
    
Если адаптер вернут страницу 404, то она отображается у пользователя вместе с соотвествующим http статусом.
    
Помимо статических файлов в хранилище также находятся файлы с мета-информацией (META-INF.yaml), данный файл выглядит следующим образом:
```
current-node: '/'
*actual-node: '0.0.1'
*welcome-page: '/welcome.html'
```
Поля, почеменные "*" могут быть только в одном экземпляре (в данном случае либо "actual-node" либо "welcome-page").

- current-node - информация о текуще местоположении 
- actual-node - информация о директории являющейся актуальной, например, для версионирования выделяется актуальная версия продукта, соответственно в данном поле должен быть указан путь к директории с актуальной версией продукта  
- welcome-page - информация о странице, которая является стартовой для текущей директории 

Для поиска запрашиваемой страницы, адаптер обращается к приведенным выше мета-объектам и на основе указанной в них информации определяет необходимый путь. 

Алгоритм работы сервиса:

IMAGE

Для развертывания программы используется Docker. В проекте есть 2 Dockerfile:

1. backend
2. minio

Dockerfile для backend копирует исходники и pom.xml. На основе образа maven + java17 проект собирается и запускается, с
помощью команды spring-boot:run (spring-boot имеет встроенный Tomcat)

Dockerfile для minio устанавливает образ хранилища, создает необходимую директорию (бакет) и в нее копируется заранее
подготовленный dataset (находится в проекте, в директории minio)

Для управления вышеописанными контейнера используется docker-compose.

ВАЖНО: перед запуском контейнеров убедитесь, что указан верный активный профиль (spring.profile.active: prod) в файле
src/main/resources/application.yaml

Сборка и деплой:

```
1. git clone https://github.com/GetRhymes/staticfile-manager.git
2. cd staticfile-manager
2. docker-compose up
```

Структура хранилища:

```
  /help
    |- welcome.html
    |- not-found.html
    |- META-INF.yaml
    |- idea
    |   |- META-INF.yaml
    |   |- 0.0.1
    |      |- getting-started.html
    |      |- feature1.html
    |      |- feature1.html
    |      |- META-INF.yaml
    |   |- 0.0.2
    |      |- getting-started.html
    |      |- feature1.html
    |      |- feature1.html
    |      |- META-INF.yaml
    |- tools
    |   |- welcome.html
    |   |- welcome.json
    |   |- META-INF.yaml
    |- kotlin
    |   |- welcome.html
    |   |- META-INF.yaml
    |   |- features
    |       |- feature1.html
    |       |- feature2.html
    |       |- META-INF.yaml
```

Варианты эксплуатации:

    Case №1
        http://localhost:8080/help -> http://localhost:8080/help/welcome.html
    
    Case №2
        http://localhost:8080/help/idea -> http://localhost:8080/help/idea/0.0.2/getting-started.html
        http://localhost:8080/help/idea/0.0.1 -> http://localhost:8080/help/idea/0.0.1/getting-started.html
        http://localhost:8080/help/idea/0.0.1/feature1.html -> http://localhost:8080/help/idea/0.0.1/feature1.html

    Case №3
        http://localhost:8080/help/tools -> http://localhost:8080/help/tools/welcome.html

    Case №4
        http://localhost:8080/help/kotlin -> http://localhost:8080/help/kotlin/welcome.html
        http://localhost:8080/help/kotlin/features -> http://localhost:8080/help/kotlin/features/feature1.html
        http://localhost:8080/help/kotlin/features/feature1.html -> http://localhost:8080/help/kotlin/features/feature1.html
        http://localhost:8080/help/kotlin/features/feature2.html -> http://localhost:8080/help/kotlin/features/feature2.html
    
    Case №5
        http://localhost:8080/help/kotlin/sdk -> 404not-found
        http://localhost:8080/help/0.0.1 -> 404not-found
        http://localhost:8080/help/feature1.html -> 404not-found