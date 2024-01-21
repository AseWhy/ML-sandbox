/**
 * Игра в крестики нолики...
 */
import { tensor2d, sequential, layers, Tensor } from "@tensorflow/tfjs-node";
import { join } from "path";
import { createInterface } from "readline";

/** Количество эпох обучения модели */
const EPOCHS = 500;
/** Инфтерфейс чтения ввода юзера */
const readline = createInterface({ input: process.stdin, output: process.stdout });

/**
 * Запрос ввода
 * @param msg сообщение ввода
 * @returns обещание, с сообщение ответа
 */
function question(msg: string): Promise<string> {
  return new Promise(res => readline.question(msg, res));
}

/**
 * Распечатать доску
 * @param ground доска
 */
function print(ground: number[]) {
  for(let i = 0; i < ground.length; i += 3) {
    console.log(ground.slice(i, i + 3).map(e => e == 1 ? "[x]" : e == -1 ? "[0]" : "[ ]").join(" "));
  }
}

/**
 * Получить победителя текущей партии
 * @param ground игровая площадка
 * @returns победитель текщей партии 1 - игрок -1 - машина 0 - никто -2 - все клетки заполнены
 */
function checkWinner(ground: number[]) {
  // Если все клетки заполнены
  if (!ground.some(e => e == 0)) {
    return -2;
  }
  // Итерируем 1 это игрок -1 это нейронка
  for (const c of [1, -1]) {
    // Если ячейки по диагонали равны текущему юзеру
    if ([0, 4, 8].map(e => ground[e]).every(e => e == c) ||
        [2, 4, 6].map(e => ground[e]).every(e => e == c)) {
          return c;
    }
    // Либо все столбцы равны
    for(let i = 0; i < 3; i++) {
      const hor = ground.slice(i * 3, i * 3 + 3);
      const ver = ground.filter((e, id) => (id + i) % 3 == 0)
      // Если все элементы по горизонтали или по вертикали равны текущему юезру
      if (hor.every(e => e === c) || ver.every(e => e === c)) {
        return c;
      }
    }
  }
  return 0;
}

void async function() {
  // Создаем модель
  const model = sequential();
  // Добавляем слой с 9-ю входящими и выходящими нейронами
  model.add(layers.dense({ units: 9, inputShape: [ 9 ], activation: "relu" }));
  // Компилируем модель, добавлем функцию обработки потерь и оптимизатор и метрикой точности
  model.compile({ loss: 'meanSquaredError', optimizer: 'adam', metrics: ['acc'] });
  // Загружаем данные для обучения
  const data = require(join(process.cwd(), "train/tic-tac-toe.json")) as number[][][];
  // Обучаем модель
  await model.fit(tensor2d(data.map(e => e[0])), tensor2d(data.map(e => e[1])), { epochs: EPOCHS, batchSize: 1 });

  // Создаем пустую игровую площадку 3 на 3
  const ground = [0, 0, 0, 0, 0, 0, 0, 0, 0];
  // Состояния карты
  const states: number[][] = [];
  // Шаги предпринятые ботом
  const steps: number[][] = [];

  /**
   * Проверить состояние текущего шага
   * @returns true если раунд завершен
   */
  async function check() {
    console.clear();
    const winner = checkWinner(ground);
    // Бот выйграл
    if (winner == -1) {
      console.log("Бот победил!! Пару секунд, сейчас я переобучусь...")
      // Если бот выйграл, значит действия были верными переобучаем модель
      await model.fit(tensor2d(states), tensor2d(steps), { epochs: EPOCHS });
      console.log("Готово! Играем дальше...")
    } else if (winner == 1) {
      // Сообщение о победе
      console.log("Человек победил!(((")
    }
    // Если все заполнено то очищаем карту
    if (winner == -2) {
      console.log("Кажется нам нехватает место, сейчас все почищу!)");
    }
    print(ground);
    // Если есть победитель
    if (winner != 0) {
      states.splice(0, states.length);
      steps.splice(0, steps.length);
      ground.fill(0, 0, 9);
      console.log("Играем ещё раз!");
      print(ground);
      return true;
    }
  }

  while(true) {
    const answer: string = await question("Введите координаты крестика (x, y) от 0: ");
    const coords = answer.split(" ").map(e => parseInt(e)) as number[];
    ground[coords[0] + coords[1] * 3] = 1;
    if (await check()) {
      continue;
    }
    // Нейронка делает ход
    const predicted = model.predict(tensor2d([ ground ])) as Tensor;
    const predict = await predicted.array() as number[][];
    // Выбираем наиболее вероятное решение
    const cell: number[] = predict[0].map((e, i) => [ i, e ]).sort((a, b) => a[1] - b[1]).filter(e => ground[e[0]] == 0).map(e => e[0]);
    if (cell == null) {
      continue;
    }
    const step = new Array(9);
    // Пишем в иторию, потом будем учится на этом
    step.fill(0);
    step[cell[0]] = 1;
    ground[cell[0]] = -1;
    steps.push(step);
    states.push(Array.from(ground));
    // Проверяем, вдруг мы выйграли
    await check();
  }
}();