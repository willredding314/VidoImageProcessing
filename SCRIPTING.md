# vido - Image Processing Scripting Guide

The vido Image Processing GUI can be used through script commands
or terminal commands. The following explains each of the available commands, 
how to use them, and what restrictions apply to each command.  

### Creating a layer

When vido begins, the current project has no layers, so you'll
need to begin by creating a layer. For this command, you will need
the 'creat' command, a layer name, and a position, starting at 0.
This position must be between 0 and the number of layers in the image, 
so the first layer must be at index 0. The name of this layer cannot 
be a layer that already exists, and it cannot be the name of another
command.  

Formula: 'create' + LayerName + Position  

Example: "create base 0 create second 1 create newFirst 0"
Result: Layer newFirst at position 0, Layer base at position 1, 
Layer second at position 2  

### Loading an image

To load an image into the project, use the "load" keyword, alongside 
the file path to get to this file. For a file within the folder of this 
GUI, you can use a relative classpath. This file must have the same 
dimensions as the project, unless this file is loaded into the only 
layer of the project. This image is loaded into the current layer. 

Formula: 'load' + filePath

Example: "load res/someImage.png"
Result: Replaces the current layer with the file located within the folder
of vido, in folder res, named someImage.png, assuming someImage.png matches
the dimensions of the current project. 

### Loading a project

If you wish to load a previous project, with all of its layers, the "loadproj"
command can be used with a file path to the folder of the project. Naturally, 
this project must contain files that create a legal project, as per the results
of a "saveall" command. 

Formula: 'loadproj' + filePath

Example: "loadproj + myfiles/myProject"
Result: Loads in the project myProject from the folder myfiles into the 
current project, assuming myProject is a valid project. 


### Saving a image

If you wish to save only one layer of your project, the "save" command
can be used, followed by the name you want for your file. The name should
have an extension attached to it, based on the currently supported file 
formats (jpg, png, ppm). This will only save the layer that is currently
being worked on.

Formula: 'save' + "name.extension"

Example: "save base.png"
Result: Saves the current layer to file, with the name "base.png" in the 
png format. 

### Saving a project

To save the whole project, the "saveall" command can be used. This is followed
by the name you wish to assign to the project, and the format that you wish 
to give the project. This format must be one of jpg, png, or ppm. This will
create a folder with the name that is specified, containing a file for each 
layer of the project, each in the specified format. The folder will also 
contain a text file that the name of the project, the dimensions of the 
project, and the locations of each layer in the project. 

Formula: 'saveall' + projectName + extension

Example: "saveall project jpg"
Result: Creates a folder named "project" with each item saved as a jpg file, 
alongside a text file showing where each layer is. 

### Setting a layer to work on

Many commands will only be applied to the layer that is being worked on.
In order to access all layers of an image, you may need to change which 
layer is the focus. This can be done through the 'workon' command, which
is followed by the name of a layer in the project. As always, the layer name
provided must exist within the project.  

Formula: 'workon' + LayerName  

Example: "workon second"
Result: In a project with a layer named "second", the current layer
will be set to the layer named "second".

### Removing a layer

To remove a created layer, you will use the 'remove' command, which 
is followed by a layer name. This can be done regardless of the current
focussed layer, removing whatever layer is assigned the layer name that is
given. The layer name given must exist within the project.  

Formula: 'remove' + LayerName  

Example: "remove base"  
Result: In a project that has a layer 'base', the base layer will be removed.  

### Applying an operation  

Applying an operation is done to the current layer, meaning that you must 
use the 'workon' command before applying an operation if the intended layer
is not already in focus. To use the command, use the keyword 'apply' followed
by any of the available commands. These include 'sepia' for a sepia color 
transformation, 'grayscale' for a grayscale color transformation, 'blur' for 
a blur filter, and 'sharpen' for a sharpen filter. Using any of these will 
replace the current layer with the modified layer image. This operation cannot
modify the size of the image, unless the focused layer is the only layer in the
project. 

Formula: 'apply' + OperationName  

Example: "apply sepia"  
Result: Replaces the current layer with a layer of the same image, with the 
sepia color transformation applied to it.  

### Copying a layer

To copy a layer within the project, and place it somewhere else in the project, 
the 'copy' command can be used. 'Copy' is followed by three other inputs. First 
is the name of the layer being copied, which must exist in the project. Second
is the name of the new layer that will be created, which cannot be a keyword or 
the name of another layer in the project. Third is a position, which must be 
between 0 and the number of layers in the project. 

Formula: 'copy' + OldLayerName + NewLayerName + Position  

Example: "copy base baseCopy 1"  
Result: Creates layer "baseCopy" at position 1, which is identical to the layer
"base', assuming that base exists within the project.  

### Setting visibility

If you wish to set the visibility of the current layer, use the 'visibility' command, 
followed by 'on' or 'off'. Without support for viewing layered images together, 
this currently has no effect, but may be useful for a future GUI.  

Formula: 'visibility' + 'on'/'off'

Example: "visibility off"
Result: Sets the visibility of the current layer to be off.