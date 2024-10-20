const  TelegramBot = require('node-telegram-bot-api');
const fs = require('fs');
const { console } = require('inspector');

const token = '' ; // 用您自己的机器人令牌替换
const bot = new  TelegramBot (token, {
    polling: true,
});

bot.onText(/\get/, (msg) => {
    const chatId = msg.chat.id;

    const data=fs.readFileSync('./sensor.log','utf-8');
    const lines=data.split(/\r?\n/);
    
    console.log(data);
    bot.sendMessage(chatId, data);
  });

bot.on('message', (msg) => {
    const chatId = msg.chat.id;
    const messageText = msg.text;
  
    // Process the incoming message here
    if (messageText === '/start') {
        console.log("/start");
        bot.sendMessage(chatId, 'Telegram homeassistant bot V1.0 \nupdate in 2024/10/21');
    }

    if (messageText === '/ping') {
        console.log("/ping");
        bot.sendMessage(chatId, 'pong!');
    }
    
  });

  bot.onText(/\/repeat (.+)/, (msg, match) => {
    const chatId = msg.chat.id;
    const text = match[1];

    console.log("/repeat");
    console.log("text:"+text);
  
    bot.sendMessage(chatId, text);
  });