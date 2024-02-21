package ru.astecom.webcam;

import com.twelvemonkeys.image.ImageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.astecom.support.ApplicationConfigure;
import ru.astecom.support.ApplicationHelper;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Тесты распознования картинок с вебкамеры
 */
class WebcamNumberDetectorTest {

    /** Обнаружитель чисел */
    private final WebcamNumberDetector detector;

    /**
     * Конструктор
     */
    public WebcamNumberDetectorTest() {
        detector = new WebcamNumberDetector(28, 28, ApplicationHelper.getModelPath(WebcamModelTrainer.MODEL_FILE_NAME));
    }

    /**
     * Перед тестами обновляем конфигурацию
     */
    @BeforeAll
    public static void before() {
        ApplicationConfigure.configure();
    }

    /**
     * Выполнить проверку соответствия результата возвращаемого нейросетью с ожидаемым результатом
     * @param example  пример для распознования
     * @param expected ожидаемое значение
     * @throws URISyntaxException если путь не валиден
     * @throws IOException если произошла ошибка ввода вывода
     */
    private void testNumberClassificationPrediction(String example, int expected) throws URISyntaxException, IOException {
        var in = ImageIO.read(ClassLoader.getSystemResource(example).toURI().toURL());
        var scaled = WebcamUtils.toBlackAndWhite(ImageUtil.createScaled(in, 28, 28, 0));
        Assertions.assertEquals(expected, detector.detect(scaled));
    }

    /**
     * Выполнить проверку распознования цифры 0
     * @throws URISyntaxException если путь не валиден
     * @throws IOException если произошла ошибка ввода вывода
     */
    @Test
    public void TestNumberClassificationPrediction0() throws URISyntaxException, IOException {
        testNumberClassificationPrediction("images/test-0.png", 0);
    }

    /**
     * Выполнить проверку распознования цифры 1
     * @throws URISyntaxException если путь не валиден
     * @throws IOException если произошла ошибка ввода вывода
     */
    @Test
    public void TestNumberClassificationPrediction1() throws URISyntaxException, IOException {
        testNumberClassificationPrediction("images/test-1.png", 1);
    }

    /**
     * Выполнить проверку распознования цифры 2
     * @throws URISyntaxException если путь не валиден
     * @throws IOException если произошла ошибка ввода вывода
     */
    @Test
    public void TestNumberClassificationPrediction2() throws URISyntaxException, IOException {
        testNumberClassificationPrediction("images/test-2.png", 2);
    }

    /**
     * Выполнить проверку распознования цифры 3
     * @throws URISyntaxException если путь не валиден
     * @throws IOException если произошла ошибка ввода вывода
     */
    @Test
    public void TestNumberClassificationPrediction3() throws URISyntaxException, IOException {
        testNumberClassificationPrediction("images/test-3.png", 3);
    }

    /**
     * Выполнить проверку распознования цифры 4
     * @throws URISyntaxException если путь не валиден
     * @throws IOException если произошла ошибка ввода вывода
     */
    @Test
    public void TestNumberClassificationPrediction4() throws URISyntaxException, IOException {
        testNumberClassificationPrediction("images/test-4.png", 4);
    }

    /**
     * Выполнить проверку распознования цифры 5
     * @throws URISyntaxException если путь не валиден
     * @throws IOException если произошла ошибка ввода вывода
     */
    @Test
    public void TestNumberClassificationPrediction5() throws URISyntaxException, IOException {
        testNumberClassificationPrediction("images/test-5.png", 5);
    }

    /**
     * Выполнить проверку распознования цифры 6
     * @throws URISyntaxException если путь не валиден
     * @throws IOException если произошла ошибка ввода вывода
     */
    @Test
    public void TestNumberClassificationPrediction6() throws URISyntaxException, IOException {
        testNumberClassificationPrediction("images/test-6.png", 6);
    }

    /**
     * Выполнить проверку распознования цифры 7
     * @throws URISyntaxException если путь не валиден
     * @throws IOException если произошла ошибка ввода вывода
     */
    @Test
    public void TestNumberClassificationPrediction7() throws URISyntaxException, IOException {
        testNumberClassificationPrediction("images/test-7.png", 7);
    }

    /**
     * Выполнить проверку распознования цифры 8
     * @throws URISyntaxException если путь не валиден
     * @throws IOException если произошла ошибка ввода вывода
     */
    @Test
    public void TestNumberClassificationPrediction8() throws URISyntaxException, IOException {
        testNumberClassificationPrediction("images/test-8.png", 8);
    }

    /**
     * Выполнить проверку распознования цифры 9
     * @throws URISyntaxException если путь не валиден
     * @throws IOException если произошла ошибка ввода вывода
     */
    @Test
    public void TestNumberClassificationPrediction9() throws URISyntaxException, IOException {
        testNumberClassificationPrediction("images/test-9.png", 9);
    }
}