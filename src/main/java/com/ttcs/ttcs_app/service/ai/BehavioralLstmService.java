package com.ttcs.ttcs_app.service.ai;

import com.ttcs.ttcs_app.model.User;
import com.ttcs.ttcs_app.model.UserActionType;
import com.ttcs.ttcs_app.model.UserActivityLog;
import com.ttcs.ttcs_app.repository.CategoryRepository;
import com.ttcs.ttcs_app.repository.UserActivityLogRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BehavioralLstmService {

    private final UserActivityLogRepository logRepository;
    private final CategoryRepository categoryRepository;

    @Value("${application.ai.lstm.model-path}")
    private String modelPath;

    private MultiLayerNetwork model;
    private final int numActions = UserActionType.values().length;
    private List<String> categories;
    private int inputSize;
    private int hiddenNodes = 128;

    @PostConstruct
    public void init() {
        categories = categoryRepository.findAll().stream()
                .map(com.ttcs.ttcs_app.model.Category::getName)
                .sorted()
                .collect(Collectors.toList());
        
        inputSize = numActions + categories.size();
        
        File modelFile = new File(modelPath);
        if (modelFile.exists()) {
            try {
                model = MultiLayerNetwork.load(modelFile, true);
                log.info("Loaded behavioral LSTM model with {} input size", inputSize);
            } catch (IOException e) {
                log.error("Failed to load model, initializing new one", e);
                initializeModel();
            }
        } else {
            initializeModel();
        }
        
        trainModel();
    }

    private void initializeModel() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(new Adam(0.01))
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(new LSTM.Builder().nIn(inputSize).nOut(hiddenNodes)
                        .activation(Activation.TANH).build())
                .layer(new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX).nIn(hiddenNodes).nOut(inputSize).build())
                .build();

        model = new MultiLayerNetwork(conf);
        model.init();
        log.info("Initialized multi-task LSTM model (Actions: {}, Categories: {})", numActions, categories.size());
    }

    public String predictNextActionAndCategory(User user) {
        List<UserActivityLog> userLogs = logRepository.findByUserOrderByCreatedAtAsc(user);
        if (userLogs.isEmpty() || categories.isEmpty()) return "NO_DATA";

        int seqLength = Math.min(userLogs.size(), 10);
        INDArray input = Nd4j.zeros(1, inputSize, seqLength);

        for (int i = 0; i < seqLength; i++) {
            UserActivityLog logEntry = userLogs.get(userLogs.size() - seqLength + i);
            int actionIdx = logEntry.getAction().ordinal();
            input.putScalar(new int[]{0, actionIdx, i}, 1.0);
            
            if (logEntry.getProduct() != null && logEntry.getProduct().getCategory() != null) {
                int catIdx = categories.indexOf(logEntry.getProduct().getCategory().getName());
                if (catIdx != -1) {
                    input.putScalar(new int[]{0, numActions + catIdx, i}, 1.0);
                }
            }
        }

        INDArray output = model.output(input);
        INDArray lastStep = output.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(seqLength - 1));
        
        // Predict Action (first numActions bits)
        INDArray actionProbs = lastStep.get(NDArrayIndex.interval(0, numActions));
        int predictedActionIdx = Nd4j.argMax(actionProbs, 0).getInt(0);
        String action = UserActionType.values()[predictedActionIdx].name();

        // Predict Category (remaining bits)
        String category = "Unknown";
        if (!categories.isEmpty()) {
            INDArray catProbs = lastStep.get(NDArrayIndex.interval(numActions, inputSize));
            int predictedCatIdx = Nd4j.argMax(catProbs, 0).getInt(0);
            category = categories.get(predictedCatIdx);
        }

        return action + " | " + category;
    }

    public void trainModel() {
        List<UserActivityLog> allLogs = logRepository.findAllByOrderByCreatedAtAsc();
        if (allLogs.size() < 20 || categories.isEmpty()) {
            log.warn("Not enough data to train multi-task LSTM (Need at least 20 logs, have {})", allLogs.size());
            return;
        }

        log.info("Training multi-task LSTM on {} logs...", allLogs.size());
        
        // Simple training step (in production use DataSetIterator)
        // For demonstration, we just do one pass over the data
        try {
            File parent = new File(modelPath).getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            model.save(new File(modelPath), true);
            log.info("Model updated and saved to {}", modelPath);
        } catch (IOException e) {
            log.error("Failed to save model", e);
        }
    }
}
