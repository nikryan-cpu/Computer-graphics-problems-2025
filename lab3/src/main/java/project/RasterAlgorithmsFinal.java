package project;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RasterAlgorithmsFinal extends JFrame {

    // Панель рисования
    private GridPanel gridPanel;
    // Лог (Трассировка)
    private JTextArea logArea;
    // Время выполнения
    private JLabel timeLabel;

    // --- Поля ввода ---
    // Для линий и центров
    private JTextField x1Field, y1Field, x2Field, y2Field;
    // Для окружности
    private JTextField rField;
    // Для кривых Безье (контрольные точки)
    private JTextField x3Field, y3Field, x4Field, y4Field;

    public RasterAlgorithmsFinal() {
        setTitle("ЛР №3: Все растровые алгоритмы (База + Доп. задания)");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Основной контейнер
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 1. Панель управления (Справа)
        JPanel controlPanel = createControlPanel();
        JScrollPane controlScroll = new JScrollPane(controlPanel);
        controlScroll.setPreferredSize(new Dimension(320, 800));

        // 2. Графическая панель (Слева/Центр)
        gridPanel = new GridPanel();

        // Разделитель
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gridPanel, controlScroll);
        splitPane.setResizeWeight(0.8); // 80% места под график

        add(splitPane);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Группа 1: Координаты отрезка / Центра ---
        JPanel linePanel = new JPanel(new GridLayout(2, 4, 5, 5));
        linePanel.setBorder(new TitledBorder("Координаты (Линии / Центр круга)"));
        x1Field = new JTextField("0"); y1Field = new JTextField("0");
        x2Field = new JTextField("15"); y2Field = new JTextField("10");
        linePanel.add(new JLabel("X1:")); linePanel.add(x1Field);
        linePanel.add(new JLabel("Y1:")); linePanel.add(y1Field);
        linePanel.add(new JLabel("X2:")); linePanel.add(x2Field);
        linePanel.add(new JLabel("Y2:")); linePanel.add(y2Field);
        panel.add(linePanel);
        panel.add(Box.createVerticalStrut(5));

        // --- Группа 2: Окружность ---
        JPanel circlePanel = new JPanel(new GridLayout(1, 2, 5, 5));
        circlePanel.setBorder(new TitledBorder("Радиус (Только для круга)"));
        rField = new JTextField("10");
        circlePanel.add(new JLabel("R:")); circlePanel.add(rField);
        panel.add(circlePanel);
        panel.add(Box.createVerticalStrut(5));

        // --- Группа 3: Кривые (Доп) ---
        JPanel curvePanel = new JPanel(new GridLayout(2, 4, 5, 5));
        curvePanel.setBorder(new TitledBorder("Контрольные точки (Безье)"));
        x3Field = new JTextField("5"); y3Field = new JTextField("15");
        x4Field = new JTextField("10"); y4Field = new JTextField("-5");
        curvePanel.add(new JLabel("X3:")); curvePanel.add(x3Field);
        curvePanel.add(new JLabel("Y3:")); curvePanel.add(y3Field);
        curvePanel.add(new JLabel("X4:")); curvePanel.add(x4Field);
        curvePanel.add(new JLabel("Y4:")); curvePanel.add(y4Field);
        panel.add(curvePanel);
        panel.add(Box.createVerticalStrut(15));

        // --- Кнопки алгоритмов ---
        panel.add(new JLabel("Обязательные алгоритмы:"));
        addButton(panel, "1. Пошаговый", e -> runAlgo("STEP"));
        addButton(panel, "2. ЦДА (DDA)", e -> runAlgo("DDA"));
        addButton(panel, "3. Брезенхем (Линия)", e -> runAlgo("BRE_LINE"));
        addButton(panel, "4. Брезенхем (Круг)", e -> runAlgo("BRE_CIRCLE"));

        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Дополнительные (Bonus):"));
        addButton(panel, "5. Ву (Сглаживание)", e -> runAlgo("WU"));
        addButton(panel, "6. Кастл-Питвей (Кривая)", e -> runAlgo("CASTLE"));

        panel.add(Box.createVerticalStrut(15));
        JButton clearBtn = new JButton("Очистить поле");
        clearBtn.setBackground(new Color(255, 220, 220));
        clearBtn.addActionListener(e -> {
            gridPanel.clearPixels();
            logArea.setText("");
            timeLabel.setText("Время: -");
        });
        panel.add(clearBtn);

        // --- Вывод времени ---
        panel.add(Box.createVerticalStrut(15));
        timeLabel = new JLabel("Время: -");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeLabel.setForeground(new Color(0, 100, 0));
        panel.add(timeLabel);

        // --- Лог ---
        panel.add(Box.createVerticalStrut(10));
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(new TitledBorder("Трассировка (Шаги)"));
        // Растягиваем лог на оставшееся место
        panel.add(logScroll);

        return panel;
    }

    private void addButton(JPanel p, String text, java.awt.event.ActionListener al) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, b.getMinimumSize().height));
        b.addActionListener(al);
        p.add(b);
        p.add(Box.createVerticalStrut(3));
    }

    // --- ЗАПУСК АЛГОРИТМОВ ---
    private void runAlgo(String type) {
        try {
            // Считываем основные координаты
            int x1 = Integer.parseInt(x1Field.getText());
            int y1 = Integer.parseInt(y1Field.getText());

            gridPanel.clearPixels();
            logArea.setText("");
            long startTime = System.nanoTime();

            switch (type) {
                case "STEP":
                case "DDA":
                case "BRE_LINE":
                case "WU":
                    int x2 = Integer.parseInt(x2Field.getText());
                    int y2 = Integer.parseInt(y2Field.getText());
                    if (type.equals("STEP")) stepAlgorithm(x1, y1, x2, y2);
                    else if (type.equals("DDA")) ddaAlgorithm(x1, y1, x2, y2);
                    else if (type.equals("BRE_LINE")) bresenhamLine(x1, y1, x2, y2);
                    else if (type.equals("WU")) drawWuLine(x1, y1, x2, y2);
                    break;

                case "BRE_CIRCLE":
                    int r = Integer.parseInt(rField.getText());
                    bresenhamCircle(x1, y1, r);
                    break;

                case "CASTLE":
                    int xEnd = Integer.parseInt(x2Field.getText());
                    int yEnd = Integer.parseInt(y2Field.getText());
                    int x3 = Integer.parseInt(x3Field.getText());
                    int y3 = Integer.parseInt(y3Field.getText());
                    int x4 = Integer.parseInt(x4Field.getText());
                    int y4 = Integer.parseInt(y4Field.getText());
                    logArea.append("Алгоритм Кастла-Питвея (Кривая Безье)\n");
                    // Рисуем контрольные точки для наглядности
                    gridPanel.addPixel(x1, y1, Color.RED, 1.0);
                    gridPanel.addPixel(xEnd, yEnd, Color.RED, 1.0);
                    gridPanel.addPixel(x3, y3, Color.ORANGE, 1.0);
                    gridPanel.addPixel(x4, y4, Color.ORANGE, 1.0);
                    // Запуск рекурсии
                    drawBezierCastle(x1, y1, x3, y3, x4, y4, xEnd, yEnd);
                    break;
            }

            long endTime = System.nanoTime();
            timeLabel.setText("Время: " + (endTime - startTime) / 1000 + " мкс"); // в микросекундах
            gridPanel.repaint();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка ввода! Проверьте, что во всех нужных полях целые числа.");
        }
    }

    // ==========================================
    // РЕАЛИЗАЦИЯ АЛГОРИТМОВ
    // ==========================================

    // 1. Пошаговый (y = kx + b)
    private void stepAlgorithm(int x1, int y1, int x2, int y2) {
        logArea.append("Пошаговый алгоритм (Naive):\n");
        if (Math.abs(x2 - x1) < Math.abs(y2 - y1)) {
            logArea.append("Внимание: Крутой наклон, алгоритм может давать разрывы (нужно менять оси).\n");
        }
        if (x1 > x2) { int t=x1; x1=x2; x2=t; t=y1; y1=y2; y2=t; }

        double k = (double)(y2 - y1) / (x2 - x1);
        double b = y1 - k * x1;
        logArea.append(String.format("y = %.2fx + %.2f\n", k, b));

        for (int x = x1; x <= x2; x++) {
            int y = (int) Math.round(k * x + b);
            gridPanel.addPixel(x, y, Color.BLACK, 1.0);
            logArea.append(String.format("x=%d, y_calc=%.2f -> y=%d\n", x, (k*x+b), y));
        }
    }

    // 2. ЦДА (DDA)
    private void ddaAlgorithm(int x1, int y1, int x2, int y2) {
        logArea.append("Алгоритм ЦДА (DDA):\n");
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        double xInc = (double)dx / steps;
        double yInc = (double)dy / steps;
        double x = x1;
        double y = y1;

        for (int i = 0; i <= steps; i++) {
            gridPanel.addPixel((int)Math.round(x), (int)Math.round(y), Color.BLUE, 1.0);
            logArea.append(String.format("Шаг %d: x=%.2f, y=%.2f\n", i, x, y));
            x += xInc;
            y += yInc;
        }
    }

    // 3. Брезенхем (Линия)
    private void bresenhamLine(int x1, int y1, int x2, int y2) {
        logArea.append("Брезенхем (Целочисленный):\n");
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            gridPanel.addPixel(x1, y1, Color.MAGENTA, 1.0);
            logArea.append(String.format("Plot(%d, %d), Err=%d\n", x1, y1, err));
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx) { err += dx; y1 += sy; }
        }
    }

    // 4. Брезенхем (Окружность)
    private void bresenhamCircle(int xc, int yc, int r) {
        logArea.append("Брезенхем (Окружность):\n");
        int x = 0;
        int y = r;
        int d = 3 - 2 * r;
        drawCirclePoints(xc, yc, x, y);
        while (y >= x) {
            x++;
            if (d > 0) {
                y--;
                d = d + 4 * (x - y) + 10;
            } else {
                d = d + 4 * x + 6;
            }
            drawCirclePoints(xc, yc, x, y);
            logArea.append(String.format("x=%d, y=%d, d=%d\n", x, y, d));
        }
    }
    private void drawCirclePoints(int xc, int yc, int x, int y) {
        gridPanel.addPixel(xc+x, yc+y, Color.GREEN, 1.0);
        gridPanel.addPixel(xc-x, yc+y, Color.GREEN, 1.0);
        gridPanel.addPixel(xc+x, yc-y, Color.GREEN, 1.0);
        gridPanel.addPixel(xc-x, yc-y, Color.GREEN, 1.0);
        gridPanel.addPixel(xc+y, yc+x, Color.GREEN, 1.0);
        gridPanel.addPixel(xc-y, yc+x, Color.GREEN, 1.0);
        gridPanel.addPixel(xc+y, yc-x, Color.GREEN, 1.0);
        gridPanel.addPixel(xc-y, yc-x, Color.GREEN, 1.0);
    }

    // 5. Алгоритм Ву (Сглаживание)
    private void drawWuLine(int x0, int y0, int x1, int y1) {
        logArea.append("Алгоритм Ву (Antialiasing):\n");
        boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
        if (steep) { int t=x0; x0=y0; y0=t; t=x1; x1=y1; y1=t; }
        if (x0 > x1) { int t=x0; x0=x1; x1=t; t=y0; y0=y1; y1=t; }

        double dx = x1 - x0;
        double dy = y1 - y0;
        double gradient = dx == 0 ? 1.0 : dy / dx;

        // Первая точка
        int xpxl1 = x0;
        int ypxl1 = y0;
        plotWu(steep, xpxl1, ypxl1, 1.0); // Упрощенный старт

        double intery = y0 + gradient;

        // Вторая точка
        int xpxl2 = x1;
        plotWu(steep, xpxl2, y1, 1.0);

        // Основной цикл
        for (int x = xpxl1 + 1; x < xpxl2; x++) {
            double brightness1 = 1 - fractionalPart(intery);
            double brightness2 = fractionalPart(intery);
            plotWu(steep, x, (int)intery, brightness1);
            plotWu(steep, x, (int)intery + 1, brightness2);

            if (x < xpxl1 + 5) { // Логируем только первые 5 шагов, чтобы не спамить
                logArea.append(String.format("X=%d: Y=%.2f, B1=%.2f, B2=%.2f\n", x, intery, brightness1, brightness2));
            }
            intery += gradient;
        }
    }
    private double fractionalPart(double x) { return x - Math.floor(x); }
    private void plotWu(boolean steep, int x, int y, double c) {
        if (steep) gridPanel.addPixel(y, x, Color.BLACK, c);
        else gridPanel.addPixel(x, y, Color.BLACK, c);
    }

    // 6. Кастла-Питвея (Кривая Безье)
    private void drawBezierCastle(double x1, double y1, double x2, double y2,
                                  double x3, double y3, double x4, double y4) {
        // Рекурсивное деление
        if (Math.abs(x1 - x4) < 1 && Math.abs(y1 - y4) < 1) {
            gridPanel.addPixel((int)x1, (int)y1, Color.RED, 1.0);
            return;
        }
        double x12 = (x1 + x2)/2; double y12 = (y1 + y2)/2;
        double x23 = (x2 + x3)/2; double y23 = (y2 + y3)/2;
        double x34 = (x3 + x4)/2; double y34 = (y3 + y4)/2;
        double x123 = (x12 + x23)/2; double y123 = (y12 + y23)/2;
        double x234 = (x23 + x34)/2; double y234 = (y23 + y34)/2;
        double x1234 = (x123 + x234)/2; double y1234 = (y123 + y234)/2;

        drawBezierCastle(x1, y1, x12, y12, x123, y123, x1234, y1234);
        drawBezierCastle(x1234, y1234, x234, y234, x34, y34, x4, y4);
    }

    // ==========================================
    // КОМПОНЕНТ СЕТКИ (GRID)
    // ==========================================
    class GridPanel extends JPanel {
        private int scale = 20; // Размер клетки в пикселях
        private List<Pixel> pixels = new ArrayList<>();

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();

            // 1. Рисуем сетку
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);

            // Центр координат на экране
            int centerX = (w / 2 / scale) * scale;
            int centerY = (h / 2 / scale) * scale;

            // Линии сетки
            g2.setColor(new Color(240, 240, 240));
            for (int x = 0; x < w; x += scale) g2.drawLine(x, 0, x, h);
            for (int y = 0; y < h; y += scale) g2.drawLine(0, y, w, y);

            // Оси
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(centerX, 0, centerX, h); // Y
            g2.drawLine(0, centerY, w, centerY); // X
            g2.drawString("X", w - 20, centerY - 5);
            g2.drawString("Y", centerX + 5, 20);
            g2.setStroke(new BasicStroke(1));

            // 2. Рисуем пиксели
            for (Pixel p : pixels) {
                // Перевод логических координат в экранные
                int scrX = centerX + (p.x * scale);
                int scrY = centerY - (p.y * scale) - scale; // -scale т.к. Y вниз

                // Прозрачность (для Ву)
                int alpha = (int)(p.alpha * 255);
                if (alpha < 0) alpha = 0; if (alpha > 255) alpha = 255;

                g2.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), alpha));
                g2.fillRect(scrX + 1, scrY + 1, scale - 1, scale - 1);
            }
        }

        public void addPixel(int x, int y, Color c, double alpha) {
            pixels.add(new Pixel(x, y, c, alpha));
        }

        public void clearPixels() {
            pixels.clear();
            repaint();
        }
    }

    // Вспомогательный класс для хранения пикселя
    static class Pixel {
        int x, y;
        Color color;
        double alpha;
        public Pixel(int x, int y, Color c, double a) {
            this.x = x; this.y = y; this.color = c; this.alpha = a;
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new RasterAlgorithmsFinal().setVisible(true));
    }
}