# vido - Image Processing GUI

Vido is an image processing desktop application written in Java. You load images into Vido and manipulate those images  
by applying one of the several image filters Vido exposes to the images you load into Vido.

##Running Vido

There are three ways that Vido can be run:

1. `java -jar Vido.jar -script path/to/script.txt` (macOS)  
   `java -jar Vido.jar -script \path\to\script` (Windows)  
   Passing the `-script` argument to Vido enables you to run a script file containing image processing commands  
   understood by Vido. This allows you share image construction techniques with others by simply sharing your script  
   file containing the commands to generate the image.   
            
   After the `-script` argument, you supply a path relative to the directory of the jar file to a script file containing the commands  
   you want to run. In the above example, the script at `./path/to/script.txt` on macOS will be parsed and run by Vido. Output of what  
   Vido is doing as it reads each command, as well as for any errors while reading the script or its commands, will be printed to Standard Output.  
   After processing the script, Vido terminates.
   
   
2. `java -jar Vido.jar -text`  
   Use this mode to interact with Vido using a terminal. In this mode, Vido reads input typed into the console and parses that input as commands  
   Vido can understand. This is effectively "manual script mode". You type out commands as you would write a script, but the state of the image  
   you are working on can change dynamically as you supply more commands to Vido.
   

3. `java -jar Vido.jar -interactive`  
   Launch Vido with this command line argument to activate Vido's GUI interface. The GUI interface is the visual version of the text-interface provided  
   to a user of Vido. The GUI exposes the images the user is currently working on and displays menu items for manipulating each layer of the image.  
   See `Working with the GUI` for more details on how to use the GUI


### Working with the GUI

When the GUI for Vido opens on launch, the user is greeted with an empty canvas. On the right-hand side is a list of the names of layers in the image you are working  
on at any given moment.

To begin working with images, you must first create a new layer. All layer operations and manipulations are contained within the edit menu. Go to  
`Edit -> Layer -> Create -> New Layer` to create a new layer for your project. A popup will display prompting you to give a name for the layer. The name must be  
a nonempty string. You can create more layers using the same menu button or you can clone layers by going to `Edit -> Layer -> Create -> New Layer From Existing`.  
The layer that is cloned is the layer that is currently selected/shown on the left-hand side. Note that this will only work if a layer already exists to be copied.  
By default, a new layer has a size of 100 x 100 if there are no layers in the project, or the layer takes on the size of the other layers in the project  
(since each layer must have the same size as every other layer in the project). If you create a new layer without cloning another one, it is added directly after  
the layer that is currently selected.

Once you have created at least one layer for your current project, you can load images into the layers from the `File` menu. Go to `File -> Load -> Load Image` to bring   
up a file picker to select an image on your computer. The image must have a path relative to that of the jar you launched the program with; otherwise you will receive an  
error message telling you that the image failed to load. As long as the image has the same dimensions as the images in the layers already in the project, the image will then be     
displayed on the left-hand side of the center split panel. This panel displays the current layer you are working on. The panel on the right-hand side shows the top-most visible  
layer. If all layers in the project are invisible, the right-hand panel will be blank. To toggle the visibility of layers, go to `Edit -> Visibile`. If there are no layers in the project,  
the toggle button will only change the visibility of incoming layers (new layers added). Note that the current layer will always be visible regardless of the visibility of the layer  
you are working on since visibility only affects what is rendered as the top-most visible layer.

Alternatively, you can load a multi-layered image project that you worked on previously using the `File -> Load -> Load Project`. This will populate the Canvas  
with the contents of the previous project. Note that the project must be contained in the directory it was originally saved within in order  
that the project be loaded properly.

To apply a layer operations to images in each layer, go to the `Edit -> Apply` menu to select between applying a sepia, grayscale, blur, sharpen, and mosaic filter  
to the image in the layer you have currently selected. 

Finally, the GUI supports running a script file to create layers and load images, which will then be displayed in the GUI according to the behavior of the script. Go to  
`File -> Load -> Load From Script` and select a text file to run a script from the GUI. Again, the file must be specified with respect to the root

When you're done editing the images you've loaded into the canvas, you can save your work. Go to `File -> Save` to select an option. You have the option to save the  
currently-selected layer, the top-most visible layer, or to save all of the layers in the canvas as a project for later reloading. You'll be prompted to choose a location to  
save you're work.

All features required for the assignment are complete, with the additional functionality for mosaic filters.

### Mosaic Images

For adding mosaic images, we created a new VContentOperation titled "VMosaicFilter", and 
made that compatible with the GUI by having it implement our VContentOperation interface. 
As a result, the GUI sees no difference when interpreting a mosaic filter compared to another filter, 
outside of the need to prompt the user for a seed count. No modifications were necessary for this
extra credit, only extensions of existing code. 

### Image Citation
All images used were created by us, and we authorize the use of these images for this project.

## Archive: Assignment 5 & 6

This is the section of the README.md is what was submitted for Assignment 5 & 6. It is left for the grader  
in the event the grader wants a broader overview of the program.


Vido is an image processing desktop application written in Java. Vido currently does not have any GUI user interaction but is currently  
used via a terminal.

Vido currently supports loading image files from disk (including those of the PPM, JPG, and PNG file formats) and applying one of   
several filters to the image to produce a new one, including

1. a sepia tone filter, to give an image the appearance of having been taken in the early 1900s
2. a grayscale filter, to give an image the appearance of having been taken in the mid-1900s
3. a blur filter, to make an image appear softer/fuzzier
4. a sharpening filter, to exaggerate edges in an image

The remainder of the document outlines the structure of Vido's data model and interaction code. The README also contains information  
about any design changes made to Vido during the course of writing Assignment 6 and highlights what it left to come.

## Design Overview

To better understand Vido's data model, we break down the analysis into four parts, each containing their own subsections: image structure,  
image creation, image processing, and image persistence. Each subsection describes a general theme of the purpose of classes which comprise  
the data model within that subsection. This theme is further reflected in the subpackage structure within the *model* package of the project.

You'll see different colored sections below to indicate sections where new additions to the README.md occurred.  
Please not that this <span style="color:red"> does not imply that changes were required </span> to already existing interfaces.  
In fact, no such changes were needed. It is just a guide in case you get tired reading through tons of code all day :)

### Image Structure

#### Pixel Structure

Pixel data is effectively formatted color data. In Vido, pixels are represented with the `VPixel` interface which requires that all pixels be   
able to convert their underlying information into red, green, and blue color components as integers. Each method declared in the interface   
corresponds to one of the three RGB `ChannelType`  enum cases, each of which identifies a single RGB component type. Such channel information  
is later used by the image processing kernels to signify which color channel the given kernel applies to.

The actual values of any pixel are interpreted in a certain color context. Some clients may wish to specify their red, green, and blue components  
with respect to an arbitrary scaling. Enforcing that the components are valid is left up to clients based on their own interpretation of pixel data;  
for example, a `PPMImageSaver` ensures it can only save pixel color data in the range 0-255.

The `VRGBPixel` class is a pixel that can be directly constructed with RGB color values. It provides convenience constructors for copying color  
information or data from other pixels and converting them directly to RGB storage.

The flexibility of `VPixel` is such that other data color formats can automatically be added to Vido simply by creating a new implementor   
of the `VPixel` interface. For example, if Vido needed to add support for CMYK-colored pixels, one could create a `VCMYKPixel` class   
that internally translates CMYK colors to RGB values clients expect.


#### Image Representation

There are two types of images in Vido: multi-layered images and "sheet" images. The concepts of an image are captured by four interrelated interfaces  
discussed in sequence below:

##### Sheet Images <span style="color:orange">New for Assignment 6</span>

Sheet images are effectively fixed 2D grids of pixel data. You can think of a sheet image like a physical photograph printed out: it merely contains  
colors at every point on the film paper the picture is printed on. Vido defines two kinds of sheet images: mutable and immutable. Immutable images are  
represented with the `VImage` interface. You can query immutable images for their width and height and read pixel data at any point within the image.   
`VPixel`s don't themselves know their location within an image; instead, images manage pixel data structure. You access a particular pixel in an image   
using a `VPixelCoordinate`, which wraps a zero-based row index and column index used by a particular `VImage` to locate the appropriate pixel data.

Mutable images are represented with the `VMutableImage` interface which extends the `VImage` interface. The `VMutableImage` interface   
defines a method for writing pixel data at any location in an image. Thus, mutable images can both be read from and written to.

Both the `VImage` and `VMutableImage` interfaces have methods to create mutable and immutable copies of themselves respectively. This preserves the  
distinction between mutable and immutable images and allows image-modifying code to work with immutable images without needing to change the original  
copy's underlying pixel data.

The `VImageImpl` class is a readable, immutable image that implements the `VImage` interface. Its mutable counterpart, `VMutableImageImpl`, provides the  
same functionality with the additional functionality required by the `VMutableImage` interface. Both implementations share the same nested array storage   
for their pixel data, and thus they both derive from the abstract class `AbstractVImage`. Both classes were designed to internally behave as you would   
imagine image data, i.e. as 2D data structures.

##### Multi-layered Images <span style="color:orange">New in Assignment 6</span>

Multi-layered images are images that are composed of several sheet images. Each sheet image that composes the larger multi-layered image is called a *layer*.  
Layers in a multi-layered image are represented by the `VLayer` interface. A layer *is* itself an image with additional properties such as a name and a visibility  
status. Hence, the `VLayer` interface extends the `VImage` interface. You bind together individual layers to form a multi-layered image by creating a `VLayeredImage`  
instance like a `VLayeredImageImpl`. Note that this interface is not a subinterface of `VImage` since the concepts surrounding the manipulation of individual layers is  
different enough to merit a new interface. 

A `VLayeredImage` is a mutable type. You can add and remove layers to and from the multi-layered image, copy layers within the image, and perform familiar image operations  
such as querying for the width and height of the layered image. Layered images enforce that each layer have the same dimensions unless the multi-layered image contains 1 or  
less layers (in which case the layered image can change sizes).

A `VLayeredImage` is not suitable for keeping track of layers in a current working canvas on its own. Instead, you use a `VFocusableLayeredImage`, a subinterface of the   
`VLayeredImage` interface, that provides functionality for keeping track of a single layer in the image called the *focus layer*. Users of Vido perform operations on a single layer  
at a time by specifying which focus layer they are interested in manipulated. However, the design is such that including operations which act upon multiple layers within the image  
(for example, a blending operation) can easily be included. Such operations effectively map sub-images within a multi-layered image to another multi-layered image or into a single  
`VLayer`, the result of which can be added to a working canvas. 

<span style="color:green">For the grader</span>  
Multi-layered images were a quite natural extension of the design we already had from assignment 5. We merely needed to describe a notion of multiple images coming together to form  
a single "unit" of work that users could work on and built off our existing design.

### Image Creation <span style="color:orange">Updated for Assignment 6</span>

#### Loading Sheet Images

Vido currently supports loading images stored as PPM P3 files, PNG files, JPEG/JPG files, GIF, and other system-supported file types on disk into memory as `VImage` instances.  
Loading images from disk is accomplished with the  `VImageProvider` interface. The interface defines a single method, `extractImage`. When clients need to load an image from a source  
external to the program, they first create a suitable instance of type `VImageProvider` that can handle loading image data from the desired source. For example, you use the `PPMImageProvider`   
to load P3 PPM images from disk into memory as `VImage` instances. The image provider can easily be extended to support loading images from other sources, e.g. from other image formats or   
from command line input, by simply creating another `VImageProvider` specific to the disk format holding image data. Use the `VImageManagers` factory class methods to create appropriate image  
loaders which can read data on disk.

Vido also supports creating images programmatically. Currently, only checkerboard images can be created and customized. To create images programatically, you create a pre-built `VImageProvider`   
using one of the static methods of the `VPatternImageCreator` factory class. Note that the class produces an image *provider* and not an image itself. This allows clients to produce as many images  
as they desire with only one call to the factory method. For example, you can create a heckerboard image provider with the `VPatternImageCreator.checkboardImageProvider` method. In the future,   
if we needed more pre-defined programmatic images, it would suffice to simply create a new factory method in the class.

Image creation may fail under certain circumstances. To specify when there is an issue when creating an image, an `ImageExtractionException` is thrown. The name of the exception more clearly   
expresses the underlying issue.

#### Loading Multi-Layered Images

To load past projects involving multi-layered images, you create an instance of the `VLayeredImageProvider` interface. Vido saves multi-layered images in their own dedicated directories. Within the  
directory are the images that make up the layers of the multi-layered image as well as special file names **"layers.txt"**. The text file describes the relations the images have to the mutli-layered  
image that is subsequently loaded from disk. When a `VLayeredImageProvider` instance loads an image on disk in this format, such as an instance of the `VLayeredImageDiskProvider` class, it decodes  
the contents of the text file to reconstruct the multi-layered image from the image files in the same directory.

### Image Processing

Once an image is loaded into memory as a  `VImage`, Vido can process the image. All transformations   
of an image are described by the `VImageContentOperation` interface, which allows operations to (fittingly) operate on  
images. Operations work inherently with immutable images. This was intentional: clients have the assurance that their image data will  
not be modified by filters which are applied to images they provide. Instead, operations on images typically work with mutable copies  
of the images they are provided and write pixel data to the copies. Interface polymorphism ensures that any `VMutableImage`  
they return to callers are masked by the `VImage` interface, hiding their mutability. 

If you wish to perform multiple operations on an image consecutively, there is a  `VImageCompositeOperation`   
class. The transformation merely applies the operations it receives in sequence.

There are currently two types of content operations that directly modify images: filters and color tranformations.  
Filters are described by the `VImageFilter` class. They modify each pixel in an image based on a custom per-pixel   
transformation (known as a kernel, which is a small matrix). A filter operates on a single `ChannelType` at a time. The filter  
traverses the entire image it operates on and assigns a new color channel value to each pixel in the image by linearly weighing the   
channel values of a pixel's neighbors according to the kernel and adding together the values. The resulting value then becomes  
the new channel value for the pixel under consideration. Since the filter only accepts kernels of odd dimensions, it is guaranteed that  
a single pixel can always be centered by the kernel. 

The actual evaluation of a kernel is carried out by the filter's `VKernelResolution`. The `VKernelResolution` interface represents  
how a kernel behaves at every location in the image. Therefore, it also defines the edge behavior of the kernel. Pixels on or near the edge  
of an image are different in that the overlayed kernel may not be fully contained by the image. The decision of what to do with out-of-bounds  
pixel accesses by a filter's kernel is what is captured by the  `VKernelResolution` interface.

There is currently only support for ignoring pixels in a kernel attempting to access pixels outside of the image in the `KernelResolutionExclude`   
class, which will count a pixel beyond the boundaries of an image as having a value of 0 within the specified channel. It is easy to  
create more edge modes by simply writing another class describing the behavior of a kernel at the edge of an image.

However, to generate these resolutions coveniently, there is the `KernelEdgeMode` enumeration that denotes different types of edge case   
resolutions supported in the application at any given time. To create the appropriate `VKernelResolution` object for any given `KernelEdgeMode`,  
you use the  `VKernelResolutionFactory` that generates the appropriate `VKernelResolution` object at run-time.

The other currently supported operations are color transformations, described by the  `VImageColorTransformation` class. It is also  
a `VImageContentOperation` and has support for operating on arbitrary images. However, color transformations only map pixel colors   
to new colors, and hence are effectively functions from colors to colors applied over an entire image. Linear color transformation  
functions are created with the `VImageTransformConversion` class. It is effectively a 3 by 3 matrix of doubles which   
can be applied to a pixel to generate new values for each pixel in the channel. 

Finally,  you can create pre-built filters and color transformations using the  `VImageFilters` factory class, which has   
static methods for creating sepia and grayscale color transformations, as well as blur and sharpen filters.  

Our design is easily extended. Supporting a new operation means simply implementing the  `VImageContentOperation` with new  
functionality. For example, non-linear color transformations can easily be implemented using a custom color function or  
different kinds of image operations using distant pixels can easily be added to by simply adding the required functionality to  `VImageContentOperation`.


### Image Persistence <span style="color:orange">Updated for Assignment 6</span>

#### Saving Sheet Images

After users have applied operations on their images in a desired manner, Vido can save those images to disk. Saving images  
is the purpose of the `VImageSaver` interface. Classes which implement the interface provide functionality to save images to  
disk. Currently, a  `VImage` can be saved to disk in the PPM P3 format through the `PPMImageSaver` class. Like image loading,  
you can use the `VImageManagers` static factory methods to retrieve an instance of an appropriate `VImageSaver` instance.

#### Saving Multi-layered Images

Vido can save a working canvas consisting of a multi-layered image to disk as well. You use the `VLayeredImageSaver` interface to saver multi-layered images for later retrieval by  
a `VLayeredImageLoader`. To save multi-layered images to disk, use a `VLayeredImageDiskSaver` instance. The class creates a new directory for the multi-layered image and saves the  
individual images into that directory along with the special "layers.txt" file that maps the images to the multi-layered image that is currently in memory.


# Summary

All commands shown in the USEME.md and script files are fully supported. One additional command is also supported; but since the layers.txt file is based on absolute file paths, an example  
could not be created with an automated script. See the `loadproj` command description in the USEME.md for a complete description of the command. The command allows you to  
reload a multi-layered image project back into Vido for further manipulation.

Closing remarks: All tests have been completed on the macOS operating system. Locating files on Windows is different than doing so on macOS. Theoretically since we are using  
the `Path` interface throughout our codebase, Vido should work on Windows. If you are experiencing any difficulties, let us know and we can do a demo if need be,