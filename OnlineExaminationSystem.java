import java.util.*;

public class OnlineExaminationSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Question> questions = new ArrayList<>();
    private static int score = 0;

    public static void main(String[] args) {
        System.out.println("Welcome to the Online Examination System!");
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Admin - Create Exam");
            System.out.println("2. Student - Attempt Exam");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 1 -> adminMenu();
                case 2 -> studentMenu();
                case 3 -> {
                    System.out.println("Thank you for using the Online Examination System!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void adminMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.print("Enter the number of questions to add: ");
        int numQuestions = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        for (int i = 0; i < numQuestions; i++) {
            System.out.println("\nEnter details for Question " + (i + 1) + ":");
            System.out.print("Enter the question text: ");
            String questionText = scanner.nextLine();

            String[] options = new String[4];
            for (int j = 0; j < 4; j++) {
                System.out.print("Enter option " + (j + 1) + ": ");
                options[j] = scanner.nextLine();
            }

            System.out.print("Enter the correct option number (1-4): ");
            int correctOption = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            questions.add(new Question(questionText, options, correctOption));
        }
        System.out.println("Questions added successfully!");
    }

    private static void studentMenu() {
        if (questions.isEmpty()) {
            System.out.println("\nNo exams are currently available. Please contact the admin.");
            return;
        }

        System.out.println("\n=== Exam Start ===");
        System.out.println("You will have 30 seconds to answer each question. Good luck!");

        Timer timer = new Timer();
        Iterator<Question> questionIterator = questions.iterator();

        while (questionIterator.hasNext()) {
            Question question = questionIterator.next();
            System.out.println("\n" + question.getQuestionText());
            String[] options = question.getOptions();

            for (int i = 0; i < options.length; i++) {
                System.out.println((i + 1) + ". " + options[i]);
            }

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("\nTime's up for this question! Moving to the next one...");
                }
            }, 30000); // 30 seconds

            System.out.print("Enter your answer (1-4): ");
            int answer = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            if (answer == question.getCorrectOption()) {
                System.out.println("Correct!");
                score++;
            } else {
                System.out.println("Wrong! The correct answer was: " + question.getCorrectOption());
            }

            timer.cancel();
            timer.purge();
        }

        System.out.println("\n=== Exam Ended ===");
        System.out.println("Your final score: " + score + "/" + questions.size());
    }
}

class Question {
    private final String questionText;
    private final String[] options;
    private final int correctOption;

    public Question(String questionText, String[] options, int correctOption) {
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectOption() {
        return correctOption;
    }
}
