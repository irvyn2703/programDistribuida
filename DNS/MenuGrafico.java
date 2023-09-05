package DNS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class MenuGrafico extends JFrame {
    private JList<String> itemList;
    private DefaultListModel<String> listModel;
    private JCheckBox[] checkBoxes;
    private VerArchivos archivos;
    private JPanel checkBoxPanel;

    public MenuGrafico(VerArchivos archivos) {
        this.archivos = archivos;// agregamos el archivo donde tenemos la lista de archivos
        //agregamos atributos principales de nuestro menu
        setTitle("Menú Gráfico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();// agregamos el nombre de los archivos que tenemos
        for (int i = 0; i < archivos.getArchivos().size(); i++) {
            listModel.addElement(archivos.getNombre(i) + "." + archivos.getExtension(i));
        }

        itemList = new JList<>(listModel); // los agregamos

        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemList.addListSelectionListener(e -> actualizarCheckBoxes());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(itemList), BorderLayout.CENTER);// lo agregamos a nuestro panel

        checkBoxPanel = new JPanel(); // Inicializamos checkBoxPanel
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.PAGE_AXIS));

        checkBoxes = new JCheckBox[listModel.getSize()];
        for (int i = 0; i < listModel.getSize(); i++) {
            // agregamos los checkbox con el atributu de publicar 
            checkBoxes[i] = new JCheckBox();
            checkBoxes[i].setSelected(archivos.getPublicar(i));
            final int index = i;

            checkBoxes[i].addItemListener(e -> {// agregamos un evento al checbox
                if (e.getStateChange() == ItemEvent.SELECTED) {// si el checbox esta seleccionado
                    archivos.cambiarPublicar(index, true);// cambiamos el estado
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {// en caso contrario
                    archivos.cambiarPublicar(index, false);// cambiamos el estado
                }
                System.out.println("Cambio realizado para " + archivos.getNombre(index) + "." + archivos.getExtension(index));
            });

            checkBoxPanel.add(checkBoxes[i]);// agregamos el checkbox al panel
        }

        panel.add(checkBoxPanel, BorderLayout.EAST);// agregamos los checkbox al panel

        add(panel); // agregamos el panel
    }

    public void actualizarMenu() {
        listModel.clear();// limpiamos el la lista
        for (int i = 0; i < archivos.getArchivos().size(); i++) {
            listModel.addElement(archivos.getNombre(i) + "." + archivos.getExtension(i));// volvemos a agregar los archivos
        }
        actualizarCheckBoxes(); // Actualiza los checkboxes cuando se actualiza la lista de elementos
    }

    private void actualizarCheckBoxes() {
        for (JCheckBox checkBox : checkBoxes) {
            checkBoxPanel.remove(checkBox); // Elimina los checkboxes existentes
        }

        checkBoxes = new JCheckBox[listModel.getSize()];
        for (int i = 0; i < listModel.getSize(); i++) { // agregamos los checkboxes
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
