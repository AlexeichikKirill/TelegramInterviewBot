## Как вывести на экран 10 случайных чисел, используя `forEach()`?
```java
(new Random())
    .ints()
    .limit(10)
    .forEach(System.out::println);
```

[к оглавлению](#java-8)

## Как можно вывести на экран уникальные квадраты чисел используя метод `map()`?
```java
Stream
    .of(1, 2, 3, 2, 1)
    .map(s -> s * s)
    .distinct()
    .forEach(System.out::println);
```

[к оглавлению](#java-8)

## Как вывести на экран количество пустых строк с помощью метода `filter()`?
```java
System.out.println(
    Stream
        .of("Hello", "", ", ", "world", "!")
        .filter(String::isEmpty)
        .count());
```

[к оглавлению](#java-8)

## Как вывести на экран 10 случайных чисел в порядке возрастания?
```java
(new Random())
    .ints()
    .limit(10)
    .sorted()
    .forEach(System.out::println);
```

[к оглавлению](#java-8)

## Как найти максимальное число в наборе?
```java
Stream
    .of(5, 3, 4, 55, 2)
    .mapToInt(a -> a)
    .max()
    .getAsInt(); //55
```

[к оглавлению](#java-8)

## Как найти минимальное число в наборе?
```java
Stream
    .of(5, 3, 4, 55, 2)
    .mapToInt(a -> a)
    .min()
    .getAsInt(); //2
```
[к оглавлению](#java-8)

## Как получить сумму всех чисел в наборе?
```java
Stream
    .of(5, 3, 4, 55, 2)
    .mapToInt()
    .sum(); //69
```
[к оглавлению](#java-8)

## Как получить среднее значение всех чисел?
```java
Stream
    .of(5, 3, 4, 55, 2)
    .mapToInt(a -> a)
    .average()
    .getAsDouble(); //13.8
```
[к оглавлению](#java-8)

## Какие дополнительные методы для работы с ассоциативными массивами (maps) появились в Java 8?
+ `putIfAbsent()` добавляет пару «ключ-значение», только если ключ отсутствовал:

`map.putIfAbsent("a", "Aa");`

+ `forEach()` принимает функцию, которая производит операцию над каждым элементом:

`map.forEach((k, v) -> System.out.println(v));`

+ `compute()` создаёт или обновляет текущее значение на полученное в результате вычисления (возможно использовать ключ и текущее значение):

`map.compute("a", (k, v) -> String.valueOf(k).concat(v)); //["a", "aAa"]`

+ `computeIfPresent()` если ключ существует, обновляет текущее значение на полученное в результате вычисления (возможно использовать ключ и текущее значение):

`map.computeIfPresent("a", (k, v) -> k.concat(v));`

+ `computeIfAbsent()` если ключ отсутствует, создаёт его со значением, которое вычисляется (возможно использовать ключ):

`map.computeIfAbsent("a", k -> "A".concat(k)); //["a","Aa"]`

+ `getOrDefault()` в случае отсутствия ключа, возвращает переданное значение по-умолчанию:

`map.getOrDefault("a", "not found");`

+ `merge()` принимает ключ, значение и функцию, которая объединяет передаваемое и текущее значения. Если под заданным ключем значение отсутствует, то записывает туда передаваемое значение.

`map.merge("a", "z", (value, newValue) -> value.concat(newValue)); //["a","Aaz"]`

[к оглавлению](#java-8)

## Что такое `LocalDateTime`?
`LocalDateTime` объединяет вместе `LocaleDate` и `LocalTime`, содержит дату и время в календарной системе ISO-8601 без привязки к часовому поясу. Время хранится с точностью до наносекунды. Содержит множество удобных методов, таких как plusMinutes, plusHours, isAfter, toSecondOfDay и т.д.

[к оглавлению](#java-8)

## Что такое `ZonedDateTime`?
`java.time.ZonedDateTime` — аналог `java.util.Calendar`, класс с самым полным объемом информации о временном контексте в календарной системе ISO-8601. Включает временную зону, поэтому все операции с временными сдвигами этот класс проводит с её учётом.

[к оглавлению](#java-8)

## Как получить текущую дату с использованием Date Time API из Java 8?
```java
LocalDate.now();
```

[к оглавлению](#java-8)

## Как добавить 1 неделю, 1 месяц, 1 год, 10 лет к текущей дате с использованием Date Time API?
```java
LocalDate.now().plusWeeks(1);
LocalDate.now().plusMonths(1);
LocalDate.now().plusYears(1);
LocalDate.now().plus(1, ChronoUnit.DECADES);
```

[к оглавлению](#java-8)

## Как получить следующий вторник используя Date Time API?
```java
LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
```

[к оглавлению](#java-8)

## Как получить вторую субботу текущего месяца используя Date Time API?
```java
LocalDate
    .of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1)
    .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
    .with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
```

[к оглавлению](#java-8)

## Как получить текущее время с точностью до миллисекунд используя Date Time API?
```java
new Date().toInstant();
```

[к оглавлению](#java-8)

## Как получить текущее время по местному времени с точностью до миллисекунд используя Date Time API?
```java
LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
```

[к оглавлению](#java-8)

## Как определить повторяемую аннотацию?
Чтобы определить повторяемую аннотацию, необходимо создать аннотацию-контейнер для списка повторяемых аннотаций и обозначить повторяемую мета-аннотацией `@Repeatable`:

```java
@interface Schedulers
{
    Scheduler[] value();
}

@Repeatable(Schedulers.class)
@interface Scheduler
{
    String birthday() default "Jan 8 1935";
}
```

[к оглавлению](#java-8)

## Что такое `Nashorn`?
__Nashorn__ - это движок JavaScript, разрабатываемый на Java компанией Oracle. Призван дать возможность встраивать код JavaScript в приложения Java. В сравнении с _Rhino_, который поддерживается Mozilla Foundation, Nashorn обеспечивает от 2 до 10 раз более высокую производительность, так как он компилирует код и передает байт-код виртуальной машине Java непосредственно в памяти. Nashorn умеет компилировать код JavaScript и генерировать классы Java, которые загружаются специальным загрузчиком. Так же возможен вызов кода Java прямо из JavaScript.

[к оглавлению](#java-8)

## Что такое `jjs`?
`jjs` это утилита командной строки, которая позволяет исполнять программы на языке JavaScript прямо в консоли.

[к оглавлению](#java-8)

## Какой класс появился в Java 8 для кодирования/декодирования данных?
`Base64` - потокобезопасный класс, который реализует кодировщик и декодировщик данных, используя схему кодирования base64 согласно _RFC 4648_ и _RFC 2045_.

Base64 содержит 6 основных методов:

`getEncoder()`/`getDecoder()` - возвращает кодировщик/декодировщик base64, соответствующий стандарту _RFC 4648_;
`getUrlEncoder()`/`getUrlDecoder()` - возвращает URL-safe кодировщик/декодировщик base64, соответствующий стандарту _RFC 4648_;
`getMimeEncoder()`/`getMimeDecoder()` - возвращает MIME кодировщик/декодировщик, соответствующий стандарту _RFC 2045_.

[к оглавлению](#java-8)

## Как создать Base64 кодировщик и декодировщик?
```java
// Encode
String b64 = Base64.getEncoder().encodeToString("input".getBytes("utf-8")); //aW5wdXQ==
// Decode
new String(Base64.getDecoder().decode("aW5wdXQ=="), "utf-8"); //input
```

[к оглавлению](#java-8)

# Источники
+ [Хабрахабр - Новое в Java 8](https://habrahabr.ru/post/216431/)
+ [Хабрахабр - Шпаргалка Java программиста 4. Java Stream API](https://habrahabr.ru/company/luxoft/blog/270383/)
+ [METANIT.COM](http://metanit.com/java/tutorial/9.1.php)
+ [javadevblog.com](http://javadevblog.com/interfejsy-v-java-8-staticheskie-metody-metody-po-umolchaniyu-funktsional-ny-e-interfejsy.html)

[Вопросы для собеседования](README.md)
