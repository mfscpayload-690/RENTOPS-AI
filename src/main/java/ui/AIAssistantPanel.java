package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import com.rentops.ai.config.AiComponents;
import com.rentops.ai.intent.IntentExtractionService;
import com.rentops.ai.summarize.ChunkSummarizerService;
import com.rentops.ai.summarize.MergeSummarizerService;
import com.rentops.ai.summarize.SummarizationPipeline;

import dao.CarDAO;
import models.Car;

/**
 * AI Assistant Panel for Users Provides natural language interface for car
 * searches, availability queries, and intelligent recommendations based on user
 * needs.
 */
public class AIAssistantPanel extends JPanel {

    private JTextField queryField;
    private JTextArea responseArea;
    private JProgressBar progressBar;
    private final CarDAO carDAO;

    // AI services
    private AiComponents aiComponents;
    private IntentExtractionService intentService;
    private SummarizationPipeline summarizationService;

    // Modern color scheme consistent with user interface
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;

    public AIAssistantPanel() {
        this.carDAO = new CarDAO();
        initializeAIServices();
        initializeUI();
    }

    private void initializeAIServices() {
        try {
            aiComponents = AiComponents.build(false);
            if (aiComponents.config.isEnabled()) {
                intentService = new IntentExtractionService(
                        aiComponents.config,
                        aiComponents.router,
                        aiComponents.llm,
                        aiComponents.safety
                );

                ChunkSummarizerService chunkSummarizer = new ChunkSummarizerService(
                        aiComponents.llm,
                        aiComponents.config.isEnabled()
                );
                MergeSummarizerService mergeSummarizer = new MergeSummarizerService(
                        aiComponents.llm,
                        aiComponents.config.isEnabled()
                );
                summarizationService = new SummarizationPipeline(
                        1500, // target chunk size for user queries
                        chunkSummarizer,
                        mergeSummarizer
                );
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize AI services: " + e.getMessage());
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Header section
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Status bar
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("AI Assistant Ready - Ask me anything about cars!");
        progressBar.setPreferredSize(new Dimension(0, 25));
        add(progressBar, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel("🤖 AI Car Assistant");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(PRIMARY_COLOR);

        JLabel subtitle = new JLabel("Ask me about car availability, recommendations, or booking assistance");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(108, 117, 125));

        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.setBackground(CARD_COLOR);
        titleContainer.add(title, BorderLayout.NORTH);
        titleContainer.add(subtitle, BorderLayout.CENTER);

        panel.add(titleContainer, BorderLayout.WEST);

        // Quick action buttons
        JPanel quickActions = createQuickActions();
        panel.add(quickActions, BorderLayout.EAST);

        return panel;
    }

    private JPanel createQuickActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(CARD_COLOR);

        JButton availableBtn = createQuickButton("Show Available Cars", SUCCESS_COLOR);
        availableBtn.addActionListener(e -> runQuickQuery("Show me all available cars right now"));

        JButton luxuryBtn = createQuickButton("Luxury Cars", WARNING_COLOR);
        luxuryBtn.addActionListener(e -> runQuickQuery("I want to see luxury vehicles"));

        JButton helpBtn = createQuickButton("Help", ACCENT_COLOR);
        helpBtn.addActionListener(e -> showHelpDialog());

        panel.add(availableBtn);
        panel.add(luxuryBtn);
        panel.add(helpBtn);

        return panel;
    }

    private JButton createQuickButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Input section
        JPanel inputSection = createInputSection();
        panel.add(inputSection, BorderLayout.NORTH);

        // Response section
        JPanel responseSection = createResponseSection();
        panel.add(responseSection, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInputSection() {
        JPanel section = createCard("Ask Your Question");
        section.setLayout(new BorderLayout(0, 15));

        JLabel instructionLabel = new JLabel("Type your question about cars, availability, or booking:");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(new Color(73, 80, 87));

        queryField = new JTextField();
        queryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        queryField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        queryField.setPreferredSize(new Dimension(0, 45));
        queryField.addActionListener(e -> processUserQuery()); // Enter key support

        // Example queries panel
        JPanel examplesPanel = createExamplesPanel();

        JButton askButton = createStyledButton("Ask AI Assistant", ACCENT_COLOR);
        askButton.addActionListener(e -> processUserQuery());

        JPanel inputContainer = new JPanel(new BorderLayout(0, 10));
        inputContainer.setBackground(CARD_COLOR);
        inputContainer.add(instructionLabel, BorderLayout.NORTH);
        inputContainer.add(queryField, BorderLayout.CENTER);
        inputContainer.add(examplesPanel, BorderLayout.SOUTH);

        section.add(inputContainer, BorderLayout.CENTER);
        section.add(askButton, BorderLayout.SOUTH);

        return section;
    }

    private JPanel createExamplesPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBackground(CARD_COLOR);

        JLabel exampleLabel = new JLabel("Try asking:");
        exampleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        exampleLabel.setForeground(new Color(108, 117, 125));

        String[] examples = {
            "\"Show available SUVs\"",
            "\"I need a car for 4 people\"",
            "\"What luxury cars do you have?\""
        };

        panel.add(exampleLabel);
        for (String example : examples) {
            JLabel exampleText = new JLabel(example);
            exampleText.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            exampleText.setForeground(ACCENT_COLOR);
            exampleText.setCursor(new Cursor(Cursor.HAND_CURSOR));
            exampleText.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    queryField.setText(example.replaceAll("\"", ""));
                }
            });
            panel.add(exampleText);
        }

        return panel;
    }

    private JPanel createResponseSection() {
        JPanel section = createCard("AI Response");
        section.setLayout(new BorderLayout());

        responseArea = new JTextArea(15, 50);
        responseArea.setEditable(false);
        responseArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        responseArea.setBackground(new Color(248, 249, 250));
        responseArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setText("👋 Hello! I'm your AI car assistant. I can help you:\n\n"
                + "🔍 Search for available cars\n"
                + "📋 Check car details and specifications\n"
                + "💡 Get recommendations based on your needs\n"
                + "📅 Find cars for specific dates\n"
                + "💰 Compare prices and features\n\n"
                + "Just type your question above and I'll provide personalized results!");

        JScrollPane scrollPane = new JScrollPane(responseArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(233, 236, 239)));
        section.add(scrollPane);

        return section;
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(233, 236, 239)),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(20, 20, 20, 20),
                        title,
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 16),
                        PRIMARY_COLOR
                )
        ));
        return card;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 45));
        return button;
    }

    private void processUserQuery() {
        String query = queryField.getText().trim();
        if (query.isEmpty()) {
            responseArea.setText("Please enter a question first!");
            return;
        }

        progressBar.setString("🤖 AI is thinking...");
        progressBar.setIndeterminate(true);

        CompletableFuture.runAsync(() -> {
            try {
                String response = generateAIResponse(query);

                SwingUtilities.invokeLater(() -> {
                    responseArea.setText(response);
                    progressBar.setIndeterminate(false);
                    progressBar.setString("Response ready! Ask another question.");
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    responseArea.setText("❌ Sorry, I encountered an error: " + e.getMessage()
                            + "\n\nPlease try rephrasing your question or contact support.");
                    progressBar.setIndeterminate(false);
                    progressBar.setString("Error occurred - please try again");
                });
            }
        });
    }

    private void runQuickQuery(String query) {
        queryField.setText(query);
        processUserQuery();
    }

    private String generateAIResponse(String query) {
        try {
            List<Car> availableCars = carDAO.getAvailableCars();

            StringBuilder response = new StringBuilder();
            response.append("🤖 AI CAR ASSISTANT RESPONSE\n");
            response.append("============================\n\n");

            String lowerQuery = query.toLowerCase();

            // Try to use real AI intent extraction first
            if (aiComponents != null && aiComponents.config.isEnabled() && intentService != null) {
                try {
                    com.rentops.ai.intent.BookingIntent intent = intentService.extract(query);

                    response.append("🧠 AI Analysis Results:\n");
                    response.append("Action Detected: ").append(intent.action()).append("\n");

                    if (intent.passengers() > 0) {
                        response.append("Passengers: ").append(intent.passengers()).append(" people\n");
                    }

                    if (intent.vehicleType() != null && !intent.vehicleType().isBlank()) {
                        response.append("Vehicle Type: ").append(intent.vehicleType()).append("\n");
                    }

                    if (intent.pickupLocation() != null && !intent.pickupLocation().isBlank()) {
                        response.append("Pickup: ").append(intent.pickupLocation()).append("\n");
                    }

                    if (intent.dropoffLocation() != null && !intent.dropoffLocation().isBlank()) {
                        response.append("Dropoff: ").append(intent.dropoffLocation()).append("\n");
                    }

                    response.append("AI Confidence: High (Groq AI Model)\n\n");

                    // Process based on AI-detected intent
                    switch (intent.action()) {
                        case CREATE:
                            response.append("🚗 BOOKING ASSISTANCE:\n\n");

                            // Filter cars based on AI-extracted criteria
                            List<Car> filteredCars = availableCars;

                            if (intent.passengers() > 0) {
                                final int passengers = intent.passengers();
                                filteredCars = filteredCars.stream()
                                        .filter(car -> getSeatingCapacity(car) >= passengers)
                                        .collect(java.util.stream.Collectors.toList());
                            }

                            if (intent.vehicleType() != null && !intent.vehicleType().isBlank()) {
                                final String vehicleType = intent.vehicleType().toLowerCase();
                                filteredCars = filteredCars.stream()
                                        .filter(car -> getCarType(car).toLowerCase().contains(vehicleType)
                                        || car.getModel().toLowerCase().contains(vehicleType)
                                        || car.getMake().toLowerCase().contains(vehicleType))
                                        .collect(java.util.stream.Collectors.toList());
                            }

                            if (filteredCars.isEmpty()) {
                                response.append("❌ No vehicles match your AI-analyzed criteria.\n");
                                response.append("💡 Try adjusting your requirements or contact support.\n");
                            } else {
                                response.append(String.format("Found %d vehicles matching your AI-analyzed requirements:\n\n", filteredCars.size()));

                                for (int i = 0; i < Math.min(4, filteredCars.size()); i++) {
                                    Car car = filteredCars.get(i);
                                    response.append(String.format("🚙 %s %s (%s)\n", car.getMake(), car.getModel(), car.getYear()));
                                    response.append(String.format("   Seats: %d | Type: %s | Price: ₹%.2f/day\n\n",
                                            getSeatingCapacity(car), getCarType(car), car.getPricePerDay()));
                                }

                                if (filteredCars.size() > 4) {
                                    response.append(String.format("... and %d more matching vehicles!\n", filteredCars.size() - 4));
                                }
                            }
                            break;

                        case QUERY:
                            response.append("📊 AVAILABILITY STATUS:\n\n");
                            response.append(String.format("Total Available Vehicles: %d\n", availableCars.size()));

                            // Group by type for summary
                            java.util.Map<String, Long> carsByType = availableCars.stream()
                                    .collect(java.util.stream.Collectors.groupingBy(
                                            this::getCarType,
                                            java.util.stream.Collectors.counting()
                                    ));

                            response.append("Breakdown by Type:\n");
                            carsByType.forEach((type, count)
                                    -> response.append(String.format("• %s: %d vehicles\n", type, count))
                            );
                            break;

                        default:
                            response.append("🔍 GENERAL CAR SEARCH:\n\n");
                            response.append("Here are some available options:\n");

                            for (int i = 0; i < Math.min(3, availableCars.size()); i++) {
                                Car car = availableCars.get(i);
                                response.append(String.format("🚗 %s %s - ₹%.2f/day\n",
                                        car.getMake(), car.getModel(), car.getPricePerDay()));
                            }

                            response.append("\n💡 Try being more specific: \"I need an SUV for 5 people\" or \"Show luxury cars\"\n");
                            break;
                    }

                } catch (Exception aiError) {
                    response.append("⚠️ AI processing unavailable: ").append(aiError.getMessage()).append("\n");
                    response.append("Falling back to basic pattern matching...\n\n");
                    return generateFallbackResponse(query, availableCars);
                }

            } else {
                response.append("ℹ️ AI services not available - using pattern matching\n\n");
                return generateFallbackResponse(query, availableCars);
            }

            return response.toString();

        } catch (Exception e) {
            return "❌ I apologize, but I'm having trouble accessing car information right now. Please try again later or contact support.\n\nError: " + e.getMessage();
        }
    }

    private String generateFallbackResponse(String query, List<Car> availableCars) {
        StringBuilder response = new StringBuilder();
        response.append("🔍 PATTERN-BASED CAR SEARCH\n");
        response.append("============================\n\n");

        String lowerQuery = query.toLowerCase();

        // Handle availability queries
        if (lowerQuery.contains("available") || lowerQuery.contains("show")) {
            response.append("🚗 AVAILABLE CARS:\n\n");

            if (lowerQuery.contains("suv")) {
                response.append("SUV vehicles available:\n");
                availableCars = availableCars.stream()
                        .filter(car -> getCarType(car).equalsIgnoreCase("SUV"))
                        .collect(java.util.stream.Collectors.toList());
            } else if (lowerQuery.contains("luxury")) {
                response.append("Luxury vehicles available:\n");
                availableCars = availableCars.stream()
                        .filter(car -> isLuxuryCar(car))
                        .collect(java.util.stream.Collectors.toList());
            } else {
                response.append("All available vehicles:\n");
            }

            if (availableCars.isEmpty()) {
                response.append("❌ No vehicles matching your criteria are currently available.\n");
            } else {
                response.append(String.format("Found %d vehicles:\n\n", availableCars.size()));

                for (int i = 0; i < Math.min(5, availableCars.size()); i++) {
                    Car car = availableCars.get(i);
                    response.append(String.format("🚙 %s %s (%s)\n", car.getMake(), car.getModel(), car.getYear()));
                    response.append(String.format("   Type: %s | Seats: %d | Price: ₹%.2f/day\n\n",
                            getCarType(car), getSeatingCapacity(car), car.getPricePerDay()));
                }

                if (availableCars.size() > 5) {
                    response.append(String.format("... and %d more vehicles available!\n", availableCars.size() - 5));
                }
            }
        } // Handle passenger requirements
        else if (lowerQuery.contains("people") || lowerQuery.contains("passengers")) {
            int passengerCount = extractPassengerCount(lowerQuery);
            response.append(String.format("🧑‍🤝‍🧑 CARS FOR %d PASSENGERS:\n\n", passengerCount));

            availableCars = availableCars.stream()
                    .filter(car -> getSeatingCapacity(car) >= passengerCount)
                    .collect(java.util.stream.Collectors.toList());

            if (availableCars.isEmpty()) {
                response.append("❌ No vehicles available for that many passengers.\n");
            } else {
                response.append(String.format("Found %d suitable vehicles:\n\n", availableCars.size()));

                for (int i = 0; i < Math.min(4, availableCars.size()); i++) {
                    Car car = availableCars.get(i);
                    response.append(String.format("🚗 %s %s - %d seats (₹%.2f/day)\n",
                            car.getMake(), car.getModel(), getSeatingCapacity(car), car.getPricePerDay()));
                }
            }
        } // Handle recommendations
        else if (lowerQuery.contains("recommend") || lowerQuery.contains("suggest") || lowerQuery.contains("best")) {
            response.append("💡 PATTERN-BASED RECOMMENDATIONS:\n\n");

            // Sort by value (features vs price)
            availableCars = availableCars.stream()
                    .sorted((a, b) -> {
                        double valueA = getSeatingCapacity(a) * 100.0 / a.getPricePerDay().doubleValue();
                        double valueB = getSeatingCapacity(b) * 100.0 / b.getPricePerDay().doubleValue();
                        return Double.compare(valueB, valueA);
                    })
                    .collect(java.util.stream.Collectors.toList());

            response.append("Top recommendations based on value:\n\n");

            for (int i = 0; i < Math.min(3, availableCars.size()); i++) {
                Car car = availableCars.get(i);
                response.append(String.format("🏆 #%d: %s %s\n", i + 1, car.getMake(), car.getModel()));
                response.append(String.format("   Perfect for: %s\n", getRecommendationReason(car)));
                response.append(String.format("   Price: ₹%.2f/day | Seats: %d\n\n", car.getPricePerDay(), getSeatingCapacity(car)));
            }
        } // Handle booking inquiries
        else if (lowerQuery.contains("book") || lowerQuery.contains("rent") || lowerQuery.contains("reserve")) {
            response.append("📅 BOOKING ASSISTANCE:\n\n");
            response.append("I'd be happy to help you with booking! Here's how:\n\n");
            response.append("1. Use the 'Search Cars' tab to browse available vehicles\n");
            response.append("2. Select your preferred car\n");
            response.append("3. Choose your rental dates\n");
            response.append("4. Complete your booking\n\n");
            response.append("💡 Need specific recommendations? Just ask me about car types or passenger requirements!");
        } // General help
        else {
            response.append("🤖 HOW CAN I HELP?\n\n");
            response.append("I can assist you with:\n\n");
            response.append("🔍 Car Search:\n");
            response.append("• \"Show available SUVs\"\n");
            response.append("• \"I need a car for 4 people\"\n");
            response.append("• \"What luxury cars do you have?\"\n\n");
            response.append("💡 Recommendations:\n");
            response.append("• \"Recommend a good family car\"\n");
            response.append("• \"Suggest economical options\"\n\n");
            response.append("📅 Booking Help:\n");
            response.append("• \"How do I book a car?\"\n");
            response.append("• \"Help me with booking process\"\n\n");
            response.append("Note: AI services would provide enhanced understanding!\n");
        }

        return response.toString();
    }

    private void showHelpDialog() {
        String helpText = "🤖 AI Car Assistant Help\n\n"
                + "I can help you with:\n"
                + "• Finding available cars\n"
                + "• Getting recommendations\n"
                + "• Checking prices and features\n"
                + "• Booking assistance\n\n"
                + "Example questions:\n"
                + "• \"Show me available SUVs\"\n"
                + "• \"I need a car for 5 people\"\n"
                + "• \"What's the cheapest option?\"\n"
                + "• \"Recommend luxury cars\"";

        javax.swing.JOptionPane.showMessageDialog(this, helpText, "AI Assistant Help",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    // Helper methods to extract information from Car specs
    private String getCarType(Car car) {
        if (car.getSpecs() != null) {
            String specs = car.getSpecs().toLowerCase();
            if (specs.contains("suv")) {
                return "SUV";
            }
            if (specs.contains("sedan")) {
                return "Sedan";
            }
            if (specs.contains("hatchback")) {
                return "Hatchback";
            }
            if (specs.contains("luxury")) {
                return "Luxury";
            }
        }

        // Fallback based on model name
        String model = car.getModel().toLowerCase();
        if (model.contains("suv") || model.contains("x") || model.contains("q")) {
            return "SUV";
        }
        if (model.contains("sedan") || model.contains("camry") || model.contains("accord")) {
            return "Sedan";
        }
        if (model.contains("hatch") || model.contains("polo") || model.contains("swift")) {
            return "Hatchback";
        }

        return "Sedan"; // Default fallback
    }

    private int getSeatingCapacity(Car car) {
        if (car.getSpecs() != null) {
            String specs = car.getSpecs().toLowerCase();
            if (specs.contains("8 seats") || specs.contains("8-seater")) {
                return 8;
            }
            if (specs.contains("7 seats") || specs.contains("7-seater")) {
                return 7;
            }
            if (specs.contains("6 seats") || specs.contains("6-seater")) {
                return 6;
            }
            if (specs.contains("5 seats") || specs.contains("5-seater")) {
                return 5;
            }
            if (specs.contains("4 seats") || specs.contains("4-seater")) {
                return 4;
            }
            if (specs.contains("2 seats") || specs.contains("2-seater")) {
                return 2;
            }
        }

        // Fallback based on car type
        String type = getCarType(car);
        if (type.equals("SUV")) {
            return 7;
        }
        if (type.equals("Sedan")) {
            return 5;
        }
        if (type.equals("Hatchback")) {
            return 5;
        }

        return 5; // Default fallback
    }

    private boolean isLuxuryCar(Car car) {
        if (car.getPricePerDay().compareTo(new java.math.BigDecimal("2000")) > 0) {
            return true;
        }

        String make = car.getMake().toLowerCase();
        return make.contains("bmw") || make.contains("mercedes") || make.contains("audi")
                || make.contains("lexus") || make.contains("jaguar");
    }

    private int extractPassengerCount(String query) {
        if (query.contains("4")) {
            return 4;
        }
        if (query.contains("5")) {
            return 5;
        }
        if (query.contains("6")) {
            return 6;
        }
        if (query.contains("7")) {
            return 7;
        }
        if (query.contains("8")) {
            return 8;
        }
        if (query.contains("2")) {
            return 2;
        }
        return 4; // Default
    }

    private String getRecommendationReason(Car car) {
        String type = getCarType(car);
        if (type.equals("SUV")) {
            return "Family trips and adventure";
        }
        if (type.equals("Sedan")) {
            return "Business and comfortable travel";
        }
        if (isLuxuryCar(car)) {
            return "Premium experience";
        }
        return "Great value and reliability";
    }
}
