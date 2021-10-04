# vido - Image Processing GUI User Guide

Vido is an image processing GUI that supports user input through a 
series of selection buttons, rather than supporting only script commands. 
Alternatively, the GUI can be used with interactive script commands or 
pre-set script commands, differentiated by the following terminal commands.  

Once in a terminal for the proper directory, the commands are as follows:  

java -jar Program.jar -script path-of-script-file  
    -for using script commands that are precreated  
java -jar Program.jar -text  
    -for using interactive scripting through the terminal  
java -jar Program.jar -interactive  
    -for using vido with the GUI, as intended  

The following will explain the commands and restrictions used in 
the GUI. If you intend to either scripting command form, refer to
'SCRIPTING.md' for the complete rules and restrictions of scripts. 

### Screen Format

The vido GUI is broken up into three panels.  

The right most panel is designated for the top-most visible layer, and 
will show what image is in that position. If there are no visible layers 
in the project, a default white image will be shown.  

The center panel is designated for the current layer, showing whatever image
is the focussed layer. Above the image will be the word 'Visible' or 'Invisible', 
depending on the visibility of the image.  

The left most panel is designated for the names of the layers in the project, 
with a title 'Layers' above it. Selecting any of the names under this heading 
will assign the layer selected to the focussed position. If there are no layers, 
there will be no names below the heading.  

Above these panels is a bar for controls, with the 'File' menu having commands
for saving and loading files, and the 'Edit' menu having commands for modifying
the project.  

### Creating a layer

When vido begins, the current project has no layers, so you'll
need to begin by creating a layer. For this selection, select 
the new layer button. You will be prompted for
a name, which can be any string that does not already exist as 
a layer name in the project. This layer will be placed immediately 
after the current layer. Note that the first layer of a project has a 
default size, which can only be changed if it is the only layer in
the project. As such, it will be difficult to load in files if you 
create many default layers before loading in any images. 

Selection: 'Edit'->'Layer'->'Create'->'New Layer'

### Loading an image

To load an image into the project, use the load image button. This 
will create a popup for you to select the file for the current layer. 
This file must be a jpg, png, or ppm file, and it must match the dimensions
of the project, unless it is loaded into the sole layer in the project. 

Selection: 'File'->'Load'->'Load Image'

### Loading a project

If you wish to load a previous project, with all of its layers, select
the load project button. This will create a popup that can only select
directories from file. If a file is selected that is a valid project
per the project saving feature, the project will attempt to load in. 
This would be most often done when the current project is empty, at which 
point a valid project will always load in. It is also possible to load
in a project while the current project already has items in it. This is also
possible, but only if the dimensions of the two projects are identical and 
there are no shared names between the current project and the loaded project.  

Selection: 'File'->'Load'->'Load Project'

### Saving the current image

If you wish to save the current layer of your project, use the save button. 
When selected, a save popup will appear, allowing a location and file name 
to be selected for the saved layer. This file name must have an extension at its
end, being one of ".jpg", ".png", or ".ppm", with the file being saved in the 
format specified. 

Selection: 'File'->'Save'->'Save Current Layer'  

### Saving the top-most visible image

If you wish to save the top-most visible layer of your project, use the save button.
When selected, a save popup will appear, allowing a location and file name
to be selected for the saved layer. This file name must have an extension at its
end, being one of ".jpg", ".png", or ".ppm", with the file being saved in the
format specified.

Selection: 'File'->'Save'->'Save Top-most Layer'

### Saving a project

To save the whole project, use the save project button. This will prompt you
first with a saving popup, which will require a name for the new directory as 
well as a location for the project. Assuming this location is valid, the user
will then be prompted for a file extension, which should be one of "jpg", "png",
or "ppm". All files in the project will be saved in this format at the specified
loaction.  

Selection: 'File'->'Save'->'Save as Project'  

### Setting a layer to work on

Many commands will only be applied to the layer that is being worked on.
In order to access all layers of an image, you may need to change which 
layer is the focus. This is done through the left most panel of layer names. 
Selecting a layer name will set the current layer to the selected layer, 
updating the center panel to reflect the image selected. 

### Removing a layer

To remove a created layer, you can select the remove layer button. This will
first display a popup, ensuring that the user does wish to remove the current 
layer. If the user does not cancel at this step, the current layer will be removed. 

Selection: 'Edit'->'Layer'->'Remove Current Layer'  

### Applying an operation  

Applying an operation is done to the current layer, and is done through the 
apply menu. There are 5 options for applying single layer operations, each 
denoted by their name within that menu. 

Selection for Sepia Filter: 'Edit'->'Apply'->'Apply Sepia Filter'
Selection for Grayscale Filter: 'Edit'->'Apply'->'Apply Grayscale Filter'
Selection for Blur Filter: 'Edit'->'Apply'->'Apply Blur Filter'
Selection for Sharpen Filter: 'Edit'->'Apply'->'Apply Sharpen Filter'
Selection for Mosaic Filter: 'Edit'->'Apply'->'Apply Mosaic Filter'

### Copying a layer

To copy a layer within the project, and place it somewhere else in the project, 
the user can choose the create from existing command. This will copy the current 
layer. The user will then be prompted for a name of the new layer, which cannot 
be the name of a layer already in the project. Finally, the user will be prompted
for a position of the new layer, which must be between 1 and 1 greater than the 
greatest position in the project. This value must be submitted as an integer.  

Selection: 'Edit'->'Layers'->'Create'->'New Layer from Existing'  

### Setting visibility

If you wish to toggle the visibility of the current layer, use the visibility 
button. This button will have a checkmark next to it if the current layer is
visible, and no checkmark if it is invisible. Clicking this button will change the 
visibility of the current layer, making it visible if it wasn't already, or invisible 
if it was previously visible.  

Selection: 'Edit'->'Layers'->'Visible'  

### Renaming a layer

To rename the current layer, select the rename button. The user will be prompted
for a new name for the layer, which cannot be the name of another layer in the project. 

Selection: 'Edit'->'Layers'->'Rename Current Layer'  

### Running a script
If you wish to run a pre-made script on the GUI, use the load from script buttton. 
This will create a loading popup, where the user can select a valid script file 
for vido. See the 'SCRIPTING.md' file for instructions on scripting. We also provide
a sample script that loads and modifies some pre-selected images. The script will 
run step by step, so if a later command fails, the previous commands will be executed, 
so be careful to ensure that your scripting file is correct. 

Selection: 'Load'->'Load from Script'
