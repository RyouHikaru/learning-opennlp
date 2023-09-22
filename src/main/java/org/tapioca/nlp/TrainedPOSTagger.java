package org.tapioca.nlp;

import opennlp.tools.postag.*;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.*;
import opennlp.tools.util.model.ModelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TrainedPOSTagger {
    public static void main(String[] args) throws Exception {
        // Read file with POS tags
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("train/pos-tag-data.txt"));
        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        FilterObjectStream<String, POSSample> sampleStream = new WordTagSampleStream(lineStream);

        // Train a model with classifications from above file
        TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
        params.put(TrainingParameters.CUTOFF_PARAM, 0);
        POSModel model = POSTaggerME.train("en", sampleStream, params, new POSTaggerFactory());

        // Serialize model to some file
        model.serialize(new File("train/pos-tag-model.bin"));


        try (Scanner scanner = new Scanner(System.in)) {

            while (true) {
                // Get input in loop
                System.out.print("Enter a sentence (type X to exit): ");
                String inputText = scanner.nextLine();

                if (inputText.equals("X") || inputText.equals("x"))
                    break;

                // Initialize tagger
                POSTaggerME myTagger = new POSTaggerME(model);

                // Tag sentence
                String[] tokens = myTagger.tag(getTokens(inputText));
                for (String t : tokens) {
                    System.out.println("Tokens: " + t);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Tokenize sentence.
     *
     * @param sentence The sentence to be tokenized
     * @return The tokenized form
     */
    public static String[] getTokens(String sentence) {
        // The Tokenizer model
        try (InputStream modelIn = new FileInputStream("train/tokenizer-model.bin")) {
            TokenizerME myTokenizer = new TokenizerME(new TokenizerModel(modelIn));

            String[] tokens = myTokenizer.tokenize(sentence);

            for (String t : tokens) {
                System.out.println("Token: " + t);
            }

            return tokens;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }
}
