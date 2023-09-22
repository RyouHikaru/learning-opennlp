package org.tapioca.nlp;

import opennlp.tools.langdetect.*;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.model.ModelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TrainedLanguageDetector {
    public static void main(String[] args) throws Exception {
        // Read file with greetings in many languages
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("train/training-data.txt"));
        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        ObjectStream<LanguageSample> sampleStream = new LanguageDetectorSampleStream(lineStream);

        // Train a model
        LanguageDetectorModel model = LanguageDetectorME.train(sampleStream, ModelUtil.createDefaultTrainingParameters(), new LanguageDetectorFactory());

        // Serialize model to some file
        model.serialize(new File("train/language-model.bin"));

        try (InputStream modelIn = new FileInputStream("train/language-model.bin");
             Scanner scanner = new Scanner(System.in)) {
            // Load serialized model
            LanguageDetectorModel trainedModel = new LanguageDetectorModel(modelIn);

            while (true) {
                // Get input in loop
                System.out.print("Enter a greeting (type X to exit): ");
                String inputText = scanner.nextLine();

                if (inputText.equals("X") || inputText.equals("x"))
                    break;

                // Initialize language detector tool
                LanguageDetectorME myCategorizer = new LanguageDetectorME(trainedModel);

                // Get language prediction based on learnings
                Language bestLanguage = myCategorizer.predictLanguage(inputText);
                System.out.println("Best language candidate: " + bestLanguage.getLang());
                System.out.println("Best language confidence: " + bestLanguage.getConfidence());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}