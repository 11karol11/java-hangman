package pl.edu.agh.hangman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Hangman {

    // Interfejs do strategii animacji
    public interface AnimationStrategy {
        String getHangmanPic(int attemptsLeft);
        int getMaxAttempts();
    }

    // Implementacja podstawowej animacji
    public static class DefaultAnimationStrategy implements AnimationStrategy {
        private static final String[] HANGMANPICS = new String[]{
                "  +---+\n" +
                        "  |   |\n" +
                        "      |\n" +
                        "      |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========",
                "  +---+\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        "      |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========",
                "  +---+\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        "  |   |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========",
                "  +---+\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        " /|   |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========",
                "  +---+\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        " /|\\  |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========",
                "  +---+\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        " /|\\  |\n" +
                        " /    |\n" +
                        "      |\n" +
                        "=========",
                "  +---+\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        " /|\\  |\n" +
                        " / \\  |\n" +
                        "      |\n" +
                        "========"
        };

        @Override
        public String getHangmanPic(int attemptsLeft) {
            return HANGMANPICS[HANGMANPICS.length - 1 - attemptsLeft];
        }

        @Override
        public int getMaxAttempts() {
            return HANGMANPICS.length - 1;
        }
    }

    // Interfejs do strategii pozyskiwania słów
    public interface WordSourceStrategy {
        String getWord(int minLength, int maxLength) throws IOException;
    }

    // Implementacja pozyskiwania słów z pliku
    public static class FileWordSourceStrategy implements WordSourceStrategy {
        private final String filePath;

        public FileWordSourceStrategy(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public String getWord(int minLength, int maxLength) throws IOException {
            List<String> words = Files.readAllLines(Paths.get(filePath));
            List<String> filteredWords = new ArrayList<>();

            for (String word : words) {
                word = word.trim().toUpperCase();
                if (word.length() >= minLength && word.length() <= maxLength) {
                    filteredWords.add(word);
                }
            }

            if (filteredWords.isEmpty()) {
                throw new IOException("Brak słów w zadanym zakresie długości!");
            }

            return filteredWords.get(new Random().nextInt(filteredWords.size()));
        }
    }

    // Implementacja pozyskiwania słów z Wordnik
    public static class WordnikWordSourceStrategy implements WordSourceStrategy {
        private final String apiKey;

        public WordnikWordSourceStrategy(String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public String getWord(int minLength, int maxLength) throws IOException {
            String urlString = String.format("https://api.wordnik.com/v4/words.json/randomWord?minLength=%d&maxLength=%d&apiKey=%s",
                    minLength, maxLength, apiKey);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();

            // Parsowanie JSON (proste podejście, można dodać bibliotekę do parsowania JSON)
            String word = content.toString().split(":")[1].split("\"")[1].toUpperCase();
            return word;
        }
    }

    public static void main(String[] args) {
        // Parametry gry
        Scanner scanner = new Scanner(System.in);
        System.out.print("Podaj minimalną długość słowa: ");
        int minLength = scanner.nextInt();
        System.out.print("Podaj maksymalną długość słowa: ");
        int maxLength = scanner.nextInt();
        scanner.nextLine(); // Konsumowanie nowej linii

        // Wybór strategii animacji i słów
        AnimationStrategy animationStrategy = new DefaultAnimationStrategy();
        System.out.print("Podaj API Key do Wordnik (lub pozostaw puste aby użyć pliku): ");
        String apiKey = scanner.nextLine().trim();
        WordSourceStrategy wordSourceStrategy;

        if (apiKey.isEmpty()) {
            wordSourceStrategy = new FileWordSourceStrategy("C:\\Users\\Public\\Documents\\hangman\\java-hangman\\src\\main\\resources\\slowa.txt");
        } else {
            wordSourceStrategy = new WordnikWordSourceStrategy(apiKey);
        }

        // Gra
        String wordToGuess;
        try {
            wordToGuess = wordSourceStrategy.getWord(minLength, maxLength);
        } catch (IOException e) {
            System.out.println("Błąd wczytywania słowa: " + e.getMessage());
            return;
        }

        char[] guessedWord = new char[wordToGuess.length()];
        Arrays.fill(guessedWord, '_');
        Set<Character> guessedLetters = new HashSet<>();
        int attemptsLeft = animationStrategy.getMaxAttempts();

        while (attemptsLeft > 0) {
            System.out.println("\n" + animationStrategy.getHangmanPic(attemptsLeft));
            System.out.println("Słowo: " + String.valueOf(guessedWord));
            System.out.println("Pozostałe próby: " + attemptsLeft);
            System.out.print("Podaj literę: ");

            String input = scanner.nextLine().toUpperCase();
            if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
                System.out.println("Podaj pojedynczą literę!");
                continue;
            }

            char guessedLetter = input.charAt(0);
            if (guessedLetters.contains(guessedLetter)) {
                System.out.println("Już próbowałeś tej litery!");
                continue;
            }

            guessedLetters.add(guessedLetter);

            if (wordToGuess.indexOf(guessedLetter) >= 0) {
                for (int i = 0; i < wordToGuess.length(); i++) {
                    if (wordToGuess.charAt(i) == guessedLetter) {
                        guessedWord[i] = guessedLetter;
                    }
                }
                if (String.valueOf(guessedWord).equals(wordToGuess)) {
                    System.out.println("Brawo! Odgadłeś słowo: " + wordToGuess);
                    return;
                }
            } else {
                attemptsLeft--;
                System.out.println("Nie ma tej litery w słowie!");
            }
        }

        System.out.println("\n" + animationStrategy.getHangmanPic(attemptsLeft));
        System.out.println("Przegrałeś! Słowo to: " + wordToGuess);
    }
}