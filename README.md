# Сервис раздачи статических файлов

### Стек

- Kotlin
- Spring-Boot
- Docker
- AmazonS3
- MinIO

### Описание

Данный сервис позволяет раздавать статические файлы, сохраненные в хранилище (MinIO, AmazonS3). Сервис отлавливает GET
запросы с помощью реализованного контроллера `FileManagerController` и передает необходимую информацию
адаптеру `AmazonS3Adapter` / `MinioAdapter`. Адаптер в зависимости от полученной информации возвращает:

- **Запрашиваемый файл**. Если адаптер вернул запрашиваемый файл, то он отображается у пользователя.
- **Путь для переадресации**. Если адаптер вернул путь для переадресации, то сервис сам выполняет перенаправление на
  нужную страницу и в конечном счете возращает результат пользователю.
- **Страницу (*404 - Not found*)**. Если адаптер вернет страницу 404, то она отображается у пользователя вместе с
  соотвествующим http-статусом.

Помимо статических файлов в хранилище также находятся файлы с мета-информацией (`META-INF.yaml`), данный файл выглядит
следующим образом:

```yaml
current-node: '/'
*actual-node : '0.0.1'
*welcome-page : '/welcome.html'
```

Поля, почеменные `*` могут быть только в одном экземпляре (в данном случае либо `actual-node`, либо `welcome-page`).

- `current-node` - информация о текущем местоположении.
- `actual-node` - информация о директории, являющейся актуальной. Например, для версионирования выделяется актуальная
  версия продукта, соответственно, в данном поле должен быть указан путь к директории с актуальной версией продукта.
- `welcome-page` - информация о странице, которая является стартовой для текущей директории.

Для поиска запрашиваемого файла адаптер обращается к приведенным выше мета-объектам и на основе указанной в них
информации определяет необходимый путь.

### Алгоритм работы сервиса

![](https://raw.githubusercontent.com/GetRhymes/staticfile-manager/master/readme-images/alg.jpg)

### Сборка и развертывание

```
1. git clone https://github.com/GetRhymes/staticfile-manager.git
2. cd staticfile-manager
3. docker-compose up
```

Для развертывания программы используется Docker. В проекте есть 2 Dockerfile:

- Dockerfile для `backend` копирует исходники и `pom.xml`. На основе образа maven + java17 проект собирается и
  запускается с
  помощью команды `spring-boot:run` (spring-boot имеет встроенный Tomcat).
- Dockerfile для `minio` устанавливает образ хранилища, создает необходимую директорию (бакет), и в нее копируется
  заранее
  подготовленный dataset (находится в проекте, в директории minio).

Для управления вышеописанными контейнерами используется **docker-compose**.

**ВАЖНО**: перед запуском контейнеров убедитесь, что указан верный активный профиль (`spring.profile.active: prod`) в
файле
*src/main/resources/application.yaml*.

### Структура хранилища

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

### Use cases

**Case 1**. Главная страница документации

http://localhost:8080/help → http://localhost:8080/help/welcome.html

**Case 2**. Документация IDEA

http://localhost:8080/help/idea → http://localhost:8080/help/idea/0.0.2/getting-started.html
http://localhost:8080/help/idea/0.0.1 → http://localhost:8080/help/idea/0.0.1/getting-started.html
http://localhost:8080/help/idea/0.0.1/feature1.html → http://localhost:8080/help/idea/0.0.1/feature1.html

**Case 3**. Документация инструментов

http://localhost:8080/help/tools → http://localhost:8080/help/tools/welcome.html
http://localhost:8080/help/tools/welcome.json → http://localhost:8080/help/tools/welcome.json

**Case 4**. Документация Kotlin

http://localhost:8080/help/kotlin → http://localhost:8080/help/kotlin/welcome.html
http://localhost:8080/help/kotlin/features → http://localhost:8080/help/kotlin/features/feature1.html
http://localhost:8080/help/kotlin/features/feature1.html → http://localhost:8080/help/kotlin/features/feature1.html
http://localhost:8080/help/kotlin/features/feature2.html → http://localhost:8080/help/kotlin/features/feature2.html

**Case 5**. Некорректный запрос

http://localhost:8080/help/kotlin/sdk → 404 not-found
http://localhost:8080/help/0.0.1 → 404 not-found
http://localhost:8080/help/feature1.html → 404 not-found