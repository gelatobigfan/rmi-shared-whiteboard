# RMI Shared Whiteboard

A distributed whiteboard built with Java RMI, enabling real-time collaboration between multiple users.

---

## Author

Clara Pan  
Master of Information Technology  
University of Melbourne  

---

## Overview

This project implements a distributed, real-time shared whiteboard using Java RMI.  
Multiple users can connect to the same whiteboard session and collaborate simultaneously through drawing and text-based communication.

The system follows a client–server architecture, where a central server coordinates drawing updates and synchronises state across all connected clients.

This project is presented as a personal portfolio piece demonstrating core distributed systems concepts, remote method invocation, and collaborative application design.

---

## Key Features

- Real-time shared whiteboard across multiple clients  
- Support for multiple drawing tools (line, rectangle, oval, text, eraser, etc.)  
- Live synchronisation of drawing actions between users  
- Integrated text messaging between participants  
- Role-based access control (manager and guest clients)  
- Java Swing–based graphical user interface  

---

## System Architecture

The application is built on a Java RMI–based client–server model:

- The **server** maintains the shared whiteboard state and broadcasts updates  
- **clients** communicate with the server through remote interfaces  
- Bidirectional RMI callbacks propagate drawing and chat events  
- The drawing interface follows a Model–View–Presenter (MVP) design pattern  

This structure improves modularity, scalability, and ease of extension.

---

## Tech Stack
- Language: Java
- Distributed Framework: Java RMI
- UI: Java AWT / Swing
- Architecture: Client–Server, MVP pattern
