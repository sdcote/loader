
# Coyote Loader

The Coyote Loader is a framework for loading components in a JRE and managing their lifecycle. It supports multi-threading, but in a very simplistic manner to keep maintainability as high as possible.

This is a prototyping project which provides a starting point for creating a loader in a variety of other projects. That is to say, this loader project will be used as a template for the loader of other projects and will therefore have rather unique adaptability requirements. For example, this loader framework must operate on embedded devices a there are currently several embedded Java projects underway to which this project will contribute. 

By separating the Loader out into a separate project it is easier to focus on just the design and test of component loading without the distraction of the system as a whole. The hope is that a very flexible component loader can be developed and applied to several projects currently being developed. This framework will be tested and developed separately and merged into other projects when they are read for implementing a command line loader. 

# Project Goals

This is a prototyping project which will be used to drive a loader for a set of IoT (Internet of Things) projects. It therefore must support traditional platforms (e.g. server installations) and the restricted resources of embedded systems. It therefore must not rely on classes or libraries which may not be available in JRE images with limited libraries. 

  * Small Footprint - Forego larger, general purpose libraries for for simple, purpose driven code. Resources spent on storing unused code are resources taken away from application data.
  * Portability - Usable on as many publicly available embedded systems platforms as possible. If it runs Java, it should be able to run this loader.
  * Simplicity over Elegance - Maintainability of the code is key to stable systems, this project uses simple concepts and plainly written code (and comments) so bugs are easier to spot and fix.
  * Security Built-In, not Bolted-On, working in the utilities industry has made it clear that security should be first on your requirements list and development plan.

What this project is not:

  * The best way to do `X` - Everyone's needs will be different and this is just what has been found to solve many common problems in this area. YMMV

## Prerequisites:

  * JDK 1.6 or later installed
  * Ability to run bash (*nix) or batch (Windows) scripts
  * Network connection to get the dependencies (there are ways around that)
  * Assumes you do not have gradle installed (if you do, you can replace gradlew with gradle)
