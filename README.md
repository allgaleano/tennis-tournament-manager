# Project Initialization

This repository contains two main folders: 
- `back`: A Spring Boot application
- `front`: A Next.js application

## Prerequisites

### Dependecies for development collaboration
- **Java 21**
- **IntelliJ IDEA**
- **Maven**
- **Node.js 22.8.0**
- **npm**
  
Make sure these dependencies are installed globally on your system.

---

## Backend (`back`) - Spring Boot Application

### Prerequisites

- **Docker**

### How to run the app

```
cd back

docker compose up db --build -d

mvn clean install

docker compose down

docker compose up --build
```


## Frontend (`front`) - Next.js Application

### Prerequisites

- **Node.js 22.8.0**: [Download Node.js](https://nodejs.org/en/download/)
- **TailwindCSS**: Preconfigured within the Next.js project.
- **TypeScript**: Preconfigured within the Next.js project.

### Setup

1. Navigate to the `front` folder:
    ```bash
    cd front
    ```

2. Install the required dependencies:
    ```bash
    npm install
    ```

3. Run the development server:
    ```bash
    npm run dev
    ```

The frontend should now be running on `http://localhost:3000`.

---

## Folder Structure

- `back/`: Contains the Spring Boot backend code.
- `front/`: Contains the Next.js frontend code.

---

## Additional Commands

### Backend

- To run tests:
    ```bash
    mvn test
    ```

### Frontend

- To build the frontend for production:
    ```bash
    npm run build
    ```

- To run TypeScript checks:
    ```bash
    npm run type-check
    ```

---

## Troubleshooting

- Ensure Java, Maven, Node.js, and npm versions match the required versions specified in the prerequisites.
- Use the `--force` flag when installing npm packages if you encounter dependency issues.