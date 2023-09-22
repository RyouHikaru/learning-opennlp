package org.tapioca.nlp;

import opennlp.tools.doccat.*;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.*;
import opennlp.tools.util.model.ModelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TrainedSentimentAnalyzer {
    public static void main(String[] args) throws Exception {
        // Read file with classifications
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("train/sentiment-data.txt"));
        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

        // Use CUT_OFF as zero since we will use very few samples.
        // BagOfWordsFeatureGenerator will treat each word as a feature.
        // Since we have few samples, each feature/word will have small counts, so it won't meet high cutoff.
        TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
        params.put(TrainingParameters.CUTOFF_PARAM, 0);

        DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] {
                new BagOfWordsFeatureGenerator()
        });

        // Train a model with classifications from above file
        DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, factory);

        // Serialize model to some file
        model.serialize(new File("train/sentiment-analyzer-model.bin"));


        try (InputStream modelIn = new FileInputStream("train/sentiment-analyzer-model.bin");
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                // Get input in loop
                System.out.print("Enter a sentence (type X to exit): ");
                String inputText = scanner.nextLine();

                if (inputText.equals("X") || inputText.equals("x"))
                    break;

                // Initialize categorizer
                DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);

                // Get the sentiment probabilities
                double[] probabilitiesOfOutcomes = myCategorizer.categorize(getTokens(inputText));

                // Get the classification of highest probability
                String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);
                System.out.println("Category: " + category);
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
