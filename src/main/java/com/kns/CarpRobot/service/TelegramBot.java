package com.kns.CarpRobot.service;

import com.kns.CarpRobot.config.BotConfig;
import com.kns.CarpRobot.model.UserChat;
import com.kns.CarpRobot.model.UserRepository;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import  java.util.List;




import java.util.ArrayList;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    final BotConfig config;

    static final String HELP_TEXT = "Этот чат создан для регистрации тренировок.\n\n" +
            "Вы можете использовать команды в меню слева или набрать их. \n\n" +
            "Команда /start покажет приветсвенное сообщение. \n\n" +
            "Команда /create_workout пока не обрабатывается. \n\n" +
            "Команда /my_workouts пока не обрабатывается. \n\n" +
            "Команда /delete_workout пока не обрабатывается.\n\n" +
            "Команда /help покажет это сообщение повторно. \n\n" +
            "Команда /settings пока не обрабатывается.";

    public TelegramBot(BotConfig config){
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Приветсвенное сообщение"));
        listofCommands.add(new BotCommand("/create_workout", "Начать запись тренировки"));
        listofCommands.add(new BotCommand("/my_workouts", "Получить записи о тренировках"));
        listofCommands.add(new BotCommand("/delete_workout", "Удалить тренировку"));
        listofCommands.add(new BotCommand("/help", "Инструкция по чату"));
        listofCommands.add(new BotCommand("/settings", "Настройки чата"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));

        }
        catch (TelegramApiException e){
            log.error("Error setting bot's command list:" + e.getMessage());
        }

    }
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText){
                case "/start":

                        registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                case "/help":

                    sendMessage(chatId, HELP_TEXT);
                    break;

              case "/create_workout":
                    create_workout(chatId);
                    break;
                default:

                        sendMessage(chatId, "Не понимать начальника, я ток учусь");

            }

        } else if (update.hasCallbackQuery()) {

            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chartId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("EASY_WORKOUT")){

                String text = "Вы выбрали легкую тренировку, с чего начнем?";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chartId));
                message.setText(text);
                message.setMessageId((int) messageId);
                try{
                    execute(message);
                }
                catch (TelegramApiException e){
                    log.error("Error occurred: " + e.getMessage());
                }
            }
            else if (callbackData.equals("MEDIUM_WORKOUT")) {

                String text = "Вы выбрали среднюю тренировку, с чего начнем?";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chartId));
                message.setText(text);
                message.setMessageId((int) messageId);
                try{
                    execute(message);
                }
                catch (TelegramApiException e){
                    log.error("Error occurred: " + e.getMessage());
                }
            }
            else if (callbackData.equals("HARD_WORKOUT")) {

                String text = "Вы выбрали тяжелую тренировку, с чего начнем?";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chartId));
                message.setText(text);
                message.setMessageId((int) messageId);

                try{
                    execute(message);
                }
                catch (TelegramApiException e){
                    log.error("Error occurred: " + e.getMessage());
                }

            };

        }

    }

    private void create_workout(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Отличная идея! Какой тип тренировки вы хотие начать?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var уasyWorkoutButton = new InlineKeyboardButton();

        уasyWorkoutButton.setText("Легкая тренировка");
        уasyWorkoutButton.setCallbackData("EASY_WORKOUT");

        var mediumWorkoutButton = new InlineKeyboardButton();

        mediumWorkoutButton.setText("Стредняя тренировка");
        mediumWorkoutButton.setCallbackData("MEDIUM_WORKOUT");

        var hardWorkoutButton = new InlineKeyboardButton();

        hardWorkoutButton.setText("Тяжелая тренировка");
        hardWorkoutButton.setCallbackData("HARD_WORKOUT");

        rowInLine.add(уasyWorkoutButton);
        rowInLine.add(mediumWorkoutButton);
        rowInLine.add(hardWorkoutButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        try{
            execute(message);
        }
        catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void registerUser(Message msg) {

       if(userRepository.findById(msg.getChatId()).isEmpty()){

           var chatId = msg.getChatId();
           var chat = msg.getChat();

           UserChat userChat = new UserChat();

           userChat.setChatId(chatId);
           userChat.setFirstName(chat.getFirstName());
           userChat.setLastName(chat.getLastName());
           userChat.setUserName(chat.getUserName());
           userChat.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

           userRepository.save(userChat);
           log.info("user saved: " + userChat);
       }
    }

    private void startCommandReceived(long chatId, String name) {
        //String answer = "Привет, "+name+"! Рады видеть Вас на канале для регистрации тренировок";
        String answer = EmojiParser.parseToUnicode("Привет, "+name+"! Рады видеть Вас на канале для регистрации тренировок" + " :muscle:");
        log.info("Replied to user " + name);
        sendMessage(chatId, answer);
    }
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);



        try{
            execute(message);
        }
        catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
