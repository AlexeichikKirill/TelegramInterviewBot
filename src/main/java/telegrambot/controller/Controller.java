package telegrambot.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import telegrambot.Bot;
import telegrambot.work.FileParser;
import telegrambot.work.KeyboardMap;

public class Controller {

    private final Bot bot;

    public Controller(Bot bot) {
        this.bot = bot;
    }

    public void mainManage(Update update, long chatId) {
        if (update.hasCallbackQuery()) {
            CallbackQuery query = update.getCallbackQuery();
            this.bot.deleteMessage(query);
            this.manageResponse(query.getData(), chatId);
        } else {
            String txt = update.getMessage().getText();
            this.manageResponse(txt, chatId);
        }

    }

    private void manageResponse(String question, long chatId) {
        switch (question) {
            case "/start" -> getStart(chatId);
            case "main" -> bot.sendCommand(chatId, "Выберите тему", KeyboardMap.mainMarkup);
            default -> manageLocal(question, chatId);
        }
    }

    private void manageLocal(String question, long chatId) {
        if (FileParser.QUESTIONS.containsKey(question)) {
            manageMainButtons(question, chatId);
        } else {
            manageLocalButtons(question, chatId);
        }

    }

    private void manageMainButtons(String question, long chatId) {
        for (Entry<String, InlineKeyboardMarkup> entry : KeyboardMap.localMarkups.entrySet()) {
            if (entry.getKey().startsWith(question)) {
                question = entry.getKey();
                break;
            }
        }
        InlineKeyboardMarkup markup = KeyboardMap.localMarkups.get(question);
        bot.sendCommand(chatId, "Ваша тема: " + question + "\nВыберите вопрос", markup);
    }

    private void manageLocalButtons(String question, long chatId) {
        boolean warn = true;
        for (Entry<String, String> entry : FileParser.QUEST_DESCRIPTION.entrySet()) {
            if (entry.getKey().startsWith(question)) {
                question = entry.getKey();
                warn = false;
                break;
            }
        }

        if (warn) {
            return;
        }

        String description = "Вопрос: " + question + ": \n\n" + FileParser.QUEST_DESCRIPTION.get(question);

        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("Вернуться к теме").callbackData(FileParser.QUESTION_NAME.get(question)).build();
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder().build();
        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        lists.add(List.of(button));
        lists.add(createNextPrevButtons(question));
        keyboard.setKeyboard(lists);

        if (description.length() > 4000) {
            File file = newDoc(question, description);
            bot.sendFile(chatId, file.getPath(), keyboard);
        } else {
            bot.sendText(chatId, description, keyboard);
        }
    }

    private List<InlineKeyboardButton> createNextPrevButtons(String question) {
        InlineKeyboardButton next = null;
        InlineKeyboardButton prev = null;

        boolean stop = false;
        for (Entry<String, InlineKeyboardMarkup> entry : KeyboardMap.localMarkups.entrySet()) {
            if (stop) {break;}
            InlineKeyboardMarkup markup = entry.getValue();
            List<List<InlineKeyboardButton>> lists = markup.getKeyboard();
            for (int i = 0; i < lists.size(); i++) {
                String buttonText = lists.get(i).get(0).getText();
                if (buttonText.equals(question)) {
                    if (i < lists.size() - 2) {
                        InlineKeyboardButton button = lists.get(i + 1).get(0);
                        next = InlineKeyboardButton.builder()
                                .text("Следующий")
                                .callbackData(button.getCallbackData()).build();
                    }
                    if (i > 0) {
                        InlineKeyboardButton button = lists.get(i - 1).get(0);
                        prev = InlineKeyboardButton.builder()
                                .text("Предыдущий")
                                .callbackData(button.getCallbackData()).build();
                    }
                    stop = true;
                    break;
                }
            }
        }

        List<InlineKeyboardButton> list = new ArrayList<>();

        if (prev != null){
            list.add(prev);
        }
        if (next != null){
            list.add(next);
        }

        return list;
    }

    private File newDoc(String question, String description) {
        String dir = "src/main/resources/QuestionsBigDescriptions/" + question + ".txt";
        File file = new File(dir);
        if (!file.exists()) {
            fileWriter(dir, description);
        }
        return file;
    }

    private void fileWriter(String path, String text) {
        try {
            FileWriter writer = new FileWriter(path, false);
            writer.write(text);

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getStart(long id) {
        String lineSeparator = System.lineSeparator();
        String text = "Привет, это бот поможет Вам" + lineSeparator +
                "подготовиться к технической части интервью!" + System.lineSeparator() +
                "Он много знает, поэтому начинай)";
        InlineKeyboardButton button = InlineKeyboardButton.builder().text("Начать").callbackData("main").build();
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder().keyboardRow(List.of(button)).build();
        this.bot.sendCommand(id, text, keyboard);
    }
}
