package project;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ColorConverterApp extends JFrame {
    // RGB Components
    private final JSlider rSlider;
    private final JSlider gSlider;
    private final JSlider bSlider;
    private final JTextField rField;
    private final JTextField gField;
    private final JTextField bField;
    // CMYK Components
    private final JSlider cSlider;
    private final JSlider mSlider;
    private final JSlider ySlider;
    private final JSlider kSlider;
    private final JTextField cField;
    private final JTextField mField;
    private final JTextField yField;
    private final JTextField kField;
    // HSV Components
    private final JSlider hSlider;
    private final JSlider sSlider;
    private final JSlider vSlider;
    private final JTextField hField;
    private final JTextField sField;
    private final JTextField vField;
    private JPanel colorPreviewPanel;
    private JButton chooseColorButton;
    private boolean isUpdating = false;
    private float preservedH = 0f;
    private float preservedS = 1f;
    private static final float LOW_VALUE_THRESHOLD = 0.02f;

    public ColorConverterApp() {
        setTitle("Color Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // RGB Panel
        JPanel rgbPanel = createColorPanel("RGB");
        rSlider = createSlider(0, 255);
        gSlider = createSlider(0, 255);
        bSlider = createSlider(0, 255);
        rField = createTextField();
        gField = createTextField();
        bField = createTextField();
        addComponentsToPanel(rgbPanel, new JLabel("R:"), rSlider, rField);
        addComponentsToPanel(rgbPanel, new JLabel("G:"), gSlider, gField);
        addComponentsToPanel(rgbPanel, new JLabel("B:"), bSlider, bField);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(rgbPanel, gbc);
        // CMYK Panel
        JPanel cmykPanel = createColorPanel("CMYK");
        cSlider = createSlider(0, 100);
        mSlider = createSlider(0, 100);
        ySlider = createSlider(0, 100);
        kSlider = createSlider(0, 100);
        cField = createTextField();
        mField = createTextField();
        yField = createTextField();
        kField = createTextField();
        addComponentsToPanel(cmykPanel, new JLabel("C:"), cSlider, cField);
        addComponentsToPanel(cmykPanel, new JLabel("M:"), mSlider, mField);
        addComponentsToPanel(cmykPanel, new JLabel("Y:"), ySlider, yField);
        addComponentsToPanel(cmykPanel, new JLabel("K:"), kSlider, kField);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(cmykPanel, gbc);
        // HSV Panel
        JPanel hsvPanel = createColorPanel("HSV");
        hSlider = createSlider(0, 360);
        sSlider = createSlider(0, 100);
        vSlider = createSlider(0, 100);
        hField = createTextField();
        sField = createTextField();
        vField = createTextField();
        addComponentsToPanel(hsvPanel, new JLabel("H:"), hSlider, hField);
        addComponentsToPanel(hsvPanel, new JLabel("S:"), sSlider, sField);
        addComponentsToPanel(hsvPanel, new JLabel("V:"), vSlider, vField);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(hsvPanel, gbc);
        // Color Preview and Button Panel
        JPanel previewAndButtonPanel = new JPanel(new BorderLayout(10, 10));
        colorPreviewPanel = new JPanel();
        colorPreviewPanel.setPreferredSize(new Dimension(100, 100));
        colorPreviewPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        chooseColorButton = new JButton("Choose Color");
        previewAndButtonPanel.add(colorPreviewPanel, BorderLayout.CENTER);
        previewAndButtonPanel.add(chooseColorButton, BorderLayout.SOUTH);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(previewAndButtonPanel, gbc);
        addListeners();
        updateColor(new Color(0, 0, 0));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private JPanel createColorPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }
    private JSlider createSlider(int min, int max) {
        JSlider slider = new JSlider(min, max);
        slider.setMajorTickSpacing((max - min) / 5);
        slider.setPaintTicks(true);
        return slider;
    }
    private JTextField createTextField() {
        return new JTextField(4);
    }
    private void addComponentsToPanel(JPanel panel, JLabel label, JSlider slider, JTextField textField) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        panel.add(label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(slider, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(textField, gbc);
    }

    private void addDocumentListener(JTextField field, Runnable updateAction) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateAction.run(); }
            public void removeUpdate(DocumentEvent e) { updateAction.run(); }
            public void changedUpdate(DocumentEvent e) { updateAction.run(); }
        });
    }

    private void addListeners() {
        // RGB Listeners
        rSlider.addChangeListener(e -> updateFromRGBSliders());
        gSlider.addChangeListener(e -> updateFromRGBSliders());
        bSlider.addChangeListener(e -> updateFromRGBSliders());
        addDocumentListener(rField, this::updateFromRGBFields);
        addDocumentListener(gField, this::updateFromRGBFields);
        addDocumentListener(bField, this::updateFromRGBFields);

        // CMYK Listeners
        cSlider.addChangeListener(e -> updateFromCMYKSliders());
        mSlider.addChangeListener(e -> updateFromCMYKSliders());
        ySlider.addChangeListener(e -> updateFromCMYKSliders());
        kSlider.addChangeListener(e -> updateFromCMYKSliders());
        addDocumentListener(cField, this::updateFromCMYKFields);
        addDocumentListener(mField, this::updateFromCMYKFields);
        addDocumentListener(yField, this::updateFromCMYKFields);
        addDocumentListener(kField, this::updateFromCMYKFields);

        // HSV Listeners
        hSlider.addChangeListener(e -> updateFromHSVSliders());
        sSlider.addChangeListener(e -> updateFromHSVSliders());
        vSlider.addChangeListener(e -> updateFromVComponents()); // Special handler for V slider

        addDocumentListener(hField, this::updateFromHSVFields);
        addDocumentListener(sField, this::updateFromHSVFields);
        addDocumentListener(vField, this::updateFromHSVFields); // General handler for text fields

        chooseColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose a color", colorPreviewPanel.getBackground());
            if (newColor != null) {
                updateColor(newColor);
            }
        });
    }

    private void updateFromRGBSliders() {
        if (isUpdating) return;
        int r = rSlider.getValue();
        int g = gSlider.getValue();
        int b = bSlider.getValue();
        updateColor(new Color(r, g, b));
    }
    private void updateFromRGBFields() {
        if (isUpdating) return;
        try {
            int r = Integer.parseInt(rField.getText());
            int g = Integer.parseInt(gField.getText());
            int b = Integer.parseInt(bField.getText());
            if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) return;
            updateColor(new Color(r, g, b));
        } catch (NumberFormatException e) { /* Ignore */ }
    }
    private void updateFromCMYKSliders() {
        if (isUpdating) return;
        float c = cSlider.getValue() / 100f;
        float m = mSlider.getValue() / 100f;
        float y = ySlider.getValue() / 100f;
        float k = kSlider.getValue() / 100f;
        updateColor(cmykToRgb(c, m, y, k));
    }
    private void updateFromCMYKFields() {
        if (isUpdating) return;
        try {
            float c = Float.parseFloat(cField.getText()) / 100f;
            float m = Float.parseFloat(mField.getText()) / 100f;
            float y = Float.parseFloat(yField.getText()) / 100f;
            float k = Float.parseFloat(kField.getText()) / 100f;
            if (c < 0 || c > 1 || m < 0 || m > 1 || y < 0 || y > 1 || k < 0 || k > 1) return;
            updateColor(cmykToRgb(c, m, y, k));
        } catch (NumberFormatException e) { /* Ignore */ }
    }

    private void updateFromHSVSliders() {
        if (isUpdating) return;
        float h = hSlider.getValue() / 360f;
        float s = sSlider.getValue() / 100f;
        float v = vSlider.getValue() / 100f;
        updateColor(Color.getHSBColor(h, s, v));
    }

    private void updateFromHSVFields() {
        if (isUpdating) return;
        try {
            float h = Float.parseFloat(hField.getText()) / 360f;
            float s = Float.parseFloat(sField.getText()) / 100f;
            float v = Float.parseFloat(vField.getText()) / 100f;
            if (h < 0 || h > 1 || s < 0 || s > 1 || v < 0 || v > 1) return;
            updateColor(Color.getHSBColor(h, s, v));
        } catch (NumberFormatException e) { /* Ignore */ }
    }

    // Special method to update from V slider without resetting H and S sliders
    private void updateFromVComponents() {
        if (isUpdating) return;
        isUpdating = true;

        float h = hSlider.getValue() / 360f;
        float s = sSlider.getValue() / 100f;
        float v = vSlider.getValue() / 100f;
        Color color = Color.getHSBColor(h, s, v);

        // Update everything EXCEPT H and S components
        updateRGB(color);
        updateCMYK(color);
        colorPreviewPanel.setBackground(color);
        vField.setText(String.format("%.0f", v * 100)); // Only update V field

        isUpdating = false;
    }

    private void updateRGB(Color color) {
        rSlider.setValue(color.getRed());
        gSlider.setValue(color.getGreen());
        bSlider.setValue(color.getBlue());
        rField.setText(String.valueOf(color.getRed()));
        gField.setText(String.valueOf(color.getGreen()));
        bField.setText(String.valueOf(color.getBlue()));
    }

    private void updateCMYK(Color color) {
        float[] cmyk = rgbToCmyk(color.getRed(), color.getGreen(), color.getBlue());
        cSlider.setValue((int) Math.round(cmyk[0] * 100));
        mSlider.setValue((int) Math.round(cmyk[1] * 100));
        ySlider.setValue((int) Math.round(cmyk[2] * 100));
        kSlider.setValue((int) Math.round(cmyk[3] * 100));
        cField.setText(String.format("%.0f", cmyk[0] * 100));
        mField.setText(String.format("%.0f", cmyk[1] * 100));
        yField.setText(String.format("%.0f", cmyk[2] * 100));
        kField.setText(String.format("%.0f", cmyk[3] * 100));
    }

    private void updateHSV(Color color) {
        float[] hsv = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);

        if (hsv[2] > LOW_VALUE_THRESHOLD) {
            preservedS = hsv[1];
            if (hsv[1] > LOW_VALUE_THRESHOLD) {
                preservedH = hsv[0];
            }
        }
        float displayH = (hsv[2] <= LOW_VALUE_THRESHOLD || hsv[1] <= LOW_VALUE_THRESHOLD) ? preservedH : hsv[0];
        float displayS = (hsv[2] <= LOW_VALUE_THRESHOLD) ? preservedS : hsv[1];

        hSlider.setValue((int) Math.round(displayH * 360));
        sSlider.setValue((int) Math.round(displayS * 100));
        vSlider.setValue((int) Math.round(hsv[2] * 100));
        hField.setText(String.format("%.0f", displayH * 360));
        sField.setText(String.format("%.0f", displayS * 100));
        vField.setText(String.format("%.0f", hsv[2] * 100));
    }

    // General update method, called from most places
    private void updateColor(Color color) {
        isUpdating = true;
        updateRGB(color);
        updateCMYK(color);
        updateHSV(color);
        colorPreviewPanel.setBackground(color);
        isUpdating = false;
    }

    private float[] rgbToCmyk(int r, int g, int b) {
        if (r == 0 && g == 0 && b == 0) {
            return new float[]{0, 0, 0, 1};
        }
        float r_ = r / 255f;
        float g_ = g / 255f;
        float b_ = b / 255f;
        float k = 1 - Math.max(r_, Math.max(g_, b_));
        if (1 - k < 1e-9) { // Avoid division by zero for pure black
            return new float[]{0, 0, 0, 1};
        }
        float c = (1 - r_ - k) / (1 - k);
        float m = (1 - g_ - k) / (1 - k);
        float y = (1 - b_ - k) / (1 - k);
        return new float[]{c, m, y, k};
    }

    private Color cmykToRgb(float c, float m, float y, float k) {
        int r = (int) Math.round(255.0f * (1 - c) * (1 - k));
        int g = (int) Math.round(255.0f * (1 - m) * (1 - k));
        int b = (int) Math.round(255.0f * (1 - y) * (1 - k));
        return new Color(r, g, b);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ColorConverterApp::new);
    }
}