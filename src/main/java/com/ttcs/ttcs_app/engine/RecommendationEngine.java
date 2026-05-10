package com.ttcs.ttcs_app.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationEngine {

    public List<String> getUserBasedRecommendations(String targetUserId, Map<String, Map<String, Double>> matrix) {
        Map<String, Double> targetUserRatings = matrix.get(targetUserId);
        if (targetUserRatings == null || targetUserRatings.isEmpty()) return new ArrayList<>();

        Map<String, Double> similarities = new HashMap<>();
        for (String userId : matrix.keySet()) {
            if (!userId.equals(targetUserId)) {
                double sim = calculateCosineSimilarity(targetUserRatings, matrix.get(userId));
                if (sim > 0) similarities.put(userId, sim);
            }
        }

        Map<String, Double> scoreBoard = new HashMap<>();
        for (String neighborId : similarities.keySet()) {
            double sim = similarities.get(neighborId);
            matrix.get(neighborId).forEach((productId, rating) -> {
                if (!targetUserRatings.containsKey(productId)) {
                    scoreBoard.put(productId, scoreBoard.getOrDefault(productId, 0.0) + rating * sim);
                }
            });
        }

        return getTopKeys(scoreBoard, 10);
    }

    public List<String> getItemBasedRecommendations(String lastProductId, Map<String, Map<String, Double>> matrix) {
        // Xoay ma trận: Từ User-Item sang Item-User để tính độ giống nhau giữa các SP
        Map<String, Map<String, Double>> itemMatrix = new HashMap<>();
        matrix.forEach((userId, items) -> {
            items.forEach((productId, rating) -> {
                itemMatrix.computeIfAbsent(productId, k -> new HashMap<>()).put(userId, rating);
            });
        });

        Map<String, Double> targetItemProfile = itemMatrix.get(lastProductId);
        if (targetItemProfile == null) return new ArrayList<>();

        Map<String, Double> itemSimilarities = new HashMap<>();
        for (String productId : itemMatrix.keySet()) {
            if (!productId.equals(lastProductId)) {
                double sim = calculateCosineSimilarity(targetItemProfile, itemMatrix.get(productId));
                if (sim > 0.5) itemSimilarities.put(productId, sim); //TODO: thử nghiệm cái này
            }
        }

        return getTopKeys(itemSimilarities, 5);
    }

    private double calculateCosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
        Set<String> common = new HashSet<>(vec1.keySet());
        common.retainAll(vec2.keySet());
        if (common.isEmpty()) return 0.0;

        double dot = 0, n1 = 0, n2 = 0;
        for (String s : common) dot += vec1.get(s) * vec2.get(s);
        for (double v : vec1.values()) n1 += v * v;
        for (double v : vec2.values()) n2 += v * v;
        return dot / (Math.sqrt(n1) * Math.sqrt(n2));
    }

    private List<String> getTopKeys(Map<String, Double> map, int limit) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit).map(Map.Entry::getKey).collect(Collectors.toList());
    }
}