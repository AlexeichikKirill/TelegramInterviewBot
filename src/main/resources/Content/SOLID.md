Super-Separator

## Что такое SOLID, что означает каждая буква.

SOLID — это акроним, образованный из заглавных букв первых пяти принципов ООП и
проектирования. Принципы придумал Роберт Мартин в начале двухтысячных, 
а аббревиатуру позже ввел в обиход Майкл Фэзерс.

Вот что входит в принципы SOLID:
Single Responsibility Principle (Принцип единственной ответственности).
Open Closed Principle (Принцип открытости/закрытости).
Liskov’s Substitution Principle (Принцип подстановки Барбары Лисков).
Interface Segregation Principle (Принцип разделения интерфейса).
Dependency Inversion Principle (Принцип инверсии зависимостей).

Super-Separator

## S. Принцип единственной ответственности (SRP)

Данный принцип гласит: никогда не должно быть больше одной причины изменить класс.

На каждый объект возлагается одна обязанность, полностью инкапсулированная в класс. 
Все сервисы класса направлены на обеспечение этой обязанности.

Такие классы всегда будет просто изменять, если это понадобится, потому что понятно, 
за что класс отвечает, а за что — нет. То есть можно будет вносить изменения и не бояться последствий — 
влияния на другие объекты. А еще подобный код гораздо проще тестировать, ведь вы покрываете тестами одну 
функциональность в изоляции от всех остальных.

Представьте себе модуль, который обрабатывает заказы. Если заказ верно сформирован, 
он сохраняет его в базу данных и высылает письмо для подтверждения заказа:

```java
public class OrderProcessor {
    public void process(Order order){
        if (order.isValid() && save(order)) {
            sendConfirmationEmail(order);
        }
    }

    private boolean save(Order order) {
        MySqlConnection connection = new MySqlConnection("database.url");
        // сохраняем заказ в базу данных

        return true;
    }

    private void sendConfirmationEmail(Order order) {
        String name = order.getCustomerName();
        String email = order.getCustomerEmail();

        // Шлем письмо клиенту
    }
}
``` 

Такой модуль может измениться по трем причинам. Во-первых может стать другой логика обработки заказа,
во-вторых, способ его сохранения (тип базы данных), в-третьих — способ отправки письма подтверждения 
(скажем, вместо email нужно отправлять SMS).

Принцип единственной обязанности подразумевает, что три аспекта этой проблемы на самом деле — три 
разные обязанности. А значит, должны находиться в разных классах или модулях. Объединение нескольких 
сущностей, которые могут меняться в разное время и по разным причинам, считается плохим проектным решением.

Гораздо лучше разделить модуль на три отдельных, каждый из которых будет выполнять одну единственную функцию:
 
```java
public class MySQLOrderRepository {
    public boolean save(Order order) {
        MySqlConnection connection = new MySqlConnection("database.url");
        // сохраняем заказ в базу данных

        return true;
    }
}

public class ConfirmationEmailSender {
    public void sendConfirmationEmail(Order order) {
        String name = order.getCustomerName();
        String email = order.getCustomerEmail();

        // Шлем письмо клиенту
    }
}

public class OrderProcessor {
    public void process(Order order){

        MySQLOrderRepository repository = new MySQLOrderRepository();
        ConfirmationEmailSender mailSender = new ConfirmationEmailSender();

        if (order.isValid() && repository.save(order)) {
            mailSender.sendConfirmationEmail(order);
        }
    }
}
```

Super-Separator

## O. Принцип открытости/закрытости (OCP)

Этот принцип емко описывают так: программные сущности (классы, модули, функции и т.п.)
должны быть открыты для расширения, но закрыты для изменения.

Это означает, что должна быть возможность изменять внешнее поведение класса, не внося 
физические изменения в сам класс. Следуя этому принципу, классы разрабатываются так, 
чтобы для подстройки класса к конкретным условиям применения было достаточно расширить 
его и переопределить некоторые функции.

Поэтому система должна быть гибкой, с возможностью работы в переменных условиях 
без изменения исходного кода.

Продолжая наш пример с заказом, предположим, что нам нужно выполнять какие-то действия 
перед обработкой заказа и после отправки письма с подтверждением. Вместо того, чтобы менять 
сам класс OrderProcessor, мы расширим его и добьемся решения поставленной задачи, не нарушая принцип OCP: 

```java
public class OrderProcessorWithPreAndPostProcessing extends OrderProcessor {

    @Override
    public void process(Order order) {
        beforeProcessing();
        super.process(order);
        afterProcessing();
    }

    private void beforeProcessing() {
        // Осуществим некоторые действия перед обработкой заказа
    }

    private void afterProcessing() {
        // Осуществим некоторые действия после обработки заказа
    }
}
```

Super-Separator

## L. Принцип подстановки Барбары Лисков (LSP)
Это вариация принципа открытости/закрытости, о котором говорилось ранее. 
Его можно описать так: объекты в программе можно заменить их наследниками без изменения свойств программы.

Это означает, что класс, разработанный путем расширения на основании базового 
класса, должен переопределять его методы так, чтобы не нарушалась функциональность 
с точки зрения клиента. То есть, если разработчик расширяет ваш класс и использует 
его в приложении, он не должен изменять ожидаемое поведение переопределенных методов.

Подклассы должны переопределять методы базового класса так, чтобы не нарушалась 
функциональность с точки зрения клиента. Подробно это можно рассмотреть на следующем примере.

Предположим у нас есть класс, который отвечает за валидацию заказа и проверяет, 
все ли из товаров заказа находятся на складе. У данного класса есть метод isValid 
который возвращает true или false:

```java
public class OrderStockValidator {

    public boolean isValid(Order order) {
        for (Item item : order.getItems()) {
            if (! item.isInStock()) {
                return false;
            }
        }

        return true;
    }
}
```


Также предположим, что некоторые заказы нужно валидировать иначе: 
проверять, все ли товары заказа находятся на складе и все ли товары упакованы. 
Для этого мы расширили класс OrderStockValidator классом OrderStockAndPackValidator:

```java
public class OrderStockAndPackValidator extends OrderStockValidator {

    @Override
    public boolean isValid(Order order) {
        for (Item item : order.getItems()) {
            if ( !item.isInStock() || !item.isPacked() ){
                throw new IllegalStateException(
                     String.format("Order %d is not valid!", order.getId())
                );
            }
        }

        return true;
    }
}
```
Однако в данном классе мы нарушили принцип LSP, так как вместо того, чтобы вернуть false, 
если заказ не прошел валидацию, наш метод бросает исключение IllegalStateException. 
Клиенты данного кода не рассчитывают на такое: они ожидают возвращения true или false. 
Это может привести к ошибкам в работе программы.

Super-Separator

## I. Принцип разделения интерфейса (ISP)
Характеризуется следующим утверждением: клиенты не должны быть вынуждены реализовывать методы, 
которые они не будут использовать.

Принцип разделения интерфейсов говорит о том, что слишком «толстые» интерфейсы необходимо 
разделять на более мелкие и специфические, чтобы клиенты мелких интерфейсов знали только о 
методах, необходимых в работе. В итоге, при изменении метода интерфейса не должны меняться 
клиенты, которые этот метод не используют.

Рассмотрим пример. Разработчик Алекс создал интерфейс "отчет" и добавил два метода: 
generateExcel() и generatedPdf(). Теперь клиент А хочет использовать этот интерфейс, 
но он намерен использовать отчеты только в PDF-формате, а не в Excel. Устроит ли его 
такая функциональность?

Нет. Он должен будет реализовать два метода, один из которых по большому счету не нужен 
и существует только благодаря Алексу — дизайнеру программного обеспечения. Клиент 
воспользуется либо другим интерфейсом, либо оставит поле для Excel пустым.

Так в чем же решение? Оно состоит в разделении существующего интерфейса на два более мелких. 
Один — отчет в формате PDF, второй — отчет в формате Excel. Это даст пользователю возможность 
использовать только необходимый для него функционал.

Super-Separator

## D. Принцип инверсии зависимостей (DIP)
Этот принцип SOLID в Java описывают так: зависимости внутри системы строятся на основе 
абстракций. Модули верхнего уровня не зависят от модулей нижнего уровня. Абстракции не 
должны зависеть от деталей. Детали должны зависеть от абстракций.

Программное обеспечение нужно разрабатывать так, чтобы различные модули были автономными 
и соединялись друг с другом с помощью абстракции.

Классическое применение этого принципа — Spring framework. В рамках Spring framework 
все модули выполнены в виде отдельных компонентов, которые могут работать вместе. 
Они настолько автономны, что могут быть быть с такой же легкостью задействованы в 
других программных модулях помимо Spring framework.

Это достигнуто за счет зависимости закрытых и открытых принципов. Все модули предоставляют 
доступ только к абстракции, которая может использоваться в другом модуле.

Попробуем продемонстрировать это на примере. Говоря о принципе единственной ответственности, 
мы рассматривали некоторый OrderProcessor. Взглянем еще раз на код данного класса: 

```java
public class OrderProcessor {
    public void process(Order order){

        MySQLOrderRepository repository = new MySQLOrderRepository();
        ConfirmationEmailSender mailSender = new ConfirmationEmailSender();

        if (order.isValid() && repository.save(order)) {
            mailSender.sendConfirmationEmail(order);
        }
    }

}
```

В данном примере наш OrderProcessor зависит от двух конкретных классов MySQLOrderRepository и 
ConfirmationEmailSender. Приведем также код данных классов:

```java
public class MySQLOrderRepository {
    public boolean save(Order order) {
        MySqlConnection connection = new MySqlConnection("database.url");
        // сохраняем заказ в базу данных

        return true;
    }
}

public class ConfirmationEmailSender {
    public void sendConfirmationEmail(Order order) {
        String name = order.getCustomerName();
        String email = order.getCustomerEmail();

        // Шлем письмо клиенту
    }
}
```

Эти классы далеки от того, чтобы называться абстракциями. 
И с точки зрения принципа DIP было бы правильнее для начала создать некоторые 
абстракции, которые позволят нам оперировать в дальнейшем ими, а не конкретными 
реализациями. Создадим два интерфейса MailSender и OrderRepository, 
которые и станут нашими абстракциями:

```java
public interface MailSender {
    void sendConfirmationEmail(Order order);
}

public interface OrderRepository {
    boolean save(Order order);
}
```

Теперь имплементируем данные интерфейсы в уже готовых для этого классах: 

```java
public class ConfirmationEmailSender implements MailSender {

    @Override
    public void sendConfirmationEmail(Order order) {
        String name = order.getCustomerName();
        String email = order.getCustomerEmail();

        // Шлем письмо клиенту
    }

}

public class MySQLOrderRepository implements OrderRepository {

    @Override
    public boolean save(Order order) {
        MySqlConnection connection = new MySqlConnection("database.url");
        // сохраняем заказ в базу данных

        return true;
    }
}
```

Мы провели подготовительную работу, чтобы наш класс OrderProcessor зависит не 
от конкретных деталей, а от абстракций. Внесем в него изменения, внедряя наши 
зависимости в конструкторе класса: 

```java
public class OrderProcessor {

    private MailSender mailSender;
    private OrderRepository repository;

    public OrderProcessor(MailSender mailSender, OrderRepository repository) {
        this.mailSender = mailSender;
        this.repository = repository;
    }

    public void process(Order order){
        if (order.isValid() && repository.save(order)) {
            mailSender.sendConfirmationEmail(order);
        }
    }
}
```
Теперь наш класс зависит от абстракций, а не от конкретных реализаций. 
Можно без труда менять его поведение, внедряя нужную зависимость в момент 
создания экземпляра OrderProcessor.