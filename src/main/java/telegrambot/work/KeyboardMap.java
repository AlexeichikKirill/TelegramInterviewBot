package telegrambot.work;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import telegrambot.pogo.Question;

public class KeyboardMap {
    private static final Map<String, List<Question>> questions = FileParser.QUESTIONS;

    public static final InlineKeyboardMarkup mainMarkup = createMainMarkup();
    public static final Map<String, InlineKeyboardMarkup> localMarkups = createLocalMarkups();

    private static InlineKeyboardMarkup createMainMarkup() {
        Set<String> setNames = questions.keySet();
        List<String> names = new ArrayList<>(List.copyOf(setNames));
        Collections.sort(names);
        List<InlineKeyboardButton> list = createKeyboard(names);
        return createMainMarkup(list);
    }

    private static Map<String, InlineKeyboardMarkup> createLocalMarkups() {
        Set<String> names = questions.keySet();
        Map<String, InlineKeyboardMarkup> localMarkups = new HashMap<>();

        for (String name : names) {
            List<String> questList = new ArrayList<>();

            for (Question question : questions.get(name)) {
                questList.add(question.getQuestion());
            }
            List<InlineKeyboardButton> buttons = createKeyboard(questList);
            InlineKeyboardMarkup markup = createLocalMarkup(buttons);
            localMarkups.put(name, markup);
        }
        return localMarkups;
    }

    private static List<InlineKeyboardButton> createKeyboard(List<String> names) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (String name : names) {
            String backData = String.valueOf(name);
            if (backData.length() > 30) {
                backData = name.substring(backData.length() - 30, backData.length());
            }
            InlineKeyboardButton kbButton = InlineKeyboardButton.builder().text(name).callbackData(backData).build();
            buttons.add(kbButton);
        }

        return buttons;
    }

    private static InlineKeyboardMarkup createLocalMarkup(List<InlineKeyboardButton> list) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardList = new ArrayList<>();

        for (InlineKeyboardButton button : list) {
            keyboardList.add(List.of(button));
        }

        keyboardList.add(List.of(back()));
        markup.setKeyboard(keyboardList);
        return markup;
    }

    private static InlineKeyboardMarkup createMainMarkup(List<InlineKeyboardButton> list) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList = new ArrayList<>();

        for (InlineKeyboardButton button : list) {
            if (keyboardList.isEmpty()) {
                keyboardList.add(List.of(button));
            } else if (buttonList.size() < 2) {
                buttonList.add(button);
            } else {
                keyboardList.add(List.copyOf(buttonList));
                buttonList.clear();
                buttonList.add(button);
            }
        }

        if (!buttonList.isEmpty()) {
            keyboardList.add(List.copyOf(buttonList));
        }

        markup.setKeyboard(keyboardList);
        return markup;
    }

    private static InlineKeyboardButton back() {
        return InlineKeyboardButton.builder().text("Назад").callbackData("main").build();
    }

}
