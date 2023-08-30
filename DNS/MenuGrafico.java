package DNS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuGrafico extends JFrame {
    private JList<String> itemList;
    private DefaultListModel<String> listModel;
    private JCheckBox[] checkBoxes;
    private VerArchivos archivos;

    public MenuGrafico(VerArchivos arvhivos) {
        this.archivos = arvhivos;
        setTitle("Menú Gráfico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();
        for (int i = 0; i < archivos.getArchivos().size(); i++) {
            listModel.addElement(archivos.getNombre(i) + "." + archivos.getExtension(i));
        }

        itemList = new JList<>(listModel);

        checkBoxes = new JCheckBox[listModel.getSize()];
        for (int i = 0; i < listModel.getSize(); i++) {
            checkBoxes[i] = new JCheckBox();
            checkBoxes[i].setSelected(archivos.getPublicar(i));
        }

        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemList.addListSelectionListener(e -> updateCheckBoxes());

        JButton submitButton = new JButton("Enviar");
        submitButton.addActionListener(e -> {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    for (int i = 0; i < listModel.getSize(); i++) {
                        if (checkBoxes[i].isSelected()) {
                            archivos.cambiarPublicar(i, true);
                        }else{
                            arvhivos.cambiarPublicar(i, false);
                        }
                    }
                    System.out.println("cambios realizados");
                    for (int i = 0; i < archivos.getArchivos().size(); i++) {
                        System.out.println(archivos.getNombre(i) + "." + archivos.getExtension(i) + " , " + " - " + archivos.getPublicar(i));
                    }
                    return null;
                }
            };
            worker.execute();
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(itemList), BorderLayout.CENTER);

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.PAGE_AXIS));
        for (JCheckBox checkBox : checkBoxes) {
            checkBoxPanel.add(checkBox);
        }
        
        panel.add(checkBoxPanel, BorderLayout.EAST);
        panel.add(submitButton, BorderLayout.SOUTH);
        
        add(panel);
    }

    public void cerrar() {
        dispose(); // Cierra la ventana y libera los recursos asociados
    }

    public void actualizarMenu() {
        listModel.clear();
        for (int i = 0; i < archivos.getArchivos().size(); i++) {
            listModel.addElement(archivos.getNombre(i) + "." + archivos.getExtension(i));
            checkBoxes[i].setSelected(archivos.getPublicar(i));
        }
    }
    
    private void updateCheckBoxes() {
        for (int i = 0; i < listModel.getSize(); i++) {
            checkBoxes[i].setText(listModel.getElementAt(i));
        }
    }
}

