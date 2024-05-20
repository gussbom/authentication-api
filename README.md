# authentication-api
A simple user Authentication API.

The Authentication API is a secure and scalable service that provides user authentication and authorization functionality for applications. It allows users to securely log in and log out of a system, while protecting sensitive data and ensuring that only authorized users can access specific resources.

# Features
* User Registration: Allows new users to sign up and create an account by providing a unique username, email, and password. 
* User Login: Securely authenticates users by verifying their credentials (username/email and password) and issuing access tokens upon successful login. 
* Access Tokens: Generates JWT (JSON Web Tokens) as access tokens, which are used to authenticate and authorize API requests for protected resources. 
* Token Expiration: Configurable token expiration to ensure better security and manage session lengths. 
* Password Reset: Provides a password reset mechanism that allows users to reset their passwords in case of a forgotten password. 
* Role-Based Access Control: Supports role-based access control (RBAC) to manage user permissions and restrict access to specific API endpoints based on user roles.

# Getting Started
* Clone the repository and navigate to the project directory. 
* Install the required dependencies using npm install or yarn install. 
* Configure the database settings in the config file, such as database connection details and JWT secret key. 
* Run the API server. 

# API Endpoints 
1. POST /auth-api/register: Register a new user by providing username, email, and password. 
2. POST /auth-api/login: Authenticate a user by providing username/email and password. Returns an access token upon successful login.
3. GET  /auth-api/logout: Returns a successfully logged out message.
4. POST /auth-api/refresh-token: Refresh an expired access token using a valid refresh token. 
5. POST /auth-api/reset-password: Initiate the password reset process for a user by sending a reset link to their email. 
6. POST /auth-api/change-password: Change the user's password after providing the old password and the new password.

# Authentication Flow
* The user registers a new account by providing necessary details.  
* Users log in by providing their email and password. 
* Upon successful login, the API returns an access token, which the user includes in their subsequent requests for authentication. 
* Access tokens have a limited expiration time, after which the user can refresh their token to obtain a new one without needing to log in again.
* Refresh tokens have a longer but limited expiration time, after which they can not be used to obtain a new token and as such, the user will have to log in again.
* If a user forgets their password, they can initiate the password reset process, which sends a reset link to their registered email address. 

# Technologies Used
* JAVA: Backend runtime environment for building the API server. 
* Spring/Spring boot: Fast and minimalistic web application framework. 
* PostgreSql: SQL database for storing user information and tokens. 
* JWT: JSON Web Tokens for securing API endpoints and managing user authentication.

For any questions or inquiries, please contact the project maintainer at ae.onaodowan@gmail.com
