package telegrambot.work;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import telegrambot.pogo.Question;

public class FileParser {

    public static final String BOT_TOKEN = getAppProperties("TOKEN");
    public static final String BOT_USERNAME = getAppProperties("BOT_USERNAME");
    public static final Map<String, List<Question>> QUESTIONS = pars();
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

    private static Map<String, List<Question>> pars() {
        Map<String, List<Question>> questionMap = new HashMap<>();
        HashMap<Path, String> dataMap = new HashMap<>();

        String dir = "src/main/resources/content/";
        String ogl = "Super-Separator";

        String[] names = new File(dir).list();

        if (names != null){
            for (String path : names) {
                dataMap.put(Paths.get(dir + path.trim()), ogl);
            }
        }

        if (dataMap.isEmpty()){
            Logger.getGlobal().warning("Добавьте данные в " + dir);
            throw new RuntimeException();
        }


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

                    String keyR = key.toString().trim().replaceAll("_", "");
                    String valueR = value.toString().strip();
                    Question question = new Question(keyR, valueR);
                    questionList.add(question);

                    String[] splitName = file.getKey().toString()
                            .replace("\\", "\t").split("\t");
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
