# User Management System

## How to Run This Project

### Step 1: Clone the Repository

```sh
git clone <repository_url>
cd User_Management
```

### Step 2: Open with IDE

Use your preferred IDE (e.g., IntelliJ IDEA, VS Code) to open the project.

### Step 3: Create MySQL Database

Run the following command in your MySQL database:

```sql
CREATE DATABASE user_management;
```

### Step 4: Configure Database Connection

Go to `application.properties` and update the database connection details with your local MySQL credentials.

### Step 5: Run the Project

Start the project, and Spring Boot JPA will generate the required tables automatically.

### Step 6: Populate the Database

Go to the resource files and locate the SQL file containing insert statements form `Asset` directory to populate the database.

### Step 7: Access API Collection

You can view the API collection in Postman using this link:
[Postman Collection](https://documenter.getpostman.com/view/29496949/2sAYX6ph57)
You may use postman collection file form `Asset`directory.

### Step 8: Configure JWT Token in Postman

1. Open Postman and go to `USER MANAGEMENT > Variables > GameGrid-access-key`.
   ![Postman access token variable location](https://raw.githubusercontent.com/connectkhalid/User-Management/refs/heads/main/Asset/Screenshot%20of%20JWT%20%20token%20variable.png)
3. Add the JWT token here.
4. Save the variable using `Ctrl+S` if auto-save is disabled.

---

## Demo Users

### SUPER ADMIN

```json
{
  "mailAddress": "rahman.ashikur@bjitgroup.com",
  "password": "Aa@123456"
}
```

### ADMIN_A

```json
{
  "mailAddress": "khalidh7@gmail.com",
  "password": "Aa@123456"
}
```

### ADMIN_B

```json
{
  "mailAddress": "rafi2@gmail.com",
  "password": "Sr@123456"
}
```

### USER

```json
{
  "mailAddress": "sazid14@bjitgroup.com",
  "password": "Sr@123456"
}
```

---

## Permissions

| Role        | Permissions                                                                          |
| ----------- | ------------------------------------------------------------------------------------ |
| SUPER ADMIN | CREATE, UPDATE, DELETE, VIEW DETAIL, VIEW LIST, SEARCH for USER, ADMIN_A, ADMIN_B    |
| ADMIN_A     | UPDATE own profile, CREATE USER, MANAGE USERS created by them                        |
| ADMIN_B     | UPDATE own profile, CREATE USER, MANAGE USERS created by them (no DELETE permission) |

---

## Technologies & Versions

- **Java**: 17
- **Spring Boot**: 3.3.3

### Dependencies

- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- JJWT (JSON Web Token)
- Spring Boot Starter Web
- Spring Boot Starter Validation
- MySQL Connector/J (runtime scope)
- Lombok
- JSON
- Google API Client
- Apache Commons Lang
- Apache Commons Text
- Jakarta XML Binding (JAXB)
- Jakarta Validation API
- Commons Validator
- Java JWT (com.auth0)
