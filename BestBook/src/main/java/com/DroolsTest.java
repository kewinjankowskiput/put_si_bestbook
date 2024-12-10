package com.sample;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {

    public static final void main(String[] args) {
        try {
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession kSession = kContainer.newKieSession("ksession-rules");

            
            Pytanie pytanie = new Pytanie();
            kSession.insert(pytanie);
            kSession.fireAllRules();
            new DroolsTest().init(kContainer, true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    public DroolsTest() {
    }

    public void init(KieContainer kContainer, boolean exitOnClose) {
        String question = "What is your favorite type of book?";
        Vector<String> answers = new Vector<>();
        answers.add("Fiction");
        answers.add("Non-fiction");
        answers.add("Science");
        answers.add("Fantasy");
        answers.add("Mystery");

        BestBookUI ui = new BestBookUI(question, answers, answer -> {
            System.out.println("Selected answer: " + answer);
        });

        ui.createAndShowGUI(exitOnClose);
    }

    /**
     * GUI to display a question and answers.
     */
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

            // Tworzenie panelu z przyciskami odpowiedzi
            JPanel answersPanel = new JPanel();
            answersPanel.setLayout(new GridLayout(answers.size(), 1, 5, 5));
            answersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            for (String answer : answers) {
                JButton answerButton = new JButton(answer);
                answerButton.addActionListener(e -> callback.onAnswerSelected(answer));
                answersPanel.add(answerButton);
            }

            add(new JScrollPane(answersPanel), BorderLayout.CENTER);

            // Panel do wyświetlania wyników
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

        public interface AnswerCallback {
            void onAnswerSelected(String answer);
        }
    }

    public static class Pytanie {

    	public Pytanie() {
        }

        public Pytanie(String tresc, String odpowiedz) {
            this.tresc = tresc;
            this.odpowiedz = odpowiedz;
        }

        private String tresc;
        private String odpowiedz;

        public String getTresc() {
            return this.tresc;
        }

        public void setTresc(String tresc) {
            this.tresc = tresc;
        }

        public String getOdpowiedz() {
            return this.odpowiedz;
        }

        public void setOdpowiedz(String odpowiedz) {
            this.odpowiedz = odpowiedz;
        }

    }

}
