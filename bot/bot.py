import telebot
from telebot import types
import requests
import json
# Переменные среды или файл конфигурации для хранения токена

with open('config.json', 'r') as conf:
    data = json.load(conf)
    TOKEN = data["TOKEN"]
    BASE_URL = data["BASE_URL"]

bot = telebot.TeleBot(TOKEN)

def send_score(sourceTelegramId, targetTelegramId, skillName, score):
    query_params = {
        "sourceTelegramId": sourceTelegramId,
        "targetTelegramId": targetTelegramId,
        "skillName": skillName,
        "score": int(score)
        }
    response = requests.post(f"{BASE_URL}/api/v1/vote", json=query_params)
    
    #print(f"sended: {sourceTelegramId}, {targetTelegramId}, {skillName}, {score}")

def send_open_assessments():
    response = requests.post(f"{BASE_URL}/api/v1/assessment/reformat")

def send_close_assessments():
    pass
    #response = requests.post(f"{BASE_URL}/api/v1/assessment/end")


# Функция для получения прав доступа пользователя
def get_access_rights(username):
    query_params = {
        "telegramId": username
    }
    response = requests.post(f"{BASE_URL}/api/v1/auth", json=query_params)
    return response.json()["role"]

# Функция для получения списка сотрудников по идентификатору пользователя
def get_workers_by_id(username):
    query_params = {
        "sourceTelegramId": username
    }
    response = requests.post(f"{BASE_URL}/api/v1/vote/targets", json=query_params)
    try:
        return response.json()["targets"]
    except:
        print(username)
        print(response.json())
        return None

# Функция для получения ассесмента по идентификатору сотрудника
def get_assessment_by_id(username, targetId):
    query_params = {
        "sourceTelegramId": username,
        "targetTelegramId": targetId
    }
    response = requests.post(f"{BASE_URL}/api/v1/vote/votable", json=query_params)
    return response.json()["skills"]

def get_matrix_by_id(username):
    query_params = {
        "telegramId": username,
    }
    response = requests.post(f"{BASE_URL}/api/v1/assessment/matrix", json=query_params)
    return response.json()


def skill_pack(skill, sourceTelegramId, targetTelegramId):
    return f"skill_({skill})#({sourceTelegramId})#({targetTelegramId})"

def skill_unpack(packed_skill):
    s = packed_skill.split(')#(')
    skill = s[0][len("skill_("):]
    sourceTelegramId = s[1]
    targetTelegramId = s[2][0:-1]
    return skill, sourceTelegramId, targetTelegramId

def score_pack(sourceTelegramId, targetTelegramId, skillName, score):
    return f"score_({sourceTelegramId})#({targetTelegramId})#({skillName})#({score})"

def score_unpack(packed_score):
    s = packed_score.split(')#(')
    sourceTelegramId = s[0][len("skill_("):]
    targetTelegramId = s[1]
    skillName = s[2]
    score = s[3][0:-1]
    return sourceTelegramId, targetTelegramId, skillName, score

def assessment_pack(sourceTelegramId, targetTelegramId):
    return f"assessment_({sourceTelegramId})#({targetTelegramId})"

def assessment_unpack(packed_assessment):
    s = packed_assessment.split(')#(')
    sourceTelegramId = s[0][len("assessment_("):]
    targetTelegramId = s[1][0:-1]
    return sourceTelegramId, targetTelegramId

# Отлавливаем /start, проверяем права доступа, запускаем сценарий
@bot.message_handler(commands=['start'])
def start(message):
    user = message.from_user
    bot.send_message(message.chat.id, f"Привет, {user.first_name}!")

    # Заполняем базу дефолтными значениями
    # response = requests.post(f"{BASE_URL}/api/v1/assessment/reformat")
    # print(response)

    rights = get_access_rights(user.username)

    if rights == "ADMIN":
        bot.send_message(message.chat.id, f"Вы вошли как админ", reply_markup=admin_script())
    elif rights == "USER":
        bot.send_message(message.chat.id, f"Вы вошли как сотрудник.", reply_markup=worker_keyboard(user.username, message.chat.id))
    else:
        bot.send_message(message.chat.id, f"Тебя нет в базе данных, обратись в отдел кадров.")


# Функция для создания клавиатуры сотрудника
def worker_keyboard(sourceTelegramId, chatId):
    keyboard = types.ReplyKeyboardMarkup(row_width=1, resize_keyboard=True)

    workers = get_workers_by_id(sourceTelegramId)

    if workers is not None:
        button_search = types.KeyboardButton('Поиск сотрудника')
        keyboard.add(button_search)
        for i in workers:
            colleague_id = i["telegramId"]
            colleague_name = i["firstName"]
            colleague_second_name = i["lastName"]
            button = types.KeyboardButton(f"Пройти ассесмент для @{colleague_id}: {colleague_name}, {colleague_second_name}")
            keyboard.add(button)
    else:
        bot.send_message(chatId, f"Нет доступных ассесментов.")
    return keyboard

# Обработчик нажатия на кнопку с ассесментом
@bot.message_handler(func=lambda message: message.text.startswith('Пройти ассесмент для @'))
def handle_assessment(message):
    parts = message.text.split(':')
    colleague_id = parts[0].split('@')[1].strip()

    sourceTelegramId = message.from_user.username

    mess = bot.send_message(message.chat.id, '_', reply_markup=types.ReplyKeyboardRemove())
    bot.delete_message(mess.chat.id, mess.message_id)

    targetTelegramId = colleague_id
    bot.send_message(message.chat.id, f"Вы выбрали прохождение ассесмента для коллеги с ID {colleague_id}", reply_markup=assessment_keyboard(sourceTelegramId, targetTelegramId))

# Функция для создания клавиатуры с ассесментом
def assessment_keyboard(sourceTelegramId, targetTelegramId):
    keyboard = types.InlineKeyboardMarkup()

    assessment = get_assessment_by_id(sourceTelegramId, targetTelegramId)

    for skill in assessment:
        button_skill = types.InlineKeyboardButton(f"{skill}", callback_data=skill_pack(skill, sourceTelegramId, targetTelegramId))
        keyboard.add(button_skill)

    button_back = types.InlineKeyboardButton(f"Назад", callback_data=f"skill_back_{sourceTelegramId}")
    keyboard.add(button_back)

    return keyboard

# Обработчик нажатия на кнопку в ассесменте
@bot.callback_query_handler(func=lambda call: True)
def callback_query(call):
    if call.data.startswith("assessment_"):
        sourceTelegramId, targetTelegramId = assessment_unpack(call.data)
        bot.delete_message(call.message.chat.id, call.message.message_id)
        bot.send_message(call.message.chat.id, f"Вы выбрали прохождение ассесмента для коллеги с ID @{targetTelegramId}", reply_markup=assessment_keyboard(sourceTelegramId, targetTelegramId))

    if call.data.startswith("skill_back_"):
            sourceTelegramId = call.data[len("skill_back_"):]
            bot.delete_message(call.message.chat.id, call.message.message_id)
            bot.send_message(call.message.chat.id, f"Выберите сотрудника.", reply_markup=worker_keyboard(sourceTelegramId, call.message.chat.id))
    
    elif call.data.startswith("skill_"):
        skillName, sourceTelegramId, targetTelegramId = skill_unpack(call.data)
        bot.edit_message_reply_markup(call.message.chat.id, call.message.message_id, reply_markup=score_keyboard(sourceTelegramId, targetTelegramId, skillName))

    if call.data.startswith("score_"):
        sourceTelegramId, targetTelegramId, skillName, score = score_unpack(call.data)
        if score != "back":
            send_score(sourceTelegramId, targetTelegramId, skillName, score)
        bot.edit_message_reply_markup(call.message.chat.id, call.message.message_id, reply_markup=assessment_keyboard(sourceTelegramId, targetTelegramId))

    if call.data.startswith("matrix_"):
        username = call.data[len("matrix_"):]
        matrix = get_matrix_by_id(username)
        skills = matrix["skills"]
        response = f"Информация по сотруднику @{username}."
        for skill in skills:
            response += f"\n{skill["name"]} : результат оценки - {skill["level"]}, вердикт - {skill["rating"]}"
        bot.send_message(call.message.chat.id, response, reply_markup=admin_script())



def score_keyboard(sourceTelegramId, targetTelegramId, skillName):
    keyboard = types.InlineKeyboardMarkup()

    button_refrain = types.InlineKeyboardButton(f"Воздержаться", callback_data=score_pack(sourceTelegramId, targetTelegramId, skillName, 0))
    keyboard.add(button_refrain)

    row1_buttons = []
    row2_buttons = []

    for i in range(10):
        score = i+1
        button = types.InlineKeyboardButton(f"{i + 1}", callback_data=score_pack(sourceTelegramId, targetTelegramId, skillName, score))
        if i < 5:
            row1_buttons.append(button)
        else:
            row2_buttons.append(button)
    keyboard.row(*row1_buttons)
    keyboard.row(*row2_buttons)

    button_back = types.InlineKeyboardButton(f"Назад", callback_data=score_pack(sourceTelegramId, targetTelegramId, skillName, "back"))
    keyboard.add(button_back)

    return keyboard

# Обработчик кнопки "Поиск сотрудника"
@bot.message_handler(func=lambda message: message.text == 'Поиск сотрудника')
def handle_search_employee(message):
    mess = bot.send_message(message.chat.id, '_', reply_markup=types.ReplyKeyboardRemove())
    bot.delete_message(mess.chat.id, mess.message_id)
    bot.send_message(message.chat.id, "Введите информацию о сотруднике в формате: @TelegramID Фамилия Имя\nИли подстроку")
    bot.register_next_step_handler(message,handle_worker_info)

# Функция для поиска сотрудника
def handle_worker_info(message):

    try:
        search_query = message.text.lower()  # Преобразуем запрос пользователя к нижнему регистру для удобства поиска
        colleagues = get_workers_by_id(message.from_user.username)
        found_colleagues = []

        for i in colleagues:
            colleague_id = i["telegramId"]
            colleague_name = i["firstName"]
            colleague_second_name = i["lastName"]
            # Формируем строку с информацией о сотруднике
            colleague_info = f"@{colleague_id} {colleague_name} {colleague_second_name}"

            # Проверяем, содержится ли поисковый запрос в информации о сотруднике
            if search_query in colleague_info.lower():
                found_colleagues.append([colleague_id, colleague_name, colleague_second_name])

        if found_colleagues:
            
            keyboard = types.InlineKeyboardMarkup()
            for i in found_colleagues:
                button = types.InlineKeyboardButton(f"@{i[0]}: {i[1]}, {i[2]}", callback_data=assessment_pack(sourceTelegramId=message.from_user.username, targetTelegramId=i[0]))
                keyboard.add(button)
                
            bot.send_message(message.chat.id, "Найденные сотрудники:\n", reply_markup=keyboard)

        else:
            bot.send_message(message.chat.id, "Сотрудников не найдено.", reply_markup=worker_keyboard(message.from_user.username, message.chat.id))

    except:
        bot.send_message(message.chat.id, "Я не понимаю что вы ввели.", reply_markup=worker_keyboard(message.from_user.username, message.chat.id))
        

# Создание клавиатуры администратора
def admin_script():
    keyboard = types.ReplyKeyboardMarkup(row_width=1, resize_keyboard=True)
    buttons = ["Открыть ассесмент", "Получить отчет по одному сотруднику","Закрыть ассесмент"]
    for button_text in buttons:
        button = types.KeyboardButton(button_text)
        keyboard.add(button)
    return keyboard

@bot.message_handler(func=lambda message: message.text == 'Открыть ассесмент')
def open_assessment(message):
    
    send_open_assessments()
    bot.send_message(message.chat.id, "Ассесмент открыт!")
    
@bot.message_handler(func=lambda message: message.text == 'Закрыть ассесмент')
def close_assessment(message):
    send_close_assessments()
    bot.send_message(message.chat.id, "Ассесмент закрыт!")

@bot.message_handler(func=lambda message: message.text == 'Получить отчет по одному сотруднику')
def handle_search_employee(message):
    
    mess = bot.send_message(message.chat.id, '_', reply_markup=types.ReplyKeyboardRemove())
    bot.delete_message(mess.chat.id, mess.message_id)

    bot.send_message(message.chat.id, "Введите информацию о сотруднике в формате: @TelegramID Фамилия Имя")
    bot.register_next_step_handler(message,admin_handle_worker_info)

def admin_handle_worker_info(message):
    try:
        search_query = message.text.lower()  # Преобразуем запрос пользователя к нижнему регистру для удобства поиска
        colleagues = get_workers_by_id(message.from_user.username)
        found_colleagues = []

        for i in colleagues:
            colleague_id = i["telegramId"]
            colleague_name = i["firstName"]
            colleague_second_name = i["lastName"]
            # Формируем строку с информацией о сотруднике
            colleague_info = f"@{colleague_id} {colleague_name} {colleague_second_name}"

            # Проверяем, содержится ли поисковый запрос в информации о сотруднике
            if search_query in colleague_info.lower():
                found_colleagues.append([colleague_id, colleague_name, colleague_second_name])

        if found_colleagues:
            
            keyboard = types.InlineKeyboardMarkup()
            for i in found_colleagues:
                button = types.InlineKeyboardButton(f"@{i[0]}: {i[1]}, {i[2]}", callback_data=f"matrix_{i[0]}")
                keyboard.add(button)
                
            bot.send_message(message.chat.id, "Найденные сотрудники:\n", reply_markup=keyboard)

        else:
            bot.send_message(message.chat.id, "Сотрудников не найдено.", reply_markup=admin_script())

    except:
        bot.send_message(message.chat.id, "Я не понимаю что вы ввели.", reply_markup=admin_script())


if __name__ == '__main__':
    bot.polling()
