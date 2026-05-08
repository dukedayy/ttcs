package com.ttcs.ttcs_app.service.ai;

import com.ttcs.ttcs_app.model.Product;
import com.ttcs.ttcs_app.model.User;
import com.ttcs.ttcs_app.model.UserActivityLog;
import com.ttcs.ttcs_app.repository.ProductRepository;
import com.ttcs.ttcs_app.repository.UserActivityLogRepository;
import com.ttcs.ttcs_app.repository.UserRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiChatService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final BehavioralLstmService behavioralLstmService;
    private final UserActivityLogRepository logRepository;

    @Value("${application.ai.groq.api-key}")
    private String groqApiKey;

    @Value("${application.ai.groq.model-name}")
    private String groqModelName;

    private Assistant assistant;

    public interface Assistant {
        @SystemMessage("""
            Bạn là trợ lý bán hàng thông minh của shop TTCS.
            Dưới đây là danh sách sản phẩm hiện có:
            {{products}}
            
            Các sản phẩm khách hàng vừa quan tâm gần đây:
            {{recentProducts}}
            
            Dựa trên lịch sử, khách hàng này có khả năng sẽ: {{prediction}}
            
            Hãy trả lời khách hàng một cách lịch sự, thân thiện và gợi ý sản phẩm phù hợp. 
            Ưu tiên tư vấn các sản phẩm liên quan đến những gì họ vừa xem.
            """)
        String chat(@V("products") String products, @V("recentProducts") String recentProducts, @V("prediction") String prediction, @UserMessage String userMessage);
    }

    @PostConstruct
    public void init() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(groqApiKey)
                .baseUrl("https://api.groq.com/openai/v1")
                .modelName(groqModelName)
                .build();

        assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .build();
    }

    public String getChatResponse(String userId, String userMessage) {
        User user = null;
        if (userId != null && !userId.equals("guest")) {
            try {
                user = userRepository.findById(userId).orElse(null);
            } catch (Exception e) {
                // Ignore invalid UUID or other DB errors
            }
        }
        
        // Fetch products for context (RAG - simplified)
        List<Product> products = productRepository.findAll();
        String productContext = products.stream()
                .map(p -> String.format("- %s: %.2f VNĐ (%s)", p.getName(), p.getPrice(), p.getDescription()))
                .collect(Collectors.joining("\n"));

        // Get behavior prediction from LSTM
        String prediction = (user != null) ? behavioralLstmService.predictNextActionAndCategory(user) : "Chưa có dữ liệu (Khách vãng lai)";

        // Get recent products
        String recentProducts = "Chưa có dữ liệu";
        if (user != null) {
            List<UserActivityLog> logs = logRepository.findTop5ByUserOrderByCreatedAtDesc(user);
            if (!logs.isEmpty()) {
                recentProducts = logs.stream()
                        .map(log -> log.getProduct().getName())
                        .distinct()
                        .collect(Collectors.joining(", "));
            }
        }

        // LOGGING ĐỂ KIỂM TRA
        System.out.println("=== AI CHAT DEBUG ===");
        System.out.println("User ID: " + userId);
        System.out.println("Email: " + (user != null ? user.getEmail() : "Guest"));
        System.out.println("Prediction: " + prediction);
        System.out.println("Recent Products: " + recentProducts);
        System.out.println("=====================");

        return assistant.chat(productContext, recentProducts, prediction, userMessage);
    }
}
