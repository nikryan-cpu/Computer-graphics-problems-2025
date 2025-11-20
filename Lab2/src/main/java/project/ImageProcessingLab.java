package project;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageProcessingLab extends JFrame {

    private final JLabel imageLabel;
    private BufferedImage sourceImage;
    private BufferedImage currentImage;

    public ImageProcessingLab() {
        setTitle("Лабораторная работа: Обработка изображений");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Основная панель
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Панель для отображения картинки (с прокруткой)
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель управления
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JButton loadButton = new JButton("Загрузить изображение");
        JButton blurButton = new JButton("Сглаживание (Blur)");
        JButton niblackButton = new JButton("Метод Ниблэка");
        JButton sauvolaButton = new JButton("Метод Сауволы");
        JButton resetButton = new JButton("Сброс");

        // --- Обработчики событий ---

        loadButton.addActionListener(e -> loadNewImage());

        blurButton.addActionListener(e -> {
            if (sourceImage != null) {
                // Размер ядра 5x5
                applyFilter(applyBoxBlur(currentImage, 5));
            }
        });

        niblackButton.addActionListener(e -> {
            if (sourceImage != null) {
                // Окно 15, k = -0.2
                applyFilter(applyNiblack(currentImage, 15, -0.2));
            }
        });

        sauvolaButton.addActionListener(e -> {
            if (sourceImage != null) {
                // Окно 15, k = 0.2 (для Сауволы k положительный обычно)
                applyFilter(applySauvola(currentImage, 15, 0.2));
            }
        });

        resetButton.addActionListener(e -> {
            if (sourceImage != null) {
                currentImage = copyImage(sourceImage);
                imageLabel.setIcon(new ImageIcon(currentImage));
            }
        });

        controlPanel.add(loadButton);
        controlPanel.add(blurButton);
        controlPanel.add(niblackButton);
        controlPanel.add(sauvolaButton);
        controlPanel.add(resetButton);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    // --- Вспомогательные методы GUI ---

    private void loadNewImage() {
        JFileChooser fileChooser = new JFileChooser();
        int res = fileChooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                sourceImage = ImageIO.read(file);
                currentImage = copyImage(sourceImage);
                imageLabel.setIcon(new ImageIcon(currentImage));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки: " + ex.getMessage());
            }
        }
    }

    private void applyFilter(BufferedImage processed) {
        currentImage = processed;
        imageLabel.setIcon(new ImageIcon(currentImage));
    }

    private BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    // --- АЛГОРИТМЫ ОБРАБОТКИ ---

    /**
     * 1. Низкочастотный фильтр (Box Blur - усреднение)
     */
    private BufferedImage applyBoxBlur(BufferedImage src, int radius) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage result = new BufferedImage(width, height, src.getType());

        int windowSize = radius * 2 + 1;
        int area = windowSize * windowSize;

        for (int y = radius; y < height - radius; y++) {
            for (int x = radius; x < width - radius; x++) {
                int rSum = 0, gSum = 0, bSum = 0;

                // Проход по окну
                for (int ky = -radius; ky <= radius; ky++) {
                    for (int kx = -radius; kx <= radius; kx++) {
                        int pixel = src.getRGB(x + kx, y + ky);
                        rSum += (pixel >> 16) & 0xFF;
                        gSum += (pixel >> 8) & 0xFF;
                        bSum += pixel & 0xFF;
                    }
                }

                int r = rSum / area;
                int g = gSum / area;
                int b = bSum / area;

                int newPixel = (0xFF << 24) | (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newPixel);
            }
        }
        return result;
    }

    /**
     * Вспомогательный метод: Получение яркости пикселя (Grayscale)
     */
    private int getGray(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        // Формула люминансности
        return (int) (0.299 * r + 0.587 * g + 0.114 * b);
    }

    /**
     * 2. Локальная пороговая обработка: Метод Ниблэка
     * T = mean + k * stdDev
     */
    private BufferedImage applyNiblack(BufferedImage src, int windowSize, double k) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        int radius = windowSize / 2;

        for (int y = radius; y < height - radius; y++) {
            for (int x = radius; x < width - radius; x++) {

                double sum = 0;
                double sumSq = 0;
                int count = 0;

                // 1. Вычисляем среднее и стандартное отклонение в окне
                for (int ky = -radius; ky <= radius; ky++) {
                    for (int kx = -radius; kx <= radius; kx++) {
                        int gray = getGray(src.getRGB(x + kx, y + ky));
                        sum += gray;
                        sumSq += gray * gray;
                        count++;
                    }
                }

                double mean = sum / count;
                double variance = (sumSq / count) - (mean * mean);
                double stdDev = Math.sqrt(Math.max(0, variance));

                // 2. Формула Ниблэка
                double threshold = mean + k * stdDev;

                int centerGray = getGray(src.getRGB(x, y));
                int newPixel = (centerGray > threshold) ? 0xFFFFFFFF : 0xFF000000;
                result.setRGB(x, y, newPixel);
            }
        }
        return result;
    }

    /**
     * 3. Локальная пороговая обработка: Метод Сауволы
     * T = mean * (1 + k * (stdDev / R - 1))
     */
    private BufferedImage applySauvola(BufferedImage src, int windowSize, double k) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        int radius = windowSize / 2;
        double R = 128.0; // Динамический диапазон стандартного отклонения

        for (int y = radius; y < height - radius; y++) {
            for (int x = radius; x < width - radius; x++) {

                double sum = 0;
                double sumSq = 0;
                int count = 0;

                for (int ky = -radius; ky <= radius; ky++) {
                    for (int kx = -radius; kx <= radius; kx++) {
                        int gray = getGray(src.getRGB(x + kx, y + ky));
                        sum += gray;
                        sumSq += gray * gray;
                        count++;
                    }
                }

                double mean = sum / count;
                double variance = (sumSq / count) - (mean * mean);
                double stdDev = Math.sqrt(Math.max(0, variance));

                // Формула Сауволы
                double threshold = mean * (1 + k * ((stdDev / R) - 1));

                int centerGray = getGray(src.getRGB(x, y));
                int newPixel = (centerGray > threshold) ? 0xFFFFFFFF : 0xFF000000;
                result.setRGB(x, y, newPixel);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ImageProcessingLab().setVisible(true);
        });
    }
}