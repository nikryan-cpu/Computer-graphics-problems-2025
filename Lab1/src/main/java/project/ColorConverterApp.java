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
    private JSlider hSlider;
    private final JSlider sSlider;
    private final JSlider vSlider;
    private final JTextField hField;
    private JTextField sField;
    private JTextField vField;

    private JPanel colorPreviewPanel;
    private JButton chooseColorButton;

    private boolean isUpdating = false;

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

    private void addListeners() {
        // RGB Listeners
        rSlider.addChangeListener(e -> updateFromRGBSliders());
        gSlider.addChangeListener(e -> updateFromRGBSliders());
        bSlider.addChangeListener(e -> updateFromRGBSliders());
        rField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFromRGBFields(); }
            public void removeUpdate(DocumentEvent e) { updateFromRGBFields(); }
            public void changedUpdate(DocumentEvent e) { updateFromRGBFields(); }
        });
        gField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFromRGBFields(); }
            public void removeUpdate(DocumentEvent e) { updateFromRGBFields(); }
            public void changedUpdate(DocumentEvent e) { updateFromRGBFields(); }
        });
        bField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFromRGBFields(); }
            public void removeUpdate(DocumentEvent e) { updateFromRGBFields(); }
            public void changedUpdate(DocumentEvent e) { updateFromRGBFields(); }
        });


        // CMYK Listeners
        cSlider.addChangeListener(e -> updateFromCMYKSliders());
        mSlider.addChangeListener(e -> updateFromCMYKSliders());
        ySlider.addChangeListener(e -> updateFromCMYKSliders());
        kSlider.addChangeListener(e -> updateFromCMYKSliders());
        cField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFromCMYKFields(); }
            public void removeUpdate(DocumentEvent e) { updateFromCMYKFields(); }
            public void changedUpdate(DocumentEvent e) { updateFromCMYKFields(); }
        });
        mField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFromCMYKFields(); }
            public void removeUpdate(DocumentEvent e) { updateFromCMYKFields(); }
            public void changedUpdate(DocumentEvent e) { updateFromCMYKFields(); }
        });
        yField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFromCMYKFields(); }
            public void removeUpdate(DocumentEvent e) { updateFromCMYKFields(); }
            public void changedUpdate(DocumentEvent e) { updateFromCMYKFields(); }
        });
        kField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFromCMYKFields(); }
            public void removeUpdate(DocumentEvent e) { updateFromCMYKFields(); }
            public void changedUpdate(DocumentEvent e) { updateFromCMYKFields(); }
        });


        // HSV Listeners
        hSlider.addChangeListener(e -> updateFromHSVSliders());
        sSlider.addChangeListener(e -> updateFromHSVSliders());
        vSlider.addChangeListener(e -> updateFromHSVSliders());
        hField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFromHSVFields(); }
            public void removeUpdate(DocumentEvent e) { updateFromHSVFields(); }
            public void changedUpdate(DocumentEvent e) { updateFromHSVFields(); }
        });
        sField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFromHSVFields(); }
            public void removeUpdate(DocumentEvent e) { updateFromHSVFields(); }
            public void changedUpdate(DocumentEvent e) { updateFromHSVFields(); }
        });
        vField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFromHSVFields(); }
            public void removeUpdate(DocumentEvent e) { updateFromHSVFields(); }
            public void changedUpdate(DocumentEvent e) { updateFromHSVFields(); }
        });


        chooseColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose a color", colorPreviewPanel.getBackground());
            if (newColor != null) {
                updateColor(newColor);
            }
        });
    }

    private void updateFromRGBSliders() {
        if (!isUpdating) {
            int r = rSlider.getValue();
            int g = gSlider.getValue();
            int b = bSlider.getValue();
            updateColor(new Color(r, g, b));
        }
    }

    private void updateFromRGBFields() {
        if (!isUpdating) {
            try {
                int r = Integer.parseInt(rField.getText());
                int g = Integer.parseInt(gField.getText());
                int b = Integer.parseInt(bField.getText());
                updateColor(new Color(r, g, b));
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
    }

    private void updateFromCMYKSliders() {
        if (!isUpdating) {
            float c = cSlider.getValue() / 100f;
            float m = mSlider.getValue() / 100f;
            float y = ySlider.getValue() / 100f;
            float k = kSlider.getValue() / 100f;
            updateColor(cmykToRgb(c, m, y, k));
        }
    }

    private void updateFromCMYKFields() {
        if (!isUpdating) {
            try {
                float c = Float.parseFloat(cField.getText()) / 100f;
                float m = Float.parseFloat(mField.getText()) / 100f;
                float y = Float.parseFloat(yField.getText()) / 100f;
                float k = Float.parseFloat(kField.getText()) / 100f;
                updateColor(cmykToRgb(c, m, y, k));
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
    }

    private void updateFromHSVSliders() {
        if (!isUpdating) {
            float h = hSlider.getValue() / 360f;
            float s = sSlider.getValue() / 100f;
            float v = vSlider.getValue() / 100f;
            updateColor(Color.getHSBColor(h, s, v));
        }
    }

    private void updateFromHSVFields() {
        if (!isUpdating) {
            try {
                float h = Float.parseFloat(hField.getText()) / 360f;
                float s = Float.parseFloat(sField.getText()) / 100f;
                float v = Float.parseFloat(vField.getText()) / 100f;
                updateColor(Color.getHSBColor(h, s, v));
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
    }


    private void updateColor(Color color) {
        isUpdating = true;

        // Update RGB
        rSlider.setValue(color.getRed());
        gSlider.setValue(color.getGreen());
        bSlider.setValue(color.getBlue());
        rField.setText(String.valueOf(color.getRed()));
        gField.setText(String.valueOf(color.getGreen()));
        bField.setText(String.valueOf(color.getBlue()));

        // Update CMYK
        float[] cmyk = rgbToCmyk(color.getRed(), color.getGreen(), color.getBlue());
        cSlider.setValue((int) (cmyk[0] * 100));
        mSlider.setValue((int) (cmyk[1] * 100));
        ySlider.setValue((int) (cmyk[2] * 100));
        kSlider.setValue((int) (cmyk[3] * 100));
        cField.setText(String.format("%.0f", cmyk[0] * 100));
        mField.setText(String.format("%.0f", cmyk[1] * 100));
        yField.setText(String.format("%.0f", cmyk[2] * 100));
        kField.setText(String.format("%.0f", cmyk[3] * 100));

        // Update HSV
        float[] hsv = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
        hSlider.setValue((int) (hsv[0] * 360));
        sSlider.setValue((int) (hsv[1] * 100));
        vSlider.setValue((int) (hsv[2] * 100));
        hField.setText(String.format("%.0f", hsv[0] * 360));
        sField.setText(String.format("%.0f", hsv[1] * 100));
        vField.setText(String.format("%.0f", hsv[2] * 100));

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
        float c = (1 - r_ - k) / (1 - k);
        float m = (1 - g_ - k) / (1 - k);
        float y = (1 - b_ - k) / (1 - k);
        return new float[]{c, m, y, k};
    }

    private Color cmykToRgb(float c, float m, float y, float k) {
        int r = (int) (255 * (1 - c) * (1 - k));
        int g = (int) (255 * (1 - m) * (1 - k));
        int b = (int) (255 * (1 - y) * (1 - k));
        return new Color(r, g, b);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ColorConverterApp::new);
    }
}