package telegrambot.work;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import telegrambot.pogo.Question;

public class FileParser {
    public static final String BOT_TOKEN = getAppProperties("TOKEN");
    public static final String BOT_USERNAME = getAppProperties("BOT_USERNAME");
    public static final Map<String, List<Question>> QUESTIONS = parse();
    public static final Map<String, String> QUEST_DESCRIPTION = getQuestDescription();
    public static final Map<String, String> QUESTION_NAME = getQuestionName();

    private static String getAppProperties(String string) {
        try {
            FileReader reader = new FileReader("application.yaml");
            Scanner scan = new Scanner(reader);
            boolean on = scan.hasNextLine();

            while (on) {
                String line = scan.nextLine();
                if (line.startsWith(string)) {
                    string = line.trim().split(":", 2)[1];
                    on = false;
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return string.trim();
    }

    private static Map<String, List<Question>> parse() {
        Map<String, List<Question>> questionMap = new HashMap<>();
        HashMap<Path, String> dataMap = new HashMap<>();

        String dir = "src/main/resources/content/";
        String ogl = "\\[к оглавлению]";
        dataMap.put(Paths.get(dir + "Patterns.md"), ogl + "\\(#Шаблоны-проектирования\\)");
        dataMap.put(Paths.get(dir + "Collections_1.md"), ogl + "\\(#java-collections-framework\\)");
        dataMap.put(Paths.get(dir + "Collections_2.md"), ogl + "\\(#java-collections-framework\\)");
        dataMap.put(Paths.get(dir + "IO.md"), ogl + "\\(#Потоки-вводавывода-в-java\\)");
        dataMap.put(Paths.get(dir + "Concurrency_1.md"), ogl + "\\(#Многопоточность\\)");
        dataMap.put(Paths.get(dir + "Concurrency_2.md"), ogl + "\\(#Многопоточность\\)");
        dataMap.put(Paths.get(dir + "Serialization.md"), ogl + "\\(#Сериализация\\)");
        dataMap.put(Paths.get(dir + "Servlets_1.md"), ogl + "\\(#servlets-jsp-jstl\\)");
        dataMap.put(Paths.get(dir + "Servlets_2.md"), ogl + "\\(#servlets-jsp-jstl\\)");
        dataMap.put(Paths.get(dir + "Log.md"), ogl + "\\(#Журналирование\\)");
        dataMap.put(Paths.get(dir + "Testing.md"), ogl + "\\(#Тестирование\\)");
        dataMap.put(Paths.get(dir + "HTML.md"), ogl + "\\(#Основы-html\\)");
        dataMap.put(Paths.get(dir + "CSS.md"), ogl + "\\(#Основы-css\\)");
        dataMap.put(Paths.get(dir + "DataBase.md"), ogl + "\\(#Базы-данных\\)");
        dataMap.put(Paths.get(dir + "JavaCore_1.md"), ogl + "\\(#java-core\\)");
        dataMap.put(Paths.get(dir + "JavaCore_2.md"), ogl + "\\(#java-core\\)");
        dataMap.put(Paths.get(dir + "JavaCore_3.md"), ogl + "\\(#java-core\\)");
        dataMap.put(Paths.get(dir + "WEB.md"), ogl + "\\(#Основы-web\\)");
        dataMap.put(Paths.get(dir + "Java8_1.md"), ogl + "\\(#java-8\\)");
        dataMap.put(Paths.get(dir + "Java8_2.md"), ogl + "\\(#java-8\\)");
        dataMap.put(Paths.get(dir + "JDBC.md"), ogl + "\\(#jdbc\\)");
        dataMap.put(Paths.get(dir + "OOP.md"), ogl + "\\(#ООП\\)");
        dataMap.put(Paths.get(dir + "JVM.md"), ogl + "\\(#jvm\\)");
        dataMap.put(Paths.get(dir + "XML.md"), ogl + "\\(#xml\\)");
        dataMap.put(Paths.get(dir + "SQL.md"), ogl + "\\(#sql\\)");
        dataMap.put(Paths.get(dir + "UML.md"), ogl + "\\(#uml\\)");

        try {
            for (Entry<Path, String> file : dataMap.entrySet()) {

                String fileContent = String.join("\n", Files.readAllLines(file.getKey()));
                String[] splitValue = fileContent.split(file.getValue());

                List<Question> questionList = new ArrayList<>();

                for (String ss : splitValue) {
                    StringBuilder key = new StringBuilder();
                    StringBuilder value = new StringBuilder();
                    String[] splitN = ss.split("\n");

                    for (String s : splitN) {
                        if (s.startsWith("##")) {
                            s = s.replaceAll("##", "").trim();
                            key.append(s).append("\n");
                        } else {
                            value.append(s).append("\n");
                        }
                    }
                    String keyStr = key.toString().trim();
                    if (keyStr.equals("") || value.toString().trim().equals("")) {
                        continue;
                    }
                    if (keyStr.startsWith("Источник")) {
                        continue;
                    }

                    Question question = new Question(key.toString().trim(), value.toString().trim());
                    questionList.add(question);

                    String[] splitName = file.getKey().toString().replace("\\", "\t").split("\t");
                    String name = splitName[splitName.length - 1].replace(".md", "");
                    questionMap.put(name, questionList);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return questionMap;
    }

    private static Map<String, String> getQuestDescription() {
        Map<String, String> questionDescription = new HashMap<>();

        for (Entry<String, List<Question>> entry : QUESTIONS.entrySet()) {
            for (Question question : entry.getValue()) {
                questionDescription.put(question.getQuestion(), question.getDescription());
            }
        }
        return questionDescription;
    }

    private static Map<String, String> getQuestionName() {
        Map<String, String> map = new HashMap<>();
        for (Entry<String, List<Question>> entry : QUESTIONS.entrySet()) {
            for (Question question : entry.getValue()) {
                map.put(question.getQuestion(), entry.getKey());
            }
        }

        return map;
    }

}
