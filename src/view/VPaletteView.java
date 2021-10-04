package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionListener;
import model.image.VImage;
import model.image.VLayer;
import model.misc.Constants;
import model.misc.ObjectsExtension;
import model.misc.VImageUtils;
import model.processing.VImageFilters;

/**
 * A GUI view that displays two images to a user, one for the layer that the
 * user is currently working on and another that displays the top-most visible
 * layer to the user. The {@link VPaletteView} allows the user to work on a
 * different layer from the top-most visible layer while still working on their
 * entire project.
 */
public class VPaletteView extends JFrame implements VIView {

  private final JList<String> layerNamesView;
  private final JSplitImageView imageView;
  private JCheckBoxMenuItem visibleMenuItem;

  private final List<VIViewListener> listeners;

  // Set default window parameters
  {
    setTitle("Vido");
    setSize(new Dimension(1200, 1000));
    setPreferredSize(new Dimension(1200, 1000));
    setLayout(new BorderLayout(10, 10));
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  /**
   * Construct a new palette view that defaults to having two empty
   * images as placeholders.
   */
  public VPaletteView() {
    super();

    JMenuBar menuBar = createMenuBar();
    JSplitPane body = new JSplitPane();
    this.layerNamesView = new JList<>();
    LayersView layersView = new LayersView(this.layerNamesView);
    this.imageView = new JSplitImageView();
    this.listeners = new ArrayList<>();

    body.setOneTouchExpandable(true);
    body.setLeftComponent(layersView);
    body.setRightComponent(imageView);

    this.setJMenuBar(menuBar);
    this.add(body, BorderLayout.CENTER);

    // Initialize listeners to relay to the
    // view listeners
    {
      ListSelectionListener layersViewListener = listSelectionEvent -> {
        if (!listSelectionEvent.getValueIsAdjusting()) {
          listeners.forEach(listener -> {
            listener.layerWithIndexSelected(listSelectionEvent.getFirstIndex());
          });
        }
      };
      this.layerNamesView.addListSelectionListener(layersViewListener);
    }
  }

  @Override
  public void renderAsTopMostVisibleLayer(VLayer layer) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(layer);
    this.imageView.setRightImage(layer);
  }

  @Override
  public void renderAsCurrentLayer(VLayer layer) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(layer);
    this.imageView.setLeftImage(layer);

    // Update the menu checkbox to match the state of the layer provided
    visibleMenuItem.setSelected(layer.isVisible());
  }

  /**
   * Renders the names of the layers using the names provided.
   *
   * @param names the names of the layers to be displayed
   * @throws IllegalArgumentException if any of the names are null or if the array is null
   */
  private void renderLayersUsing(String[] names) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull((Object) names);
    ObjectsExtension.requireNonnull((Object[]) names);

    this.layerNamesView.setModel(new AbstractListModel<String>() {
      @Override
      public int getSize() {
        return names.length;
      }

      @Override
      public String getElementAt(int index) {
        return names[index];
      }
    });
    this.layerNamesView.updateUI();
  }

  @Override
  public void renderLayerNames(String[] names, int currentIndex) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull((Object) names);
    ObjectsExtension.requireNonnull((Object[]) names);

    if (currentIndex < 0 || currentIndex >= names.length) {
      throw new IllegalArgumentException("Current layer identified illegal");
    }

    this.renderLayersUsing(names);
  }

  @Override
  public void clearLayerNames() {
    this.renderLayersUsing(new String[]{});
  }

  @Override
  public void addListener(VIViewListener listener) throws IllegalArgumentException {
    ObjectsExtension.requireNonnull(listener);
    listeners.add(listener);
  }

  @Override
  public void display() {
    setVisible(true);
  }

  /**
   * Creates a new menu bar that includes all of the
   * menus and their submenus for manipulating images
   * in Vido.
   *
   * <p>The menu bar includes two main menus, titled "File" and
   * "Edit" respectively. Both provide numerous features for
   * manipulating and saving images</p>
   *
   * @return a new menu bar with all the standard Vido features
   */
  private JMenuBar createMenuBar() {
    JMenuBar toolMenuBar = new JMenuBar();

    // Create the "File"" menu
    {
      JMenu fileMenu =  new JMenu("File");

      // Create the "Load" submenu
      {
        JMenu loadSubmenu = new JMenu("Load");

        JMenuItem loadProject = new JMenuItem("Load Project");
        JMenuItem loadImage = new JMenuItem("Load Image");
        JMenuItem loadFromScript = new JMenuItem("Load from Script");

        loadSubmenu.add(loadProject);
        loadSubmenu.add(loadImage);
        loadSubmenu.add(loadFromScript);

        loadProject.addActionListener(e -> {
          listeners.forEach(VIViewListener::loadProject);
        });

        loadImage.addActionListener(e -> {
          listeners.forEach(VIViewListener::loadImage);
        });

        loadFromScript.addActionListener(e -> {
          listeners.forEach(VIViewListener::executeScript);
        });

        fileMenu.add(loadSubmenu);
      }

      // Create the "Save" submenu
      {
        JMenu saveSubmenu = new JMenu("Save");

        JMenuItem saveCurrentLayer = new JMenuItem("Save Current Layer");
        JMenuItem saveTopMostLayer = new JMenuItem("Save Top-most Layer");
        JMenuItem saveProject = new JMenuItem("Save as Project");

        saveProject.addActionListener(e -> {
          listeners.forEach(VIViewListener::saveProject);
        });

        saveCurrentLayer.addActionListener(e -> {
          listeners.forEach(VIViewListener::saveLayer);
        });

        saveTopMostLayer.addActionListener(e -> {
          listeners.forEach(VIViewListener::saveTopMostVisibleLayer);
        });
        saveSubmenu.add(saveCurrentLayer);
        saveSubmenu.add(saveTopMostLayer);
        saveSubmenu.add(saveProject);

        fileMenu.add(saveSubmenu);
      }

      toolMenuBar.add(fileMenu);
    }

    // Create the "Edit" menu
    {
      JMenu editMenu =  new JMenu("Edit");

      // Create the "Apply"" submenu
      {
        JMenu applySubmenu = new JMenu("Apply");

        JMenuItem applySepia = new JMenuItem("Apply Sepia Filter");
        JMenuItem applyGrayscale = new JMenuItem("Apply Grayscale Filter");
        JMenuItem applySharpen = new JMenuItem("Apply Sharpen Filter");
        JMenuItem applyBlurFilter = new JMenuItem("Apply Blur Filter");
        JMenuItem applyMosaicFilter = new JMenuItem("Apply Mosaic Filter");

        applySepia.addActionListener(e -> {
          listeners.forEach(listener -> listener.applyOperation(VImageFilters.sepiaColorFilter()));
        });

        applyGrayscale.addActionListener(e -> {
          listeners.forEach(listener -> listener.applyOperation(
                  VImageFilters.grayscaleColorFilter()));
        });

        applySharpen.addActionListener(e -> {
          listeners.forEach(listener -> listener.applyOperation(VImageFilters.sharpenFilter()));
        });

        applyBlurFilter.addActionListener(e -> {
          listeners.forEach(listener -> listener.applyOperation(VImageFilters.blurFilter()));
        });

        applyMosaicFilter.addActionListener(e -> {
          listeners.forEach(VIViewListener::applyMosaic);
        });

        applySubmenu.add(applySepia);
        applySubmenu.add(applyGrayscale);
        applySubmenu.add(applySharpen);
        applySubmenu.add(applyBlurFilter);
        applySubmenu.add(applyMosaicFilter);

        editMenu.add(applySubmenu);
      }

      // Create the "Layer" submenu
      {
        JMenu currentLayer = new JMenu("Layer");

        JMenuItem renameCurrent = new JMenuItem("Rename Current Layer");
        JMenuItem removeCurrent = new JMenuItem("Remove Current Layer");
        currentLayer.add(renameCurrent);
        currentLayer.add(removeCurrent);

        renameCurrent.addActionListener(listener -> {
          listeners.forEach(VIViewListener::changeName);
        });

        removeCurrent.addActionListener(listener -> {
          listeners.forEach(VIViewListener::removeLayer);
        });

        // Add a checkbox menu for visibility
        {
          JCheckBoxMenuItem visibilityItem = new JCheckBoxMenuItem("Visible");

          visibilityItem.addActionListener(listener -> {
            if (visibilityItem.isSelected()) {
              listeners.forEach(VIViewListener::setCurrentVisibleOn);
            }
            else {
              listeners.forEach(VIViewListener::setCurrentVisibleOff);
            }
          });
          this.visibleMenuItem = visibilityItem;

          currentLayer.add(visibilityItem);
        }

        // Create the "Create" submenu
        {
          JMenu createMenu = new JMenu("Create");

          JMenuItem createNewLayer = new JMenuItem("New Layer");
          JMenuItem copyExistingLayer = new JMenuItem("New Layer From Existing");

          createNewLayer.addActionListener(listener -> {
            listeners.forEach(VIViewListener::addLayer);
          });

          copyExistingLayer.addActionListener(listener -> {
            listeners.forEach(VIViewListener::addFromCopy);
          });

          createMenu.add(createNewLayer);
          createMenu.add(copyExistingLayer);

          currentLayer.add(createMenu);
        }

        editMenu.add(currentLayer);
      }

      toolMenuBar.add(editMenu);
    }

    return toolMenuBar;
  }

  /**
   * A custom {@link JPanel} that is capable of supporting the
   * display of two {@link VImage} instances side-by-side simultaneously.
   *
   * <p>A {@link JSplitImageView} is suited for displaying two images
   * together for use in a professional workflow involving two images
   * whose contents are related to one another. For example, one side may
   * show an entire image while another side might show a section of the
   * left side's contents for analyzing at a greater detail</p>
   */
  private static final class JSplitImageView extends JPanel {
    private final JLabel isVisibleLabel;
    private final JLabel leftImageView;
    private final JLabel rightImageView;

    /**
     * Construct a new split image that defaults to
     * having two blank white images in place of images that
     * will eventually go in those locations.
     */
    public JSplitImageView() {
      super();

      JSplitPane imageSplitView = new JSplitPane();
      this.isVisibleLabel = new JLabel("", JLabel.CENTER);
      this.leftImageView = new JLabel();
      this.rightImageView = new JLabel();

      JPanel leftWithTitle = new JPanel();
      leftWithTitle.setLayout(new BorderLayout());
      leftWithTitle.add(isVisibleLabel, BorderLayout.NORTH);

      JScrollPane leftScrollArea = new JScrollPane(leftImageView);
      leftWithTitle.add(leftScrollArea, BorderLayout.CENTER);

      JScrollPane rightScrollArea = new JScrollPane(rightImageView);
      imageSplitView.setLeftComponent(leftWithTitle);
      imageSplitView.setRightComponent(rightScrollArea);
      imageSplitView.setOneTouchExpandable(true);
      imageSplitView.setDividerLocation(250);

      setLayout(new BorderLayout(10, 10));
      add(imageSplitView, BorderLayout.CENTER);
    }

    /**
     * Sets the given image as the icon of the given label.
     *
     * @param image the image whose contents will become the
     *              label's icon
     * @param label the label whose icon needs to be set
     * @throws IllegalArgumentException if either argument is null
     */
    private void setImage(VImage image, JLabel label) throws IllegalArgumentException {
      ObjectsExtension.requireNonnull(image, label);
      BufferedImage bufferredImage = VImageUtils.extractToBuffer(image);
      Icon icon = new ImageIcon(bufferredImage);
      label.setIcon(icon);
      label.setVerticalAlignment(JLabel.CENTER);
      label.setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * Determines the text that should be displayed in the visibility text
     * label above the left image pane based on the visibility condition
     * provided.
     *
     * @param isVisible whether or not the text should be rendered for visible
     *                  images in the left panel
     * @return a string to display for the visible label
     */
    private String visibleTextForVisibility(boolean isVisible) {
      return isVisible ? Constants.LAYER_VISIBLE_STRING : Constants.LAYER_INVISIBLE_STRING;
    }

    /**
     * Set the left image to have the contents of the image provided.
     *
     * @param image the image whose contents should be shown in the left pane
     * @throws IllegalArgumentException if the image is {@code null}
     */
    public void setLeftImage(VLayer image) throws IllegalArgumentException {
      this.setImage(image, this.leftImageView);

      // Update the visible label accordingly
      this.isVisibleLabel.setText(visibleTextForVisibility(image.isVisible()));
    }

    /**
     * Set the right image to have the contents of the image provided.
     *
     * @param image the image whose contents should be shown in the left pane
     * @throws IllegalArgumentException if the image is {@code null}
     */
    public void setRightImage(VLayer image) throws IllegalArgumentException {
      this.setImage(image, this.rightImageView);
    }
  }

  /**
   * A custom view for displaying layer names in a vertical stack with
   * a title at the top of the stack.
   */
  private static final class LayersView extends JPanel {
    /**
     * Construct a new layers view with no layers currently
     * specified in the view.
     *
     * @throws IllegalArgumentException if the provided layer name view is
     *                                  null
     */
    public LayersView(JList<String> layerNamesView) throws IllegalArgumentException {
      ObjectsExtension.requireNonnull(layerNamesView);
      JLabel topLabel = new JLabel(Constants.LAYER_TITLE_STRING);

      setLayout(new BorderLayout());

      add(topLabel, BorderLayout.NORTH);
      add(layerNamesView, BorderLayout.CENTER);
    }
  }
}
