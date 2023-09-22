package org.tapioca.nlp;

import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TrainedTokenizer {
    public static void main(String[] args) throws Exception {
        // Read file with example of tokenization
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("train/tokenizer-data.txt"));
        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        ObjectStream<TokenSample> sampleStream = new TokenSampleStream(lineStream);

        // Train a model
        TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
        TokenizerModel model = TokenizerME.train(sampleStream, factory, TrainingParameters.defaultParams());

        // Serialize model to some file
        model.serialize(new File("train/tokenizer-model.bin"));

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                // Get input in loop
                System.out.print("Enter a sentence (type X to exit): ");
                String inputText = scanner.nextLine();

                if (inputText.equals("X") || inputText.equals("x"))
                    break;

                // Initialize tokenizer tool using the model
                TokenizerME myTokenizer = new TokenizerME(model);

                // Tokenize sentence
                String[] tokens = myTokenizer.tokenize(inputText);
                for (String t : tokens) {
                    System.out.println("Token: " + t);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
