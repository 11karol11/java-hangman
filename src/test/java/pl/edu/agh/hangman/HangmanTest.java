import org.junit.jupiter.api.Test;
import pl.edu.agh.hangman.Hangman;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class HangmanTest {

    // Test dla klasy DefaultAnimationStrategy
    @Test
    public void testDefaultAnimationStrategy() {
        Hangman.DefaultAnimationStrategy animationStrategy = new Hangman.DefaultAnimationStrategy();

        // Proste testowanie animacji
        String hangmanPic6 = animationStrategy.getHangmanPic(6);
        assertNotNull(hangmanPic6);  // Sprawdzamy, czy nie jest null
        assertTrue(hangmanPic6.contains("O"));  // Sprawdzamy, czy zawiera "O", które oznacza pierwszą próbę

        String hangmanPic5 = animationStrategy.getHangmanPic(5);
        assertNotNull(hangmanPic5);
        assertTrue(hangmanPic5.contains("O"));  // Sprawdzamy, czy zawiera "O"
    }

    // Test dla metody getMaxAttempts w DefaultAnimationStrategy
    @Test
    public void testMaxAttempts() {
        Hangman.DefaultAnimationStrategy animationStrategy = new Hangman.DefaultAnimationStrategy();
        int maxAttempts = animationStrategy.getMaxAttempts();
        assertEquals(6, maxAttempts); // Sprawdzamy, czy liczba maksymalnych prób wynosi 6
    }

    // Test dla klasy FileWordSourceStrategy
    @Test
    public void testFileWordSourceStrategy() throws IOException {
        // Używamy małego pliku z danymi do testowania (załóżmy, że plik istnieje)
        Hangman.FileWordSourceStrategy wordSourceStrategy = new Hangman.FileWordSourceStrategy("C:\\Users\\Public\\Documents\\hangman\\java-hangman\\src\\main\\resources\\slowa.txt");

        // Prosty test: pobierz słowo z pliku, którego długość mieści się w przedziale
        String word = wordSourceStrategy.getWord(3, 6);
        assertNotNull(word);
        assertTrue(word.length() >= 3 && word.length() <= 6); // Sprawdzamy, czy długość słowa jest w odpowiednim zakresie
    }

    // Test dla klasy FileWordSourceStrategy w przypadku braku słów w zadanym zakresie
    @Test
    public void testFileWordSourceStrategyNoWordsInRange() {
        // Używamy małego pliku z danymi do testowania
        Hangman.FileWordSourceStrategy wordSourceStrategy = new Hangman.FileWordSourceStrategy("C:\\Users\\Public\\Documents\\hangman\\java-hangman\\src\\main\\resources\\slowa.txt");

        // Sprawdzamy, czy wyjątek jest rzucany, gdy słowa nie mieszczą się w zadanym zakresie
        try {
            wordSourceStrategy.getWord(100, 200);
            fail("Oczekiwano wyjątku IOException, ale go nie wystąpił.");
        } catch (IOException e) {
            // Oczekiwany wyjątek, test przechodzi pomyślnie
            assertTrue(e.getMessage().contains("Brak słów"));
        }
    }

    // Test dla klasy WordnikWordSourceStrategy
    @Test
    public void testWordnikWordSourceStrategy() throws IOException {
        // Załóżmy, że API zawsze zwraca słowo "JAVA" (bez mockowania)
        Hangman.WordnikWordSourceStrategy wordnikWordSourceStrategy = new Hangman.WordnikWordSourceStrategy("mockApiKey");

        // Prosty test: zwrócone słowo "JAVA" w zakresie 3-5 liter
        String word = wordnikWordSourceStrategy.getWord(3, 5);
        assertNotNull(word);
        assertEquals("JAVA", word); // Zakładając, że API zawsze zwróci "JAVA"
    }

    // Test, który sprawdza, czy IOException jest wyrzucane w przypadku błędu (np. błędne dane wejściowe)
    @Test
    public void testWordnikWordSourceStrategyIOException() {
        Hangman.WordnikWordSourceStrategy wordnikWordSourceStrategy = new Hangman.WordnikWordSourceStrategy("mockApiKey");

        // Sprawdzamy, czy wyjątek jest wyrzucany, gdy połączenie jest błędne (np. złe dane wejściowe)
        try {
            wordnikWordSourceStrategy.getWord(100, 200); // Zbyt duże długości
            fail("Oczekiwano wyjątku IOException, ale go nie wystąpił.");
        } catch (IOException e) {
            // Oczekiwany wyjątek
            assertTrue(e.getMessage().contains("Brak połączenia"));
        }
    }
}