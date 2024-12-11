package com.sample;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class DroolsTest {

    private KieSession kSession;
    private Pytanie pytanie;
    private BestBookUI ui;
    private FactHandle factHandle;

    public static void main(String[] args) {
        try {
            // Load up the knowledge base
            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();
            KieSession kSession = kContainer.newKieSession("ksession-rules");

            Pytanie pytanie = new Pytanie();
            FactHandle factHandle = kSession.insert(pytanie);
            kSession.fireAllRules();

            new DroolsTest(kSession, pytanie, factHandle);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public DroolsTest(KieSession kSession, Pytanie pytanie, FactHandle factHandle) {
        this.kSession = kSession;
        this.pytanie = pytanie;
        this.factHandle = factHandle;

        // Ustaw pytanie początkowe
        this.pytanie.setTresc("Do you want some good popular fiction?");
        this.pytanie.setOpcje(new String[]{"Yes", "No"});

        // Zainicjalizuj GUI
        updateQuestion();
    }

    private void updateQuestion() {
        String question = pytanie.getTresc();
        Vector<String> answers = new Vector<>();
        for (String option : pytanie.getOpcje()) {
            answers.add(option);
        }

        if (ui == null) {
            // Tworzymy UI dla pierwszego pytania
            ui = new BestBookUI(question, answers, answer -> {
                if (answer != null && !answer.isEmpty()) {
                    pytanie.setOdpowiedz(answer);
                    System.out.println("Selected answer: " + answer);

                    // Zaktualizuj fakt w sesji i uruchom zasady
                    if (factHandle != null) {
                        kSession.update(factHandle, pytanie);
                    } else {
                        System.out.println("FactHandle not found, inserting the fact again.");
                        factHandle = kSession.insert(pytanie);
                    }
                    kSession.fireAllRules();

                    // Odśwież pytanie w GUI
                    updateQuestion();
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an answer!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            ui.createAndShowGUI(true);
        } else {
            // Aktualizujemy istniejące UI z nowym pytaniem i odpowiedziami
            ui.updateQuestion(question, answers);
        }
    }

    public static class Pytanie {
        private String tresc;
        private String[] opcje;
        private String odpowiedz;

        public String getTresc() {
            return tresc;
        }

        public void setTresc(String tresc) {
            this.tresc = tresc;
        }

        public String[] getOpcje() {
            return opcje;
        }

        public void setOpcje(String[] opcje) {
            this.opcje = opcje;
        }

        public String getOdpowiedz() {
            return odpowiedz;
        }

        public void setOdpowiedz(String odpowiedz) {
            this.odpowiedz = odpowiedz;
        }
    }

    public static class BestBookUI extends JPanel {

        private static final long serialVersionUID = 510L;
        private JTextArea output;
        private AnswerCallback callback;

        public BestBookUI(String question, Vector<String> answers, AnswerCallback callback) {
            super(new BorderLayout());
            this.callback = callback;

            JPanel questionPanel = new JPanel();
            questionPanel.setLayout(new BorderLayout());
            questionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JLabel questionLabel = new JLabel(question);
            questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
            questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
            questionPanel.add(questionLabel, BorderLayout.CENTER);

            add(questionPanel, BorderLayout.NORTH);

            // Panel z odpowiedziami
            JPanel answersPanel = new JPanel();
            answersPanel.setLayout(new GridLayout(answers.size(), 1, 5, 5));
            answersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            for (String answer : answers) {
                JButton answerButton = new JButton(answer);
                answerButton.addActionListener(e -> callback.onAnswerSelected(answer));
                answersPanel.add(answerButton);
            }

            add(new JScrollPane(answersPanel), BorderLayout.CENTER);

            // Panel wyjściowy
            output = new JTextArea(5, 30);
            output.setEditable(false);
            JScrollPane outputPane = new JScrollPane(output);
            outputPane.setBorder(BorderFactory.createTitledBorder("Output"));
            add(outputPane, BorderLayout.SOUTH);
        }

        public void setOutputText(String text) {
            output.setText(text);
        }

        public void createAndShowGUI(boolean exitOnClose) {
            JFrame frame = new JFrame("BestBook UI");
            frame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);

            setOpaque(true);
            frame.setContentPane(this);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }

        public void updateQuestion(String question, Vector<String> answers) {
            // Zaktualizuj etykietę pytania
            ((JLabel)((JPanel)getComponent(0)).getComponent(0)).setText(question);

            // Zaktualizuj panel odpowiedzi
            JPanel answersPanel = (JPanel)((JScrollPane)getComponent(1)).getViewport().getView();
            answersPanel.removeAll();
            for (String answer : answers) {
                JButton answerButton = new JButton(answer);
                answerButton.addActionListener(e -> callback.onAnswerSelected(answer));
                answersPanel.add(answerButton);
            }
            answersPanel.revalidate();
            answersPanel.repaint();
        }

        public interface AnswerCallback {
            void onAnswerSelected(String answer);
        }
    }
}
