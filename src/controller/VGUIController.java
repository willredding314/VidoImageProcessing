package controller;



import model.creation.ImageExtractionException;
import model.creation.VImageManagers;
import model.creation.VImageProvider;
import model.creation.VLayeredImageProvider;
import model.creation.VLayeredImageDiskProvider;

import model.image.VFocusableLayeredImage;
import model.image.VImage;
import model.image.VLayer;
import model.image.VLayeredImage;
import model.image.VLayerImpl;
import model.misc.Constants;
import model.misc.ObjectsExtension;
import model.persistence.ImageSavingException;
import model.persistence.VImageSaver;
import model.persistence.VLayeredImageDiskSaver;
import model.persistence.VLayeredImageSaver;
import model.processing.VImageContentOperation;
import model.processing.VMosaicFilter;
import view.VIView;
import view.VIViewListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.NoSuchElementException;

/**
 * A controller for the GUI variant of a Vido Controller.
 */
public class VGUIController implements VController, VIViewListener {

  private static final VLayer baseLayer = new VLayerImpl(100, 100);

  private VFocusableLayeredImage model;
  private VIView view;

  /**
   * Constructs a VGUIController with a view and a model.
   * @param view the graphic view for Vido
   * @param model the layered image of vido
   * @throws IllegalArgumentException if either argument is null
   */
  public VGUIController(VIView view, VFocusableLayeredImage model) throws
          IllegalArgumentException {

    ObjectsExtension.requireNonnull(view, model);
    this.model = model;
    this.view = view;
  }

  @Override
  public void run() {

    view.addListener(this);
    view.renderAsCurrentLayer(baseLayer);
    view.renderAsTopMostVisibleLayer(baseLayer);
    view.clearLayerNames();
    view.display();
  }

  /**
   * Gets the top most visible layer in the image.
   * @return the top most visible layer in the image.
   */
  private VLayer getTopMostVisible() {
    for (int i = 0; i < model.numLayers(); i += 1) {
      if (model.getLayer(i).isVisible()) {
        return model.getLayer(i);
      }
    }

    int thisWidth;
    int thisHeight;

    if (model.getWidth() < 0 || model.getHeight() < 0) {
      thisWidth = 100;
      thisHeight = 100;
    } else {
      thisWidth = model.getWidth();
      thisHeight = model.getHeight();
    }
    return new VLayerImpl(new VLayerImpl(thisWidth, thisHeight));
  }

  /**
   * Gets the index of the topmost visible layer in an image.
   * @return the index of the topmost visible layer
   */
  private int getTopMostVisibleIndex() {
    for (int i = 0; i < model.numLayers(); i += 1) {
      if (model.getLayer(i).isVisible()) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Gets the names of the layers in the image in an array.
   * @return the names of the layers as an array of strings
   */
  private String[] getNames() {

    String[] names = new String[model.numLayers()];

    for (int i = 0; i < model.numLayers(); i += 1) {
      names[i] = model.getLayer(i).getName();
    }

    return names;
  }
  
  @Override
  public void saveProject() {

    final JFileChooser fchooser = new JFileChooser(".");
    boolean found = false;
    File f;

    int retvalue = fchooser.showSaveDialog(null);

    if (retvalue == JFileChooser.CANCEL_OPTION) {
      return;
    }

    if (retvalue == JFileChooser.APPROVE_OPTION) {
      f = fchooser.getSelectedFile();

      JFrame window = new JFrame();
      String name = JOptionPane.showInputDialog(window,
              "Enter the format for the files (jpg, png, ppm)");

      if (!name.equals("jpg") && !name.equals("png") && !name.equals("ppm")) {


        JOptionPane.showMessageDialog(null,
                "File must be a jpg, png, or ppm",
                "Saving Error",
                JOptionPane.ERROR_MESSAGE);

      } else {

        VLayeredImageSaver saver = new VLayeredImageDiskSaver(
                name, Path.of(f.toString()).toAbsolutePath());

        try {
          saver.saveImage(model);
        } catch (ImageSavingException e) {

          JOptionPane.showMessageDialog(null,
                  "Image failed to save.",
                  "Saving Error",
                  JOptionPane.ERROR_MESSAGE);

        }
      }
    } else {

      JOptionPane.showMessageDialog(null,
              "File path is invalid",
              "Saving Error",
              JOptionPane.ERROR_MESSAGE);

    }
  }

  @Override
  public void saveLayer() {

    if (model.numLayers() == 0) {
      JOptionPane.showMessageDialog(null,
              "No layers to save.",
              "Saving Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    final JFileChooser fchooser = new JFileChooser(".");
    boolean found = false;
    File f;

    int retvalue = fchooser.showSaveDialog(null);

    if (retvalue == JFileChooser.CANCEL_OPTION) {
      return;
    }

    if (retvalue == JFileChooser.APPROVE_OPTION) {
      f = fchooser.getSelectedFile();
      VImageSaver saver = VImageManagers.diskSaverFor(
                      Path.of(f.toString()).toAbsolutePath());

      try {
        VLayer layer = model.getFocusLayer().orElseThrow();
        saver.saveImage(layer);
      } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(null,
                "Layer could not be found.",
                "Saving Error",
                JOptionPane.ERROR_MESSAGE);
      }
      catch (ImageSavingException e) {

        JOptionPane.showMessageDialog(null,
                "Image failed to save.",
                "Saving Error",
                JOptionPane.ERROR_MESSAGE);
      }
      catch (NoSuchElementException e) {

        JOptionPane.showMessageDialog(null,
                "Could not find layer.",
                "Saving Error",
                JOptionPane.ERROR_MESSAGE);
      }

    } else {

      JOptionPane.showMessageDialog(null,
              "Image failed to save.",
              "Saving Error",
              JOptionPane.ERROR_MESSAGE);
    }

  }

  @Override
  public void saveTopMostVisibleLayer() {

    if (model.numLayers() == 0) {
      JOptionPane.showMessageDialog(null,
              "No layers to save.",
              "Saving Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    final JFileChooser fchooser = new JFileChooser(".");
    boolean found = false;
    File f;

    int retvalue = fchooser.showSaveDialog(null);

    if (retvalue == JFileChooser.CANCEL_OPTION) {
      return;
    }

    if (retvalue == JFileChooser.APPROVE_OPTION) {
      f = fchooser.getSelectedFile();
      VImageSaver saver = VImageManagers.diskSaverFor(
              Path.of(f.toString()).toAbsolutePath());

      try {
        VLayer layer = this.getTopMostVisible();
        saver.saveImage(layer);
      } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(null,
                "Layer could not be found.",
                "Saving Error",
                JOptionPane.ERROR_MESSAGE);
      }
      catch (ImageSavingException e) {

        JOptionPane.showMessageDialog(null,
                "Image failed to save.",
                "Saving Error",
                JOptionPane.ERROR_MESSAGE);
      }

    } else {

      JOptionPane.showMessageDialog(null,
              "Image failed to save.",
              "Saving Error",
              JOptionPane.ERROR_MESSAGE);
    }

  }

  @Override
  public void addLayer() {
    JFrame window = new JFrame();
    String name = JOptionPane.showInputDialog(window,
            "Enter the name of the new layer:");

    if (name == null) {
      return;
    }

    if (name.equals("")) {
      JOptionPane.showMessageDialog(
              window,"Name cannot be empty.",
              "Failed Name Input",JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (model.getHeight() == -1 || model.getWidth() == -1) {
      model.createNewLayer(name, model.getFocusLayerIndex() + 1,
              new VLayerImpl(100, 100));
    } else {
      model.createNewLayer(name, model.getFocusLayerIndex() + 1,
              new VLayerImpl(model.getWidth(), model.getHeight()));
    }
    view.renderLayerNames(this.getNames(), model.getFocusLayerIndex());
    view.renderAsCurrentLayer(model.getFocusLayer().orElseThrow());
    view.renderAsTopMostVisibleLayer(this.getTopMostVisible());
  }

  @Override
  public void loadImage() {

    if (model.numLayers() == 0) {
      JOptionPane.showMessageDialog(null,
              "No layers to load into.",
              "Loading Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    final JFileChooser fchooser = new JFileChooser(".");
    boolean found = false;
    File f;

    int retvalue = fchooser.showOpenDialog(null);

    if (retvalue == JFileChooser.CANCEL_OPTION) {
      return;
    }

    if (retvalue == JFileChooser.APPROVE_OPTION) {
      f = fchooser.getSelectedFile();

      VImageProvider provider =
              VImageManagers.diskProviderFor(
                      Path.of(f.toString()).toAbsolutePath());

      try {
        VImage loaded = provider.extractImage();
        int workingLayer = model.getFocusLayerIndex();
        model.replace(workingLayer, loaded);
        view.renderAsCurrentLayer(model.getFocusLayer().orElseThrow());
        view.renderAsTopMostVisibleLayer(this.getTopMostVisible());
      }
      catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(null,
                "Image failed to load.",
                "Loading Error",
                JOptionPane.ERROR_MESSAGE);
      }
      catch (ImageExtractionException e) {
        JOptionPane.showMessageDialog(null,
                "Image failed to load.",
                "Loading Error",
                JOptionPane.ERROR_MESSAGE);
      }
      catch (NoSuchElementException e) {
        JOptionPane.showMessageDialog(null,
                "Layer was  not found.",
                "Loading Error",
                JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(null,
              "Image failed to load.",
              "Loading Error",
              JOptionPane.ERROR_MESSAGE);
    }

  }

  @Override
  public void loadProject() {

    final JFileChooser fchooser = new JFileChooser(".");
    fchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    File f;

    int retvalue = fchooser.showOpenDialog(null);

    if (retvalue == JFileChooser.CANCEL_OPTION) {
      return;
    }

    if (retvalue == JFileChooser.APPROVE_OPTION) {
      f = fchooser.getSelectedFile();


      VLayeredImageProvider loader =
              new VLayeredImageDiskProvider(
                      Path.of(f.toString()));

      try {
        VLayeredImage loaded = loader.extractLayeredImage();

        for (int i = 0; i < loaded.numLayers(); i += 1) {
          VLayer layer = loaded.getLayer(i);
          model.createNewLayer(layer.getName(), i, layer);
        }

        model.setFocusLayer(0);
        model.setName(loaded.getName());

        view.renderAsCurrentLayer(model.getFocusLayer().orElseThrow());
        view.renderAsTopMostVisibleLayer(this.getTopMostVisible());
        view.renderLayerNames(this.getNames(), model.getFocusLayerIndex());
      }
      catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(null,
                "At least one file in the project is incompatible with "
                        + "the current project.",
                "Loading Error",
                JOptionPane.ERROR_MESSAGE);
      }
      catch (ImageExtractionException e) {
        JOptionPane.showMessageDialog(null,
                "Project failed to load.",
                "Loading Error",
                JOptionPane.ERROR_MESSAGE);
      }
      catch (NoSuchElementException e) {
        JOptionPane.showMessageDialog(null,
                "Layer could not be found.",
                "Loading Error",
                JOptionPane.ERROR_MESSAGE);
      }
    } else {

      JOptionPane.showMessageDialog(null,
              "Project failed to load.",
              "Loading Error",
              JOptionPane.ERROR_MESSAGE);
    }
  }

  @Override
  public void addFromCopy() {

    if (model.numLayers() == 0) {
      JOptionPane.showMessageDialog(null,
              "No layers to copy from.",
              "Layer Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    JFrame window = new JFrame();
    String name = JOptionPane.showInputDialog(window,
            "Enter the name of the new layer:");

    if (name.equals("")) {
      JOptionPane.showMessageDialog(
              window,"Name cannot be empty.",
              "Failed Name Input",JOptionPane.WARNING_MESSAGE);
      return;
    }

    String positionString = JOptionPane.showInputDialog(window,
            "Enter the position of the new layer:");

    if (name == null || positionString == null) {
      return;
    }

    try {
      int position = Integer.parseInt(positionString) - 1;
      model.createNewLayer(name, position,
              model.getFocusLayer().orElseThrow());
      view.renderLayerNames(this.getNames(), model.getFocusLayerIndex());
      view.renderAsTopMostVisibleLayer(this.getTopMostVisible());
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(
              window,"Input must be an integer.",
              "Failed Position Input",JOptionPane.WARNING_MESSAGE);
    } catch (IllegalArgumentException e) {
      JOptionPane.showMessageDialog(
              window,"Input must be greater than 0, and no more than one greater than "
                      + "the number of layers in the project",
              "Failed Position Input",JOptionPane.WARNING_MESSAGE);
    } catch (NoSuchElementException e) {
      JOptionPane.showMessageDialog(
              window,"No layers available to copy.",
              "Failed Position Input",JOptionPane.WARNING_MESSAGE);
    }

  }

  @Override
  public void removeLayer() {

    JFrame window = new JFrame();

    if (model.numLayers() == 0) {
      JOptionPane.showMessageDialog(
              window,"Cannot remove a layer from an empty project.",
              "Failed Removal",JOptionPane.WARNING_MESSAGE);
      return;
    }

    int goodToGo = JOptionPane.showConfirmDialog(null,
            "Are you sure you would like to remove the current layer?");

    if (goodToGo == 0) {
      try {
        model.removeLayer(model.getFocusLayerIndex());
      } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(
                window,"Failed to find selected layer.",
                "Removal error",JOptionPane.WARNING_MESSAGE);
      }

      if (model.numLayers() == 0) {

        view.renderAsCurrentLayer(baseLayer);
        view.clearLayerNames();
        view.renderAsTopMostVisibleLayer(baseLayer);

      } else {
        view.renderAsCurrentLayer(model.getFocusLayer().orElseThrow());
        view.renderLayerNames(this.getNames(), model.getFocusLayerIndex());
        view.renderAsTopMostVisibleLayer(this.getTopMostVisible());
      }
    }
  }


  @Override
  public void setCurrentVisibleOn() {
    JFrame window = new JFrame();

    if (model.numLayers() == 0) {
      JOptionPane.showMessageDialog(null,
              "No layers available.",
              "Edit Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    try {
      model.setVisible(true, model.getFocusLayerIndex());
      view.renderAsCurrentLayer(model.getFocusLayer().orElseThrow());
      view.renderAsTopMostVisibleLayer(this.getTopMostVisible());
    } catch (IllegalArgumentException e) {
      JOptionPane.showMessageDialog(
              window, "No focus layer available.",
              "Removal error", JOptionPane.WARNING_MESSAGE);
    } catch (NoSuchElementException e) {
      JOptionPane.showMessageDialog(
              window, "No focus layer available.",
              "Removal error", JOptionPane.WARNING_MESSAGE);
    }
  }

  @Override
  public void setCurrentVisibleOff() {

    JFrame window = new JFrame();

    if (model.numLayers() == 0) {
      JOptionPane.showMessageDialog(null,
              "No layers available.",
              "Edit Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      model.setVisible(false, model.getFocusLayerIndex());
      view.renderAsCurrentLayer(model.getFocusLayer().orElseThrow());
      view.renderAsTopMostVisibleLayer(this.getTopMostVisible());
    } catch (IllegalArgumentException e) {
      JOptionPane.showMessageDialog(
              window,"No focus layer available.",
              "Editing error",JOptionPane.WARNING_MESSAGE);
    } catch (NoSuchElementException e) {
      JOptionPane.showMessageDialog(
              window,"No focus layer available.",
              "Editing error",JOptionPane.WARNING_MESSAGE);
    }
  }

  @Override
  public void changeName() {

    if (model.numLayers() == 0) {
      JOptionPane.showMessageDialog(null,
              "No layers available to rename.",
              "Edit Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    JFrame window = new JFrame();
    String name = JOptionPane.showInputDialog(window,
            "Enter the new name of this layer:");

    if (name == null) {
      return;
    }

    try {
      model.rename(name, model.getFocusLayerIndex());
      view.renderAsCurrentLayer(model.getFocusLayer().orElseThrow());
    } catch (IllegalArgumentException e) {
      JOptionPane.showMessageDialog(
              window,"No focus layer available.",
              "Editing error",JOptionPane.WARNING_MESSAGE);
    } catch (NoSuchElementException e) {
      JOptionPane.showMessageDialog(
              window,"No focus layer available.",
              "Editing error",JOptionPane.WARNING_MESSAGE);
    }
  }

  @Override
  public void layerWithIndexSelected(int index) {

    JFrame window = new JFrame();

    if (model.numLayers() == 0) {
      JOptionPane.showMessageDialog(null,
              "No layers available to select.",
              "Selection Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    try {
      model.setFocusLayer(index);
      view.renderAsCurrentLayer(model.getFocusLayer().orElseThrow());
      view.renderLayerNames(this.getNames(), model.getFocusLayerIndex());
    } catch (IllegalArgumentException e) {
      JOptionPane.showMessageDialog(
              window,"No focus layer available.",
              "Editing error",JOptionPane.WARNING_MESSAGE);
    } catch (NoSuchElementException e) {
      JOptionPane.showMessageDialog(
              window,"No focus layer available.",
              "Editing error",JOptionPane.WARNING_MESSAGE);
    }
  }

  @Override
  public void applyOperation(VImageContentOperation op) {

    JFrame window = new JFrame();

    if (model.numLayers() == 0) {
      JOptionPane.showMessageDialog(null,
              "No layers available to modify.",
              "Edit Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      model.apply(op, model.getFocusLayerIndex());

      view.renderAsCurrentLayer(model.getFocusLayer().orElseThrow());
      view.renderAsTopMostVisibleLayer(this.getTopMostVisible());
    } catch (IllegalArgumentException e) {
      JOptionPane.showMessageDialog(
              window,"Could not find layer.",
              "Editing error",JOptionPane.WARNING_MESSAGE);
    } catch (NoSuchElementException e) {
      JOptionPane.showMessageDialog(
              window,"Could not find layer.",
              "Editing error",JOptionPane.WARNING_MESSAGE);
    }
  }

  @Override
  public void applyMosaic() {

    if (model.numLayers() == 0) {
      JOptionPane.showMessageDialog(null,
              "No layers available to modify.",
              "Edit Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    JFrame window = new JFrame();
    String seed = JOptionPane.showInputDialog(window,
            "Enter the number of seed in the mosaic.");

    if (seed == null) {
      return;
    }

    try {
      int seedNum = Integer.parseInt(seed);

      model.apply(new VMosaicFilter(seedNum, Constants.MOSAIC_RANDOM_SEED),
              model.getFocusLayerIndex());

      view.renderAsCurrentLayer(model.getFocusLayer().orElseThrow());
      view.renderAsTopMostVisibleLayer(this.getTopMostVisible());
    } catch (NumberFormatException e) {

      JOptionPane.showMessageDialog(
              window,"Seeds must be a number.",
              "Failed Mosaic Input",
              JOptionPane.WARNING_MESSAGE);

    } catch (IllegalArgumentException e) {

      JOptionPane.showMessageDialog(
              window,"Input must be greater than 0.",
              "Failed Mosaic Input",
              JOptionPane.WARNING_MESSAGE);
    } catch (NoSuchElementException e) {
      JOptionPane.showMessageDialog(
              window,"Could not find focus layer.",
              "Failed Mosaic Input",
              JOptionPane.WARNING_MESSAGE);
    }
  }

  @Override
  public void executeScript() {

    final JFileChooser fchooser = new JFileChooser(".");
    boolean found = false;
    File f;

    int retvalue = fchooser.showOpenDialog(null);

    if (retvalue == JFileChooser.CANCEL_OPTION) {
      return;
    }

    if (retvalue == JFileChooser.APPROVE_OPTION) {
      f = fchooser.getSelectedFile();


      try {
        VController terminalControl = new VTerminalController(
                new FileReader(f), this.model);
        terminalControl.run();
        view.renderLayerNames(this.getNames(), model.getFocusLayerIndex());
        view.renderAsTopMostVisibleLayer(this.getTopMostVisible());
        view.renderAsCurrentLayer(model.getFocusLayer().orElseThrow());

      } catch (FileNotFoundException e) {
        JOptionPane.showMessageDialog(null,
                "Could not find script file",
                "Script error",
                JOptionPane.ERROR_MESSAGE);

      } catch (NoSuchElementException e) {
        JOptionPane.showMessageDialog(null,
                "Script contains invalid commands, some or all of the "
                        + "commands could not be executed.",
                "Script error",
                JOptionPane.ERROR_MESSAGE);
      } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(null,
                "Script contains invalid commands, some or all of the "
                        + "commands could not be executed.",
                "Script error",
                JOptionPane.ERROR_MESSAGE);
      }
    } else {

      JOptionPane.showMessageDialog(null,
              "Could not find script file",
              "Script error",
              JOptionPane.ERROR_MESSAGE);
    }

  }

}
