package DNS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class MenuGrafico extends JFrame {
    private JList<String> itemList;
    private DefaultListModel<String> listModel;
    private JCheckBox[] checkBoxes;
    private VerArchivos archivos;
    private JPanel checkBoxPanel; // Agregamos la declaración de checkBoxPanel

    public MenuGrafico(VerArchivos archivos) {
        this.archivos = archivos;
        setTitle("Menú Gráfico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();
        for (int i = 0; i < archivos.getArchivos().size(); i++) {
            listModel.addElement(archivos.getNombre(i) + "." + archivos.getExtension(i));
        }

        itemList = new JList<>(listModel);

        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemList.addListSelectionListener(e -> updateCheckBoxes());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(itemList), BorderLayout.CENTER);

        checkBoxPanel = new JPanel(); // Inicializamos checkBoxPanel
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.PAGE_AXIS));

        checkBoxes = new JCheckBox[listModel.getSize()];
        for (int i = 0; i < listModel.getSize(); i++) {
            checkBoxes[i] = new JCheckBox();
            checkBoxes[i].setSelected(archivos.getPublicar(i));
            final int index = i;

            checkBoxes[i].addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    archivos.cambiarPublicar(index, true);
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    archivos.cambiarPublicar(index, false);
                }
                System.out.println("Cambio realizado para " + archivos.getNombre(index) + "." + archivos.getExtension(index));
            });

            checkBoxPanel.add(checkBoxes[i]);
        }

        panel.add(checkBoxPanel, BorderLayout.EAST);

        add(panel);
    }

    public void cerrar() {
        dispose();
    }

    public void actualizarMenu() {
        listModel.clear();
        for (int i = 0; i < archivos.getArchivos().size(); i++) {
            listModel.addElement(archivos.getNombre(i) + "." + archivos.getExtension(i));
        }
        updateCheckBoxes(); // Actualiza los checkboxes cuando se actualiza la lista de elementos
    }

    private void updateCheckBoxes() {
        for (JCheckBox checkBox : checkBoxes) {
            checkBoxPanel.remove(checkBox); // Elimina los checkboxes existentes
        }

        checkBoxes = new JCheckBox[listModel.getSize()];
        for (int i = 0; i < listModel.getSize(); i++) {
            checkBoxes[i] = new JCheckBox();
            checkBoxes[i].setSelected(archivos.getPublicar(i));
            final int index = i;

            checkBoxes[i].addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    archivos.cambiarPublicar(index, true);
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    archivos.cambiarPublicar(index, false);
                }
                System.out.println("Cambio realizado para " + archivos.getNombre(index) + "." + archivos.getExtension(index));
            });

            checkBoxPanel.add(checkBoxes[i]); // Agrega los nuevos checkboxes
        }

        checkBoxPanel.revalidate(); // Actualiza la disposición del panel
        checkBoxPanel.repaint(); // Repinta el panel
    }
}
