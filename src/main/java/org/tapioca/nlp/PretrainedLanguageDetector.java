package org.tapioca.nlp;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class PretrainedLanguageDetector {
    public static void main(String[] args) {
        try (InputStream modelIn = new FileInputStream("train/langdetect-183.bin");
             Scanner scanner = new Scanner(System.in)) {
            // Load serialized model
            LanguageDetectorModel trainedModel = new LanguageDetectorModel(modelIn);

            while (true) {
                // Get input in loop
                System.out.print("Enter a word/phrase/sentence (type X to exit): ");
                String inputText = scanner.nextLine();

                if (inputText.equals("X") || inputText.equals("x"))
                    break;

                // Initialize language detector tool
                LanguageDetectorME myDetector = new LanguageDetectorME(trainedModel);

                // Get language prediction
                Language bestLanguage = myDetector.predictLanguage(inputText);
                System.out.println("Best language candidate: " + bestLanguage.getLang());
                System.out.println("Best language confidence: " + bestLanguage.getConfidence());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
