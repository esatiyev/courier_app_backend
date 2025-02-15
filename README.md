# TERM PROJECT: Object Oriented Programming 2 (2023 Fall)
## Courier App

1. Introduction
•	The Courier App project aims to provide a mobile application and backend system for managing courier services, enabling users to place orders, track deliveries, and optimize courier operations.
2. Project Overview
•	The Courier App project is sponsored by BEU to provide a courier management solution for end-users and delivery personnel.
3. Scope
•	The Courier App will include the following key features:
o	User registration and authentication
o	Order placement and tracking
o	Real-time notifications
o	Geolocation for tracking and routing
o	Customer support interface
o	Security measures
o	Offline functionality for tracking
•	Constraints and assumptions:
o	The app will support Android platforms.
o	The backend will be hosted on Local Computer.
o	Legal compliance with data protection regulations (e.g., GDPR) is mandatory.
4. Functional Requirements
4.1 Mobile App
•	User Registration:
o	Users can create accounts with a username, password, and contact information.
o	Users can log in and reset their passwords.
•	Order Placement:
o	Users can create new courier orders by providing sender and recipient information, package details, and delivery instructions.
o	Users can select delivery options (e.g., express, standard).
•	Order Tracking:
o	Users can track the status and location of their courier orders in real-time.
•	Notifications:
o	Users receive push notifications for order status updates and delivery alerts.
•	User Support:
o	Users can contact customer support via an in-app chat or email.
•	Geolocation:
o	The app will access the device's GPS data for tracking and route optimization.
•	Security:
o	User data will be encrypted and secured.
•	Offline Functionality:
o	Users can view order history and tracking information offline.
4.2 Backend
•	Database:
o	Use a SQL database to store user profiles, orders, and payment information.
•	API Endpoints:
o	Define RESTful API endpoints for user registration, order management, and notifications.
•	Order Processing:
o	Process new orders, update order status, and manage delivery routes.
•	Authentication:
o	Implement user authentication using secure tokens.
•	Routing and Navigation:
o	Optimize courier routes and provide navigation directions to delivery personnel.
•	Scalability and Performance:
o	Ensure the system can handle increasing loads and provide acceptable response times.
•	Security:
o	Implement measures for data protection and access control.
•	Reporting and Analytics:
o	Provide reporting and analytics features for administrators.
5. Non-Functional Requirements
•	Performance:
o	Response time: Within 2 seconds for most user interactions.
o	Availability: 99.9% uptime.
•	Usability:
o	Intuitive user interface and smooth user experience.
•	Reliability:
o	Implement failover and backup systems for data redundancy.
•	Compatibility:
o	Support Android devices.
•	Legal and Compliance:
o	Ensure compliance with data protection regulations.
•	Documentation:
o	Provide user manuals and API documentation.
6. System Architecture
•	The system will use a multi-tier architecture, consisting of a mobile app interface, a RESTful API, and a SQL database hosted on BEU.
7. Test Requirements
•	Testing will include unit testing, integration testing, and user acceptance testing. Test cases will be documented and executed.
8. Timeline and Milestones
•	The project timeline includes development, testing, and deployment phases with specific milestones.
