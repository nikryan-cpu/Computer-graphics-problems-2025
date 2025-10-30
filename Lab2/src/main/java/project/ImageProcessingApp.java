package project;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class ImageProcessingApp extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage processedImage;
    private JLabel originalLabel;
    private JLabel processedLabel;

    // Smoothing components
    private JComboBox<String> filterComboBox;
    private JTextField kernelSizeField;
    private JTextField sigmaField;

    // Thresholding components
    private JComboBox<String> methodComboBox;
    private JTextField windowSizeField;
    private JTextField kField;
    private JTextField rField;

    private JTabbedPane tabbedPane;
    private JPanel smoothingPanel;
    private JPanel thresholdingPanel;

    public ImageProcessingApp() {
        setTitle("Image Processing Application");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open Image");
        JMenuItem saveItem = new JMenuItem("Save Processed Image");
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Tabbed pane
        tabbedPane = new JTabbedPane();

        // Smoothing tab
        smoothingPanel = createSmoothingPanel();
        tabbedPane.addTab("Smoothing Filters", smoothingPanel);

        // Thresholding tab
        thresholdingPanel = createThresholdingPanel();
        tabbedPane.addTab("Local Thresholding", thresholdingPanel);

        add(tabbedPane, BorderLayout.NORTH);

        // Image display panel
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(1, 2));

        originalLabel = new JLabel("Original Image");
        originalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        processedLabel = new JLabel("Processed Image");
        processedLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane originalScroll = new JScrollPane(originalLabel);
        JScrollPane processedScroll = new JScrollPane(processedLabel);

        imagePanel.add(originalScroll);
        imagePanel.add(processedScroll);

        add(imagePanel, BorderLayout.CENTER);

        // Event listeners
        openItem.addActionListener(e -> loadImage());
        saveItem.addActionListener(e -> saveImage());

        // Initial enable for sigma
        sigmaField.setEnabled("Gaussian".equals(filterComboBox.getSelectedItem()));

        // Initial for thresholding
        rField.setEnabled("Sauvola".equals(methodComboBox.getSelectedItem()));
    }

    private JPanel createSmoothingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel filterLabel = new JLabel("Filter:");
        filterComboBox = new JComboBox<>(new String[]{"Box", "Gaussian"});
        panel.add(filterLabel);
        panel.add(filterComboBox);

        JLabel kernelSizeLabel = new JLabel("Kernel Size:");
        kernelSizeField = new JTextField("3", 5);
        panel.add(kernelSizeLabel);
        panel.add(kernelSizeField);

        JLabel sigmaLabel = new JLabel("Sigma (Gaussian):");
        sigmaField = new JTextField("1.0", 5);
        panel.add(sigmaLabel);
        panel.add(sigmaField);

        JButton applyButton = new JButton("Apply Filter");
        panel.add(applyButton);

        applyButton.addActionListener(e -> applySmoothing());

        filterComboBox.addActionListener(e -> {
            sigmaField.setEnabled("Gaussian".equals(filterComboBox.getSelectedItem()));
        });

        return panel;
    }

    private JPanel createThresholdingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel methodLabel = new JLabel("Method:");
        methodComboBox = new JComboBox<>(new String[]{"Niblack", "Sauvola"});
        panel.add(methodLabel);
        panel.add(methodComboBox);

        JLabel windowSizeLabel = new JLabel("Window Size:");
        windowSizeField = new JTextField("15", 5);
        panel.add(windowSizeLabel);
        panel.add(windowSizeField);

        JLabel kLabel = new JLabel("k:");
        kField = new JTextField("-0.2", 5);
        panel.add(kLabel);
        panel.add(kField);

        JLabel rLabel = new JLabel("R (Sauvola):");
        rField = new JTextField("128", 5);
        panel.add(rLabel);
        panel.add(rField);

        JButton applyButton = new JButton("Apply Threshold");
        panel.add(applyButton);

        applyButton.addActionListener(e -> applyThreshold());

        methodComboBox.addActionListener(e -> {
            String method = (String) methodComboBox.getSelectedItem();
            if ("Niblack".equals(method)) {
                kField.setText("-0.2");
                rField.setEnabled(false);
            } else if ("Sauvola".equals(method)) {
                kField.setText("0.5");
                rField.setEnabled(true);
            }
        });

        return panel;
    }

    private void loadImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                System.out.println("Trying to load: " + chooser.getSelectedFile().getAbsolutePath());
                originalImage = ImageIO.read(chooser.getSelectedFile());
                displayImage(originalLabel, originalImage);
                processedImage = null;
                displayImage(processedLabel, null);
            } catch (IOException ex) {
                System.out.println("Error loading image: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage());
            }
        }
    }

    private void saveImage() {
        if (processedImage == null) {
            JOptionPane.showMessageDialog(this, "No processed image to save.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(".png")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }
                ImageIO.write(processedImage, "png", file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage());
            }
        }
    }

    private void displayImage(JLabel label, BufferedImage image) {
        if (image != null) {
            label.setIcon(new ImageIcon(image));
        } else {
            label.setIcon(null);
        }
        label.revalidate();
        label.repaint();
    }

    private void applySmoothing() {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "Please load an image first.");
            return;
        }

        int kernelSize;
        double sigma = 1.0;
        try {
            kernelSize = Integer.parseInt(kernelSizeField.getText());
            if (kernelSize % 2 == 0 || kernelSize < 3) {
                throw new NumberFormatException("Kernel size must be odd and >= 3.");
            }
            if ("Gaussian".equals(filterComboBox.getSelectedItem())) {
                sigma = Double.parseDouble(sigmaField.getText());
                if (sigma <= 0) {
                    throw new NumberFormatException("Sigma must be > 0.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid parameters: " + ex.getMessage());
            return;
        }

        String filter = (String) filterComboBox.getSelectedItem();
        processedImage = applySmoothingFilter(originalImage, filter, kernelSize, sigma);
        displayImage(processedLabel, processedImage);
    }

    private BufferedImage applySmoothingFilter(BufferedImage img, String filter, int kernelSize, double sigma) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage result = new BufferedImage(width, height, img.getType());

        double[][] kernel = createKernel(filter, kernelSize, sigma);

        int radius = kernelSize / 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double red = 0, green = 0, blue = 0, alpha = 0;
                double sumWeights = 0;

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        int nx = Math.max(0, Math.min(width - 1, x + dx));
                        int ny = Math.max(0, Math.min(height - 1, y + dy));
                        Color pixel = new Color(img.getRGB(nx, ny), true);
                        double weight = kernel[dy + radius][dx + radius];
                        red += pixel.getRed() * weight;
                        green += pixel.getGreen() * weight;
                        blue += pixel.getBlue() * weight;
                        alpha += pixel.getAlpha() * weight;
                        sumWeights += weight;
                    }
                }

                if (sumWeights > 0) {
                    red /= sumWeights;
                    green /= sumWeights;
                    blue /= sumWeights;
                    alpha /= sumWeights;
                }

                int r = (int) Math.min(255, Math.max(0, red));
                int g = (int) Math.min(255, Math.max(0, green));
                int b = (int) Math.min(255, Math.max(0, blue));
                int a = (int) Math.min(255, Math.max(0, alpha));
                result.setRGB(x, y, new Color(r, g, b, a).getRGB());
            }
        }

        return result;
    }

    private double[][] createKernel(String filter, int size, double sigma) {
        double[][] kernel = new double[size][size];
        double sum = 0;

        if ("Box".equals(filter)) {
            double value = 1.0 / (size * size);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    kernel[i][j] = value;
                    sum += value;
                }
            }
        } else if ("Gaussian".equals(filter)) {
            int radius = size / 2;
            double sigma2 = sigma * sigma;
            for (int i = -radius; i <= radius; i++) {
                for (int j = -radius; j <= radius; j++) {
                    double exp = Math.exp(-(i * i + j * j) / (2 * sigma2));
                    kernel[i + radius][j + radius] = exp / (2 * Math.PI * sigma2);
                    sum += kernel[i + radius][j + radius];
                }
            }
        }

        // Normalize
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[i][j] /= sum;
            }
        }

        return kernel;
    }

    private void applyThreshold() {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "Please load an image first.");
            return;
        }

        int windowSize;
        double k;
        double r = 128;
        try {
            windowSize = Integer.parseInt(windowSizeField.getText());
            if (windowSize % 2 == 0 || windowSize < 3) {
                throw new NumberFormatException("Window size must be odd and >= 3.");
            }
            k = Double.parseDouble(kField.getText());
            if ("Sauvola".equals(methodComboBox.getSelectedItem())) {
                r = Double.parseDouble(rField.getText());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid parameters: " + ex.getMessage());
            return;
        }

        String method = (String) methodComboBox.getSelectedItem();
        processedImage = localThreshold(originalImage, method, windowSize, k, r);
        displayImage(processedLabel, processedImage);
    }

    private BufferedImage localThreshold(BufferedImage img, String method, int windowSize, double k, double r) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage gray = toGrayscale(img);
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        int radius = windowSize / 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sum = 0;
                double sumSq = 0;
                int count = 0;

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        int nx = x + dx;
                        int ny = y + dy;
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            int pixel = gray.getRGB(nx, ny) & 0xFF;
                            sum += pixel;
                            sumSq += pixel * pixel;
                            count++;
                        }
                    }
                }

                double mean = sum / count;
                double variance = (sumSq / count) - (mean * mean);
                double stdDev = Math.sqrt(variance);

                double threshold;
                if ("Niblack".equals(method)) {
                    threshold = mean + k * stdDev;
                } else if ("Sauvola".equals(method)) {
                    threshold = mean * (1 + k * (stdDev / r - 1));
                } else {
                    threshold = mean;
                }

                int pixel = gray.getRGB(x, y) & 0xFF;
                int binary = (pixel > threshold) ? 255 : 0;
                result.setRGB(x, y, new Color(binary, binary, binary).getRGB());
            }
        }

        return result;
    }

    private BufferedImage toGrayscale(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage gray = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(img.getRGB(x, y));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                int lum = red + green + blue;
                gray.setRGB(x, y, new Color(lum, lum, lum).getRGB());
            }
        }

        return gray;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageProcessingApp().setVisible(true));
    }
}