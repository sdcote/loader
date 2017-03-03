
# Coyote Loader

The Coyote Loader is a toolkit and a framework for loading components in a JRE and managing their life cycle. It supports multi-threading, but in a very simplistic manner to keep maintainability as high as possible.

This is an evolving project which provides a starting point for creating a loader in a variety of other projects. That is to say, this loader project will be used as a starting point for the loader of other projects and will therefore have rather unique adaptability requirements. For example, this loader framework must operate on embedded devices and there are currently several embedded Java projects underway to which this project will contribute. 

### Why
By separating the Loader out into a separate project it is easier to focus on just the design and test of component loading without the distraction of the system as a whole. The hope is that a very flexible component loader can be developed and applied to several projects currently being developed. This framework will be tested and developed separately and merged into other projects when they are ready for implementing a command line loader. 

Other container projects are far too complex for our needs as they try to be everything for everyone. This is a purposed built toolkit for a specific set of needs.

# Project Goals

This is a prototyping project which will be used to drive a loader for a set of IoT (Internet of Things) projects. It therefore must support traditional platforms (e.g. server installations) and the restricted resources of embedded systems. It therefore must not rely on classes or libraries which may not be available in JRE images with limited libraries. 

  * Small Footprint - Forego larger, general purpose libraries for for simple, purpose driven code. Resources spent on storing unused code are resources taken away from application data.
  * Portability - Usable on as many publicly available embedded systems platforms as possible. If it runs Java, it should be able to run this loader.
  * Simplicity over Elegance - Maintainability of the code is key to stable systems, this project uses simple concepts and plainly written code (and comments) so bugs are easier to spot and fix.
  * Security Built-In, not Bolted-On, working in the utilities industry has made it clear that security should be first on your requirements list and development plan.
  * Micro Services - No need for containers and complex frameworks to expose your components through secure ReST APIs.
  * Stay out of the developers way; no surprises.

What this project is not:

  * The best way to do `X` - Everyone's needs will be different and this is just what has been found to solve many common problems in this area. YMMV
  * Containerization - This is a JRE toolkit not a self-contained environment to run in the cloud.
  * Application Server - While it serves the same role as many App Servers, this is not intended to be the full-blown environments on the market.
  * Intended To Lock You In - This is a way to run your components your way, not ours.

# Capabilities
 * Configuration File Driven - No coding, just specify a file to direct operation.
 * Component Life Cycle Management - Creation, monitoring and cleanup of components.
 * HTTP Server - Lightweight message exchange for component communications.

## Prerequisites:

  * JDK 1.6 or later installed
  * Ability to run bash (*nix) or batch (Windows) scripts
  * Network connection to get the dependencies (there are ways around that)
  * Assumes you do not have gradle installed (if you do, you can replace gradlew with gradle)
