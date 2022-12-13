package telegrambot;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.controller.Controller;
import telegrambot.work.FileParser;

public class Bot extends TelegramLongPollingBot {

    private final Map<Long, Controller> chatIdControllerMap = new HashMap<>();

    @Override
    public String getBotUsername() {
        return FileParser.BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return FileParser.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.hasCallbackQuery() ?
                update.getCallbackQuery().getMessage() : update.getMessage();
        long chatId = message.getChatId();

        if (!this.chatIdControllerMap.containsKey(chatId)) {
            this.chatIdControllerMap.put(chatId, new Controller(this));
        }

        chatIdControllerMap.get(chatId).mainManage(update, chatId);
    }

    public void sendFile(Long who, String path, InlineKeyboardMarkup kb) {
        File file = new File(path);
        SendDocument document = new SendDocument();
        document.setChatId(who.toString());
        document.setReplyMarkup(kb);
        document.setDocument(new InputFile().setMedia(file));

        try {
            this.execute(document);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendText(Long who, String what, InlineKeyboardMarkup markup) {
        SendMessage message = SendMessage.builder()
                .chatId(who.toString())
                .replyMarkup(markup)
                .text(what).build();

        try {
            this.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCommand(Long who, String txt, InlineKeyboardMarkup kb) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();
        try {
            this.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMessage(CallbackQuery callbackQuery) {
        if (callbackQuery == null) {
            return;
        }

        Message message = callbackQuery.getMessage();
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(message.getMessageId());
        deleteMessage.setChatId(message.getChat().getId());

        try {
            this.execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
